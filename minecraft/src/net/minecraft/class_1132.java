package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1132 extends MinecraftServer {
	private static final Logger field_5520 = LogManager.getLogger();
	private final class_310 field_5518;
	private final class_1940 field_5523;
	private boolean field_5524;
	private int field_5522 = -1;
	private class_1133 field_5519;
	private UUID field_5521;

	public class_1132(
		class_310 arg,
		String string,
		String string2,
		class_1940 arg2,
		YggdrasilAuthenticationService yggdrasilAuthenticationService,
		MinecraftSessionService minecraftSessionService,
		GameProfileRepository gameProfileRepository,
		class_3312 arg3,
		class_3950 arg4
	) {
		super(
			new File(arg.field_1697, "saves"),
			arg.method_1487(),
			arg.method_1543(),
			new class_2170(false),
			yggdrasilAuthenticationService,
			minecraftSessionService,
			gameProfileRepository,
			arg3,
			arg4,
			string
		);
		this.method_3817(arg.method_1548().method_1676());
		this.method_3849(string2);
		this.method_3730(arg.method_1530());
		this.method_3778(arg2.method_8581());
		this.method_3850(256);
		this.method_3846(new class_1130(this));
		this.field_5518 = arg;
		this.field_5523 = arg2;
	}

	@Override
	public void method_3735(String string, String string2, long l, class_1942 arg, JsonElement jsonElement) {
		this.method_3755(string);
		class_29 lv = this.method_3781().method_242(string, this);
		this.method_3861(this.method_3865(), lv);
		class_31 lv2 = lv.method_133();
		if (lv2 == null) {
			lv2 = new class_31(this.field_5523, string2);
		} else {
			lv2.method_182(string2);
		}

		this.method_3800(lv.method_132(), lv2);
		class_3949 lv3 = this.field_17439.create(11);
		this.method_3786(lv, lv2, this.field_5523, lv3);
		if (this.method_3847(class_2874.field_13072).method_8401().method_207() == null) {
			this.method_3776(this.field_5518.field_1690.field_1851, true);
		}

		this.method_3774(lv3);
	}

	@Override
	public boolean method_3823() throws IOException {
		field_5520.info("Starting integrated minecraft server version " + class_155.method_16673().getName());
		this.method_3864(true);
		this.method_3840(true);
		this.method_3769(true);
		this.method_3815(true);
		this.method_3745(true);
		field_5520.info("Generating keypair");
		this.method_3853(class_3515.method_15237());
		this.method_3735(this.method_3865(), this.method_3726(), this.field_5523.method_8577(), this.field_5523.method_8576(), this.field_5523.method_8584());
		this.method_3834(this.method_3811() + " - " + this.method_3847(class_2874.field_13072).method_8401().method_150());
		return true;
	}

	@Override
	public void method_3748(BooleanSupplier booleanSupplier) {
		boolean bl = this.field_5524;
		this.field_5524 = class_310.method_1551().method_1562() != null && class_310.method_1551().method_1493();
		class_3689 lv = this.method_16044();
		if (!bl && this.field_5524) {
			lv.method_15396("autoSave");
			field_5520.info("Saving and pausing game...");
			this.method_3760().method_14617();
			this.method_3723(false, true, false);
			lv.method_15407();
		}

		if (!this.field_5524) {
			super.method_3748(booleanSupplier);
			int i = Math.max(2, this.field_5518.field_1690.field_1870 + -2);
			if (i != this.method_3760().method_14568()) {
				field_5520.info("Changing view distance to {}, from {}", i, this.method_3760().method_14568());
				this.method_3760().method_14608(i, i - 2);
			}
		}
	}

	@Override
	public boolean method_3792() {
		return false;
	}

	@Override
	public class_1934 method_3790() {
		return this.field_5523.method_8574();
	}

	@Override
	public class_1267 method_3722() {
		return this.field_5518.field_1687.method_8401().method_207();
	}

	@Override
	public boolean method_3754() {
		return this.field_5523.method_8582();
	}

	@Override
	public boolean method_3732() {
		return true;
	}

	@Override
	public boolean method_9201() {
		return true;
	}

	@Override
	public File method_3831() {
		return this.field_5518.field_1697;
	}

	@Override
	public boolean method_3816() {
		return false;
	}

	@Override
	public boolean method_3759() {
		return false;
	}

	@Override
	public void method_3744(class_128 arg) {
		this.field_5518.method_1494(arg);
	}

	@Override
	public class_128 method_3859(class_128 arg) {
		arg = super.method_3859(arg);
		arg.method_567().method_578("Type", "Integrated Server (map_client.txt)");
		arg.method_567()
			.method_577(
				"Is Modded",
				() -> {
					String string = ClientBrandRetriever.getClientModName();
					if (!string.equals("vanilla")) {
						return "Definitely; Client brand changed to '" + string + "'";
					} else {
						string = this.getServerModName();
						if (!"vanilla".equals(string)) {
							return "Definitely; Server brand changed to '" + string + "'";
						} else {
							return class_310.class.getSigners() == null
								? "Very likely; Jar signature invalidated"
								: "Probably not. Jar signature remains and both client + server brands are untouched.";
						}
					}
				}
			);
		return arg;
	}

	@Override
	public void method_5495(class_1276 arg) {
		super.method_5495(arg);
		arg.method_5481("snooper_partner", this.field_5518.method_1552().method_5479());
	}

	@Override
	public boolean method_3763(class_1934 arg, boolean bl, int i) {
		try {
			this.method_3787().method_14354(null, i);
			field_5520.info("Started serving on {}", i);
			this.field_5522 = i;
			this.field_5519 = new class_1133(this.method_3818(), i + "");
			this.field_5519.start();
			this.method_3760().method_14595(arg);
			this.method_3760().method_14607(bl);
			int j = this.method_3835(this.field_5518.field_1724.method_7334());
			this.field_5518.field_1724.method_3147(j);

			for (class_3222 lv : this.method_3760().method_14571()) {
				this.method_3734().method_9241(lv);
			}

			return true;
		} catch (IOException var7) {
			return false;
		}
	}

	@Override
	public void method_3782() {
		super.method_3782();
		if (this.field_5519 != null) {
			this.field_5519.interrupt();
			this.field_5519 = null;
		}
	}

	@Override
	public void method_3747(boolean bl) {
		this.method_19537(() -> {
			for (class_3222 lv : Lists.newArrayList(this.method_3760().method_14571())) {
				if (!lv.method_5667().equals(this.field_5521)) {
					this.method_3760().method_14611(lv);
				}
			}
		});
		super.method_3747(bl);
		if (this.field_5519 != null) {
			this.field_5519.interrupt();
			this.field_5519 = null;
		}
	}

	@Override
	public boolean method_3860() {
		return this.field_5522 > -1;
	}

	@Override
	public int method_3756() {
		return this.field_5522;
	}

	@Override
	public void method_3838(class_1934 arg) {
		super.method_3838(arg);
		this.method_3760().method_14595(arg);
	}

	@Override
	public boolean method_3812() {
		return true;
	}

	@Override
	public int method_3798() {
		return 2;
	}

	public void method_4817(UUID uUID) {
		this.field_5521 = uUID;
	}

	@Override
	public boolean method_19466(GameProfile gameProfile) {
		return gameProfile.getName().equalsIgnoreCase(this.method_3811());
	}
}
