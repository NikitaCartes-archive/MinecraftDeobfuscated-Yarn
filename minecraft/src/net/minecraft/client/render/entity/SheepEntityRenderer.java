package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SheepEntityRenderer extends AgeableMobEntityRenderer<SheepEntity, SheepEntityRenderState, SheepEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/sheep/sheep.png");

	public SheepEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SheepEntityModel(context.getPart(EntityModelLayers.SHEEP)), new SheepEntityModel(context.getPart(EntityModelLayers.SHEEP_BABY)), 0.7F);
		this.addFeature(new SheepWoolFeatureRenderer(this, context.getModelLoader()));
	}

	public Identifier getTexture(SheepEntityRenderState sheepEntityRenderState) {
		return TEXTURE;
	}

	public SheepEntityRenderState createRenderState() {
		return new SheepEntityRenderState();
	}

	public void updateRenderState(SheepEntity sheepEntity, SheepEntityRenderState sheepEntityRenderState, float f) {
		super.updateRenderState(sheepEntity, sheepEntityRenderState, f);
		sheepEntityRenderState.headAngle = sheepEntity.getHeadAngle(f);
		sheepEntityRenderState.neckAngle = sheepEntity.getNeckAngle(f);
		sheepEntityRenderState.sheared = sheepEntity.isSheared();
		sheepEntityRenderState.color = sheepEntity.getColor();
		sheepEntityRenderState.id = sheepEntity.getId();
	}
}
