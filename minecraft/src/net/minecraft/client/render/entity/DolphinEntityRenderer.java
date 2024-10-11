package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.DolphinEntityRenderState;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DolphinEntityRenderer extends AgeableMobEntityRenderer<DolphinEntity, DolphinEntityRenderState, DolphinEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/dolphin.png");

	public DolphinEntityRenderer(EntityRendererFactory.Context context) {
		super(
			context, new DolphinEntityModel(context.getPart(EntityModelLayers.DOLPHIN)), new DolphinEntityModel(context.getPart(EntityModelLayers.DOLPHIN_BABY)), 0.7F
		);
		this.addFeature(new DolphinHeldItemFeatureRenderer(this, context.getItemRenderer()));
	}

	public Identifier getTexture(DolphinEntityRenderState dolphinEntityRenderState) {
		return TEXTURE;
	}

	public DolphinEntityRenderState createRenderState() {
		return new DolphinEntityRenderState();
	}

	public void updateRenderState(DolphinEntity dolphinEntity, DolphinEntityRenderState dolphinEntityRenderState, float f) {
		super.updateRenderState(dolphinEntity, dolphinEntityRenderState, f);
		dolphinEntityRenderState.moving = dolphinEntity.getVelocity().horizontalLengthSquared() > 1.0E-7;
	}
}
