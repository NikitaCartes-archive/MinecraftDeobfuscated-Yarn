package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4491;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.Session;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.crash.CrashReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class Main {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void main(String[] strings) {
		OptionParser optionParser = new OptionParser();
		optionParser.allowsUnrecognizedOptions();
		optionParser.accepts("demo");
		optionParser.accepts("fullscreen");
		optionParser.accepts("checkGlErrors");
		OptionSpec<String> optionSpec = optionParser.accepts("server").withRequiredArg();
		OptionSpec<Integer> optionSpec2 = optionParser.accepts("port").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(25565);
		OptionSpec<File> optionSpec3 = optionParser.accepts("gameDir").withRequiredArg().<File>ofType(File.class).defaultsTo(new File("."));
		OptionSpec<File> optionSpec4 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
		OptionSpec<File> optionSpec5 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
		OptionSpec<String> optionSpec6 = optionParser.accepts("proxyHost").withRequiredArg();
		OptionSpec<Integer> optionSpec7 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
		OptionSpec<String> optionSpec8 = optionParser.accepts("proxyUser").withRequiredArg();
		OptionSpec<String> optionSpec9 = optionParser.accepts("proxyPass").withRequiredArg();
		OptionSpec<String> optionSpec10 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + SystemUtil.getMeasuringTimeMs() % 1000L);
		OptionSpec<String> optionSpec11 = optionParser.accepts("uuid").withRequiredArg();
		OptionSpec<String> optionSpec12 = optionParser.accepts("accessToken").withRequiredArg().required();
		OptionSpec<String> optionSpec13 = optionParser.accepts("version").withRequiredArg().required();
		OptionSpec<Integer> optionSpec14 = optionParser.accepts("width").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(854);
		OptionSpec<Integer> optionSpec15 = optionParser.accepts("height").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(480);
		OptionSpec<Integer> optionSpec16 = optionParser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
		OptionSpec<Integer> optionSpec17 = optionParser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
		OptionSpec<String> optionSpec18 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
		OptionSpec<String> optionSpec19 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
		OptionSpec<String> optionSpec20 = optionParser.accepts("assetIndex").withRequiredArg();
		OptionSpec<String> optionSpec21 = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy");
		OptionSpec<String> optionSpec22 = optionParser.accepts("versionType").withRequiredArg().defaultsTo("release");
		OptionSpec<String> optionSpec23 = optionParser.nonOptions();
		OptionSet optionSet = optionParser.parse(strings);
		List<String> list = optionSet.valuesOf(optionSpec23);
		if (!list.isEmpty()) {
			System.out.println("Completely ignored arguments: " + list);
		}

		String string = getOption(optionSet, optionSpec6);
		Proxy proxy = Proxy.NO_PROXY;
		if (string != null) {
			try {
				proxy = new Proxy(Type.SOCKS, new InetSocketAddress(string, getOption(optionSet, optionSpec7)));
			} catch (Exception var66) {
			}
		}

		final String string2 = getOption(optionSet, optionSpec8);
		final String string3 = getOption(optionSet, optionSpec9);
		if (!proxy.equals(Proxy.NO_PROXY) && isNotNullOrEmpty(string2) && isNotNullOrEmpty(string3)) {
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(string2, string3.toCharArray());
				}
			});
		}

		int i = getOption(optionSet, optionSpec14);
		int j = getOption(optionSet, optionSpec15);
		OptionalInt optionalInt = toOptional(getOption(optionSet, optionSpec16));
		OptionalInt optionalInt2 = toOptional(getOption(optionSet, optionSpec17));
		boolean bl = optionSet.has("fullscreen");
		boolean bl2 = optionSet.has("demo");
		String string4 = getOption(optionSet, optionSpec13);
		Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new Serializer()).create();
		PropertyMap propertyMap = JsonHelper.deserialize(gson, getOption(optionSet, optionSpec18), PropertyMap.class);
		PropertyMap propertyMap2 = JsonHelper.deserialize(gson, getOption(optionSet, optionSpec19), PropertyMap.class);
		String string5 = getOption(optionSet, optionSpec22);
		File file = getOption(optionSet, optionSpec3);
		File file2 = optionSet.has(optionSpec4) ? getOption(optionSet, optionSpec4) : new File(file, "assets/");
		File file3 = optionSet.has(optionSpec5) ? getOption(optionSet, optionSpec5) : new File(file, "resourcepacks/");
		String string6 = optionSet.has(optionSpec11) ? optionSpec11.value(optionSet) : PlayerEntity.getOfflinePlayerUuid(optionSpec10.value(optionSet)).toString();
		String string7 = optionSet.has(optionSpec20) ? optionSpec20.value(optionSet) : null;
		String string8 = getOption(optionSet, optionSpec);
		Integer integer = getOption(optionSet, optionSpec2);
		Session session = new Session(optionSpec10.value(optionSet), string6, optionSpec12.value(optionSet), optionSpec21.value(optionSet));
		RunArgs runArgs = new RunArgs(
			new RunArgs.Network(session, propertyMap, propertyMap2, proxy),
			new WindowSettings(i, j, optionalInt, optionalInt2, bl),
			new RunArgs.Directories(file, file3, file2, string7),
			new RunArgs.Game(bl2, string4, string5),
			new RunArgs.AutoConnect(string8, integer)
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
		new class_4491();
		final MinecraftClient minecraftClient = new MinecraftClient(runArgs);
		Thread.currentThread().setName("Render thread");
		RenderSystem.initRenderThread();

		try {
			minecraftClient.init();
		} catch (Throwable var65) {
			CrashReport crashReport = CrashReport.create(var65, "Initializing game");
			crashReport.addElement("Initialization");
			minecraftClient.printCrashReport(minecraftClient.populateCrashReport(crashReport));
			return;
		}

		Thread thread2;
		if (minecraftClient.shouldRenderAsync()) {
			thread2 = new Thread("Client thread") {
				public void run() {
					try {
						RenderSystem.initClientThread();
						minecraftClient.start();
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
				minecraftClient.start();
			} catch (Throwable var64) {
				LOGGER.error("Unhandled game exception", var64);
			}
		}

		try {
			minecraftClient.scheduleStop();
			if (thread2 != null) {
				thread2.join();
			}
		} catch (InterruptedException var62) {
			LOGGER.error("Exception during client thread shutdown", (Throwable)var62);
		} finally {
			minecraftClient.stop();
		}
	}

	private static OptionalInt toOptional(@Nullable Integer integer) {
		return integer != null ? OptionalInt.of(integer) : OptionalInt.empty();
	}

	private static <T> T getOption(OptionSet optionSet, OptionSpec<T> optionSpec) {
		try {
			return optionSet.valueOf(optionSpec);
		} catch (Throwable var5) {
			if (optionSpec instanceof ArgumentAcceptingOptionSpec) {
				ArgumentAcceptingOptionSpec<T> argumentAcceptingOptionSpec = (ArgumentAcceptingOptionSpec<T>)optionSpec;
				List<T> list = argumentAcceptingOptionSpec.defaultValues();
				if (!list.isEmpty()) {
					return (T)list.get(0);
				}
			}

			throw var5;
		}
	}

	private static boolean isNotNullOrEmpty(String string) {
		return string != null && !string.isEmpty();
	}

	static {
		System.setProperty("java.awt.headless", "true");
	}
}
