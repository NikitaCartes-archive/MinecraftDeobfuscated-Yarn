package net.minecraft;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2631 extends class_2586 implements class_3000 {
	private GameProfile field_12087;
	private int field_12085;
	private boolean field_12086;
	private static class_3312 field_12089;
	private static MinecraftSessionService field_12088;

	public class_2631() {
		super(class_2591.field_11913);
	}

	public static void method_11337(class_3312 arg) {
		field_12089 = arg;
	}

	public static void method_11336(MinecraftSessionService minecraftSessionService) {
		field_12088 = minecraftSessionService;
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		if (this.field_12087 != null) {
			class_2487 lv = new class_2487();
			class_2512.method_10684(lv, this.field_12087);
			arg.method_10566("Owner", lv);
		}

		return arg;
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		if (arg.method_10573("Owner", 10)) {
			this.method_11333(class_2512.method_10683(arg.method_10562("Owner")));
		} else if (arg.method_10573("ExtraType", 8)) {
			String string = arg.method_10558("ExtraType");
			if (!class_3544.method_15438(string)) {
				this.method_11333(new GameProfile(null, string));
			}
		}
	}

	@Override
	public void method_16896() {
		class_2248 lv = this.method_11010().method_11614();
		if (lv == class_2246.field_10337 || lv == class_2246.field_10472) {
			if (this.field_11863.method_8479(this.field_11867)) {
				this.field_12086 = true;
				this.field_12085++;
			} else {
				this.field_12086 = false;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_11338(float f) {
		return this.field_12086 ? (float)this.field_12085 + f : (float)this.field_12085;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public GameProfile method_11334() {
		return this.field_12087;
	}

	@Nullable
	@Override
	public class_2622 method_16886() {
		return new class_2622(this.field_11867, 4, this.method_16887());
	}

	@Override
	public class_2487 method_16887() {
		return this.method_11007(new class_2487());
	}

	public void method_11333(@Nullable GameProfile gameProfile) {
		this.field_12087 = gameProfile;
		this.method_11339();
	}

	private void method_11339() {
		this.field_12087 = method_11335(this.field_12087);
		this.method_5431();
	}

	public static GameProfile method_11335(GameProfile gameProfile) {
		if (gameProfile != null && !class_3544.method_15438(gameProfile.getName())) {
			if (gameProfile.isComplete() && gameProfile.getProperties().containsKey("textures")) {
				return gameProfile;
			} else if (field_12089 != null && field_12088 != null) {
				GameProfile gameProfile2 = field_12089.method_14515(gameProfile.getName());
				if (gameProfile2 == null) {
					return gameProfile;
				} else {
					Property property = Iterables.getFirst(gameProfile2.getProperties().get("textures"), null);
					if (property == null) {
						gameProfile2 = field_12088.fillProfileProperties(gameProfile2, true);
					}

					return gameProfile2;
				}
			} else {
				return gameProfile;
			}
		} else {
			return gameProfile;
		}
	}
}
