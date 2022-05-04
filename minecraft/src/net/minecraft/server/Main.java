package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.util.profiling.jfr.InstanceType;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.updater.WorldUpdater;
import org.slf4j.Logger;

public class Main {
	private static final Logger LOGGER = LogUtils.getLogger();

	@DontObfuscate
	public static void main(String[] args) {
		SharedConstants.createGameVersion();
		OptionParser optionParser = new OptionParser();
		OptionSpec<Void> optionSpec = optionParser.accepts("nogui");
		OptionSpec<Void> optionSpec2 = optionParser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
		OptionSpec<Void> optionSpec3 = optionParser.accepts("demo");
		OptionSpec<Void> optionSpec4 = optionParser.accepts("bonusChest");
		OptionSpec<Void> optionSpec5 = optionParser.accepts("forceUpgrade");
		OptionSpec<Void> optionSpec6 = optionParser.accepts("eraseCache");
		OptionSpec<Void> optionSpec7 = optionParser.accepts("safeMode", "Loads level with vanilla datapack only");
		OptionSpec<Void> optionSpec8 = optionParser.accepts("help").forHelp();
		OptionSpec<String> optionSpec9 = optionParser.accepts("singleplayer").withRequiredArg();
		OptionSpec<String> optionSpec10 = optionParser.accepts("universe").withRequiredArg().defaultsTo(".");
		OptionSpec<String> optionSpec11 = optionParser.accepts("world").withRequiredArg();
		OptionSpec<Integer> optionSpec12 = optionParser.accepts("port").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(-1);
		OptionSpec<String> optionSpec13 = optionParser.accepts("serverId").withRequiredArg();
		OptionSpec<Void> optionSpec14 = optionParser.accepts("jfrProfile");
		OptionSpec<String> optionSpec15 = optionParser.nonOptions();

		try {
			OptionSet optionSet = optionParser.parse(args);
			if (optionSet.has(optionSpec8)) {
				optionParser.printHelpOn(System.err);
				return;
			}

			CrashReport.initCrashReport();
			if (optionSet.has(optionSpec14)) {
				FlightProfiler.INSTANCE.start(InstanceType.SERVER);
			}

			Bootstrap.initialize();
			Bootstrap.logMissing();
			Util.startTimerHack();
			Path path = Paths.get("server.properties");
			ServerPropertiesLoader serverPropertiesLoader = new ServerPropertiesLoader(path);
			serverPropertiesLoader.store();
			Path path2 = Paths.get("eula.txt");
			EulaReader eulaReader = new EulaReader(path2);
			if (optionSet.has(optionSpec2)) {
				LOGGER.info("Initialized '{}' and '{}'", path.toAbsolutePath(), path2.toAbsolutePath());
				return;
			}

			if (!eulaReader.isEulaAgreedTo()) {
				LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
				return;
			}

			File file = new File(optionSet.valueOf(optionSpec10));
			YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY);
			MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
			GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
			UserCache userCache = new UserCache(gameProfileRepository, new File(file, MinecraftServer.USER_CACHE_FILE.getName()));
			String string = (String)Optional.ofNullable(optionSet.valueOf(optionSpec11)).orElse(serverPropertiesLoader.getPropertiesHandler().levelName);
			LevelStorage levelStorage = LevelStorage.create(file.toPath());
			LevelStorage.Session session = levelStorage.createSession(string);
			LevelSummary levelSummary = session.getLevelSummary();
			if (levelSummary != null) {
				if (levelSummary.requiresConversion()) {
					LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
					return;
				}

				if (!levelSummary.isVersionAvailable()) {
					LOGGER.info("This world was created by an incompatible version.");
					return;
				}
			}

			boolean bl = optionSet.has(optionSpec7);
			if (bl) {
				LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
			}

			ResourcePackManager resourcePackManager = new ResourcePackManager(
				ResourceType.SERVER_DATA,
				new VanillaDataPackProvider(),
				new FileResourcePackProvider(session.getDirectory(WorldSavePath.DATAPACKS).toFile(), ResourcePackSource.PACK_SOURCE_WORLD)
			);

			SaveLoader saveLoader;
			try {
				DataPackSettings dataPackSettings = (DataPackSettings)Objects.requireNonNullElse(session.getDataPackSettings(), DataPackSettings.SAFE_MODE);
				SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, dataPackSettings, bl);
				SaveLoading.ServerConfig serverConfig = new SaveLoading.ServerConfig(
					dataPacks, CommandManager.RegistrationEnvironment.DEDICATED, serverPropertiesLoader.getPropertiesHandler().functionPermissionLevel
				);
				saveLoader = (SaveLoader)Util.waitAndApply(
						executor -> SaveLoader.load(
								serverConfig,
								(resourceManager, dataPackSettingsx) -> {
									DynamicRegistryManager.Mutable mutable = DynamicRegistryManager.createAndLoad();
									DynamicOps<NbtElement> dynamicOps = RegistryOps.ofLoaded(NbtOps.INSTANCE, mutable, resourceManager);
									SaveProperties savePropertiesx = session.readLevelProperties(dynamicOps, dataPackSettingsx, mutable.getRegistryLifecycle());
									if (savePropertiesx != null) {
										return Pair.of(savePropertiesx, mutable.toImmutable());
									} else {
										LevelInfo levelInfo;
										GeneratorOptions generatorOptions;
										if (optionSet.has(optionSpec3)) {
											levelInfo = MinecraftServer.DEMO_LEVEL_INFO;
											generatorOptions = WorldPresets.createDemoOptions(mutable);
										} else {
											ServerPropertiesHandler serverPropertiesHandler = serverPropertiesLoader.getPropertiesHandler();
											levelInfo = new LevelInfo(
												serverPropertiesHandler.levelName,
												serverPropertiesHandler.gameMode,
												serverPropertiesHandler.hardcore,
												serverPropertiesHandler.difficulty,
												false,
												new GameRules(),
												dataPackSettingsx
											);
											generatorOptions = optionSet.has(optionSpec4)
												? serverPropertiesHandler.getGeneratorOptions(mutable).withBonusChest()
												: serverPropertiesHandler.getGeneratorOptions(mutable);
										}

										LevelProperties levelProperties = new LevelProperties(levelInfo, generatorOptions, Lifecycle.stable());
										return Pair.of(levelProperties, mutable.toImmutable());
									}
								},
								Util.getMainWorkerExecutor(),
								executor
							)
					)
					.get();
			} catch (Exception var38) {
				LOGGER.warn(
					"Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", (Throwable)var38
				);
				return;
			}

