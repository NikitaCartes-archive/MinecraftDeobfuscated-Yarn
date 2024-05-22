package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.WitherArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class WitherEntityRenderer extends MobEntityRenderer<WitherEntity, WitherEntityModel<WitherEntity>> {
	private static final Identifier INVULNERABLE_TEXTURE = Identifier.ofVanilla("textures/entity/wither/wither_invulnerable.png");
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/wither/wither.png");

	public WitherEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new WitherEntityModel<>(context.getPart(EntityModelLayers.WITHER)), 1.0F);
		this.addFeature(new WitherArmorFeatureRenderer(this, context.getModelLoader()));
	}

	protected int getBlockLight(WitherEntity witherEntity, BlockPos blockPos) {
		return 15;
	}

	public Identifier getTexture(WitherEntity witherEntity) {
		int i = witherEntity.getInvulnerableTimer();
		return i > 0 && (i > 80 || i / 5 % 2 != 1) ? INVULNERABLE_TEXTURE : TEXTURE;
	}

	protected void scale(WitherEntity witherEntity, MatrixStack matrixStack, float f) {
		float g = 2.0F;
		int i = witherEntity.getInvulnerableTimer();
		if (i > 0) {
			g -= ((float)i - f) / 220.0F * 0.5F;
		}

		matrixStack.scale(g, g, g);
	}
}
