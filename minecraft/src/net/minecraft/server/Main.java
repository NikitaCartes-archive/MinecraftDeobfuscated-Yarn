package net.minecraft.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.Bootstrap;
import net.minecraft.class_5219;
import net.minecraft.datafixer.Schemas;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.util.UserCache;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void main(String[] args) {
		OptionParser optionParser = new OptionParser();
		OptionSpec<Void> optionSpec = optionParser.accepts("nogui");
		OptionSpec<Void> optionSpec2 = optionParser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
		OptionSpec<Void> optionSpec3 = optionParser.accepts("demo");
		OptionSpec<Void> optionSpec4 = optionParser.accepts("bonusChest");
		OptionSpec<Void> optionSpec5 = optionParser.accepts("forceUpgrade");
		OptionSpec<Void> optionSpec6 = optionParser.accepts("eraseCache");
		OptionSpec<Void> optionSpec7 = optionParser.accepts("help").forHelp();
		OptionSpec<String> optionSpec8 = optionParser.accepts("singleplayer").withRequiredArg();
		OptionSpec<String> optionSpec9 = optionParser.accepts("universe").withRequiredArg().defaultsTo(".");
		OptionSpec<String> optionSpec10 = optionParser.accepts("world").withRequiredArg();
		OptionSpec<Integer> optionSpec11 = optionParser.accepts("port").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(-1);
		OptionSpec<String> optionSpec12 = optionParser.accepts("serverId").withRequiredArg();
		OptionSpec<String> optionSpec13 = optionParser.nonOptions();

		try {
			OptionSet optionSet = optionParser.parse(args);
			if (optionSet.has(optionSpec7)) {
				optionParser.printHelpOn(System.err);
				return;
			}

			CrashReport.initCrashReport();
			Bootstrap.initialize();
			Bootstrap.logMissing();
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

			File file = new File(optionSet.valueOf(optionSpec9));
			YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
			MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
			GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
			UserCache userCache = new UserCache(gameProfileRepository, new File(file, MinecraftServer.USER_CACHE_FILE.getName()));
			String string = (String)Optional.ofNullable(optionSet.valueOf(optionSpec10)).orElse(serverPropertiesLoader.getPropertiesHandler().levelName);
			LevelStorage levelStorage = LevelStorage.create(file.toPath());
			LevelStorage.Session session = levelStorage.createSession(string);
			MinecraftServer.method_27725(session, Schemas.getFixer(), optionSet.has(optionSpec5), optionSet.has(optionSpec6), () -> true);
			class_5219 lv = session.readLevelProperties();
			if (lv == null) {
				LevelInfo levelInfo;
				if (optionSet.has(optionSpec3)) {
					levelInfo = MinecraftServer.DEMO_LEVEL_INFO;
				} else {
					ServerPropertiesHandler serverPropertiesHandler = serverPropertiesLoader.getPropertiesHandler();
					levelInfo = new LevelInfo(
						serverPropertiesHandler.levelName,
						serverPropertiesHandler.gameMode,
						serverPropertiesHandler.hardcore,
						serverPropertiesHandler.difficulty,
						false,
						new GameRules(),
						optionSet.has(optionSpec4) ? serverPropertiesHandler.field_24623 : serverPropertiesHandler.field_24623.method_28036()
					);
				}

				lv = new LevelProperties(levelInfo);
			}

			final MinecraftDedicatedServer minecraftDedicatedServer = new MinecraftDedicatedServer(
				session, lv, serverPropertiesLoader, Schemas.getFixer(), minecraftSessionService, gameProfileRepository, userCache, WorldGenerationProgressLogger::new
			);
			minecraftDedicatedServer.setServerName(optionSet.valueOf(optionSpec8));
			minecraftDedicatedServer.setServerPort(optionSet.valueOf(optionSpec11));
			minecraftDedicatedServer.setDemo(optionSet.has(optionSpec3));
			minecraftDedicatedServer.setServerId(optionSet.valueOf(optionSpec12));
			boolean bl = !optionSet.has(optionSpec) && !optionSet.valuesOf(optionSpec13).contains("nogui");
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
		} catch (Exception var32) {
			LOGGER.fatal("Failed to start the minecraft server", (Throwable)var32);
		}
	}
}
