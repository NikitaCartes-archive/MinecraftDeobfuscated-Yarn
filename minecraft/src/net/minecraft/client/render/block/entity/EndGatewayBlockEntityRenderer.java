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
	public void render(EndPortalBlockEntity endPortalBlockEntity, double d, double e, double f, float g, int i) {
		GlStateManager.disableFog();
		EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)endPortalBlockEntity;
		if (endGatewayBlockEntity.method_11420() || endGatewayBlockEntity.method_11421()) {
			GlStateManager.alphaFunc(516, 0.1F);
			this.bindTexture(BEAM_TEXTURE);
			float h = endGatewayBlockEntity.method_11420() ? endGatewayBlockEntity.method_11417(g) : endGatewayBlockEntity.method_11412(g);
			double j = endGatewayBlockEntity.method_11420() ? 256.0 - e : 50.0;
			h = MathHelper.sin(h * (float) Math.PI);
			int k = MathHelper.floor((double)h * j);
			float[] fs = endGatewayBlockEntity.method_11420() ? DyeColor.MAGENTA.getColorComponents() : DyeColor.PURPLE.getColorComponents();
			BeaconBlockEntityRenderer.method_3545(d, e, f, (double)g, (double)h, endGatewayBlockEntity.getWorld().getTime(), 0, k, fs, 0.15, 0.175);
			BeaconBlockEntityRenderer.method_3545(d, e, f, (double)g, (double)h, endGatewayBlockEntity.getWorld().getTime(), 0, -k, fs, 0.15, 0.175);
		}

		super.render(endPortalBlockEntity, d, e, f, g, i);
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
