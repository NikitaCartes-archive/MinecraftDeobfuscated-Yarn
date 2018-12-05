package net.minecraft.server.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1419;
import net.minecraft.class_1655;
import net.minecraft.class_1919;
import net.minecraft.class_1946;
import net.minecraft.class_3202;
import net.minecraft.class_3485;
import net.minecraft.class_37;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.BlockActionClientPacket;
import net.minecraft.client.network.packet.EntitySpawnGlobalClientPacket;
import net.minecraft.client.network.packet.EntityStatusClientPacket;
import net.minecraft.client.network.packet.ExplosionClientPacket;
import net.minecraft.client.network.packet.GameStateChangeClientPacket;
import net.minecraft.client.network.packet.ParticleClientPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.WaterCreatureEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.Packet;
import net.minecraft.particle.Particle;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.ScoreboardState;
import net.minecraft.scoreboard.ScoreboardSynchronizer;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.EntityTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sortme.RaidState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagManager;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.MinecraftException;
import net.minecraft.util.Profiler;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.WorldVillageManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.FeatureConfig;
import net.minecraft.world.gen.feature.BonusChestFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorld extends World {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftServer server;
	private final EntityTracker entityTracker;
	private final Map<UUID, Entity> entitiesByUuid = Maps.<UUID, Entity>newHashMap();
	public boolean field_13957;
	private boolean field_13955;
	private int field_13948;
	private final class_1946 field_13956;
	private final ServerTickScheduler<Block> blockTickScheduler = new ServerTickScheduler<>(
		this, block -> block == null || block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, this::method_14189
	);
	private final ServerTickScheduler<Fluid> fluidTickScheduler = new ServerTickScheduler<>(
		this, fluid -> fluid == null || fluid == Fluids.field_15906, Registry.FLUID::getId, Registry.FLUID::get, this::method_14171
	);
	protected final class_1419 field_13958 = new class_1419(this);
	ObjectLinkedOpenHashSet<class_1919> field_13950 = new ObjectLinkedOpenHashSet<>();
	private boolean field_13953;

	public ServerWorld(
		MinecraftServer minecraftServer,
		WorldSaveHandler worldSaveHandler,
		class_37 arg,
		LevelProperties levelProperties,
		DimensionType dimensionType,
		Profiler profiler
	) {
		super(
			worldSaveHandler,
			arg,
			levelProperties,
			dimensionType,
			(world, dimension) -> new ServerChunkManager(
					world,
					worldSaveHandler.createChunkSaveHandler(dimension),
					dimension.createChunkGenerator(),
					minecraftServer.getConfigurationManager().getViewDistance(),
					1
				),
			profiler,
			false
		);
		this.server = minecraftServer;
		this.entityTracker = new EntityTracker(this);
		this.field_13956 = new class_1946(this);
		this.updateAmbientDarkness();
		this.initWeatherGradients();
		this.getWorldBorder().setMaxWorldBorderRadius(minecraftServer.getMaxWorldBorderRadius());
	}

	public ServerWorld method_14185() {
		String string = RaidState.method_16533(this.dimension);
		RaidState raidState = this.method_8648(this.dimension.getType(), RaidState::new, string);
		if (raidState == null) {
			this.raidState = new RaidState(this);
			this.method_8647(this.dimension.getType(), string, this.raidState);
		} else {
			this.raidState = raidState;
			this.raidState.method_16530(this);
		}

		String string2 = WorldVillageManager.getBaseTag(this.dimension);
		WorldVillageManager worldVillageManager = this.method_8648(DimensionType.field_13072, WorldVillageManager::new, string2);
		if (worldVillageManager == null) {
			this.villageManager = new WorldVillageManager(this);
			this.method_8647(DimensionType.field_13072, string2, this.villageManager);
		} else {
			this.villageManager = worldVillageManager;
			this.villageManager.setWorld(this);
		}

		this.villageManager.method_16471();
		ScoreboardState scoreboardState = this.method_8648(DimensionType.field_13072, ScoreboardState::new, "scoreboard");
		if (scoreboardState == null) {
			scoreboardState = new ScoreboardState();
			this.method_8647(DimensionType.field_13072, "scoreboard", scoreboardState);
		}

		scoreboardState.setScoreboard(this.server.method_3845());
		this.server.method_3845().method_12935(new ScoreboardSynchronizer(scoreboardState));
		this.getWorldBorder().setCenter(this.properties.getBorderCenterX(), this.properties.getBorderCenterZ());
		this.getWorldBorder().setDamagePerBlock(this.properties.getBorderDamagePerBlock());
		this.getWorldBorder().setSafeZone(this.properties.getBorderSafeZone());
		this.getWorldBorder().setWarningBlocks(this.properties.getBorderWarningBlocks());
		this.getWorldBorder().setWarningTime(this.properties.getBorderWarningTime());
		if (this.properties.getBorderSizeLerpTime() > 0L) {
			this.getWorldBorder().method_11957(this.properties.getBorderSize(), this.properties.getBorderSizeLerpTarget(), this.properties.getBorderSizeLerpTime());
		} else {
			this.getWorldBorder().setSize(this.properties.getBorderSize());
		}

		return this;
	}

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
		this.field_13953 = true;
		super.tick(booleanSupplier);
		if (this.getLevelProperties().isHardcore() && this.getDifficulty() != Difficulty.HARD) {
			this.getLevelProperties().setDifficulty(Difficulty.HARD);
		}

		if (this.method_14172()) {
			if (this.getGameRules().getBoolean("doDaylightCycle")) {
				long l = this.properties.getTimeOfDay() + 24000L;
				this.setTimeOfDay(l - l % 24000L);
			}

			this.method_14200();
		}

		int i = this.calculateAmbientDarkness(1.0F);
		if (i != this.getAmbientDarkness()) {
			this.setAmbientDarkness(i);
		}

		this.tickTime();
		this.getProfiler().begin("chunkSource");
		this.getChunkManager().tick(booleanSupplier);
		this.getProfiler().endBegin("tickPending");
		this.tickScheduledTicks();
		this.getProfiler().endBegin("village");
		this.villageManager.tick();
		this.field_13958.method_6445();
		this.getProfiler().endBegin("portalForcer");
		this.field_13956.method_8656(this.getTime());
		this.getProfiler().endBegin("raid");
		this.raidState.tick();
		this.getProfiler().end();
		this.method_14192();
		this.field_13953 = false;
	}

	public boolean method_14177() {
		return this.field_13953;
	}

	@Override
	public void updateSleepingStatus() {
		this.field_13955 = false;
		if (!this.players.isEmpty()) {
			int i = 0;
			int j = 0;

			for (PlayerEntity playerEntity : this.players) {
				if (playerEntity.isSpectator()) {
					i++;
				} else if (playerEntity.isSleeping()) {
					j++;
				}
			}

			this.field_13955 = j > 0 && j >= this.players.size() - i;
		}
	}

	public ServerScoreboard method_14170() {
		return this.server.method_3845();
	}

	protected void method_14200() {
		this.field_13955 = false;

		for (PlayerEntity playerEntity : (List)this.players.stream().filter(PlayerEntity::isSleeping).collect(Collectors.toList())) {
			playerEntity.method_7358(false, false, true);
		}

		if (this.getGameRules().getBoolean("doWeatherCycle")) {
			this.method_14195();
		}
	}

	private void method_14195() {
		this.properties.setRainTime(0);
		this.properties.setRaining(false);
		this.properties.setThunderTime(0);
		this.properties.setThundering(false);
	}

	public boolean method_14172() {
		if (this.field_13955 && !this.isRemote) {
			for (PlayerEntity playerEntity : this.players) {
				if (!playerEntity.isSpectator() && !playerEntity.method_7276()) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setDefaultSpawnClient() {
		if (this.properties.getSpawnY() <= 0) {
			this.properties.setSpawnY(this.getSeaLevel() + 1);
		}

		int i = this.properties.getSpawnX();
		int j = this.properties.getSpawnZ();
		int k = 0;

		while (this.getTopNonAirState(new BlockPos(i, 0, j)).isAir()) {
			i += this.random.nextInt(8) - this.random.nextInt(8);
			j += this.random.nextInt(8) - this.random.nextInt(8);
			if (++k == 10000) {
				break;
			}
		}

		this.properties.setSpawnX(i);
		this.properties.setSpawnZ(j);
	}

	@Override
	public boolean isChunkLoaded(int i, int j) {
		return this.method_8393(i, j);
	}

	@Override
	public void updateEntities() {
		if (this.players.isEmpty() && !this.method_8551()) {
			if (this.field_13948++ >= 300) {
				return;
			}
		} else {
			this.method_14197();
		}

		this.dimension.method_12461();
		super.updateEntities();
	}

	@Override
	protected void method_8541() {
		super.method_8541();
		this.getProfiler().endBegin("players");

		for (int i = 0; i < this.players.size(); i++) {
			Entity entity = (Entity)this.players.get(i);
			Entity entity2 = entity.getRiddenEntity();
			if (entity2 != null) {
				if (!entity2.invalid && entity2.hasPassenger(entity)) {
					continue;
				}

				entity.stopRiding();
			}

			this.getProfiler().begin("tick");
			if (!entity.invalid) {
				try {
					this.method_8552(entity);
				} catch (Throwable var7) {
					CrashReport crashReport = CrashReport.create(var7, "Ticking player");
					CrashReportElement crashReportElement = crashReport.addElement("Player being ticked");
					entity.populateCrashReport(crashReportElement);
					throw new CrashException(crashReport);
				}
			}

			this.getProfiler().end();
			this.getProfiler().begin("remove");
			if (entity.invalid) {
				int j = entity.chunkX;
				int k = entity.chunkZ;
				if (entity.field_6016 && this.isChunkLoaded(j, k)) {
					this.getChunk(j, k).remove(entity);
				}

				this.entities.remove(entity);
				this.onEntityRemoved(entity);
			}

			this.getProfiler().end();
		}
	}

	public void method_14197() {
		this.field_13948 = 0;
	}

	public void tickScheduledTicks() {
		if (this.properties.getGeneratorType() != LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.blockTickScheduler.tick();
			this.fluidTickScheduler.tick();
		}
	}

	private void method_14171(ScheduledTick<Fluid> scheduledTick) {
		FluidState fluidState = this.getFluidState(scheduledTick.pos);
		if (fluidState.getFluid() == scheduledTick.getObject()) {
			fluidState.method_15770(this, scheduledTick.pos);
		}
	}

	private void method_14189(ScheduledTick<Block> scheduledTick) {
		BlockState blockState = this.getBlockState(scheduledTick.pos);
		if (blockState.getBlock() == scheduledTick.getObject()) {
			blockState.scheduledTick(this, scheduledTick.pos, this.random);
		}
	}

	@Override
	public void method_8553(Entity entity, boolean bl) {
		if (!this.shouldSpawnAnimals() && (entity instanceof AnimalEntity || entity instanceof WaterCreatureEntity)) {
			entity.invalidate();
		}

		if (!this.shouldSpawnNpcs() && entity instanceof class_1655) {
			entity.invalidate();
		}

		super.method_8553(entity, bl);
	}

	private boolean shouldSpawnNpcs() {
		return this.server.shouldSpawnNpcs();
	}

	private boolean shouldSpawnAnimals() {
		return this.server.shouldSpawnAnimals();
	}

	@Override
	public boolean canPlayerModifyAt(PlayerEntity playerEntity, BlockPos blockPos) {
		return !this.server.isSpawnProtected(this, blockPos, playerEntity) && this.getWorldBorder().contains(blockPos);
	}

	@Override
	public void init(LevelInfo levelInfo) {
		if (!this.properties.isInitialized()) {
			try {
				this.method_14184(levelInfo);
				if (this.properties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
					this.setDebugWorldProperties();
				}

				super.init(levelInfo);
			} catch (Throwable var6) {
				CrashReport crashReport = CrashReport.create(var6, "Exception initializing level");

				try {
					this.toCrashReportElement(crashReport);
				} catch (Throwable var5) {
				}

				throw new CrashException(crashReport);
			}

			this.properties.setInitialized(true);
		}
	}

	private void setDebugWorldProperties() {
		this.properties.setStructures(false);
		this.properties.setCommandsAllowed(true);
		this.properties.setRaining(false);
		this.properties.setThundering(false);
		this.properties.setClearWeatherTime(1000000000);
		this.properties.setTimeOfDay(6000L);
		this.properties.setGameMode(GameMode.field_9219);
		this.properties.setHardcore(false);
		this.properties.setDifficulty(Difficulty.PEACEFUL);
		this.properties.setDifficultyLocked(true);
		this.getGameRules().put("doDaylightCycle", "false", this.server);
	}

	private void method_14184(LevelInfo levelInfo) {
		if (!this.dimension.method_12448()) {
			this.properties.setSpawnPos(BlockPos.ORIGIN.up(this.chunkManager.getChunkGenerator().method_12100()));
		} else if (this.properties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
			this.properties.setSpawnPos(BlockPos.ORIGIN.up());
		} else {
			BiomeSource biomeSource = this.chunkManager.getChunkGenerator().getBiomeSource();
			List<Biome> list = biomeSource.getSpawnBiomes();
			Random random = new Random(this.getSeed());
			BlockPos blockPos = biomeSource.method_8762(0, 0, 256, list, random);
			ChunkPos chunkPos = blockPos == null ? new ChunkPos(0, 0) : new ChunkPos(blockPos);
			if (blockPos == null) {
				LOGGER.warn("Unable to find spawn biome");
			}

			boolean bl = false;

			for (Block block : BlockTags.field_15478.values()) {
				if (biomeSource.getTopMaterials().contains(block.getDefaultState())) {
					bl = true;
					break;
				}
			}

			this.properties.setSpawnPos(chunkPos.method_8323().add(8, this.chunkManager.getChunkGenerator().method_12100(), 8));
			int i = 0;
			int j = 0;
			int k = 0;
			int l = -1;
			int m = 32;

			for (int n = 0; n < 1024; n++) {
				if (i > -16 && i <= 16 && j > -16 && j <= 16) {
					BlockPos blockPos2 = this.dimension.method_12452(new ChunkPos(chunkPos.x + i, chunkPos.z + j), bl);
					if (blockPos2 != null) {
						this.properties.setSpawnPos(blockPos2);
						break;
					}
				}

				if (i == j || i < 0 && i == -j || i > 0 && i == 1 - j) {
					int o = k;
					k = -l;
					l = o;
				}

				i += k;
				j += l;
			}

			if (levelInfo.hasBonusChest()) {
				this.method_14187();
			}
		}
	}

	protected void method_14187() {
		BonusChestFeature bonusChestFeature = Feature.BONUS_CHEST;

		for (int i = 0; i < 10; i++) {
			int j = this.properties.getSpawnX() + this.random.nextInt(6) - this.random.nextInt(6);
			int k = this.properties.getSpawnZ() + this.random.nextInt(6) - this.random.nextInt(6);
			BlockPos blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(j, 0, k)).up();
			if (bonusChestFeature.method_12817(
				this, (ChunkGenerator<? extends ChunkGeneratorSettings>)this.chunkManager.getChunkGenerator(), this.random, blockPos, FeatureConfig.DEFAULT
			)) {
				break;
			}
		}
	}

	@Nullable
	public BlockPos getForcedSpawnPoint() {
		return this.dimension.getForcedSpawnPoint();
	}

	public void save(@Nullable ProgressListener progressListener, boolean bl) throws MinecraftException {
		ServerChunkManager serverChunkManager = this.getChunkManager();
		if (!this.field_13957) {
			if (progressListener != null) {
				progressListener.method_15412(new TranslatableTextComponent("menu.savingLevel"));
			}

			this.method_14188();
			if (progressListener != null) {
				progressListener.method_15414(new TranslatableTextComponent("menu.savingChunks"));
			}

			serverChunkManager.save(bl);
		}
	}

	protected void method_14188() throws MinecraftException {
		this.checkSessionLock();

		for (ServerWorld serverWorld : this.server.getWorlds()) {
			if (serverWorld instanceof class_3202) {
				((class_3202)serverWorld).method_14032();
			}
		}

		this.properties.setBorderSize(this.getWorldBorder().getSize());
		this.properties.setBorderCenterX(this.getWorldBorder().getCenterX());
		this.properties.borderCenterZ(this.getWorldBorder().getCenterZ());
		this.properties.setBorderSafeZone(this.getWorldBorder().getSafeZone());
		this.properties.setBorderDamagePerBlock(this.getWorldBorder().getDamagePerBlock());
		this.properties.setBorderWarningBlocks(this.getWorldBorder().getWarningBlocks());
		this.properties.setBorderWarningTime(this.getWorldBorder().getWarningTime());
		this.properties.setBorderSizeLerpTarget(this.getWorldBorder().getTargetSize());
		this.properties.setBorderSizeLerpTime(this.getWorldBorder().getTargetRemainingTime());
		this.properties.setCustomBossEvents(this.server.method_3837().method_12974());
		this.saveHandler.saveWorld(this.properties, this.server.getConfigurationManager().getUserData());
		this.method_8646().method_265();
	}

	@Override
	public boolean spawnEntity(Entity entity) {
		return this.method_14175(entity) ? super.spawnEntity(entity) : false;
	}

	@Override
	public void loadEntities(Stream<Entity> stream) {
		stream.forEach(entity -> {
			if (this.method_14175(entity)) {
				this.entities.add(entity);
				this.onEntityAdded(entity);
			}
		});
	}

	private boolean method_14175(Entity entity) {
		if (entity.invalid) {
			LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getId(entity.getType()));
			return false;
		} else {
			UUID uUID = entity.getUuid();
			if (this.entitiesByUuid.containsKey(uUID)) {
				Entity entity2 = (Entity)this.entitiesByUuid.get(uUID);
				if (this.unloadedEntities.contains(entity2)) {
					this.unloadedEntities.remove(entity2);
				} else {
					if (!(entity instanceof PlayerEntity)) {
						LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.getId(entity2.getType()), uUID.toString());
						return false;
					}

					LOGGER.warn("Force-added player with duplicate UUID {}", uUID.toString());
				}

				this.method_8507(entity2);
			}

			return true;
		}
	}

	@Override
	protected void onEntityAdded(Entity entity) {
		super.onEntityAdded(entity);
		this.entitiesById.put(entity.getEntityId(), entity);
		this.entitiesByUuid.put(entity.getUuid(), entity);
		Entity[] entitys = entity.getParts();
		if (entitys != null) {
			for (Entity entity2 : entitys) {
				this.entitiesById.put(entity2.getEntityId(), entity2);
			}
		}
	}

	@Override
	protected void onEntityRemoved(Entity entity) {
		super.onEntityRemoved(entity);
		this.entitiesById.method_15312(entity.getEntityId());
		this.entitiesByUuid.remove(entity.getUuid());
		Entity[] entitys = entity.getParts();
		if (entitys != null) {
			for (Entity entity2 : entitys) {
				this.entitiesById.method_15312(entity2.getEntityId());
			}
		}
	}

	@Override
	public boolean addGlobalEntity(Entity entity) {
		if (super.addGlobalEntity(entity)) {
			this.server
				.getConfigurationManager()
				.sendToAround(null, entity.x, entity.y, entity.z, 512.0, this.dimension.getType(), new EntitySpawnGlobalClientPacket(entity));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_8421(Entity entity, byte b) {
		this.getEntityTracker().method_14073(entity, new EntityStatusClientPacket(entity, b));
	}

	public ServerChunkManager getChunkManager() {
		return (ServerChunkManager)super.getChunkManager();
	}

	@Environment(EnvType.CLIENT)
	public CompletableFuture<WorldChunk> method_16177(int i, int j, boolean bl) {
		return this.getChunkManager().getChunkAsync(i, j, ChunkStatus.field_12803, bl).thenApply(either -> either.map(chunk -> (WorldChunk)chunk, unloaded -> null));
	}

	@Override
	public Explosion createExplosion(@Nullable Entity entity, DamageSource damageSource, double d, double e, double f, float g, boolean bl, boolean bl2) {
		Explosion explosion = new Explosion(this, entity, d, e, f, g, bl, bl2);
		if (damageSource != null) {
			explosion.setDamageSource(damageSource);
		}

		explosion.collectBlocksAndDamageEntities();
		explosion.affectWorld(false);
		if (!bl2) {
			explosion.clearAffectedBlocks();
		}

		for (PlayerEntity playerEntity : this.players) {
			if (playerEntity.squaredDistanceTo(d, e, f) < 4096.0) {
				((ServerPlayerEntity)playerEntity)
					.networkHandler
					.sendPacket(new ExplosionClientPacket(d, e, f, g, explosion.getAffectedBlocks(), (Vec3d)explosion.getAffectedPlayers().get(playerEntity)));
			}
		}

		return explosion;
	}

	@Override
	public void addBlockAction(BlockPos blockPos, Block block, int i, int j) {
		this.field_13950.add(new class_1919(blockPos, block, i, j));
	}

	private void method_14192() {
		while (!this.field_13950.isEmpty()) {
			class_1919 lv = this.field_13950.removeFirst();
			if (this.method_14174(lv)) {
				this.server
					.getConfigurationManager()
					.sendToAround(
						null,
						(double)lv.method_8306().getX(),
						(double)lv.method_8306().getY(),
						(double)lv.method_8306().getZ(),
						64.0,
						this.dimension.getType(),
						new BlockActionClientPacket(lv.method_8306(), lv.method_8309(), lv.method_8307(), lv.method_8308())
					);
			}
		}
	}

	private boolean method_14174(class_1919 arg) {
		BlockState blockState = this.getBlockState(arg.method_8306());
		return blockState.getBlock() == arg.method_8309() ? blockState.onBlockAction(this, arg.method_8306(), arg.method_8307(), arg.method_8308()) : false;
	}

	@Override
	public void close() {
		this.chunkManager.close();
		super.close();
	}

	@Override
	protected void updateWeather() {
		boolean bl = this.isRaining();
		super.updateWeather();
		if (this.rainGradientPrev != this.rainGradient) {
			this.server.getConfigurationManager().sendToDimension(new GameStateChangeClientPacket(7, this.rainGradient), this.dimension.getType());
		}

		if (this.thunderGradientPrev != this.thunderGradient) {
			this.server.getConfigurationManager().sendToDimension(new GameStateChangeClientPacket(8, this.thunderGradient), this.dimension.getType());
		}

		if (bl != this.isRaining()) {
			if (bl) {
				this.server.getConfigurationManager().sendToAll(new GameStateChangeClientPacket(2, 0.0F));
			} else {
				this.server.getConfigurationManager().sendToAll(new GameStateChangeClientPacket(1, 0.0F));
			}

			this.server.getConfigurationManager().sendToAll(new GameStateChangeClientPacket(7, this.rainGradient));
			this.server.getConfigurationManager().sendToAll(new GameStateChangeClientPacket(8, this.thunderGradient));
		}
	}

	public ServerTickScheduler<Block> getBlockTickScheduler() {
		return this.blockTickScheduler;
	}

	public ServerTickScheduler<Fluid> getFluidTickScheduler() {
		return this.fluidTickScheduler;
	}

	@Nonnull
	@Override
	public MinecraftServer getServer() {
		return this.server;
	}

	public EntityTracker getEntityTracker() {
		return this.entityTracker;
	}

	public class_1946 method_14173() {
		return this.field_13956;
	}

	public class_3485 method_14183() {
		return this.saveHandler.method_134();
	}

	public <T extends Particle> int method_14199(T particle, double d, double e, double f, int i, double g, double h, double j, double k) {
		ParticleClientPacket particleClientPacket = new ParticleClientPacket(particle, false, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i);
		int l = 0;

		for (int m = 0; m < this.players.size(); m++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(m);
			if (this.method_14191(serverPlayerEntity, false, d, e, f, particleClientPacket)) {
				l++;
			}
		}

		return l;
	}

	public <T extends Particle> boolean method_14166(
		ServerPlayerEntity serverPlayerEntity, T particle, boolean bl, double d, double e, double f, int i, double g, double h, double j, double k
	) {
		Packet<?> packet = new ParticleClientPacket(particle, bl, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i);
		return this.method_14191(serverPlayerEntity, bl, d, e, f, packet);
	}

	private boolean method_14191(ServerPlayerEntity serverPlayerEntity, boolean bl, double d, double e, double f, Packet<?> packet) {
		if (serverPlayerEntity.getServerWorld() != this) {
			return false;
		} else {
			BlockPos blockPos = serverPlayerEntity.getPos();
			double g = blockPos.squaredDistanceTo(d, e, f);
			if (!(g <= 1024.0) && (!bl || !(g <= 262144.0))) {
				return false;
			} else {
				serverPlayerEntity.networkHandler.sendPacket(packet);
				return true;
			}
		}
	}

	@Nullable
	@Override
	public Entity getEntityByUuid(UUID uUID) {
		return (Entity)this.entitiesByUuid.get(uUID);
	}

	@Nullable
	@Override
	public BlockPos method_8487(String string, BlockPos blockPos, int i, boolean bl) {
		return this.getChunkManager().getChunkGenerator().locateStructure(this, string, blockPos, i, bl);
	}

	@Override
	public RecipeManager getRecipeManager() {
		return this.server.getRecipeManager();
	}

	@Override
	public TagManager getTagManager() {
		return this.server.getTagManager();
	}

	@Override
	public void setTime(long l) {
		super.setTime(l);
		this.properties.method_143().method_988(this.server, l);
	}

	@Override
	public boolean method_8458() {
		return this.field_13957;
	}
}
