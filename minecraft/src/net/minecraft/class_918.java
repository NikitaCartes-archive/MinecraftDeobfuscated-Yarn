package net.minecraft;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_918 implements class_3302 {
	public static final class_2960 field_4731 = new class_2960("textures/misc/enchanted_item_glint.png");
	private static final Set<class_1792> field_4728 = Sets.<class_1792>newHashSet(class_1802.field_8162);
	public float field_4730;
	private final class_763 field_4732;
	private final class_1060 field_4729;
	private final class_325 field_4733;

	public class_918(class_1060 arg, class_1092 arg2, class_325 arg3) {
		this.field_4729 = arg;
		this.field_4732 = new class_763(arg2);

		for (class_1792 lv : class_2378.field_11142) {
			if (!field_4728.contains(lv)) {
				this.field_4732.method_3309(lv, new class_1091(class_2378.field_11142.method_10221(lv), "inventory"));
			}
		}

		this.field_4733 = arg3;
	}

	public class_763 method_4012() {
		return this.field_4732;
	}

	private void method_4018(class_1087 arg, class_1799 arg2) {
		this.method_4027(arg, -1, arg2);
	}

	private void method_4013(class_1087 arg, int i) {
		this.method_4027(arg, i, class_1799.field_8037);
	}

	private void method_4027(class_1087 arg, int i, class_1799 arg2) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1590);
		Random random = new Random();
		long l = 42L;

		for (class_2350 lv3 : class_2350.values()) {
			random.setSeed(42L);
			this.method_4031(lv2, arg.method_4707(null, lv3, random), i, arg2);
		}

		random.setSeed(42L);
		this.method_4031(lv2, arg.method_4707(null, null, random), i, arg2);
		lv.method_1350();
	}

	public void method_4006(class_1799 arg, class_1087 arg2) {
		if (!arg.method_7960()) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
			if (arg2.method_4713()) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableRescaleNormal();
				class_756.field_3986.method_3166(arg);
			} else {
				this.method_4018(arg2, arg);
				if (arg.method_7958()) {
					method_4011(this.field_4729, () -> this.method_4013(arg2, -8372020), 8);
				}
			}

			GlStateManager.popMatrix();
		}
	}

	public static void method_4011(class_1060 arg, Runnable runnable, int i) {
		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(GlStateManager.class_1033.field_5145, GlStateManager.class_1027.field_5078);
		arg.method_4618(field_4731);
		GlStateManager.matrixMode(5890);
		GlStateManager.pushMatrix();
		GlStateManager.scalef((float)i, (float)i, (float)i);
		float f = (float)(class_156.method_658() % 3000L) / 3000.0F / (float)i;
		GlStateManager.translatef(f, 0.0F, 0.0F);
		GlStateManager.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
		runnable.run();
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.scalef((float)i, (float)i, (float)i);
		float g = (float)(class_156.method_658() % 4873L) / 4873.0F / (float)i;
		GlStateManager.translatef(-g, 0.0F, 0.0F);
		GlStateManager.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
		runnable.run();
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088);
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		arg.method_4618(class_1059.field_5275);
	}

	private void method_4003(class_287 arg, class_777 arg2) {
		class_2382 lv = arg2.method_3358().method_10163();
		arg.method_1320((float)lv.method_10263(), (float)lv.method_10264(), (float)lv.method_10260());
	}

	private void method_4002(class_287 arg, class_777 arg2, int i) {
		arg.method_1333(arg2.method_3357());
		arg.method_1332(i);
		this.method_4003(arg, arg2);
	}

	private void method_4031(class_287 arg, List<class_777> list, int i, class_1799 arg2) {
		boolean bl = i == -1 && !arg2.method_7960();
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			class_777 lv = (class_777)list.get(j);
			int l = i;
			if (bl && lv.method_3360()) {
				l = this.field_4733.method_1704(arg2, lv.method_3359());
				l |= -16777216;
			}

			this.method_4002(arg, lv, l);
		}
	}

	public boolean method_4014(class_1799 arg) {
		class_1087 lv = this.field_4732.method_3308(arg);
		return lv == null ? false : lv.method_4712();
	}

	public void method_4009(class_1799 arg, class_809.class_811 arg2) {
		if (!arg.method_7960()) {
			class_1087 lv = this.method_4007(arg);
			this.method_4024(arg, lv, arg2, false);
		}
	}

	public class_1087 method_4028(class_1799 arg, @Nullable class_1937 arg2, @Nullable class_1309 arg3) {
		class_1087 lv = this.field_4732.method_3308(arg);
		class_1792 lv2 = arg.method_7909();
		return !lv2.method_7845() ? lv : this.method_4020(lv, arg, arg2, arg3);
	}

	public class_1087 method_4019(class_1799 arg, class_1937 arg2, class_1309 arg3) {
		class_1792 lv = arg.method_7909();
		class_1087 lv2;
		if (lv == class_1802.field_8547) {
			lv2 = this.field_4732.method_3303().method_4742(new class_1091("minecraft:trident_in_hand#inventory"));
		} else {
			lv2 = this.field_4732.method_3308(arg);
		}

		return !lv.method_7845() ? lv2 : this.method_4020(lv2, arg, arg2, arg3);
	}

	public class_1087 method_4007(class_1799 arg) {
		return this.method_4028(arg, null, null);
	}

	private class_1087 method_4020(class_1087 arg, class_1799 arg2, @Nullable class_1937 arg3, @Nullable class_1309 arg4) {
		class_1087 lv = arg.method_4710().method_3495(arg, arg2, arg3, arg4);
		return lv == null ? this.field_4732.method_3303().method_4744() : lv;
	}

	public void method_4016(class_1799 arg, class_1309 arg2, class_809.class_811 arg3, boolean bl) {
		if (!arg.method_7960() && arg2 != null) {
			class_1087 lv = this.method_4019(arg, arg2.field_6002, arg2);
			this.method_4024(arg, lv, arg3, bl);
		}
	}

	protected void method_4024(class_1799 arg, class_1087 arg2, class_809.class_811 arg3, boolean bl) {
		if (!arg.method_7960()) {
			this.field_4729.method_4618(class_1059.field_5275);
			this.field_4729.method_4619(class_1059.field_5275).method_4626(false, false);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableRescaleNormal();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
			);
			GlStateManager.pushMatrix();
			class_809 lv = arg2.method_4709();
			class_809.method_3502(lv.method_3503(arg3), bl);
			if (this.method_4030(lv.method_3503(arg3))) {
				GlStateManager.cullFace(GlStateManager.class_1024.field_5068);
			}

			this.method_4006(arg, arg2);
			GlStateManager.cullFace(GlStateManager.class_1024.field_5070);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			this.field_4729.method_4618(class_1059.field_5275);
			this.field_4729.method_4619(class_1059.field_5275).method_4627();
		}
	}

	private boolean method_4030(class_804 arg) {
		return arg.field_4285.method_4943() < 0.0F ^ arg.field_4285.method_4945() < 0.0F ^ arg.field_4285.method_4947() < 0.0F;
	}

	public void method_4010(class_1799 arg, int i, int j) {
		this.method_4021(arg, i, j, this.method_4007(arg));
	}

	protected void method_4021(class_1799 arg, int i, int j, class_1087 arg2) {
		GlStateManager.pushMatrix();
		this.field_4729.method_4618(class_1059.field_5275);
		this.field_4729.method_4619(class_1059.field_5275).method_4626(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.method_4017(i, j, arg2.method_4712());
		arg2.method_4709().method_3500(class_809.class_811.field_4317);
		this.method_4006(arg, arg2);
		GlStateManager.disableAlphaTest();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		this.field_4729.method_4618(class_1059.field_5275);
		this.field_4729.method_4619(class_1059.field_5275).method_4627();
	}

	private void method_4017(int i, int j, boolean bl) {
		GlStateManager.translatef((float)i, (float)j, 100.0F + this.field_4730);
		GlStateManager.translatef(8.0F, 8.0F, 0.0F);
		GlStateManager.scalef(1.0F, -1.0F, 1.0F);
		GlStateManager.scalef(16.0F, 16.0F, 16.0F);
		if (bl) {
			GlStateManager.enableLighting();
		} else {
			GlStateManager.disableLighting();
		}
	}

	public void method_4023(class_1799 arg, int i, int j) {
		this.method_4026(class_310.method_1551().field_1724, arg, i, j);
	}

	public void method_4026(@Nullable class_1309 arg, class_1799 arg2, int i, int j) {
		if (!arg2.method_7960()) {
			this.field_4730 += 50.0F;

			try {
				this.method_4021(arg2, i, j, this.method_4028(arg2, null, arg));
			} catch (Throwable var8) {
				class_128 lv = class_128.method_560(var8, "Rendering item");
				class_129 lv2 = lv.method_562("Item being rendered");
				lv2.method_577("Item Type", () -> String.valueOf(arg2.method_7909()));
				lv2.method_577("Item Damage", () -> String.valueOf(arg2.method_7919()));
				lv2.method_577("Item NBT", () -> String.valueOf(arg2.method_7969()));
				lv2.method_577("Item Foil", () -> String.valueOf(arg2.method_7958()));
				throw new class_148(lv);
			}

			this.field_4730 -= 50.0F;
		}
	}

	public void method_4025(class_327 arg, class_1799 arg2, int i, int j) {
		this.method_4022(arg, arg2, i, j, null);
	}

	public void method_4022(class_327 arg, class_1799 arg2, int i, int j, @Nullable String string) {
		if (!arg2.method_7960()) {
			if (arg2.method_7947() != 1 || string != null) {
				String string2 = string == null ? String.valueOf(arg2.method_7947()) : string;
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableBlend();
				arg.method_1720(string2, (float)(i + 19 - 2 - arg.method_1727(string2)), (float)(j + 6 + 3), 16777215);
				GlStateManager.enableBlend();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}

			if (arg2.method_7986()) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableTexture();
				GlStateManager.disableAlphaTest();
				GlStateManager.disableBlend();
				class_289 lv = class_289.method_1348();
				class_287 lv2 = lv.method_1349();
				float f = (float)arg2.method_7919();
				float g = (float)arg2.method_7936();
				float h = Math.max(0.0F, (g - f) / g);
				int k = Math.round(13.0F - f * 13.0F / g);
				int l = class_3532.method_15369(h / 3.0F, 1.0F, 1.0F);
				this.method_4004(lv2, i + 2, j + 13, 13, 2, 0, 0, 0, 255);
				this.method_4004(lv2, i + 2, j + 13, k, 1, l >> 16 & 0xFF, l >> 8 & 0xFF, l & 0xFF, 255);
				GlStateManager.enableBlend();
				GlStateManager.enableAlphaTest();
				GlStateManager.enableTexture();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}

			class_746 lv3 = class_310.method_1551().field_1724;
			float m = lv3 == null ? 0.0F : lv3.method_7357().method_7905(arg2.method_7909(), class_310.method_1551().method_1488());
			if (m > 0.0F) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableTexture();
				class_289 lv4 = class_289.method_1348();
				class_287 lv5 = lv4.method_1349();
				this.method_4004(lv5, i, j + class_3532.method_15375(16.0F * (1.0F - m)), 16, class_3532.method_15386(16.0F * m), 255, 255, 255, 127);
				GlStateManager.enableTexture();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}
		}
	}

	private void method_4004(class_287 arg, int i, int j, int k, int l, int m, int n, int o, int p) {
		arg.method_1328(7, class_290.field_1576);
		arg.method_1315((double)(i + 0), (double)(j + 0), 0.0).method_1323(m, n, o, p).method_1344();
		arg.method_1315((double)(i + 0), (double)(j + l), 0.0).method_1323(m, n, o, p).method_1344();
		arg.method_1315((double)(i + k), (double)(j + l), 0.0).method_1323(m, n, o, p).method_1344();
		arg.method_1315((double)(i + k), (double)(j + 0), 0.0).method_1323(m, n, o, p).method_1344();
		class_289.method_1348().method_1350();
	}

	@Override
	public void method_14491(class_3300 arg) {
		this.field_4732.method_3310();
	}
}
