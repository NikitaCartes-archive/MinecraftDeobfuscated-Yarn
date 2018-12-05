package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.LayerEntityRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_3684 implements LayerEntityRenderer<CatEntity> {
	private static final Identifier field_16260 = new Identifier("textures/entity/cat/cat_collar.png");
	private final CatEntityRenderer field_16262;
	private final CatEntityModel field_16261 = new CatEntityModel(0.01F);

	public class_3684(CatEntityRenderer catEntityRenderer) {
		this.field_16262 = catEntityRenderer;
	}

	public void method_16047(CatEntity catEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (catEntity.isTamed() && !catEntity.isInvisible()) {
			this.field_16262.bindTexture(field_16260);
			float[] fs = catEntity.getCollarColor().getColorComponents();
			GlStateManager.color3f(fs[0], fs[1], fs[2]);
			this.field_16261.setAttributes(this.field_16262.method_4038());
			this.field_16261.animateModel(catEntity, f, g, h);
			this.field_16261.render(catEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return true;
	}
}
