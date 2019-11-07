package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BeeHiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BeeHiveBlockEntity extends BlockEntity implements Tickable {
	private final List<BeeHiveBlockEntity.Bee> bees = Lists.<BeeHiveBlockEntity.Bee>newArrayList();
	private BlockPos flowerPos = BlockPos.ORIGIN;

	public BeeHiveBlockEntity() {
		super(BlockEntityType.BEEHIVE);
	}

	@Override
	public void markDirty() {
		if (this.method_23280()) {
			this.angerBees(null, this.world.getBlockState(this.getPos()), BeeHiveBlockEntity.BeeState.EMERGENCY);
		}

		super.markDirty();
	}

	public boolean method_23280() {
		if (this.world == null) {
			return false;
		} else {
			for (BlockPos blockPos : BlockPos.iterate(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1))) {
				if (this.world.getBlockState(blockPos).getBlock() instanceof FireBlock) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean hasNoBees() {
		return this.bees.isEmpty();
	}

	public boolean isFullOfBees() {
		return this.bees.size() == 3;
	}

	public void angerBees(@Nullable PlayerEntity playerEntity, BlockState blockState, BeeHiveBlockEntity.BeeState beeState) {
		List<Entity> list = this.tryReleaseBee(blockState, beeState);
		if (playerEntity != null) {
			for (Entity entity : list) {
				if (entity instanceof BeeEntity) {
					BeeEntity beeEntity = (BeeEntity)entity;
					if (playerEntity.getPos().squaredDistanceTo(entity.getPos()) <= 16.0) {
						if (!BeeHiveBlock.method_23755(this.world, this.getPos())) {
							beeEntity.setBeeAttacker(playerEntity);
						} else {
							beeEntity.setCannotEnterHiveTicks(400);
						}
					}
				}
			}
		}
	}

	private List<Entity> tryReleaseBee(BlockState blockState, BeeHiveBlockEntity.BeeState beeState) {
		List<Entity> list = Lists.<Entity>newArrayList();
		this.bees.removeIf(bee -> this.releaseBee(blockState, bee.entityData, list, beeState));
		return list;
	}

	public void tryEnterHive(Entity entity, boolean hasNectar) {
		this.tryEnterHive(entity, hasNectar, 0);
	}

	protected void method_23757() {
		DebugRendererInfoManager.method_23856(this);
	}

	public void tryEnterHive(Entity entity, boolean hasNectar, int ticksInHive) {
		if (this.bees.size() < 3) {
			entity.removeAllPassengers();
			CompoundTag compoundTag = new CompoundTag();
			entity.saveToTag(compoundTag);
			this.bees.add(new BeeHiveBlockEntity.Bee(compoundTag, ticksInHive, hasNectar ? 2400 : 600));
			if (this.world != null) {
				if (entity instanceof BeeEntity) {
					BeeEntity beeEntity = (BeeEntity)entity;
					if (!this.hasFlowerPos() || beeEntity.hasFlower() && this.world.random.nextBoolean()) {
						this.flowerPos = beeEntity.getFlowerPos();
					}
				}

				BlockPos blockPos = this.getPos();
				this.world
					.playSound(
						null, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F
					);
			}

			entity.remove();
		}
	}

	private boolean releaseBee(BlockState blockState, CompoundTag compoundTag, @Nullable List<Entity> list, BeeHiveBlockEntity.BeeState beeState) {
		BlockPos blockPos = this.getPos();
		if ((!this.world.isDaylight() || this.world.isRaining()) && beeState != BeeHiveBlockEntity.BeeState.EMERGENCY) {
			return false;
		} else {
			compoundTag.remove("Passengers");
			compoundTag.remove("Leash");
			compoundTag.removeUuid("UUID");
			Optional<BlockPos> optional = Optional.empty();
			Direction direction = blockState.get(BeeHiveBlock.FACING);
			BlockPos blockPos2 = blockPos.method_10079(direction, 2);
			if (this.world.getBlockState(blockPos2).getCollisionShape(this.world, blockPos2).isEmpty()) {
				optional = Optional.of(blockPos2);
			}

			if (!optional.isPresent()) {
				for (Direction direction2 : Direction.Type.HORIZONTAL) {
					BlockPos blockPos3 = blockPos.add(direction2.getOffsetX() * 2, direction2.getOffsetY(), direction2.getOffsetZ() * 2);
					if (this.world.getBlockState(blockPos3).getCollisionShape(this.world, blockPos3).isEmpty()) {
						optional = Optional.of(blockPos3);
						break;
					}
				}
			}

			if (!optional.isPresent()) {
				for (Direction direction2x : Direction.Type.VERTICAL) {
					BlockPos blockPos3 = blockPos.add(direction2x.getOffsetX() * 2, direction2x.getOffsetY(), direction2x.getOffsetZ() * 2);
					if (this.world.getBlockState(blockPos3).getCollisionShape(this.world, blockPos3).isEmpty()) {
						optional = Optional.of(blockPos3);
					}
				}
			}

			if (!optional.isPresent()) {
				return false;
			} else {
				BlockPos blockPos4 = (BlockPos)optional.get();
				Entity entity = EntityType.loadEntityWithPassengers(compoundTag, this.world, entityx -> {
					entityx.setPositionAndAngles((double)blockPos4.getX(), (double)blockPos4.getY(), (double)blockPos4.getZ(), entityx.yaw, entityx.pitch);
					return entityx;
				});
				if (entity != null) {
					if (!entity.getType().isTaggedWith(EntityTypeTags.BEEHIVE_INHABITORS)) {
						return false;
					} else {
						if (entity instanceof BeeEntity) {
							BeeEntity beeEntity = (BeeEntity)entity;
							if (this.hasFlowerPos() && !beeEntity.hasFlower() && this.world.random.nextFloat() < 0.9F) {
								beeEntity.setFlowerPos(this.flowerPos);
							}

							if (beeState == BeeHiveBlockEntity.BeeState.HONEY_DELIVERED) {
								beeEntity.onHoneyDelivered();
								if (blockState.getBlock().matches(BlockTags.BEEHIVES)) {
									int i = (Integer)blockState.get(BeeHiveBlock.HONEY_LEVEL);
									if (i < 5) {
										int j = this.world.random.nextInt(100) == 0 ? 2 : 1;
										if (i + j > 5) {
											j--;
										}

										this.world.setBlockState(this.getPos(), blockState.with(BeeHiveBlock.HONEY_LEVEL, Integer.valueOf(i + j)));
									}
								}
							}

							if (list != null) {
								beeEntity.resetPollinationTicks();
								list.add(beeEntity);
							}
						}

						BlockPos blockPos3 = this.getPos();
						this.world
							.playSound(
								null, (double)blockPos3.getX(), (double)blockPos3.getY(), (double)blockPos3.getZ(), SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F
							);
						return this.world.spawnEntity(entity);
					}
				} else {
					return false;
				}
			}
		}
	}

	private boolean hasFlowerPos() {
		return this.flowerPos != BlockPos.ORIGIN;
	}

	private void tickBees() {
		Iterator<BeeHiveBlockEntity.Bee> iterator = this.bees.iterator();
		BlockState blockState = this.getCachedState();

		while (iterator.hasNext()) {
			BeeHiveBlockEntity.Bee bee = (BeeHiveBlockEntity.Bee)iterator.next();
			if (bee.ticksInHive > bee.minOccupationTIcks) {
				CompoundTag compoundTag = bee.entityData;
				BeeHiveBlockEntity.BeeState beeState = compoundTag.getBoolean("HasNectar")
					? BeeHiveBlockEntity.BeeState.HONEY_DELIVERED
					: BeeHiveBlockEntity.BeeState.BEE_RELEASED;
				if (this.releaseBee(blockState, compoundTag, null, beeState)) {
					iterator.remove();
				}
			} else {
				bee.ticksInHive++;
			}
		}
	}

	@Override
	public void tick() {
		if (!this.world.isClient) {
			this.tickBees();
			BlockPos blockPos = this.getPos();
			if (this.bees.size() > 0 && this.world.getRandom().nextDouble() < 0.005) {
				double d = (double)blockPos.getX() + 0.5;
				double e = (double)blockPos.getY();
				double f = (double)blockPos.getZ() + 0.5;
				this.world.playSound(null, d, e, f, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}

			this.method_23757();
		}
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.bees.clear();
		ListTag listTag = compoundTag.getList("Bees", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompound(i);
			BeeHiveBlockEntity.Bee bee = new BeeHiveBlockEntity.Bee(
				compoundTag2.getCompound("EntityData"), compoundTag2.getInt("TicksInHive"), compoundTag2.getInt("MinOccupationTicks")
			);
			this.bees.add(bee);
		}

		this.flowerPos = NbtHelper.toBlockPos(compoundTag.getCompound("FlowerPos"));
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.put("Bees", this.getBees());
		compoundTag.put("FlowerPos", NbtHelper.fromBlockPos(this.flowerPos));
		return compoundTag;
	}

	public ListTag getBees() {
		ListTag listTag = new ListTag();

		for (BeeHiveBlockEntity.Bee bee : this.bees) {
			bee.entityData.removeUuid("UUID");
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.put("EntityData", bee.entityData);
			compoundTag.putInt("TicksInHive", bee.ticksInHive);
			compoundTag.putInt("MinOccupationTicks", bee.minOccupationTIcks);
			listTag.add(compoundTag);
		}

		return listTag;
	}

	static class Bee {
		private final CompoundTag entityData;
		private int ticksInHive;
		private final int minOccupationTIcks;

		private Bee(CompoundTag entityData, int ticksInHive, int minOccupationTicks) {
			entityData.removeUuid("UUID");
			this.entityData = entityData;
			this.ticksInHive = ticksInHive;
			this.minOccupationTIcks = minOccupationTicks;
		}
	}

	public static enum BeeState {
		HONEY_DELIVERED,
		BEE_RELEASED,
		EMERGENCY;
	}
}
