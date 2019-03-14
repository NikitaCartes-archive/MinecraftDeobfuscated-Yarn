package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EndGatewayBlockEntityRenderer extends EndPortalBlockEntityRenderer {
	private static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/end_gateway_beam.png");

	@Override
	public void method_3591(EndPortalBlockEntity endPortalBlockEntity, double d, double e, double f, float g, int i) {
		GlStateManager.disableFog();
		EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)endPortalBlockEntity;
		if (endGatewayBlockEntity.isRecentlyGenerated() || endGatewayBlockEntity.needsCooldownBeforeTeleporting()) {
			GlStateManager.alphaFunc(516, 0.1F);
			this.bindTexture(BEAM_TEXTURE);
			float h = endGatewayBlockEntity.isRecentlyGenerated()
				? endGatewayBlockEntity.getRecentlyGeneratedBeamHeight(g)
				: endGatewayBlockEntity.getCooldownBeamHeight(g);
			double j = endGatewayBlockEntity.isRecentlyGenerated() ? 256.0 - e : 50.0;
			h = MathHelper.sin(h * (float) Math.PI);
			int k = MathHelper.floor((double)h * j);
			float[] fs = endGatewayBlockEntity.isRecentlyGenerated() ? DyeColor.field_7958.getColorComponents() : DyeColor.field_7945.getColorComponents();
			BeaconBlockEntityRenderer.renderLightBeam(d, e, f, (double)g, (double)h, endGatewayBlockEntity.getWorld().getTime(), 0, k, fs, 0.15, 0.175);
			BeaconBlockEntityRenderer.renderLightBeam(d, e, f, (double)g, (double)h, endGatewayBlockEntity.getWorld().getTime(), 0, -k, fs, 0.15, 0.175);
		}

		super.method_3591(endPortalBlockEntity, d, e, f, g, i);
		GlStateManager.enableFog();
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
