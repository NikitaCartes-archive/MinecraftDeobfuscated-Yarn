package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EndGatewayBlockEntityRenderer extends EndPortalBlockEntityRenderer<EndGatewayBlockEntity> {
	private static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/end_gateway_beam.png");

	public EndGatewayBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_22751(
		EndGatewayBlockEntity endGatewayBlockEntity,
		double d,
		double e,
		double f,
		float g,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		int j
	) {
		if (endGatewayBlockEntity.isRecentlyGenerated() || endGatewayBlockEntity.needsCooldownBeforeTeleporting()) {
			float h = endGatewayBlockEntity.isRecentlyGenerated()
				? endGatewayBlockEntity.getRecentlyGeneratedBeamHeight(g)
				: endGatewayBlockEntity.getCooldownBeamHeight(g);
			double k = endGatewayBlockEntity.isRecentlyGenerated() ? 256.0 - e : 50.0;
			h = MathHelper.sin(h * (float) Math.PI);
			int l = MathHelper.floor((double)h * k);
			float[] fs = endGatewayBlockEntity.isRecentlyGenerated() ? DyeColor.MAGENTA.getColorComponents() : DyeColor.PURPLE.getColorComponents();
			long m = endGatewayBlockEntity.getWorld().getTime();
			BeaconBlockEntityRenderer.renderLightBeam(matrixStack, layeredVertexConsumerStorage, BEAM_TEXTURE, g, h, m, 0, l, fs, 0.15F, 0.175F);
			BeaconBlockEntityRenderer.renderLightBeam(matrixStack, layeredVertexConsumerStorage, BEAM_TEXTURE, g, h, m, 0, -l, fs, 0.15F, 0.175F);
		}

		super.method_3591(endGatewayBlockEntity, d, e, f, g, matrixStack, layeredVertexConsumerStorage, i, j);
	}

	@Override
	protected int method_3592(double d) {
		return super.method_3592(d) + 1;
	}

	@Override
	protected float method_3594() {
		return 1.0F;
	}
}
