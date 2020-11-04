package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DolphinEntityRenderer extends MobEntityRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/dolphin.png");

	public DolphinEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new DolphinEntityModel<>(arg.method_32167(EntityModelLayers.DOLPHIN)), 0.7F);
		this.addFeature(new DolphinHeldItemFeatureRenderer(this));
	}

	public Identifier getTexture(DolphinEntity dolphinEntity) {
		return TEXTURE;
	}
}
