package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.entity.state.WitchEntityRenderState;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WitchEntityRenderer extends MobEntityRenderer<WitchEntity, WitchEntityRenderState, WitchEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/witch.png");

	public WitchEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new WitchEntityModel(context.getPart(EntityModelLayers.WITCH)), 0.5F);
		this.addFeature(new WitchHeldItemFeatureRenderer(this, context.getItemRenderer()));
	}

	public Identifier getTexture(WitchEntityRenderState witchEntityRenderState) {
		return TEXTURE;
	}

	public WitchEntityRenderState getRenderState() {
		return new WitchEntityRenderState();
	}

	public void updateRenderState(WitchEntity witchEntity, WitchEntityRenderState witchEntityRenderState, float f) {
		super.updateRenderState(witchEntity, witchEntityRenderState, f);
		witchEntityRenderState.id = witchEntity.getId();
		witchEntityRenderState.holdingItem = !witchEntity.getMainHandStack().isEmpty();
	}
}
