package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ArmadilloEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ArmadilloEntityRenderer extends MobEntityRenderer<ArmadilloEntity, ArmadilloEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/armadillo.png");

	public ArmadilloEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new ArmadilloEntityModel(context.getPart(EntityModelLayers.ARMADILLO)), 0.4F);
	}

	public Identifier getTexture(ArmadilloEntity armadilloEntity) {
		return TEXTURE;
	}
}
