package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.HoglinEntityRenderState;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HoglinEntityRenderer extends AbstractHoglinEntityRenderer<HoglinEntity> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/hoglin/hoglin.png");

	public HoglinEntityRenderer(EntityRendererFactory.Context context) {
		super(context, EntityModelLayers.HOGLIN, EntityModelLayers.HOGLIN_BABY, 0.7F);
	}

	public Identifier getTexture(HoglinEntityRenderState hoglinEntityRenderState) {
		return TEXTURE;
	}

	public void updateRenderState(HoglinEntity hoglinEntity, HoglinEntityRenderState hoglinEntityRenderState, float f) {
		super.updateRenderState(hoglinEntity, hoglinEntityRenderState, f);
		hoglinEntityRenderState.canConvert = hoglinEntity.canConvert();
	}

	protected boolean isShaking(HoglinEntityRenderState hoglinEntityRenderState) {
		return super.isShaking(hoglinEntityRenderState) || hoglinEntityRenderState.canConvert;
	}
}
