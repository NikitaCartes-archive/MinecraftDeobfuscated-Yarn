package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.BeretFeatureRenderer;
import net.minecraft.client.render.entity.feature.MustacheFeatureRenderer;
import net.minecraft.client.render.entity.feature.TailFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.passive.RayTracingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RayTracingEntityRenderer extends LivingEntityRenderer<RayTracingEntity, PlayerEntityModel<RayTracingEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/player/wide/ray.png");

	public RayTracingEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER), false), 0.5F);
		this.addFeature(new BeretFeatureRenderer<>(this, context.getModelLoader()));
		this.addFeature(new MustacheFeatureRenderer<>(this, context.getModelLoader()));
		this.addFeature(new TailFeatureRenderer<>(this, context.getModelLoader()));
	}

	public Identifier getTexture(RayTracingEntity rayTracingEntity) {
		return TEXTURE;
	}
}
