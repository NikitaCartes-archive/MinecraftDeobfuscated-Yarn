package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EndGatewayBlockEntityRenderer extends EndPortalBlockEntityRenderer<EndGatewayBlockEntity> {
	private static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/end_gateway_beam.png");

	public EndGatewayBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		super(context);
	}

	public void render(EndGatewayBlockEntity endGatewayBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		if (endGatewayBlockEntity.isRecentlyGenerated() || endGatewayBlockEntity.needsCooldownBeforeTeleporting()) {
			float g = endGatewayBlockEntity.isRecentlyGenerated()
				? endGatewayBlockEntity.getRecentlyGeneratedBeamHeight(f)
				: endGatewayBlockEntity.getCooldownBeamHeight(f);
			double d = endGatewayBlockEntity.isRecentlyGenerated() ? (double)endGatewayBlockEntity.getWorld().getTopHeightLimit() : 50.0;
			g = MathHelper.sin(g * (float) Math.PI);
			int k = MathHelper.floor((double)g * d);
			float[] fs = endGatewayBlockEntity.isRecentlyGenerated() ? DyeColor.MAGENTA.getColorComponents() : DyeColor.PURPLE.getColorComponents();
			long l = endGatewayBlockEntity.getWorld().getTime();
			BeaconBlockEntityRenderer.renderBeam(matrixStack, vertexConsumerProvider, BEAM_TEXTURE, f, g, l, -k, k * 2, fs, 0.15F, 0.175F);
		}

		super.render(endGatewayBlockEntity, f, matrixStack, vertexConsumerProvider, i, j);
	}

	@Override
	protected int getDetailLevel(double distance) {
		return super.getDetailLevel(distance) + 1;
	}

	@Override
	protected float getTopYOffset() {
		return 1.0F;
	}
}
