/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.util.GlException;
import net.minecraft.client.util.Session;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.WinNativeModuleUtil;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.util.profiling.jfr.InstanceType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class Main {
    static final Logger LOGGER = LogUtils.getLogger();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @DontObfuscate
    public static void main(String[] args) {
        Thread thread2;
        MinecraftClient minecraftClient;
        SharedConstants.createGameVersion();
        SharedConstants.method_43250();
        OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        optionParser.accepts("demo");
        optionParser.accepts("disableMultiplayer");
        optionParser.accepts("disableChat");
        optionParser.accepts("fullscreen");
        optionParser.accepts("checkGlErrors");
        OptionSpecBuilder optionSpec = optionParser.accepts("jfrProfile");
        ArgumentAcceptingOptionSpec<String> optionSpec2 = optionParser.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> optionSpec3 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<File> optionSpec4 = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), (File[])new File[0]);
        ArgumentAcceptingOptionSpec<File> optionSpec5 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> optionSpec6 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<String> optionSpec7 = optionParser.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> optionSpec8 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", (String[])new String[0]).ofType(Integer.class);
        ArgumentAcceptingOptionSpec<String> optionSpec9 = optionParser.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> optionSpec10 = optionParser.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> optionSpec11 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + Util.getMeasuringTimeMs() % 1000L, (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionSpec12 = optionParser.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> optionSpec13 = optionParser.accepts("xuid").withOptionalArg().defaultsTo("", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionSpec14 = optionParser.accepts("clientId").withOptionalArg().defaultsTo("", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionSpec15 = optionParser.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<String> optionSpec16 = optionParser.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<Integer> optionSpec17 = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<Integer> optionSpec18 = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<Integer> optionSpec19 = optionParser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec<Integer> optionSpec20 = optionParser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec<String> optionSpec21 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionSpec22 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionSpec23 = optionParser.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> optionSpec24 = optionParser.accepts("userType").withRequiredArg().defaultsTo(Session.AccountType.LEGACY.getName(), (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionSpec25 = optionParser.accepts("versionType").withRequiredArg().defaultsTo("release", (String[])new String[0]);
        NonOptionArgumentSpec<String> optionSpec26 = optionParser.nonOptions();
        OptionSet optionSet = optionParser.parse(args);
        List<String> list = optionSet.valuesOf(optionSpec26);
        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }
        String string = Main.getOption(optionSet, optionSpec7);
        Proxy proxy = Proxy.NO_PROXY;
        if (string != null) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(string, (int)Main.getOption(optionSet, optionSpec8)));
            } catch (Exception exception) {
                // empty catch block
            }
        }
        final String string2 = Main.getOption(optionSet, optionSpec9);
        final String string3 = Main.getOption(optionSet, optionSpec10);
        if (!proxy.equals(Proxy.NO_PROXY) && Main.isNotNullOrEmpty(string2) && Main.isNotNullOrEmpty(string3)) {
            Authenticator.setDefault(new Authenticator(){

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(string2, string3.toCharArray());
                }
            });
        }
        int i = Main.getOption(optionSet, optionSpec17);
        int j = Main.getOption(optionSet, optionSpec18);
        OptionalInt optionalInt = Main.toOptional(Main.getOption(optionSet, optionSpec19));
        OptionalInt optionalInt2 = Main.toOptional(Main.getOption(optionSet, optionSpec20));
        boolean bl = optionSet.has("fullscreen");
        boolean bl2 = optionSet.has("demo");
        boolean bl3 = optionSet.has("disableMultiplayer");
        boolean bl4 = optionSet.has("disableChat");
        String string4 = Main.getOption(optionSet, optionSpec16);
        Gson gson = new GsonBuilder().registerTypeAdapter((Type)((Object)PropertyMap.class), new PropertyMap.Serializer()).create();
        PropertyMap propertyMap = JsonHelper.deserialize(gson, Main.getOption(optionSet, optionSpec21), PropertyMap.class);
        PropertyMap propertyMap2 = JsonHelper.deserialize(gson, Main.getOption(optionSet, optionSpec22), PropertyMap.class);
        String string5 = Main.getOption(optionSet, optionSpec25);
        File file = Main.getOption(optionSet, optionSpec4);
        File file2 = optionSet.has(optionSpec5) ? Main.getOption(optionSet, optionSpec5) : new File(file, "assets/");
        File file3 = optionSet.has(optionSpec6) ? Main.getOption(optionSet, optionSpec6) : new File(file, "resourcepacks/");
        String string6 = optionSet.has(optionSpec12) ? (String)optionSpec12.value(optionSet) : DynamicSerializableUuid.getOfflinePlayerUuid((String)optionSpec11.value(optionSet)).toString();
        String string7 = optionSet.has(optionSpec23) ? (String)optionSpec23.value(optionSet) : null;
        String string8 = optionSet.valueOf(optionSpec13);
        String string9 = optionSet.valueOf(optionSpec14);
        String string10 = Main.getOption(optionSet, optionSpec2);
        Integer integer = Main.getOption(optionSet, optionSpec3);
        if (optionSet.has(optionSpec)) {
            FlightProfiler.INSTANCE.start(InstanceType.CLIENT);
        }
        CrashReport.initCrashReport();
        Bootstrap.initialize();
        Bootstrap.logMissing();
        Util.startTimerHack();
        String string11 = (String)optionSpec24.value(optionSet);
        Session.AccountType accountType = Session.AccountType.byName(string11);
        if (accountType == null) {
            LOGGER.warn("Unrecognized user type: {}", (Object)string11);
        }
        Session session = new Session((String)optionSpec11.value(optionSet), string6, (String)optionSpec15.value(optionSet), Main.toOptional(string8), Main.toOptional(string9), accountType);
        RunArgs runArgs = new RunArgs(new RunArgs.Network(session, propertyMap, propertyMap2, proxy), new WindowSettings(i, j, optionalInt, optionalInt2, bl), new RunArgs.Directories(file, file3, file2, string7), new RunArgs.Game(bl2, string4, string5, bl3, bl4), new RunArgs.AutoConnect(string10, integer));
        Thread thread = new Thread("Client Shutdown Thread"){

            @Override
            public void run() {
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                if (minecraftClient == null) {
                    return;
                }
                IntegratedServer integratedServer = minecraftClient.getServer();
                if (integratedServer != null) {
                    integratedServer.stop(true);
                }
            }
        };
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        Runtime.getRuntime().addShutdownHook(thread);
        try {
            Thread.currentThread().setName("Render thread");
            RenderSystem.initRenderThread();
            RenderSystem.beginInitialization();
            minecraftClient = new MinecraftClient(runArgs);
            RenderSystem.finishInitialization();
        } catch (GlException glException) {
            LOGGER.warn("Failed to create window: ", glException);
            return;
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Initializing game");
            CrashReportSection crashReportSection = crashReport.addElement("Initialization");
            WinNativeModuleUtil.addDetailTo(crashReportSection);
            MinecraftClient.addSystemDetailsToCrashReport(null, null, runArgs.game.version, null, crashReport);
            MinecraftClient.printCrashReport(crashReport);
            return;
        }
        if (minecraftClient.shouldRenderAsync()) {
            thread2 = new Thread("Game thread"){

                @Override
                public void run() {
                    try {
                        RenderSystem.initGameThread(true);
                        minecraftClient.run();
                    } catch (Throwable throwable) {
                        LOGGER.error("Exception in client thread", throwable);
                    }
                }
            };
            thread2.start();
            while (minecraftClient.isRunning()) {
            }
        } else {
            thread2 = null;
            try {
                RenderSystem.initGameThread(false);
                minecraftClient.run();
            } catch (Throwable throwable2) {
                LOGGER.error("Unhandled game exception", throwable2);
            }
        }
        BufferRenderer.unbindAll();
        try {
            minecraftClient.scheduleStop();
            if (thread2 != null) {
                thread2.join();
            }
        } catch (InterruptedException interruptedException) {
            LOGGER.error("Exception during client thread shutdown", interruptedException);
        } finally {
            minecraftClient.stop();
        }
    }

    private static Optional<String> toOptional(String string) {
        return string.isEmpty() ? Optional.empty() : Optional.of(string);
    }

    private static OptionalInt toOptional(@Nullable Integer i) {
        return i != null ? OptionalInt.of(i) : OptionalInt.empty();
    }

    @Nullable
    private static <T> T getOption(OptionSet optionSet, OptionSpec<T> optionSpec) {
        try {
            return optionSet.valueOf(optionSpec);
        } catch (Throwable throwable) {
            ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec;
            List list;
            if (optionSpec instanceof ArgumentAcceptingOptionSpec && !(list = (argumentAcceptingOptionSpec = (ArgumentAcceptingOptionSpec)optionSpec).defaultValues()).isEmpty()) {
                return (T)list.get(0);
            }
            throw throwable;
        }
    }

    private static boolean isNotNullOrEmpty(@Nullable String s) {
        return s != null && !s.isEmpty();
    }

    static {
        System.setProperty("java.awt.headless", "true");
    }
}

