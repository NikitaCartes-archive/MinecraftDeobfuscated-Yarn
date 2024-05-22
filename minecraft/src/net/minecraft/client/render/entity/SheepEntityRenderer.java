package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SheepEntityRenderer extends MobEntityRenderer<SheepEntity, SheepEntityModel<SheepEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/sheep/sheep.png");

	public SheepEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SheepEntityModel<>(context.getPart(EntityModelLayers.SHEEP)), 0.7F);
		this.addFeature(new SheepWoolFeatureRenderer(this, context.getModelLoader()));
	}

	public Identifier getTexture(SheepEntity sheepEntity) {
		return TEXTURE;
	}
}
