package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SlimeEntityRenderer extends MobEntityRenderer<SlimeEntity, SlimeEntityRenderState, SlimeEntityModel> {
	public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/slime/slime.png");

	public SlimeEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SlimeEntityModel(context.getPart(EntityModelLayers.SLIME)), 0.25F);
		this.addFeature(new SlimeOverlayFeatureRenderer(this, context.getModelLoader()));
	}

	public void render(SlimeEntityRenderState slimeEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		this.shadowRadius = 0.25F * (float)slimeEntityRenderState.size;
		super.render(slimeEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	protected void scale(SlimeEntityRenderState slimeEntityRenderState, MatrixStack matrixStack) {
		float f = 0.999F;
		matrixStack.scale(0.999F, 0.999F, 0.999F);
		matrixStack.translate(0.0F, 0.001F, 0.0F);
		float g = (float)slimeEntityRenderState.size;
		float h = slimeEntityRenderState.stretch / (g * 0.5F + 1.0F);
		float i = 1.0F / (h + 1.0F);
		matrixStack.scale(i * g, 1.0F / i * g, i * g);
	}

	public Identifier getTexture(SlimeEntityRenderState slimeEntityRenderState) {
		return TEXTURE;
	}

	public SlimeEntityRenderState createRenderState() {
		return new SlimeEntityRenderState();
	}

	public void updateRenderState(SlimeEntity slimeEntity, SlimeEntityRenderState slimeEntityRenderState, float f) {
		super.updateRenderState(slimeEntity, slimeEntityRenderState, f);
		slimeEntityRenderState.stretch = MathHelper.lerp(f, slimeEntity.lastStretch, slimeEntity.stretch);
		slimeEntityRenderState.size = slimeEntity.getSize();
	}
}
