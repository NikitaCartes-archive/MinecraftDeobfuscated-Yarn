package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class IronGolemEntityRenderer extends MobEntityRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/iron_golem.png");

	public IronGolemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new IronGolemEntityModel<>(), 0.7F);
		this.addFeature(new IronGolemFlowerFeatureRenderer(this));
	}

	public Identifier method_3987(IronGolemEntity ironGolemEntity) {
		return SKIN;
	}

	protected void method_3986(IronGolemEntity ironGolemEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(ironGolemEntity, matrixStack, f, g, h);
		if (!((double)ironGolemEntity.limbDistance < 0.01)) {
			float i = 13.0F;
			float j = ironGolemEntity.limbAngle - ironGolemEntity.limbDistance * (1.0F - h) + 6.0F;
			float k = (Math.abs(j % 13.0F - 6.5F) - 3.25F) / 3.25F;
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(6.5F * k, true));
		}
	}
}
