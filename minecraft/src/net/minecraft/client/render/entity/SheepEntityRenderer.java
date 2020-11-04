package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SheepEntityRenderer extends MobEntityRenderer<SheepEntity, SheepEntityModel<SheepEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/sheep/sheep.png");

	public SheepEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new SheepEntityModel<>(arg.method_32167(EntityModelLayers.SHEEP)), 0.7F);
		this.addFeature(new SheepWoolFeatureRenderer(this, arg.method_32170()));
	}

	public Identifier getTexture(SheepEntity sheepEntity) {
		return TEXTURE;
	}
}
