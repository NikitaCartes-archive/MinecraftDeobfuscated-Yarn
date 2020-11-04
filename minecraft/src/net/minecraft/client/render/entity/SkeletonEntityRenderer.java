package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SkeletonEntityRenderer extends BipedEntityRenderer<AbstractSkeletonEntity, SkeletonEntityModel<AbstractSkeletonEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/skeleton.png");

	public SkeletonEntityRenderer(class_5617.class_5618 arg) {
		this(arg, EntityModelLayers.SKELETON, EntityModelLayers.SKELETON_INNER_ARMOR, EntityModelLayers.SKELETON_OUTER_ARMOR);
	}

	public SkeletonEntityRenderer(
		class_5617.class_5618 arg, EntityModelLayer entityModelLayer, EntityModelLayer entityModelLayer2, EntityModelLayer entityModelLayer3
	) {
		super(arg, new SkeletonEntityModel<>(arg.method_32167(entityModelLayer)), 0.5F);
		this.addFeature(
			new ArmorFeatureRenderer<>(this, new SkeletonEntityModel(arg.method_32167(entityModelLayer2)), new SkeletonEntityModel(arg.method_32167(entityModelLayer3)))
		);
	}

	public Identifier getTexture(AbstractSkeletonEntity abstractSkeletonEntity) {
		return TEXTURE;
	}
}
