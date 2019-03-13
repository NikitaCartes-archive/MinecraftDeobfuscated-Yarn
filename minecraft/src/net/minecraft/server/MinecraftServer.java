package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.class_4093;
import net.minecraft.client.network.packet.DifficultyS2CPacket;
import net.minecraft.client.network.packet.WorldTimeUpdateS2CPacket;
import net.minecraft.datafixers.Schemas;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.player.ChunkTicketType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.DefaultResourcePackCreator;
import net.minecraft.resource.FileResourcePackCreator;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.scoreboard.ScoreboardState;
import net.minecraft.scoreboard.ScoreboardSynchronizer;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.config.OperatorEntry;
import net.minecraft.server.config.WhitelistList;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.SecondaryServerWorld;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.TagManager;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.MetricsData;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.UserCache;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.DisableableProfiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SessionLockException;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.loot.LootManager;
import net.minecraft.world.updater.WorldUpdater;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer extends class_4093<ServerTask> implements SnooperListener, CommandOutput, AutoCloseable, Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final File USER_CACHE_FILE = new File("usercache.json");
	public static final LevelInfo field_17704 = new LevelInfo((long)"North Carolina".hashCode(), GameMode.field_9215, true, false, LevelGeneratorType.DEFAULT)
		.setBonusChest();
	private final LevelStorage levelStorage;
	private final Snooper snooper = new Snooper("server", this, SystemUtil.getMeasuringTimeMs());
	private final File gameDir;
	private final List<Runnable> tickables = Lists.<Runnable>newArrayList();
	private final DisableableProfiler profiler = new DisableableProfiler(this::getTicks);
	private final ServerNetworkIO field_4563;
	protected final WorldGenerationProgressListenerFactory field_17439;
	private final ServerMetadata field_4593 = new ServerMetadata();
	private final Random random = new Random();
	private final DataFixer dataFixer;
	private String serverIp;
	private int serverPort = -1;
	private final Map<DimensionType, ServerWorld> worlds = Maps.<DimensionType, ServerWorld>newIdentityHashMap();
	private PlayerManager field_4550;
	private volatile boolean running = true;
	private boolean stopped;
	private int ticks;
	protected final Proxy field_4599;
	private boolean onlineMode;
	private boolean preventProxyConnections;
	private boolean spawnAnimals;
	private boolean spawnNpcs;
	private boolean pvpEnabled;
	private boolean flightEnabled;
	@Nullable
	private String motd;
	private int worldHeight;
	private int playerIdleTimeout;
	public final long[] lastTickLengths = new long[100];
	protected final Map<DimensionType, long[]> field_4600 = Maps.<DimensionType, long[]>newIdentityHashMap();
	@Nullable
	private KeyPair keyPair;
	@Nullable
	private String userName;
	private final String levelName;
	@Nullable
	@Environment(EnvType.CLIENT)
	private String displayName;
	private boolean demo;
	private boolean bonusChest;
	private String resourcePackUrl = "";
	private String resourcePackHash = "";
	private volatile boolean field_4547;
	private long field_4557;
	@Nullable
	private TextComponent field_4601;
	private boolean field_4597;
	private boolean forceGameMode;
	@Nullable
	private final YggdrasilAuthenticationService authService;
	private final MinecraftSessionService sessionService;
	private final GameProfileRepository gameProfileRepo;
	private final UserCache field_4556;
	private long field_4551;
	protected final Thread serverThread = SystemUtil.consume(
		new Thread(this, "Server thread"), thread -> thread.setUncaughtExceptionHandler((threadx, throwable) -> LOGGER.error(throwable))
	);
	private long timeReference = SystemUtil.getMeasuringTimeMs();
	@Environment(EnvType.CLIENT)
	private boolean iconFilePresent;
	private final ReloadableResourceManager field_4576 = new ReloadableResourceManagerImpl(ResourceType.DATA, this.serverThread);
	private final ResourcePackContainerManager<ResourcePackContainer> field_4595 = new ResourcePackContainerManager<>(ResourcePackContainer::new);
	@Nullable
	private FileResourcePackCreator field_4553;
	private final ServerCommandManager commandManager;
	private final RecipeManager recipeManager = new RecipeManager();
	private final TagManager field_4583 = new TagManager();
	private final ServerScoreboard field_4558 = new ServerScoreboard(this);
	private final BossBarManager field_4548 = new BossBarManager(this);
	private final LootManager lootManager = new LootManager();
	private final ServerAdvancementLoader field_4567 = new ServerAdvancementLoader();
	private final CommandFunctionManager field_4591 = new CommandFunctionManager(this);
	private final MetricsData field_16205 = new MetricsData();
	private boolean field_4570;
	private boolean forceWorldUpgrade;
	private float tickTime;
	private final Executor workerExecutor;
	@Nullable
	private String field_17601;

	public MinecraftServer(
		File file,
		Proxy proxy,
		DataFixer dataFixer,
		ServerCommandManager serverCommandManager,
		YggdrasilAuthenticationService yggdrasilAuthenticationService,
		MinecraftSessionService minecraftSessionService,
		GameProfileRepository gameProfileRepository,
		UserCache userCache,
		WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory,
		String string
	) {
		super("Server");
		this.field_4599 = proxy;
		this.commandManager = serverCommandManager;
		this.authService = yggdrasilAuthenticationService;
		this.sessionService = minecraftSessionService;
		this.gameProfileRepo = gameProfileRepository;
		this.field_4556 = userCache;
		this.gameDir = file;
		this.field_4563 = new ServerNetworkIO(this);
		this.field_17439 = worldGenerationProgressListenerFactory;
		this.levelStorage = new LevelStorage(file.toPath(), file.toPath().resolve("../backups"), dataFixer);
		this.dataFixer = dataFixer;
		this.field_4576.registerListener(this.field_4583);
		this.field_4576.registerListener(this.recipeManager);
		this.field_4576.registerListener(this.lootManager);
		this.field_4576.registerListener(this.field_4591);
		this.field_4576.registerListener(this.field_4567);
		this.workerExecutor = SystemUtil.getServerWorkerExecutor();
		this.levelName = string;
	}

	private void method_17976(PersistentStateManager persistentStateManager) {
		ScoreboardState scoreboardState = persistentStateManager.getOrCreate(ScoreboardState::new, "scoreboard");
		scoreboardState.setScoreboard(this.method_3845());
		this.method_3845().method_12935(new ScoreboardSynchronizer(scoreboardState));
	}

	protected abstract boolean setupServer() throws IOException;

	protected void method_3755(String string) {
		if (this.getLevelStorage().requiresConversion(string)) {
			LOGGER.info("Converting map!");
			this.method_3768(new TranslatableTextComponent("menu.convertingLevel"));
			this.getLevelStorage().method_17927(string, new ProgressListener() {
				private long lastProgressUpdate = SystemUtil.getMeasuringTimeMs();

				@Override
				public void method_15412(TextComponent textComponent) {
				}

				@Environment(EnvType.CLIENT)
				@Override
				public void method_15413(TextComponent textComponent) {
				}

				@Override
				public void progressStagePercentage(int i) {
					if (SystemUtil.getMeasuringTimeMs() - this.lastProgressUpdate >= 1000L) {
						this.lastProgressUpdate = SystemUtil.getMeasuringTimeMs();
						MinecraftServer.LOGGER.info("Converting... {}%", i);
					}
				}

				@Environment(EnvType.CLIENT)
				@Override
				public void setDone() {
				}

				@Override
				public void method_15414(TextComponent textComponent) {
				}
			});
		}

		if (this.forceWorldUpgrade) {
			LOGGER.info("Forcing world upgrade!");
			LevelProperties levelProperties = this.getLevelStorage().getLevelProperties(this.getLevelName());
			if (levelProperties != null) {
				WorldUpdater worldUpdater = new WorldUpdater(this.getLevelName(), this.getLevelStorage(), levelProperties);
				TextComponent textComponent = null;

				while (!worldUpdater.isDone()) {
					TextComponent textComponent2 = worldUpdater.method_5394();
					if (textComponent != textComponent2) {
						textComponent = textComponent2;
						LOGGER.info(worldUpdater.method_5394().getString());
					}

					int i = worldUpdater.getTotalChunkCount();
					if (i > 0) {
						int j = worldUpdater.getUpgradedChunkCount() + worldUpdater.getSkippedChunkCount();
						LOGGER.info("{}% completed ({} / {} chunks)...", MathHelper.floor((float)j / (float)i * 100.0F), j, i);
					}

					if (this.isStopped()) {
						worldUpdater.cancel();
					} else {
						try {
							Thread.sleep(1000L);
						} catch (InterruptedException var8) {
						}
					}
				}
			}
		}
	}

	protected synchronized void method_3768(TextComponent textComponent) {
		this.field_4601 = textComponent;
	}

	protected void method_3735(String string, String string2, long l, LevelGeneratorType levelGeneratorType, JsonElement jsonElement) {
		this.method_3755(string);
		this.method_3768(new TranslatableTextComponent("menu.loadingLevel"));
		WorldSaveHandler worldSaveHandler = this.getLevelStorage().method_242(string, this);
		this.method_3861(this.getLevelName(), worldSaveHandler);
		LevelProperties levelProperties = worldSaveHandler.readProperties();
		LevelInfo levelInfo;
		if (levelProperties == null) {
			if (this.isDemo()) {
				levelInfo = field_17704;
			} else {
				levelInfo = new LevelInfo(l, this.getDefaultGameMode(), this.shouldGenerateStructures(), this.isHardcore(), levelGeneratorType);
				levelInfo.setGeneratorOptions(jsonElement);
				if (this.bonusChest) {
					levelInfo.setBonusChest();
				}
			}

			levelProperties = new LevelProperties(levelInfo, string2);
		} else {
			levelProperties.setLevelName(string2);
			levelInfo = new LevelInfo(levelProperties);
		}

		this.method_3800(worldSaveHandler.getWorldDir(), levelProperties);
		WorldGenerationProgressListener worldGenerationProgressListener = this.field_17439.create(11);
		this.method_3786(worldSaveHandler, levelProperties, levelInfo, worldGenerationProgressListener);
		this.setDifficulty(this.getDefaultDifficulty(), true);
		this.method_3774(worldGenerationProgressListener);
	}

	protected void method_3786(
		WorldSaveHandler worldSaveHandler, LevelProperties levelProperties, LevelInfo levelInfo, WorldGenerationProgressListener worldGenerationProgressListener
	) {
		if (this.isDemo()) {
			levelProperties.loadLevelInfo(field_17704);
		}

		ServerWorld serverWorld = new ServerWorld(
			this, this.workerExecutor, worldSaveHandler, levelProperties, DimensionType.field_13072, this.profiler, worldGenerationProgressListener
		);
		this.worlds.put(DimensionType.field_13072, serverWorld);
		this.method_17976(serverWorld.getPersistentStateManager());
		serverWorld.method_8621().method_17905(levelProperties);
		ServerWorld serverWorld2 = this.method_3847(DimensionType.field_13072);
		if (!levelProperties.isInitialized()) {
			try {
				serverWorld2.init(levelInfo);
				if (levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
					this.method_17977(levelProperties);
				}

				levelProperties.setInitialized(true);
			} catch (Throwable var11) {
				CrashReport crashReport = CrashReport.create(var11, "Exception initializing level");

				try {
					serverWorld2.method_8538(crashReport);
				} catch (Throwable var10) {
				}

				throw new CrashException(crashReport);
			}

			levelProperties.setInitialized(true);
		}

		this.method_3760().method_14591(serverWorld2);
		if (levelProperties.method_228() != null) {
			this.method_3837().fromTag(levelProperties.method_228());
		}

		for (DimensionType dimensionType : DimensionType.getAll()) {
			if (dimensionType != DimensionType.field_13072) {
				this.worlds
					.put(
						dimensionType,
						new SecondaryServerWorld(serverWorld2, this, this.workerExecutor, worldSaveHandler, dimensionType, this.profiler, worldGenerationProgressListener)
					);
			}
		}
	}

	private void method_17977(LevelProperties levelProperties) {
		levelProperties.setStructures(false);
		levelProperties.setCommandsAllowed(true);
		levelProperties.setRaining(false);
		levelProperties.setThundering(false);
		levelProperties.setClearWeatherTime(1000000000);
		levelProperties.setTimeOfDay(6000L);
		levelProperties.setGameMode(GameMode.field_9219);
		levelProperties.setHardcore(false);
		levelProperties.setDifficulty(Difficulty.PEACEFUL);
		levelProperties.setDifficultyLocked(true);
		levelProperties.getGameRules().put("doDaylightCycle", "false", this);
	}

	protected void method_3800(File file, LevelProperties levelProperties) {
		this.field_4595.method_14443(new DefaultResourcePackCreator());
		this.field_4553 = new FileResourcePackCreator(new File(file, "datapacks"));
		this.field_4595.method_14443(this.field_4553);
		this.field_4595.callCreators();
		List<ResourcePackContainer> list = Lists.<ResourcePackContainer>newArrayList();

		for (String string : levelProperties.getEnabledDataPacks()) {
			ResourcePackContainer resourcePackContainer = this.field_4595.method_14449(string);
			if (resourcePackContainer != null) {
				list.add(resourcePackContainer);
			} else {
				LOGGER.warn("Missing data pack {}", string);
			}
		}

		this.field_4595.setEnabled(list);
		this.reloadDataPacks(levelProperties);
	}

	protected void method_3774(WorldGenerationProgressListener worldGenerationProgressListener) {
		this.method_3768(new TranslatableTextComponent("menu.generatingTerrain"));
		ServerWorld serverWorld = this.method_3847(DimensionType.field_13072);
		LOGGER.info("Preparing start region for dimension " + DimensionType.method_12485(serverWorld.field_9247.method_12460()));
		BlockPos blockPos = serverWorld.method_8395();
		worldGenerationProgressListener.start(new ChunkPos(blockPos));
		ServerChunkManager serverChunkManager = serverWorld.method_14178();
		serverChunkManager.method_17293().method_17304(500);
		this.timeReference = SystemUtil.getMeasuringTimeMs();
		serverChunkManager.method_17297(ChunkTicketType.START, new ChunkPos(blockPos), 11, net.minecraft.util.Void.INSTANCE);

		while (serverChunkManager.getTotalChunksLoadedCount() != 441) {
			this.timeReference += 100L;
			this.method_16208();
		}

		this.timeReference += 100L;
		this.method_16208();

		for (DimensionType dimensionType : DimensionType.getAll()) {
			ForcedChunkState forcedChunkState = this.method_3847(dimensionType).getPersistentStateManager().get(ForcedChunkState::new, "chunks");
			if (forcedChunkState != null) {
				ServerWorld serverWorld2 = this.method_3847(dimensionType);
				LongIterator longIterator = forcedChunkState.getChunks().iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					ChunkPos chunkPos = new ChunkPos(l);
					serverWorld2.method_14178().setChunkForced(chunkPos, true);
				}
			}
		}

		this.timeReference += 100L;
		this.method_16208();
		worldGenerationProgressListener.stop();
		serverChunkManager.method_17293().method_17304(5);
	}

	protected void method_3861(String string, WorldSaveHandler worldSaveHandler) {
		File file = new File(worldSaveHandler.getWorldDir(), "resources.zip");
		if (file.isFile()) {
			try {
				this.setResourcePack("level://" + URLEncoder.encode(string, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
			} catch (UnsupportedEncodingException var5) {
				LOGGER.warn("Something went wrong url encoding {}", string);
			}
		}
	}

	public abstract boolean shouldGenerateStructures();

	public abstract GameMode getDefaultGameMode();

	public abstract Difficulty getDefaultDifficulty();

	public abstract boolean isHardcore();

	public abstract int getOpPermissionLevel();

	public abstract boolean shouldBroadcastRconToOps();

	public boolean save(boolean bl, boolean bl2, boolean bl3) {
		boolean bl4 = false;

		for (ServerWorld serverWorld : this.getWorlds()) {
			if (!bl) {
				LOGGER.info("Saving chunks for level '{}'/{}", serverWorld.method_8401().getLevelName(), DimensionType.method_12485(serverWorld.field_9247.method_12460()));
			}

			try {
				serverWorld.method_14176(null, bl2, serverWorld.savingDisabled && !bl3);
			} catch (SessionLockException var8) {
				LOGGER.warn(var8.getMessage());
			}

			bl4 = true;
		}

		ServerWorld serverWorld2 = this.method_3847(DimensionType.field_13072);
		LevelProperties levelProperties = serverWorld2.method_8401();
		serverWorld2.method_8621().method_17904(levelProperties);
		levelProperties.method_221(this.method_3837().toTag());
		serverWorld2.getSaveHandler().method_131(levelProperties, this.method_3760().getUserData());
		return bl4;
	}

	@Override
	public void close() {
		this.shutdown();
	}

	protected void shutdown() {
		LOGGER.info("Stopping server");
		if (this.method_3787() != null) {
			this.method_3787().stop();
		}

		if (this.field_4550 != null) {
			LOGGER.info("Saving players");
			this.field_4550.saveAllPlayerData();
			this.field_4550.disconnectAllPlayers();
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
				} catch (IOException var4) {
					LOGGER.error("Exception closing the level", (Throwable)var4);
				}
			}
		}

		if (this.snooper.isActive()) {
			this.snooper.cancel();
		}
	}

	public String getServerIp() {
		return this.serverIp;
	}

	public void setServerIp(String string) {
		this.serverIp = string;
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

	public void run() {
		try {
			if (this.setupServer()) {
				this.timeReference = SystemUtil.getMeasuringTimeMs();
				this.field_4593.setDescription(new StringTextComponent(this.motd));
				this.field_4593.setVersion(new ServerMetadata.Version(SharedConstants.getGameVersion().getName(), SharedConstants.getGameVersion().getProtocolVersion()));
				this.method_3791(this.field_4593);

				while (this.running) {
					long l = SystemUtil.getMeasuringTimeMs() - this.timeReference;
					if (l > 2000L && this.timeReference - this.field_4557 >= 15000L) {
						long m = l / 50L;
						LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", l, m);
						this.timeReference += m * 50L;
						this.field_4557 = this.timeReference;
					}

					this.timeReference += 50L;
					if (this.field_4597) {
						this.field_4597 = false;
						this.profiler.getController().enable();
					}

					this.profiler.startTick();
					this.profiler.push("tick");
					this.method_3748(this::shouldKeepTicking);
					this.profiler.swap("nextTickWait");
					this.method_16208();
					this.profiler.pop();
					this.profiler.endTick();
					this.field_4547 = true;
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
		return this.method_18860() || SystemUtil.getMeasuringTimeMs() < this.timeReference;
	}

	protected void method_16208() {
		this.executeTaskQueue();
		this.method_18857(() -> !this.shouldKeepTicking());
	}

	protected ServerTask method_16209(Runnable runnable) {
		return new ServerTask(this.ticks, runnable);
	}

	protected boolean method_19464(ServerTask serverTask) {
		return serverTask.getCreationTicks() + 3 < this.ticks || this.shouldKeepTicking();
	}

	@Override
	public boolean executeQueuedTask() {
		if (super.executeQueuedTask()) {
			return true;
		} else {
			if (this.shouldKeepTicking()) {
				for (ServerWorld serverWorld : this.getWorlds()) {
					if (serverWorld.method_14178().method_19492()) {
						return true;
					}
				}
			}

			return false;
		}
	}

	public void method_3791(ServerMetadata serverMetadata) {
		File file = this.getFile("server-icon.png");
		if (!file.exists()) {
			file = this.getLevelStorage().resolveFile(this.getLevelName(), "icon.png");
		}

		if (file.isFile()) {
			ByteBuf byteBuf = Unpooled.buffer();

			try {
				BufferedImage bufferedImage = ImageIO.read(file);
				Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide");
				Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high");
				ImageIO.write(bufferedImage, "PNG", new ByteBufOutputStream(byteBuf));
				ByteBuffer byteBuffer = Base64.getEncoder().encode(byteBuf.nioBuffer());
				serverMetadata.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(byteBuffer));
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
		return this.getLevelStorage().resolveFile(this.getLevelName(), "icon.png");
	}

	public File getRunDirectory() {
		return new File(".");
	}

	protected void setCrashReport(CrashReport crashReport) {
	}

	protected void exit() {
	}

	protected void method_3748(BooleanSupplier booleanSupplier) {
		long l = SystemUtil.getMeasuringTimeNano();
		this.ticks++;
		this.tick(booleanSupplier);
		if (l - this.field_4551 >= 5000000000L) {
			this.field_4551 = l;
			this.field_4593.setPlayers(new ServerMetadata.Players(this.getMaxPlayerCount(), this.getCurrentPlayerCount()));
			GameProfile[] gameProfiles = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
			int i = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - gameProfiles.length);

			for (int j = 0; j < gameProfiles.length; j++) {
				gameProfiles[j] = ((ServerPlayerEntity)this.field_4550.getPlayerList().get(i + j)).getGameProfile();
			}

			Collections.shuffle(Arrays.asList(gameProfiles));
			this.field_4593.getPlayers().setSample(gameProfiles);
		}

		if (this.ticks % 900 == 0) {
			this.profiler.push("save");
			this.field_4550.saveAllPlayerData();
			this.save(true, false, false);
			this.profiler.pop();
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
		long m = this.lastTickLengths[this.ticks % 100] = SystemUtil.getMeasuringTimeNano() - l;
		this.tickTime = this.tickTime * 0.8F + (float)m / 1000000.0F * 0.19999999F;
		long n = SystemUtil.getMeasuringTimeNano();
		this.field_16205.pushSample(n - l);
		this.profiler.pop();
	}

	protected void tick(BooleanSupplier booleanSupplier) {
		this.profiler.push("commandFunctions");
		this.method_3740().method_18699();
		this.profiler.swap("levels");

		for (ServerWorld serverWorld : this.getWorlds()) {
			long l = SystemUtil.getMeasuringTimeNano();
			if (serverWorld.field_9247.method_12460() == DimensionType.field_13072 || this.isNetherAllowed()) {
				this.profiler
					.push((Supplier<String>)(() -> serverWorld.method_8401().getLevelName() + " " + Registry.DIMENSION.method_10221(serverWorld.field_9247.method_12460())));
				if (this.ticks % 20 == 0) {
					this.profiler.push("timeSync");
					this.field_4550
						.sendToDimension(
							new WorldTimeUpdateS2CPacket(serverWorld.getTime(), serverWorld.getTimeOfDay(), serverWorld.getGameRules().getBoolean("doDaylightCycle")),
							serverWorld.field_9247.method_12460()
						);
					this.profiler.pop();
				}

				this.profiler.push("tick");

				try {
					serverWorld.method_18765(booleanSupplier);
				} catch (Throwable var8) {
					CrashReport crashReport = CrashReport.create(var8, "Exception ticking world");
					serverWorld.method_8538(crashReport);
					throw new CrashException(crashReport);
				}

				this.profiler.pop();
				this.profiler.pop();
			}

			((long[])this.field_4600.computeIfAbsent(serverWorld.field_9247.method_12460(), dimensionType -> new long[100]))[this.ticks % 100] = SystemUtil.getMeasuringTimeNano()
				- l;
		}

		this.profiler.swap("connection");
		this.method_3787().tick();
		this.profiler.swap("players");
		this.field_4550.updatePlayerLatency();
		this.profiler.swap("server gui refresh");

		for (int i = 0; i < this.tickables.size(); i++) {
			((Runnable)this.tickables.get(i)).run();
		}

		this.profiler.pop();
	}

	public boolean isNetherAllowed() {
		return true;
	}

	public void registerTickable(Runnable runnable) {
		this.tickables.add(runnable);
	}

	public static void main(String[] strings) {
		OptionParser optionParser = new OptionParser();
		OptionSpec<Void> optionSpec = optionParser.accepts("nogui");
		OptionSpec<Void> optionSpec2 = optionParser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
		OptionSpec<Void> optionSpec3 = optionParser.accepts("demo");
		OptionSpec<Void> optionSpec4 = optionParser.accepts("bonusChest");
		OptionSpec<Void> optionSpec5 = optionParser.accepts("forceUpgrade");
		OptionSpec<Void> optionSpec6 = optionParser.accepts("help").forHelp();
		OptionSpec<String> optionSpec7 = optionParser.accepts("singleplayer").withRequiredArg();
		OptionSpec<String> optionSpec8 = optionParser.accepts("universe").withRequiredArg().defaultsTo(".");
		OptionSpec<String> optionSpec9 = optionParser.accepts("world").withRequiredArg();
		OptionSpec<Integer> optionSpec10 = optionParser.accepts("port").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(-1);
		OptionSpec<String> optionSpec11 = optionParser.accepts("serverId").withRequiredArg();
		OptionSpec<String> optionSpec12 = optionParser.nonOptions();

		try {
			OptionSet optionSet = optionParser.parse(strings);
			if (optionSet.has(optionSpec6)) {
				optionParser.printHelpOn(System.err);
				return;
			}

			Path path = Paths.get("server.properties");
			ServerPropertiesLoader serverPropertiesLoader = new ServerPropertiesLoader(path);
			serverPropertiesLoader.store();
			Path path2 = Paths.get("eula.txt");
			EulaReader eulaReader = new EulaReader(path2);
			if (optionSet.has(optionSpec2)) {
				LOGGER.info("Initialized '" + path.toAbsolutePath().toString() + "' and '" + path2.toAbsolutePath().toString() + "'");
				return;
			}

			if (!eulaReader.isEulaAgreedTo()) {
				LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
				return;
			}

			Bootstrap.initialize();
			Bootstrap.logMissingTranslations();
			String string = optionSet.valueOf(optionSpec8);
			YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
			MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
			GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
			UserCache userCache = new UserCache(gameProfileRepository, new File(string, USER_CACHE_FILE.getName()));
			String string2 = (String)Optional.ofNullable(optionSet.valueOf(optionSpec9)).orElse(serverPropertiesLoader.getPropertiesHandler().levelName);
			final MinecraftDedicatedServer minecraftDedicatedServer = new MinecraftDedicatedServer(
				new File(string),
				serverPropertiesLoader,
				Schemas.getFixer(),
				yggdrasilAuthenticationService,
				minecraftSessionService,
				gameProfileRepository,
				userCache,
				WorldGenerationProgressLogger::new,
				string2
			);
			minecraftDedicatedServer.setUserName(optionSet.valueOf(optionSpec7));
			minecraftDedicatedServer.setServerPort(optionSet.valueOf(optionSpec10));
			minecraftDedicatedServer.setDemo(optionSet.has(optionSpec3));
			minecraftDedicatedServer.setBonusChest(optionSet.has(optionSpec4));
			minecraftDedicatedServer.setForceWorldUpgrade(optionSet.has(optionSpec5));
			minecraftDedicatedServer.method_17819(optionSet.valueOf(optionSpec11));
			boolean bl = !optionSet.has(optionSpec) && !optionSet.valuesOf(optionSpec12).contains("nogui");
			if (bl && !GraphicsEnvironment.isHeadless()) {
				minecraftDedicatedServer.createGui();
			}

			minecraftDedicatedServer.start();
			Thread thread = new Thread("Server Shutdown Thread") {
				public void run() {
					minecraftDedicatedServer.stop(true);
				}
			};
			thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
			Runtime.getRuntime().addShutdownHook(thread);
		} catch (Exception var28) {
			LOGGER.fatal("Failed to start the minecraft server", (Throwable)var28);
		}
	}

	protected void method_17819(String string) {
		this.field_17601 = string;
	}

	protected void setForceWorldUpgrade(boolean bl) {
		this.forceWorldUpgrade = bl;
	}

	public void start() {
		this.serverThread.start();
	}

	@Environment(EnvType.CLIENT)
	public boolean isServerThreadAlive() {
		return !this.serverThread.isAlive();
	}

	public File getFile(String string) {
		return new File(this.getRunDirectory(), string);
	}

	public void info(String string) {
		LOGGER.info(string);
	}

	public void warn(String string) {
		LOGGER.warn(string);
	}

	public ServerWorld method_3847(DimensionType dimensionType) {
		return (ServerWorld)this.worlds.get(dimensionType);
	}

	public Iterable<ServerWorld> getWorlds() {
		return this.worlds.values();
	}

	public String getVersion() {
		return SharedConstants.getGameVersion().getName();
	}

	public int getCurrentPlayerCount() {
		return this.field_4550.getCurrentPlayerCount();
	}

	public int getMaxPlayerCount() {
		return this.field_4550.getMaxPlayerCount();
	}

	public String[] getPlayerNames() {
		return this.field_4550.getPlayerNames();
	}

	public boolean isDebuggingEnabled() {
		return false;
	}

	public void logError(String string) {
		LOGGER.error(string);
	}

	public void log(String string) {
		if (this.isDebuggingEnabled()) {
			LOGGER.info(string);
		}
	}

	public String getServerModName() {
		return "vanilla";
	}

	public CrashReport populateCrashReport(CrashReport crashReport) {
		if (this.field_4550 != null) {
			crashReport.method_567()
				.method_577(
					"Player Count", () -> this.field_4550.getCurrentPlayerCount() + " / " + this.field_4550.getMaxPlayerCount() + "; " + this.field_4550.getPlayerList()
				);
		}

		crashReport.method_567().method_577("Data Packs", () -> {
			StringBuilder stringBuilder = new StringBuilder();

			for (ResourcePackContainer resourcePackContainer : this.field_4595.getEnabledContainers()) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(", ");
				}

				stringBuilder.append(resourcePackContainer.getName());
				if (!resourcePackContainer.getCompatibility().isCompatible()) {
					stringBuilder.append(" (incompatible)");
				}
			}

			return stringBuilder.toString();
		});
		if (this.field_17601 != null) {
			crashReport.method_567().method_577("Server Id", () -> this.field_17601);
		}

		return crashReport;
	}

	public boolean method_3814() {
		return this.gameDir != null;
	}

	@Override
	public void method_9203(TextComponent textComponent) {
		LOGGER.info(textComponent.getString());
	}

	public KeyPair getKeyPair() {
		return this.keyPair;
	}

	public int getServerPort() {
		return this.serverPort;
	}

	public void setServerPort(int i) {
		this.serverPort = i;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String string) {
		this.userName = string;
	}

	public boolean isSinglePlayer() {
		return this.userName != null;
	}

	public String getLevelName() {
		return this.levelName;
	}

	@Environment(EnvType.CLIENT)
	public void setServerName(String string) {
		this.displayName = string;
	}

	@Environment(EnvType.CLIENT)
	public String getServerName() {
		return this.displayName;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	public void setDifficulty(Difficulty difficulty, boolean bl) {
		for (ServerWorld serverWorld : this.getWorlds()) {
			LevelProperties levelProperties = serverWorld.method_8401();
			if (bl || !levelProperties.isDifficultyLocked()) {
				if (levelProperties.isHardcore()) {
					levelProperties.setDifficulty(Difficulty.HARD);
					serverWorld.setMobSpawnOptions(true, true);
				} else if (this.isSinglePlayer()) {
					levelProperties.setDifficulty(difficulty);
					serverWorld.setMobSpawnOptions(serverWorld.getDifficulty() != Difficulty.PEACEFUL, true);
				} else {
					levelProperties.setDifficulty(difficulty);
					serverWorld.setMobSpawnOptions(this.isMonsterSpawningEnabled(), this.spawnAnimals);
				}
			}
		}

		this.method_3760().getPlayerList().forEach(this::method_19465);
	}

	public void method_19467(boolean bl) {
		for (ServerWorld serverWorld : this.getWorlds()) {
			LevelProperties levelProperties = serverWorld.method_8401();
			levelProperties.setDifficultyLocked(bl);
		}

		this.method_3760().getPlayerList().forEach(this::method_19465);
	}

	private void method_19465(ServerPlayerEntity serverPlayerEntity) {
		LevelProperties levelProperties = serverPlayerEntity.getServerWorld().method_8401();
		serverPlayerEntity.field_13987.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
	}

	protected boolean isMonsterSpawningEnabled() {
		return true;
	}

	public boolean isDemo() {
		return this.demo;
	}

	public void setDemo(boolean bl) {
		this.demo = bl;
	}

	public void setBonusChest(boolean bl) {
		this.bonusChest = bl;
	}

	public LevelStorage getLevelStorage() {
		return this.levelStorage;
	}

	public String getResourcePackUrl() {
		return this.resourcePackUrl;
	}

	public String getResourcePackHash() {
		return this.resourcePackHash;
	}

	public void setResourcePack(String string, String string2) {
		this.resourcePackUrl = string;
		this.resourcePackHash = string2;
	}

	@Override
	public void addSnooperInfo(Snooper snooper) {
		snooper.addInfo("whitelist_enabled", false);
		snooper.addInfo("whitelist_count", 0);
		if (this.field_4550 != null) {
			snooper.addInfo("players_current", this.getCurrentPlayerCount());
			snooper.addInfo("players_max", this.getMaxPlayerCount());
			snooper.addInfo("players_seen", this.method_3847(DimensionType.field_13072).getSaveHandler().getSavedPlayerIds().length);
		}

		snooper.addInfo("uses_auth", this.onlineMode);
		snooper.addInfo("gui_state", this.hasGui() ? "enabled" : "disabled");
		snooper.addInfo("run_time", (SystemUtil.getMeasuringTimeMs() - snooper.getStartTime()) / 60L * 1000L);
		snooper.addInfo("avg_tick_ms", (int)(MathHelper.average(this.lastTickLengths) * 1.0E-6));
		int i = 0;

		for (ServerWorld serverWorld : this.getWorlds()) {
			if (serverWorld != null) {
				LevelProperties levelProperties = serverWorld.method_8401();
				snooper.addInfo("world[" + i + "][dimension]", serverWorld.field_9247.method_12460());
				snooper.addInfo("world[" + i + "][mode]", levelProperties.getGameMode());
				snooper.addInfo("world[" + i + "][difficulty]", serverWorld.getDifficulty());
				snooper.addInfo("world[" + i + "][hardcore]", levelProperties.isHardcore());
				snooper.addInfo("world[" + i + "][generator_name]", levelProperties.getGeneratorType().getName());
				snooper.addInfo("world[" + i + "][generator_version]", levelProperties.getGeneratorType().getVersion());
				snooper.addInfo("world[" + i + "][height]", this.worldHeight);
				snooper.addInfo("world[" + i + "][chunks_loaded]", serverWorld.method_14178().getLoadedChunkCount());
				i++;
			}
		}

		snooper.addInfo("worlds", i);
	}

	public abstract boolean isDedicated();

	public boolean isOnlineMode() {
		return this.onlineMode;
	}

	public void setOnlineMode(boolean bl) {
		this.onlineMode = bl;
	}

	public boolean shouldPreventProxyConnections() {
		return this.preventProxyConnections;
	}

	public void setPreventProxyConnections(boolean bl) {
		this.preventProxyConnections = bl;
	}

	public boolean shouldSpawnAnimals() {
		return this.spawnAnimals;
	}

	public void setSpawnAnimals(boolean bl) {
		this.spawnAnimals = bl;
	}

	public boolean shouldSpawnNpcs() {
		return this.spawnNpcs;
	}

	public abstract boolean isUsingNativeTransport();

	public void setSpawnNpcs(boolean bl) {
		this.spawnNpcs = bl;
	}

	public boolean isPvpEnabled() {
		return this.pvpEnabled;
	}

	public void setPvpEnabled(boolean bl) {
		this.pvpEnabled = bl;
	}

	public boolean isFlightEnabled() {
		return this.flightEnabled;
	}

	public void setFlightEnabled(boolean bl) {
		this.flightEnabled = bl;
	}

	public abstract boolean areCommandBlocksEnabled();

	public String getServerMotd() {
		return this.motd;
	}

	public void setMotd(String string) {
		this.motd = string;
	}

	public int getWorldHeight() {
		return this.worldHeight;
	}

	public void setWorldHeight(int i) {
		this.worldHeight = i;
	}

	public boolean isStopped() {
		return this.stopped;
	}

	public PlayerManager method_3760() {
		return this.field_4550;
	}

	public void method_3846(PlayerManager playerManager) {
		this.field_4550 = playerManager;
	}

	public abstract boolean isRemote();

	public void setDefaultGameMode(GameMode gameMode) {
		for (ServerWorld serverWorld : this.getWorlds()) {
			serverWorld.method_8401().setGameMode(gameMode);
		}
	}

	@Nullable
	public ServerNetworkIO method_3787() {
		return this.field_4563;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_3820() {
		return this.field_4547;
	}

	public boolean hasGui() {
		return false;
	}

	public abstract boolean openToLan(GameMode gameMode, boolean bl, int i);

	public int getTicks() {
		return this.ticks;
	}

	public void method_3832() {
		this.field_4597 = true;
	}

	@Environment(EnvType.CLIENT)
	public Snooper getSnooper() {
		return this.snooper;
	}

	public int getSpawnProtectionRadius() {
		return 16;
	}

	public boolean isSpawnProtected(World world, BlockPos blockPos, PlayerEntity playerEntity) {
		return false;
	}

	public void setForceGameMode(boolean bl) {
		this.forceGameMode = bl;
	}

	public boolean shouldForceGameMode() {
		return this.forceGameMode;
	}

	public int getPlayerIdleTimeout() {
		return this.playerIdleTimeout;
	}

	public void setPlayerIdleTimeout(int i) {
		this.playerIdleTimeout = i;
	}

	public MinecraftSessionService getSessionService() {
		return this.sessionService;
	}

	public GameProfileRepository getGameProfileRepo() {
		return this.gameProfileRepo;
	}

	public UserCache method_3793() {
		return this.field_4556;
	}

	public ServerMetadata method_3765() {
		return this.field_4593;
	}

	public void method_3856() {
		this.field_4551 = 0L;
	}

	public int getMaxWorldBorderRadius() {
		return 29999984;
	}

	@Override
	public boolean isOffThread() {
		return super.isOffThread() && !this.isStopped();
	}

	@Override
	public Thread method_3777() {
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

	public int method_3829(@Nullable ServerWorld serverWorld) {
		return serverWorld != null ? serverWorld.getGameRules().getInteger("spawnRadius") : 10;
	}

	public ServerAdvancementLoader method_3851() {
		return this.field_4567;
	}

	public CommandFunctionManager method_3740() {
		return this.field_4591;
	}

	public void reload() {
		if (!this.method_18854()) {
			this.execute(this::reload);
		} else {
			this.method_3760().saveAllPlayerData();
			this.field_4595.callCreators();
			this.reloadDataPacks(this.method_3847(DimensionType.field_13072).method_8401());
			this.method_3760().onDataPacksReloaded();
		}
	}

	private void reloadDataPacks(LevelProperties levelProperties) {
		List<ResourcePackContainer> list = Lists.<ResourcePackContainer>newArrayList(this.field_4595.getEnabledContainers());

		for (ResourcePackContainer resourcePackContainer : this.field_4595.getAlphabeticallyOrderedContainers()) {
			if (!levelProperties.getDisabledDataPacks().contains(resourcePackContainer.getName()) && !list.contains(resourcePackContainer)) {
				LOGGER.info("Found new data pack {}, loading it automatically", resourcePackContainer.getName());
				resourcePackContainer.getSortingDirection().locate(list, resourcePackContainer, resourcePackContainerx -> resourcePackContainerx, false);
			}
		}

		this.field_4595.setEnabled(list);
		List<ResourcePack> list2 = Lists.<ResourcePack>newArrayList();
		this.field_4595.getEnabledContainers().forEach(resourcePackContainerx -> list2.add(resourcePackContainerx.createResourcePack()));
		CompletableFuture<net.minecraft.util.Void> completableFuture = this.field_4576
			.beginReload(this.workerExecutor, this, list2, CompletableFuture.completedFuture(net.minecraft.util.Void.INSTANCE));
		this.method_18857(completableFuture::isDone);
		levelProperties.getEnabledDataPacks().clear();
		levelProperties.getDisabledDataPacks().clear();
		this.field_4595.getEnabledContainers().forEach(resourcePackContainerx -> levelProperties.getEnabledDataPacks().add(resourcePackContainerx.getName()));
		this.field_4595.getAlphabeticallyOrderedContainers().forEach(resourcePackContainerx -> {
			if (!this.field_4595.getEnabledContainers().contains(resourcePackContainerx)) {
				levelProperties.getDisabledDataPacks().add(resourcePackContainerx.getName());
			}
		});
	}

	public void method_3728(ServerCommandSource serverCommandSource) {
		if (this.method_3729()) {
			PlayerManager playerManager = serverCommandSource.getMinecraftServer().method_3760();
			WhitelistList whitelistList = playerManager.method_14590();
			if (whitelistList.isEnabled()) {
				for (ServerPlayerEntity serverPlayerEntity : Lists.newArrayList(playerManager.getPlayerList())) {
					if (!whitelistList.method_14653(serverPlayerEntity.getGameProfile())) {
						serverPlayerEntity.field_13987.disconnect(new TranslatableTextComponent("multiplayer.disconnect.not_whitelisted"));
					}
				}
			}
		}
	}

	public ReloadableResourceManager method_3809() {
		return this.field_4576;
	}

	public ResourcePackContainerManager<ResourcePackContainer> method_3836() {
		return this.field_4595;
	}

	public ServerCommandManager getCommandManager() {
		return this.commandManager;
	}

	public ServerCommandSource getCommandSource() {
		return new ServerCommandSource(
			this,
			this.method_3847(DimensionType.field_13072) == null ? Vec3d.ZERO : new Vec3d(this.method_3847(DimensionType.field_13072).method_8395()),
			Vec2f.ZERO,
			this.method_3847(DimensionType.field_13072),
			4,
			"Server",
			new StringTextComponent("Server"),
			this,
			null
		);
	}

	@Override
	public boolean sendCommandFeedback() {
		return true;
	}

	@Override
	public boolean shouldTrackOutput() {
		return true;
	}

	public RecipeManager getRecipeManager() {
		return this.recipeManager;
	}

	public TagManager method_3801() {
		return this.field_4583;
	}

	public ServerScoreboard method_3845() {
		return this.field_4558;
	}

	public LootManager getLootManager() {
		return this.lootManager;
	}

	public GameRules getGameRules() {
		return this.method_3847(DimensionType.field_13072).getGameRules();
	}

	public BossBarManager method_3837() {
		return this.field_4548;
	}

	public boolean method_3729() {
		return this.field_4570;
	}

	public void method_3731(boolean bl) {
		this.field_4570 = bl;
	}

	@Environment(EnvType.CLIENT)
	public float getTickTime() {
		return this.tickTime;
	}

	public int getPermissionLevel(GameProfile gameProfile) {
		if (this.method_3760().isOperator(gameProfile)) {
			OperatorEntry operatorEntry = this.method_3760().method_14603().get(gameProfile);
			if (operatorEntry != null) {
				return operatorEntry.getPermissionLevel();
			} else if (this.method_19466(gameProfile)) {
				return 4;
			} else if (this.isSinglePlayer()) {
				return this.method_3760().areCheatsAllowed() ? 4 : 0;
			} else {
				return this.getOpPermissionLevel();
			}
		} else {
			return 0;
		}
	}

	@Environment(EnvType.CLIENT)
	public MetricsData method_15876() {
		return this.field_16205;
	}

	public DisableableProfiler getProfiler() {
		return this.profiler;
	}

	public Executor getWorkerExecutor() {
		return this.workerExecutor;
	}

	public abstract boolean method_19466(GameProfile gameProfile);
}
