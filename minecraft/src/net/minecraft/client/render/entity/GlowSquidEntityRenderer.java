package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class GlowSquidEntityRenderer extends SquidEntityRenderer<GlowSquidEntity> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/squid/glow_squid.png");

	public GlowSquidEntityRenderer(EntityRendererFactory.Context context, SquidEntityModel<GlowSquidEntity> squidEntityModel) {
		super(context, squidEntityModel);
	}

	public Identifier getTexture(GlowSquidEntity glowSquidEntity) {
		return TEXTURE;
	}

	protected int getBlockLight(GlowSquidEntity glowSquidEntity, BlockPos blockPos) {
		int i = (int)MathHelper.clampedLerp(0.0F, 15.0F, 1.0F - (float)glowSquidEntity.getDarkTicksRemaining() / 10.0F);
		return i == 15 ? 15 : Math.max(i, super.getBlockLight(glowSquidEntity, blockPos));
	}
}
