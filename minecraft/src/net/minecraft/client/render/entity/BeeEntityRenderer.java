package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BeeEntityRenderer extends MobEntityRenderer<BeeEntity, BeeEntityModel<BeeEntity>> {
	private static final Identifier ANGRY_TEXTURE = new Identifier("textures/entity/bee/bee_angry.png");
	private static final Identifier ANGRY_NECTAR_TEXTURE = new Identifier("textures/entity/bee/bee_angry_nectar.png");
	private static final Identifier PASSIVE_TEXTURE = new Identifier("textures/entity/bee/bee.png");
	private static final Identifier NECTAR_TEXTURE = new Identifier("textures/entity/bee/bee_nectar.png");

	public BeeEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new BeeEntityModel<>(arg.method_32167(EntityModelLayers.BEE)), 0.4F);
	}

	public Identifier getTexture(BeeEntity beeEntity) {
		if (beeEntity.hasAngerTime()) {
			return beeEntity.hasNectar() ? ANGRY_NECTAR_TEXTURE : ANGRY_TEXTURE;
		} else {
			return beeEntity.hasNectar() ? NECTAR_TEXTURE : PASSIVE_TEXTURE;
		}
	}
}
