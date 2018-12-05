package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.IllagerBeastEntityModel;
import net.minecraft.entity.mob.IllagerBeastEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class IllagerBeastEntityRenderer extends EntityMobRenderer<IllagerBeastEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/beast.png");

	public IllagerBeastEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new IllagerBeastEntityModel(), 0.7F);
	}

	protected Identifier getTexture(IllagerBeastEntity illagerBeastEntity) {
		return TEXTURE;
	}
}
