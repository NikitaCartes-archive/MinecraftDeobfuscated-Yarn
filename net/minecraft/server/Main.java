/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.minecraft.Bootstrap;
import net.minecraft.datafixer.Schemas;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.server.dedicated.EulaReader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        OptionParser optionParser = new OptionParser();
        OptionSpecBuilder optionSpec = optionParser.accepts("nogui");
        OptionSpecBuilder optionSpec2 = optionParser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
        OptionSpecBuilder optionSpec3 = optionParser.accepts("demo");
        OptionSpecBuilder optionSpec4 = optionParser.accepts("bonusChest");
        OptionSpecBuilder optionSpec5 = optionParser.accepts("forceUpgrade");
        OptionSpecBuilder optionSpec6 = optionParser.accepts("eraseCache");
        AbstractOptionSpec optionSpec7 = optionParser.accepts("help").forHelp();
        ArgumentAcceptingOptionSpec<String> optionSpec8 = optionParser.accepts("singleplayer").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> optionSpec9 = optionParser.accepts("universe").withRequiredArg().defaultsTo(".", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionSpec10 = optionParser.accepts("world").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> optionSpec11 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(-1, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<String> optionSpec12 = optionParser.accepts("serverId").withRequiredArg();
        NonOptionArgumentSpec<String> optionSpec13 = optionParser.nonOptions();
        try {
            boolean bl;
            SaveProperties saveProperties;
            OptionSet optionSet = optionParser.parse(args);
            if (optionSet.has(optionSpec7)) {
                optionParser.printHelpOn(System.err);
                return;
            }
            CrashReport.initCrashReport();
            Bootstrap.initialize();
            Bootstrap.logMissing();
            Path path = Paths.get("server.properties", new String[0]);
            ServerPropertiesLoader serverPropertiesLoader = new ServerPropertiesLoader(path);
            serverPropertiesLoader.store();
            Path path2 = Paths.get("eula.txt", new String[0]);
            EulaReader eulaReader = new EulaReader(path2);
            if (optionSet.has(optionSpec2)) {
                LOGGER.info("Initialized '{}' and '{}'", (Object)path.toAbsolutePath(), (Object)path2.toAbsolutePath());
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
            String string = Optional.ofNullable(optionSet.valueOf(optionSpec10)).orElse(serverPropertiesLoader.getPropertiesHandler().levelName);
            LevelStorage levelStorage = LevelStorage.create(file.toPath());
            LevelStorage.Session session = levelStorage.createSession(string);
            MinecraftServer.method_27725(session);
            if (optionSet.has(optionSpec5)) {
                Main.method_29173(session, Schemas.getFixer(), optionSet.has(optionSpec6), () -> true);
            }
            if ((saveProperties = session.readLevelProperties()) == null) {
                LevelInfo levelInfo;
                if (optionSet.has(optionSpec3)) {
                    levelInfo = MinecraftServer.DEMO_LEVEL_INFO;
                } else {
                    ServerPropertiesHandler serverPropertiesHandler = serverPropertiesLoader.getPropertiesHandler();
                    levelInfo = new LevelInfo(serverPropertiesHandler.levelName, serverPropertiesHandler.gameMode, serverPropertiesHandler.hardcore, serverPropertiesHandler.difficulty, false, new GameRules(), optionSet.has(optionSpec4) ? serverPropertiesHandler.field_24623.withBonusChest() : serverPropertiesHandler.field_24623);
                }
                saveProperties = new LevelProperties(levelInfo);
            }
            final MinecraftDedicatedServer minecraftDedicatedServer = new MinecraftDedicatedServer(session, saveProperties, serverPropertiesLoader, Schemas.getFixer(), minecraftSessionService, gameProfileRepository, userCache, WorldGenerationProgressLogger::new);
            minecraftDedicatedServer.setServerName(optionSet.valueOf(optionSpec8));
            minecraftDedicatedServer.setServerPort(optionSet.valueOf(optionSpec11));
            minecraftDedicatedServer.setDemo(optionSet.has(optionSpec3));
            minecraftDedicatedServer.setServerId(optionSet.valueOf(optionSpec12));
            boolean bl2 = bl = !optionSet.has(optionSpec) && !optionSet.valuesOf(optionSpec13).contains("nogui");
            if (bl && !GraphicsEnvironment.isHeadless()) {
                minecraftDedicatedServer.createGui();
            }
            minecraftDedicatedServer.start();
            Thread thread = new Thread("Server Shutdown Thread"){

                @Override
                public void run() {
                    minecraftDedicatedServer.stop(true);
                }
            };
            thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
            Runtime.getRuntime().addShutdownHook(thread);
        } catch (Exception exception) {
            LOGGER.fatal("Failed to start the minecraft server", (Throwable)exception);
        }
    }

    private static void method_29173(LevelStorage.Session session, DataFixer dataFixer, boolean bl, BooleanSupplier booleanSupplier) {
        LOGGER.info("Forcing world upgrade!");
        SaveProperties saveProperties = session.readLevelProperties();
        if (saveProperties != null) {
            WorldUpdater worldUpdater = new WorldUpdater(session, dataFixer, saveProperties, bl);
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

