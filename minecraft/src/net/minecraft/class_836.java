package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_836 extends class_827<class_2631> {
	public static class_836 field_4392;
	private static final Map<class_2484.class_2485, class_607> field_4391 = class_156.method_654(Maps.<class_2484.class_2485, class_607>newHashMap(), hashMap -> {
		class_607 lv = new class_607(0, 0, 64, 32);
		class_607 lv2 = new class_569();
		class_626 lv3 = new class_626(0.0F);
		hashMap.put(class_2484.class_2486.field_11512, lv);
		hashMap.put(class_2484.class_2486.field_11513, lv);
		hashMap.put(class_2484.class_2486.field_11510, lv2);
		hashMap.put(class_2484.class_2486.field_11508, lv2);
		hashMap.put(class_2484.class_2486.field_11507, lv);
		hashMap.put(class_2484.class_2486.field_11511, lv3);
	});
	private static final Map<class_2484.class_2485, class_2960> field_4390 = class_156.method_654(
		Maps.<class_2484.class_2485, class_2960>newHashMap(), hashMap -> {
			hashMap.put(class_2484.class_2486.field_11512, new class_2960("textures/entity/skeleton/skeleton.png"));
			hashMap.put(class_2484.class_2486.field_11513, new class_2960("textures/entity/skeleton/wither_skeleton.png"));
			hashMap.put(class_2484.class_2486.field_11508, new class_2960("textures/entity/zombie/zombie.png"));
			hashMap.put(class_2484.class_2486.field_11507, new class_2960("textures/entity/creeper/creeper.png"));
			hashMap.put(class_2484.class_2486.field_11511, new class_2960("textures/entity/enderdragon/dragon.png"));
			hashMap.put(class_2484.class_2486.field_11510, class_1068.method_4649());
		}
	);

	public void method_3577(class_2631 arg, double d, double e, double f, float g, int i) {
		float h = arg.method_11338(g);
		class_2680 lv = arg.method_11010();
		boolean bl = lv.method_11614() instanceof class_2549;
		class_2350 lv2 = bl ? lv.method_11654(class_2549.field_11724) : null;
		float j = 22.5F * (float)(bl ? (2 + lv2.method_10161()) * 4 : (Integer)lv.method_11654(class_2484.field_11505));
		this.method_3581((float)d, (float)e, (float)f, lv2, j, ((class_2190)lv.method_11614()).method_9327(), arg.method_11334(), i, h);
	}

	@Override
	public void method_3568(class_824 arg) {
		super.method_3568(arg);
		field_4392 = this;
	}

	public void method_3581(
		float f, float g, float h, @Nullable class_2350 arg, float i, class_2484.class_2485 arg2, @Nullable GameProfile gameProfile, int j, float k
	) {
		class_607 lv = (class_607)field_4391.get(arg2);
		if (j >= 0) {
			this.method_3566(field_4368[j]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(4.0F, 2.0F, 1.0F);
			GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			this.method_3566(this.method_3578(arg2, gameProfile));
		}

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		if (arg == null) {
			GlStateManager.translatef(f + 0.5F, g, h + 0.5F);
		} else {
			switch (arg) {
				case field_11043:
					GlStateManager.translatef(f + 0.5F, g + 0.25F, h + 0.74F);
					break;
				case field_11035:
					GlStateManager.translatef(f + 0.5F, g + 0.25F, h + 0.26F);
					break;
				case field_11039:
					GlStateManager.translatef(f + 0.74F, g + 0.25F, h + 0.5F);
					break;
				case field_11034:
				default:
					GlStateManager.translatef(f + 0.26F, g + 0.25F, h + 0.5F);
			}
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlphaTest();
		if (arg2 == class_2484.class_2486.field_11510) {
			GlStateManager.setProfile(GlStateManager.class_1032.field_5128);
		}

		lv.method_2821(k, 0.0F, 0.0F, i, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
		if (j >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}

	private class_2960 method_3578(class_2484.class_2485 arg, @Nullable GameProfile gameProfile) {
		class_2960 lv = (class_2960)field_4390.get(arg);
		if (arg == class_2484.class_2486.field_11510 && gameProfile != null) {
			class_310 lv2 = class_310.method_1551();
			Map<Type, MinecraftProfileTexture> map = lv2.method_1582().method_4654(gameProfile);
			if (map.containsKey(Type.SKIN)) {
				lv = lv2.method_1582().method_4656((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
			} else {
				lv = class_1068.method_4648(class_1657.method_7271(gameProfile));
			}
		}

		return lv;
	}
}
