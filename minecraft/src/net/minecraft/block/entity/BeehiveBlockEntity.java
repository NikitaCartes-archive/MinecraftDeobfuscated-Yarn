package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.Arrays;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BeehiveBlockEntity extends BlockEntity {
	public static final String FLOWER_POS_KEY = "FlowerPos";
	public static final String MIN_OCCUPATION_TICKS_KEY = "MinOccupationTicks";
	public static final String ENTITY_DATA_KEY = "EntityData";
	public static final String TICKS_IN_HIVE_KEY = "TicksInHive";
	public static final String HAS_NECTAR_KEY = "HasNectar";
	public static final String BEES_KEY = "Bees";
	private static final List<String> IRRELEVANT_BEE_NBT_KEYS = Arrays.asList(
		"Air",
		"ArmorDropChances",
		"ArmorItems",
		"Brain",
		"CanPickUpLoot",
		"DeathTime",
		"FallDistance",
		"FallFlying",
		"Fire",
		"HandDropChances",
		"HandItems",
		"HurtByTimestamp",
		"HurtTime",
		"LeftHanded",
		"Motion",
		"NoGravity",
		"OnGround",
		"PortalCooldown",
		"Pos",
		"Rotation",
		"CannotEnterHiveTicks",
		"TicksSincePollination",
		"CropsGrownSincePollination",
		"HivePos",
		"Passengers",
		"Leash",
		"UUID"
	);
	public static final int MAX_BEE_COUNT = 3;
	private static final int ANGERED_CANNOT_ENTER_HIVE_TICKS = 400;
	private static final int MIN_OCCUPATION_TICKS_WITH_NECTAR = 2400;
	public static final int MIN_OCCUPATION_TICKS_WITHOUT_NECTAR = 600;
	private final List<BeehiveBlockEntity.Bee> bees = Lists.<BeehiveBlockEntity.Bee>newArrayList();
	@Nullable
	private BlockPos flowerPos;

	public BeehiveBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.BEEHIVE, pos, state);
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
							beeEntity.setTarget(player);
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
		this.bees.removeIf(bee -> releaseBee(this.world, this.pos, state, bee, list, beeState, this.flowerPos));
		if (!list.isEmpty()) {
			super.markDirty();
		}

		return list;
	}

	public void tryEnterHive(Entity entity, boolean hasNectar) {
		this.tryEnterHive(entity, hasNectar, 0);
	}

	@Debug
	public int getBeeCount() {
		return this.bees.size();
	}

	public static int getHoneyLevel(BlockState state) {
		return (Integer)state.get(BeehiveBlock.HONEY_LEVEL);
	}

	@Debug
	public boolean isSmoked() {
		return CampfireBlock.isLitCampfireInRange(this.world, this.getPos());
	}

	public void tryEnterHive(Entity entity, boolean hasNectar, int ticksInHive) {
		if (this.bees.size() < 3) {
			entity.stopRiding();
			entity.removeAllPassengers();
			NbtCompound nbtCompound = new NbtCompound();
			entity.saveNbt(nbtCompound);
			this.addBee(nbtCompound, ticksInHive, hasNectar);
			if (this.world != null) {
				if (entity instanceof BeeEntity beeEntity && beeEntity.hasFlower() && (!this.hasFlowerPos() || this.world.random.nextBoolean())) {
					this.flowerPos = beeEntity.getFlowerPos();
				}

				BlockPos blockPos = this.getPos();
				this.world
					.playSound(
						null, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F
					);
				this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(entity, this.getCachedState()));
			}

			entity.discard();
			super.markDirty();
		}
	}

	public void addBee(NbtCompound nbtCompound, int ticksInHive, boolean hasNectar) {
		this.bees.add(new BeehiveBlockEntity.Bee(nbtCompound, ticksInHive, hasNectar ? 2400 : 600));
	}

	private static boolean releaseBee(
		World world,
		BlockPos pos,
		BlockState state,
		BeehiveBlockEntity.Bee bee,
		@Nullable List<Entity> entities,
		BeehiveBlockEntity.BeeState beeState,
		@Nullable BlockPos flowerPos
	) {
		if ((world.isNight() || world.isRaining()) && beeState != BeehiveBlockEntity.BeeState.EMERGENCY) {
			return false;
		} else {
			NbtCompound nbtCompound = bee.entityData.copy();
			removeIrrelevantNbtKeys(nbtCompound);
			nbtCompound.put("HivePos", NbtHelper.fromBlockPos(pos));
			nbtCompound.putBoolean("NoGravity", true);
			Direction direction = state.get(BeehiveBlock.FACING);
			BlockPos blockPos = pos.offset(direction);
			boolean bl = !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty();
			if (bl && beeState != BeehiveBlockEntity.BeeState.EMERGENCY) {
				return false;
			} else {
				Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, world, entityx -> entityx);
				if (entity != null) {
					if (!entity.getType().isIn(EntityTypeTags.BEEHIVE_INHABITORS)) {
						return false;
					} else {
						if (entity instanceof BeeEntity beeEntity) {
							if (flowerPos != null && !beeEntity.hasFlower() && world.random.nextFloat() < 0.9F) {
								beeEntity.setFlowerPos(flowerPos);
							}

							if (beeState == BeehiveBlockEntity.BeeState.HONEY_DELIVERED) {
								beeEntity.onHoneyDelivered();
								if (state.isIn(BlockTags.BEEHIVES, statex -> statex.contains(BeehiveBlock.HONEY_LEVEL))) {
									int i = getHoneyLevel(state);
									if (i < 5) {
										int j = world.random.nextInt(100) == 0 ? 2 : 1;
										if (i + j > 5) {
											j--;
										}

										world.setBlockState(pos, state.with(BeehiveBlock.HONEY_LEVEL, Integer.valueOf(i + j)));
									}
								}
							}

							ageBee(bee.ticksInHive, beeEntity);
							if (entities != null) {
								entities.add(beeEntity);
							}

							float f = entity.getWidth();
							double d = bl ? 0.0 : 0.55 + (double)(f / 2.0F);
							double e = (double)pos.getX() + 0.5 + d * (double)direction.getOffsetX();
							double g = (double)pos.getY() + 0.5 - (double)(entity.getHeight() / 2.0F);
							double h = (double)pos.getZ() + 0.5 + d * (double)direction.getOffsetZ();
							entity.refreshPositionAndAngles(e, g, h, entity.getYaw(), entity.getPitch());
						}

						world.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
						world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(entity, world.getBlockState(pos)));
						return world.spawnEntity(entity);
					}
				} else {
					return false;
				}
			}
		}
	}

	static void removeIrrelevantNbtKeys(NbtCompound compound) {
		for (String string : IRRELEVANT_BEE_NBT_KEYS) {
			compound.remove(string);
		}
	}

	private static void ageBee(int ticks, BeeEntity bee) {
		int i = bee.getBreedingAge();
		if (i < 0) {
			bee.setBreedingAge(Math.min(0, i + ticks));
		} else if (i > 0) {
			bee.setBreedingAge(Math.max(0, i - ticks));
		}

		bee.setLoveTicks(Math.max(0, bee.getLoveTicks() - ticks));
	}

	private boolean hasFlowerPos() {
		return this.flowerPos != null;
	}

	private static void tickBees(World world, BlockPos pos, BlockState state, List<BeehiveBlockEntity.Bee> bees, @Nullable BlockPos flowerPos) {
		boolean bl = false;
		Iterator<BeehiveBlockEntity.Bee> iterator = bees.iterator();

		while (iterator.hasNext()) {
			BeehiveBlockEntity.Bee bee = (BeehiveBlockEntity.Bee)iterator.next();
			if (bee.ticksInHive > bee.minOccupationTicks) {
				BeehiveBlockEntity.BeeState beeState = bee.entityData.getBoolean("HasNectar")
					? BeehiveBlockEntity.BeeState.HONEY_DELIVERED
					: BeehiveBlockEntity.BeeState.BEE_RELEASED;
				if (releaseBee(world, pos, state, bee, null, beeState, flowerPos)) {
					bl = true;
					iterator.remove();
				}
			}

			bee.ticksInHive++;
		}

		if (bl) {
			markDirty(world, pos, state);
		}
	}

	public static void serverTick(World world, BlockPos pos, BlockState state, BeehiveBlockEntity blockEntity) {
		tickBees(world, pos, state, blockEntity.bees, blockEntity.flowerPos);
		if (!blockEntity.bees.isEmpty() && world.getRandom().nextDouble() < 0.005) {
			double d = (double)pos.getX() + 0.5;
			double e = (double)pos.getY();
			double f = (double)pos.getZ() + 0.5;
			world.playSound(null, d, e, f, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}

		DebugInfoSender.sendBeehiveDebugData(world, pos, state, blockEntity);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.bees.clear();
		NbtList nbtList = nbt.getList("Bees", NbtElement.COMPOUND_TYPE);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			BeehiveBlockEntity.Bee bee = new BeehiveBlockEntity.Bee(
				nbtCompound.getCompound("EntityData").copy(), nbtCompound.getInt("TicksInHive"), nbtCompound.getInt("MinOccupationTicks")
			);
			this.bees.add(bee);
		}

		this.flowerPos = null;
		if (nbt.contains("FlowerPos")) {
			this.flowerPos = NbtHelper.toBlockPos(nbt.getCompound("FlowerPos"));
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.put("Bees", this.getBees());
		if (this.hasFlowerPos()) {
			nbt.put("FlowerPos", NbtHelper.fromBlockPos(this.flowerPos));
		}
	}

	public NbtList getBees() {
		NbtList nbtList = new NbtList();

		for (BeehiveBlockEntity.Bee bee : this.bees) {
			NbtCompound nbtCompound = bee.entityData.copy();
			nbtCompound.remove("UUID");
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.put("EntityData", nbtCompound);
			nbtCompound2.putInt("TicksInHive", bee.ticksInHive);
			nbtCompound2.putInt("MinOccupationTicks", bee.minOccupationTicks);
			nbtList.add(nbtCompound2);
		}

		return nbtList;
	}

	static class Bee {
		final NbtCompound entityData;
		int ticksInHive;
		final int minOccupationTicks;

		Bee(NbtCompound entityData, int ticksInHive, int minOccupationTicks) {
			BeehiveBlockEntity.removeIrrelevantNbtKeys(entityData);
			this.entityData = entityData;
			this.ticksInHive = ticksInHive;
			this.minOccupationTicks = minOccupationTicks;
		}
	}

	public static enum BeeState {
		HONEY_DELIVERED,
		BEE_RELEASED,
		EMERGENCY;
	}
}
