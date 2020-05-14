/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
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
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import javax.imageio.ImageIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.class_5217;
import net.minecraft.class_5218;
import net.minecraft.class_5219;
import net.minecraft.class_5268;
import net.minecraft.class_5285;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.scoreboard.ScoreboardState;
import net.minecraft.scoreboard.ScoreboardSynchronizer;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.server.ServerTask;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.SecondaryServerWorld;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.RegistryTagManager;
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
import net.minecraft.util.registry.Registry;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class MinecraftServer
extends ReentrantThreadExecutor<ServerTask>
implements SnooperListener,
CommandOutput,
AutoCloseable,
Runnable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final File USER_CACHE_FILE = new File("usercache.json");
    private static final CompletableFuture<Unit> COMPLETED_UNIT_FUTURE = CompletableFuture.completedFuture(Unit.INSTANCE);
    public static final LevelInfo DEMO_LEVEL_INFO = new LevelInfo("Demo World", GameMode.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), class_5285.field_24520);
    protected final LevelStorage.Session session;
    protected final WorldSaveHandler field_24371;
    private final Snooper snooper = new Snooper("server", this, Util.getMeasuringTimeMs());
    private final List<Runnable> serverGuiTickables = Lists.newArrayList();
    private TickTimeTracker tickTimeTracker = new TickTimeTracker(Util.nanoTimeSupplier, this::getTicks);
    private Profiler profiler = DummyProfiler.INSTANCE;
    private final ServerNetworkIo networkIo;
    protected final WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;
    private final ServerMetadata metadata = new ServerMetadata();
    private final Random random = new Random();
    private final DataFixer dataFixer;
    private String serverIp;
    private int serverPort = -1;
    private final Map<DimensionType, ServerWorld> worlds = Maps.newIdentityHashMap();
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
    private int worldHeight;
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
    private boolean forceGameMode;
    private final MinecraftSessionService sessionService;
    private final GameProfileRepository gameProfileRepo;
    private final UserCache userCache;
    private long lastPlayerSampleUpdate;
    protected final Thread serverThread = Util.make(new Thread((Runnable)this, "Server thread"), thread2 -> thread2.setUncaughtExceptionHandler((thread, throwable) -> LOGGER.error(throwable)));
    private long timeReference = Util.getMeasuringTimeMs();
    private long field_19248;
    private boolean waitingForNextTick;
    @Environment(value=EnvType.CLIENT)
    private boolean iconFilePresent;
    private final ReloadableResourceManager dataManager = new ReloadableResourceManagerImpl(ResourceType.SERVER_DATA, this.serverThread);
    private final ResourcePackManager<ResourcePackProfile> dataPackManager = new ResourcePackManager<ResourcePackProfile>(ResourcePackProfile::new);
    @Nullable
    private FileResourcePackProvider fileDataPackProvider;
    private final CommandManager commandManager;
    private final RecipeManager recipeManager = new RecipeManager();
    private final RegistryTagManager tagManager = new RegistryTagManager();
    private final ServerScoreboard scoreboard = new ServerScoreboard(this);
    @Nullable
    private DataCommandStorage dataCommandStorage;
    private final BossBarManager bossBarManager = new BossBarManager(this);
    private final LootConditionManager predicateManager = new LootConditionManager();
    private final LootManager lootManager = new LootManager(this.predicateManager);
    private final ServerAdvancementLoader advancementLoader = new ServerAdvancementLoader(this.predicateManager);
    private final CommandFunctionManager commandFunctionManager = new CommandFunctionManager(this);
    private final MetricsData metricsData = new MetricsData();
    private boolean enforceWhitelist;
    private float tickTime;
    private final Executor workerExecutor;
    @Nullable
    private String serverId;
    private final StructureManager structureManager;
    protected final class_5219 field_24372;

    public MinecraftServer(LevelStorage.Session session, class_5219 arg, Proxy proxy, DataFixer dataFixer, CommandManager commandManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super("Server");
        this.field_24372 = arg;
        this.proxy = proxy;
        this.commandManager = commandManager;
        this.sessionService = minecraftSessionService;
        this.gameProfileRepo = gameProfileRepository;
        this.userCache = userCache;
        this.networkIo = new ServerNetworkIo(this);
        this.worldGenerationProgressListenerFactory = worldGenerationProgressListenerFactory;
        this.session = session;
        this.field_24371 = session.method_27427();
        this.dataFixer = dataFixer;
        this.dataManager.registerListener(this.tagManager);
        this.dataManager.registerListener(this.predicateManager);
        this.dataManager.registerListener(this.recipeManager);
        this.dataManager.registerListener(this.lootManager);
        this.dataManager.registerListener(this.commandFunctionManager);
        this.dataManager.registerListener(this.advancementLoader);
        this.workerExecutor = Util.getServerWorkerExecutor();
        this.structureManager = new StructureManager(this, session, dataFixer);
    }

    private void initScoreboard(PersistentStateManager persistentStateManager) {
        ScoreboardState scoreboardState = persistentStateManager.getOrCreate(ScoreboardState::new, "scoreboard");
        scoreboardState.setScoreboard(this.getScoreboard());
        this.getScoreboard().addUpdateListener(new ScoreboardSynchronizer(scoreboardState));
    }

    protected abstract boolean setupServer() throws IOException;

    public static void method_27725(LevelStorage.Session session, DataFixer dataFixer, boolean bl, boolean bl2, BooleanSupplier booleanSupplier) {
        if (session.needsConversion()) {
            LOGGER.info("Converting map!");
            session.convert(new ProgressListener(){
                private long lastProgressUpdate = Util.getMeasuringTimeMs();

                @Override
                public void method_15412(Text text) {
                }

                @Override
                @Environment(value=EnvType.CLIENT)
                public void method_15413(Text text) {
                }

                @Override
                public void progressStagePercentage(int i) {
                    if (Util.getMeasuringTimeMs() - this.lastProgressUpdate >= 1000L) {
                        this.lastProgressUpdate = Util.getMeasuringTimeMs();
                        LOGGER.info("Converting... {}%", (Object)i);
                    }
                }

                @Override
                @Environment(value=EnvType.CLIENT)
                public void setDone() {
                }

                @Override
                public void method_15414(Text text) {
                }
            });
        }
        if (bl) {
            LOGGER.info("Forcing world upgrade!");
            class_5219 lv = session.readLevelProperties();
            if (lv != null) {
                WorldUpdater worldUpdater = new WorldUpdater(session, dataFixer, lv, bl2);
                Text text = null;
                while (!worldUpdater.isDone()) {
                    int i;
                    Text text2 = worldUpdater.getStatus();
                    if (text != text2) {
                        text = text2;
                        LOGGER.info(worldUpdater.getStatus().getString());
                    }
                    if ((i = worldUpdater.getTotalChunkCount()) > 0) {
                        int j = worldUpdater.getUpgradedChunkCount() + worldUpdater.getSkippedChunkCount();
                        LOGGER.info("{}% completed ({} / {} chunks)...", (Object)MathHelper.floor((float)j / (float)i * 100.0f), (Object)j, (Object)i);
                    }
                    if (!booleanSupplier.getAsBoolean()) {
                        worldUpdater.cancel();
                        continue;
                    }
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException interruptedException) {}
                }
            }
        }
    }

    protected void loadWorld() {
        this.loadWorldResourcePack();
        this.field_24372.addServerBrand(this.getServerModName(), this.getModdedStatusMessage().isPresent());
        this.loadWorldDataPacks();
        WorldGenerationProgressListener worldGenerationProgressListener = this.worldGenerationProgressListenerFactory.create(11);
        this.createWorlds(worldGenerationProgressListener);
        this.method_27731();
        this.prepareStartRegion(worldGenerationProgressListener);
    }

    protected void method_27731() {
    }

    protected void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener) {
        class_5268 lv = this.field_24372.method_27859();
        class_5285 lv2 = this.field_24372.method_28057();
        boolean bl = lv2.method_28033();
        long l = lv2.method_28028();
        long m = BiomeAccess.hashSeed(l);
        ServerWorld serverWorld = new ServerWorld(this, this.workerExecutor, this.session, lv, DimensionType.OVERWORLD, worldGenerationProgressListener, lv2.method_28032(), bl, m);
        this.worlds.put(DimensionType.OVERWORLD, serverWorld);
        PersistentStateManager persistentStateManager = serverWorld.getPersistentStateManager();
        this.initScoreboard(persistentStateManager);
        this.dataCommandStorage = new DataCommandStorage(persistentStateManager);
        serverWorld.getWorldBorder().load(lv.method_27422());
        ServerWorld serverWorld2 = this.getWorld(DimensionType.OVERWORLD);
        if (!lv.isInitialized()) {
            try {
                MinecraftServer.method_27901(serverWorld2, serverWorld2.getDimension(), lv, lv2.method_28030(), bl);
                lv.setInitialized(true);
                if (bl) {
                    this.setToDebugWorldProperties(this.field_24372);
                }
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Exception initializing level");
                try {
                    serverWorld2.addDetailsToCrashReport(crashReport);
                } catch (Throwable throwable2) {
                    // empty catch block
                }
                throw new CrashException(crashReport);
            }
            lv.setInitialized(true);
        }
        this.getPlayerManager().setMainWorld(serverWorld2);
        if (this.field_24372.getCustomBossEvents() != null) {
            this.getBossBarManager().fromTag(this.field_24372.getCustomBossEvents());
        }
        for (Map.Entry<DimensionType, ChunkGenerator> entry : lv2.method_28031().entrySet()) {
            DimensionType dimensionType = entry.getKey();
            if (dimensionType == DimensionType.OVERWORLD) continue;
            this.worlds.put(dimensionType, new SecondaryServerWorld(serverWorld2, this.field_24372.method_27859(), this, this.workerExecutor, this.session, dimensionType, worldGenerationProgressListener, entry.getValue(), bl, m));
        }
    }

    private static void method_27901(ServerWorld serverWorld, Dimension dimension, class_5268 arg, boolean bl, boolean bl2) {
        ChunkPos chunkPos;
        ChunkGenerator chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
        if (!dimension.canPlayersSleep()) {
            arg.setSpawnPos(BlockPos.ORIGIN.up(chunkGenerator.getSpawnHeight()));
            return;
        }
        if (bl2) {
            arg.setSpawnPos(BlockPos.ORIGIN.up());
            return;
        }
        BiomeSource biomeSource = chunkGenerator.getBiomeSource();
        List<Biome> list = biomeSource.getSpawnBiomes();
        Random random = new Random(serverWorld.getSeed());
        BlockPos blockPos = biomeSource.locateBiome(0, serverWorld.getSeaLevel(), 0, 256, list, random);
        ChunkPos chunkPos2 = chunkPos = blockPos == null ? new ChunkPos(0, 0) : new ChunkPos(blockPos);
        if (blockPos == null) {
            LOGGER.warn("Unable to find spawn biome");
        }
        boolean bl3 = false;
        for (Block block : BlockTags.VALID_SPAWN.values()) {
            if (!biomeSource.getTopMaterials().contains(block.getDefaultState())) continue;
            bl3 = true;
            break;
        }
        arg.setSpawnPos(chunkPos.getCenterBlockPos().add(8, chunkGenerator.getSpawnHeight(), 8));
        int i = 0;
        int j = 0;
        int k = 0;
        int l = -1;
        int m = 32;
        for (int n = 0; n < 1024; ++n) {
            BlockPos blockPos2;
            if (i > -16 && i <= 16 && j > -16 && j <= 16 && (blockPos2 = dimension.getSpawningBlockInChunk(serverWorld.getSeed(), new ChunkPos(chunkPos.x + i, chunkPos.z + j), bl3)) != null) {
                arg.setSpawnPos(blockPos2);
                break;
            }
            if (i == j || i < 0 && i == -j || i > 0 && i == 1 - j) {
                int o = k;
                k = -l;
                l = o;
            }
            i += k;
            j += l;
        }
        if (bl) {
            ConfiguredFeature<DefaultFeatureConfig, ?> configuredFeature = Feature.BONUS_CHEST.configure(FeatureConfig.DEFAULT);
            configuredFeature.generate(serverWorld, serverWorld.getStructureAccessor(), chunkGenerator, serverWorld.random, new BlockPos(arg.getSpawnX(), arg.getSpawnY(), arg.getSpawnZ()));
        }
    }

    private void setToDebugWorldProperties(class_5219 properties) {
        properties.setDifficulty(Difficulty.PEACEFUL);
        properties.setDifficultyLocked(true);
        class_5268 lv = properties.method_27859();
        lv.setRaining(false);
        lv.setThundering(false);
        lv.setClearWeatherTime(1000000000);
        lv.setTimeOfDay(6000L);
        lv.setGameMode(GameMode.SPECTATOR);
    }

    private void loadWorldDataPacks() {
        this.dataPackManager.registerProvider(new VanillaDataPackProvider());
        this.fileDataPackProvider = new FileResourcePackProvider(this.session.getDirectory(class_5218.field_24186).toFile());
        this.dataPackManager.registerProvider(this.fileDataPackProvider);
        this.dataPackManager.scanPacks();
        ArrayList<ResourcePackProfile> list = Lists.newArrayList();
        for (String string : this.field_24372.getEnabledDataPacks()) {
            ResourcePackProfile resourcePackProfile = this.dataPackManager.getProfile(string);
            if (resourcePackProfile != null) {
                list.add(resourcePackProfile);
                continue;
            }
            LOGGER.warn("Missing data pack {}", (Object)string);
        }
        this.dataPackManager.setEnabledProfiles(list);
        this.reloadDataPacks();
        this.method_24154();
    }

    private void prepareStartRegion(WorldGenerationProgressListener worldGenerationProgressListener) {
        ServerWorld serverWorld = this.getWorld(DimensionType.OVERWORLD);
        LOGGER.info("Preparing start region for dimension " + DimensionType.getId(serverWorld.method_27983()));
        BlockPos blockPos = serverWorld.method_27911();
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
        for (DimensionType dimensionType : DimensionType.getAll()) {
            ForcedChunkState forcedChunkState = this.getWorld(dimensionType).getPersistentStateManager().get(ForcedChunkState::new, "chunks");
            if (forcedChunkState == null) continue;
            ServerWorld serverWorld2 = this.getWorld(dimensionType);
            LongIterator longIterator = forcedChunkState.getChunks().iterator();
            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                ChunkPos chunkPos = new ChunkPos(l);
                serverWorld2.getChunkManager().setChunkForced(chunkPos, true);
            }
        }
        this.timeReference = Util.getMeasuringTimeMs() + 10L;
        this.method_16208();
        worldGenerationProgressListener.stop();
        serverChunkManager.getLightingProvider().setTaskBatchSize(5);
        this.method_27729();
    }

    protected void loadWorldResourcePack() {
        File file = this.session.getDirectory(class_5218.field_24187).toFile();
        if (file.isFile()) {
            String string = this.session.getDirectoryName();
            try {
                this.setResourcePack("level://" + URLEncoder.encode(string, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
            } catch (UnsupportedEncodingException unsupportedEncodingException) {
                LOGGER.warn("Something went wrong url encoding {}", (Object)string);
            }
        }
    }

    public GameMode getDefaultGameMode() {
        return this.field_24372.getGameMode();
    }

    public boolean isHardcore() {
        return this.field_24372.isHardcore();
    }

    public abstract int getOpPermissionLevel();

    public abstract int getFunctionPermissionLevel();

    public abstract boolean shouldBroadcastRconToOps();

    public boolean save(boolean bl, boolean bl2, boolean bl3) {
        boolean bl4 = false;
        for (ServerWorld serverWorld : this.getWorlds()) {
            if (!bl) {
                LOGGER.info("Saving chunks for level '{}'/{}", (Object)serverWorld, (Object)DimensionType.getId(serverWorld.method_27983()));
            }
            serverWorld.save(null, bl2, serverWorld.savingDisabled && !bl3);
            bl4 = true;
        }
        ServerWorld serverWorld2 = this.getWorld(DimensionType.OVERWORLD);
        class_5268 lv = this.field_24372.method_27859();
        lv.method_27415(serverWorld2.getWorldBorder().method_27355());
        this.field_24372.setCustomBossEvents(this.getBossBarManager().toTag());
        this.session.method_27426(this.field_24372, this.getPlayerManager().getUserData());
        return bl4;
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
            if (serverWorld == null) continue;
            serverWorld.savingDisabled = false;
        }
        this.save(false, true, false);
        for (ServerWorld serverWorld : this.getWorlds()) {
            if (serverWorld == null) continue;
            try {
                serverWorld.close();
            } catch (IOException iOException) {
                LOGGER.error("Exception closing the level", (Throwable)iOException);
            }
        }
        if (this.snooper.isActive()) {
            this.snooper.cancel();
        }
        try {
            this.session.close();
        } catch (IOException iOException2) {
            LOGGER.error("Failed to unlock level {}", (Object)this.session.getDirectoryName(), (Object)iOException2);
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
            } catch (InterruptedException interruptedException) {
                LOGGER.error("Error while shutting down", (Throwable)interruptedException);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
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
                        LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", (Object)l, (Object)m);
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
                    this.field_19248 = Math.max(Util.getMeasuringTimeMs() + 50L, this.timeReference);
                    this.method_16208();
                    this.profiler.pop();
                    this.profiler.endTick();
                    this.endMonitor(tickDurationMonitor);
                    this.loading = true;
                }
            } else {
                this.setCrashReport(null);
            }
        } catch (Throwable throwable) {
            LOGGER.error("Encountered an unexpected exception", throwable);
            CrashReport crashReport = throwable instanceof CrashException ? this.populateCrashReport(((CrashException)throwable).getReport()) : this.populateCrashReport(new CrashReport("Exception in server tick loop", throwable));
            File file = new File(new File(this.getRunDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");
            if (crashReport.writeToFile(file)) {
                LOGGER.error("This crash report has been saved to: {}", (Object)file.getAbsolutePath());
            } else {
                LOGGER.error("We were unable to save this crash report to disk.");
            }
            this.setCrashReport(crashReport);
        } finally {
            try {
                this.stopped = true;
                this.shutdown();
            } catch (Throwable throwable) {
                LOGGER.error("Exception stopping the server", throwable);
            } finally {
                this.exit();
            }
        }
    }

    private boolean shouldKeepTicking() {
        return this.hasRunningTasks() || Util.getMeasuringTimeMs() < (this.waitingForNextTick ? this.field_19248 : this.timeReference);
    }

    protected void method_16208() {
        this.runTasks();
        this.runTasks(() -> !this.shouldKeepTicking());
    }

    @Override
    protected ServerTask createTask(Runnable runnable) {
        return new ServerTask(this.ticks, runnable);
    }

    @Override
    protected boolean canExecute(ServerTask serverTask) {
        return serverTask.getCreationTicks() + 3 < this.ticks || this.shouldKeepTicking();
    }

    @Override
    public boolean runTask() {
        boolean bl;
        this.waitingForNextTick = bl = this.method_20415();
        return bl;
    }

    private boolean method_20415() {
        if (super.runTask()) {
            return true;
        }
        if (this.shouldKeepTicking()) {
            for (ServerWorld serverWorld : this.getWorlds()) {
                if (!serverWorld.getChunkManager().executeQueuedTasks()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void executeTask(ServerTask serverTask) {
        this.getProfiler().visit("runTask");
        super.executeTask(serverTask);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setFavicon(ServerMetadata metadata) {
        File file = this.getFile("server-icon.png");
        if (!file.exists()) {
            file = this.session.getIconFile();
        }
        if (file.isFile()) {
            ByteBuf byteBuf = Unpooled.buffer();
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                ImageIO.write((RenderedImage)bufferedImage, "PNG", new ByteBufOutputStream(byteBuf));
                ByteBuffer byteBuffer = Base64.getEncoder().encode(byteBuf.nioBuffer());
                metadata.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(byteBuffer));
            } catch (Exception exception) {
                LOGGER.error("Couldn't load server icon", (Throwable)exception);
            } finally {
                byteBuf.release();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasIconFile() {
        this.iconFilePresent = this.iconFilePresent || this.getIconFile().isFile();
        return this.iconFilePresent;
    }

    @Environment(value=EnvType.CLIENT)
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
        ++this.ticks;
        this.tickWorlds(shouldKeepTicking);
        if (l - this.lastPlayerSampleUpdate >= 5000000000L) {
            this.lastPlayerSampleUpdate = l;
            this.metadata.setPlayers(new ServerMetadata.Players(this.getMaxPlayerCount(), this.getCurrentPlayerCount()));
            GameProfile[] gameProfiles = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
            int i = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - gameProfiles.length);
            for (int j = 0; j < gameProfiles.length; ++j) {
                gameProfiles[j] = this.playerManager.getPlayerList().get(i + j).getGameProfile();
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
        long l2 = Util.getMeasuringTimeNano() - l;
        this.lastTickLengths[this.ticks % 100] = l2;
        long m = l2;
        this.tickTime = this.tickTime * 0.8f + (float)m / 1000000.0f * 0.19999999f;
        long n = Util.getMeasuringTimeNano();
        this.metricsData.pushSample(n - l);
        this.profiler.pop();
    }

    protected void tickWorlds(BooleanSupplier shouldKeepTicking) {
        this.profiler.push("commandFunctions");
        this.getCommandFunctionManager().tick();
        this.profiler.swap("levels");
        for (ServerWorld serverWorld : this.getWorlds()) {
            if (serverWorld.method_27983() != DimensionType.OVERWORLD && !this.isNetherAllowed()) continue;
            this.profiler.push(() -> serverWorld + " " + Registry.DIMENSION_TYPE.getId(serverWorld.method_27983()));
            if (this.ticks % 20 == 0) {
                this.profiler.push("timeSync");
                this.playerManager.sendToDimension(new WorldTimeUpdateS2CPacket(serverWorld.getTime(), serverWorld.getTimeOfDay(), serverWorld.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), serverWorld.method_27983());
                this.profiler.pop();
            }
            this.profiler.push("tick");
            try {
                serverWorld.tick(shouldKeepTicking);
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Exception ticking world");
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
        for (int i = 0; i < this.serverGuiTickables.size(); ++i) {
            this.serverGuiTickables.get(i).run();
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

    public void start() {
        this.serverThread.start();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isStopping() {
        return !this.serverThread.isAlive();
    }

    public File getFile(String string) {
        return new File(this.getRunDirectory(), string);
    }

    public ServerWorld getWorld(DimensionType dimensionType) {
        return this.worlds.get(dimensionType);
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

    public CrashReport populateCrashReport(CrashReport crashReport) {
        if (this.playerManager != null) {
            crashReport.getSystemDetailsSection().add("Player Count", () -> this.playerManager.getCurrentPlayerCount() + " / " + this.playerManager.getMaxPlayerCount() + "; " + this.playerManager.getPlayerList());
        }
        crashReport.getSystemDetailsSection().add("Data Packs", () -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (ResourcePackProfile resourcePackProfile : this.dataPackManager.getEnabledProfiles()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(resourcePackProfile.getName());
                if (resourcePackProfile.getCompatibility().isCompatible()) continue;
                stringBuilder.append(" (incompatible)");
            }
            return stringBuilder.toString();
        });
        if (this.serverId != null) {
            crashReport.getSystemDetailsSection().add("Server Id", () -> this.serverId);
        }
        return crashReport;
    }

    public abstract Optional<String> getModdedStatusMessage();

    @Override
    public void sendSystemMessage(Text message) {
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

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public void setDifficulty(Difficulty difficulty, boolean bl) {
        if (!bl && this.field_24372.isDifficultyLocked()) {
            return;
        }
        this.field_24372.setDifficulty(this.field_24372.isHardcore() ? Difficulty.HARD : difficulty);
        this.method_27729();
        this.getPlayerManager().getPlayerList().forEach(this::sendDifficulty);
    }

    public int adjustTrackingDistance(int initialDistance) {
        return initialDistance;
    }

    private void method_27729() {
        for (ServerWorld serverWorld : this.getWorlds()) {
            serverWorld.setMobSpawnOptions(this.isMonsterSpawningEnabled(), this.shouldSpawnAnimals());
        }
    }

    public void setDifficultyLocked(boolean locked) {
        this.field_24372.setDifficultyLocked(locked);
        this.getPlayerManager().getPlayerList().forEach(this::sendDifficulty);
    }

    private void sendDifficulty(ServerPlayerEntity player) {
        class_5217 lv = player.getServerWorld().getLevelProperties();
        player.networkHandler.sendPacket(new DifficultyS2CPacket(lv.getDifficulty(), lv.isDifficultyLocked()));
    }

    protected boolean isMonsterSpawningEnabled() {
        return this.field_24372.getDifficulty() != Difficulty.PEACEFUL;
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
            snooper.addInfo("players_seen", this.field_24371.getSavedPlayerIds().length);
        }
        snooper.addInfo("uses_auth", this.onlineMode);
        snooper.addInfo("gui_state", this.hasGui() ? "enabled" : "disabled");
        snooper.addInfo("run_time", (Util.getMeasuringTimeMs() - snooper.getStartTime()) / 60L * 1000L);
        snooper.addInfo("avg_tick_ms", (int)(MathHelper.average(this.lastTickLengths) * 1.0E-6));
        int i = 0;
        for (ServerWorld serverWorld : this.getWorlds()) {
            if (serverWorld == null) continue;
            snooper.addInfo("world[" + i + "][dimension]", serverWorld.method_27983());
            snooper.addInfo("world[" + i + "][mode]", (Object)this.field_24372.getGameMode());
            snooper.addInfo("world[" + i + "][difficulty]", (Object)serverWorld.getDifficulty());
            snooper.addInfo("world[" + i + "][hardcore]", this.field_24372.isHardcore());
            snooper.addInfo("world[" + i + "][height]", this.worldHeight);
            snooper.addInfo("world[" + i + "][chunks_loaded]", serverWorld.getChunkManager().getLoadedChunkCount());
            ++i;
        }
        snooper.addInfo("worlds", i);
    }

    public abstract boolean isDedicated();

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

    public abstract boolean areCommandBlocksEnabled();

    public String getServerMotd() {
        return this.motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public int getWorldHeight() {
        return this.worldHeight;
    }

    public void setWorldHeight(int worldHeight) {
        this.worldHeight = worldHeight;
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
        this.field_24372.setGameMode(gameMode);
    }

    @Nullable
    public ServerNetworkIo getNetworkIo() {
        return this.networkIo;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLoading() {
        return this.loading;
    }

    public boolean hasGui() {
        return false;
    }

    public abstract boolean openToLan(GameMode var1, boolean var2, int var3);

    public int getTicks() {
        return this.ticks;
    }

    @Environment(value=EnvType.CLIENT)
    public Snooper getSnooper() {
        return this.snooper;
    }

    public int getSpawnProtectionRadius() {
        return 16;
    }

    public boolean isSpawnProtected(ServerWorld serverWorld, BlockPos pos, PlayerEntity player) {
        return false;
    }

    public void setForceGameMode(boolean forceGameMode) {
        this.forceGameMode = forceGameMode;
    }

    public boolean shouldForceGameMode() {
        return this.forceGameMode;
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
        if (world != null) {
            return world.getGameRules().getInt(GameRules.SPAWN_RADIUS);
        }
        return 10;
    }

    public ServerAdvancementLoader getAdvancementLoader() {
        return this.advancementLoader;
    }

    public CommandFunctionManager getCommandFunctionManager() {
        return this.commandFunctionManager;
    }

    public void reload() {
        if (!this.isOnThread()) {
            this.execute(this::reload);
            return;
        }
        this.getPlayerManager().saveAllPlayerData();
        this.dataPackManager.scanPacks();
        this.reloadDataPacks();
        this.getPlayerManager().onDataPacksReloaded();
        this.method_24154();
    }

    private void reloadDataPacks() {
        ArrayList<ResourcePackProfile> list = Lists.newArrayList(this.dataPackManager.getEnabledProfiles());
        for (ResourcePackProfile resourcePackProfile2 : this.dataPackManager.getProfiles()) {
            if (this.field_24372.getDisabledDataPacks().contains(resourcePackProfile2.getName()) || list.contains(resourcePackProfile2)) continue;
            LOGGER.info("Found new data pack {}, loading it automatically", (Object)resourcePackProfile2.getName());
            resourcePackProfile2.getInitialPosition().insert(list, resourcePackProfile2, resourcePackProfile -> resourcePackProfile, false);
        }
        this.dataPackManager.setEnabledProfiles(list);
        ArrayList<ResourcePack> list2 = Lists.newArrayList();
        this.dataPackManager.getEnabledProfiles().forEach(resourcePackProfile -> list2.add(resourcePackProfile.createResourcePack()));
        CompletableFuture<Unit> completableFuture = this.dataManager.beginReload(this.workerExecutor, this, list2, COMPLETED_UNIT_FUTURE);
        this.runTasks(completableFuture::isDone);
        try {
            completableFuture.get();
        } catch (Exception exception) {
            LOGGER.error("Failed to reload data packs", (Throwable)exception);
        }
        this.field_24372.getEnabledDataPacks().clear();
        this.field_24372.getDisabledDataPacks().clear();
        this.dataPackManager.getEnabledProfiles().forEach(resourcePackProfile -> this.field_24372.getEnabledDataPacks().add(resourcePackProfile.getName()));
        this.dataPackManager.getProfiles().forEach(resourcePackProfile -> {
            if (!this.dataPackManager.getEnabledProfiles().contains(resourcePackProfile)) {
                this.field_24372.getDisabledDataPacks().add(resourcePackProfile.getName());
            }
        });
    }

    public void kickNonWhitelistedPlayers(ServerCommandSource source) {
        if (!this.isEnforceWhitelist()) {
            return;
        }
        PlayerManager playerManager = source.getMinecraftServer().getPlayerManager();
        Whitelist whitelist = playerManager.getWhitelist();
        if (!whitelist.isEnabled()) {
            return;
        }
        ArrayList<ServerPlayerEntity> list = Lists.newArrayList(playerManager.getPlayerList());
        for (ServerPlayerEntity serverPlayerEntity : list) {
            if (whitelist.isAllowed(serverPlayerEntity.getGameProfile())) continue;
            serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.not_whitelisted"));
        }
    }

    public ReloadableResourceManager getDataManager() {
        return this.dataManager;
    }

    public ResourcePackManager<ResourcePackProfile> getDataPackManager() {
        return this.dataPackManager;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public ServerCommandSource getCommandSource() {
        return new ServerCommandSource(this, this.getWorld(DimensionType.OVERWORLD) == null ? Vec3d.ZERO : Vec3d.of(this.getWorld(DimensionType.OVERWORLD).method_27911()), Vec2f.ZERO, this.getWorld(DimensionType.OVERWORLD), 4, "Server", new LiteralText("Server"), this, null);
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
        return this.recipeManager;
    }

    public RegistryTagManager getTagManager() {
        return this.tagManager;
    }

    public ServerScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public DataCommandStorage getDataCommandStorage() {
        if (this.dataCommandStorage == null) {
            throw new NullPointerException("Called before server init");
        }
        return this.dataCommandStorage;
    }

    public LootManager getLootManager() {
        return this.lootManager;
    }

    public LootConditionManager getPredicateManager() {
        return this.predicateManager;
    }

    public GameRules getGameRules() {
        return this.getWorld(DimensionType.OVERWORLD).getGameRules();
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
            OperatorEntry operatorEntry = (OperatorEntry)this.getPlayerManager().getOpList().get(profile);
            if (operatorEntry != null) {
                return operatorEntry.getPermissionLevel();
            }
            if (this.isHost(profile)) {
                return 4;
            }
            if (this.isSinglePlayer()) {
                return this.getPlayerManager().areCheatsAllowed() ? 4 : 0;
            }
            return this.getOpPermissionLevel();
        }
        return 0;
    }

    @Environment(value=EnvType.CLIENT)
    public MetricsData getMetricsData() {
        return this.metricsData;
    }

    public Profiler getProfiler() {
        return this.profiler;
    }

    public Executor getWorkerExecutor() {
        return this.workerExecutor;
    }

    public abstract boolean isHost(GameProfile var1);

    public void dump(Path path) throws IOException {
        Path path2 = path.resolve("levels");
        for (Map.Entry<DimensionType, ServerWorld> entry : this.worlds.entrySet()) {
            Identifier identifier = DimensionType.getId(entry.getKey());
            Path path3 = path2.resolve(identifier.getNamespace()).resolve(identifier.getPath());
            Files.createDirectories(path3, new FileAttribute[0]);
            entry.getValue().dump(path3);
        }
        this.dumpGamerules(path.resolve("gamerules.txt"));
        this.dumpClasspath(path.resolve("classpath.txt"));
        this.dumpExampleCrash(path.resolve("example_crash.txt"));
        this.dumpStats(path.resolve("stats.txt"));
        this.dumpThreads(path.resolve("threads.txt"));
    }

    private void dumpStats(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            writer.write(String.format("pending_tasks: %d\n", this.getTaskCount()));
            writer.write(String.format("average_tick_time: %f\n", Float.valueOf(this.getTickTime())));
            writer.write(String.format("tick_times: %s\n", Arrays.toString(this.lastTickLengths)));
            writer.write(String.format("queue: %s\n", Util.getServerWorkerExecutor()));
        }
    }

    private void dumpExampleCrash(Path path) throws IOException {
        CrashReport crashReport = new CrashReport("Server dump", new Exception("dummy"));
        this.populateCrashReport(crashReport);
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            writer.write(crashReport.asString());
        }
    }

    private void dumpGamerules(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            final ArrayList<String> list = Lists.newArrayList();
            final GameRules gameRules = this.getGameRules();
            GameRules.forEachType(new GameRules.RuleTypeConsumer(){

                @Override
                public <T extends GameRules.Rule<T>> void accept(GameRules.RuleKey<T> key, GameRules.RuleType<T> type) {
                    list.add(String.format("%s=%s\n", key.getName(), ((GameRules.Rule)gameRules.get(key)).toString()));
                }
            });
            for (String string : list) {
                writer.write(string);
            }
        }
    }

    private void dumpClasspath(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            String string = System.getProperty("java.class.path");
            String string2 = System.getProperty("path.separator");
            for (String string3 : Splitter.on(string2).split(string)) {
                writer.write(string3);
                writer.write("\n");
            }
        }
    }

    private void dumpThreads(Path path) throws IOException {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
        Arrays.sort(threadInfos, Comparator.comparing(ThreadInfo::getThreadName));
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            for (ThreadInfo threadInfo : threadInfos) {
                writer.write(threadInfo.toString());
                ((Writer)writer).write(10);
            }
        }
    }

    private void method_24154() {
        Blocks.refreshShapeCache();
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

    public Path method_27050(class_5218 arg) {
        return this.session.getDirectory(arg);
    }

    public boolean syncChunkWrites() {
        return true;
    }

    public StructureManager getStructureManager() {
        return this.structureManager;
    }

    public class_5219 method_27728() {
        return this.field_24372;
    }

    @Override
    public /* synthetic */ void executeTask(Runnable task) {
        this.executeTask((ServerTask)task);
    }

    @Override
    public /* synthetic */ boolean canExecute(Runnable task) {
        return this.canExecute((ServerTask)task);
    }

    @Override
    public /* synthetic */ Runnable createTask(Runnable runnable) {
        return this.createTask(runnable);
    }
}

