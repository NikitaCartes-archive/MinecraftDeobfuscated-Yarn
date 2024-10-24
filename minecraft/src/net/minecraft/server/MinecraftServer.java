package net.minecraft.server;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.mojang.jtracy.DiscontinuousFrame;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FuelRegistry;
import net.minecraft.network.QueryableServer;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
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
import net.minecraft.server.world.ChunkErrorHandler;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.test.TestManager;
import net.minecraft.text.Text;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Identifier;
import net.minecraft.util.ModStatus;
import net.minecraft.util.PathUtil;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.WinNativeModuleUtil;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ReportType;
import net.minecraft.util.crash.SuppressedExceptionsTracker;
import net.minecraft.util.function.Finishable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.profiler.DebugRecorder;
import net.minecraft.util.profiler.DummyRecorder;
import net.minecraft.util.profiler.EmptyProfileResult;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerTiming;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.RecordDumper;
import net.minecraft.util.profiler.Recorder;
import net.minecraft.util.profiler.ServerSamplerSource;
import net.minecraft.util.profiler.ServerTickType;
import net.minecraft.util.profiler.log.DebugSampleLog;
import net.minecraft.util.profiler.log.DebugSampleType;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PlayerSaveHandler;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.MiscConfiguredFeatures;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.spawner.SpecialSpawner;
import net.minecraft.world.storage.StorageKey;
import org.slf4j.Logger;

/**
 * Represents a logical Minecraft server.
 * 
 * <p>Since Minecraft uses a Client-Server architecture for the game, the server processes all logical game functions.
 * A few of the actions a Minecraft server will handle includes processing player actions, handling damage to entities, advancing the world time and executing commands.
 * 
 * <p>There are two primary implementations for a Minecraft server: a dedicated and an integrated server.
 * 
 * <p>A dedicated server is a Minecraft server not attached to a Minecraft game client and may be run remotely from any connected players.
 * A dedicated server has a few exclusive features such as a whitelist/blacklist, remote rcon connections, and a terminal to input commands.
 * 
 * <p>An integrated server is functionally equivalent to a dedicated server except that is hosted by a Minecraft game client and is typically used in a single player world.
 * An integrated server differs from a dedicated server by allowing connections within the local area network (LAN) and the host client.
 * Generally, you will always want to treat connection to an integrated server like you would to a dedicated server since the concept of an integrated server is an implementation detail in Minecraft.
 * 
 * @see net.minecraft.server.dedicated.MinecraftDedicatedServer
 * @see net.minecraft.server.integrated.IntegratedServer
 */
public abstract class MinecraftServer extends ReentrantThreadExecutor<ServerTask> implements QueryableServer, ChunkErrorHandler, CommandOutput {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String VANILLA = "vanilla";
	private static final float field_33212 = 0.8F;
	private static final int field_33213 = 100;
	private static final long OVERLOAD_THRESHOLD_NANOS = 20L * TimeHelper.SECOND_IN_NANOS / 20L;
	private static final int field_47144 = 20;
	private static final long OVERLOAD_WARNING_INTERVAL_NANOS = 10L * TimeHelper.SECOND_IN_NANOS;
	private static final int field_47146 = 100;
	private static final long PLAYER_SAMPLE_UPDATE_INTERVAL_NANOS = 5L * TimeHelper.SECOND_IN_NANOS;
	private static final long PREPARE_START_REGION_TICK_DELAY_NANOS = 10L * TimeHelper.MILLI_IN_NANOS;
	private static final int field_33218 = 12;
	private static final int field_48466 = 5;
	private static final int field_33220 = 6000;
	private static final int field_47149 = 100;
	private static final int field_33221 = 3;
	public static final int MAX_WORLD_BORDER_RADIUS = 29999984;
	public static final LevelInfo DEMO_LEVEL_INFO = new LevelInfo(
		"Demo World", GameMode.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(FeatureFlags.DEFAULT_ENABLED_FEATURES), DataConfiguration.SAFE_MODE
	);
	public static final GameProfile ANONYMOUS_PLAYER_PROFILE = new GameProfile(Util.NIL_UUID, "Anonymous Player");
	protected final LevelStorage.Session session;
	protected final PlayerSaveHandler saveHandler;
	private final List<Runnable> serverGuiTickables = Lists.<Runnable>newArrayList();
	private Recorder recorder = DummyRecorder.INSTANCE;
	private Consumer<ProfileResult> recorderResultConsumer = profileResult -> this.resetRecorder();
	private Consumer<Path> recorderDumpConsumer = path -> {
	};
	private boolean needsRecorderSetup;
	@Nullable
	private MinecraftServer.DebugStart debugStart;
	private boolean needsDebugSetup;
	private final ServerNetworkIo networkIo;
	private final WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;
	@Nullable
	private ServerMetadata metadata;
	@Nullable
	private ServerMetadata.Favicon favicon;
	private final Random random = Random.create();
	private final DataFixer dataFixer;
	private String serverIp;
	private int serverPort = -1;
	private final CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries;
	private final Map<RegistryKey<World>, ServerWorld> worlds = Maps.<RegistryKey<World>, ServerWorld>newLinkedHashMap();
	private PlayerManager playerManager;
	private volatile boolean running = true;
	private boolean stopped;
	private int ticks;
	private int ticksUntilAutosave = 6000;
	protected final Proxy proxy;
	private boolean onlineMode;
	private boolean preventProxyConnections;
	private boolean pvpEnabled;
	private boolean flightEnabled;
	@Nullable
	private String motd;
	private int playerIdleTimeout;
	private final long[] tickTimes = new long[100];
	private long recentTickTimesNanos = 0L;
	@Nullable
	private KeyPair keyPair;
	@Nullable
	private GameProfile hostProfile;
	private boolean demo;
	private volatile boolean loading;
	private long lastOverloadWarningNanos;
	protected final ApiServices apiServices;
	private long lastPlayerSampleUpdate;
	private final Thread serverThread;
	private long prevFullTickLogTime = Util.getMeasuringTimeNano();
	private long tasksStartTime = Util.getMeasuringTimeNano();
	private long waitTime;
	private long tickStartTimeNanos = Util.getMeasuringTimeNano();
	private boolean waitingForNextTick = false;
	private long tickEndTimeNanos;
	private boolean hasJustExecutedTask;
	private final ResourcePackManager dataPackManager;
	private final ServerScoreboard scoreboard = new ServerScoreboard(this);
	@Nullable
	private DataCommandStorage dataCommandStorage;
	private final BossBarManager bossBarManager = new BossBarManager();
	private final CommandFunctionManager commandFunctionManager;
	private boolean enforceWhitelist;
	private float averageTickTime;
	private final Executor workerExecutor;
	@Nullable
	private String serverId;
	private MinecraftServer.ResourceManagerHolder resourceManagerHolder;
	private final StructureTemplateManager structureTemplateManager;
	private final ServerTickManager tickManager;
	protected final SaveProperties saveProperties;
	private final BrewingRecipeRegistry brewingRecipeRegistry;
	private FuelRegistry fuelRegistry;
	private int idleTickCount;
	private volatile boolean saving;
	private static final AtomicReference<RuntimeException> WORLD_GEN_EXCEPTION = new AtomicReference();
	private final SuppressedExceptionsTracker suppressedExceptionsTracker = new SuppressedExceptionsTracker();
	private final DiscontinuousFrame discontinuousFrame;

	public static <S extends MinecraftServer> S startServer(Function<Thread, S> serverFactory) {
		AtomicReference<S> atomicReference = new AtomicReference();
		Thread thread = new Thread(() -> ((MinecraftServer)atomicReference.get()).runServer(), "Server thread");
		thread.setUncaughtExceptionHandler((threadx, throwable) -> LOGGER.error("Uncaught exception in server thread", throwable));
		if (Runtime.getRuntime().availableProcessors() > 4) {
			thread.setPriority(8);
		}

		S minecraftServer = (S)serverFactory.apply(thread);
		atomicReference.set(minecraftServer);
		thread.start();
		return minecraftServer;
	}

