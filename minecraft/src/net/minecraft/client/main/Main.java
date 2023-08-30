package net.minecraft.client.main;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.util.UndashedUuid;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.telemetry.GameLoadTimeEvent;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.util.GlException;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.WinNativeModuleUtil;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.util.profiling.jfr.InstanceType;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class Main {
	static final Logger LOGGER = LogUtils.getLogger();

	@DontObfuscate
	public static void main(String[] args) {
		Stopwatch stopwatch = Stopwatch.createStarted(Ticker.systemTicker());
		Stopwatch stopwatch2 = Stopwatch.createStarted(Ticker.systemTicker());
		GameLoadTimeEvent.INSTANCE.addTimer(TelemetryEventProperty.LOAD_TIME_TOTAL_TIME_MS, stopwatch);
		GameLoadTimeEvent.INSTANCE.addTimer(TelemetryEventProperty.LOAD_TIME_PRE_WINDOW_MS, stopwatch2);
		SharedConstants.createGameVersion();
		SharedConstants.enableDataFixerOptimization();
		OptionParser optionParser = new OptionParser();
		optionParser.allowsUnrecognizedOptions();
		optionParser.accepts("demo");
		optionParser.accepts("disableMultiplayer");
		optionParser.accepts("disableChat");
		optionParser.accepts("fullscreen");
		optionParser.accepts("checkGlErrors");
		OptionSpec<Void> optionSpec = optionParser.accepts("jfrProfile");
		OptionSpec<String> optionSpec2 = optionParser.accepts("quickPlayPath").withRequiredArg();
		OptionSpec<String> optionSpec3 = optionParser.accepts("quickPlaySingleplayer").withRequiredArg();
		OptionSpec<String> optionSpec4 = optionParser.accepts("quickPlayMultiplayer").withRequiredArg();
		OptionSpec<String> optionSpec5 = optionParser.accepts("quickPlayRealms").withRequiredArg();
		OptionSpec<File> optionSpec6 = optionParser.accepts("gameDir").withRequiredArg().<File>ofType(File.class).defaultsTo(new File("."));
		OptionSpec<File> optionSpec7 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
		OptionSpec<File> optionSpec8 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
		OptionSpec<String> optionSpec9 = optionParser.accepts("proxyHost").withRequiredArg();
		OptionSpec<Integer> optionSpec10 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
		OptionSpec<String> optionSpec11 = optionParser.accepts("proxyUser").withRequiredArg();
		OptionSpec<String> optionSpec12 = optionParser.accepts("proxyPass").withRequiredArg();
		OptionSpec<String> optionSpec13 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + Util.getMeasuringTimeMs() % 1000L);
		OptionSpec<String> optionSpec14 = optionParser.accepts("uuid").withRequiredArg();
		OptionSpec<String> optionSpec15 = optionParser.accepts("xuid").withOptionalArg().defaultsTo("");
		OptionSpec<String> optionSpec16 = optionParser.accepts("clientId").withOptionalArg().defaultsTo("");
		OptionSpec<String> optionSpec17 = optionParser.accepts("accessToken").withRequiredArg().required();
		OptionSpec<String> optionSpec18 = optionParser.accepts("version").withRequiredArg().required();
		OptionSpec<Integer> optionSpec19 = optionParser.accepts("width").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(854);
		OptionSpec<Integer> optionSpec20 = optionParser.accepts("height").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(480);
		OptionSpec<Integer> optionSpec21 = optionParser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
		OptionSpec<Integer> optionSpec22 = optionParser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
		OptionSpec<String> optionSpec23 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
		OptionSpec<String> optionSpec24 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
		OptionSpec<String> optionSpec25 = optionParser.accepts("assetIndex").withRequiredArg();
		OptionSpec<String> optionSpec26 = optionParser.accepts("userType").withRequiredArg().defaultsTo(Session.AccountType.LEGACY.getName());
		OptionSpec<String> optionSpec27 = optionParser.accepts("versionType").withRequiredArg().defaultsTo("release");
		OptionSpec<String> optionSpec28 = optionParser.nonOptions();
		OptionSet optionSet = optionParser.parse(args);
		List<String> list = optionSet.valuesOf(optionSpec28);
		if (!list.isEmpty()) {
			LOGGER.info("Completely ignored arguments: " + list);
		}

		String string = getOption(optionSet, optionSpec9);
		Proxy proxy = Proxy.NO_PROXY;
		if (string != null) {
			try {
				proxy = new Proxy(Type.SOCKS, new InetSocketAddress(string, getOption(optionSet, optionSpec10)));
			} catch (Exception var83) {
			}
		}

		final String string2 = getOption(optionSet, optionSpec11);
		final String string3 = getOption(optionSet, optionSpec12);
		if (!proxy.equals(Proxy.NO_PROXY) && isNotNullOrEmpty(string2) && isNotNullOrEmpty(string3)) {
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(string2, string3.toCharArray());
				}
			});
		}

		int i = getOption(optionSet, optionSpec19);
		int j = getOption(optionSet, optionSpec20);
		OptionalInt optionalInt = toOptional(getOption(optionSet, optionSpec21));
		OptionalInt optionalInt2 = toOptional(getOption(optionSet, optionSpec22));
		boolean bl = optionSet.has("fullscreen");
		boolean bl2 = optionSet.has("demo");
		boolean bl3 = optionSet.has("disableMultiplayer");
		boolean bl4 = optionSet.has("disableChat");
		String string4 = getOption(optionSet, optionSpec18);
		Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new Serializer()).create();
		PropertyMap propertyMap = JsonHelper.deserialize(gson, getOption(optionSet, optionSpec23), PropertyMap.class);
		PropertyMap propertyMap2 = JsonHelper.deserialize(gson, getOption(optionSet, optionSpec24), PropertyMap.class);
		String string5 = getOption(optionSet, optionSpec27);
		File file = getOption(optionSet, optionSpec6);
		File file2 = optionSet.has(optionSpec7) ? getOption(optionSet, optionSpec7) : new File(file, "assets/");
		File file3 = optionSet.has(optionSpec8) ? getOption(optionSet, optionSpec8) : new File(file, "resourcepacks/");
		UUID uUID = optionSet.has(optionSpec14)
			? UndashedUuid.fromStringLenient(optionSpec14.value(optionSet))
			: Uuids.getOfflinePlayerUuid(optionSpec13.value(optionSet));
		String string6 = optionSet.has(optionSpec25) ? optionSpec25.value(optionSet) : null;
		String string7 = optionSet.valueOf(optionSpec15);
		String string8 = optionSet.valueOf(optionSpec16);
		String string9 = getOption(optionSet, optionSpec2);
		String string10 = getOption(optionSet, optionSpec3);
		String string11 = getOption(optionSet, optionSpec4);
		String string12 = getOption(optionSet, optionSpec5);
		if (optionSet.has(optionSpec)) {
			FlightProfiler.INSTANCE.start(InstanceType.CLIENT);
		}

		CrashReport.initCrashReport();
		Bootstrap.initialize();
		GameLoadTimeEvent.INSTANCE.setBootstrapTime(Bootstrap.LOAD_TIME.get());
		Bootstrap.logMissing();
		Util.startTimerHack();
		String string13 = optionSpec26.value(optionSet);
		Session.AccountType accountType = Session.AccountType.byName(string13);
		if (accountType == null) {
			LOGGER.warn("Unrecognized user type: {}", string13);
		}

		Session session = new Session(optionSpec13.value(optionSet), uUID, optionSpec17.value(optionSet), toOptional(string7), toOptional(string8), accountType);
		RunArgs runArgs = new RunArgs(
			new RunArgs.Network(session, propertyMap, propertyMap2, proxy),
			new WindowSettings(i, j, optionalInt, optionalInt2, bl),
			new RunArgs.Directories(file, file3, file2, string6),
			new RunArgs.Game(bl2, string4, string5, bl3, bl4),
			new RunArgs.QuickPlay(string9, string10, string11, string12)
		);
		Thread thread = new Thread("Client Shutdown Thread") {
			public void run() {
				MinecraftClient minecraftClient = MinecraftClient.getInstance();
				if (minecraftClient != null) {
					IntegratedServer integratedServer = minecraftClient.getServer();
					if (integratedServer != null) {
						integratedServer.stop(true);
					}
				}
			}
		};
		thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
		Runtime.getRuntime().addShutdownHook(thread);

		final MinecraftClient minecraftClient;
		try {
			Thread.currentThread().setName("Render thread");
			RenderSystem.initRenderThread();
			RenderSystem.beginInitialization();
			minecraftClient = new MinecraftClient(runArgs);
			RenderSystem.finishInitialization();
		} catch (GlException var81) {
			LOGGER.warn("Failed to create window: ", (Throwable)var81);
			return;
		} catch (Throwable var82) {
			CrashReport crashReport = CrashReport.create(var82, "Initializing game");
			CrashReportSection crashReportSection = crashReport.addElement("Initialization");
			WinNativeModuleUtil.addDetailTo(crashReportSection);
			MinecraftClient.addSystemDetailsToCrashReport(null, null, runArgs.game.version, null, crashReport);
			MinecraftClient.printCrashReport(crashReport);
			return;
		}

		Thread thread2;
		if (minecraftClient.shouldRenderAsync()) {
			thread2 = new Thread("Game thread") {
				public void run() {
					try {
						RenderSystem.initGameThread(true);
						minecraftClient.run();
					} catch (Throwable var2) {
						Main.LOGGER.error("Exception in client thread", var2);
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
			} catch (Throwable var80) {
				LOGGER.error("Unhandled game exception", var80);
			}
		}

		BufferRenderer.reset();

		try {
			minecraftClient.scheduleStop();
			if (thread2 != null) {
				thread2.join();
			}
		} catch (InterruptedException var78) {
			LOGGER.error("Exception during client thread shutdown", (Throwable)var78);
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
		} catch (Throwable var5) {
			if (optionSpec instanceof ArgumentAcceptingOptionSpec<T> argumentAcceptingOptionSpec) {
				List<T> list = argumentAcceptingOptionSpec.defaultValues();
				if (!list.isEmpty()) {
					return (T)list.get(0);
				}
			}

			throw var5;
		}
	}

	private static boolean isNotNullOrEmpty(@Nullable String s) {
		return s != null && !s.isEmpty();
	}

	static {
		System.setProperty("java.awt.headless", "true");
	}
}
