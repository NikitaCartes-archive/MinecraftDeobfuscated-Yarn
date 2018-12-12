package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DolphinEntityRenderer extends MobEntityRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
	private static final Identifier field_4654 = new Identifier("textures/entity/dolphin.png");

	public DolphinEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new DolphinEntityModel<>(), 0.7F);
		this.addFeature(new DolphinHeldItemFeatureRenderer(this));
	}

	protected Identifier getTexture(DolphinEntity dolphinEntity) {
		return field_4654;
	}

	protected void method_3901(DolphinEntity dolphinEntity, float f) {
		float g = 1.0F;
		GlStateManager.scalef(1.0F, 1.0F, 1.0F);
	}

	protected void setupTransforms(DolphinEntity dolphinEntity, float f, float g, float h) {
		super.setupTransforms(dolphinEntity, f, g, h);
	}
}