	public MinecraftServer(
		Thread serverThread,
		LevelStorage.Session session,
		ResourcePackManager dataPackManager,
		SaveLoader saveLoader,
		Proxy proxy,
		DataFixer dataFixer,
		ApiServices apiServices,
		WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory
	) {
		super("Server");
		this.combinedDynamicRegistries = saveLoader.combinedDynamicRegistries();
		this.saveProperties = saveLoader.saveProperties();
		if (!this.combinedDynamicRegistries.getCombinedRegistryManager().getOrThrow(RegistryKeys.DIMENSION).contains(DimensionOptions.OVERWORLD)) {
			throw new IllegalStateException("Missing Overworld dimension data");
		} else {
			this.proxy = proxy;
			this.dataPackManager = dataPackManager;
			this.resourceManagerHolder = new MinecraftServer.ResourceManagerHolder(saveLoader.resourceManager(), saveLoader.dataPackContents());
			this.apiServices = apiServices;
			if (apiServices.userCache() != null) {
				apiServices.userCache().setExecutor(this);
			}

			this.networkIo = new ServerNetworkIo(this);
			this.tickManager = new ServerTickManager(this);
			this.worldGenerationProgressListenerFactory = worldGenerationProgressListenerFactory;
			this.session = session;
			this.saveHandler = session.createSaveHandler();
			this.dataFixer = dataFixer;
			this.commandFunctionManager = new CommandFunctionManager(this, this.resourceManagerHolder.dataPackContents.getFunctionLoader());
			RegistryEntryLookup<Block> registryEntryLookup = this.combinedDynamicRegistries
				.getCombinedRegistryManager()
				.getOrThrow(RegistryKeys.BLOCK)
				.withFeatureFilter(this.saveProperties.getEnabledFeatures());
			this.structureTemplateManager = new StructureTemplateManager(saveLoader.resourceManager(), session, dataFixer, registryEntryLookup);
			this.serverThread = serverThread;
			this.workerExecutor = Util.getMainWorkerExecutor();
			this.brewingRecipeRegistry = BrewingRecipeRegistry.create(this.saveProperties.getEnabledFeatures());
			this.resourceManagerHolder.dataPackContents.getRecipeManager().initialize(this.saveProperties.getEnabledFeatures());
			this.fuelRegistry = FuelRegistry.createDefault(this.combinedDynamicRegistries.getCombinedRegistryManager(), this.saveProperties.getEnabledFeatures());
			this.discontinuousFrame = TracyClient.createDiscontinuousFrame("Server Tick");
		}
	}

	private void initScoreboard(PersistentStateManager persistentStateManager) {
		persistentStateManager.getOrCreate(this.getScoreboard().getPersistentStateType(), "scoreboard");
	}

	/**
	 * Setups a Minecraft server to be ready for players to connect.
	 * This method does several things including loading server properties and loading worlds.
	 * 
	 * @return true if the Minecraft server was successfully setup, false if the server failed to be setup.
	 */
	protected abstract boolean setupServer() throws IOException;

	protected void loadWorld() {
		if (!FlightProfiler.INSTANCE.isProfiling()) {
		}

		boolean bl = false;
		Finishable finishable = FlightProfiler.INSTANCE.startWorldLoadProfiling();
		this.saveProperties.addServerBrand(this.getServerModName(), this.getModStatus().isModded());
		WorldGenerationProgressListener worldGenerationProgressListener = this.worldGenerationProgressListenerFactory
			.create(this.saveProperties.getGameRules().getInt(GameRules.SPAWN_CHUNK_RADIUS));
		this.createWorlds(worldGenerationProgressListener);
		this.updateDifficulty();
		this.prepareStartRegion(worldGenerationProgressListener);
		if (finishable != null) {
			finishable.finish();
		}

		if (bl) {
			try {
				FlightProfiler.INSTANCE.stop();
			} catch (Throwable var5) {
				LOGGER.warn("Failed to stop JFR profiling", var5);
			}
		}
	}

	protected void updateDifficulty() {
	}

