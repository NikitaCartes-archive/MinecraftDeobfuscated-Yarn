package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HoglinEntityModel;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZoglinEntityRenderer extends MobEntityRenderer<ZoglinEntity, HoglinEntityModel<ZoglinEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/hoglin/zoglin.png");

	public ZoglinEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new HoglinEntityModel<>(arg.method_32167(EntityModelLayers.ZOGLIN)), 0.7F);
	}

	public Identifier getTexture(ZoglinEntity zoglinEntity) {
		return TEXTURE;
	}
}
