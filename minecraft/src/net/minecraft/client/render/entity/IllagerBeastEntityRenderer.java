package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.IllagerBeastEntityModel;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class IllagerBeastEntityRenderer extends MobEntityRenderer<RavagerEntity, IllagerBeastEntityModel> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/ravager.png");

	public IllagerBeastEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new IllagerBeastEntityModel(), 1.1F);
	}

	protected Identifier method_3984(RavagerEntity ravagerEntity) {
		return TEXTURE;
	}
}
