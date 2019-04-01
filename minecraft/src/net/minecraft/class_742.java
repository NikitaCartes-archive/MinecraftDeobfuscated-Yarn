package net.minecraft;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_742 extends class_1657 {
	private class_640 field_3901;
	public float field_3900;
	public float field_3899;
	public float field_3898;
	public final class_638 field_17892;

	public class_742(class_638 arg, GameProfile gameProfile) {
		super(arg, gameProfile);
		this.field_17892 = arg;
	}

	@Override
	public boolean method_7325() {
		class_640 lv = class_310.method_1551().method_1562().method_2871(this.method_7334().getId());
		return lv != null && lv.method_2958() == class_1934.field_9219;
	}

	@Override
	public boolean method_7337() {
		class_640 lv = class_310.method_1551().method_1562().method_2871(this.method_7334().getId());
		return lv != null && lv.method_2958() == class_1934.field_9220;
	}

	public boolean method_3125() {
		return this.method_3123() != null;
	}

	@Nullable
	protected class_640 method_3123() {
		if (this.field_3901 == null) {
			this.field_3901 = class_310.method_1551().method_1562().method_2871(this.method_5667());
		}

		return this.field_3901;
	}

	public boolean method_3127() {
		class_640 lv = this.method_3123();
		return lv != null && lv.method_2967();
	}

	public class_2960 method_3117() {
		class_640 lv = this.method_3123();
		return lv == null ? class_1068.method_4648(this.method_5667()) : lv.method_2968();
	}

	@Nullable
	public class_2960 method_3119() {
		class_640 lv = this.method_3123();
		return lv == null ? null : lv.method_2979();
	}

	public boolean method_3126() {
		return this.method_3123() != null;
	}

	@Nullable
	public class_2960 method_3122() {
		class_640 lv = this.method_3123();
		return lv == null ? null : lv.method_2957();
	}

	public static class_1046 method_3120(class_2960 arg, String string) {
		class_1060 lv = class_310.method_1551().method_1531();
		class_1062 lv2 = lv.method_4619(arg);
		if (lv2 == null) {
			lv2 = new class_1046(
				null,
				String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", class_3544.method_15440(string)),
				class_1068.method_4648(method_7310(string)),
				new class_764()
			);
			lv.method_4616(arg, lv2);
		}

		return (class_1046)lv2;
	}

	public static class_2960 method_3124(String string) {
		return new class_2960("skins/" + Hashing.sha1().hashUnencodedChars(class_3544.method_15440(string)));
	}

	public String method_3121() {
		class_640 lv = this.method_3123();
		return lv == null ? class_1068.method_4647(this.method_5667()) : lv.method_2977();
	}

	public float method_3118() {
		float f = 1.0F;
		if (this.field_7503.field_7479) {
			f *= 1.1F;
		}

		class_1324 lv = this.method_5996(class_1612.field_7357);
		f = (float)((double)f * ((lv.method_6194() / (double)this.field_7503.method_7253() + 1.0) / 2.0));
		if (this.field_7503.method_7253() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
			f = 1.0F;
		}

		if (this.method_6115() && this.method_6030().method_7909() == class_1802.field_8102) {
			int i = this.method_6048();
			float g = (float)i / 20.0F;
			if (g > 1.0F) {
				g = 1.0F;
			} else {
				g *= g;
			}

			f *= 1.0F - g * 0.15F;
		}

		return f;
	}
}
