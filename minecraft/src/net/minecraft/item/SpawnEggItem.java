package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.Spawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class SpawnEggItem extends Item {
	private static final Map<EntityType<? extends MobEntity>, SpawnEggItem> SPAWN_EGGS = Maps.<EntityType<? extends MobEntity>, SpawnEggItem>newIdentityHashMap();
	private final int primaryColor;
	private final int secondaryColor;
	private final EntityType<?> type;

	public SpawnEggItem(EntityType<? extends MobEntity> type, int primaryColor, int secondaryColor, Item.Settings settings) {
		super(settings);
		this.type = type;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		SPAWN_EGGS.put(type, this);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if (!(world instanceof ServerWorld)) {
			return ActionResult.SUCCESS;
		} else {
			ItemStack itemStack = context.getStack();
			BlockPos blockPos = context.getBlockPos();
			Direction direction = context.getSide();
			BlockState blockState = world.getBlockState(blockPos);
			if (world.getBlockEntity(blockPos) instanceof Spawner spawner) {
				EntityType<?> entityType = this.getEntityType(itemStack.getNbt());
				spawner.setEntityType(entityType, world.getRandom());
				world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
				world.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
				itemStack.decrement(1);
				return ActionResult.CONSUME;
			} else {
				BlockPos blockPos2;
				if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
					blockPos2 = blockPos;
				} else {
					blockPos2 = blockPos.offset(direction);
				}

				EntityType<?> entityType = this.getEntityType(itemStack.getNbt());
				if (entityType.spawnFromItemStack(
						(ServerWorld)world,
						itemStack,
						context.getPlayer(),
						blockPos2,
						SpawnReason.SPAWN_EGG,
						true,
						!Objects.equals(blockPos, blockPos2) && direction == Direction.UP
					)
					!= null) {
					itemStack.decrement(1);
					world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
				}

				return ActionResult.CONSUME;
			}
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
		if (blockHitResult.getType() != HitResult.Type.BLOCK) {
			return TypedActionResult.pass(itemStack);
		} else if (!(world instanceof ServerWorld)) {
			return TypedActionResult.success(itemStack);
		} else {
			BlockPos blockPos = blockHitResult.getBlockPos();
			if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
				return TypedActionResult.pass(itemStack);
			} else if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
				EntityType<?> entityType = this.getEntityType(itemStack.getNbt());
				Entity entity = entityType.spawnFromItemStack((ServerWorld)world, itemStack, user, blockPos, SpawnReason.SPAWN_EGG, false, false);
				if (entity == null) {
					return TypedActionResult.pass(itemStack);
				} else {
					if (!user.getAbilities().creativeMode) {
						itemStack.decrement(1);
					}

					user.incrementStat(Stats.USED.getOrCreateStat(this));
					world.emitGameEvent(user, GameEvent.ENTITY_PLACE, entity.getPos());
					return TypedActionResult.consume(itemStack);
				}
			} else {
				return TypedActionResult.fail(itemStack);
			}
		}
	}

	public boolean isOfSameEntityType(@Nullable NbtCompound nbt, EntityType<?> type) {
		return Objects.equals(this.getEntityType(nbt), type);
	}

	/**
	 * {@return the color of the specified tint index}
	 * 
	 * @implSpec If the tint index is 0, returns {@link #primaryColor}. Otherwise, returns {@link #secondaryColor}.
	 * 
	 * @param tintIndex the tint index
	 */
	public int getColor(int tintIndex) {
		return tintIndex == 0 ? this.primaryColor : this.secondaryColor;
	}

	@Nullable
	public static SpawnEggItem forEntity(@Nullable EntityType<?> type) {
		return (SpawnEggItem)SPAWN_EGGS.get(type);
	}

	public static Iterable<SpawnEggItem> getAll() {
		return Iterables.unmodifiableIterable(SPAWN_EGGS.values());
	}

	public EntityType<?> getEntityType(@Nullable NbtCompound nbt) {
		if (nbt != null && nbt.contains("EntityTag", NbtElement.COMPOUND_TYPE)) {
			NbtCompound nbtCompound = nbt.getCompound("EntityTag");
			if (nbtCompound.contains("id", NbtElement.STRING_TYPE)) {
				return (EntityType<?>)EntityType.get(nbtCompound.getString("id")).orElse(this.type);
			}
		}

		return this.type;
	}

	@Override
	public FeatureSet getRequiredFeatures() {
		return this.type.getRequiredFeatures();
	}

	public Optional<MobEntity> spawnBaby(
		PlayerEntity user, MobEntity entity, EntityType<? extends MobEntity> entityType, ServerWorld world, Vec3d pos, ItemStack stack
	) {
		if (!this.isOfSameEntityType(stack.getNbt(), entityType)) {
			return Optional.empty();
		} else {
			MobEntity mobEntity;
			if (entity instanceof PassiveEntity) {
				mobEntity = ((PassiveEntity)entity).createChild(world, (PassiveEntity)entity);
			} else {
				mobEntity = entityType.create(world);
			}

			if (mobEntity == null) {
				return Optional.empty();
			} else {
				mobEntity.setBaby(true);
				if (!mobEntity.isBaby()) {
					return Optional.empty();
				} else {
					mobEntity.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
					world.spawnEntityAndPassengers(mobEntity);
					if (stack.hasCustomName()) {
						mobEntity.setCustomName(stack.getName());
					}

					if (!user.getAbilities().creativeMode) {
						stack.decrement(1);
					}

					return Optional.of(mobEntity);
				}
			}
		}
	}
}
