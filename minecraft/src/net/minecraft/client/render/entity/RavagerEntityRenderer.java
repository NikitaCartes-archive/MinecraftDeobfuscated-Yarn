package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.RavagerEntityModel;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RavagerEntityRenderer extends MobEntityRenderer<RavagerEntity, RavagerEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/ravager.png");

	public RavagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new RavagerEntityModel(context.getPart(EntityModelLayers.RAVAGER)), 1.1F);
	}

	public Identifier getTexture(RavagerEntity ravagerEntity) {
		return TEXTURE;
	}
}
