package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HoglinEntityModel;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZoglinEntityRenderer extends MobEntityRenderer<ZoglinEntity, HoglinEntityModel<ZoglinEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/hoglin/zoglin.png");

	public ZoglinEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new HoglinEntityModel<>(context.getPart(EntityModelLayers.ZOGLIN)), 0.7F);
	}

	public Identifier getTexture(ZoglinEntity zoglinEntity) {
		return TEXTURE;
	}
}
