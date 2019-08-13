package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class IronGolemEntityRenderer extends MobEntityRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/iron_golem.png");

	public IronGolemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new IronGolemEntityModel<>(), 0.7F);
		this.addFeature(new IronGolemFlowerFeatureRenderer(this));
	}

	protected Identifier method_3987(IronGolemEntity ironGolemEntity) {
		return SKIN;
	}

	protected void method_3986(IronGolemEntity ironGolemEntity, float f, float g, float h) {
		super.setupTransforms(ironGolemEntity, f, g, h);
		if (!((double)ironGolemEntity.limbDistance < 0.01)) {
			float i = 13.0F;
			float j = ironGolemEntity.limbAngle - ironGolemEntity.limbDistance * (1.0F - h) + 6.0F;
			float k = (Math.abs(j % 13.0F - 6.5F) - 3.25F) / 3.25F;
			GlStateManager.rotatef(6.5F * k, 0.0F, 0.0F, 1.0F);
		}
	}
}
