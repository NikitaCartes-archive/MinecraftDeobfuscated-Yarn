package net.minecraft.block.spawner;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TrialSpawnerBlock;
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
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
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
	public static final String NORMAL_CONFIG_NBT_KEY = "normal_config";
	public static final String OMINOUS_CONFIG_NBT_KEY = "ominous_config";
	public static final int field_47358 = 40;
	private static final int field_50179 = 36000;
	private static final int field_50180 = 14;
	private static final int MAX_ENTITY_DISTANCE = 47;
	private static final int MAX_ENTITY_DISTANCE_SQUARED = MathHelper.square(47);
	private static final float field_47361 = 0.02F;
	private final TrialSpawnerConfig normalConfig;
	private final TrialSpawnerConfig ominousConfig;
	private final TrialSpawnerData data;
	private final int entityDetectionRange;
	private final int cooldownLength;
	private final TrialSpawnerLogic.TrialSpawner trialSpawner;
	private EntityDetector entityDetector;
	private final EntityDetector.Selector entitySelector;
	private boolean forceActivate;
	private boolean ominous;

	public Codec<TrialSpawnerLogic> codec() {
		return RecordCodecBuilder.create(
			instance -> instance.group(
						TrialSpawnerConfig.CODEC.optionalFieldOf("normal_config", TrialSpawnerConfig.DEFAULT).forGetter(TrialSpawnerLogic::getNormalConfig),
						TrialSpawnerConfig.CODEC.optionalFieldOf("ominous_config", TrialSpawnerConfig.DEFAULT).forGetter(TrialSpawnerLogic::getOminousConfigForSerialization),
						TrialSpawnerData.codec.forGetter(TrialSpawnerLogic::getData),
						Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("target_cooldown_length", 36000).forGetter(TrialSpawnerLogic::getCooldownLength),
						Codec.intRange(1, 128).optionalFieldOf("required_player_range", 14).forGetter(TrialSpawnerLogic::getDetectionRadius)
					)
					.apply(
						instance,
						(config, trialSpawnerConfig, trialSpawnerData, integer, integer2) -> new TrialSpawnerLogic(
								config, trialSpawnerConfig, trialSpawnerData, integer, integer2, this.trialSpawner, this.entityDetector, this.entitySelector
							)
					)
		);
	}

	public TrialSpawnerLogic(TrialSpawnerLogic.TrialSpawner trialSpawner, EntityDetector entityDetector, EntityDetector.Selector entitySelector) {
		this(TrialSpawnerConfig.DEFAULT, TrialSpawnerConfig.DEFAULT, new TrialSpawnerData(), 36000, 14, trialSpawner, entityDetector, entitySelector);
	}

	public TrialSpawnerLogic(
		TrialSpawnerConfig normalConfig,
		TrialSpawnerConfig ominousConfig,
		TrialSpawnerData data,
		int cooldownLength,
		int entityDetectionRange,
		TrialSpawnerLogic.TrialSpawner trialSpawner,
		EntityDetector entityDetector,
		EntityDetector.Selector entitySelector
	) {
		this.normalConfig = normalConfig;
		this.ominousConfig = ominousConfig;
		this.data = data;
		this.cooldownLength = cooldownLength;
		this.entityDetectionRange = entityDetectionRange;
		this.trialSpawner = trialSpawner;
		this.entityDetector = entityDetector;
		this.entitySelector = entitySelector;
	}

	public TrialSpawnerConfig getConfig() {
		return this.ominous ? this.ominousConfig : this.normalConfig;
	}

	@VisibleForTesting
	public TrialSpawnerConfig getNormalConfig() {
		return this.normalConfig;
	}

	@VisibleForTesting
	public TrialSpawnerConfig getOminousConfig() {
		return this.ominousConfig;
	}

	private TrialSpawnerConfig getOminousConfigForSerialization() {
		return !this.ominousConfig.equals(this.normalConfig) ? this.ominousConfig : TrialSpawnerConfig.DEFAULT;
	}

	public void setOminous(ServerWorld world, BlockPos pos) {
		world.setBlockState(pos, world.getBlockState(pos).with(TrialSpawnerBlock.OMINOUS, Boolean.valueOf(true)), Block.NOTIFY_ALL);
		world.syncWorldEvent(WorldEvents.TRIAL_SPAWNER_TURNS_OMINOUS, pos, 1);
		this.ominous = true;
		this.data.resetAndClearMobs(this, world);
	}

	public void setNotOminous(ServerWorld world, BlockPos pos) {
		world.setBlockState(pos, world.getBlockState(pos).with(TrialSpawnerBlock.OMINOUS, Boolean.valueOf(false)), Block.NOTIFY_ALL);
		this.ominous = false;
	}

	public boolean isOminous() {
		return this.ominous;
	}

	public TrialSpawnerData getData() {
		return this.data;
	}

	public int getCooldownLength() {
		return this.cooldownLength;
	}

	public int getDetectionRadius() {
		return this.entityDetectionRange;
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

	public EntityDetector.Selector getEntitySelector() {
		return this.entitySelector;
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
			double d = i >= 1 ? nbtList.getDouble(0) : (double)pos.getX() + (random.nextDouble() - random.nextDouble()) * (double)this.getConfig().spawnRange() + 0.5;
			double e = i >= 2 ? nbtList.getDouble(1) : (double)(pos.getY() + random.nextInt(3) - 1);
			double f = i >= 3 ? nbtList.getDouble(2) : (double)pos.getZ() + (random.nextDouble() - random.nextDouble()) * (double)this.getConfig().spawnRange() + 0.5;
			if (!world.isSpaceEmpty(((EntityType)optional.get()).getSpawnBox(d, e, f))) {
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
									mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.TRIAL_SPAWNER, null);
								}

								mobEntity.setPersistent();
								mobSpawnerEntry.getEquipment().ifPresent(mobEntity::setEquipmentFromTable);
							}

							if (!world.spawnNewEntityAndPassengers(entity)) {
								return Optional.empty();
							} else {
								TrialSpawnerLogic.Type type = this.ominous ? TrialSpawnerLogic.Type.OMINOUS : TrialSpawnerLogic.Type.NORMAL;
								world.syncWorldEvent(WorldEvents.TRIAL_SPAWNER_SPAWNS_MOB, pos, type.getIndex());
								world.syncWorldEvent(WorldEvents.TRIAL_SPAWNER_SPAWNS_MOB_AT_SPAWN_POS, blockPos, type.getIndex());
								world.emitGameEvent(entity, GameEvent.ENTITY_PLACE, blockPos);
								return Optional.of(entity.getUuid());
							}
						}
					}
				}
			}
		}
	}

	public void ejectLootTable(ServerWorld world, BlockPos pos, RegistryKey<LootTable> lootTable) {
		LootTable lootTable2 = world.getServer().getReloadableRegistries().getLootTable(lootTable);
		LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world).build(LootContextTypes.EMPTY);
		ObjectArrayList<ItemStack> objectArrayList = lootTable2.generateLoot(lootContextParameterSet);
		if (!objectArrayList.isEmpty()) {
			for (ItemStack itemStack : objectArrayList) {
				ItemDispenserBehavior.spawnItem(world, itemStack, 2, Direction.UP, Vec3d.ofBottomCenter(pos).offset(Direction.UP, 1.2));
			}

			world.syncWorldEvent(WorldEvents.TRIAL_SPAWNER_EJECTS_ITEM, pos, 0);
		}
	}

	public void tickClient(World world, BlockPos pos, boolean ominous) {
		TrialSpawnerState trialSpawnerState = this.getSpawnerState();
		trialSpawnerState.emitParticles(world, pos, ominous);
		if (trialSpawnerState.doesDisplayRotate()) {
			double d = (double)Math.max(0L, this.data.nextMobSpawnsAt - world.getTime());
			this.data.lastDisplayEntityRotation = this.data.displayEntityRotation;
			this.data.displayEntityRotation = (this.data.displayEntityRotation + trialSpawnerState.getDisplayRotationSpeed() / (d + 200.0)) % 360.0;
		}

		if (trialSpawnerState.playsSound()) {
			Random random = world.getRandom();
			if (random.nextFloat() <= 0.02F) {
				SoundEvent soundEvent = ominous ? SoundEvents.BLOCK_TRIAL_SPAWNER_AMBIENT_OMINOUS : SoundEvents.BLOCK_TRIAL_SPAWNER_AMBIENT;
				world.playSoundAtBlockCenter(pos, soundEvent, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
			}
		}
	}

	public void tickServer(ServerWorld world, BlockPos pos, boolean ominous) {
		this.ominous = ominous;
		TrialSpawnerState trialSpawnerState = this.getSpawnerState();
		if (this.data.spawnedMobsAlive.removeIf(uuid -> shouldRemoveMobFromData(world, pos, uuid))) {
			this.data.nextMobSpawnsAt = world.getTime() + (long)this.getConfig().ticksBetweenSpawn();
		}

		TrialSpawnerState trialSpawnerState2 = trialSpawnerState.tick(pos, this, world);
		if (trialSpawnerState2 != trialSpawnerState) {
			this.setSpawnerState(world, trialSpawnerState2);
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

	public static void addMobSpawnParticles(World world, BlockPos pos, Random random, SimpleParticleType particle) {
		for (int i = 0; i < 20; i++) {
			double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
			double e = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
			double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
			world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
			world.addParticle(particle, d, e, f, 0.0, 0.0, 0.0);
		}
	}

	public static void addTrialOmenParticles(World world, BlockPos pos, Random random) {
		for (int i = 0; i < 20; i++) {
			double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
			double e = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
			double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
			double g = random.nextGaussian() * 0.02;
			double h = random.nextGaussian() * 0.02;
			double j = random.nextGaussian() * 0.02;
			world.addParticle(ParticleTypes.TRIAL_OMEN, d, e, f, g, h, j);
			world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d, e, f, g, h, j);
		}
	}

	public static void addDetectionParticles(World world, BlockPos pos, Random random, int playerCount, ParticleEffect particle) {
		for (int i = 0; i < 30 + Math.min(playerCount, 10) * 5; i++) {
			double d = (double)(2.0F * random.nextFloat() - 1.0F) * 0.65;
			double e = (double)(2.0F * random.nextFloat() - 1.0F) * 0.65;
			double f = (double)pos.getX() + 0.5 + d;
			double g = (double)pos.getY() + 0.1 + (double)random.nextFloat() * 0.8;
			double h = (double)pos.getZ() + 0.5 + e;
			world.addParticle(particle, f, g, h, 0.0, 0.0, 0.0);
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

	public static enum Type {
		NORMAL(ParticleTypes.FLAME),
		OMINOUS(ParticleTypes.SOUL_FIRE_FLAME);

		public final SimpleParticleType particle;

		private Type(final SimpleParticleType particle) {
			this.particle = particle;
		}

		public static TrialSpawnerLogic.Type fromIndex(int index) {
			TrialSpawnerLogic.Type[] types = values();
			return index <= types.length && index >= 0 ? types[index] : NORMAL;
		}

		public int getIndex() {
			return this.ordinal();
		}
	}
}
