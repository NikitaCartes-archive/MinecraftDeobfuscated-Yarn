package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.WitherArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WitherEntityRenderer extends MobEntityRenderer<WitherEntity, WitherEntityRenderState, WitherEntityModel> {
	private static final Identifier INVULNERABLE_TEXTURE = Identifier.ofVanilla("textures/entity/wither/wither_invulnerable.png");
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/wither/wither.png");

	public WitherEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new WitherEntityModel(context.getPart(EntityModelLayers.WITHER)), 1.0F);
		this.addFeature(new WitherArmorFeatureRenderer(this, context.getModelLoader()));
	}

	protected int getBlockLight(WitherEntity witherEntity, BlockPos blockPos) {
		return 15;
	}

	public Identifier getTexture(WitherEntityRenderState witherEntityRenderState) {
		int i = MathHelper.floor(witherEntityRenderState.invulnerableTimer);
		return i > 0 && (i > 80 || i / 5 % 2 != 1) ? INVULNERABLE_TEXTURE : TEXTURE;
	}

	public WitherEntityRenderState createRenderState() {
		return new WitherEntityRenderState();
	}

	protected void scale(WitherEntityRenderState witherEntityRenderState, MatrixStack matrixStack) {
		float f = 2.0F;
		if (witherEntityRenderState.invulnerableTimer > 0.0F) {
			f -= witherEntityRenderState.invulnerableTimer / 220.0F * 0.5F;
		}

		matrixStack.scale(f, f, f);
	}

	public void updateRenderState(WitherEntity witherEntity, WitherEntityRenderState witherEntityRenderState, float f) {
		super.updateRenderState(witherEntity, witherEntityRenderState, f);
		int i = witherEntity.getInvulnerableTimer();
		witherEntityRenderState.invulnerableTimer = i > 0 ? (float)i - f : 0.0F;
		System.arraycopy(witherEntity.getSideHeadPitches(), 0, witherEntityRenderState.sideHeadPitches, 0, witherEntityRenderState.sideHeadPitches.length);
		System.arraycopy(witherEntity.getSideHeadYaws(), 0, witherEntityRenderState.sideHeadYaws, 0, witherEntityRenderState.sideHeadYaws.length);
		witherEntityRenderState.renderOverlay = witherEntity.shouldRenderOverlay();
	}
}
