package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.MobSpawnerLogic;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.HitResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class SpawnEggItem extends Item {
	private static final Map<EntityType<?>, SpawnEggItem> field_8914 = Maps.<EntityType<?>, SpawnEggItem>newIdentityHashMap();
	private final int field_8916;
	private final int field_8915;
	private final EntityType<?> field_8917;

	public SpawnEggItem(EntityType<?> entityType, int i, int j, Item.Settings settings) {
		super(settings);
		this.field_8917 = entityType;
		this.field_8916 = i;
		this.field_8915 = j;
		field_8914.put(entityType, this);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			ItemStack itemStack = itemUsageContext.getItemStack();
			BlockPos blockPos = itemUsageContext.getPos();
			Direction direction = itemUsageContext.getFacing();
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (block == Blocks.field_10260) {
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof MobSpawnerBlockEntity) {
					MobSpawnerLogic mobSpawnerLogic = ((MobSpawnerBlockEntity)blockEntity).getLogic();
					EntityType<?> entityType = this.method_8015(itemStack.getTag());
					if (entityType != null) {
						mobSpawnerLogic.method_8274(entityType);
						blockEntity.markDirty();
						world.updateListeners(blockPos, blockState, blockState, 3);
					}

					itemStack.subtractAmount(1);
					return ActionResult.SUCCESS;
				}
			}

			BlockPos blockPos2;
			if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
				blockPos2 = blockPos;
			} else {
				blockPos2 = blockPos.offset(direction);
			}

			EntityType<?> entityType2 = this.method_8015(itemStack.getTag());
			if (entityType2 == null
				|| entityType2.spawnFromItemStack(
						world, itemStack, itemUsageContext.getPlayer(), blockPos2, SpawnType.field_16465, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP
					)
					!= null) {
				itemStack.subtractAmount(1);
			}

			return ActionResult.SUCCESS;
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (world.isClient) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			HitResult hitResult = getHitResult(world, playerEntity, RayTraceContext.FluidHandling.field_1345);
			if (hitResult.getType() != HitResult.Type.BLOCK) {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			} else {
				BlockHitResult blockHitResult = (BlockHitResult)hitResult;
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
					return new TypedActionResult<>(ActionResult.PASS, itemStack);
				} else if (world.canPlayerModifyAt(playerEntity, blockPos) && playerEntity.canPlaceBlock(blockPos, blockHitResult.getSide(), itemStack)) {
					EntityType<?> entityType = this.method_8015(itemStack.getTag());
					if (entityType != null && entityType.spawnFromItemStack(world, itemStack, playerEntity, blockPos, SpawnType.field_16465, false, false) != null) {
						if (!playerEntity.abilities.creativeMode) {
							itemStack.subtractAmount(1);
						}

						playerEntity.incrementStat(Stats.field_15372.method_14956(this));
						return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
					} else {
						return new TypedActionResult<>(ActionResult.PASS, itemStack);
					}
				} else {
					return new TypedActionResult<>(ActionResult.FAILURE, itemStack);
				}
			}
		}
	}

	public boolean method_8018(@Nullable CompoundTag compoundTag, EntityType<?> entityType) {
		return Objects.equals(this.method_8015(compoundTag), entityType);
	}

	@Environment(EnvType.CLIENT)
	public int method_8016(int i) {
		return i == 0 ? this.field_8916 : this.field_8915;
	}

	@Environment(EnvType.CLIENT)
	public static SpawnEggItem method_8019(@Nullable EntityType<?> entityType) {
		return (SpawnEggItem)field_8914.get(entityType);
	}

	public static Iterable<SpawnEggItem> method_8017() {
		return Iterables.unmodifiableIterable(field_8914.values());
	}

	@Nullable
	public EntityType<?> method_8015(@Nullable CompoundTag compoundTag) {
		if (compoundTag != null && compoundTag.containsKey("EntityTag", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("EntityTag");
			if (compoundTag2.containsKey("id", 8)) {
				return EntityType.get(compoundTag2.getString("id"));
			}
		}

		return this.field_8917;
	}
}
