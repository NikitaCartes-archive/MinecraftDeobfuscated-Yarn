package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.Optional;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1132;
import net.minecraft.class_140;
import net.minecraft.class_156;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_320;
import net.minecraft.class_3518;
import net.minecraft.class_542;
import net.minecraft.class_543;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class Main {
	private static final Logger field_12138 = LogManager.getLogger();

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
		OptionSpec<String> optionSpec10 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + class_156.method_658() % 1000L);
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

		String string = method_11428(optionSet, optionSpec6);
		Proxy proxy = Proxy.NO_PROXY;
		if (string != null) {
			try {
				proxy = new Proxy(Type.SOCKS, new InetSocketAddress(string, method_11428(optionSet, optionSpec7)));
			} catch (Exception var52) {
			}
		}

		final String string2 = method_11428(optionSet, optionSpec8);
		final String string3 = method_11428(optionSet, optionSpec9);
		if (!proxy.equals(Proxy.NO_PROXY) && method_11429(string2) && method_11429(string3)) {
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(string2, string3.toCharArray());
				}
			});
		}

		int i = method_11428(optionSet, optionSpec14);
		int j = method_11428(optionSet, optionSpec15);
		Optional<Integer> optional = Optional.ofNullable(method_11428(optionSet, optionSpec16));
		Optional<Integer> optional2 = Optional.ofNullable(method_11428(optionSet, optionSpec17));
		boolean bl = optionSet.has("fullscreen");
		boolean bl2 = true;
		String string4 = method_11428(optionSet, optionSpec13);
		Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new Serializer()).create();
		PropertyMap propertyMap = class_3518.method_15284(gson, method_11428(optionSet, optionSpec18), PropertyMap.class);
		PropertyMap propertyMap2 = class_3518.method_15284(gson, method_11428(optionSet, optionSpec19), PropertyMap.class);
		String string5 = method_11428(optionSet, optionSpec22);
		File file = method_11428(optionSet, optionSpec3);
		File file2 = optionSet.has(optionSpec4) ? method_11428(optionSet, optionSpec4) : new File(file, "assets/");
		File file3 = optionSet.has(optionSpec5) ? method_11428(optionSet, optionSpec5) : new File(file, "resourcepacks/");
		String string6 = optionSet.has(optionSpec11) ? optionSpec11.value(optionSet) : class_1657.method_7310(optionSpec10.value(optionSet)).toString();
		String string7 = optionSet.has(optionSpec20) ? optionSpec20.value(optionSet) : null;
		String string8 = method_11428(optionSet, optionSpec);
		Integer integer = method_11428(optionSet, optionSpec2);
		class_320 lv = new class_320(optionSpec10.value(optionSet), string6, optionSpec12.value(optionSet), optionSpec21.value(optionSet));
		class_542 lv2 = new class_542(
			new class_542.class_547(lv, propertyMap, propertyMap2, proxy),
			new class_543(i, j, optional, optional2, bl),
			new class_542.class_544(file, file3, file2, string7),
			new class_542.class_545(true, string4, string5),
			new class_542.class_546(string8, integer)
		);
		Thread thread = new Thread("Client Shutdown Thread") {
			public void run() {
				class_310 lv = class_310.method_1551();
				if (lv != null) {
					class_1132 lv2 = lv.method_1576();
					if (lv2 != null) {
						lv2.method_3747(true);
					}
				}
			}
		};
		thread.setUncaughtExceptionHandler(new class_140(field_12138));
		Runtime.getRuntime().addShutdownHook(thread);
		Thread.currentThread().setName("Client thread");
		new class_310(lv2).method_1514();
	}

	private static <T> T method_11428(OptionSet optionSet, OptionSpec<T> optionSpec) {
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

	private static boolean method_11429(String string) {
		return string != null && !string.isEmpty();
	}

	static {
		System.setProperty("java.awt.headless", "true");
	}
}
