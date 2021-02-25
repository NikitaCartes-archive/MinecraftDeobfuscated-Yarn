package net.minecraft.server;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.network.DemoServerPlayerInteractionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagManager;
import net.minecraft.test.TestManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.Unit;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.TickTimeTracker;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.CatSpawner;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.PillagerSpawner;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer extends ReentrantThreadExecutor<ServerTask> implements SnooperListener, CommandOutput, AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final File USER_CACHE_FILE = new File("usercache.json");
	public static final LevelInfo DEMO_LEVEL_INFO = new LevelInfo(
		"Demo World", GameMode.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), DataPackSettings.SAFE_MODE
	);
	protected final LevelStorage.Session session;
	protected final WorldSaveHandler saveHandler;
	private final Snooper snooper = new Snooper("server", this, Util.getMeasuringTimeMs());
	private final List<Runnable> serverGuiTickables = Lists.<Runnable>newArrayList();
	private final TickTimeTracker tickTimeTracker = new TickTimeTracker(Util.nanoTimeSupplier, this::getTicks);
	private Profiler profiler = DummyProfiler.INSTANCE;
	private final ServerNetworkIo networkIo;
	private final WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;
	private final ServerMetadata metadata = new ServerMetadata();
	private final Random random = new Random();
	private final DataFixer dataFixer;
	private String serverIp;
	private int serverPort = -1;
	protected final DynamicRegistryManager.Impl registryManager;
	private final Map<RegistryKey<World>, ServerWorld> worlds = Maps.<RegistryKey<World>, ServerWorld>newLinkedHashMap();
	private PlayerManager playerManager;
	private volatile boolean running = true;
	private boolean stopped;
	private int ticks;
	protected final Proxy proxy;
	private boolean onlineMode;
	private boolean preventProxyConnections;
	private boolean pvpEnabled;
	private boolean flightEnabled;
	@Nullable
	private String motd;
	private int playerIdleTimeout;
	public final long[] lastTickLengths = new long[100];
	@Nullable
	private KeyPair keyPair;
	@Nullable
	private String userName;
	private boolean demo;
	private String resourcePackUrl = "";
	private String resourcePackHash = "";
	private volatile boolean loading;
	private long lastTimeReference;
	private boolean profilerStartQueued;
	private final MinecraftSessionService sessionService;
	private final GameProfileRepository gameProfileRepo;
	private final UserCache userCache;
	private long lastPlayerSampleUpdate;
	private final Thread serverThread;
	private long timeReference = Util.getMeasuringTimeMs();
	private long nextTickTimestamp;
	private boolean waitingForNextTick;
	@Environment(EnvType.CLIENT)
	private boolean iconFilePresent;
	private final ResourcePackManager dataPackManager;
	private final ServerScoreboard scoreboard = new ServerScoreboard(this);
	@Nullable
	private DataCommandStorage dataCommandStorage;
	private final BossBarManager bossBarManager = new BossBarManager();
	private final CommandFunctionManager commandFunctionManager;
	private final MetricsData metricsData = new MetricsData();
	private boolean enforceWhitelist;
	private float tickTime;
	private final Executor workerExecutor;
	@Nullable
	private String serverId;
	private ServerResourceManager serverResourceManager;
	private final StructureManager structureManager;
	protected final SaveProperties saveProperties;

	public static <S extends MinecraftServer> S startServer(Function<Thread, S> serverFactory) {
		AtomicReference<S> atomicReference = new AtomicReference();
		Thread thread = new Thread(() -> ((MinecraftServer)atomicReference.get()).runServer(), "Server thread");
		thread.setUncaughtExceptionHandler((threadx, throwable) -> LOGGER.error(throwable));
		S minecraftServer = (S)serverFactory.apply(thread);
		atomicReference.set(minecraftServer);
		thread.start();
		return minecraftServer;
	}

	public MinecraftServer(
		Thread serverThread,
		DynamicRegistryManager.Impl registryManager,
		LevelStorage.Session session,
		SaveProperties saveProperties,
		ResourcePackManager dataPackManager,
		Proxy proxy,
		DataFixer dataFixer,
		ServerResourceManager serverResourceManager,
		MinecraftSessionService sessionService,
		GameProfileRepository gameProfileRepo,
		UserCache userCache,
		WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory
	) {
		super("Server");
		this.registryManager = registryManager;
		this.saveProperties = saveProperties;
		this.proxy = proxy;
		this.dataPackManager = dataPackManager;
		this.serverResourceManager = serverResourceManager;
		this.sessionService = sessionService;
		this.gameProfileRepo = gameProfileRepo;
		this.userCache = userCache;
		this.networkIo = new ServerNetworkIo(this);
		this.worldGenerationProgressListenerFactory = worldGenerationProgressListenerFactory;
		this.session = session;
		this.saveHandler = session.createSaveHandler();
		this.dataFixer = dataFixer;
		this.commandFunctionManager = new CommandFunctionManager(this, serverResourceManager.getFunctionLoader());
		this.structureManager = new StructureManager(serverResourceManager.getResourceManager(), session, dataFixer);
		this.serverThread = serverThread;
		this.workerExecutor = Util.getMainWorkerExecutor();
	}

	private void initScoreboard(PersistentStateManager persistentStateManager) {
		persistentStateManager.getOrCreate(this.getScoreboard()::stateFromNbt, this.getScoreboard()::createState, "scoreboard");
	}

	protected abstract boolean setupServer() throws IOException;

	public static void convertLevel(LevelStorage.Session session) {
		if (session.needsConversion()) {
			LOGGER.info("Converting map!");
			session.convert(new ProgressListener() {
				private long lastProgressUpdate = Util.getMeasuringTimeMs();

				@Override
				public void method_15412(Text text) {
				}

				@Environment(EnvType.CLIENT)
				@Override
				public void method_15413(Text text) {
				}

				@Override
				public void progressStagePercentage(int i) {
					if (Util.getMeasuringTimeMs() - this.lastProgressUpdate >= 1000L) {
						this.lastProgressUpdate = Util.getMeasuringTimeMs();
						MinecraftServer.LOGGER.info("Converting... {}%", i);
					}
				}

				@Environment(EnvType.CLIENT)
				@Override
				public void setDone() {
				}

				@Override
				public void method_15414(Text text) {
				}
			});
		}
	}

	protected void loadWorld() {
		this.loadWorldResourcePack();
		this.saveProperties.addServerBrand(this.getServerModName(), this.getModdedStatusMessage().isPresent());
		WorldGenerationProgressListener worldGenerationProgressListener = this.worldGenerationProgressListenerFactory.create(11);
		this.createWorlds(worldGenerationProgressListener);
		this.updateDifficulty();
		this.prepareStartRegion(worldGenerationProgressListener);
	}

	protected void updateDifficulty() {
	}

	protected void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener) {
		ServerWorldProperties serverWorldProperties = this.saveProperties.getMainWorldProperties();
		GeneratorOptions generatorOptions = this.saveProperties.getGeneratorOptions();
		boolean bl = generatorOptions.isDebugWorld();
		long l = generatorOptions.getSeed();
		long m = BiomeAccess.hashSeed(l);
		List<Spawner> list = ImmutableList.of(
			new PhantomSpawner(), new PillagerSpawner(), new CatSpawner(), new ZombieSiegeManager(), new WanderingTraderManager(serverWorldProperties)
		);
		SimpleRegistry<DimensionOptions> simpleRegistry = generatorOptions.getDimensions();
		DimensionOptions dimensionOptions = simpleRegistry.get(DimensionOptions.OVERWORLD);
		ChunkGenerator chunkGenerator;
		DimensionType dimensionType;
		if (dimensionOptions == null) {
			dimensionType = this.registryManager.get(Registry.DIMENSION_TYPE_KEY).getOrThrow(DimensionType.OVERWORLD_REGISTRY_KEY);
			chunkGenerator = GeneratorOptions.createOverworldGenerator(
				this.registryManager.get(Registry.BIOME_KEY), this.registryManager.get(Registry.NOISE_SETTINGS_WORLDGEN), new Random().nextLong()
			);
		} else {
			dimensionType = dimensionOptions.getDimensionType();
			chunkGenerator = dimensionOptions.getChunkGenerator();
		}

		ServerWorld serverWorld = new ServerWorld(
			this,
			this.workerExecutor,
			this.session,
			serverWorldProperties,
			World.OVERWORLD,
			dimensionType,
			worldGenerationProgressListener,
			chunkGenerator,
			bl,
			m,
			list,
			true
		);
		this.worlds.put(World.OVERWORLD, serverWorld);
		PersistentStateManager persistentStateManager = serverWorld.getPersistentStateManager();
		this.initScoreboard(persistentStateManager);
		this.dataCommandStorage = new DataCommandStorage(persistentStateManager);
		WorldBorder worldBorder = serverWorld.getWorldBorder();
		worldBorder.load(serverWorldProperties.getWorldBorder());
		if (!serverWorldProperties.isInitialized()) {
			try {
				setupSpawn(serverWorld, serverWorldProperties, generatorOptions.hasBonusChest(), bl);
				serverWorldProperties.setInitialized(true);
				if (bl) {
					this.setToDebugWorldProperties(this.saveProperties);
				}
			} catch (Throwable var26) {
				CrashReport crashReport = CrashReport.create(var26, "Exception initializing level");

				try {
					serverWorld.addDetailsToCrashReport(crashReport);
				} catch (Throwable var25) {
				}

				throw new CrashException(crashReport);
			}

			serverWorldProperties.setInitialized(true);
		}

		this.getPlayerManager().setMainWorld(serverWorld);
		if (this.saveProperties.getCustomBossEvents() != null) {
			this.getBossBarManager().readNbt(this.saveProperties.getCustomBossEvents());
		}

		for (Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : simpleRegistry.getEntries()) {
			RegistryKey<DimensionOptions> registryKey = (RegistryKey<DimensionOptions>)entry.getKey();
			if (registryKey != DimensionOptions.OVERWORLD) {
				RegistryKey<World> registryKey2 = RegistryKey.of(Registry.DIMENSION, registryKey.getValue());
				DimensionType dimensionType2 = ((DimensionOptions)entry.getValue()).getDimensionType();
				ChunkGenerator chunkGenerator2 = ((DimensionOptions)entry.getValue()).getChunkGenerator();
				UnmodifiableLevelProperties unmodifiableLevelProperties = new UnmodifiableLevelProperties(this.saveProperties, serverWorldProperties);
				ServerWorld serverWorld2 = new ServerWorld(
					this,
					this.workerExecutor,
					this.session,
					unmodifiableLevelProperties,
					registryKey2,
					dimensionType2,
					worldGenerationProgressListener,
					chunkGenerator2,
					bl,
					m,
					ImmutableList.of(),
					false
				);
				worldBorder.addListener(new WorldBorderListener.WorldBorderSyncer(serverWorld2.getWorldBorder()));
				this.worlds.put(registryKey2, serverWorld2);
			}
		}
	}

	private static void setupSpawn(ServerWorld world, ServerWorldProperties worldProperties, boolean bonusChest, boolean debugWorld) {
		if (debugWorld) {
			worldProperties.setSpawnPos(BlockPos.ORIGIN.up(80), 0.0F);
		} else {
			ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
			BiomeSource biomeSource = chunkGenerator.getBiomeSource();
			Random random = new Random(world.getSeed());
			BlockPos blockPos = biomeSource.locateBiome(0, world.getSeaLevel(), 0, 256, biome -> biome.getSpawnSettings().isPlayerSpawnFriendly(), random);
			ChunkPos chunkPos = blockPos == null ? new ChunkPos(0, 0) : new ChunkPos(blockPos);
			if (blockPos == null) {
				LOGGER.warn("Unable to find spawn biome");
			}

			boolean bl = false;

			for (Block block : BlockTags.VALID_SPAWN.values()) {
				if (biomeSource.getTopMaterials().contains(block.getDefaultState())) {
					bl = true;
					break;
				}
			}

			int i = chunkGenerator.getSpawnHeight();
			if (i < world.getBottomY()) {
				BlockPos blockPos2 = chunkPos.getStartPos();
				i = world.getTopY(Heightmap.Type.WORLD_SURFACE, blockPos2.getX() + 8, blockPos2.getZ() + 8);
			}

			worldProperties.setSpawnPos(chunkPos.getStartPos().add(8, i, 8), 0.0F);
			int j = 0;
			int k = 0;
			int l = 0;
			int m = -1;
			int n = 32;

			for (int o = 0; o < 1024; o++) {
				if (j > -16 && j <= 16 && k > -16 && k <= 16) {
					BlockPos blockPos3 = SpawnLocating.findServerSpawnPoint(world, new ChunkPos(chunkPos.x + j, chunkPos.z + k), bl);
					if (blockPos3 != null) {
						worldProperties.setSpawnPos(blockPos3, 0.0F);
						break;
					}
				}

				if (j == k || j < 0 && j == -k || j > 0 && j == 1 - k) {
					int p = l;
					l = -m;
					m = p;
				}

				j += l;
				k += m;
			}

			if (bonusChest) {
				ConfiguredFeature<?, ?> configuredFeature = ConfiguredFeatures.BONUS_CHEST;
				configuredFeature.generate(
					world, chunkGenerator, world.random, new BlockPos(worldProperties.getSpawnX(), worldProperties.getSpawnY(), worldProperties.getSpawnZ())
				);
			}
		}
	}

	private void setToDebugWorldProperties(SaveProperties properties) {
		properties.setDifficulty(Difficulty.PEACEFUL);
		properties.setDifficultyLocked(true);
		ServerWorldProperties serverWorldProperties = properties.getMainWorldProperties();
		serverWorldProperties.setRaining(false);
		serverWorldProperties.setThundering(false);
		serverWorldProperties.setClearWeatherTime(1000000000);
		serverWorldProperties.setTimeOfDay(6000L);
		serverWorldProperties.setGameMode(GameMode.SPECTATOR);
	}

	private void prepareStartRegion(WorldGenerationProgressListener worldGenerationProgressListener) {
		ServerWorld serverWorld = this.getOverworld();
		LOGGER.info("Preparing start region for dimension {}", serverWorld.getRegistryKey().getValue());
		BlockPos blockPos = serverWorld.getSpawnPos();
		worldGenerationProgressListener.start(new ChunkPos(blockPos));
		ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
		serverChunkManager.getLightingProvider().setTaskBatchSize(500);
		this.timeReference = Util.getMeasuringTimeMs();
		serverChunkManager.addTicket(ChunkTicketType.START, new ChunkPos(blockPos), 11, Unit.INSTANCE);

		while (serverChunkManager.getTotalChunksLoadedCount() != 441) {
			this.timeReference = Util.getMeasuringTimeMs() + 10L;
			this.method_16208();
		}

		this.timeReference = Util.getMeasuringTimeMs() + 10L;
		this.method_16208();

		for (ServerWorld serverWorld2 : this.worlds.values()) {
			ForcedChunkState forcedChunkState = serverWorld2.getPersistentStateManager().get(ForcedChunkState::fromNbt, "chunks");
			if (forcedChunkState != null) {
				LongIterator longIterator = forcedChunkState.getChunks().iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					ChunkPos chunkPos = new ChunkPos(l);
					serverWorld2.getChunkManager().setChunkForced(chunkPos, true);
				}
			}
		}

		this.timeReference = Util.getMeasuringTimeMs() + 10L;
		this.method_16208();
		worldGenerationProgressListener.stop();
		serverChunkManager.getLightingProvider().setTaskBatchSize(5);
		this.updateMobSpawnOptions();
	}

	protected void loadWorldResourcePack() {
		File file = this.session.getDirectory(WorldSavePath.RESOURCES_ZIP).toFile();
		if (file.isFile()) {
			String string = this.session.getDirectoryName();

			try {
				this.setResourcePack("level://" + URLEncoder.encode(string, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
			} catch (UnsupportedEncodingException var4) {
				LOGGER.warn("Something went wrong url encoding {}", string);
			}
		}
	}

	public GameMode getDefaultGameMode() {
		return this.saveProperties.getGameMode();
	}

	public boolean isHardcore() {
		return this.saveProperties.isHardcore();
	}

	public abstract int getOpPermissionLevel();

	public abstract int getFunctionPermissionLevel();

	public abstract boolean shouldBroadcastRconToOps();

	/**
	 * Saves the server to the data storage device.
	 * 
	 * To store the player data in addition to server data, call {@link PlayerManager#saveAllPlayerData()}.
	 * 
	 * @return whether saving was successful
	 * 
	 * @param flush if it should immediately write all data to storage device
	 * @param force when set to true, all the {@link ServerWorld}s will be saved even if {@link ServerWorld#savingDisabled} is set to true
	 */
	public boolean save(boolean suppressLogs, boolean flush, boolean force) {
		boolean bl = false;

		for (ServerWorld serverWorld : this.getWorlds()) {
			if (!suppressLogs) {
				LOGGER.info("Saving chunks for level '{}'/{}", serverWorld, serverWorld.getRegistryKey().getValue());
			}

			serverWorld.save(null, flush, serverWorld.savingDisabled && !force);
			bl = true;
		}

		ServerWorld serverWorld2 = this.getOverworld();
		ServerWorldProperties serverWorldProperties = this.saveProperties.getMainWorldProperties();
		serverWorldProperties.setWorldBorder(serverWorld2.getWorldBorder().write());
		this.saveProperties.setCustomBossEvents(this.getBossBarManager().toNbt());
		this.session.backupLevelDataFile(this.registryManager, this.saveProperties, this.getPlayerManager().getUserData());
		return bl;
	}

	@Override
	public void close() {
		this.shutdown();
	}

	protected void shutdown() {
		LOGGER.info("Stopping server");
		if (this.getNetworkIo() != null) {
			this.getNetworkIo().stop();
		}

		if (this.playerManager != null) {
			LOGGER.info("Saving players");
			this.playerManager.saveAllPlayerData();
			this.playerManager.disconnectAllPlayers();
		}

		LOGGER.info("Saving worlds");

		for (ServerWorld serverWorld : this.getWorlds()) {
			if (serverWorld != null) {
				serverWorld.savingDisabled = false;
			}
		}

		this.save(false, true, false);

		for (ServerWorld serverWorldx : this.getWorlds()) {
			if (serverWorldx != null) {
				try {
					serverWorldx.close();
				} catch (IOException var5) {
					LOGGER.error("Exception closing the level", (Throwable)var5);
				}
			}
		}

		if (this.snooper.isActive()) {
			this.snooper.cancel();
		}

		this.serverResourceManager.close();

		try {
			this.session.close();
		} catch (IOException var4) {
			LOGGER.error("Failed to unlock level {}", this.session.getDirectoryName(), var4);
		}
	}

	public String getServerIp() {
		return this.serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public boolean isRunning() {
		return this.running;
	}

	public void stop(boolean bl) {
		this.running = false;
		if (bl) {
			try {
				this.serverThread.join();
			} catch (InterruptedException var3) {
				LOGGER.error("Error while shutting down", (Throwable)var3);
			}
		}
	}

	protected void runServer() {
		try {
			if (this.setupServer()) {
				this.timeReference = Util.getMeasuringTimeMs();
				this.metadata.setDescription(new LiteralText(this.motd));
				this.metadata.setVersion(new ServerMetadata.Version(SharedConstants.getGameVersion().getName(), SharedConstants.getGameVersion().getProtocolVersion()));
				this.setFavicon(this.metadata);

				while (this.running) {
					long l = Util.getMeasuringTimeMs() - this.timeReference;
					if (l > 2000L && this.timeReference - this.lastTimeReference >= 15000L) {
						long m = l / 50L;
						LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", l, m);
						this.timeReference += m * 50L;
						this.lastTimeReference = this.timeReference;
					}

					this.timeReference += 50L;
					TickDurationMonitor tickDurationMonitor = TickDurationMonitor.create("Server");
					this.startMonitor(tickDurationMonitor);
					this.profiler.startTick();
					this.profiler.push("tick");
					this.tick(this::shouldKeepTicking);
					this.profiler.swap("nextTickWait");
					this.waitingForNextTick = true;
					this.nextTickTimestamp = Math.max(Util.getMeasuringTimeMs() + 50L, this.timeReference);
					this.method_16208();
					this.profiler.pop();
					this.profiler.endTick();
					this.endMonitor(tickDurationMonitor);
					this.loading = true;
				}
			} else {
				this.setCrashReport(null);
			}
		} catch (Throwable var44) {
			LOGGER.error("Encountered an unexpected exception", var44);
			CrashReport crashReport;
			if (var44 instanceof CrashException) {
				crashReport = this.populateCrashReport(((CrashException)var44).getReport());
			} else {
				crashReport = this.populateCrashReport(new CrashReport("Exception in server tick loop", var44));
			}

			File file = new File(
				new File(this.getRunDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt"
			);
			if (crashReport.writeToFile(file)) {
				LOGGER.error("This crash report has been saved to: {}", file.getAbsolutePath());
			} else {
				LOGGER.error("We were unable to save this crash report to disk.");
			}

			this.setCrashReport(crashReport);
		} finally {
			try {
				this.stopped = true;
				this.shutdown();
			} catch (Throwable var42) {
				LOGGER.error("Exception stopping the server", var42);
			} finally {
				this.exit();
			}
		}
	}

	private boolean shouldKeepTicking() {
		return this.hasRunningTasks() || Util.getMeasuringTimeMs() < (this.waitingForNextTick ? this.nextTickTimestamp : this.timeReference);
	}

	protected void method_16208() {
		this.runTasks();
		this.runTasks(() -> !this.shouldKeepTicking());
	}

	protected ServerTask createTask(Runnable runnable) {
		return new ServerTask(this.ticks, runnable);
	}

	protected boolean canExecute(ServerTask serverTask) {
		return serverTask.getCreationTicks() + 3 < this.ticks || this.shouldKeepTicking();
	}

	@Override
	public boolean runTask() {
		boolean bl = this.method_20415();
		this.waitingForNextTick = bl;
		return bl;
	}

	private boolean method_20415() {
		if (super.runTask()) {
			return true;
		} else {
			if (this.shouldKeepTicking()) {
				for (ServerWorld serverWorld : this.getWorlds()) {
					if (serverWorld.getChunkManager().executeQueuedTasks()) {
						return true;
					}
				}
			}

			return false;
		}
	}

	protected void executeTask(ServerTask serverTask) {
		this.getProfiler().visit("runTask");
		super.executeTask(serverTask);
	}

	private void setFavicon(ServerMetadata metadata) {
		File file = this.getFile("server-icon.png");
		if (!file.exists()) {
			file = this.session.getIconFile();
		}

		if (file.isFile()) {
			ByteBuf byteBuf = Unpooled.buffer();

			try {
				BufferedImage bufferedImage = ImageIO.read(file);
				Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide");
				Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high");
				ImageIO.write(bufferedImage, "PNG", new ByteBufOutputStream(byteBuf));
				ByteBuffer byteBuffer = Base64.getEncoder().encode(byteBuf.nioBuffer());
				metadata.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(byteBuffer));
			} catch (Exception var9) {
				LOGGER.error("Couldn't load server icon", (Throwable)var9);
			} finally {
				byteBuf.release();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean hasIconFile() {
		this.iconFilePresent = this.iconFilePresent || this.getIconFile().isFile();
		return this.iconFilePresent;
	}

	@Environment(EnvType.CLIENT)
	public File getIconFile() {
		return this.session.getIconFile();
	}

	public File getRunDirectory() {
		return new File(".");
	}

	protected void setCrashReport(CrashReport report) {
	}

	protected void exit() {
	}

	protected void tick(BooleanSupplier shouldKeepTicking) {
		long l = Util.getMeasuringTimeNano();
		this.ticks++;
		this.tickWorlds(shouldKeepTicking);
		if (l - this.lastPlayerSampleUpdate >= 5000000000L) {
			this.lastPlayerSampleUpdate = l;
			this.metadata.setPlayers(new ServerMetadata.Players(this.getMaxPlayerCount(), this.getCurrentPlayerCount()));
			GameProfile[] gameProfiles = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
			int i = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - gameProfiles.length);

			for (int j = 0; j < gameProfiles.length; j++) {
				gameProfiles[j] = ((ServerPlayerEntity)this.playerManager.getPlayerList().get(i + j)).getGameProfile();
			}

			Collections.shuffle(Arrays.asList(gameProfiles));
			this.metadata.getPlayers().setSample(gameProfiles);
		}

		if (this.ticks % 6000 == 0) {
			LOGGER.debug("Autosave started");
			this.profiler.push("save");
			this.playerManager.saveAllPlayerData();
			this.save(true, false, false);
			this.profiler.pop();
			LOGGER.debug("Autosave finished");
		}

		this.profiler.push("snooper");
		if (!this.snooper.isActive() && this.ticks > 100) {
			this.snooper.method_5482();
		}

		if (this.ticks % 6000 == 0) {
			this.snooper.update();
		}

		this.profiler.pop();
		this.profiler.push("tallying");
		long m = this.lastTickLengths[this.ticks % 100] = Util.getMeasuringTimeNano() - l;
		this.tickTime = this.tickTime * 0.8F + (float)m / 1000000.0F * 0.19999999F;
		long n = Util.getMeasuringTimeNano();
		this.metricsData.pushSample(n - l);
		this.profiler.pop();
	}

	protected void tickWorlds(BooleanSupplier shouldKeepTicking) {
		this.profiler.push("commandFunctions");
		this.getCommandFunctionManager().tick();
		this.profiler.swap("levels");

		for (ServerWorld serverWorld : this.getWorlds()) {
			this.profiler.push((Supplier<String>)(() -> serverWorld + " " + serverWorld.getRegistryKey().getValue()));
			if (this.ticks % 20 == 0) {
				this.profiler.push("timeSync");
				this.playerManager
					.sendToDimension(
						new WorldTimeUpdateS2CPacket(serverWorld.getTime(), serverWorld.getTimeOfDay(), serverWorld.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)),
						serverWorld.getRegistryKey()
					);
				this.profiler.pop();
			}

			this.profiler.push("tick");

			try {
				serverWorld.tick(shouldKeepTicking);
			} catch (Throwable var6) {
				CrashReport crashReport = CrashReport.create(var6, "Exception ticking world");
				serverWorld.addDetailsToCrashReport(crashReport);
				throw new CrashException(crashReport);
			}

			this.profiler.pop();
			this.profiler.pop();
		}

		this.profiler.swap("connection");
		this.getNetworkIo().tick();
		this.profiler.swap("players");
		this.playerManager.updatePlayerLatency();
		if (SharedConstants.isDevelopment) {
			TestManager.INSTANCE.tick();
		}

		this.profiler.swap("server gui refresh");

		for (int i = 0; i < this.serverGuiTickables.size(); i++) {
			((Runnable)this.serverGuiTickables.get(i)).run();
		}

		this.profiler.pop();
	}

	public boolean isNetherAllowed() {
		return true;
	}

	public void addServerGuiTickable(Runnable tickable) {
		this.serverGuiTickables.add(tickable);
	}

	protected void setServerId(String serverId) {
		this.serverId = serverId;
	}

	@Environment(EnvType.CLIENT)
	public boolean isStopping() {
		return !this.serverThread.isAlive();
	}

	/**
	 * @param path relative path from the run directory
	 */
	public File getFile(String path) {
		return new File(this.getRunDirectory(), path);
	}

	public final ServerWorld getOverworld() {
		return (ServerWorld)this.worlds.get(World.OVERWORLD);
	}

	@Nullable
	public ServerWorld getWorld(RegistryKey<World> key) {
		return (ServerWorld)this.worlds.get(key);
	}

	public Set<RegistryKey<World>> getWorldRegistryKeys() {
		return this.worlds.keySet();
	}

	public Iterable<ServerWorld> getWorlds() {
		return this.worlds.values();
	}

	public String getVersion() {
		return SharedConstants.getGameVersion().getName();
	}

	public int getCurrentPlayerCount() {
		return this.playerManager.getCurrentPlayerCount();
	}

	public int getMaxPlayerCount() {
		return this.playerManager.getMaxPlayerCount();
	}

	public String[] getPlayerNames() {
		return this.playerManager.getPlayerNames();
	}

	public String getServerModName() {
		return "vanilla";
	}

	public CrashReport populateCrashReport(CrashReport report) {
		if (this.playerManager != null) {
			report.getSystemDetailsSection()
				.add(
					"Player Count",
					(CrashCallable<String>)(() -> this.playerManager.getCurrentPlayerCount()
							+ " / "
							+ this.playerManager.getMaxPlayerCount()
							+ "; "
							+ this.playerManager.getPlayerList())
				);
		}

		report.getSystemDetailsSection().add("Data Packs", (CrashCallable<String>)(() -> {
			StringBuilder stringBuilder = new StringBuilder();

			for (ResourcePackProfile resourcePackProfile : this.dataPackManager.getEnabledProfiles()) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(", ");
				}

				stringBuilder.append(resourcePackProfile.getName());
				if (!resourcePackProfile.getCompatibility().isCompatible()) {
					stringBuilder.append(" (incompatible)");
				}
			}

			return stringBuilder.toString();
		}));
		if (this.serverId != null) {
			report.getSystemDetailsSection().add("Server Id", (CrashCallable<String>)(() -> this.serverId));
		}

		return report;
	}

	public abstract Optional<String> getModdedStatusMessage();

	@Override
	public void sendSystemMessage(Text message, UUID senderUuid) {
		LOGGER.info(message.getString());
	}

	public KeyPair getKeyPair() {
		return this.keyPair;
	}

	public int getServerPort() {
		return this.serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setServerName(String serverName) {
		this.userName = serverName;
	}

	public boolean isSinglePlayer() {
		return this.userName != null;
	}

	protected void generateKeyPair() {
		LOGGER.info("Generating keypair");

		try {
			this.keyPair = NetworkEncryptionUtils.generateServerKeyPair();
		} catch (NetworkEncryptionException var2) {
			throw new IllegalStateException("Failed to generate key pair", var2);
		}
	}

	public void setDifficulty(Difficulty difficulty, boolean forceUpdate) {
		if (forceUpdate || !this.saveProperties.isDifficultyLocked()) {
			this.saveProperties.setDifficulty(this.saveProperties.isHardcore() ? Difficulty.HARD : difficulty);
			this.updateMobSpawnOptions();
			this.getPlayerManager().getPlayerList().forEach(this::sendDifficulty);
		}
	}

	public int adjustTrackingDistance(int initialDistance) {
		return initialDistance;
	}

	private void updateMobSpawnOptions() {
		for (ServerWorld serverWorld : this.getWorlds()) {
			serverWorld.setMobSpawnOptions(this.isMonsterSpawningEnabled(), this.shouldSpawnAnimals());
		}
	}

	public void setDifficultyLocked(boolean locked) {
		this.saveProperties.setDifficultyLocked(locked);
		this.getPlayerManager().getPlayerList().forEach(this::sendDifficulty);
	}

	private void sendDifficulty(ServerPlayerEntity player) {
		WorldProperties worldProperties = player.getServerWorld().getLevelProperties();
		player.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
	}

	protected boolean isMonsterSpawningEnabled() {
		return this.saveProperties.getDifficulty() != Difficulty.PEACEFUL;
	}

	public boolean isDemo() {
		return this.demo;
	}

	public void setDemo(boolean demo) {
		this.demo = demo;
	}

	public String getResourcePackUrl() {
		return this.resourcePackUrl;
	}

	public String getResourcePackHash() {
		return this.resourcePackHash;
	}

	public void setResourcePack(String url, String hash) {
		this.resourcePackUrl = url;
		this.resourcePackHash = hash;
	}

	@Override
	public void addSnooperInfo(Snooper snooper) {
		snooper.addInfo("whitelist_enabled", false);
		snooper.addInfo("whitelist_count", 0);
		if (this.playerManager != null) {
			snooper.addInfo("players_current", this.getCurrentPlayerCount());
			snooper.addInfo("players_max", this.getMaxPlayerCount());
			snooper.addInfo("players_seen", this.saveHandler.getSavedPlayerIds().length);
		}

		snooper.addInfo("uses_auth", this.onlineMode);
		snooper.addInfo("gui_state", this.hasGui() ? "enabled" : "disabled");
		snooper.addInfo("run_time", (Util.getMeasuringTimeMs() - snooper.getStartTime()) / 60L * 1000L);
		snooper.addInfo("avg_tick_ms", (int)(MathHelper.average(this.lastTickLengths) * 1.0E-6));
		int i = 0;

		for (ServerWorld serverWorld : this.getWorlds()) {
			if (serverWorld != null) {
				snooper.addInfo("world[" + i + "][dimension]", serverWorld.getRegistryKey().getValue());
				snooper.addInfo("world[" + i + "][mode]", this.saveProperties.getGameMode());
				snooper.addInfo("world[" + i + "][difficulty]", serverWorld.getDifficulty());
				snooper.addInfo("world[" + i + "][hardcore]", this.saveProperties.isHardcore());
				snooper.addInfo("world[" + i + "][height]", serverWorld.getTopY());
				snooper.addInfo("world[" + i + "][chunks_loaded]", serverWorld.getChunkManager().getLoadedChunkCount());
				i++;
			}
		}

		snooper.addInfo("worlds", i);
	}

	public abstract boolean isDedicated();

	public abstract int getRateLimit();

	public boolean isOnlineMode() {
		return this.onlineMode;
	}

	public void setOnlineMode(boolean onlineMode) {
		this.onlineMode = onlineMode;
	}

	public boolean shouldPreventProxyConnections() {
		return this.preventProxyConnections;
	}

	public void setPreventProxyConnections(boolean preventProxyConnections) {
		this.preventProxyConnections = preventProxyConnections;
	}

	public boolean shouldSpawnAnimals() {
		return true;
	}

	public boolean shouldSpawnNpcs() {
		return true;
	}

	public abstract boolean isUsingNativeTransport();

	public boolean isPvpEnabled() {
		return this.pvpEnabled;
	}

	public void setPvpEnabled(boolean pvpEnabled) {
		this.pvpEnabled = pvpEnabled;
	}

	public boolean isFlightEnabled() {
		return this.flightEnabled;
	}

	public void setFlightEnabled(boolean flightEnabled) {
		this.flightEnabled = flightEnabled;
	}

	/**
	 * Specifies whether command blocks can execute commands on the server.
	 */
	public abstract boolean areCommandBlocksEnabled();

	public String getServerMotd() {
		return this.motd;
	}

	public void setMotd(String motd) {
		this.motd = motd;
	}

	public boolean isStopped() {
		return this.stopped;
	}

	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}

	public void setPlayerManager(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	public abstract boolean isRemote();

	public void setDefaultGameMode(GameMode gameMode) {
		this.saveProperties.setGameMode(gameMode);
	}

	@Nullable
	public ServerNetworkIo getNetworkIo() {
		return this.networkIo;
	}

	@Environment(EnvType.CLIENT)
	public boolean isLoading() {
		return this.loading;
	}

	public boolean hasGui() {
		return false;
	}

	/**
	 * Opens a server for LAN connections.
	 * This is only supported on an integrated server, a dedicated server will always fail to open to LAN.
	 * 
	 * @return whether the server was successfully opened to LAN
	 * 
	 * @param gameMode the game mode connecting players will have set by default
	 * @param cheatsAllowed whether players on the server have operator permissions
	 * @param port the port to open up to LAN connections
	 */
	public boolean openToLan(@Nullable GameMode gameMode, boolean cheatsAllowed, int port) {
		return false;
	}

	/**
	 * Gets the amount of ticks the server has been running for.
	 */
	public int getTicks() {
		return this.ticks;
	}

	@Environment(EnvType.CLIENT)
	public Snooper getSnooper() {
		return this.snooper;
	}

	public int getSpawnProtectionRadius() {
		return 16;
	}

	public boolean isSpawnProtected(ServerWorld world, BlockPos pos, PlayerEntity player) {
		return false;
	}

	public boolean acceptsStatusQuery() {
		return true;
	}

	public int getPlayerIdleTimeout() {
		return this.playerIdleTimeout;
	}

	public void setPlayerIdleTimeout(int playerIdleTimeout) {
		this.playerIdleTimeout = playerIdleTimeout;
	}

	public MinecraftSessionService getSessionService() {
		return this.sessionService;
	}

	public GameProfileRepository getGameProfileRepo() {
		return this.gameProfileRepo;
	}

	public UserCache getUserCache() {
		return this.userCache;
	}

	public ServerMetadata getServerMetadata() {
		return this.metadata;
	}

	public void forcePlayerSampleUpdate() {
		this.lastPlayerSampleUpdate = 0L;
	}

	public int getMaxWorldBorderRadius() {
		return 29999984;
	}

	@Override
	public boolean shouldExecuteAsync() {
		return super.shouldExecuteAsync() && !this.isStopped();
	}

	@Override
	public Thread getThread() {
		return this.serverThread;
	}

	public int getNetworkCompressionThreshold() {
		return 256;
	}

	public long getServerStartTime() {
		return this.timeReference;
	}

	public DataFixer getDataFixer() {
		return this.dataFixer;
	}

	public int getSpawnRadius(@Nullable ServerWorld world) {
		return world != null ? world.getGameRules().getInt(GameRules.SPAWN_RADIUS) : 10;
	}

	public ServerAdvancementLoader getAdvancementLoader() {
		return this.serverResourceManager.getServerAdvancementLoader();
	}

	public CommandFunctionManager getCommandFunctionManager() {
		return this.commandFunctionManager;
	}

	/**
	 * Reloads this server's datapacks.
	 * 
	 * @return a completable future which specifies whether the reload was successful
	 * A reload has failed when the future is exceptionally completed.
	 * @see CompletableFuture
	 * 
	 * @param datapacks a collection of datapacks to reload with
	 */
	public CompletableFuture<Void> reloadResources(Collection<String> datapacks) {
		CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(
				() -> (ImmutableList)datapacks.stream()
						.map(this.dataPackManager::getProfile)
						.filter(Objects::nonNull)
						.map(ResourcePackProfile::createResourcePack)
						.collect(ImmutableList.toImmutableList()),
				this
			)
			.thenCompose(
				immutableList -> ServerResourceManager.reload(
						immutableList,
						this.registryManager,
						this.isDedicated() ? CommandManager.RegistrationEnvironment.DEDICATED : CommandManager.RegistrationEnvironment.INTEGRATED,
						this.getFunctionPermissionLevel(),
						this.workerExecutor,
						this
					)
			)
			.thenAcceptAsync(serverResourceManager -> {
				this.serverResourceManager.close();
				this.serverResourceManager = serverResourceManager;
				this.dataPackManager.setEnabledProfiles(datapacks);
				this.saveProperties.updateLevelInfo(createDataPackSettings(this.dataPackManager));
				serverResourceManager.loadRegistryTags();
				this.getPlayerManager().saveAllPlayerData();
				this.getPlayerManager().onDataPacksReloaded();
				this.commandFunctionManager.update(this.serverResourceManager.getFunctionLoader());
				this.structureManager.setResourceManager(this.serverResourceManager.getResourceManager());
			}, this);
		if (this.isOnThread()) {
			this.runTasks(completableFuture::isDone);
		}

		return completableFuture;
	}

	public static DataPackSettings loadDataPacks(ResourcePackManager resourcePackManager, DataPackSettings dataPackSettings, boolean safeMode) {
		resourcePackManager.scanPacks();
		if (safeMode) {
			resourcePackManager.setEnabledProfiles(Collections.singleton("vanilla"));
			return new DataPackSettings(ImmutableList.of("vanilla"), ImmutableList.of());
		} else {
			Set<String> set = Sets.<String>newLinkedHashSet();

			for (String string : dataPackSettings.getEnabled()) {
				if (resourcePackManager.hasProfile(string)) {
					set.add(string);
				} else {
					LOGGER.warn("Missing data pack {}", string);
				}
			}

			for (ResourcePackProfile resourcePackProfile : resourcePackManager.getProfiles()) {
				String string2 = resourcePackProfile.getName();
				if (!dataPackSettings.getDisabled().contains(string2) && !set.contains(string2)) {
					LOGGER.info("Found new data pack {}, loading it automatically", string2);
					set.add(string2);
				}
			}

			if (set.isEmpty()) {
				LOGGER.info("No datapacks selected, forcing vanilla");
				set.add("vanilla");
			}

			resourcePackManager.setEnabledProfiles(set);
			return createDataPackSettings(resourcePackManager);
		}
	}

	private static DataPackSettings createDataPackSettings(ResourcePackManager dataPackManager) {
		Collection<String> collection = dataPackManager.getEnabledNames();
		List<String> list = ImmutableList.copyOf(collection);
		List<String> list2 = (List<String>)dataPackManager.getNames()
			.stream()
			.filter(string -> !collection.contains(string))
			.collect(ImmutableList.toImmutableList());
		return new DataPackSettings(list, list2);
	}

	public void kickNonWhitelistedPlayers(ServerCommandSource source) {
		if (this.isEnforceWhitelist()) {
			PlayerManager playerManager = source.getMinecraftServer().getPlayerManager();
			Whitelist whitelist = playerManager.getWhitelist();

			for (ServerPlayerEntity serverPlayerEntity : Lists.newArrayList(playerManager.getPlayerList())) {
				if (!whitelist.isAllowed(serverPlayerEntity.getGameProfile())) {
					serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.not_whitelisted"));
				}
			}
		}
	}

	public ResourcePackManager getDataPackManager() {
		return this.dataPackManager;
	}

	/**
	 * Gets the server's command manager.
	 * The command manager is responsible for parsing and dispatching commands.
	 */
	public CommandManager getCommandManager() {
		return this.serverResourceManager.getCommandManager();
	}

	/**
	 * Creates a command source which represents this Minecraft server instance.
	 */
	public ServerCommandSource getCommandSource() {
		ServerWorld serverWorld = this.getOverworld();
		return new ServerCommandSource(
			this, serverWorld == null ? Vec3d.ZERO : Vec3d.of(serverWorld.getSpawnPos()), Vec2f.ZERO, serverWorld, 4, "Server", new LiteralText("Server"), this, null
		);
	}

	@Override
	public boolean shouldReceiveFeedback() {
		return true;
	}

	@Override
	public boolean shouldTrackOutput() {
		return true;
	}

	public RecipeManager getRecipeManager() {
		return this.serverResourceManager.getRecipeManager();
	}

	public TagManager getTagManager() {
		return this.serverResourceManager.getRegistryTagManager();
	}

	public ServerScoreboard getScoreboard() {
		return this.scoreboard;
	}

	public DataCommandStorage getDataCommandStorage() {
		if (this.dataCommandStorage == null) {
			throw new NullPointerException("Called before server init");
		} else {
			return this.dataCommandStorage;
		}
	}

	public LootManager getLootManager() {
		return this.serverResourceManager.getLootManager();
	}

	public LootConditionManager getPredicateManager() {
		return this.serverResourceManager.getLootConditionManager();
	}

	public LootFunctionManager getItemModifierManager() {
		return this.serverResourceManager.getLootFunctionManager();
	}

	public GameRules getGameRules() {
		return this.getOverworld().getGameRules();
	}

	public BossBarManager getBossBarManager() {
		return this.bossBarManager;
	}

	public boolean isEnforceWhitelist() {
		return this.enforceWhitelist;
	}

	public void setEnforceWhitelist(boolean whitelistEnabled) {
		this.enforceWhitelist = whitelistEnabled;
	}

	public float getTickTime() {
		return this.tickTime;
	}

	public int getPermissionLevel(GameProfile profile) {
		if (this.getPlayerManager().isOperator(profile)) {
			OperatorEntry operatorEntry = this.getPlayerManager().getOpList().get(profile);
			if (operatorEntry != null) {
				return operatorEntry.getPermissionLevel();
			} else if (this.isHost(profile)) {
				return 4;
			} else if (this.isSinglePlayer()) {
				return this.getPlayerManager().areCheatsAllowed() ? 4 : 0;
			} else {
				return this.getOpPermissionLevel();
			}
		} else {
			return 0;
		}
	}

	@Environment(EnvType.CLIENT)
	public MetricsData getMetricsData() {
		return this.metricsData;
	}

	public Profiler getProfiler() {
		return this.profiler;
	}

	public abstract boolean isHost(GameProfile profile);

	public void dump(Path path) throws IOException {
		Path path2 = path.resolve("levels");

		for (Entry<RegistryKey<World>, ServerWorld> entry : this.worlds.entrySet()) {
			Identifier identifier = ((RegistryKey)entry.getKey()).getValue();
			Path path3 = path2.resolve(identifier.getNamespace()).resolve(identifier.getPath());
			Files.createDirectories(path3);
			((ServerWorld)entry.getValue()).dump(path3);
		}

		this.dumpGamerules(path.resolve("gamerules.txt"));
		this.dumpClasspath(path.resolve("classpath.txt"));
		this.dumpExampleCrash(path.resolve("example_crash.txt"));
		this.dumpStats(path.resolve("stats.txt"));
		this.dumpThreads(path.resolve("threads.txt"));
	}

	private void dumpStats(Path path) throws IOException {
		Writer writer = Files.newBufferedWriter(path);
		Throwable var3 = null;

		try {
			writer.write(String.format("pending_tasks: %d\n", this.getTaskCount()));
			writer.write(String.format("average_tick_time: %f\n", this.getTickTime()));
			writer.write(String.format("tick_times: %s\n", Arrays.toString(this.lastTickLengths)));
			writer.write(String.format("queue: %s\n", Util.getMainWorkerExecutor()));
		} catch (Throwable var12) {
			var3 = var12;
			throw var12;
		} finally {
			if (writer != null) {
				if (var3 != null) {
					try {
						writer.close();
					} catch (Throwable var11) {
						var3.addSuppressed(var11);
					}
				} else {
					writer.close();
				}
			}
		}
	}

	private void dumpExampleCrash(Path path) throws IOException {
		CrashReport crashReport = new CrashReport("Server dump", new Exception("dummy"));
		this.populateCrashReport(crashReport);
		Writer writer = Files.newBufferedWriter(path);
		Throwable var4 = null;

		try {
			writer.write(crashReport.asString());
		} catch (Throwable var13) {
			var4 = var13;
			throw var13;
		} finally {
			if (writer != null) {
				if (var4 != null) {
					try {
						writer.close();
					} catch (Throwable var12) {
						var4.addSuppressed(var12);
					}
				} else {
					writer.close();
				}
			}
		}
	}

	private void dumpGamerules(Path path) throws IOException {
		Writer writer = Files.newBufferedWriter(path);
		Throwable var3 = null;

		try {
			final List<String> list = Lists.<String>newArrayList();
			final GameRules gameRules = this.getGameRules();
			GameRules.accept(new GameRules.Visitor() {
				@Override
				public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
					list.add(String.format("%s=%s\n", key.getName(), gameRules.get(key)));
				}
			});

			for (String string : list) {
				writer.write(string);
			}
		} catch (Throwable var15) {
			var3 = var15;
			throw var15;
		} finally {
			if (writer != null) {
				if (var3 != null) {
					try {
						writer.close();
					} catch (Throwable var14) {
						var3.addSuppressed(var14);
					}
				} else {
					writer.close();
				}
			}
		}
	}

	private void dumpClasspath(Path path) throws IOException {
		Writer writer = Files.newBufferedWriter(path);
		Throwable var3 = null;

		try {
			String string = System.getProperty("java.class.path");
			String string2 = System.getProperty("path.separator");

			for (String string3 : Splitter.on(string2).split(string)) {
				writer.write(string3);
				writer.write("\n");
			}
		} catch (Throwable var15) {
			var3 = var15;
			throw var15;
		} finally {
			if (writer != null) {
				if (var3 != null) {
					try {
						writer.close();
					} catch (Throwable var14) {
						var3.addSuppressed(var14);
					}
				} else {
					writer.close();
				}
			}
		}
	}

	private void dumpThreads(Path path) throws IOException {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
		Arrays.sort(threadInfos, Comparator.comparing(ThreadInfo::getThreadName));
		Writer writer = Files.newBufferedWriter(path);
		Throwable var5 = null;

		try {
			for (ThreadInfo threadInfo : threadInfos) {
				writer.write(threadInfo.toString());
				writer.write(10);
			}
		} catch (Throwable var17) {
			var5 = var17;
			throw var17;
		} finally {
			if (writer != null) {
				if (var5 != null) {
					try {
						writer.close();
					} catch (Throwable var16) {
						var5.addSuppressed(var16);
					}
				} else {
					writer.close();
				}
			}
		}
	}

	private void startMonitor(@Nullable TickDurationMonitor monitor) {
		if (this.profilerStartQueued) {
			this.profilerStartQueued = false;
			this.tickTimeTracker.enable();
		}

		this.profiler = TickDurationMonitor.tickProfiler(this.tickTimeTracker.getProfiler(), monitor);
	}

	private void endMonitor(@Nullable TickDurationMonitor monitor) {
		if (monitor != null) {
			monitor.endTick();
		}

		this.profiler = this.tickTimeTracker.getProfiler();
	}

	public boolean isDebugRunning() {
		return this.tickTimeTracker.isActive();
	}

	public void enableProfiler() {
		this.profilerStartQueued = true;
	}

	public ProfileResult stopDebug() {
		ProfileResult profileResult = this.tickTimeTracker.getResult();
		this.tickTimeTracker.disable();
		return profileResult;
	}

	public Path getSavePath(WorldSavePath worldSavePath) {
		return this.session.getDirectory(worldSavePath);
	}

	public boolean syncChunkWrites() {
		return true;
	}

	public StructureManager getStructureManager() {
		return this.structureManager;
	}

	public SaveProperties getSaveProperties() {
		return this.saveProperties;
	}

	public DynamicRegistryManager getRegistryManager() {
		return this.registryManager;
	}

	public TextStream createFilterer(ServerPlayerEntity player) {
		return TextStream.UNFILTERED;
	}

	public boolean requireResourcePack() {
		return false;
	}

	public ServerPlayerInteractionManager getPlayerInteractionManager(ServerPlayerEntity player) {
		return (ServerPlayerInteractionManager)(this.isDemo() ? new DemoServerPlayerInteractionManager(player) : new ServerPlayerInteractionManager(player));
	}

	/**
	 * Returns the game mode a player should be set to when connecting to the server, or {@code null} if none is set.
	 */
	@Nullable
	public GameMode getForcedGameMode() {
		return null;
	}
}
