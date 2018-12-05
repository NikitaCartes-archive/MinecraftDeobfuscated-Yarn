package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WolfCollarEntityRenderer implements LayerEntityRenderer<WolfEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/wolf/wolf_collar.png");
	private final WolfEntityRenderer field_4914;

	public WolfCollarEntityRenderer(WolfEntityRenderer wolfEntityRenderer) {
		this.field_4914 = wolfEntityRenderer;
	}

	public void render(WolfEntity wolfEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (wolfEntity.isTamed() && !wolfEntity.isInvisible()) {
			this.field_4914.bindTexture(SKIN);
			float[] fs = wolfEntity.method_6713().getColorComponents();
			GlStateManager.color3f(fs[0], fs[1], fs[2]);
			this.field_4914.method_4038().render(wolfEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return true;
	}
}