			DynamicRegistryManager.Immutable immutable = saveLoader.dynamicRegistryManager();
			serverPropertiesLoader.getPropertiesHandler().getGeneratorOptions(immutable);
			SaveProperties saveProperties = saveLoader.saveProperties();
			if (optionSet.has(optionSpec5)) {
				forceUpgradeWorld(session, Schemas.getFixer(), optionSet.has(optionSpec6), () -> true, saveProperties.getGeneratorOptions());
			}

			session.backupLevelDataFile(immutable, saveProperties);
			final MinecraftDedicatedServer minecraftDedicatedServer = MinecraftServer.startServer(
				threadx -> {
					MinecraftDedicatedServer minecraftDedicatedServerx = new MinecraftDedicatedServer(
						threadx,
						session,
						resourcePackManager,
						saveLoader,
						serverPropertiesLoader,
						Schemas.getFixer(),
						minecraftSessionService,
						gameProfileRepository,
						userCache,
						WorldGenerationProgressLogger::new
					);
					minecraftDedicatedServerx.setHostProfile(optionSet.has(optionSpec9) ? new GameProfile(null, optionSet.valueOf(optionSpec9)) : null);
					minecraftDedicatedServerx.setServerPort(optionSet.valueOf(optionSpec12));
					minecraftDedicatedServerx.setDemo(optionSet.has(optionSpec3));
					minecraftDedicatedServerx.setServerId(optionSet.valueOf(optionSpec13));
					boolean blx = !optionSet.has(optionSpec) && !optionSet.valuesOf(optionSpec15).contains("nogui");
					if (blx && !GraphicsEnvironment.isHeadless()) {
						minecraftDedicatedServerx.createGui();
					}

					return minecraftDedicatedServerx;
				}
			);
			Thread thread = new Thread("Server Shutdown Thread") {
				public void run() {
					minecraftDedicatedServer.stop(true);
				}
			};
			thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
			Runtime.getRuntime().addShutdownHook(thread);
		} catch (Exception var39) {
			LOGGER.error(LogUtils.FATAL_MARKER, "Failed to start the minecraft server", (Throwable)var39);
		}
	}

	private static void forceUpgradeWorld(
		LevelStorage.Session session, DataFixer dataFixer, boolean eraseCache, BooleanSupplier continueCheck, GeneratorOptions generatorOptions
	) {
		LOGGER.info("Forcing world upgrade!");
		WorldUpdater worldUpdater = new WorldUpdater(session, dataFixer, generatorOptions, eraseCache);
		Text text = null;

		while (!worldUpdater.isDone()) {
			Text text2 = worldUpdater.getStatus();
			if (text != text2) {
				text = text2;
				LOGGER.info(worldUpdater.getStatus().getString());
			}

			int i = worldUpdater.getTotalChunkCount();
			if (i > 0) {
				int j = worldUpdater.getUpgradedChunkCount() + worldUpdater.getSkippedChunkCount();
				LOGGER.info("{}% completed ({} / {} chunks)...", MathHelper.floor((float)j / (float)i * 100.0F), j, i);
			}

			if (!continueCheck.getAsBoolean()) {
				worldUpdater.cancel();
			} else {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException var10) {
				}
			}
		}
	}
}
