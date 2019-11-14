package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SkeletonEntityRenderer extends BipedEntityRenderer<AbstractSkeletonEntity, SkeletonEntityModel<AbstractSkeletonEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/skeleton/skeleton.png");

	public SkeletonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SkeletonEntityModel<>(), 0.5F);
		this.addFeature(new HeldItemFeatureRenderer<>(this));
		this.addFeature(new ArmorBipedFeatureRenderer<>(this, new SkeletonEntityModel(0.5F, true), new SkeletonEntityModel(1.0F, true)));
	}

	public Identifier method_4119(AbstractSkeletonEntity abstractSkeletonEntity) {
		return SKIN;
	}
}
