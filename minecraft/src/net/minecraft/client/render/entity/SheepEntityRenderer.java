package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SheepEntityRenderer extends MobEntityRenderer<SheepEntity, SheepWoolEntityModel<SheepEntity>> {
	private static final Identifier field_4778 = new Identifier("textures/entity/sheep/sheep.png");

	public SheepEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SheepWoolEntityModel<>(), 0.7F);
		this.method_4046(new SheepWoolFeatureRenderer(this));
	}

	protected Identifier method_4106(SheepEntity sheepEntity) {
		return field_4778;
	}
}
