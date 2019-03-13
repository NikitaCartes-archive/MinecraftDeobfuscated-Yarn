package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BipedEntityRenderer<T extends MobEntity, M extends BipedEntityModel<T>> extends MobEntityRenderer<T, M> {
	private static final Identifier field_4713 = new Identifier("textures/entity/steve.png");

	public BipedEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M bipedEntityModel, float f) {
		super(entityRenderDispatcher, bipedEntityModel, f);
		this.method_4046(new HeadFeatureRenderer<>(this));
		this.method_4046(new ElytraFeatureRenderer<>(this));
		this.method_4046(new HeldItemFeatureRenderer<>(this));
	}

	protected Identifier method_3982(T mobEntity) {
		return field_4713;
	}
}
