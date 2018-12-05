package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpiderEntityRenderer<T extends SpiderEntity> extends EntityMobRenderer<T> {
	private static final Identifier SKIN = new Identifier("textures/entity/spider/spider.png");

	public SpiderEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SpiderEntityModel(), 1.0F);
		this.addLayer(new SpiderEyesEntityRenderer<>(this));
	}

	protected float method_4124(T spiderEntity) {
		return 180.0F;
	}

	protected Identifier getTexture(T spiderEntity) {
		return SKIN;
	}
}
