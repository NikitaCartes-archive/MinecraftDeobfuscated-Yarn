/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.minecraft.Bootstrap;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressLogger;
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
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
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
        OptionSpecBuilder optionSpec7 = optionParser.accepts("safeMode", "Loads level with vanilla datapack only");
        AbstractOptionSpec optionSpec8 = optionParser.accepts("help").forHelp();
        ArgumentAcceptingOptionSpec<String> optionSpec9 = optionParser.accepts("singleplayer").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> optionSpec10 = optionParser.accepts("universe").withRequiredArg().defaultsTo(".", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionSpec11 = optionParser.accepts("world").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> optionSpec12 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(-1, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<String> optionSpec13 = optionParser.accepts("serverId").withRequiredArg();
        NonOptionArgumentSpec<String> optionSpec14 = optionParser.nonOptions();
        try {
            ServerResourceManager serverResourceManager;
            OptionSet optionSet = optionParser.parse(args);
            if (optionSet.has(optionSpec8)) {
                optionParser.printHelpOn(System.err);
                return;
            }
            CrashReport.initCrashReport();
            Bootstrap.initialize();
            Bootstrap.logMissing();
            Util.startTimerHack();
            DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
            Path path = Paths.get("server.properties", new String[0]);
            ServerPropertiesLoader serverPropertiesLoader = new ServerPropertiesLoader(impl, path);
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
            File file = new File(optionSet.valueOf(optionSpec10));
            YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY);
            MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
            UserCache userCache = new UserCache(gameProfileRepository, new File(file, MinecraftServer.USER_CACHE_FILE.getName()));
            String string = Optional.ofNullable(optionSet.valueOf(optionSpec11)).orElse(serverPropertiesLoader.getPropertiesHandler().levelName);
            LevelStorage levelStorage = LevelStorage.create(file.toPath());
            LevelStorage.Session session = levelStorage.createSession(string);
            MinecraftServer.convertLevel(session);
            LevelSummary levelSummary = session.getLevelSummary();
            if (levelSummary != null && levelSummary.isPreWorldHeightChangeVersion()) {
                LOGGER.info("Loading of old worlds is temporarily disabled.");
                return;
            }
            DataPackSettings dataPackSettings = session.getDataPackSettings();
            boolean bl = optionSet.has(optionSpec7);
            if (bl) {
                LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
            }
            ResourcePackManager resourcePackManager = new ResourcePackManager(ResourceType.SERVER_DATA, new VanillaDataPackProvider(), new FileResourcePackProvider(session.getDirectory(WorldSavePath.DATAPACKS).toFile(), ResourcePackSource.PACK_SOURCE_WORLD));
            DataPackSettings dataPackSettings2 = MinecraftServer.loadDataPacks(resourcePackManager, dataPackSettings == null ? DataPackSettings.SAFE_MODE : dataPackSettings, bl);
            CompletableFuture<ServerResourceManager> completableFuture = ServerResourceManager.reload(resourcePackManager.createResourcePacks(), impl, CommandManager.RegistrationEnvironment.DEDICATED, serverPropertiesLoader.getPropertiesHandler().functionPermissionLevel, Util.getMainWorkerExecutor(), Runnable::run);
            try {
                serverResourceManager = completableFuture.get();
            } catch (Exception exception) {
                LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", (Throwable)exception);
                resourcePackManager.close();
                return;
            }
            serverResourceManager.loadRegistryTags();
            RegistryOps<Tag> registryOps = RegistryOps.of(NbtOps.INSTANCE, serverResourceManager.getResourceManager(), impl);
            SaveProperties saveProperties = session.readLevelProperties(registryOps, dataPackSettings2);
            if (saveProperties == null) {
                GeneratorOptions generatorOptions;
                LevelInfo levelInfo;
                if (optionSet.has(optionSpec3)) {
                    levelInfo = MinecraftServer.DEMO_LEVEL_INFO;
                    generatorOptions = GeneratorOptions.createDemo(impl);
                } else {
                    ServerPropertiesHandler serverPropertiesHandler = serverPropertiesLoader.getPropertiesHandler();
                    levelInfo = new LevelInfo(serverPropertiesHandler.levelName, serverPropertiesHandler.gameMode, serverPropertiesHandler.hardcore, serverPropertiesHandler.difficulty, false, new GameRules(), dataPackSettings2);
                    generatorOptions = optionSet.has(optionSpec4) ? serverPropertiesHandler.generatorOptions.withBonusChest() : serverPropertiesHandler.generatorOptions;
                }
                saveProperties = new LevelProperties(levelInfo, generatorOptions, Lifecycle.stable());
            }
            if (optionSet.has(optionSpec5)) {
                Main.forceUpgradeWorld(session, Schemas.getFixer(), optionSet.has(optionSpec6), () -> true, saveProperties.getGeneratorOptions().getWorlds());
            }
            session.backupLevelDataFile(impl, saveProperties);
            SaveProperties saveProperties2 = saveProperties;
            final MinecraftDedicatedServer minecraftDedicatedServer = MinecraftServer.startServer(serverThread -> {
                boolean bl;
                MinecraftDedicatedServer minecraftDedicatedServer = new MinecraftDedicatedServer((Thread)serverThread, impl, session, resourcePackManager, serverResourceManager, saveProperties2, serverPropertiesLoader, Schemas.getFixer(), minecraftSessionService, gameProfileRepository, userCache, WorldGenerationProgressLogger::new);
                minecraftDedicatedServer.setServerName((String)optionSet.valueOf(optionSpec9));
                minecraftDedicatedServer.setServerPort((Integer)optionSet.valueOf(optionSpec12));
                minecraftDedicatedServer.setDemo(optionSet.has(optionSpec3));
                minecraftDedicatedServer.setServerId((String)optionSet.valueOf(optionSpec13));
                boolean bl2 = bl = !optionSet.has(optionSpec) && !optionSet.valuesOf(optionSpec14).contains("nogui");
                if (bl && !GraphicsEnvironment.isHeadless()) {
                    minecraftDedicatedServer.createGui();
                }
                return minecraftDedicatedServer;
            });
            Thread thread = new Thread("Server Shutdown Thread"){

                @Override
                public void run() {
                    minecraftDedicatedServer.stop(true);
                }
            };
            thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
            Runtime.getRuntime().addShutdownHook(thread);
        } catch (Exception exception2) {
            LOGGER.fatal("Failed to start the minecraft server", (Throwable)exception2);
        }
    }

    private static void forceUpgradeWorld(LevelStorage.Session session, DataFixer dataFixer, boolean eraseCache, BooleanSupplier booleanSupplier, ImmutableSet<RegistryKey<World>> worlds) {
        LOGGER.info("Forcing world upgrade!");
        WorldUpdater worldUpdater = new WorldUpdater(session, dataFixer, worlds, eraseCache);
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

