package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BatEntityRenderer extends MobEntityRenderer<BatEntity, BatEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/bat.png");

	public BatEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new BatEntityModel(context.getPart(EntityModelLayers.BAT)), 0.25F);
	}

	public Identifier getTexture(BatEntity batEntity) {
		return TEXTURE;
	}
}
