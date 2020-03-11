package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.HoglinEntityModel;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HoglinEntityRenderer extends MobEntityRenderer<HoglinEntity, HoglinEntityModel> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/hoglin/hoglin.png");

	public HoglinEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new HoglinEntityModel(), 0.7F);
	}

	public Identifier getTexture(HoglinEntity hoglinEntity) {
		return TEXTURE;
	}
}
