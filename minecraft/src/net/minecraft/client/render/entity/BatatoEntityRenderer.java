package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BatatoEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.BatatoEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BatatoEntityRenderer extends MobEntityRenderer<BatatoEntity, BatatoEntityModel> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/batato.png");

	public BatatoEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new BatatoEntityModel(context.getPart(EntityModelLayers.BATATO)), 0.25F);
	}

	public Identifier getTexture(BatatoEntity batatoEntity) {
		return TEXTURE;
	}
}
