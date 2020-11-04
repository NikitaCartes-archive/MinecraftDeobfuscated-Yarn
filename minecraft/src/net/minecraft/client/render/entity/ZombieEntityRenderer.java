package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public class ZombieEntityRenderer extends ZombieBaseEntityRenderer<ZombieEntity, ZombieEntityModel<ZombieEntity>> {
	public ZombieEntityRenderer(class_5617.class_5618 arg) {
		this(arg, EntityModelLayers.ZOMBIE, EntityModelLayers.ZOMBIE_INNER_ARMOR, EntityModelLayers.ZOMBIE_OUTER_ARMOR);
	}

	public ZombieEntityRenderer(
		class_5617.class_5618 arg, EntityModelLayer entityModelLayer, EntityModelLayer entityModelLayer2, EntityModelLayer entityModelLayer3
	) {
		super(
			arg,
			new ZombieEntityModel<>(arg.method_32167(entityModelLayer)),
			new ZombieEntityModel<>(arg.method_32167(entityModelLayer2)),
			new ZombieEntityModel<>(arg.method_32167(entityModelLayer3))
		);
	}
}
