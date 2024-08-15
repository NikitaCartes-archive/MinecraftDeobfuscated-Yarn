package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.PhantomEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.client.render.entity.state.PhantomEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class PhantomEntityRenderer extends MobEntityRenderer<PhantomEntity, PhantomEntityRenderState, PhantomEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/phantom.png");

	public PhantomEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new PhantomEntityModel(context.getPart(EntityModelLayers.PHANTOM)), 0.75F);
		this.addFeature(new PhantomEyesFeatureRenderer(this));
	}

	public Identifier getTexture(PhantomEntityRenderState phantomEntityRenderState) {
		return TEXTURE;
	}

	public PhantomEntityRenderState getRenderState() {
		return new PhantomEntityRenderState();
	}

	public void updateRenderState(PhantomEntity phantomEntity, PhantomEntityRenderState phantomEntityRenderState, float f) {
		super.updateRenderState(phantomEntity, phantomEntityRenderState, f);
		phantomEntityRenderState.wingFlapProgress = (float)phantomEntity.getWingFlapTickOffset() + phantomEntityRenderState.age;
		phantomEntityRenderState.size = phantomEntity.getPhantomSize();
	}

	protected void scale(PhantomEntityRenderState phantomEntityRenderState, MatrixStack matrixStack) {
		float f = 1.0F + 0.15F * (float)phantomEntityRenderState.size;
		matrixStack.scale(f, f, f);
		matrixStack.translate(0.0F, 1.3125F, 0.1875F);
	}

	protected void setupTransforms(PhantomEntityRenderState phantomEntityRenderState, MatrixStack matrixStack, float f, float g) {
		super.setupTransforms(phantomEntityRenderState, matrixStack, f, g);
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(phantomEntityRenderState.pitch));
	}
}
