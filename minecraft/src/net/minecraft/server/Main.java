package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.PathConverter;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.text.Text;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.util.profiling.jfr.InstanceType;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
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
		OptionSpec<Path> optionSpec15 = optionParser.accepts("pidFile").withRequiredArg().withValuesConvertedBy(new PathConverter());
		OptionSpec<String> optionSpec16 = optionParser.nonOptions();

		try {
			OptionSet optionSet = optionParser.parse(args);
			if (optionSet.has(optionSpec8)) {
				optionParser.printHelpOn(System.err);
				return;
			}

			Path path = optionSet.valueOf(optionSpec15);
			if (path != null) {
				writePidFile(path);
			}

			CrashReport.initCrashReport();
			if (optionSet.has(optionSpec14)) {
				FlightProfiler.INSTANCE.start(InstanceType.SERVER);
			}

			Bootstrap.initialize();
			Bootstrap.logMissing();
			Util.startTimerHack();
			Path path2 = Paths.get("server.properties");
			ServerPropertiesLoader serverPropertiesLoader = new ServerPropertiesLoader(path2);
			serverPropertiesLoader.store();
			Path path3 = Paths.get("eula.txt");
			EulaReader eulaReader = new EulaReader(path3);
			if (optionSet.has(optionSpec2)) {
				LOGGER.info("Initialized '{}' and '{}'", path2.toAbsolutePath(), path3.toAbsolutePath());
				return;
			}

			if (!eulaReader.isEulaAgreedTo()) {
				LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
				return;
			}

			File file = new File(optionSet.valueOf(optionSpec10));
			ApiServices apiServices = ApiServices.create(new YggdrasilAuthenticationService(Proxy.NO_PROXY), file);
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

			ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session.getDirectory(WorldSavePath.DATAPACKS));

			SaveLoader saveLoader;
			try {
				SaveLoading.ServerConfig serverConfig = createServerConfig(serverPropertiesLoader.getPropertiesHandler(), session, bl, resourcePackManager);
				saveLoader = (SaveLoader)Util.waitAndApply(
						applyExecutor -> SaveLoading.load(
								serverConfig,
								context -> {
									Registry<DimensionOptions> registry = context.dimensionsRegistryManager().get(RegistryKeys.DIMENSION);
									DynamicOps<NbtElement> dynamicOps = RegistryOps.of(NbtOps.INSTANCE, context.worldGenRegistryManager());
									Pair<SaveProperties, DimensionOptionsRegistryHolder.DimensionsConfig> pair = session.readLevelProperties(
										dynamicOps, context.dataConfiguration(), registry, context.worldGenRegistryManager().getRegistryLifecycle()
									);
									if (pair != null) {
										return new SaveLoading.LoadContext<>(pair.getFirst(), pair.getSecond().toDynamicRegistryManager());
									} else {
										LevelInfo levelInfo;
										GeneratorOptions generatorOptions;
										DimensionOptionsRegistryHolder dimensionOptionsRegistryHolder;
										if (optionSet.has(optionSpec3)) {
											levelInfo = MinecraftServer.DEMO_LEVEL_INFO;
											generatorOptions = GeneratorOptions.DEMO_OPTIONS;
											dimensionOptionsRegistryHolder = WorldPresets.createDemoOptions(context.worldGenRegistryManager());
										} else {
											ServerPropertiesHandler serverPropertiesHandler = serverPropertiesLoader.getPropertiesHandler();
											levelInfo = new LevelInfo(
												serverPropertiesHandler.levelName,
												serverPropertiesHandler.gameMode,
												serverPropertiesHandler.hardcore,
												serverPropertiesHandler.difficulty,
												false,
												new GameRules(),
												context.dataConfiguration()
											);
											generatorOptions = optionSet.has(optionSpec4)
												? serverPropertiesHandler.generatorOptions.withBonusChest(true)
												: serverPropertiesHandler.generatorOptions;
											dimensionOptionsRegistryHolder = serverPropertiesHandler.createDimensionsRegistryHolder(context.worldGenRegistryManager());
										}

										DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = dimensionOptionsRegistryHolder.toConfig(registry);
										Lifecycle lifecycle = dimensionsConfig.getLifecycle().add(context.worldGenRegistryManager().getRegistryLifecycle());
										return new SaveLoading.LoadContext<>(
											new LevelProperties(levelInfo, generatorOptions, dimensionsConfig.specialWorldProperty(), lifecycle), dimensionsConfig.toDynamicRegistryManager()
										);
									}
								},
								SaveLoader::new,
								Util.getMainWorkerExecutor(),
								applyExecutor
							)
					)
					.get();
			} catch (Exception var37) {
				LOGGER.warn(
					"Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", (Throwable)var37
				);
				return;
			}

			DynamicRegistryManager.Immutable immutable = saveLoader.combinedDynamicRegistries().getCombinedRegistryManager();
			if (optionSet.has(optionSpec5)) {
				forceUpgradeWorld(session, Schemas.getFixer(), optionSet.has(optionSpec6), () -> true, immutable.get(RegistryKeys.DIMENSION));
			}

			SaveProperties saveProperties = saveLoader.saveProperties();
			session.backupLevelDataFile(immutable, saveProperties);
			final MinecraftDedicatedServer minecraftDedicatedServer = MinecraftServer.startServer(
				threadx -> {
					MinecraftDedicatedServer minecraftDedicatedServerx = new MinecraftDedicatedServer(
						threadx, session, resourcePackManager, saveLoader, serverPropertiesLoader, Schemas.getFixer(), apiServices, WorldGenerationProgressLogger::new
					);
					minecraftDedicatedServerx.setHostProfile(optionSet.has(optionSpec9) ? new GameProfile(null, optionSet.valueOf(optionSpec9)) : null);
					minecraftDedicatedServerx.setServerPort(optionSet.valueOf(optionSpec12));
					minecraftDedicatedServerx.setDemo(optionSet.has(optionSpec3));
					minecraftDedicatedServerx.setServerId(optionSet.valueOf(optionSpec13));
					boolean blx = !optionSet.has(optionSpec) && !optionSet.valuesOf(optionSpec16).contains("nogui");
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
		} catch (Exception var38) {
			LOGGER.error(LogUtils.FATAL_MARKER, "Failed to start the minecraft server", (Throwable)var38);
		}
	}

	private static void writePidFile(Path path) {
		try {
			long l = ProcessHandle.current().pid();
			Files.writeString(path, Long.toString(l));
		} catch (IOException var3) {
			throw new UncheckedIOException(var3);
		}
	}

	private static SaveLoading.ServerConfig createServerConfig(
		ServerPropertiesHandler serverPropertiesHandler, LevelStorage.Session session, boolean safeMode, ResourcePackManager dataPackManager
	) {
		DataConfiguration dataConfiguration = session.getDataPackSettings();
		DataConfiguration dataConfiguration2;
		boolean bl;
		if (dataConfiguration != null) {
			bl = false;
			dataConfiguration2 = dataConfiguration;
		} else {
			bl = true;
			dataConfiguration2 = new DataConfiguration(serverPropertiesHandler.dataPackSettings, FeatureFlags.DEFAULT_ENABLED_FEATURES);
		}

		SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(dataPackManager, dataConfiguration2, safeMode, bl);
		return new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.DEDICATED, serverPropertiesHandler.functionPermissionLevel);
	}

	private static void forceUpgradeWorld(
		LevelStorage.Session session, DataFixer dataFixer, boolean eraseCache, BooleanSupplier continueCheck, Registry<DimensionOptions> dimensionOptionsRegistry
	) {
		LOGGER.info("Forcing world upgrade!");
		WorldUpdater worldUpdater = new WorldUpdater(session, dataFixer, dimensionOptionsRegistry, eraseCache);
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
