package net.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_530 implements class_537 {
	private final GameProfile field_3253;
	private final class_2960 field_3252;

	public class_530(GameProfile gameProfile) {
		this.field_3253 = gameProfile;
		class_310 lv = class_310.method_1551();
		Map<Type, MinecraftProfileTexture> map = lv.method_1582().method_4654(gameProfile);
		if (map.containsKey(Type.SKIN)) {
			this.field_3252 = lv.method_1582().method_4656((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
		} else {
			this.field_3252 = class_1068.method_4648(class_1657.method_7271(gameProfile));
		}
	}

	@Override
	public void method_2783(class_531 arg) {
		class_310.method_1551().method_1562().method_2883(new class_2884(this.field_3253.getId()));
	}

	@Override
	public class_2561 method_16892() {
		return new class_2585(this.field_3253.getName());
	}

	@Override
	public void method_2784(float f, int i) {
		class_310.method_1551().method_1531().method_4618(this.field_3252);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, (float)i / 255.0F);
		class_332.blit(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
		class_332.blit(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
	}

	@Override
	public boolean method_16893() {
		return true;
	}
}
