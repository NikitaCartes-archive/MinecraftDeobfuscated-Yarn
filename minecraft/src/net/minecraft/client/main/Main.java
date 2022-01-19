package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
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
import net.minecraft.client.util.GlException;
import net.minecraft.client.util.Session;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
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
		SharedConstants.createGameVersion();
		OptionParser optionParser = new OptionParser();
		optionParser.allowsUnrecognizedOptions();
		optionParser.accepts("demo");
		optionParser.accepts("disableMultiplayer");
		optionParser.accepts("disableChat");
		optionParser.accepts("fullscreen");
		optionParser.accepts("checkGlErrors");
		OptionSpec<Void> optionSpec = optionParser.accepts("jfrProfile");
		OptionSpec<String> optionSpec2 = optionParser.accepts("server").withRequiredArg();
		OptionSpec<Integer> optionSpec3 = optionParser.accepts("port").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(25565);
		OptionSpec<File> optionSpec4 = optionParser.accepts("gameDir").withRequiredArg().<File>ofType(File.class).defaultsTo(new File("."));
		OptionSpec<File> optionSpec5 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
		OptionSpec<File> optionSpec6 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
		OptionSpec<String> optionSpec7 = optionParser.accepts("proxyHost").withRequiredArg();
		OptionSpec<Integer> optionSpec8 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
		OptionSpec<String> optionSpec9 = optionParser.accepts("proxyUser").withRequiredArg();
		OptionSpec<String> optionSpec10 = optionParser.accepts("proxyPass").withRequiredArg();
		OptionSpec<String> optionSpec11 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + Util.getMeasuringTimeMs() % 1000L);
		OptionSpec<String> optionSpec12 = optionParser.accepts("uuid").withRequiredArg();
		OptionSpec<String> optionSpec13 = optionParser.accepts("xuid").withOptionalArg().defaultsTo("");
		OptionSpec<String> optionSpec14 = optionParser.accepts("clientId").withOptionalArg().defaultsTo("");
		OptionSpec<String> optionSpec15 = optionParser.accepts("accessToken").withRequiredArg().required();
		OptionSpec<String> optionSpec16 = optionParser.accepts("version").withRequiredArg().required();
		OptionSpec<Integer> optionSpec17 = optionParser.accepts("width").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(854);
		OptionSpec<Integer> optionSpec18 = optionParser.accepts("height").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(480);
		OptionSpec<Integer> optionSpec19 = optionParser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
		OptionSpec<Integer> optionSpec20 = optionParser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
		OptionSpec<String> optionSpec21 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
		OptionSpec<String> optionSpec22 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
		OptionSpec<String> optionSpec23 = optionParser.accepts("assetIndex").withRequiredArg();
		OptionSpec<String> optionSpec24 = optionParser.accepts("userType").withRequiredArg().defaultsTo(Session.AccountType.LEGACY.getName());
		OptionSpec<String> optionSpec25 = optionParser.accepts("versionType").withRequiredArg().defaultsTo("release");
		OptionSpec<String> optionSpec26 = optionParser.nonOptions();
		OptionSet optionSet = optionParser.parse(args);
		List<String> list = optionSet.valuesOf(optionSpec26);
		if (!list.isEmpty()) {
			System.out.println("Completely ignored arguments: " + list);
		}

		String string = getOption(optionSet, optionSpec7);
		Proxy proxy = Proxy.NO_PROXY;
		if (string != null) {
			try {
				proxy = new Proxy(Type.SOCKS, new InetSocketAddress(string, getOption(optionSet, optionSpec8)));
			} catch (Exception var77) {
			}
		}

		final String string2 = getOption(optionSet, optionSpec9);
		final String string3 = getOption(optionSet, optionSpec10);
		if (!proxy.equals(Proxy.NO_PROXY) && isNotNullOrEmpty(string2) && isNotNullOrEmpty(string3)) {
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(string2, string3.toCharArray());
				}
			});
		}

		int i = getOption(optionSet, optionSpec17);
		int j = getOption(optionSet, optionSpec18);
		OptionalInt optionalInt = toOptional(getOption(optionSet, optionSpec19));
		OptionalInt optionalInt2 = toOptional(getOption(optionSet, optionSpec20));
		boolean bl = optionSet.has("fullscreen");
		boolean bl2 = optionSet.has("demo");
		boolean bl3 = optionSet.has("disableMultiplayer");
		boolean bl4 = optionSet.has("disableChat");
		String string4 = getOption(optionSet, optionSpec16);
		Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new Serializer()).create();
		PropertyMap propertyMap = JsonHelper.deserialize(gson, getOption(optionSet, optionSpec21), PropertyMap.class);
		PropertyMap propertyMap2 = JsonHelper.deserialize(gson, getOption(optionSet, optionSpec22), PropertyMap.class);
		String string5 = getOption(optionSet, optionSpec25);
		File file = getOption(optionSet, optionSpec4);
		File file2 = optionSet.has(optionSpec5) ? getOption(optionSet, optionSpec5) : new File(file, "assets/");
		File file3 = optionSet.has(optionSpec6) ? getOption(optionSet, optionSpec6) : new File(file, "resourcepacks/");
		String string6 = optionSet.has(optionSpec12) ? optionSpec12.value(optionSet) : PlayerEntity.getOfflinePlayerUuid(optionSpec11.value(optionSet)).toString();
		String string7 = optionSet.has(optionSpec23) ? optionSpec23.value(optionSet) : null;
		String string8 = optionSet.valueOf(optionSpec13);
		String string9 = optionSet.valueOf(optionSpec14);
		String string10 = getOption(optionSet, optionSpec2);
		Integer integer = getOption(optionSet, optionSpec3);
		if (optionSet.has(optionSpec)) {
			FlightProfiler.INSTANCE.start(InstanceType.CLIENT);
		}

		CrashReport.initCrashReport();
		Bootstrap.initialize();
		Bootstrap.logMissing();
		Util.startTimerHack();
		String string11 = optionSpec24.value(optionSet);
		Session.AccountType accountType = Session.AccountType.byName(string11);
		if (accountType == null) {
			LOGGER.warn("Unrecognized user type: {}", string11);
		}

		Session session = new Session(optionSpec11.value(optionSet), string6, optionSpec15.value(optionSet), toOptional(string8), toOptional(string9), accountType);
		RunArgs runArgs = new RunArgs(
			new RunArgs.Network(session, propertyMap, propertyMap2, proxy),
			new WindowSettings(i, j, optionalInt, optionalInt2, bl),
			new RunArgs.Directories(file, file3, file2, string7),
			new RunArgs.Game(bl2, string4, string5, bl3, bl4),
			new RunArgs.AutoConnect(string10, integer)
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
		} catch (GlException var75) {
			LOGGER.warn("Failed to create window: ", (Throwable)var75);
			return;
		} catch (Throwable var76) {
			CrashReport crashReport = CrashReport.create(var76, "Initializing game");
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
			} catch (Throwable var74) {
				LOGGER.error("Unhandled game exception", var74);
			}
		}

		BufferRenderer.unbindAll();

		try {
			minecraftClient.scheduleStop();
			if (thread2 != null) {
				thread2.join();
			}
		} catch (InterruptedException var72) {
			LOGGER.error("Exception during client thread shutdown", (Throwable)var72);
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
