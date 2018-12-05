package net.minecraft.server;

import com.google.common.base.Stopwatch;
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
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
import net.minecraft.class_1932;
import net.minecraft.class_3004;
import net.minecraft.class_3202;
import net.minecraft.class_3689;
import net.minecraft.class_37;
import net.minecraft.class_3738;
import net.minecraft.class_3807;
import net.minecraft.client.network.packet.WorldTimeUpdateClientPacket;
import net.minecraft.datafixers.Schemas;
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
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.config.OperatorEntry;
import net.minecraft.server.config.WhitelistList;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerDemoWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ServerWorldListener;
import net.minecraft.tag.TagManager;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.MetricsData;
import net.minecraft.util.MinecraftException;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.ThreadTaskQueue;
import net.minecraft.util.Tickable;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.UserCache;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.AnvilLevelStorage;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.loot.LootManager;
import net.minecraft.world.updater.WorldUpdater;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer extends ThreadTaskQueue<class_3738> implements SnooperListener, CommandOutput, Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final File USER_CACHE_FILE = new File("usercache.json");
	private final LevelStorage levelStorage;
	private final Snooper snooper = new Snooper("server", this, SystemUtil.getMeasuringTimeMili());
	private final File gameDir;
	private final List<Tickable> tickables = Lists.<Tickable>newArrayList();
	private final class_3689 profiler = new class_3689(this::getTicks);
	private final ServerNetworkIO networkIO;
	private final ServerMetadata metadata = new ServerMetadata();
	private final Random random = new Random();
	private final DataFixer dataFixer;
	private String serverIp;
	private int serverPort = -1;
	private final Map<DimensionType, ServerWorld> worlds = Maps.<DimensionType, ServerWorld>newIdentityHashMap();
	private PlayerManager serverConfigurationManager;
	private volatile boolean running = true;
	private boolean stopped;
	private int ticks;
	protected final Proxy field_4599;
	private TextComponent field_4581;
	private boolean onlineMode;
	private boolean preventProxyConnections;
	private boolean spawnAnimals;
	private boolean spawnNpcs;
	private boolean pvpEnabled;
	private boolean flightEnabled;
	private String motd;
	private int worldHeight;
	private int playerIdleTimeout;
	public final long[] lastTickLengths = new long[100];
	protected final Map<DimensionType, long[]> field_4600 = Maps.<DimensionType, long[]>newIdentityHashMap();
	private KeyPair keyPair;
	private String userName;
	private String levelName;
	@Environment(EnvType.CLIENT)
	private String displayName;
	private boolean demo;
	private boolean bonusChest;
	private String resourcePackUrl = "";
	private String resourcePackHash = "";
	private volatile boolean field_4547;
	private long field_4557;
	private TextComponent field_4601;
	private boolean field_4597;
	private boolean forceGameMode;
	private final YggdrasilAuthenticationService authService;
	private final MinecraftSessionService sessionService;
	private final GameProfileRepository gameProfileRepo;
	private final UserCache userCache;
	private long field_4551;
	protected final Thread serverThread = SystemUtil.consume(
		new Thread(this, "Server thread"), thread -> thread.setUncaughtExceptionHandler((threadx, throwable) -> LOGGER.error(throwable))
	);
	private long timeReference = SystemUtil.getMeasuringTimeMili();
	@Environment(EnvType.CLIENT)
	private boolean iconFilePresent;
	private final ReloadableResourceManager dataManager = new ReloadableResourceManagerImpl(ResourceType.DATA);
	private final ResourcePackContainerManager<ResourcePackContainer> field_4595 = new ResourcePackContainerManager<>(ResourcePackContainer::new);
	private FileResourcePackCreator field_4553;
	private final ServerCommandManager commandManager;
	private final RecipeManager recipeManager = new RecipeManager();
	private final TagManager tagManager = new TagManager();
	private final ServerScoreboard field_4558 = new ServerScoreboard(this);
	private final class_3004 field_4548 = new class_3004(this);
	private final LootManager lootManager = new LootManager();
	private final ServerAdvancementManager advancementManager = new ServerAdvancementManager();
	private final CommandFunctionManager commandFunctionManager = new CommandFunctionManager(this);
	private final MetricsData field_16205 = new MetricsData();
	private final long field_16204 = SystemUtil.getMeasuringTimeNano();
	private boolean field_4570;
	private boolean forceWorldUpgrade;
	private float field_4592;

	public MinecraftServer(
		@Nullable File file,
		Proxy proxy,
		DataFixer dataFixer,
		ServerCommandManager serverCommandManager,
		YggdrasilAuthenticationService yggdrasilAuthenticationService,
		MinecraftSessionService minecraftSessionService,
		GameProfileRepository gameProfileRepository,
		UserCache userCache
	) {
		this.field_4599 = proxy;
		this.commandManager = serverCommandManager;
		this.authService = yggdrasilAuthenticationService;
		this.sessionService = minecraftSessionService;
		this.gameProfileRepo = gameProfileRepository;
		this.userCache = userCache;
		this.gameDir = file;
		this.networkIO = file == null ? null : new ServerNetworkIO(this);
		this.levelStorage = file == null ? null : new AnvilLevelStorage(file.toPath(), file.toPath().resolve("../backups"), dataFixer);
		this.dataFixer = dataFixer;
		this.dataManager.addListener(this.tagManager);
		this.dataManager.addListener(this.recipeManager);
		this.dataManager.addListener(this.lootManager);
		this.dataManager.addListener(this.commandFunctionManager);
		this.dataManager.addListener(this.advancementManager);
	}

	protected abstract boolean setupServer() throws IOException;

	protected void method_3755(String string) {
		if (this.getLevelStorage().requiresConversion(string)) {
			LOGGER.info("Converting map!");
			this.method_3768(new TranslatableTextComponent("menu.convertingLevel"));
			this.getLevelStorage().convertLevel(string, new ProgressListener() {
				private long lastProgressUpdate = SystemUtil.getMeasuringTimeMili();

				@Override
				public void method_15412(TextComponent textComponent) {
				}

				@Environment(EnvType.CLIENT)
				@Override
				public void method_15413(TextComponent textComponent) {
				}

				@Override
				public void progressStagePercentage(int i) {
					if (SystemUtil.getMeasuringTimeMili() - this.lastProgressUpdate >= 1000L) {
						this.lastProgressUpdate = SystemUtil.getMeasuringTimeMili();
						MinecraftServer.LOGGER.info("Converting... {}%", i);
					}
				}

				@Environment(EnvType.CLIENT)
				@Override
				public void method_15411() {
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

				while (!worldUpdater.method_5403()) {
					TextComponent textComponent2 = worldUpdater.getStatus();
					if (textComponent != textComponent2) {
						textComponent = textComponent2;
						LOGGER.info(worldUpdater.getStatus().getString());
					}

					int i = worldUpdater.method_5397();
					if (i > 0) {
						int j = worldUpdater.method_5400() + worldUpdater.method_5399();
						LOGGER.info("{}% completed ({} / {} chunks)...", MathHelper.floor((float)j / (float)i * 100.0F), j, i);
					}

					if (this.isStopped()) {
						worldUpdater.method_5402();
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

	@Nullable
	@Environment(EnvType.CLIENT)
	public synchronized TextComponent method_3810() {
		return this.field_4601;
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
				levelInfo = ServerDemoWorld.INFO;
			} else {
				levelInfo = new LevelInfo(l, this.getDefaultGameMode(), this.shouldGenerateStructures(), this.isHardcore(), levelGeneratorType);
				levelInfo.method_8579(jsonElement);
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
		class_37 lv = new class_37(worldSaveHandler);
		this.method_3786(worldSaveHandler, lv, levelProperties, levelInfo);
		this.setDifficulty(this.getDefaultDifficulty());
		this.method_3774(lv);
	}

	protected void method_3786(WorldSaveHandler worldSaveHandler, class_37 arg, LevelProperties levelProperties, LevelInfo levelInfo) {
		if (this.isDemo()) {
			this.worlds
				.put(DimensionType.field_13072, new ServerDemoWorld(this, worldSaveHandler, arg, levelProperties, DimensionType.field_13072, this.profiler).method_14185());
		} else {
			this.worlds
				.put(DimensionType.field_13072, new ServerWorld(this, worldSaveHandler, arg, levelProperties, DimensionType.field_13072, this.profiler).method_14185());
		}

		ServerWorld serverWorld = this.getWorld(DimensionType.field_13072);
		serverWorld.init(levelInfo);
		serverWorld.registerListener(new ServerWorldListener(this, serverWorld));
		if (!this.isSinglePlayer()) {
			serverWorld.getLevelProperties().setGameMode(this.getDefaultGameMode());
		}

		class_3202 lv = new class_3202(this, worldSaveHandler, DimensionType.field_13076, serverWorld, this.profiler).method_14033();
		this.worlds.put(DimensionType.field_13076, lv);
		lv.registerListener(new ServerWorldListener(this, lv));
		if (!this.isSinglePlayer()) {
			lv.getLevelProperties().setGameMode(this.getDefaultGameMode());
		}

		class_3202 lv2 = new class_3202(this, worldSaveHandler, DimensionType.field_13078, serverWorld, this.profiler).method_14033();
		this.worlds.put(DimensionType.field_13078, lv2);
		lv2.registerListener(new ServerWorldListener(this, lv2));
		if (!this.isSinglePlayer()) {
			lv2.getLevelProperties().setGameMode(this.getDefaultGameMode());
		}

		this.getConfigurationManager().method_14591(serverWorld);
		if (levelProperties.getCustomBossEvents() != null) {
			this.method_3837().method_12972(levelProperties.getCustomBossEvents());
		}
	}

	protected void method_3800(File file, LevelProperties levelProperties) {
		this.field_4595.addCreator(new DefaultResourcePackCreator());
		this.field_4553 = new FileResourcePackCreator(new File(file, "datapacks"));
		this.field_4595.addCreator(this.field_4553);
		this.field_4595.callCreators();
		List<ResourcePackContainer> list = Lists.<ResourcePackContainer>newArrayList();

		for (String string : levelProperties.getEnabledDataPacks()) {
			ResourcePackContainer resourcePackContainer = this.field_4595.getContainer(string);
			if (resourcePackContainer != null) {
				list.add(resourcePackContainer);
			} else {
				LOGGER.warn("Missing data pack {}", string);
			}
		}

		this.field_4595.resetEnabled(list);
		this.reloadDataPacks(levelProperties);
	}

	protected void method_3774(class_37 arg) {
		this.method_3768(new TranslatableTextComponent("menu.generatingTerrain"));
		ServerWorld serverWorld = this.getWorld(DimensionType.field_13072);
		LOGGER.info("Preparing start region for dimension " + DimensionType.getId(serverWorld.dimension.getType()));
		BlockPos blockPos = serverWorld.method_8395();
		Stopwatch stopwatch = Stopwatch.createStarted();
		ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
		this.timeReference = SystemUtil.getMeasuringTimeMili();
		int i = 0;
		serverChunkManager.method_14135(new ChunkPos(blockPos), 11);
		serverChunkManager.method_14142(441);

		while (serverChunkManager.method_14107()) {
			this.timeReference += 100L;
			serverChunkManager.updateLight();
			this.method_16208();
			if (++i % 5 == 0) {
				this.method_3746(new TranslatableTextComponent("menu.preparingSpawn"), serverChunkManager.getProgressString());
			}
		}

		this.timeReference += 100L;
		serverChunkManager.updateLight();
		this.method_16208();
		this.method_3746(new TranslatableTextComponent("menu.preparingSpawn"), serverChunkManager.getProgressString());

		for (DimensionType dimensionType : DimensionType.getAll()) {
			class_1932 lv = arg.method_268(dimensionType, class_1932::new, "chunks");
			if (lv != null) {
				ServerWorld serverWorld2 = this.getWorld(dimensionType);
				int j = lv.method_8375().size();
				int k = 0;
				LongIterator longIterator = lv.method_8375().iterator();

				while (longIterator.hasNext()) {
					this.method_3746(new TranslatableTextComponent("menu.loadingForcedChunks", dimensionType), Integer.toString(k++ * 100 / j));
					long l = longIterator.nextLong();
					ChunkPos chunkPos = new ChunkPos(l);
					serverWorld2.getChunkManager().method_12124(chunkPos, true);
				}
			}
		}

		this.timeReference += 100L;
		serverChunkManager.updateLight();
		this.method_16208();
		LOGGER.info("Time elapsed: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
		this.clearSpawnAreaProgress();
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

	protected void method_3746(TextComponent textComponent, String string) {
		this.field_4581 = textComponent.append(": " + string);
		LOGGER.info(this.field_4581.getString());
	}

	protected void clearSpawnAreaProgress() {
		this.field_4581 = null;
	}

	protected void save(boolean bl, boolean bl2) {
		for (ServerWorld serverWorld : this.getWorlds()) {
			if (serverWorld != null) {
				if (!bl) {
					LOGGER.info("Saving chunks for level '{}'/{}", serverWorld.getLevelProperties().getLevelName(), DimensionType.getId(serverWorld.dimension.getType()));
				}

				try {
					serverWorld.save(null, bl2);
				} catch (MinecraftException var6) {
					LOGGER.warn(var6.getMessage());
				}
			}
		}
	}

	protected void shutdown() {
		LOGGER.info("Stopping server");
		if (this.getNetworkIO() != null) {
			this.getNetworkIO().stop();
		}

		if (this.serverConfigurationManager != null) {
			LOGGER.info("Saving players");
			this.serverConfigurationManager.saveAllPlayerData();
			this.serverConfigurationManager.disconnectAllPlayers();
		}

		LOGGER.info("Saving worlds");

		for (ServerWorld serverWorld : this.getWorlds()) {
			if (serverWorld != null) {
				serverWorld.field_13957 = false;
			}
		}

		this.save(false, true);

		for (ServerWorld serverWorldx : this.getWorlds()) {
			if (serverWorldx != null) {
				serverWorldx.close();
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

	private boolean shouldKeepTicking() {
		return SystemUtil.getMeasuringTimeMili() < this.timeReference;
	}

	public void run() {
		try {
			if (this.setupServer()) {
				this.timeReference = SystemUtil.getMeasuringTimeMili();
				this.metadata.setDescription(new StringTextComponent(this.motd));
				this.metadata.setVersion(new ServerMetadata.Version(SharedConstants.getGameVersion().getName(), SharedConstants.getGameVersion().getProtocolVersion()));
				this.method_3791(this.metadata);

				while (this.running) {
					long l = SystemUtil.getMeasuringTimeMili() - this.timeReference;
					if (l > 2000L && this.timeReference - this.field_4557 >= 15000L) {
						long m = l / 50L;
						LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", l, m);
						this.timeReference += m * 50L;
						this.field_4557 = this.timeReference;
					}

					this.timeReference += 50L;
					this.method_3748(this::shouldKeepTicking);
					this.method_16208();
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

	protected void method_16208() {
		boolean bl;
		do {
			bl = this.executeQueuedTask();
			if (!bl) {
				Thread.yield();
			}
		} while (bl || this.shouldKeepTicking());
	}

	protected class_3738 method_16209(Runnable runnable) {
		return new class_3738(this.ticks, runnable);
	}

	@Override
	public boolean executeQueuedTask() {
		if (this.shouldKeepTicking()) {
			for (ServerWorld serverWorld : this.getWorlds()) {
				if (serverWorld.getChunkManager().method_16165()) {
					return true;
				}
			}
		}

		class_3738 lv = (class_3738)this.taskQueue.peek();
		if (lv == null) {
			return false;
		} else if (!this.shouldKeepTicking() && lv.method_16338() + 3 >= this.ticks) {
			return false;
		} else {
			try {
				((class_3738)this.taskQueue.remove()).run();
			} catch (Exception var3) {
				LOGGER.fatal("Error executing task", (Throwable)var3);
			}

			return true;
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
		if (this.field_4597) {
			this.field_4597 = false;
			this.profiler.method_16055().method_16060();
		}

		this.profiler.method_16065();
		this.tick(booleanSupplier);
		if (l - this.field_4551 >= 5000000000L) {
			this.field_4551 = l;
			this.metadata.setPlayers(new ServerMetadata.Players(this.getMaxPlayerCount(), this.getCurrentPlayerCount()));
			GameProfile[] gameProfiles = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
			int i = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - gameProfiles.length);

			for (int j = 0; j < gameProfiles.length; j++) {
				gameProfiles[j] = ((ServerPlayerEntity)this.serverConfigurationManager.getPlayerList().get(i + j)).getGameProfile();
			}

			Collections.shuffle(Arrays.asList(gameProfiles));
			this.metadata.getPlayers().setSample(gameProfiles);
		}

		if (this.ticks % 900 == 0) {
			this.profiler.begin("save");
			this.serverConfigurationManager.saveAllPlayerData();
			this.save(true, false);
			this.profiler.end();
		}

		this.profiler.begin("snooper");
		if (!this.snooper.isActive() && this.ticks > 100) {
			this.snooper.method_5482();
		}

		if (this.ticks % 6000 == 0) {
			this.snooper.update();
		}

		this.profiler.end();
		this.profiler.begin("tallying");
		long m = this.lastTickLengths[this.ticks % 100] = SystemUtil.getMeasuringTimeNano() - l;
		this.field_4592 = this.field_4592 * 0.8F + (float)m / 1000000.0F * 0.19999999F;
		long n = SystemUtil.getMeasuringTimeNano();
		this.field_16205.pushSample(n - l);
		this.profiler.end();
		this.profiler.method_16066();
	}

	protected void tick(BooleanSupplier booleanSupplier) {
		this.profiler.begin("commandFunctions");
		this.getCommandFunctionManager().tick();
		this.profiler.endBegin("levels");

		for (ServerWorld serverWorld : this.getWorlds()) {
			long l = SystemUtil.getMeasuringTimeNano();
			if (serverWorld.dimension.getType() == DimensionType.field_13072 || this.isNetherAllowed()) {
				this.profiler
					.begin((Supplier<String>)(() -> serverWorld.getLevelProperties().getLevelName() + " " + Registry.DIMENSION.getId(serverWorld.dimension.getType())));
				if (this.ticks % 20 == 0) {
					this.profiler.begin("timeSync");
					this.serverConfigurationManager
						.sendToDimension(
							new WorldTimeUpdateClientPacket(serverWorld.getTime(), serverWorld.getTimeOfDay(), serverWorld.getGameRules().getBoolean("doDaylightCycle")),
							serverWorld.dimension.getType()
						);
					this.profiler.end();
				}

				this.profiler.begin("tick");

				try {
					serverWorld.tick(booleanSupplier);
				} catch (Throwable var9) {
					CrashReport crashReport = CrashReport.create(var9, "Exception ticking world");
					serverWorld.toCrashReportElement(crashReport);
					throw new CrashException(crashReport);
				}

				try {
					serverWorld.updateEntities();
				} catch (Throwable var8) {
					CrashReport crashReport = CrashReport.create(var8, "Exception ticking world entities");
					serverWorld.toCrashReportElement(crashReport);
					throw new CrashException(crashReport);
				}

				this.profiler.end();
				this.profiler.begin("tracker");
				serverWorld.getEntityTracker().method_14078();
				this.profiler.end();
				this.profiler.end();
			}

			((long[])this.field_4600.computeIfAbsent(serverWorld.dimension.getType(), dimensionType -> new long[100]))[this.ticks % 100] = SystemUtil.getMeasuringTimeNano()
				- l;
		}

		this.profiler.endBegin("connection");
		this.getNetworkIO().tick();
		this.profiler.endBegin("players");
		this.serverConfigurationManager.updatePlayerLatency();
		this.profiler.endBegin("tickables");

		for (int i = 0; i < this.tickables.size(); i++) {
			((Tickable)this.tickables.get(i)).tick();
		}

		this.profiler.end();
	}

	public boolean isNetherAllowed() {
		return true;
	}

	public void registerTickable(Tickable tickable) {
		this.tickables.add(tickable);
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
		OptionSpec<String> optionSpec11 = optionParser.nonOptions();

		try {
			OptionSet optionSet = optionParser.parse(strings);
			if (optionSet.has(optionSpec6)) {
				optionParser.printHelpOn(System.err);
				return;
			}

			Path path = Paths.get("server.properties");
			class_3807 lv = new class_3807(path);
			lv.method_16719();
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
			String string = optionSet.valueOf(optionSpec8);
			YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
			MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
			GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
			UserCache userCache = new UserCache(gameProfileRepository, new File(string, USER_CACHE_FILE.getName()));
			final MinecraftDedicatedServer minecraftDedicatedServer = new MinecraftDedicatedServer(
				new File(string), lv, Schemas.getFixer(), yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache
			);
			minecraftDedicatedServer.setUserName(optionSet.valueOf(optionSpec7));
			minecraftDedicatedServer.setLevelName(optionSet.valueOf(optionSpec9));
			minecraftDedicatedServer.setServerPort(optionSet.valueOf(optionSpec10));
			minecraftDedicatedServer.setDemo(optionSet.has(optionSpec3));
			minecraftDedicatedServer.setBonusChest(optionSet.has(optionSpec4));
			minecraftDedicatedServer.setForceWorldUpgrade(optionSet.has(optionSpec5));
			boolean bl = !optionSet.has(optionSpec) && !optionSet.valuesOf(optionSpec11).contains("nogui");
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
		} catch (Exception var26) {
			LOGGER.fatal("Failed to start the minecraft server", (Throwable)var26);
		}
	}

	protected void setForceWorldUpgrade(boolean bl) {
		this.forceWorldUpgrade = bl;
	}

	public void start() {
		this.serverThread.start();
	}

	@Environment(EnvType.CLIENT)
	public boolean method_16043() {
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

	public ServerWorld getWorld(DimensionType dimensionType) {
		return (ServerWorld)this.worlds.get(dimensionType);
	}

	public Iterable<ServerWorld> getWorlds() {
		return this.worlds.values();
	}

	public String getVersion() {
		return SharedConstants.getGameVersion().getName();
	}

	public int getCurrentPlayerCount() {
		return this.serverConfigurationManager.getCurrentPlayerCount();
	}

	public int getMaxPlayerCount() {
		return this.serverConfigurationManager.getMaxPlayerCount();
	}

	public String[] getPlayerNames() {
		return this.serverConfigurationManager.getPlayerNames();
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
		if (this.serverConfigurationManager != null) {
			crashReport.getElement()
				.add(
					"Player Count",
					(ICrashCallable<String>)(() -> this.serverConfigurationManager.getCurrentPlayerCount()
							+ " / "
							+ this.serverConfigurationManager.getMaxPlayerCount()
							+ "; "
							+ this.serverConfigurationManager.getPlayerList())
				);
		}

		crashReport.getElement().add("Data Packs", (ICrashCallable<String>)(() -> {
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
		}));
		return crashReport;
	}

	public boolean method_3814() {
		return this.gameDir != null;
	}

	@Override
	public void appendCommandFeedback(TextComponent textComponent) {
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

	public void setLevelName(String string) {
		this.levelName = string;
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

	public void setDifficulty(Difficulty difficulty) {
		for (ServerWorld serverWorld : this.getWorlds()) {
			if (serverWorld.getLevelProperties().isHardcore()) {
				serverWorld.getLevelProperties().setDifficulty(Difficulty.HARD);
				serverWorld.setMobSpawnOptions(true, true);
			} else if (this.isSinglePlayer()) {
				serverWorld.getLevelProperties().setDifficulty(difficulty);
				serverWorld.setMobSpawnOptions(serverWorld.getDifficulty() != Difficulty.PEACEFUL, true);
			} else {
				serverWorld.getLevelProperties().setDifficulty(difficulty);
				serverWorld.setMobSpawnOptions(this.isMonsterSpawningEnabled(), this.spawnAnimals);
			}
		}
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
		if (this.serverConfigurationManager != null) {
			snooper.addInfo("players_current", this.getCurrentPlayerCount());
			snooper.addInfo("players_max", this.getMaxPlayerCount());
			snooper.addInfo("players_seen", this.serverConfigurationManager.getSavedPlayerIds().length);
		}

		snooper.addInfo("uses_auth", this.onlineMode);
		snooper.addInfo("gui_state", this.hasGui() ? "enabled" : "disabled");
		snooper.addInfo("run_time", (SystemUtil.getMeasuringTimeMili() - snooper.getStartTime()) / 60L * 1000L);
		snooper.addInfo("avg_tick_ms", (int)(MathHelper.average(this.lastTickLengths) * 1.0E-6));
		int i = 0;

		for (ServerWorld serverWorld : this.getWorlds()) {
			if (serverWorld != null) {
				LevelProperties levelProperties = serverWorld.getLevelProperties();
				snooper.addInfo("world[" + i + "][dimension]", serverWorld.dimension.getType());
				snooper.addInfo("world[" + i + "][mode]", levelProperties.getGameMode());
				snooper.addInfo("world[" + i + "][difficulty]", serverWorld.getDifficulty());
				snooper.addInfo("world[" + i + "][hardcore]", levelProperties.isHardcore());
				snooper.addInfo("world[" + i + "][generator_name]", levelProperties.getGeneratorType().getName());
				snooper.addInfo("world[" + i + "][generator_version]", levelProperties.getGeneratorType().getVersion());
				snooper.addInfo("world[" + i + "][height]", this.worldHeight);
				snooper.addInfo("world[" + i + "][chunks_loaded]", serverWorld.getChunkManager().getLoadedChunkCount());
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

	public PlayerManager getConfigurationManager() {
		return this.serverConfigurationManager;
	}

	public void setConfigurationManager(PlayerManager playerManager) {
		this.serverConfigurationManager = playerManager;
	}

	public abstract boolean isRemote();

	public void setDefaultGameMode(GameMode gameMode) {
		for (ServerWorld serverWorld : this.getWorlds()) {
			serverWorld.getLevelProperties().setGameMode(gameMode);
		}
	}

	public ServerNetworkIO getNetworkIO() {
		return this.networkIO;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_3820() {
		return this.field_4547;
	}

	public boolean hasGui() {
		return false;
	}

	public abstract boolean method_3763(GameMode gameMode, boolean bl, int i);

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

	public UserCache getUserCache() {
		return this.userCache;
	}

	public ServerMetadata getServerMetadata() {
		return this.metadata;
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
	public boolean isMainThread() {
		return Thread.currentThread() == this.serverThread;
	}

	public int getNetworkCompressionThreshold() {
		return 256;
	}

	public long getServerStartTime() {
		return this.timeReference;
	}

	public Thread getMainThread() {
		return this.serverThread;
	}

	public DataFixer getDataFixer() {
		return this.dataFixer;
	}

	public int getSpawnRadius(@Nullable ServerWorld serverWorld) {
		return serverWorld != null ? serverWorld.getGameRules().getInteger("spawnRadius") : 10;
	}

	public ServerAdvancementManager getAdvancementManager() {
		return this.advancementManager;
	}

	public CommandFunctionManager getCommandFunctionManager() {
		return this.commandFunctionManager;
	}

	public void reload() {
		if (!this.isMainThread()) {
			this.execute(this::reload);
		} else {
			this.getConfigurationManager().saveAllPlayerData();
			this.field_4595.callCreators();
			this.reloadDataPacks(this.getWorld(DimensionType.field_13072).getLevelProperties());
			this.getConfigurationManager().onDataPacksReloaded();
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

		this.field_4595.resetEnabled(list);
		List<ResourcePack> list2 = Lists.<ResourcePack>newArrayList();
		this.field_4595.getEnabledContainers().forEach(resourcePackContainerx -> list2.add(resourcePackContainerx.createResourcePack()));
		this.dataManager.reload(list2);
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
			PlayerManager playerManager = serverCommandSource.getMinecraftServer().getConfigurationManager();
			WhitelistList whitelistList = playerManager.getWhitelist();
			if (whitelistList.isEnabled()) {
				for (ServerPlayerEntity serverPlayerEntity : Lists.newArrayList(playerManager.getPlayerList())) {
					if (!whitelistList.method_14653(serverPlayerEntity.getGameProfile())) {
						serverPlayerEntity.networkHandler.method_14367(new TranslatableTextComponent("multiplayer.disconnect.not_whitelisted"));
					}
				}
			}
		}
	}

	public ReloadableResourceManager getDataManager() {
		return this.dataManager;
	}

	public ResourcePackContainerManager<ResourcePackContainer> method_3836() {
		return this.field_4595;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent method_3863() {
		return this.field_4581;
	}

	public ServerCommandManager getCommandManager() {
		return this.commandManager;
	}

	public ServerCommandSource method_3739() {
		return new ServerCommandSource(
			this,
			this.getWorld(DimensionType.field_13072) == null ? Vec3d.ZERO : new Vec3d(this.getWorld(DimensionType.field_13072).method_8395()),
			Vec2f.ZERO,
			this.getWorld(DimensionType.field_13072),
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

	public TagManager getTagManager() {
		return this.tagManager;
	}

	public ServerScoreboard method_3845() {
		return this.field_4558;
	}

	public LootManager getLootManager() {
		return this.lootManager;
	}

	public GameRules getGameRules() {
		return this.getWorld(DimensionType.field_13072).getGameRules();
	}

	public class_3004 method_3837() {
		return this.field_4548;
	}

	public boolean method_3729() {
		return this.field_4570;
	}

	public void method_3731(boolean bl) {
		this.field_4570 = bl;
	}

	@Environment(EnvType.CLIENT)
	public float method_3830() {
		return this.field_4592;
	}

	public int getPermissionLevel(GameProfile gameProfile) {
		if (this.getConfigurationManager().isOperator(gameProfile)) {
			OperatorEntry operatorEntry = this.getConfigurationManager().getOpList().get(gameProfile);
			if (operatorEntry != null) {
				return operatorEntry.getPermissionLevel();
			} else if (this.isSinglePlayer()) {
				if (this.getUserName().equals(gameProfile.getName())) {
					return 4;
				} else {
					return this.getConfigurationManager().areCheatsAllowed() ? 4 : 0;
				}
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

	public class_3689 getProfiler() {
		return this.profiler;
	}
}
