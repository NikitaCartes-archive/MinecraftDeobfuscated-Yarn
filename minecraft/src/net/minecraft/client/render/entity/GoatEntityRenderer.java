package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GoatEntityModel;
import net.minecraft.client.render.entity.state.GoatEntityRenderState;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GoatEntityRenderer extends AgeableMobEntityRenderer<GoatEntity, GoatEntityRenderState, GoatEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/goat/goat.png");

	public GoatEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new GoatEntityModel(context.getPart(EntityModelLayers.GOAT)), new GoatEntityModel(context.getPart(EntityModelLayers.GOAT_BABY)), 0.7F);
	}

	public Identifier getTexture(GoatEntityRenderState goatEntityRenderState) {
		return TEXTURE;
	}

	public GoatEntityRenderState createRenderState() {
		return new GoatEntityRenderState();
	}

	public void updateRenderState(GoatEntity goatEntity, GoatEntityRenderState goatEntityRenderState, float f) {
		super.updateRenderState(goatEntity, goatEntityRenderState, f);
		goatEntityRenderState.hasLeftHorn = goatEntity.hasLeftHorn();
		goatEntityRenderState.hasRightHorn = goatEntity.hasRightHorn();
		goatEntityRenderState.headPitch = goatEntity.getHeadPitch();
	}
}