	protected void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener) {
		ServerWorldProperties serverWorldProperties = this.saveProperties.getMainWorldProperties();
		boolean bl = this.saveProperties.isDebugWorld();
		Registry<DimensionOptions> registry = this.combinedDynamicRegistries.getCombinedRegistryManager().getOrThrow(RegistryKeys.DIMENSION);
		GeneratorOptions generatorOptions = this.saveProperties.getGeneratorOptions();
		long l = generatorOptions.getSeed();
		long m = BiomeAccess.hashSeed(l);
		List<SpecialSpawner> list = ImmutableList.of(
			new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new ZombieSiegeManager(), new WanderingTraderManager(serverWorldProperties)
		);
		DimensionOptions dimensionOptions = registry.get(DimensionOptions.OVERWORLD);
		ServerWorld serverWorld = new ServerWorld(
			this, this.workerExecutor, this.session, serverWorldProperties, World.OVERWORLD, dimensionOptions, worldGenerationProgressListener, bl, m, list, true, null
		);
		this.worlds.put(World.OVERWORLD, serverWorld);
		PersistentStateManager persistentStateManager = serverWorld.getPersistentStateManager();
		this.initScoreboard(persistentStateManager);
		this.dataCommandStorage = new DataCommandStorage(persistentStateManager);
		WorldBorder worldBorder = serverWorld.getWorldBorder();
		if (!serverWorldProperties.isInitialized()) {
			try {
				setupSpawn(serverWorld, serverWorldProperties, generatorOptions.hasBonusChest(), bl);
				serverWorldProperties.setInitialized(true);
				if (bl) {
					this.setToDebugWorldProperties(this.saveProperties);
				}
			} catch (Throwable var23) {
				CrashReport crashReport = CrashReport.create(var23, "Exception initializing level");

				try {
					serverWorld.addDetailsToCrashReport(crashReport);
				} catch (Throwable var22) {
				}

				throw new CrashException(crashReport);
			}

			serverWorldProperties.setInitialized(true);
		}

		this.getPlayerManager().setMainWorld(serverWorld);
		if (this.saveProperties.getCustomBossEvents() != null) {
			this.getBossBarManager().readNbt(this.saveProperties.getCustomBossEvents(), this.getRegistryManager());
		}

		RandomSequencesState randomSequencesState = serverWorld.getRandomSequences();

		for (Entry<RegistryKey<DimensionOptions>, DimensionOptions> entry : registry.getEntrySet()) {
			RegistryKey<DimensionOptions> registryKey = (RegistryKey<DimensionOptions>)entry.getKey();
			if (registryKey != DimensionOptions.OVERWORLD) {
				RegistryKey<World> registryKey2 = RegistryKey.of(RegistryKeys.WORLD, registryKey.getValue());
				UnmodifiableLevelProperties unmodifiableLevelProperties = new UnmodifiableLevelProperties(this.saveProperties, serverWorldProperties);
				ServerWorld serverWorld2 = new ServerWorld(
					this,
					this.workerExecutor,
					this.session,
					unmodifiableLevelProperties,
					registryKey2,
					(DimensionOptions)entry.getValue(),
					worldGenerationProgressListener,
					bl,
					m,
					ImmutableList.of(),
					false,
					randomSequencesState
				);
				worldBorder.addListener(new WorldBorderListener.WorldBorderSyncer(serverWorld2.getWorldBorder()));
				this.worlds.put(registryKey2, serverWorld2);
			}
		}

		worldBorder.load(serverWorldProperties.getWorldBorder());
	}

	private static void setupSpawn(ServerWorld world, ServerWorldProperties worldProperties, boolean bonusChest, boolean debugWorld) {
		if (debugWorld) {
			worldProperties.setSpawnPos(BlockPos.ORIGIN.up(80), 0.0F);
		} else {
			ServerChunkManager serverChunkManager = world.getChunkManager();
			ChunkPos chunkPos = new ChunkPos(serverChunkManager.getNoiseConfig().getMultiNoiseSampler().findBestSpawnPosition());
			int i = serverChunkManager.getChunkGenerator().getSpawnHeight(world);
			if (i < world.getBottomY()) {
				BlockPos blockPos = chunkPos.getStartPos();
				i = world.getTopY(Heightmap.Type.WORLD_SURFACE, blockPos.getX() + 8, blockPos.getZ() + 8);
			}

			worldProperties.setSpawnPos(chunkPos.getStartPos().add(8, i, 8), 0.0F);
			int j = 0;
			int k = 0;
			int l = 0;
			int m = -1;

			for (int n = 0; n < MathHelper.square(11); n++) {
				if (j >= -5 && j <= 5 && k >= -5 && k <= 5) {
					BlockPos blockPos2 = SpawnLocating.findServerSpawnPoint(world, new ChunkPos(chunkPos.x + j, chunkPos.z + k));
					if (blockPos2 != null) {
						worldProperties.setSpawnPos(blockPos2, 0.0F);
						break;
					}
				}

				if (j == k || j < 0 && j == -k || j > 0 && j == 1 - k) {
					int o = l;
					l = -m;
					m = o;
				}

				j += l;
				k += m;
			}

			if (bonusChest) {
				world.getRegistryManager()
					.getOptional(RegistryKeys.CONFIGURED_FEATURE)
					.flatMap(featureRegistry -> featureRegistry.getOptional(MiscConfiguredFeatures.BONUS_CHEST))
					.ifPresent(
						feature -> ((ConfiguredFeature)feature.value()).generate(world, serverChunkManager.getChunkGenerator(), world.random, worldProperties.getSpawnPos())
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
		this.tickStartTimeNanos = Util.getMeasuringTimeNano();
		serverWorld.setSpawnPos(blockPos, serverWorld.getSpawnAngle());
		int i = this.getGameRules().getInt(GameRules.SPAWN_CHUNK_RADIUS);
		int j = i > 0 ? MathHelper.square(WorldGenerationProgressListener.getStartRegionSize(i)) : 0;

		while (serverChunkManager.getTotalChunksLoadedCount() < j) {
			this.tickStartTimeNanos = Util.getMeasuringTimeNano() + PREPARE_START_REGION_TICK_DELAY_NANOS;
			this.runTasksTillTickEnd();
		}

		this.tickStartTimeNanos = Util.getMeasuringTimeNano() + PREPARE_START_REGION_TICK_DELAY_NANOS;
		this.runTasksTillTickEnd();

		for (ServerWorld serverWorld2 : this.worlds.values()) {
			ForcedChunkState forcedChunkState = serverWorld2.getPersistentStateManager().get(ForcedChunkState.getPersistentStateType(), "chunks");
			if (forcedChunkState != null) {
				LongIterator longIterator = forcedChunkState.getChunks().iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					ChunkPos chunkPos = new ChunkPos(l);
					serverWorld2.getChunkManager().setChunkForced(chunkPos, true);
				}
			}
		}

		this.tickStartTimeNanos = Util.getMeasuringTimeNano() + PREPARE_START_REGION_TICK_DELAY_NANOS;
		this.runTasksTillTickEnd();
		worldGenerationProgressListener.stop();
		this.updateMobSpawnOptions();
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
		this.saveProperties.setCustomBossEvents(this.getBossBarManager().toNbt(this.getRegistryManager()));
		this.session.backupLevelDataFile(this.getRegistryManager(), this.saveProperties, this.getPlayerManager().getUserData());
		if (flush) {
			for (ServerWorld serverWorld3 : this.getWorlds()) {
				LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", serverWorld3.getChunkManager().chunkLoadingManager.getSaveDir());
			}

			LOGGER.info("ThreadedAnvilChunkStorage: All dimensions are saved");
		}

		return bl;
	}

	public boolean saveAll(boolean suppressLogs, boolean flush, boolean force) {
		boolean var4;
		try {
			this.saving = true;
			this.getPlayerManager().saveAllPlayerData();
			var4 = this.save(suppressLogs, flush, force);
		} finally {
			this.saving = false;
		}

		return var4;
	}

	@Override
	public void close() {
		this.shutdown();
	}

	public void shutdown() {
		if (this.recorder.isActive()) {
			this.forceStopRecorder();
		}

		LOGGER.info("Stopping server");
		this.getNetworkIo().stop();
		this.saving = true;
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

		while (this.worlds.values().stream().anyMatch(world -> world.getChunkManager().chunkLoadingManager.shouldDelayShutdown())) {
			this.tickStartTimeNanos = Util.getMeasuringTimeNano() + TimeHelper.MILLI_IN_NANOS;

			for (ServerWorld serverWorldx : this.getWorlds()) {
				serverWorldx.getChunkManager().removePersistentTickets();
				serverWorldx.getChunkManager().tick(() -> true, false);
			}

			this.runTasksTillTickEnd();
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

		this.saving = false;
		this.resourceManagerHolder.close();

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

	/**
	 * Stops this server.
	 * 
	 * @apiNote Pass {@code true} to {@code waitForShutdown} to wait until the server shuts
	 * down. Note that this must be {@code false} if called from the server thread,
	 * otherwise it deadlocks.
	 * 
	 * @param waitForShutdown whether to wait for server shutdown, if called outside the server thread
	 */
	public void stop(boolean waitForShutdown) {
		this.running = false;
		if (waitForShutdown) {
			try {
				this.serverThread.join();
			} catch (InterruptedException var3) {
				LOGGER.error("Error while shutting down", (Throwable)var3);
			}
		}
	}

	protected void runServer() {
		try {
			if (!this.setupServer()) {
				throw new IllegalStateException("Failed to initialize server");
			}

			this.tickStartTimeNanos = Util.getMeasuringTimeNano();
			this.favicon = (ServerMetadata.Favicon)this.loadFavicon().orElse(null);
			this.metadata = this.createMetadata();

			while (this.running) {
				long l;
				if (!this.isPaused() && this.tickManager.isSprinting() && this.tickManager.sprint()) {
					l = 0L;
					this.tickStartTimeNanos = Util.getMeasuringTimeNano();
					this.lastOverloadWarningNanos = this.tickStartTimeNanos;
				} else {
					l = this.tickManager.getNanosPerTick();
					long m = Util.getMeasuringTimeNano() - this.tickStartTimeNanos;
					if (m > OVERLOAD_THRESHOLD_NANOS + 20L * l && this.tickStartTimeNanos - this.lastOverloadWarningNanos >= OVERLOAD_WARNING_INTERVAL_NANOS + 100L * l) {
						long n = m / l;
						LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", m / TimeHelper.MILLI_IN_NANOS, n);
						this.tickStartTimeNanos += n * l;
						this.lastOverloadWarningNanos = this.tickStartTimeNanos;
					}
				}

				boolean bl = l == 0L;
				if (this.needsDebugSetup) {
					this.needsDebugSetup = false;
					this.debugStart = new MinecraftServer.DebugStart(Util.getMeasuringTimeNano(), this.ticks);
				}

				this.tickStartTimeNanos += l;

				try (Profilers.Scoped scoped = Profilers.using(this.startTickMetrics())) {
					Profiler profiler = Profilers.get();
					profiler.push("tick");
					this.discontinuousFrame.start();
					this.tick(bl ? () -> false : this::shouldKeepTicking);
					this.discontinuousFrame.end();
					profiler.swap("nextTickWait");
					this.hasJustExecutedTask = true;
					this.tickEndTimeNanos = Math.max(Util.getMeasuringTimeNano() + l, this.tickStartTimeNanos);
					this.startTaskPerformanceLog();
					this.runTasksTillTickEnd();
					this.pushPerformanceLogs();
					if (bl) {
						this.tickManager.updateSprintTime();
					}

					profiler.pop();
					this.pushFullTickLog();
				} finally {
					this.endTickMetrics();
				}

				this.loading = true;
				FlightProfiler.INSTANCE.onTick(this.averageTickTime);
			}
		} catch (Throwable var69) {
			LOGGER.error("Encountered an unexpected exception", var69);
			CrashReport crashReport = createCrashReport(var69);
			this.addSystemDetails(crashReport.getSystemDetailsSection());
			Path path = this.getRunDirectory().resolve("crash-reports").resolve("crash-" + Util.getFormattedCurrentTime() + "-server.txt");
			if (crashReport.writeToFile(path, ReportType.MINECRAFT_CRASH_REPORT)) {
				LOGGER.error("This crash report has been saved to: {}", path.toAbsolutePath());
			} else {
				LOGGER.error("We were unable to save this crash report to disk.");
			}

			this.setCrashReport(crashReport);
		} finally {
			try {
				this.stopped = true;
				this.shutdown();
			} catch (Throwable var64) {
				LOGGER.error("Exception stopping the server", var64);
			} finally {
				if (this.apiServices.userCache() != null) {
					this.apiServices.userCache().clearExecutor();
				}

				this.exit();
			}
		}
	}

	private void pushFullTickLog() {
		long l = Util.getMeasuringTimeNano();
		if (this.shouldPushTickTimeLog()) {
			this.getDebugSampleLog().push(l - this.prevFullTickLogTime);
		}

		this.prevFullTickLogTime = l;
	}

	private void startTaskPerformanceLog() {
		if (this.shouldPushTickTimeLog()) {
			this.tasksStartTime = Util.getMeasuringTimeNano();
			this.waitTime = 0L;
		}
	}

	private void pushPerformanceLogs() {
		if (this.shouldPushTickTimeLog()) {
			DebugSampleLog debugSampleLog = this.getDebugSampleLog();
			debugSampleLog.push(Util.getMeasuringTimeNano() - this.tasksStartTime - this.waitTime, ServerTickType.SCHEDULED_TASKS.ordinal());
			debugSampleLog.push(this.waitTime, ServerTickType.IDLE.ordinal());
		}
	}

	private static CrashReport createCrashReport(Throwable throwable) {
		CrashException crashException = null;

		for (Throwable throwable2 = throwable; throwable2 != null; throwable2 = throwable2.getCause()) {
			if (throwable2 instanceof CrashException crashException2) {
				crashException = crashException2;
			}
		}

		CrashReport crashReport;
		if (crashException != null) {
			crashReport = crashException.getReport();
			if (crashException != throwable) {
				crashReport.addElement("Wrapped in").add("Wrapping exception", throwable);
			}
		} else {
			crashReport = new CrashReport("Exception in server tick loop", throwable);
		}

		return crashReport;
	}

	private boolean shouldKeepTicking() {
		return this.hasRunningTasks() || Util.getMeasuringTimeNano() < (this.hasJustExecutedTask ? this.tickEndTimeNanos : this.tickStartTimeNanos);
	}

	public static boolean checkWorldGenException() {
		RuntimeException runtimeException = (RuntimeException)WORLD_GEN_EXCEPTION.get();
		if (runtimeException != null) {
			throw runtimeException;
		} else {
			return true;
		}
	}

	public static void setWorldGenException(RuntimeException exception) {
		WORLD_GEN_EXCEPTION.compareAndSet(null, exception);
	}

	@Override
	public void runTasks(BooleanSupplier stopCondition) {
		super.runTasks(() -> checkWorldGenException() && stopCondition.getAsBoolean());
	}

	protected void runTasksTillTickEnd() {
		this.runTasks();
		this.waitingForNextTick = true;

		try {
			this.runTasks(() -> !this.shouldKeepTicking());
		} finally {
			this.waitingForNextTick = false;
		}
	}

	@Override
	public void waitForTasks() {
		boolean bl = this.shouldPushTickTimeLog();
		long l = bl ? Util.getMeasuringTimeNano() : 0L;
		long m = this.waitingForNextTick ? this.tickStartTimeNanos - Util.getMeasuringTimeNano() : 100000L;
		LockSupport.parkNanos("waiting for tasks", m);
		if (bl) {
			this.waitTime = this.waitTime + (Util.getMeasuringTimeNano() - l);
		}
	}

	public ServerTask createTask(Runnable runnable) {
		return new ServerTask(this.ticks, runnable);
	}

	protected boolean canExecute(ServerTask serverTask) {
		return serverTask.getCreationTicks() + 3 < this.ticks || this.shouldKeepTicking();
	}

	@Override
	public boolean runTask() {
		boolean bl = this.runOneTask();
		this.hasJustExecutedTask = bl;
		return bl;
	}

	private boolean runOneTask() {
		if (super.runTask()) {
			return true;
		} else {
			if (this.tickManager.isSprinting() || this.shouldKeepTicking()) {
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
		Profilers.get().visit("runTask");
		super.executeTask(serverTask);
	}

	private Optional<ServerMetadata.Favicon> loadFavicon() {
		Optional<Path> optional = Optional.of(this.getPath("server-icon.png"))
			.filter(path -> Files.isRegularFile(path, new LinkOption[0]))
			.or(() -> this.session.getIconFile().filter(path -> Files.isRegularFile(path, new LinkOption[0])));
		return optional.flatMap(path -> {
			try {
				BufferedImage bufferedImage = ImageIO.read(path.toFile());
				Preconditions.checkState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide");
				Preconditions.checkState(bufferedImage.getHeight() == 64, "Must be 64 pixels high");
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
				return Optional.of(new ServerMetadata.Favicon(byteArrayOutputStream.toByteArray()));
			} catch (Exception var3) {
				LOGGER.error("Couldn't load server icon", (Throwable)var3);
				return Optional.empty();
			}
		});
	}

	public Optional<Path> getIconFile() {
		return this.session.getIconFile();
	}

	public Path getRunDirectory() {
		return Path.of("");
	}

	public void setCrashReport(CrashReport report) {
	}

	public void exit() {
	}

	public boolean isPaused() {
		return false;
	}

	public void tick(BooleanSupplier shouldKeepTicking) {
		long l = Util.getMeasuringTimeNano();
		int i = this.getPauseWhenEmptySeconds() * 20;
		if (i > 0) {
			if (this.playerManager.getCurrentPlayerCount() == 0 && !this.tickManager.isSprinting()) {
				this.idleTickCount++;
			} else {
				this.idleTickCount = 0;
			}

			if (this.idleTickCount >= i) {
				if (this.idleTickCount == i) {
					LOGGER.info("Server empty for {} seconds, pausing", this.getPauseWhenEmptySeconds());
					this.runAutosave();
				}

				this.tickNetworkIo();
				return;
			}
		}

		this.ticks++;
		this.tickManager.step();
		this.tickWorlds(shouldKeepTicking);
		if (l - this.lastPlayerSampleUpdate >= PLAYER_SAMPLE_UPDATE_INTERVAL_NANOS) {
			this.lastPlayerSampleUpdate = l;
			this.metadata = this.createMetadata();
		}

		this.ticksUntilAutosave--;
		if (this.ticksUntilAutosave <= 0) {
			this.runAutosave();
		}

		Profiler profiler = Profilers.get();
		profiler.push("tallying");
		long m = Util.getMeasuringTimeNano() - l;
		int j = this.ticks % 100;
		this.recentTickTimesNanos = this.recentTickTimesNanos - this.tickTimes[j];
		this.recentTickTimesNanos += m;
		this.tickTimes[j] = m;
		this.averageTickTime = this.averageTickTime * 0.8F + (float)m / (float)TimeHelper.MILLI_IN_NANOS * 0.19999999F;
		this.pushTickLog(l);
		profiler.pop();
	}

	private void runAutosave() {
		this.ticksUntilAutosave = this.getAutosaveInterval();
		LOGGER.debug("Autosave started");
		Profiler profiler = Profilers.get();
		profiler.push("save");
		this.saveAll(true, false, false);
		profiler.pop();
		LOGGER.debug("Autosave finished");
	}

	private void pushTickLog(long tickStartTime) {
		if (this.shouldPushTickTimeLog()) {
			this.getDebugSampleLog().push(Util.getMeasuringTimeNano() - tickStartTime, ServerTickType.TICK_SERVER_METHOD.ordinal());
		}
	}

	private int getAutosaveInterval() {
		float f;
		if (this.tickManager.isSprinting()) {
			long l = this.getAverageNanosPerTick() + 1L;
			f = (float)TimeHelper.SECOND_IN_NANOS / (float)l;
		} else {
			f = this.tickManager.getTickRate();
		}

		int i = 300;
		return Math.max(100, (int)(f * 300.0F));
	}

	public void updateAutosaveTicks() {
		int i = this.getAutosaveInterval();
		if (i < this.ticksUntilAutosave) {
			this.ticksUntilAutosave = i;
		}
	}

	protected abstract DebugSampleLog getDebugSampleLog();

	public abstract boolean shouldPushTickTimeLog();

	private ServerMetadata createMetadata() {
		ServerMetadata.Players players = this.createMetadataPlayers();
		return new ServerMetadata(
			Text.of(this.motd), Optional.of(players), Optional.of(ServerMetadata.Version.create()), Optional.ofNullable(this.favicon), this.shouldEnforceSecureProfile()
		);
	}

	private ServerMetadata.Players createMetadataPlayers() {
		List<ServerPlayerEntity> list = this.playerManager.getPlayerList();
		int i = this.getMaxPlayerCount();
		if (this.hideOnlinePlayers()) {
			return new ServerMetadata.Players(i, list.size(), List.of());
		} else {
			int j = Math.min(list.size(), 12);
			ObjectArrayList<GameProfile> objectArrayList = new ObjectArrayList<>(j);
			int k = MathHelper.nextInt(this.random, 0, list.size() - j);

			for (int l = 0; l < j; l++) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)list.get(k + l);
				objectArrayList.add(serverPlayerEntity.allowsServerListing() ? serverPlayerEntity.getGameProfile() : ANONYMOUS_PLAYER_PROFILE);
			}

			Util.shuffle(objectArrayList, this.random);
			return new ServerMetadata.Players(i, list.size(), objectArrayList);
		}
	}

	protected void tickWorlds(BooleanSupplier shouldKeepTicking) {
		Profiler profiler = Profilers.get();
		this.getPlayerManager().getPlayerList().forEach(player -> player.networkHandler.disableFlush());
		profiler.push("commandFunctions");
		this.getCommandFunctionManager().tick();
		profiler.swap("levels");

		for (ServerWorld serverWorld : this.getWorlds()) {
			profiler.push((Supplier<String>)(() -> serverWorld + " " + serverWorld.getRegistryKey().getValue()));
			if (this.ticks % 20 == 0) {
				profiler.push("timeSync");
				this.sendTimeUpdatePackets(serverWorld);
				profiler.pop();
			}

			profiler.push("tick");

			try {
				serverWorld.tick(shouldKeepTicking);
			} catch (Throwable var7) {
				CrashReport crashReport = CrashReport.create(var7, "Exception ticking world");
				serverWorld.addDetailsToCrashReport(crashReport);
				throw new CrashException(crashReport);
			}

			profiler.pop();
			profiler.pop();
		}

		profiler.swap("connection");
		this.tickNetworkIo();
		profiler.swap("players");
		this.playerManager.updatePlayerLatency();
		if (SharedConstants.isDevelopment && this.tickManager.shouldTick()) {
			TestManager.INSTANCE.tick();
		}

		profiler.swap("server gui refresh");

		for (int i = 0; i < this.serverGuiTickables.size(); i++) {
			((Runnable)this.serverGuiTickables.get(i)).run();
		}

		profiler.swap("send chunks");

		for (ServerPlayerEntity serverPlayerEntity : this.playerManager.getPlayerList()) {
			serverPlayerEntity.networkHandler.chunkDataSender.sendChunkBatches(serverPlayerEntity);
			serverPlayerEntity.networkHandler.enableFlush();
		}

		profiler.pop();
	}

	public void tickNetworkIo() {
		this.getNetworkIo().tick();
	}

	private void sendTimeUpdatePackets(ServerWorld world) {
		this.playerManager
			.sendToDimension(
				new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(), world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), world.getRegistryKey()
			);
	}

	public void sendTimeUpdatePackets() {
		Profiler profiler = Profilers.get();
		profiler.push("timeSync");

		for (ServerWorld serverWorld : this.getWorlds()) {
			this.sendTimeUpdatePackets(serverWorld);
		}

		profiler.pop();
	}

	public boolean isWorldAllowed(World world) {
		return true;
	}

	public void addServerGuiTickable(Runnable tickable) {
		this.serverGuiTickables.add(tickable);
	}

	protected void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public boolean isStopping() {
		return !this.serverThread.isAlive();
	}

	/**
	 * @param path relative path from the run directory
	 */
	public Path getPath(String path) {
		return this.getRunDirectory().resolve(path);
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

	@Override
	public String getVersion() {
		return SharedConstants.getGameVersion().getName();
	}

	@Override
	public int getCurrentPlayerCount() {
		return this.playerManager.getCurrentPlayerCount();
	}

	@Override
	public int getMaxPlayerCount() {
		return this.playerManager.getMaxPlayerCount();
	}

	public String[] getPlayerNames() {
		return this.playerManager.getPlayerNames();
	}

	@DontObfuscate
	public String getServerModName() {
		return "vanilla";
	}

	public SystemDetails addSystemDetails(SystemDetails details) {
		details.addSection("Server Running", (Supplier<String>)(() -> Boolean.toString(this.running)));
		if (this.playerManager != null) {
			details.addSection(
				"Player Count",
				(Supplier<String>)(() -> this.playerManager.getCurrentPlayerCount()
						+ " / "
						+ this.playerManager.getMaxPlayerCount()
						+ "; "
						+ this.playerManager.getPlayerList())
			);
		}

		details.addSection("Active Data Packs", (Supplier<String>)(() -> ResourcePackManager.listPacks(this.dataPackManager.getEnabledProfiles())));
		details.addSection("Available Data Packs", (Supplier<String>)(() -> ResourcePackManager.listPacks(this.dataPackManager.getProfiles())));
		details.addSection(
			"Enabled Feature Flags",
			(Supplier<String>)(() -> (String)FeatureFlags.FEATURE_MANAGER
					.toId(this.saveProperties.getEnabledFeatures())
					.stream()
					.map(Identifier::toString)
					.collect(Collectors.joining(", ")))
		);
		details.addSection("World Generation", (Supplier<String>)(() -> this.saveProperties.getLifecycle().toString()));
		details.addSection("World Seed", (Supplier<String>)(() -> String.valueOf(this.saveProperties.getGeneratorOptions().getSeed())));
		details.addSection("Suppressed Exceptions", this.suppressedExceptionsTracker::collect);
		if (this.serverId != null) {
			details.addSection("Server Id", (Supplier<String>)(() -> this.serverId));
		}

		return this.addExtraSystemDetails(details);
	}

	public abstract SystemDetails addExtraSystemDetails(SystemDetails details);

	public ModStatus getModStatus() {
		return ModStatus.check("vanilla", this::getServerModName, "Server", MinecraftServer.class);
	}

	@Override
	public void sendMessage(Text message) {
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

	@Nullable
	public GameProfile getHostProfile() {
		return this.hostProfile;
	}

	public void setHostProfile(@Nullable GameProfile hostProfile) {
		this.hostProfile = hostProfile;
	}

	/**
	 * {@return whether this server is a singleplayer server} A {@index singleplayer}
	 * server has a "single player" to whom the player data in the {@code level.dat}
	 * applies. Otherwise, the player data is not applied to anyone. Hence, it is
	 * necessary to properly load some single-player save games.
	 * 
	 * <p>All vanilla integrated servers and dedicated servers launched with the argument
	 * {@code --singleplayer <singlePlayerName>} are singleplayer servers.
	 * 
	 * <p>A dedicated singleplayer server always turns online mode off, regardless of the
	 * content of {@code server.properties}.
	 * 
	 * @see #getHostProfile
	 * @see #setHostProfile
	 */
	public boolean isSingleplayer() {
		return this.hostProfile != null;
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
			serverWorld.setMobSpawnOptions(this.isMonsterSpawningEnabled());
		}
	}

	public void setDifficultyLocked(boolean locked) {
		this.saveProperties.setDifficultyLocked(locked);
		this.getPlayerManager().getPlayerList().forEach(this::sendDifficulty);
	}

	private void sendDifficulty(ServerPlayerEntity player) {
		WorldProperties worldProperties = player.getWorld().getLevelProperties();
		player.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
	}

	public boolean isMonsterSpawningEnabled() {
		return this.saveProperties.getDifficulty() != Difficulty.PEACEFUL;
	}

	public boolean isDemo() {
		return this.demo;
	}

	public void setDemo(boolean demo) {
		this.demo = demo;
	}

	public Optional<MinecraftServer.ServerResourcePackProperties> getResourcePackProperties() {
		return Optional.empty();
	}

	public boolean requireResourcePack() {
		return this.getResourcePackProperties().filter(MinecraftServer.ServerResourcePackProperties::isRequired).isPresent();
	}

	/**
	 * Checks whether this server is a dedicated server.
	 * 
	 * <p>A dedicated server refers to a Minecraft server implementation which is detached from a parent Minecraft client process.
	 * A dedicated Minecraft server only accepts remote connections.
	 */
	public abstract boolean isDedicated();

	public abstract int getRateLimit();

	/**
	 * {@return whether this Minecraft server authenticates players logging in with the
	 * {@linkplain #getSessionService() Minecraft Session Service}} If this server is
	 * {@linkplain #isSingleplayer() singleplayer}, such as integrated servers, it will
	 * accept unauthenticated players; otherwise, it disconnects such players.
	 * 
	 * @see net.minecraft.server.network.ServerLoginNetworkHandler
	 */
	public boolean isOnlineMode() {
		return this.onlineMode;
	}

	/**
	 * Sets whether this server is in the online mode, or whether it
	 * authenticates connecting players with the Minecraft Session Service.
	 * 
	 * <p>This is called by individual server implementations on their setup.
	 * 
	 * @see #isOnlineMode()
	 * 
	 * @param onlineMode whether the server will be in online mode
	 */
	public void setOnlineMode(boolean onlineMode) {
		this.onlineMode = onlineMode;
	}

	public boolean shouldPreventProxyConnections() {
		return this.preventProxyConnections;
	}

	public void setPreventProxyConnections(boolean preventProxyConnections) {
		this.preventProxyConnections = preventProxyConnections;
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

	@Override
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

	public ServerNetworkIo getNetworkIo() {
		return this.networkIo;
	}

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
	 * @param port the port to open up to LAN connections
	 * @param gameMode the game mode connecting players will have set by default
	 * @param cheatsAllowed whether players on the server have operator permissions
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

	public int getSpawnProtectionRadius() {
		return 16;
	}

	public boolean isSpawnProtected(ServerWorld world, BlockPos pos, PlayerEntity player) {
		return false;
	}

	public boolean acceptsStatusQuery() {
		return true;
	}

	public boolean hideOnlinePlayers() {
		return false;
	}

	public Proxy getProxy() {
		return this.proxy;
	}

	public int getPlayerIdleTimeout() {
		return this.playerIdleTimeout;
	}

	public void setPlayerIdleTimeout(int playerIdleTimeout) {
		this.playerIdleTimeout = playerIdleTimeout;
	}

	public MinecraftSessionService getSessionService() {
		return this.apiServices.sessionService();
	}

	@Nullable
	public SignatureVerifier getServicesSignatureVerifier() {
		return this.apiServices.serviceSignatureVerifier();
	}

	public GameProfileRepository getGameProfileRepo() {
		return this.apiServices.profileRepository();
	}

	@Nullable
	public UserCache getUserCache() {
		return this.apiServices.userCache();
	}

	@Nullable
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
	public void executeSync(Runnable runnable) {
		if (this.isStopped()) {
			throw new RejectedExecutionException("Server already shutting down");
		} else {
			super.executeSync(runnable);
		}
	}

	@Override
	public Thread getThread() {
		return this.serverThread;
	}

	public int getNetworkCompressionThreshold() {
		return 256;
	}

	public boolean shouldEnforceSecureProfile() {
		return false;
	}

	public long getTimeReference() {
		return this.tickStartTimeNanos;
	}

	public DataFixer getDataFixer() {
		return this.dataFixer;
	}

	public int getSpawnRadius(@Nullable ServerWorld world) {
		return world != null ? world.getGameRules().getInt(GameRules.SPAWN_RADIUS) : 10;
	}

	public ServerAdvancementLoader getAdvancementLoader() {
		return this.resourceManagerHolder.dataPackContents.getServerAdvancementLoader();
	}

	public CommandFunctionManager getCommandFunctionManager() {
		return this.commandFunctionManager;
	}

	/**
	 * Reloads this server's data packs.
	 * 
	 * @return a completable future which specifies whether the reload was successful
	 * A reload has failed when the future is exceptionally completed.
	 * @see CompletableFuture
	 */
	public CompletableFuture<Void> reloadResources(Collection<String> dataPacks) {
		CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(
				() -> (ImmutableList)dataPacks.stream()
						.map(this.dataPackManager::getProfile)
						.filter(Objects::nonNull)
						.map(ResourcePackProfile::createResourcePack)
						.collect(ImmutableList.toImmutableList()),
				this
			)
			.thenCompose(
				resourcePacks -> {
					LifecycledResourceManager lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, resourcePacks);
					List<Registry.PendingTagLoad<?>> list = TagGroupLoader.startReload(lifecycledResourceManager, this.combinedDynamicRegistries.getCombinedRegistryManager());
					return DataPackContents.reload(
							lifecycledResourceManager,
							this.combinedDynamicRegistries,
							list,
							this.saveProperties.getEnabledFeatures(),
							this.isDedicated() ? CommandManager.RegistrationEnvironment.DEDICATED : CommandManager.RegistrationEnvironment.INTEGRATED,
							this.getFunctionPermissionLevel(),
							this.workerExecutor,
							this
						)
						.whenComplete((dataPackContents, throwable) -> {
							if (throwable != null) {
								lifecycledResourceManager.close();
							}
						})
						.thenApply(dataPackContents -> new MinecraftServer.ResourceManagerHolder(lifecycledResourceManager, dataPackContents));
				}
			)
			.thenAcceptAsync(resourceManagerHolder -> {
				this.resourceManagerHolder.close();
				this.resourceManagerHolder = resourceManagerHolder;
				this.dataPackManager.setEnabledProfiles(dataPacks);
				DataConfiguration dataConfiguration = new DataConfiguration(createDataPackSettings(this.dataPackManager, true), this.saveProperties.getEnabledFeatures());
				this.saveProperties.updateLevelInfo(dataConfiguration);
				this.resourceManagerHolder.dataPackContents.applyPendingTagLoads();
				this.resourceManagerHolder.dataPackContents.getRecipeManager().initialize(this.saveProperties.getEnabledFeatures());
				this.getPlayerManager().saveAllPlayerData();
				this.getPlayerManager().onDataPacksReloaded();
				this.commandFunctionManager.setFunctions(this.resourceManagerHolder.dataPackContents.getFunctionLoader());
				this.structureTemplateManager.setResourceManager(this.resourceManagerHolder.resourceManager);
				this.fuelRegistry = FuelRegistry.createDefault(this.combinedDynamicRegistries.getCombinedRegistryManager(), this.saveProperties.getEnabledFeatures());
			}, this);
		if (this.isOnThread()) {
			this.runTasks(completableFuture::isDone);
		}

		return completableFuture;
	}

	public static DataConfiguration loadDataPacks(ResourcePackManager resourcePackManager, DataConfiguration dataConfiguration, boolean initMode, boolean safeMode) {
		DataPackSettings dataPackSettings = dataConfiguration.dataPacks();
		FeatureSet featureSet = initMode ? FeatureSet.empty() : dataConfiguration.enabledFeatures();
		FeatureSet featureSet2 = initMode ? FeatureFlags.FEATURE_MANAGER.getFeatureSet() : dataConfiguration.enabledFeatures();
		resourcePackManager.scanPacks();
		if (safeMode) {
			return loadDataPacks(resourcePackManager, List.of("vanilla"), featureSet, false);
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
				String string2 = resourcePackProfile.getId();
				if (!dataPackSettings.getDisabled().contains(string2)) {
					FeatureSet featureSet3 = resourcePackProfile.getRequestedFeatures();
					boolean bl = set.contains(string2);
					if (!bl && resourcePackProfile.getSource().canBeEnabledLater()) {
						if (featureSet3.isSubsetOf(featureSet2)) {
							LOGGER.info("Found new data pack {}, loading it automatically", string2);
							set.add(string2);
						} else {
							LOGGER.info("Found new data pack {}, but can't load it due to missing features {}", string2, FeatureFlags.printMissingFlags(featureSet2, featureSet3));
						}
					}

					if (bl && !featureSet3.isSubsetOf(featureSet2)) {
						LOGGER.warn(
							"Pack {} requires features {} that are not enabled for this world, disabling pack.", string2, FeatureFlags.printMissingFlags(featureSet2, featureSet3)
						);
						set.remove(string2);
					}
				}
			}

			if (set.isEmpty()) {
				LOGGER.info("No datapacks selected, forcing vanilla");
				set.add("vanilla");
			}

			return loadDataPacks(resourcePackManager, set, featureSet, true);
		}
	}

	private static DataConfiguration loadDataPacks(
		ResourcePackManager resourcePackManager, Collection<String> enabledProfiles, FeatureSet enabledFeatures, boolean allowEnabling
	) {
		resourcePackManager.setEnabledProfiles(enabledProfiles);
		forceEnableRequestedFeatures(resourcePackManager, enabledFeatures);
		DataPackSettings dataPackSettings = createDataPackSettings(resourcePackManager, allowEnabling);
		FeatureSet featureSet = resourcePackManager.getRequestedFeatures().combine(enabledFeatures);
		return new DataConfiguration(dataPackSettings, featureSet);
	}

	private static void forceEnableRequestedFeatures(ResourcePackManager resourcePackManager, FeatureSet enabledFeatures) {
		FeatureSet featureSet = resourcePackManager.getRequestedFeatures();
		FeatureSet featureSet2 = enabledFeatures.subtract(featureSet);
		if (!featureSet2.isEmpty()) {
			Set<String> set = new ObjectArraySet<>(resourcePackManager.getEnabledIds());

			for (ResourcePackProfile resourcePackProfile : resourcePackManager.getProfiles()) {
				if (featureSet2.isEmpty()) {
					break;
				}

				if (resourcePackProfile.getSource() == ResourcePackSource.FEATURE) {
					String string = resourcePackProfile.getId();
					FeatureSet featureSet3 = resourcePackProfile.getRequestedFeatures();
					if (!featureSet3.isEmpty() && featureSet3.intersects(featureSet2) && featureSet3.isSubsetOf(enabledFeatures)) {
						if (!set.add(string)) {
							throw new IllegalStateException("Tried to force '" + string + "', but it was already enabled");
						}

						LOGGER.info("Found feature pack ('{}') for requested feature, forcing to enabled", string);
						featureSet2 = featureSet2.subtract(featureSet3);
					}
				}
			}

			resourcePackManager.setEnabledProfiles(set);
		}
	}

	private static DataPackSettings createDataPackSettings(ResourcePackManager dataPackManager, boolean allowEnabling) {
		Collection<String> collection = dataPackManager.getEnabledIds();
		List<String> list = ImmutableList.copyOf(collection);
		List<String> list2 = allowEnabling ? dataPackManager.getIds().stream().filter(name -> !collection.contains(name)).toList() : List.of();
		return new DataPackSettings(list, list2);
	}

	public void kickNonWhitelistedPlayers(ServerCommandSource source) {
		if (this.isEnforceWhitelist()) {
			PlayerManager playerManager = source.getServer().getPlayerManager();
			Whitelist whitelist = playerManager.getWhitelist();

			for (ServerPlayerEntity serverPlayerEntity : Lists.newArrayList(playerManager.getPlayerList())) {
				if (!whitelist.isAllowed(serverPlayerEntity.getGameProfile())) {
					serverPlayerEntity.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.not_whitelisted"));
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
		return this.resourceManagerHolder.dataPackContents.getCommandManager();
	}

	/**
	 * Creates a command source which represents this Minecraft server instance.
	 */
	public ServerCommandSource getCommandSource() {
		ServerWorld serverWorld = this.getOverworld();
		return new ServerCommandSource(
			this, serverWorld == null ? Vec3d.ZERO : Vec3d.of(serverWorld.getSpawnPos()), Vec2f.ZERO, serverWorld, 4, "Server", Text.literal("Server"), this, null
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

	@Override
	public abstract boolean shouldBroadcastConsoleToOps();

	public ServerRecipeManager getRecipeManager() {
		return this.resourceManagerHolder.dataPackContents.getRecipeManager();
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

	public GameRules getGameRules() {
		return this.getOverworld().getGameRules();
	}

	public BossBarManager getBossBarManager() {
		return this.bossBarManager;
	}

	public boolean isEnforceWhitelist() {
		return this.enforceWhitelist;
	}

	public void setEnforceWhitelist(boolean enforceWhitelist) {
		this.enforceWhitelist = enforceWhitelist;
	}

	public float getAverageTickTime() {
		return this.averageTickTime;
	}

	public ServerTickManager getTickManager() {
		return this.tickManager;
	}

	public long getAverageNanosPerTick() {
		return this.recentTickTimesNanos / (long)Math.min(100, Math.max(this.ticks, 1));
	}

	public long[] getTickTimes() {
		return this.tickTimes;
	}

	public int getPermissionLevel(GameProfile profile) {
		if (this.getPlayerManager().isOperator(profile)) {
			OperatorEntry operatorEntry = this.getPlayerManager().getOpList().get(profile);
			if (operatorEntry != null) {
				return operatorEntry.getPermissionLevel();
			} else if (this.isHost(profile)) {
				return 4;
			} else if (this.isSingleplayer()) {
				return this.getPlayerManager().areCheatsAllowed() ? 4 : 0;
			} else {
				return this.getOpPermissionLevel();
			}
		} else {
			return 0;
		}
	}

	public abstract boolean isHost(GameProfile profile);

	public void dumpProperties(Path file) throws IOException {
	}

	private void dump(Path path) {
		Path path2 = path.resolve("levels");

		try {
			for (Entry<RegistryKey<World>, ServerWorld> entry : this.worlds.entrySet()) {
				Identifier identifier = ((RegistryKey)entry.getKey()).getValue();
				Path path3 = path2.resolve(identifier.getNamespace()).resolve(identifier.getPath());
				Files.createDirectories(path3);
				((ServerWorld)entry.getValue()).dump(path3);
			}

			this.dumpGamerules(path.resolve("gamerules.txt"));
			this.dumpClasspath(path.resolve("classpath.txt"));
			this.dumpStats(path.resolve("stats.txt"));
			this.dumpThreads(path.resolve("threads.txt"));
			this.dumpProperties(path.resolve("server.properties.txt"));
			this.dumpNativeModules(path.resolve("modules.txt"));
		} catch (IOException var7) {
			LOGGER.warn("Failed to save debug report", (Throwable)var7);
		}
	}

	private void dumpStats(Path path) throws IOException {
		Writer writer = Files.newBufferedWriter(path);

		try {
			writer.write(String.format(Locale.ROOT, "pending_tasks: %d\n", this.getTaskCount()));
			writer.write(String.format(Locale.ROOT, "average_tick_time: %f\n", this.getAverageTickTime()));
			writer.write(String.format(Locale.ROOT, "tick_times: %s\n", Arrays.toString(this.tickTimes)));
			writer.write(String.format(Locale.ROOT, "queue: %s\n", Util.getMainWorkerExecutor()));
		} catch (Throwable var6) {
			if (writer != null) {
				try {
					writer.close();
				} catch (Throwable var5) {
					var6.addSuppressed(var5);
				}
			}

			throw var6;
		}

		if (writer != null) {
			writer.close();
		}
	}

	private void dumpGamerules(Path path) throws IOException {
		Writer writer = Files.newBufferedWriter(path);

		try {
			final List<String> list = Lists.<String>newArrayList();
			final GameRules gameRules = this.getGameRules();
			gameRules.accept(new GameRules.Visitor() {
				@Override
				public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
					list.add(String.format(Locale.ROOT, "%s=%s\n", key.getName(), gameRules.get(key)));
				}
			});

			for (String string : list) {
				writer.write(string);
			}
		} catch (Throwable var8) {
			if (writer != null) {
				try {
					writer.close();
				} catch (Throwable var7) {
					var8.addSuppressed(var7);
				}
			}

			throw var8;
		}

		if (writer != null) {
			writer.close();
		}
	}

	private void dumpClasspath(Path path) throws IOException {
		Writer writer = Files.newBufferedWriter(path);

		try {
			String string = System.getProperty("java.class.path");
			String string2 = System.getProperty("path.separator");

			for (String string3 : Splitter.on(string2).split(string)) {
				writer.write(string3);
				writer.write("\n");
			}
		} catch (Throwable var8) {
			if (writer != null) {
				try {
					writer.close();
				} catch (Throwable var7) {
					var8.addSuppressed(var7);
				}
			}

			throw var8;
		}

		if (writer != null) {
			writer.close();
		}
	}

	private void dumpThreads(Path path) throws IOException {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
		Arrays.sort(threadInfos, Comparator.comparing(ThreadInfo::getThreadName));
		Writer writer = Files.newBufferedWriter(path);

		try {
			for (ThreadInfo threadInfo : threadInfos) {
				writer.write(threadInfo.toString());
				writer.write(10);
			}
		} catch (Throwable var10) {
			if (writer != null) {
				try {
					writer.close();
				} catch (Throwable var9) {
					var10.addSuppressed(var9);
				}
			}

			throw var10;
		}

		if (writer != null) {
			writer.close();
		}
	}

	private void dumpNativeModules(Path path) throws IOException {
		Writer writer = Files.newBufferedWriter(path);

		label49: {
			try {
				label50: {
					List<WinNativeModuleUtil.NativeModule> list;
					try {
						list = Lists.<WinNativeModuleUtil.NativeModule>newArrayList(WinNativeModuleUtil.collectNativeModules());
					} catch (Throwable var7) {
						LOGGER.warn("Failed to list native modules", var7);
						break label50;
					}

					list.sort(Comparator.comparing(module -> module.path));
					Iterator throwable = list.iterator();

					while (true) {
						if (!throwable.hasNext()) {
							break label49;
						}

						WinNativeModuleUtil.NativeModule nativeModule = (WinNativeModuleUtil.NativeModule)throwable.next();
						writer.write(nativeModule.toString());
						writer.write(10);
					}
				}
			} catch (Throwable var8) {
				if (writer != null) {
					try {
						writer.close();
					} catch (Throwable var6) {
						var8.addSuppressed(var6);
					}
				}

				throw var8;
			}

			if (writer != null) {
				writer.close();
			}

			return;
		}

		if (writer != null) {
			writer.close();
		}
	}

	private Profiler startTickMetrics() {
		if (this.needsRecorderSetup) {
			this.recorder = DebugRecorder.of(
				new ServerSamplerSource(Util.nanoTimeSupplier, this.isDedicated()),
				Util.nanoTimeSupplier,
				Util.getIoWorkerExecutor(),
				new RecordDumper("server"),
				this.recorderResultConsumer,
				path -> {
					this.submitAndJoin(() -> this.dump(path.resolve("server")));
					this.recorderDumpConsumer.accept(path);
				}
			);
			this.needsRecorderSetup = false;
		}

		this.recorder.startTick();
		return TickDurationMonitor.tickProfiler(this.recorder.getProfiler(), TickDurationMonitor.create("Server"));
	}

	public void endTickMetrics() {
		this.recorder.endTick();
	}

	public boolean isRecorderActive() {
		return this.recorder.isActive();
	}

	public void setupRecorder(Consumer<ProfileResult> resultConsumer, Consumer<Path> dumpConsumer) {
		this.recorderResultConsumer = result -> {
			this.resetRecorder();
			resultConsumer.accept(result);
		};
		this.recorderDumpConsumer = dumpConsumer;
		this.needsRecorderSetup = true;
	}

	public void resetRecorder() {
		this.recorder = DummyRecorder.INSTANCE;
	}

	public void stopRecorder() {
		this.recorder.stop();
	}

	public void forceStopRecorder() {
		this.recorder.forceStop();
	}

	public Path getSavePath(WorldSavePath worldSavePath) {
		return this.session.getDirectory(worldSavePath);
	}

	public boolean syncChunkWrites() {
		return true;
	}

	public StructureTemplateManager getStructureTemplateManager() {
		return this.structureTemplateManager;
	}

	public SaveProperties getSaveProperties() {
		return this.saveProperties;
	}

	public DynamicRegistryManager.Immutable getRegistryManager() {
		return this.combinedDynamicRegistries.getCombinedRegistryManager();
	}

	public CombinedDynamicRegistries<ServerDynamicRegistryType> getCombinedDynamicRegistries() {
		return this.combinedDynamicRegistries;
	}

	public ReloadableRegistries.Lookup getReloadableRegistries() {
		return this.resourceManagerHolder.dataPackContents.getReloadableRegistries();
	}

	public TextStream createFilterer(ServerPlayerEntity player) {
		return TextStream.UNFILTERED;
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

	public ResourceManager getResourceManager() {
		return this.resourceManagerHolder.resourceManager;
	}

	public boolean isSaving() {
		return this.saving;
	}

	public boolean isDebugRunning() {
		return this.needsDebugSetup || this.debugStart != null;
	}

	public void startDebug() {
		this.needsDebugSetup = true;
	}

	public ProfileResult stopDebug() {
		if (this.debugStart == null) {
			return EmptyProfileResult.INSTANCE;
		} else {
			ProfileResult profileResult = this.debugStart.end(Util.getMeasuringTimeNano(), this.ticks);
			this.debugStart = null;
			return profileResult;
		}
	}

	public int getMaxChainedNeighborUpdates() {
		return 1000000;
	}

	public void logChatMessage(Text message, MessageType.Parameters params, @Nullable String prefix) {
		String string = params.applyChatDecoration(message).getString();
		if (prefix != null) {
			LOGGER.info("[{}] {}", prefix, string);
		} else {
			LOGGER.info("{}", string);
		}
	}

	/**
	 * {@return the message decorator used by the server}
	 * 
	 * @see MessageDecorator
	 */
	public MessageDecorator getMessageDecorator() {
		return MessageDecorator.NOOP;
	}

	public boolean shouldLogIps() {
		return true;
	}

	public void subscribeToDebugSample(ServerPlayerEntity player, DebugSampleType type) {
	}

	public boolean acceptsTransfers() {
		return false;
	}

	private void writeChunkIoReport(CrashReport report, ChunkPos pos, StorageKey key) {
		Util.getIoWorkerExecutor().execute(() -> {
			try {
				Path path = this.getPath("debug");
				PathUtil.createDirectories(path);
				String string = PathUtil.replaceInvalidChars(key.level());
				Path path2 = path.resolve("chunk-" + string + "-" + Util.getFormattedCurrentTime() + "-server.txt");
				FileStore fileStore = Files.getFileStore(path);
				long l = fileStore.getUsableSpace();
				if (l < 8192L) {
					LOGGER.warn("Not storing chunk IO report due to low space on drive {}", fileStore.name());
					return;
				}

				CrashReportSection crashReportSection = report.addElement("Chunk Info");
				crashReportSection.add("Level", key::level);
				crashReportSection.add("Dimension", (CrashCallable<String>)(() -> key.dimension().getValue().toString()));
				crashReportSection.add("Storage", key::type);
				crashReportSection.add("Position", pos::toString);
				report.writeToFile(path2, ReportType.MINECRAFT_CHUNK_IO_ERROR_REPORT);
				LOGGER.info("Saved details to {}", report.getFile());
			} catch (Exception var11) {
				LOGGER.warn("Failed to store chunk IO exception", (Throwable)var11);
			}
		});
	}

	@Override
	public void onChunkLoadFailure(Throwable exception, StorageKey key, ChunkPos chunkPos) {
		LOGGER.error("Failed to load chunk {},{}", chunkPos.x, chunkPos.z, exception);
		this.suppressedExceptionsTracker.onSuppressedException("chunk/load", exception);
		this.writeChunkIoReport(CrashReport.create(exception, "Chunk load failure"), chunkPos, key);
	}

	@Override
	public void onChunkSaveFailure(Throwable exception, StorageKey key, ChunkPos chunkPos) {
		LOGGER.error("Failed to save chunk {},{}", chunkPos.x, chunkPos.z, exception);
		this.suppressedExceptionsTracker.onSuppressedException("chunk/save", exception);
		this.writeChunkIoReport(CrashReport.create(exception, "Chunk save failure"), chunkPos, key);
	}

	public void onPacketException(Throwable exception, PacketType<?> type) {
		this.suppressedExceptionsTracker.onSuppressedException("packet/" + type.toString(), exception);
	}

	public BrewingRecipeRegistry getBrewingRecipeRegistry() {
		return this.brewingRecipeRegistry;
	}

	public FuelRegistry getFuelRegistry() {
		return this.fuelRegistry;
	}

	public ServerLinks getServerLinks() {
		return ServerLinks.EMPTY;
	}

	protected int getPauseWhenEmptySeconds() {
		return 0;
	}

	static class DebugStart {
		final long time;
		final int tick;

		DebugStart(long time, int tick) {
			this.time = time;
			this.tick = tick;
		}

		ProfileResult end(long endTime, int endTick) {
			return new ProfileResult() {
				@Override
				public List<ProfilerTiming> getTimings(String parentPath) {
					return Collections.emptyList();
				}

				@Override
				public boolean save(Path path) {
					return false;
				}

				@Override
				public long getStartTime() {
					return DebugStart.this.time;
				}

				@Override
				public int getStartTick() {
					return DebugStart.this.tick;
				}

				@Override
				public long getEndTime() {
					return endTime;
				}

				@Override
				public int getEndTick() {
					return endTick;
				}

				@Override
				public String getRootTimings() {
					return "";
				}
			};
		}
	}

	static record ResourceManagerHolder(LifecycledResourceManager resourceManager, DataPackContents dataPackContents) implements AutoCloseable {

		public void close() {
			this.resourceManager.close();
		}
	}

	public static record ServerResourcePackProperties(UUID id, String url, String hash, boolean isRequired, @Nullable Text prompt) {
	}
}
