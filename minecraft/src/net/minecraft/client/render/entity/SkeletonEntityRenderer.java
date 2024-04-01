package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SkeletonEntityRenderer<T extends AbstractSkeletonEntity> extends BipedEntityRenderer<T, SkeletonEntityModel<T>> {
	public static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/skeleton.png");

	public SkeletonEntityRenderer(EntityRendererFactory.Context context) {
		this(context, EntityModelLayers.SKELETON, EntityModelLayers.SKELETON_INNER_ARMOR, EntityModelLayers.SKELETON_OUTER_ARMOR);
	}

	public SkeletonEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer legArmorLayer, EntityModelLayer bodyArmorLayer) {
		this(ctx, legArmorLayer, bodyArmorLayer, new SkeletonEntityModel<>(ctx.getPart(layer)));
	}

	public SkeletonEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer legArmorLayer, EntityModelLayer bodyArmorLayer, SkeletonEntityModel<T> model) {
		super(ctx, model, 0.5F);
		this.addFeature(
			new ArmorFeatureRenderer<>(
				this, new SkeletonEntityModel(ctx.getPart(legArmorLayer)), new SkeletonEntityModel(ctx.getPart(bodyArmorLayer)), ctx.getModelManager()
			)
		);
	}

	public Identifier getTexture(T abstractSkeletonEntity) {
		return TEXTURE;
	}

	protected boolean isShaking(T abstractSkeletonEntity) {
		return abstractSkeletonEntity.isShaking();
	}
}
