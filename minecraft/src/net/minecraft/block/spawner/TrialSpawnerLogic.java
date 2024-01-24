package net.minecraft.block.spawner;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public final class TrialSpawnerLogic {
	public static final int field_47358 = 40;
	private static final int MAX_ENTITY_DISTANCE = 47;
	private static final int MAX_ENTITY_DISTANCE_SQUARED = MathHelper.square(47);
	private static final float field_47361 = 0.02F;
	private final TrialSpawnerConfig config;
	private final TrialSpawnerData data;
	private final TrialSpawnerLogic.TrialSpawner trialSpawner;
	private EntityDetector entityDetector;
	private boolean forceActivate;

	public Codec<TrialSpawnerLogic> codec() {
		return RecordCodecBuilder.create(
			instance -> instance.group(TrialSpawnerConfig.codec.forGetter(TrialSpawnerLogic::getConfig), TrialSpawnerData.codec.forGetter(TrialSpawnerLogic::getData))
					.apply(
						instance, (trialSpawnerConfig, trialSpawnerData) -> new TrialSpawnerLogic(trialSpawnerConfig, trialSpawnerData, this.trialSpawner, this.entityDetector)
					)
		);
	}

	public TrialSpawnerLogic(TrialSpawnerLogic.TrialSpawner trialSpawner, EntityDetector entityDetector) {
		this(TrialSpawnerConfig.defaultInstance, new TrialSpawnerData(), trialSpawner, entityDetector);
	}

	public TrialSpawnerLogic(TrialSpawnerConfig config, TrialSpawnerData data, TrialSpawnerLogic.TrialSpawner trialSpawner, EntityDetector entityDetector) {
		this.config = config;
		this.data = data;
		this.data.populateSpawnDataPool(config);
		this.trialSpawner = trialSpawner;
		this.entityDetector = entityDetector;
	}

	public TrialSpawnerConfig getConfig() {
		return this.config;
	}

	public TrialSpawnerData getData() {
		return this.data;
	}

	public TrialSpawnerState getSpawnerState() {
		return this.trialSpawner.getSpawnerState();
	}

	public void setSpawnerState(World world, TrialSpawnerState spawnerState) {
		this.trialSpawner.setSpawnerState(world, spawnerState);
	}

	public void updateListeners() {
		this.trialSpawner.updateListeners();
	}

	public EntityDetector getEntityDetector() {
		return this.entityDetector;
	}

	public boolean canActivate(World world) {
		if (this.forceActivate) {
			return true;
		} else {
			return world.getDifficulty() == Difficulty.PEACEFUL ? false : world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING);
		}
	}

	public Optional<UUID> trySpawnMob(ServerWorld world, BlockPos pos) {
		Random random = world.getRandom();
		MobSpawnerEntry mobSpawnerEntry = this.data.getSpawnData(this, world.getRandom());
		NbtCompound nbtCompound = mobSpawnerEntry.entity();
		NbtList nbtList = nbtCompound.getList("Pos", NbtElement.DOUBLE_TYPE);
		Optional<EntityType<?>> optional = EntityType.fromNbt(nbtCompound);
		if (optional.isEmpty()) {
			return Optional.empty();
		} else {
			int i = nbtList.size();
			double d = i >= 1 ? nbtList.getDouble(0) : (double)pos.getX() + (random.nextDouble() - random.nextDouble()) * (double)this.config.spawnRange() + 0.5;
			double e = i >= 2 ? nbtList.getDouble(1) : (double)(pos.getY() + random.nextInt(3) - 1);
			double f = i >= 3 ? nbtList.getDouble(2) : (double)pos.getZ() + (random.nextDouble() - random.nextDouble()) * (double)this.config.spawnRange() + 0.5;
			if (!world.isSpaceEmpty(((EntityType)optional.get()).createSimpleBoundingBox(d, e, f))) {
				return Optional.empty();
			} else {
				Vec3d vec3d = new Vec3d(d, e, f);
				if (!hasLineOfSight(world, pos.toCenterPos(), vec3d)) {
					return Optional.empty();
				} else {
					BlockPos blockPos = BlockPos.ofFloored(vec3d);
					if (!SpawnRestriction.canSpawn((EntityType)optional.get(), world, SpawnReason.TRIAL_SPAWNER, blockPos, world.getRandom())) {
						return Optional.empty();
					} else {
						if (mobSpawnerEntry.getCustomSpawnRules().isPresent()) {
							MobSpawnerEntry.CustomSpawnRules customSpawnRules = (MobSpawnerEntry.CustomSpawnRules)mobSpawnerEntry.getCustomSpawnRules().get();
							if (!customSpawnRules.canSpawn(blockPos, world)) {
								return Optional.empty();
							}
						}

						Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, world, entityx -> {
							entityx.refreshPositionAndAngles(d, e, f, random.nextFloat() * 360.0F, 0.0F);
							return entityx;
						});
						if (entity == null) {
							return Optional.empty();
						} else {
							if (entity instanceof MobEntity mobEntity) {
								if (!mobEntity.canSpawn(world)) {
									return Optional.empty();
								}

								boolean bl = mobSpawnerEntry.getNbt().getSize() == 1 && mobSpawnerEntry.getNbt().contains("id", NbtElement.STRING_TYPE);
								if (bl) {
									mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.TRIAL_SPAWNER, null, null);
								}

								mobEntity.setPersistent();
							}

							if (!world.spawnNewEntityAndPassengers(entity)) {
								return Optional.empty();
							} else {
								world.syncWorldEvent(WorldEvents.TRIAL_SPAWNER_SPAWNS_MOB, pos, 0);
								world.syncWorldEvent(WorldEvents.TRIAL_SPAWNER_SPAWNS_MOB_AT_SPAWN_POS, blockPos, 0);
								world.emitGameEvent(entity, GameEvent.ENTITY_PLACE, blockPos);
								return Optional.of(entity.getUuid());
							}
						}
					}
				}
			}
		}
	}

	public void ejectLootTable(ServerWorld world, BlockPos pos, Identifier lootTable) {
		LootTable lootTable2 = world.getServer().getLootManager().getLootTable(lootTable);
		LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world).build(LootContextTypes.EMPTY);
		ObjectArrayList<ItemStack> objectArrayList = lootTable2.generateLoot(lootContextParameterSet);
		if (!objectArrayList.isEmpty()) {
			for (ItemStack itemStack : objectArrayList) {
				ItemDispenserBehavior.spawnItem(world, itemStack, 2, Direction.UP, Vec3d.ofBottomCenter(pos).offset(Direction.UP, 1.2));
			}

			world.syncWorldEvent(WorldEvents.TRIAL_SPAWNER_EJECTS_ITEM, pos, 0);
		}
	}

	public void tickClient(World world, BlockPos pos) {
		if (!this.canActivate(world)) {
			this.data.lastDisplayEntityRotation = this.data.displayEntityRotation;
		} else {
			TrialSpawnerState trialSpawnerState = this.getSpawnerState();
			trialSpawnerState.emitParticles(world, pos);
			if (trialSpawnerState.doesDisplayRotate()) {
				double d = (double)Math.max(0L, this.data.nextMobSpawnsAt - world.getTime());
				this.data.lastDisplayEntityRotation = this.data.displayEntityRotation;
				this.data.displayEntityRotation = (this.data.displayEntityRotation + trialSpawnerState.getDisplayRotationSpeed() / (d + 200.0)) % 360.0;
			}

			if (trialSpawnerState.playsSound()) {
				Random random = world.getRandom();
				if (random.nextFloat() <= 0.02F) {
					world.playSoundAtBlockCenter(
						pos, SoundEvents.BLOCK_TRIAL_SPAWNER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false
					);
				}
			}
		}
	}

	public void tickServer(ServerWorld world, BlockPos pos) {
		TrialSpawnerState trialSpawnerState = this.getSpawnerState();
		if (!this.canActivate(world)) {
			if (trialSpawnerState.playsSound()) {
				this.data.reset();
				this.setSpawnerState(world, TrialSpawnerState.INACTIVE);
			}
		} else {
			if (this.data.spawnedMobsAlive.removeIf(uuid -> shouldRemoveMobFromData(world, pos, uuid))) {
				this.data.nextMobSpawnsAt = world.getTime() + (long)this.config.ticksBetweenSpawn();
			}

			TrialSpawnerState trialSpawnerState2 = trialSpawnerState.tick(pos, this, world);
			if (trialSpawnerState2 != trialSpawnerState) {
				this.setSpawnerState(world, trialSpawnerState2);
			}
		}
	}

	private static boolean shouldRemoveMobFromData(ServerWorld world, BlockPos pos, UUID uuid) {
		Entity entity = world.getEntity(uuid);
		return entity == null
			|| !entity.isAlive()
			|| !entity.getWorld().getRegistryKey().equals(world.getRegistryKey())
			|| entity.getBlockPos().getSquaredDistance(pos) > (double)MAX_ENTITY_DISTANCE_SQUARED;
	}

	private static boolean hasLineOfSight(World world, Vec3d spawnerPos, Vec3d spawnPos) {
		BlockHitResult blockHitResult = world.raycast(
			new RaycastContext(spawnPos, spawnerPos, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent())
		);
		return blockHitResult.getBlockPos().equals(BlockPos.ofFloored(spawnerPos)) || blockHitResult.getType() == HitResult.Type.MISS;
	}

	public static void addMobSpawnParticles(World world, BlockPos pos, Random random) {
		for (int i = 0; i < 20; i++) {
			double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
			double e = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
			double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
			world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
			world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0, 0.0, 0.0);
		}
	}

	public static void addDetectionParticles(World world, BlockPos pos, Random random, int playerCount) {
		for (int i = 0; i < 30 + Math.min(playerCount, 10) * 5; i++) {
			double d = (double)(2.0F * random.nextFloat() - 1.0F) * 0.65;
			double e = (double)(2.0F * random.nextFloat() - 1.0F) * 0.65;
			double f = (double)pos.getX() + 0.5 + d;
			double g = (double)pos.getY() + 0.1 + (double)random.nextFloat() * 0.8;
			double h = (double)pos.getZ() + 0.5 + e;
			world.addParticle(ParticleTypes.TRIAL_SPAWNER_DETECTION, f, g, h, 0.0, 0.0, 0.0);
		}
	}

	public static void addEjectItemParticles(World world, BlockPos pos, Random random) {
		for (int i = 0; i < 20; i++) {
			double d = (double)pos.getX() + 0.4 + random.nextDouble() * 0.2;
			double e = (double)pos.getY() + 0.4 + random.nextDouble() * 0.2;
			double f = (double)pos.getZ() + 0.4 + random.nextDouble() * 0.2;
			double g = random.nextGaussian() * 0.02;
			double h = random.nextGaussian() * 0.02;
			double j = random.nextGaussian() * 0.02;
			world.addParticle(ParticleTypes.SMALL_FLAME, d, e, f, g, h, j * 0.25);
			world.addParticle(ParticleTypes.SMOKE, d, e, f, g, h, j);
		}
	}

	@Deprecated(
		forRemoval = true
	)
	@VisibleForTesting
	public void setEntityDetector(EntityDetector detector) {
		this.entityDetector = detector;
	}

	@Deprecated(
		forRemoval = true
	)
	@VisibleForTesting
	public void forceActivate() {
		this.forceActivate = true;
	}

	public interface TrialSpawner {
		void setSpawnerState(World world, TrialSpawnerState spawnerState);

		TrialSpawnerState getSpawnerState();

		void updateListeners();
	}
}
