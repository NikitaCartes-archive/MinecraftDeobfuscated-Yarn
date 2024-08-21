package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.client.render.entity.state.PiglinEntityRenderState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PiglinEntityRenderer extends BipedEntityRenderer<AbstractPiglinEntity, PiglinEntityRenderState, PiglinEntityModel> {
	private static final Identifier PIGLIN_TEXTURE = Identifier.ofVanilla("textures/entity/piglin/piglin.png");
	private static final Identifier PIGLIN_BRUTE_TEXTURE = Identifier.ofVanilla("textures/entity/piglin/piglin_brute.png");
	public static final HeadFeatureRenderer.HeadTransformation HEAD_TRANSFORMATION = new HeadFeatureRenderer.HeadTransformation(0.0F, 0.0F, 1.0019531F);

	public PiglinEntityRenderer(
		EntityRendererFactory.Context ctx,
		EntityModelLayer mainLayer,
		EntityModelLayer babyMainLayer,
		EntityModelLayer armorInnerLayer,
		EntityModelLayer armorOuterLayer,
		EntityModelLayer babyArmorInnerLayer,
		EntityModelLayer babyArmorOuterLayer
	) {
		super(ctx, new PiglinEntityModel(ctx.getPart(mainLayer)), new PiglinEntityModel(ctx.getPart(babyMainLayer)), 0.5F, HEAD_TRANSFORMATION);
		this.addFeature(
			new ArmorFeatureRenderer<>(
				this,
				new ArmorEntityModel(ctx.getPart(armorInnerLayer)),
				new ArmorEntityModel(ctx.getPart(armorOuterLayer)),
				new ArmorEntityModel(ctx.getPart(babyArmorInnerLayer)),
				new ArmorEntityModel(ctx.getPart(babyArmorOuterLayer)),
				ctx.getModelManager()
			)
		);
	}

	public Identifier getTexture(PiglinEntityRenderState piglinEntityRenderState) {
		return piglinEntityRenderState.brute ? PIGLIN_BRUTE_TEXTURE : PIGLIN_TEXTURE;
	}

	public PiglinEntityRenderState getRenderState() {
		return new PiglinEntityRenderState();
	}

	public void updateRenderState(AbstractPiglinEntity abstractPiglinEntity, PiglinEntityRenderState piglinEntityRenderState, float f) {
		super.updateRenderState(abstractPiglinEntity, piglinEntityRenderState, f);
		piglinEntityRenderState.brute = abstractPiglinEntity.getType() == EntityType.PIGLIN_BRUTE;
		piglinEntityRenderState.activity = abstractPiglinEntity.getActivity();
		piglinEntityRenderState.piglinCrossbowPullTime = (float)CrossbowItem.getPullTime(abstractPiglinEntity.getActiveItem(), abstractPiglinEntity);
		piglinEntityRenderState.shouldZombify = abstractPiglinEntity.shouldZombify();
	}

	protected boolean isShaking(PiglinEntityRenderState piglinEntityRenderState) {
		return super.isShaking(piglinEntityRenderState) || piglinEntityRenderState.shouldZombify;
	}
}
