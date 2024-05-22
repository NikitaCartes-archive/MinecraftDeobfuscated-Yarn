package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class IronGolemEntityRenderer extends MobEntityRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/iron_golem/iron_golem.png");

	public IronGolemEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new IronGolemEntityModel<>(context.getPart(EntityModelLayers.IRON_GOLEM)), 0.7F);
		this.addFeature(new IronGolemCrackFeatureRenderer(this));
		this.addFeature(new IronGolemFlowerFeatureRenderer(this, context.getBlockRenderManager()));
	}

	public Identifier getTexture(IronGolemEntity ironGolemEntity) {
		return TEXTURE;
	}

	protected void setupTransforms(IronGolemEntity ironGolemEntity, MatrixStack matrixStack, float f, float g, float h, float i) {
		super.setupTransforms(ironGolemEntity, matrixStack, f, g, h, i);
		if (!((double)ironGolemEntity.limbAnimator.getSpeed() < 0.01)) {
			float j = 13.0F;
			float k = ironGolemEntity.limbAnimator.getPos(h) + 6.0F;
			float l = (Math.abs(k % 13.0F - 6.5F) - 3.25F) / 3.25F;
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.5F * l));
		}
	}
}
