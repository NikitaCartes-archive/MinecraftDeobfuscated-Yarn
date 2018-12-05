package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BipedEntityRenderer<T extends MobEntity> extends EntityMobRenderer<T> {
	private static final Identifier SKIN = new Identifier("textures/entity/steve.png");

	public BipedEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, BipedEntityModel bipedEntityModel, float f) {
		super(entityRenderDispatcher, bipedEntityModel, f);
		this.addLayer(new HeadEntityRenderer(bipedEntityModel.head));
		this.addLayer(new ElytraEntityRenderer(this));
		this.addLayer(new HeldItemEntityRenderer(this));
	}

	protected Identifier getTexture(T mobEntity) {
		return SKIN;
	}
}
