package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.RavagerEntityModel;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RavagerEntityRenderer extends MobEntityRenderer<RavagerEntity, RavagerEntityModel> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/ravager.png");

	public RavagerEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new RavagerEntityModel(arg.method_32167(EntityModelLayers.RAVAGER)), 1.1F);
	}

	public Identifier getTexture(RavagerEntity ravagerEntity) {
		return TEXTURE;
	}
}
