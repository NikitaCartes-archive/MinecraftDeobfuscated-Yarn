package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class SpawnEggItem extends Item {
	private static final Map<EntityType<?>, SpawnEggItem> SPAWN_EGGS = Maps.<EntityType<?>, SpawnEggItem>newIdentityHashMap();
	private final int primaryColor;
	private final int secondaryColor;
	private final EntityType<?> type;

	public SpawnEggItem(EntityType<?> type, int primaryColor, int secondaryColor, Item.Settings settings) {
		super(settings);
		this.type = type;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		SPAWN_EGGS.put(type, this);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			ItemStack itemStack = context.getStack();
			BlockPos blockPos = context.getBlockPos();
			Direction direction = context.getSide();
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (block == Blocks.SPAWNER) {
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof MobSpawnerBlockEntity) {
					MobSpawnerLogic mobSpawnerLogic = ((MobSpawnerBlockEntity)blockEntity).getLogic();
					EntityType<?> entityType = this.getEntityType(itemStack.getTag());
					mobSpawnerLogic.setEntityId(entityType);
					blockEntity.markDirty();
					world.updateListeners(blockPos, blockState, blockState, 3);
					itemStack.decrement(1);
					return ActionResult.SUCCESS;
				}
			}

			BlockPos blockPos2;
			if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
				blockPos2 = blockPos;
			} else {
				blockPos2 = blockPos.offset(direction);
			}

			EntityType<?> entityType2 = this.getEntityType(itemStack.getTag());
			if (entityType2.spawnFromItemStack(
					world, itemStack, context.getPlayer(), blockPos2, SpawnType.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP
				)
				!= null) {
				itemStack.decrement(1);
			}

			return ActionResult.SUCCESS;
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		HitResult hitResult = rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY);
		if (hitResult.getType() != HitResult.Type.BLOCK) {
			return TypedActionResult.pass(itemStack);
		} else if (world.isClient) {
			return TypedActionResult.success(itemStack);
		} else {
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			BlockPos blockPos = blockHitResult.getBlockPos();
			if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
				return TypedActionResult.pass(itemStack);
			} else if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
				EntityType<?> entityType = this.getEntityType(itemStack.getTag());
				if (entityType.spawnFromItemStack(world, itemStack, user, blockPos, SpawnType.SPAWN_EGG, false, false) == null) {
					return TypedActionResult.pass(itemStack);
				} else {
					if (!user.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					user.incrementStat(Stats.USED.getOrCreateStat(this));
					return TypedActionResult.success(itemStack);
				}
			} else {
				return TypedActionResult.fail(itemStack);
			}
		}
	}

	public boolean isOfSameEntityType(@Nullable CompoundTag tag, EntityType<?> type) {
		return Objects.equals(this.getEntityType(tag), type);
	}

	@Environment(EnvType.CLIENT)
	public int getColor(int num) {
		return num == 0 ? this.primaryColor : this.secondaryColor;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static SpawnEggItem forEntity(@Nullable EntityType<?> type) {
		return (SpawnEggItem)SPAWN_EGGS.get(type);
	}

	public static Iterable<SpawnEggItem> getAll() {
		return Iterables.unmodifiableIterable(SPAWN_EGGS.values());
	}

	public EntityType<?> getEntityType(@Nullable CompoundTag tag) {
		if (tag != null && tag.contains("EntityTag", 10)) {
			CompoundTag compoundTag = tag.getCompound("EntityTag");
			if (compoundTag.contains("id", 8)) {
				return (EntityType<?>)EntityType.get(compoundTag.getString("id")).orElse(this.type);
			}
		}

		return this.type;
	}

	public Optional<MobEntity> method_24793(PlayerEntity playerEntity, EntityType<? extends MobEntity> entityType, World world, Vec3d vec3d, ItemStack itemStack) {
		if (!this.isOfSameEntityType(itemStack.getTag(), entityType)) {
			return Optional.empty();
		} else {
			MobEntity mobEntity = entityType.create(world);
			if (mobEntity == null) {
				return Optional.empty();
			} else {
				mobEntity.setBaby(true);
				mobEntity.refreshPositionAndAngles(vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0.0F, 0.0F);
				world.spawnEntity(mobEntity);
				if (itemStack.hasCustomName()) {
					mobEntity.setCustomName(itemStack.getName());
				}

				if (!playerEntity.abilities.creativeMode) {
					itemStack.decrement(1);
				}

				return Optional.of(mobEntity);
			}
		}
	}
}
