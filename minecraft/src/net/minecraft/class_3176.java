package net.minecraft;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3176 extends MinecraftServer implements class_2994 {
	private static final Logger field_13814 = LogManager.getLogger();
	private static final Pattern field_13810 = Pattern.compile("^[a-fA-F0-9]{40}$");
	private final List<class_2976> field_13815 = Collections.synchronizedList(Lists.newArrayList());
	private class_3364 field_13816;
	private final class_3350 field_13811;
	private class_3408 field_13819;
	private final class_3807 field_16799;
	private class_1934 field_13813;
	@Nullable
	private class_3182 field_16800;

	public class_3176(
		File file,
		class_3807 arg,
		DataFixer dataFixer,
		YggdrasilAuthenticationService yggdrasilAuthenticationService,
		MinecraftSessionService minecraftSessionService,
		GameProfileRepository gameProfileRepository,
		class_3312 arg2,
		class_3950 arg3,
		String string
	) {
		super(
			file, Proxy.NO_PROXY, dataFixer, new class_2170(true), yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, arg2, arg3, string
		);
		this.field_16799 = arg;
		this.field_13811 = new class_3350(this);
		new Thread("Server Infinisleeper") {
			{
				this.setDaemon(true);
				this.setUncaughtExceptionHandler(new class_140(class_3176.field_13814));
				this.start();
			}

			public void run() {
				while (true) {
					try {
						Thread.sleep(2147483647L);
					} catch (InterruptedException var2) {
					}
				}
			}
		};
	}

	@Override
	public boolean method_3823() throws IOException {
		Thread thread = new Thread("Server console handler") {
			public void run() {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

				String string;
				try {
					while (!class_3176.this.method_3750() && class_3176.this.method_3806() && (string = bufferedReader.readLine()) != null) {
						class_3176.this.method_13947(string, class_3176.this.method_3739());
					}
				} catch (IOException var4) {
					class_3176.field_13814.error("Exception handling console input", (Throwable)var4);
				}
			}
		};
		thread.setDaemon(true);
		thread.setUncaughtExceptionHandler(new class_140(field_13814));
		thread.start();
		field_13814.info("Starting minecraft server version " + class_155.method_16673().getName());
		if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
			field_13814.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
		}

		field_13814.info("Loading properties");
		class_3806 lv = this.field_16799.method_16717();
		if (this.method_3724()) {
			this.method_3842("127.0.0.1");
		} else {
			this.method_3864(lv.field_16813);
			this.method_3764(lv.field_16839);
			this.method_3842(lv.field_16829);
		}

		this.method_3840(lv.field_16836);
		this.method_3769(lv.field_16809);
		this.method_3815(lv.field_16833);
		this.method_3745(lv.field_16807);
		this.method_3843(lv.field_16801, this.method_13950());
		this.method_3834(lv.field_16825);
		this.method_3794(lv.field_16827);
		super.method_3803(lv.field_16817.get());
		this.method_3731(lv.field_16805);
		this.field_13813 = lv.field_16841;
		field_13814.info("Default game type: {}", this.field_13813);
		InetAddress inetAddress = null;
		if (!this.method_3819().isEmpty()) {
			inetAddress = InetAddress.getByName(this.method_3819());
		}

		if (this.method_3756() < 0) {
			this.method_3779(lv.field_16837);
		}

		field_13814.info("Generating keypair");
		this.method_3853(class_3515.method_15237());
		field_13814.info("Starting Minecraft server on {}:{}", this.method_3819().isEmpty() ? "*" : this.method_3819(), this.method_3756());

		try {
			this.method_3787().method_14354(inetAddress, this.method_3756());
		} catch (IOException var17) {
			field_13814.warn("**** FAILED TO BIND TO PORT!");
			field_13814.warn("The exception was: {}", var17.toString());
			field_13814.warn("Perhaps a server is already running on that port?");
			return false;
		}

		if (!this.method_3828()) {
			field_13814.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
			field_13814.warn("The server will make no attempt to authenticate usernames. Beware.");
			field_13814.warn(
				"While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose."
			);
			field_13814.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
		}

		if (this.method_13951()) {
			this.method_3793().method_14518();
		}

		if (!class_3321.method_14540(this)) {
			return false;
		} else {
			this.method_3846(new class_3174(this));
			long l = class_156.method_648();
			String string = lv.field_16843;
			String string2 = lv.field_16822;
			long m = new Random().nextLong();
			if (!string.isEmpty()) {
				try {
					long n = Long.parseLong(string);
					if (n != 0L) {
						m = n;
					}
				} catch (NumberFormatException var16) {
					m = (long)string.hashCode();
				}
			}

			class_1942 lv2 = lv.field_16803;
			this.method_3850(lv.field_16810);
			class_2631.method_11337(this.method_3793());
			class_2631.method_11336(this.method_3844());
			class_3312.method_14510(this.method_3828());
			field_13814.info("Preparing level \"{}\"", this.method_3865());
			JsonObject jsonObject = new JsonObject();
			if (lv2 == class_1942.field_9277) {
				jsonObject.addProperty("flat_world_options", string2);
			} else if (!string2.isEmpty()) {
				jsonObject = class_3518.method_15285(string2);
			}

			this.method_3735(this.method_3865(), this.method_3865(), m, lv2, jsonObject);
			long o = class_156.method_648() - l;
			String string3 = String.format(Locale.ROOT, "%.3fs", (double)o / 1.0E9);
			field_13814.info("Done ({})! For help, type \"help\"", string3);
			if (lv.field_16830 != null) {
				this.method_3767().method_8359("announceAdvancements", lv.field_16830 ? "true" : "false", this);
			}

			if (lv.field_16819) {
				field_13814.info("Starting GS4 status listener");
				this.field_13816 = new class_3364(this);
				this.field_13816.method_14728();
			}

			if (lv.field_16818) {
				field_13814.info("Starting remote control listener");
				this.field_13819 = new class_3408(this);
				this.field_13819.method_14728();
			}

			if (this.method_13944() > 0L) {
				Thread thread2 = new Thread(new class_3178(this));
				thread2.setUncaughtExceptionHandler(new class_143(field_13814));
				thread2.setName("Server Watchdog");
				thread2.setDaemon(true);
				thread2.start();
			}

			class_1802.field_8162.method_7850(class_1761.field_7915, class_2371.method_10211());
			return true;
		}
	}

	public String method_13950() {
		class_3806 lv = this.field_16799.method_16717();
		String string;
		if (!lv.field_16821.isEmpty()) {
			string = lv.field_16821;
			if (!Strings.isNullOrEmpty(lv.field_16834)) {
				field_13814.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
			}
		} else if (!Strings.isNullOrEmpty(lv.field_16834)) {
			field_13814.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
			string = lv.field_16834;
		} else {
			string = "";
		}

		if (!string.isEmpty() && !field_13810.matcher(string).matches()) {
			field_13814.warn("Invalid sha1 for ressource-pack-sha1");
		}

		if (!lv.field_16801.isEmpty() && string.isEmpty()) {
			field_13814.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
		}

		return string;
	}

	@Override
	public void method_3838(class_1934 arg) {
		super.method_3838(arg);
		this.field_13813 = arg;
	}

	@Override
	public class_3806 method_16705() {
		return this.field_16799.method_16717();
	}

	@Override
	public boolean method_3792() {
		return this.method_16705().field_16826;
	}

	@Override
	public class_1934 method_3790() {
		return this.field_13813;
	}

	@Override
	public class_1267 method_3722() {
		return this.method_16705().field_16840;
	}

	@Override
	public boolean method_3754() {
		return this.method_16705().field_16838;
	}

	@Override
	public class_128 method_3859(class_128 arg) {
		arg = super.method_3859(arg);
		arg.method_567().method_577("Is Modded", () -> {
			String string = this.getServerModName();
			return !"vanilla".equals(string) ? "Definitely; Server brand changed to '" + string + "'" : "Unknown (can't tell)";
		});
		arg.method_567().method_577("Type", () -> "Dedicated Server (map_server.txt)");
		return arg;
	}

	@Override
	public void method_3821() {
		if (this.field_16800 != null) {
			this.field_16800.method_16750();
		}

		if (this.field_13819 != null) {
			this.field_13819.method_18050();
		}

		if (this.field_13816 != null) {
			this.field_13816.method_18050();
		}
	}

	@Override
	public void method_3813(BooleanSupplier booleanSupplier) {
		super.method_3813(booleanSupplier);
		this.method_13941();
	}

	@Override
	public boolean method_3839() {
		return this.method_16705().field_16811;
	}

	@Override
	public boolean method_3783() {
		return this.method_16705().field_16835;
	}

	@Override
	public void method_5495(class_1276 arg) {
		arg.method_5481("whitelist_enabled", this.method_13949().method_14614());
		arg.method_5481("whitelist_count", this.method_13949().method_14560().length);
		super.method_5495(arg);
	}

	public void method_13947(String string, class_2168 arg) {
		this.field_13815.add(new class_2976(string, arg));
	}

	public void method_13941() {
		while (!this.field_13815.isEmpty()) {
			class_2976 lv = (class_2976)this.field_13815.remove(0);
			this.method_3734().method_9249(lv.field_13378, lv.field_13377);
		}
	}

	@Override
	public boolean method_3816() {
		return true;
	}

	@Override
	public boolean method_3759() {
		return this.method_16705().field_16832;
	}

	public class_3174 method_13949() {
		return (class_3174)super.method_3760();
	}

	@Override
	public boolean method_3860() {
		return true;
	}

	@Override
	public String method_12929() {
		return this.method_3819();
	}

	@Override
	public int method_12918() {
		return this.method_3756();
	}

	@Override
	public String method_12930() {
		return this.method_3818();
	}

	public void method_13948() {
		if (this.field_16800 == null) {
			this.field_16800 = class_3182.method_13969(this);
		}
	}

	@Override
	public boolean method_3727() {
		return this.field_16800 != null;
	}

	@Override
	public boolean method_3763(class_1934 arg, boolean bl, int i) {
		return false;
	}

	@Override
	public boolean method_3812() {
		return this.method_16705().field_16806;
	}

	@Override
	public int method_3841() {
		return this.method_16705().field_16816;
	}

	@Override
	public boolean method_3785(class_1937 arg, class_2338 arg2, class_1657 arg3) {
		if (arg.field_9247.method_12460() != class_2874.field_13072) {
			return false;
		} else if (this.method_13949().method_14603().method_14641()) {
			return false;
		} else if (this.method_13949().method_14569(arg3.method_7334())) {
			return false;
		} else if (this.method_3841() <= 0) {
			return false;
		} else {
			class_2338 lv = arg.method_8395();
			int i = class_3532.method_15382(arg2.method_10263() - lv.method_10263());
			int j = class_3532.method_15382(arg2.method_10260() - lv.method_10260());
			int k = Math.max(i, j);
			return k <= this.method_3841();
		}
	}

	@Override
	public int method_3798() {
		return this.method_16705().field_16845;
	}

	@Override
	public void method_3803(int i) {
		super.method_3803(i);
		this.field_16799.method_16718(arg -> arg.field_16817.method_16745(i));
	}

	@Override
	public boolean method_3732() {
		return this.method_16705().field_16824;
	}

	@Override
	public boolean method_9201() {
		return this.method_16705().field_16802;
	}

	@Override
	public int method_3749() {
		return this.method_16705().field_16812;
	}

	@Override
	public int method_3773() {
		return this.method_16705().field_16842;
	}

	protected boolean method_13951() {
		boolean bl = false;

		for (int i = 0; !bl && i <= 2; i++) {
			if (i > 0) {
				field_13814.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
				this.method_13942();
			}

			bl = class_3321.method_14547(this);
		}

		boolean bl2 = false;

		for (int var7 = 0; !bl2 && var7 <= 2; var7++) {
			if (var7 > 0) {
				field_13814.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
				this.method_13942();
			}

			bl2 = class_3321.method_14545(this);
		}

		boolean bl3 = false;

		for (int var8 = 0; !bl3 && var8 <= 2; var8++) {
			if (var8 > 0) {
				field_13814.warn("Encountered a problem while converting the op list, retrying in a few seconds");
				this.method_13942();
			}

			bl3 = class_3321.method_14539(this);
		}

		boolean bl4 = false;

		for (int var9 = 0; !bl4 && var9 <= 2; var9++) {
			if (var9 > 0) {
				field_13814.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
				this.method_13942();
			}

			bl4 = class_3321.method_14533(this);
		}

		boolean bl5 = false;

		for (int var10 = 0; !bl5 && var10 <= 2; var10++) {
			if (var10 > 0) {
				field_13814.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
				this.method_13942();
			}

			bl5 = class_3321.method_14550(this);
		}

		return bl || bl2 || bl3 || bl4 || bl5;
	}

	private void method_13942() {
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException var2) {
		}
	}

	public long method_13944() {
		return this.method_16705().field_16815;
	}

	@Override
	public String method_12916() {
		return "";
	}

	@Override
	public String method_12934(String string) {
		this.field_13811.method_14702();
		this.method_3734().method_9249(this.field_13811.method_14700(), string);
		return this.field_13811.method_14701();
	}

	public void method_16712(boolean bl) {
		this.field_16799.method_16718(arg -> arg.field_16804.method_16745(bl));
	}

	@Override
	public void method_3782() {
		super.method_3782();
		class_156.method_18350();
	}

	@Override
	public boolean method_19466(GameProfile gameProfile) {
		return false;
	}
}
