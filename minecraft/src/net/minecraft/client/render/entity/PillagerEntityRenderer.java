package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PillagerEntityRenderer extends IllagerEntityRenderer<PillagerEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/pillager.png");

	public PillagerEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new IllagerEntityModel<>(arg.method_32167(EntityModelLayers.PILLAGER)), 0.5F);
		this.addFeature(new HeldItemFeatureRenderer<>(this));
	}

	public Identifier getTexture(PillagerEntity pillagerEntity) {
		return TEXTURE;
	}
}
