package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.state.TntEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class TntEntityRenderer extends EntityRenderer<TntEntity, TntEntityRenderState> {
	private final BlockRenderManager blockRenderManager;

	public TntEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0.5F;
		this.blockRenderManager = context.getBlockRenderManager();
	}

	public void render(TntEntityRenderState tntEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.translate(0.0F, 0.5F, 0.0F);
		float f = tntEntityRenderState.fuse;
		if (tntEntityRenderState.fuse < 10.0F) {
			float g = 1.0F - tntEntityRenderState.fuse / 10.0F;
			g = MathHelper.clamp(g, 0.0F, 1.0F);
			g *= g;
			g *= g;
			float h = 1.0F + g * 0.3F;
			matrixStack.scale(h, h, h);
		}

		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
		matrixStack.translate(-0.5F, -0.5F, 0.5F);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
		if (tntEntityRenderState.blockState != null) {
			TntMinecartEntityRenderer.renderFlashingBlock(
				this.blockRenderManager, tntEntityRenderState.blockState, matrixStack, vertexConsumerProvider, i, (int)f / 5 % 2 == 0
			);
		}

		matrixStack.pop();
		super.render(tntEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	public TntEntityRenderState createRenderState() {
		return new TntEntityRenderState();
	}

	public void updateRenderState(TntEntity tntEntity, TntEntityRenderState tntEntityRenderState, float f) {
		super.updateRenderState(tntEntity, tntEntityRenderState, f);
		tntEntityRenderState.fuse = (float)tntEntity.getFuse() - f + 1.0F;
		tntEntityRenderState.blockState = tntEntity.getBlockState();
	}
}
