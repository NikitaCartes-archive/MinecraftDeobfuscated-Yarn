package net.minecraft.block.enums;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.OminousItemSpawnerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
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

	private TrialSpawnerState(
		final String id, final int luminance, final TrialSpawnerState.ParticleEmitter particleEmitter, final double displayRotationSpeed, final boolean playsSound
	) {
		this.id = id;
		this.luminance = luminance;
		this.particleEmitter = particleEmitter;
		this.displayRotationSpeed = displayRotationSpeed;
		this.playsSound = playsSound;
	}

	public TrialSpawnerState tick(BlockPos pos, TrialSpawnerLogic logic, ServerWorld world) {
		TrialSpawnerData trialSpawnerData = logic.getData();
		TrialSpawnerConfig trialSpawnerConfig = logic.getConfig();
		TrialSpawnerState var10000;
		switch (this) {
			case INACTIVE:
				var10000 = trialSpawnerData.setDisplayEntity(logic, world, WAITING_FOR_PLAYERS) == null ? this : WAITING_FOR_PLAYERS;
				break;
			case WAITING_FOR_PLAYERS:
				if (!trialSpawnerData.hasSpawnData(logic, world.random)) {
					var10000 = INACTIVE;
				} else {
					trialSpawnerData.updatePlayers(world, pos, logic);
					var10000 = trialSpawnerData.players.isEmpty() ? this : ACTIVE;
				}
				break;
			case ACTIVE:
				if (!trialSpawnerData.hasSpawnData(logic, world.random)) {
					var10000 = INACTIVE;
				} else {
					int i = trialSpawnerData.getAdditionalPlayers(pos);
					trialSpawnerData.updatePlayers(world, pos, logic);
					if (logic.isOminous()) {
						this.spawnOminousItemSpawner(world, pos, logic);
					}

					if (trialSpawnerData.hasSpawnedAllMobs(trialSpawnerConfig, i)) {
						if (trialSpawnerData.areMobsDead()) {
							trialSpawnerData.cooldownEnd = world.getTime() + (long)logic.getCooldownLength();
							trialSpawnerData.totalSpawnedMobs = 0;
							trialSpawnerData.nextMobSpawnsAt = 0L;
							var10000 = WAITING_FOR_REWARD_EJECTION;
							break;
						}
					} else if (trialSpawnerData.canSpawnMore(world, trialSpawnerConfig, i)) {
						logic.trySpawnMob(world, pos).ifPresent(uuid -> {
							trialSpawnerData.spawnedMobsAlive.add(uuid);
							trialSpawnerData.totalSpawnedMobs++;
							trialSpawnerData.nextMobSpawnsAt = world.getTime() + (long)trialSpawnerConfig.ticksBetweenSpawn();
							trialSpawnerConfig.spawnPotentialsDefinition().getOrEmpty(world.getRandom()).ifPresent(spawnData -> {
								trialSpawnerData.spawnData = Optional.of((MobSpawnerEntry)spawnData.data());
								logic.updateListeners();
							});
						});
					}

					var10000 = this;
				}
				break;
			case WAITING_FOR_REWARD_EJECTION:
				if (trialSpawnerData.isCooldownPast(world, 40.0F, logic.getCooldownLength())) {
					world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_OPEN_SHUTTER, SoundCategory.BLOCKS);
					var10000 = EJECTING_REWARD;
				} else {
					var10000 = this;
				}
				break;
			case EJECTING_REWARD:
				if (!trialSpawnerData.isCooldownAtRepeating(world, (float)BETWEEN_EJECTING_REWARDS_COOLDOWN, logic.getCooldownLength())) {
					var10000 = this;
				} else if (trialSpawnerData.players.isEmpty()) {
					world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_CLOSE_SHUTTER, SoundCategory.BLOCKS);
					trialSpawnerData.rewardLootTable = Optional.empty();
					var10000 = COOLDOWN;
				} else {
					if (trialSpawnerData.rewardLootTable.isEmpty()) {
						trialSpawnerData.rewardLootTable = trialSpawnerConfig.lootTablesToEject().getDataOrEmpty(world.getRandom());
					}

					trialSpawnerData.rewardLootTable.ifPresent(lootTable -> logic.ejectLootTable(world, pos, lootTable));
					trialSpawnerData.players.remove(trialSpawnerData.players.iterator().next());
					var10000 = this;
				}
				break;
			case COOLDOWN:
				trialSpawnerData.updatePlayers(world, pos, logic);
				if (!trialSpawnerData.players.isEmpty()) {
					trialSpawnerData.totalSpawnedMobs = 0;
					trialSpawnerData.nextMobSpawnsAt = 0L;
					var10000 = ACTIVE;
				} else if (trialSpawnerData.isCooldownOver(world)) {
					trialSpawnerData.cooldownEnd = 0L;
					logic.setNotOminous(world, pos);
					var10000 = WAITING_FOR_PLAYERS;
				} else {
					var10000 = this;
				}
				break;
			default:
				throw new MatchException(null, null);
		}

		return var10000;
	}

	private void spawnOminousItemSpawner(ServerWorld world, BlockPos pos, TrialSpawnerLogic logic) {
		TrialSpawnerData trialSpawnerData = logic.getData();
		TrialSpawnerConfig trialSpawnerConfig = logic.getConfig();
		ItemStack itemStack = (ItemStack)trialSpawnerData.getItemsToDropWhenOminous(world, trialSpawnerConfig, pos)
			.getDataOrEmpty(world.random)
			.orElse(ItemStack.EMPTY);
		if (!itemStack.isEmpty()) {
			if (this.shouldCooldownEnd(world, trialSpawnerData)) {
				getPosToSpawnItemSpawner(world, pos, logic, trialSpawnerData).ifPresent(posx -> {
					OminousItemSpawnerEntity ominousItemSpawnerEntity = OminousItemSpawnerEntity.create(world, itemStack);
					ominousItemSpawnerEntity.refreshPositionAfterTeleport(posx);
					world.spawnEntity(ominousItemSpawnerEntity);
					float f = (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F + 1.0F;
					world.playSound(null, BlockPos.ofFloored(posx), SoundEvents.BLOCK_TRIAL_SPAWNER_SPAWN_ITEM_BEGIN, SoundCategory.BLOCKS, 1.0F, f);
					trialSpawnerData.cooldownEnd = world.getTime() + logic.getOminousConfig().getCooldownLength();
				});
			}
		}
	}

	private static Optional<Vec3d> getPosToSpawnItemSpawner(ServerWorld world, BlockPos pos, TrialSpawnerLogic logic, TrialSpawnerData data) {
		List<PlayerEntity> list = data.players
			.stream()
			.map(world::getPlayerByUuid)
			.filter(Objects::nonNull)
			.filter(
				player -> !player.isCreative()
						&& !player.isSpectator()
						&& player.isAlive()
						&& player.squaredDistanceTo(pos.toCenterPos()) <= (double)MathHelper.square(logic.getDetectionRadius())
			)
			.toList();
		if (list.isEmpty()) {
			return Optional.empty();
		} else {
			Entity entity = getRandomEntity(list, data.spawnedMobsAlive, logic, pos, world);
			return entity == null ? Optional.empty() : getPosAbove(entity, world);
		}
	}

	private static Optional<Vec3d> getPosAbove(Entity entity, ServerWorld world) {
		Vec3d vec3d = entity.getPos();
		Vec3d vec3d2 = vec3d.offset(Direction.UP, (double)(entity.getHeight() + 2.0F + (float)world.random.nextInt(4)));
		BlockHitResult blockHitResult = world.raycast(
			new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent())
		);
		Vec3d vec3d3 = blockHitResult.getBlockPos().toCenterPos().offset(Direction.DOWN, 1.0);
		BlockPos blockPos = BlockPos.ofFloored(vec3d3);
		return !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty() ? Optional.empty() : Optional.of(vec3d3);
	}

	@Nullable
	private static Entity getRandomEntity(List<PlayerEntity> players, Set<UUID> entityUuids, TrialSpawnerLogic logic, BlockPos pos, ServerWorld world) {
		Stream<Entity> stream = entityUuids.stream()
			.map(world::getEntity)
			.filter(Objects::nonNull)
			.filter(entity -> entity.isAlive() && entity.squaredDistanceTo(pos.toCenterPos()) <= (double)MathHelper.square(logic.getDetectionRadius()));
		List<? extends Entity> list = world.random.nextBoolean() ? stream.toList() : players;
		if (list.isEmpty()) {
			return null;
		} else {
			return list.size() == 1 ? (Entity)list.getFirst() : Util.getRandom(list, world.random);
		}
	}

	private boolean shouldCooldownEnd(ServerWorld world, TrialSpawnerData data) {
		return world.getTime() >= data.cooldownEnd;
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

	public void emitParticles(World world, BlockPos pos, boolean ominous) {
		this.particleEmitter.emit(world, world.getRandom(), pos, ominous);
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
		TrialSpawnerState.ParticleEmitter NONE = (world, random, pos, ominous) -> {
		};
		TrialSpawnerState.ParticleEmitter WAITING = (world, random, pos, ominous) -> {
			if (random.nextInt(2) == 0) {
				Vec3d vec3d = pos.toCenterPos().addRandom(random, 0.9F);
				emitParticle(ominous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME, vec3d, world);
			}
		};
		TrialSpawnerState.ParticleEmitter ACTIVE = (world, random, pos, ominous) -> {
			Vec3d vec3d = pos.toCenterPos().addRandom(random, 1.0F);
			emitParticle(ParticleTypes.SMOKE, vec3d, world);
			emitParticle(ominous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, vec3d, world);
		};
		TrialSpawnerState.ParticleEmitter COOLDOWN = (world, random, pos, ominous) -> {
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

		private static void emitParticle(SimpleParticleType type, Vec3d pos, World world) {
			world.addParticle(type, pos.getX(), pos.getY(), pos.getZ(), 0.0, 0.0, 0.0);
		}

		void emit(World world, Random random, BlockPos pos, boolean ominous);
	}
}
