package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_3324 {
	public static final File field_14355 = new File("banned-players.json");
	public static final File field_14364 = new File("banned-ips.json");
	public static final File field_14348 = new File("ops.json");
	public static final File field_14343 = new File("whitelist.json");
	private static final Logger field_14349 = LogManager.getLogger();
	private static final SimpleDateFormat field_14356 = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
	private final MinecraftServer field_14360;
	private final List<class_3222> field_14351 = Lists.<class_3222>newArrayList();
	private final Map<UUID, class_3222> field_14354 = Maps.<UUID, class_3222>newHashMap();
	private final class_3335 field_14344 = new class_3335(field_14355);
	private final class_3317 field_14345 = new class_3317(field_14364);
	private final class_3326 field_14353 = new class_3326(field_14348);
	private final class_3337 field_14361 = new class_3337(field_14343);
	private final Map<UUID, class_3442> field_14362 = Maps.<UUID, class_3442>newHashMap();
	private final Map<UUID, class_2985> field_14346 = Maps.<UUID, class_2985>newHashMap();
	private class_35 field_14358;
	private boolean field_14352;
	protected final int field_14347;
	private int field_14359;
	private class_1934 field_14363;
	private boolean field_14350;
	private int field_14357;

	public class_3324(MinecraftServer minecraftServer, int i) {
		this.field_14360 = minecraftServer;
		this.field_14347 = i;
		this.method_14563().method_14637(true);
		this.method_14585().method_14637(true);
	}

	public void method_14570(class_2535 arg, class_3222 arg2) {
		GameProfile gameProfile = arg2.method_7334();
		class_3312 lv = this.field_14360.method_3793();
		GameProfile gameProfile2 = lv.method_14512(gameProfile.getId());
		String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
		lv.method_14508(gameProfile);
		class_2487 lv2 = this.method_14600(arg2);
		class_3218 lv3 = this.field_14360.method_3847(arg2.field_6026);
		arg2.method_5866(lv3);
		arg2.field_13974.method_14259((class_3218)arg2.field_6002);
		String string2 = "local";
		if (arg.method_10755() != null) {
			string2 = arg.method_10755().toString();
		}

		field_14349.info(
			"{}[{}] logged in with entity id {} at ({}, {}, {})",
			arg2.method_5477().getString(),
			string2,
			arg2.method_5628(),
			arg2.field_5987,
			arg2.field_6010,
			arg2.field_6035
		);
		class_31 lv4 = lv3.method_8401();
		this.method_14615(arg2, null, lv3);
		class_3244 lv5 = new class_3244(this.field_14360, arg, arg2);
		lv5.method_14364(
			new class_2678(
				arg2.method_5628(),
				arg2.field_13974.method_14257(),
				lv4.method_152(),
				lv3.field_9247.method_12460(),
				this.method_14592(),
				lv4.method_153(),
				this.field_14359,
				lv3.method_8450().method_8355(class_1928.field_19401)
			)
		);
		lv5.method_14364(new class_2658(class_2658.field_12158, new class_2540(Unpooled.buffer()).method_10814(this.method_14561().getServerModName())));
		lv5.method_14364(new class_2632(lv4.method_207(), lv4.method_197()));
		lv5.method_14364(new class_2696(arg2.field_7503));
		lv5.method_14364(new class_2735(arg2.field_7514.field_7545));
		lv5.method_14364(new class_2788(this.field_14360.method_3772().method_8126()));
		lv5.method_14364(new class_2790(this.field_14360.method_3801()));
		this.method_14576(arg2);
		arg2.method_14248().method_14914();
		arg2.method_14253().method_14904(arg2);
		this.method_14588(lv3.method_14170(), arg2);
		this.field_14360.method_3856();
		class_2561 lv6;
		if (arg2.method_7334().getName().equalsIgnoreCase(string)) {
			lv6 = new class_2588("multiplayer.player.joined", arg2.method_5476());
		} else {
			lv6 = new class_2588("multiplayer.player.joined.renamed", arg2.method_5476(), string);
		}

		this.method_14593(lv6.method_10854(class_124.field_1054));
		lv5.method_14363(arg2.field_5987, arg2.field_6010, arg2.field_6035, arg2.field_6031, arg2.field_5965);
		this.field_14351.add(arg2);
		this.field_14354.put(arg2.method_5667(), arg2);
		this.method_14581(new class_2703(class_2703.class_2704.field_12372, arg2));

		for (int i = 0; i < this.field_14351.size(); i++) {
			arg2.field_13987.method_14364(new class_2703(class_2703.class_2704.field_12372, (class_3222)this.field_14351.get(i)));
		}

		lv3.method_18213(arg2);
		this.field_14360.method_3837().method_12975(arg2);
		this.method_14606(arg2, lv3);
		if (!this.field_14360.method_3784().isEmpty()) {
			arg2.method_14255(this.field_14360.method_3784(), this.field_14360.method_3805());
		}

		for (class_1293 lv7 : arg2.method_6026()) {
			lv5.method_14364(new class_2783(arg2.method_5628(), lv7));
		}

		if (lv2 != null && lv2.method_10573("RootVehicle", 10)) {
			class_2487 lv8 = lv2.method_10562("RootVehicle");
			class_1297 lv9 = class_1299.method_17842(lv8.method_10562("Entity"), lv3, arg2x -> !lv3.method_18768(arg2x) ? null : arg2x);
			if (lv9 != null) {
				UUID uUID = lv8.method_10584("Attach");
				if (lv9.method_5667().equals(uUID)) {
					arg2.method_5873(lv9, true);
				} else {
					for (class_1297 lv10 : lv9.method_5736()) {
						if (lv10.method_5667().equals(uUID)) {
							arg2.method_5873(lv10, true);
							break;
						}
					}
				}

				if (!arg2.method_5765()) {
					field_14349.warn("Couldn't reattach entity to player");
					lv3.method_18774(lv9);

					for (class_1297 lv10x : lv9.method_5736()) {
						lv3.method_18774(lv10x);
					}
				}
			}
		}

		arg2.method_14235();
	}

	protected void method_14588(class_2995 arg, class_3222 arg2) {
		Set<class_266> set = Sets.<class_266>newHashSet();

		for (class_268 lv : arg.method_1159()) {
			arg2.field_13987.method_14364(new class_2755(lv, 0));
		}

		for (int i = 0; i < 19; i++) {
			class_266 lv2 = arg.method_1189(i);
			if (lv2 != null && !set.contains(lv2)) {
				for (class_2596<?> lv3 : arg.method_12937(lv2)) {
					arg2.field_13987.method_14364(lv3);
				}

				set.add(lv2);
			}
		}
	}

	public void method_14591(class_3218 arg) {
		this.field_14358 = arg.method_17982();
		arg.method_8621().method_11983(new class_2780() {
			@Override
			public void method_11934(class_2784 arg, double d) {
				class_3324.this.method_14581(new class_2730(arg, class_2730.class_2731.field_12456));
			}

			@Override
			public void method_11931(class_2784 arg, double d, double e, long l) {
				class_3324.this.method_14581(new class_2730(arg, class_2730.class_2731.field_12452));
			}

			@Override
			public void method_11930(class_2784 arg, double d, double e) {
				class_3324.this.method_14581(new class_2730(arg, class_2730.class_2731.field_12450));
			}

			@Override
			public void method_11932(class_2784 arg, int i) {
				class_3324.this.method_14581(new class_2730(arg, class_2730.class_2731.field_12455));
			}

			@Override
			public void method_11933(class_2784 arg, int i) {
				class_3324.this.method_14581(new class_2730(arg, class_2730.class_2731.field_12451));
			}

			@Override
			public void method_11929(class_2784 arg, double d) {
			}

			@Override
			public void method_11935(class_2784 arg, double d) {
			}
		});
	}

	@Nullable
	public class_2487 method_14600(class_3222 arg) {
		class_2487 lv = this.field_14360.method_3847(class_2874.field_13072).method_8401().method_226();
		class_2487 lv2;
		if (arg.method_5477().getString().equals(this.field_14360.method_3811()) && lv != null) {
			lv2 = lv;
			arg.method_5651(lv);
			field_14349.debug("loading single player");
		} else {
			lv2 = this.field_14358.method_261(arg);
		}

		return lv2;
	}

	protected void method_14577(class_3222 arg) {
		this.field_14358.method_262(arg);
		class_3442 lv = (class_3442)this.field_14362.get(arg.method_5667());
		if (lv != null) {
			lv.method_14912();
		}

		class_2985 lv2 = (class_2985)this.field_14346.get(arg.method_5667());
		if (lv2 != null) {
			lv2.method_12890();
		}
	}

	public void method_14611(class_3222 arg) {
		class_3218 lv = arg.method_14220();
		arg.method_7281(class_3468.field_15389);
		this.method_14577(arg);
		if (arg.method_5765()) {
			class_1297 lv2 = arg.method_5668();
			if (lv2.method_5817()) {
				field_14349.debug("Removing player mount");
				arg.method_5848();
				lv.method_18774(lv2);

				for (class_1297 lv3 : lv2.method_5736()) {
					lv.method_18774(lv3);
				}

				lv.method_8497(arg.field_6024, arg.field_5980).method_12220();
			}
		}

		arg.method_18375();
		lv.method_18770(arg);
		arg.method_14236().method_12881();
		this.field_14351.remove(arg);
		this.field_14360.method_3837().method_12976(arg);
		UUID uUID = arg.method_5667();
		class_3222 lv4 = (class_3222)this.field_14354.get(uUID);
		if (lv4 == arg) {
			this.field_14354.remove(uUID);
			this.field_14362.remove(uUID);
			this.field_14346.remove(uUID);
		}

		this.method_14581(new class_2703(class_2703.class_2704.field_12376, arg));
	}

	@Nullable
	public class_2561 method_14586(SocketAddress socketAddress, GameProfile gameProfile) {
		if (this.field_14344.method_14650(gameProfile)) {
			class_3336 lv = this.field_14344.method_14640(gameProfile);
			class_2561 lv2 = new class_2588("multiplayer.disconnect.banned.reason", lv.method_14503());
			if (lv.method_14502() != null) {
				lv2.method_10852(new class_2588("multiplayer.disconnect.banned.expiration", field_14356.format(lv.method_14502())));
			}

			return lv2;
		} else if (!this.method_14587(gameProfile)) {
			return new class_2588("multiplayer.disconnect.not_whitelisted");
		} else if (this.field_14345.method_14527(socketAddress)) {
			class_3320 lv3 = this.field_14345.method_14528(socketAddress);
			class_2561 lv2 = new class_2588("multiplayer.disconnect.banned_ip.reason", lv3.method_14503());
			if (lv3.method_14502() != null) {
				lv2.method_10852(new class_2588("multiplayer.disconnect.banned_ip.expiration", field_14356.format(lv3.method_14502())));
			}

			return lv2;
		} else {
			return this.field_14351.size() >= this.field_14347 && !this.method_14609(gameProfile) ? new class_2588("multiplayer.disconnect.server_full") : null;
		}
	}

	public class_3222 method_14613(GameProfile gameProfile) {
		UUID uUID = class_1657.method_7271(gameProfile);
		List<class_3222> list = Lists.<class_3222>newArrayList();

		for (int i = 0; i < this.field_14351.size(); i++) {
			class_3222 lv = (class_3222)this.field_14351.get(i);
			if (lv.method_5667().equals(uUID)) {
				list.add(lv);
			}
		}

		class_3222 lv2 = (class_3222)this.field_14354.get(gameProfile.getId());
		if (lv2 != null && !list.contains(lv2)) {
			list.add(lv2);
		}

		for (class_3222 lv3 : list) {
			lv3.field_13987.method_14367(new class_2588("multiplayer.disconnect.duplicate_login"));
		}

		class_3225 lv4;
		if (this.field_14360.method_3799()) {
			lv4 = new class_3201(this.field_14360.method_3847(class_2874.field_13072));
		} else {
			lv4 = new class_3225(this.field_14360.method_3847(class_2874.field_13072));
		}

		return new class_3222(this.field_14360, this.field_14360.method_3847(class_2874.field_13072), gameProfile, lv4);
	}

	public class_3222 method_14556(class_3222 arg, class_2874 arg2, boolean bl) {
		this.field_14351.remove(arg);
		arg.method_14220().method_18770(arg);
		class_2338 lv = arg.method_7280();
		boolean bl2 = arg.method_7258();
		arg.field_6026 = arg2;
		class_3225 lv2;
		if (this.field_14360.method_3799()) {
			lv2 = new class_3201(this.field_14360.method_3847(arg.field_6026));
		} else {
			lv2 = new class_3225(this.field_14360.method_3847(arg.field_6026));
		}

		class_3222 lv3 = new class_3222(this.field_14360, this.field_14360.method_3847(arg.field_6026), arg.method_7334(), lv2);
		lv3.field_13987 = arg.field_13987;
		lv3.method_14203(arg, bl);
		lv3.method_5838(arg.method_5628());
		lv3.method_7283(arg.method_6068());

		for (String string : arg.method_5752()) {
			lv3.method_5780(string);
		}

		class_3218 lv4 = this.field_14360.method_3847(arg.field_6026);
		this.method_14615(lv3, arg, lv4);
		if (lv != null) {
			Optional<class_243> optional = class_1657.method_7288(this.field_14360.method_3847(arg.field_6026), lv, bl2);
			if (optional.isPresent()) {
				class_243 lv5 = (class_243)optional.get();
				lv3.method_5808(lv5.field_1352, lv5.field_1351, lv5.field_1350, 0.0F, 0.0F);
				lv3.method_7289(lv, bl2);
			} else {
				lv3.field_13987.method_14364(new class_2668(0, 0.0F));
			}
		}

		while (!lv4.method_17892(lv3) && lv3.field_6010 < 256.0) {
			lv3.method_5814(lv3.field_5987, lv3.field_6010 + 1.0, lv3.field_6035);
		}

		class_31 lv6 = lv3.field_6002.method_8401();
		lv3.field_13987.method_14364(new class_2724(lv3.field_6026, lv6.method_153(), lv3.field_13974.method_14257()));
		class_2338 lv7 = lv4.method_8395();
		lv3.field_13987.method_14363(lv3.field_5987, lv3.field_6010, lv3.field_6035, lv3.field_6031, lv3.field_5965);
		lv3.field_13987.method_14364(new class_2759(lv7));
		lv3.field_13987.method_14364(new class_2632(lv6.method_207(), lv6.method_197()));
		lv3.field_13987.method_14364(new class_2748(lv3.field_7510, lv3.field_7495, lv3.field_7520));
		this.method_14606(lv3, lv4);
		this.method_14576(lv3);
		lv4.method_18215(lv3);
		this.field_14351.add(lv3);
		this.field_14354.put(lv3.method_5667(), lv3);
		lv3.method_14235();
		lv3.method_6033(lv3.method_6032());
		return lv3;
	}

	public void method_14576(class_3222 arg) {
		GameProfile gameProfile = arg.method_7334();
		int i = this.field_14360.method_3835(gameProfile);
		this.method_14596(arg, i);
	}

	public void method_14601() {
		if (++this.field_14357 > 600) {
			this.method_14581(new class_2703(class_2703.class_2704.field_12371, this.field_14351));
			this.field_14357 = 0;
		}
	}

	public void method_14581(class_2596<?> arg) {
		for (int i = 0; i < this.field_14351.size(); i++) {
			((class_3222)this.field_14351.get(i)).field_13987.method_14364(arg);
		}
	}

	public void method_14589(class_2596<?> arg, class_2874 arg2) {
		for (int i = 0; i < this.field_14351.size(); i++) {
			class_3222 lv = (class_3222)this.field_14351.get(i);
			if (lv.field_6026 == arg2) {
				lv.field_13987.method_14364(arg);
			}
		}
	}

	public void method_14564(class_1657 arg, class_2561 arg2) {
		class_270 lv = arg.method_5781();
		if (lv != null) {
			for (String string : lv.method_1204()) {
				class_3222 lv2 = this.method_14566(string);
				if (lv2 != null && lv2 != arg) {
					lv2.method_9203(arg2);
				}
			}
		}
	}

	public void method_14565(class_1657 arg, class_2561 arg2) {
		class_270 lv = arg.method_5781();
		if (lv == null) {
			this.method_14593(arg2);
		} else {
			for (int i = 0; i < this.field_14351.size(); i++) {
				class_3222 lv2 = (class_3222)this.field_14351.get(i);
				if (lv2.method_5781() != lv) {
					lv2.method_9203(arg2);
				}
			}
		}
	}

	public String[] method_14580() {
		String[] strings = new String[this.field_14351.size()];

		for (int i = 0; i < this.field_14351.size(); i++) {
			strings[i] = ((class_3222)this.field_14351.get(i)).method_7334().getName();
		}

		return strings;
	}

	public class_3335 method_14563() {
		return this.field_14344;
	}

	public class_3317 method_14585() {
		return this.field_14345;
	}

	public void method_14582(GameProfile gameProfile) {
		this.field_14353.method_14633(new class_3327(gameProfile, this.field_14360.method_3798(), this.field_14353.method_14620(gameProfile)));
		class_3222 lv = this.method_14602(gameProfile.getId());
		if (lv != null) {
			this.method_14576(lv);
		}
	}

	public void method_14604(GameProfile gameProfile) {
		this.field_14353.method_14635(gameProfile);
		class_3222 lv = this.method_14602(gameProfile.getId());
		if (lv != null) {
			this.method_14576(lv);
		}
	}

	private void method_14596(class_3222 arg, int i) {
		if (arg.field_13987 != null) {
			byte b;
			if (i <= 0) {
				b = 24;
			} else if (i >= 4) {
				b = 28;
			} else {
				b = (byte)(24 + i);
			}

			arg.field_13987.method_14364(new class_2663(arg, b));
		}

		this.field_14360.method_3734().method_9241(arg);
	}

	public boolean method_14587(GameProfile gameProfile) {
		return !this.field_14352 || this.field_14353.method_14644(gameProfile) || this.field_14361.method_14644(gameProfile);
	}

	public boolean method_14569(GameProfile gameProfile) {
		return this.field_14353.method_14644(gameProfile)
			|| this.field_14360.method_19466(gameProfile) && this.field_14360.method_3847(class_2874.field_13072).method_8401().method_194()
			|| this.field_14350;
	}

	@Nullable
	public class_3222 method_14566(String string) {
		for (class_3222 lv : this.field_14351) {
			if (lv.method_7334().getName().equalsIgnoreCase(string)) {
				return lv;
			}
		}

		return null;
	}

	public void method_14605(@Nullable class_1657 arg, double d, double e, double f, double g, class_2874 arg2, class_2596<?> arg3) {
		for (int i = 0; i < this.field_14351.size(); i++) {
			class_3222 lv = (class_3222)this.field_14351.get(i);
			if (lv != arg && lv.field_6026 == arg2) {
				double h = d - lv.field_5987;
				double j = e - lv.field_6010;
				double k = f - lv.field_6035;
				if (h * h + j * j + k * k < g * g) {
					lv.field_13987.method_14364(arg3);
				}
			}
		}
	}

	public void method_14617() {
		for (int i = 0; i < this.field_14351.size(); i++) {
			this.method_14577((class_3222)this.field_14351.get(i));
		}
	}

	public class_3337 method_14590() {
		return this.field_14361;
	}

	public String[] method_14560() {
		return this.field_14361.method_14636();
	}

	public class_3326 method_14603() {
		return this.field_14353;
	}

	public String[] method_14584() {
		return this.field_14353.method_14636();
	}

	public void method_14599() {
	}

	public void method_14606(class_3222 arg, class_3218 arg2) {
		class_2784 lv = this.field_14360.method_3847(class_2874.field_13072).method_8621();
		arg.field_13987.method_14364(new class_2730(lv, class_2730.class_2731.field_12454));
		arg.field_13987.method_14364(new class_2761(arg2.method_8510(), arg2.method_8532(), arg2.method_8450().method_8355(class_1928.field_19396)));
		class_2338 lv2 = arg2.method_8395();
		arg.field_13987.method_14364(new class_2759(lv2));
		if (arg2.method_8419()) {
			arg.field_13987.method_14364(new class_2668(1, 0.0F));
			arg.field_13987.method_14364(new class_2668(7, arg2.method_8430(1.0F)));
			arg.field_13987.method_14364(new class_2668(8, arg2.method_8478(1.0F)));
		}
	}

	public void method_14594(class_3222 arg) {
		arg.method_14204(arg.field_7498);
		arg.method_14217();
		arg.field_13987.method_14364(new class_2735(arg.field_7514.field_7545));
	}

	public int method_14574() {
		return this.field_14351.size();
	}

	public int method_14592() {
		return this.field_14347;
	}

	public boolean method_14614() {
		return this.field_14352;
	}

	public void method_14557(boolean bl) {
		this.field_14352 = bl;
	}

	public List<class_3222> method_14559(String string) {
		List<class_3222> list = Lists.<class_3222>newArrayList();

		for (class_3222 lv : this.field_14351) {
			if (lv.method_14209().equals(string)) {
				list.add(lv);
			}
		}

		return list;
	}

	public int method_14568() {
		return this.field_14359;
	}

	public MinecraftServer method_14561() {
		return this.field_14360;
	}

	public class_2487 method_14567() {
		return null;
	}

	@Environment(EnvType.CLIENT)
	public void method_14595(class_1934 arg) {
		this.field_14363 = arg;
	}

	private void method_14615(class_3222 arg, class_3222 arg2, class_1936 arg3) {
		if (arg2 != null) {
			arg.field_13974.method_14261(arg2.field_13974.method_14257());
		} else if (this.field_14363 != null) {
			arg.field_13974.method_14261(this.field_14363);
		}

		arg.field_13974.method_14260(arg3.method_8401().method_210());
	}

	@Environment(EnvType.CLIENT)
	public void method_14607(boolean bl) {
		this.field_14350 = bl;
	}

	public void method_14597() {
		for (int i = 0; i < this.field_14351.size(); i++) {
			((class_3222)this.field_14351.get(i)).field_13987.method_14367(new class_2588("multiplayer.disconnect.server_shutdown"));
		}
	}

	public void method_14616(class_2561 arg, boolean bl) {
		this.field_14360.method_9203(arg);
		class_2556 lv = bl ? class_2556.field_11735 : class_2556.field_11737;
		this.method_14581(new class_2635(arg, lv));
	}

	public void method_14593(class_2561 arg) {
		this.method_14616(arg, true);
	}

	public class_3442 method_14583(class_1657 arg) {
		UUID uUID = arg.method_5667();
		class_3442 lv = uUID == null ? null : (class_3442)this.field_14362.get(uUID);
		if (lv == null) {
			File file = new File(this.field_14360.method_3847(class_2874.field_13072).method_17982().method_132(), "stats");
			File file2 = new File(file, uUID + ".json");
			if (!file2.exists()) {
				File file3 = new File(file, arg.method_5477().getString() + ".json");
				if (file3.exists() && file3.isFile()) {
					file3.renameTo(file2);
				}
			}

			lv = new class_3442(this.field_14360, file2);
			this.field_14362.put(uUID, lv);
		}

		return lv;
	}

	public class_2985 method_14578(class_3222 arg) {
		UUID uUID = arg.method_5667();
		class_2985 lv = (class_2985)this.field_14346.get(uUID);
		if (lv == null) {
			File file = new File(this.field_14360.method_3847(class_2874.field_13072).method_17982().method_132(), "advancements");
			File file2 = new File(file, uUID + ".json");
			lv = new class_2985(this.field_14360, file2, arg);
			this.field_14346.put(uUID, lv);
		}

		lv.method_12875(arg);
		return lv;
	}

	public void method_14608(int i) {
		this.field_14359 = i;
		this.method_14581(new class_4273(i));

		for (class_3218 lv : this.field_14360.method_3738()) {
			if (lv != null) {
				lv.method_14178().method_14144(i);
			}
		}
	}

	public List<class_3222> method_14571() {
		return this.field_14351;
	}

	@Nullable
	public class_3222 method_14602(UUID uUID) {
		return (class_3222)this.field_14354.get(uUID);
	}

	public boolean method_14609(GameProfile gameProfile) {
		return false;
	}

	public void method_14572() {
		for (class_2985 lv : this.field_14346.values()) {
			lv.method_12886();
		}

		this.method_14581(new class_2790(this.field_14360.method_3801()));
		class_2788 lv2 = new class_2788(this.field_14360.method_3772().method_8126());

		for (class_3222 lv3 : this.field_14351) {
			lv3.field_13987.method_14364(lv2);
			lv3.method_14253().method_14904(lv3);
		}
	}

	public boolean method_14579() {
		return this.field_14350;
	}
}
