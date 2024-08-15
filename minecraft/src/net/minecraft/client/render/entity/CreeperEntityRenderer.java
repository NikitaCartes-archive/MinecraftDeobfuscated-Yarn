package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CreeperEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CreeperEntityRenderer extends MobEntityRenderer<CreeperEntity, CreeperEntityRenderState, CreeperEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/creeper/creeper.png");

	public CreeperEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new CreeperEntityModel(context.getPart(EntityModelLayers.CREEPER)), 0.5F);
		this.addFeature(new CreeperChargeFeatureRenderer(this, context.getModelLoader()));
	}

	protected void scale(CreeperEntityRenderState creeperEntityRenderState, MatrixStack matrixStack) {
		float f = creeperEntityRenderState.fuseTime;
		float g = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		f *= f;
		f *= f;
		float h = (1.0F + f * 0.4F) * g;
		float i = (1.0F + f * 0.1F) / g;
		matrixStack.scale(h, i, h);
	}

	protected float getAnimationCounter(CreeperEntityRenderState creeperEntityRenderState) {
		float f = creeperEntityRenderState.fuseTime;
		return (int)(f * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(f, 0.5F, 1.0F);
	}

	public Identifier getTexture(CreeperEntityRenderState creeperEntityRenderState) {
		return TEXTURE;
	}

	public CreeperEntityRenderState getRenderState() {
		return new CreeperEntityRenderState();
	}

	public void updateRenderState(CreeperEntity creeperEntity, CreeperEntityRenderState creeperEntityRenderState, float f) {
		super.updateRenderState(creeperEntity, creeperEntityRenderState, f);
		creeperEntityRenderState.fuseTime = creeperEntity.getClientFuseTime(f);
		creeperEntityRenderState.charged = creeperEntity.isCharged();
	}
}
