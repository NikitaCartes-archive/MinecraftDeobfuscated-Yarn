package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BlazeEntityRenderer extends MobEntityRenderer<BlazeEntity, BlazeEntityModel<BlazeEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/blaze.png");

	public BlazeEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new BlazeEntityModel<>(), 0.5F);
	}

	protected Identifier getTexture(BlazeEntity blazeEntity) {
		return SKIN;
	}
}
