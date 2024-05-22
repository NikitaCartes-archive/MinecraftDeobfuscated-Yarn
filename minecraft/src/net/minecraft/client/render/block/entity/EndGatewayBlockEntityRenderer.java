package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EndGatewayBlockEntityRenderer extends EndPortalBlockEntityRenderer<EndGatewayBlockEntity> {
	private static final Identifier BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/end_gateway_beam.png");

	public EndGatewayBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		super(context);
	}

	public void render(EndGatewayBlockEntity endGatewayBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		if (endGatewayBlockEntity.isRecentlyGenerated() || endGatewayBlockEntity.needsCooldownBeforeTeleporting()) {
			float g = endGatewayBlockEntity.isRecentlyGenerated()
				? endGatewayBlockEntity.getRecentlyGeneratedBeamHeight(f)
				: endGatewayBlockEntity.getCooldownBeamHeight(f);
			double d = endGatewayBlockEntity.isRecentlyGenerated() ? (double)endGatewayBlockEntity.getWorld().getTopY() : 50.0;
			g = MathHelper.sin(g * (float) Math.PI);
			int k = MathHelper.floor((double)g * d);
			int l = endGatewayBlockEntity.isRecentlyGenerated() ? DyeColor.MAGENTA.getEntityColor() : DyeColor.PURPLE.getEntityColor();
			long m = endGatewayBlockEntity.getWorld().getTime();
			BeaconBlockEntityRenderer.renderBeam(matrixStack, vertexConsumerProvider, BEAM_TEXTURE, f, g, m, -k, k * 2, l, 0.15F, 0.175F);
		}

		super.render(endGatewayBlockEntity, f, matrixStack, vertexConsumerProvider, i, j);
	}

	@Override
	protected float getTopYOffset() {
		return 1.0F;
	}

	@Override
	protected float getBottomYOffset() {
		return 0.0F;
	}

	@Override
	protected RenderLayer getLayer() {
		return RenderLayer.getEndGateway();
	}

	@Override
	public int getRenderDistance() {
		return 256;
	}
}
