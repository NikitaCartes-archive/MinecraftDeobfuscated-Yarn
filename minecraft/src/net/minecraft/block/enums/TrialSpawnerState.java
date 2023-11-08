package net.minecraft.block.enums;

import java.util.Optional;
import net.minecraft.block.spawner.EntityDetector;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public enum TrialSpawnerState implements StringIdentifiable {
	INACTIVE("inactive", 0, TrialSpawnerState.ParticleEmitter.NONE, -1.0, false),
	WAITING_FOR_PLAYERS("waiting_for_players", 4, TrialSpawnerState.ParticleEmitter.WAITING, 200.0, true),
	ACTIVE("active", 8, TrialSpawnerState.ParticleEmitter.ACTIVE, 1000.0, true),
	WAITING_FOR_REWARD_EJECTION("waiting_for_reward_ejection", 8, TrialSpawnerState.ParticleEmitter.WAITING, -1.0, false),
	EJECTING_REWARD("ejecting_reward", 8, TrialSpawnerState.ParticleEmitter.WAITING, -1.0, false),
	COOLDOWN("cooldown", 0, TrialSpawnerState.ParticleEmitter.COOLDOWN, -1.0, false);

	private static final float START_EJECTING_REWARDS_COOLDOWN = 40.0F;
	private static final int BETWEEN_EJECTING_REWARDS_COOLDOWN = MathHelper.floor(30.0F);
	private final String id;
	private final int luminance;
	private final double displayRotationSpeed;
	private final TrialSpawnerState.ParticleEmitter particleEmitter;
	private final boolean playsSound;

	private TrialSpawnerState(String id, int luminance, TrialSpawnerState.ParticleEmitter particleEmitter, double displayRotationSpeed, boolean playsSound) {
		this.id = id;
		this.luminance = luminance;
		this.particleEmitter = particleEmitter;
		this.displayRotationSpeed = displayRotationSpeed;
		this.playsSound = playsSound;
	}

	public TrialSpawnerState tick(BlockPos pos, TrialSpawnerLogic logic, ServerWorld world) {
		TrialSpawnerData trialSpawnerData = logic.getData();
		TrialSpawnerConfig trialSpawnerConfig = logic.getConfig();
		EntityDetector entityDetector = logic.getEntityDetector();

		return switch (this) {
			case INACTIVE -> trialSpawnerData.setDisplayEntity(logic, world, WAITING_FOR_PLAYERS) == null ? this : WAITING_FOR_PLAYERS;
			case WAITING_FOR_PLAYERS -> {
				if (!trialSpawnerData.hasSpawnData()) {
					yield INACTIVE;
				} else {
					trialSpawnerData.updatePlayers(world, pos, entityDetector, trialSpawnerConfig.requiredPlayerRange());
					yield trialSpawnerData.players.isEmpty() ? this : ACTIVE;
				}
			}
			case ACTIVE -> {
				if (!trialSpawnerData.hasSpawnData()) {
					yield INACTIVE;
				} else {
					int i = trialSpawnerData.getAdditionalPlayers(pos);
					trialSpawnerData.updatePlayers(world, pos, entityDetector, trialSpawnerConfig.requiredPlayerRange());
					if (trialSpawnerData.hasSpawnedAllMobs(trialSpawnerConfig, i)) {
						if (trialSpawnerData.areMobsDead()) {
							trialSpawnerData.cooldownEnd = world.getTime() + (long)trialSpawnerConfig.targetCooldownLength();
							trialSpawnerData.totalSpawnedMobs = 0;
							trialSpawnerData.nextMobSpawnsAt = 0L;
							yield WAITING_FOR_REWARD_EJECTION;
						}
					} else if (trialSpawnerData.canSpawnMore(world, trialSpawnerConfig, i)) {
						logic.trySpawnMob(world, pos).ifPresent(uuid -> {
							trialSpawnerData.spawnedMobsAlive.add(uuid);
							trialSpawnerData.totalSpawnedMobs++;
							trialSpawnerData.nextMobSpawnsAt = world.getTime() + (long)trialSpawnerConfig.ticksBetweenSpawn();
							trialSpawnerData.spawnDataPool.getOrEmpty(world.getRandom()).ifPresent(spawnData -> {
								trialSpawnerData.spawnData = Optional.of((MobSpawnerEntry)spawnData.getData());
								logic.updateListeners();
							});
						});
					}

					yield this;
				}
			}
			case WAITING_FOR_REWARD_EJECTION -> {
				if (trialSpawnerData.isCooldownPast(world, trialSpawnerConfig, START_EJECTING_REWARDS_COOLDOWN)) {
					world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_OPEN_SHUTTER, SoundCategory.BLOCKS);
					yield EJECTING_REWARD;
				} else {
					yield this;
				}
			}
			case EJECTING_REWARD -> {
				if (!trialSpawnerData.isCooldownAtRepeating(world, trialSpawnerConfig, (float)BETWEEN_EJECTING_REWARDS_COOLDOWN)) {
					yield this;
				} else if (trialSpawnerData.players.isEmpty()) {
					world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_CLOSE_SHUTTER, SoundCategory.BLOCKS);
					trialSpawnerData.rewardLootTable = Optional.empty();
					yield COOLDOWN;
				} else {
					if (trialSpawnerData.rewardLootTable.isEmpty()) {
						trialSpawnerData.rewardLootTable = trialSpawnerConfig.lootTablesToEject().getDataOrEmpty(world.getRandom());
					}

					trialSpawnerData.rewardLootTable.ifPresent(lootTable -> logic.ejectLootTable(world, pos, lootTable));
					trialSpawnerData.players.remove(trialSpawnerData.players.iterator().next());
					yield this;
				}
			}
			case COOLDOWN -> {
				if (trialSpawnerData.isCooldownOver(world)) {
					trialSpawnerData.cooldownEnd = 0L;
					yield WAITING_FOR_PLAYERS;
				} else {
					yield this;
				}
			}
		};
	}

	public int getLuminance() {
		return this.luminance;
	}

	public double getDisplayRotationSpeed() {
		return this.displayRotationSpeed;
	}

	public boolean doesDisplayRotate() {
		return this.displayRotationSpeed >= 0.0;
	}

	public boolean playsSound() {
		return this.playsSound;
	}

	public void emitParticles(World world, BlockPos pos) {
		this.particleEmitter.emit(world, world.getRandom(), pos);
	}

	@Override
	public String asString() {
		return this.id;
	}

	static class DisplayRotationSpeed {
		private static final double NONE = -1.0;
		private static final double SLOW = 200.0;
		private static final double FAST = 1000.0;

		private DisplayRotationSpeed() {
		}
	}

	static class Luminance {
		private static final int NONE = 0;
		private static final int LOW = 4;
		private static final int HIGH = 8;

		private Luminance() {
		}
	}

	interface ParticleEmitter {
		TrialSpawnerState.ParticleEmitter NONE = (world, random, pos) -> {
		};
		TrialSpawnerState.ParticleEmitter WAITING = (world, random, pos) -> {
			if (random.nextInt(2) == 0) {
				Vec3d vec3d = pos.toCenterPos().addRandom(random, 0.9F);
				emitParticle(ParticleTypes.SMALL_FLAME, vec3d, world);
			}
		};
		TrialSpawnerState.ParticleEmitter ACTIVE = (wolrd, random, pos) -> {
			Vec3d vec3d = pos.toCenterPos().addRandom(random, 1.0F);
			emitParticle(ParticleTypes.SMOKE, vec3d, wolrd);
			emitParticle(ParticleTypes.FLAME, vec3d, wolrd);
		};
		TrialSpawnerState.ParticleEmitter COOLDOWN = (world, random, pos) -> {
			Vec3d vec3d = pos.toCenterPos().addRandom(random, 0.9F);
			if (random.nextInt(3) == 0) {
				emitParticle(ParticleTypes.SMOKE, vec3d, world);
			}

			if (world.getTime() % 20L == 0L) {
				Vec3d vec3d2 = pos.toCenterPos().add(0.0, 0.5, 0.0);
				int i = world.getRandom().nextInt(4) + 20;

				for (int j = 0; j < i; j++) {
					emitParticle(ParticleTypes.SMOKE, vec3d2, world);
				}
			}
		};

		private static void emitParticle(DefaultParticleType type, Vec3d pos, World world) {
			world.addParticle(type, pos.getX(), pos.getY(), pos.getZ(), 0.0, 0.0, 0.0);
		}

		void emit(World world, Random random, BlockPos pos);
	}
}
