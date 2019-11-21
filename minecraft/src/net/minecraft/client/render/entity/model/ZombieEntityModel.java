package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public class ZombieEntityModel<T extends ZombieEntity> extends AbstractZombieModel<T> {
	public ZombieEntityModel(float f, boolean bl) {
		this(f, 0.0F, 64, bl ? 32 : 64);
	}

	protected ZombieEntityModel(float f, float g, int i, int j) {
		super(f, g, i, j);
	}

	public boolean isAttacking(T zombieEntity) {
		return zombieEntity.isAttacking();
	}
}
