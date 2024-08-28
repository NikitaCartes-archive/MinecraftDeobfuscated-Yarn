package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CaveSpiderEntityRenderer extends SpiderEntityRenderer<CaveSpiderEntity> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/spider/cave_spider.png");

	public CaveSpiderEntityRenderer(EntityRendererFactory.Context context) {
		super(context, EntityModelLayers.CAVE_SPIDER);
		this.shadowRadius = 0.56F;
	}

	@Override
	public Identifier getTexture(LivingEntityRenderState state) {
		return TEXTURE;
	}
}
