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
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/skeleton.png");

	public SkeletonEntityRenderer(EntityRendererFactory.Context context) {
		this(context, EntityModelLayers.SKELETON, EntityModelLayers.SKELETON_INNER_ARMOR, EntityModelLayers.SKELETON_OUTER_ARMOR);
	}

	public SkeletonEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer legArmorLayer, EntityModelLayer bodyArmorLayer) {
		this(ctx, legArmorLayer, bodyArmorLayer, new SkeletonEntityModel<>(ctx.getPart(layer)));
	}

	public SkeletonEntityRenderer(
		EntityRendererFactory.Context context, EntityModelLayer entityModelLayer, EntityModelLayer entityModelLayer2, SkeletonEntityModel<T> skeletonEntityModel
	) {
		super(context, skeletonEntityModel, 0.5F);
		this.addFeature(
			new ArmorFeatureRenderer<>(
				this, new SkeletonEntityModel(context.getPart(entityModelLayer)), new SkeletonEntityModel(context.getPart(entityModelLayer2)), context.getModelManager()
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
