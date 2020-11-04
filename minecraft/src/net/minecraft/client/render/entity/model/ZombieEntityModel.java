package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public class ZombieEntityModel<T extends ZombieEntity> extends AbstractZombieModel<T> {
	public ZombieEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public boolean isAttacking(T zombieEntity) {
		return zombieEntity.isAttacking();
	}
}
