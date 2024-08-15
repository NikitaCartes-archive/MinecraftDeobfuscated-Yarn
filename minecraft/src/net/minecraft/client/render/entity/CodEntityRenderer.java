package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CodEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class CodEntityRenderer extends MobEntityRenderer<CodEntity, LivingEntityRenderState, CodEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fish/cod.png");

	public CodEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new CodEntityModel(context.getPart(EntityModelLayers.COD)), 0.3F);
	}

	public Identifier getTexture(LivingEntityRenderState livingEntityRenderState) {
		return TEXTURE;
	}

	public LivingEntityRenderState getRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	protected void setupTransforms(LivingEntityRenderState state, MatrixStack matrices, float animationProgress, float bodyYaw) {
		super.setupTransforms(state, matrices, animationProgress, bodyYaw);
		float f = 4.3F * MathHelper.sin(0.6F * state.age);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f));
		if (!state.touchingWater) {
			matrices.translate(0.1F, 0.1F, -0.1F);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
		}
	}
}
