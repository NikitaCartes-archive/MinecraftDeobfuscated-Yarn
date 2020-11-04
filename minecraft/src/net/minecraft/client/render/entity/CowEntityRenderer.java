package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CowEntityRenderer extends MobEntityRenderer<CowEntity, CowEntityModel<CowEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/cow/cow.png");

	public CowEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new CowEntityModel<>(arg.method_32167(EntityModelLayers.COW)), 0.7F);
	}

	public Identifier getTexture(CowEntity cowEntity) {
		return TEXTURE;
	}
}
