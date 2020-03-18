package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BeehiveBlockEntity extends BlockEntity implements Tickable {
	private final List<BeehiveBlockEntity.Bee> bees = Lists.<BeehiveBlockEntity.Bee>newArrayList();
	@Nullable
	private BlockPos flowerPos = null;

	public BeehiveBlockEntity() {
		super(BlockEntityType.BEEHIVE);
	}

	@Override
	public void markDirty() {
		if (this.isNearFire()) {
			this.angerBees(null, this.world.getBlockState(this.getPos()), BeehiveBlockEntity.BeeState.EMERGENCY);
		}

		super.markDirty();
	}

	public boolean isNearFire() {
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

	public void angerBees(@Nullable PlayerEntity player, BlockState state, BeehiveBlockEntity.BeeState beeState) {
		List<Entity> list = this.tryReleaseBee(state, beeState);
		if (player != null) {
			for (Entity entity : list) {
				if (entity instanceof BeeEntity) {
					BeeEntity beeEntity = (BeeEntity)entity;
					if (player.getPos().squaredDistanceTo(entity.getPos()) <= 16.0) {
						if (!this.isSmoked()) {
							beeEntity.setBeeAttacker(player);
						} else {
							beeEntity.setCannotEnterHiveTicks(400);
						}
					}
				}
			}
		}
	}

	private List<Entity> tryReleaseBee(BlockState state, BeehiveBlockEntity.BeeState beeState) {
		List<Entity> list = Lists.<Entity>newArrayList();
		this.bees.removeIf(bee -> this.releaseBee(state, bee.entityData, list, beeState));
		return list;
	}

	public void tryEnterHive(Entity entity, boolean hasNectar) {
		this.tryEnterHive(entity, hasNectar, 0);
	}

	public int getBeeCount() {
		return this.bees.size();
	}

	public static int getHoneyLevel(BlockState state) {
		return (Integer)state.get(BeehiveBlock.HONEY_LEVEL);
	}

	public boolean isSmoked() {
		return CampfireBlock.isLitCampfireInRange(this.world, this.getPos(), 5);
	}

	protected void sendDebugData() {
		DebugInfoSender.sendBeehiveDebugData(this);
	}

	public void tryEnterHive(Entity entity, boolean hasNectar, int ticksInHive) {
		if (this.bees.size() < 3) {
			entity.stopRiding();
			entity.removeAllPassengers();
			CompoundTag compoundTag = new CompoundTag();
			entity.saveToTag(compoundTag);
			this.bees.add(new BeehiveBlockEntity.Bee(compoundTag, ticksInHive, hasNectar ? 2400 : 600));
			if (this.world != null) {
				if (entity instanceof BeeEntity) {
					BeeEntity beeEntity = (BeeEntity)entity;
					if (beeEntity.hasFlower() && (!this.hasFlowerPos() || this.world.random.nextBoolean())) {
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

	private boolean releaseBee(BlockState state, CompoundTag compoundTag, @Nullable List<Entity> list, BeehiveBlockEntity.BeeState beeState) {
		BlockPos blockPos = this.getPos();
		if ((this.world.isNight() || this.world.isRaining()) && beeState != BeehiveBlockEntity.BeeState.EMERGENCY) {
			return false;
		} else {
			compoundTag.remove("Passengers");
			compoundTag.remove("Leash");
			compoundTag.remove("UUID");
			Direction direction = state.get(BeehiveBlock.FACING);
			BlockPos blockPos2 = blockPos.offset(direction);
			boolean bl = !this.world.getBlockState(blockPos2).getCollisionShape(this.world, blockPos2).isEmpty();
			if (bl && beeState != BeehiveBlockEntity.BeeState.EMERGENCY) {
				return false;
			} else {
				Entity entity = EntityType.loadEntityWithPassengers(compoundTag, this.world, entityx -> entityx);
				if (entity != null) {
					float f = entity.getWidth();
					double d = bl ? 0.0 : 0.55 + (double)(f / 2.0F);
					double e = (double)blockPos.getX() + 0.5 + d * (double)direction.getOffsetX();
					double g = (double)blockPos.getY() + 0.5 - (double)(entity.getHeight() / 2.0F);
					double h = (double)blockPos.getZ() + 0.5 + d * (double)direction.getOffsetZ();
					entity.refreshPositionAndAngles(e, g, h, entity.yaw, entity.pitch);
					if (!entity.getType().isIn(EntityTypeTags.BEEHIVE_INHABITORS)) {
						return false;
					} else {
						if (entity instanceof BeeEntity) {
							BeeEntity beeEntity = (BeeEntity)entity;
							if (this.hasFlowerPos() && !beeEntity.hasFlower() && this.world.random.nextFloat() < 0.9F) {
								beeEntity.setFlowerPos(this.flowerPos);
							}

							if (beeState == BeehiveBlockEntity.BeeState.HONEY_DELIVERED) {
								beeEntity.onHoneyDelivered();
								if (state.getBlock().isIn(BlockTags.BEEHIVES)) {
									int i = getHoneyLevel(state);
									if (i < 5) {
										int j = this.world.random.nextInt(100) == 0 ? 2 : 1;
										if (i + j > 5) {
											j--;
										}

										this.world.setBlockState(this.getPos(), state.with(BeehiveBlock.HONEY_LEVEL, Integer.valueOf(i + j)));
									}
								}
							}

							beeEntity.resetPollinationTicks();
							if (list != null) {
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
		return this.flowerPos != null;
	}

	private void tickBees() {
		Iterator<BeehiveBlockEntity.Bee> iterator = this.bees.iterator();
		BlockState blockState = this.getCachedState();

		while (iterator.hasNext()) {
			BeehiveBlockEntity.Bee bee = (BeehiveBlockEntity.Bee)iterator.next();
			if (bee.ticksInHive > bee.minOccupationTIcks) {
				CompoundTag compoundTag = bee.entityData;
				BeehiveBlockEntity.BeeState beeState = compoundTag.getBoolean("HasNectar")
					? BeehiveBlockEntity.BeeState.HONEY_DELIVERED
					: BeehiveBlockEntity.BeeState.BEE_RELEASED;
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

			this.sendDebugData();
		}
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		this.bees.clear();
		ListTag listTag = tag.getList("Bees", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompound(i);
			BeehiveBlockEntity.Bee bee = new BeehiveBlockEntity.Bee(
				compoundTag.getCompound("EntityData"), compoundTag.getInt("TicksInHive"), compoundTag.getInt("MinOccupationTicks")
			);
			this.bees.add(bee);
		}

		this.flowerPos = null;
		if (tag.contains("FlowerPos")) {
			this.flowerPos = NbtHelper.toBlockPos(tag.getCompound("FlowerPos"));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.put("Bees", this.getBees());
		if (this.hasFlowerPos()) {
			tag.put("FlowerPos", NbtHelper.fromBlockPos(this.flowerPos));
		}

		return tag;
	}

	public ListTag getBees() {
		ListTag listTag = new ListTag();

		for (BeehiveBlockEntity.Bee bee : this.bees) {
			bee.entityData.remove("UUID");
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
			entityData.remove("UUID");
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
