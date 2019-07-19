package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public class ZombieEntityModel<T extends ZombieEntity> extends AbstractZombieModel<T> {
	public ZombieEntityModel() {
		this(0.0F, false);
	}

	public ZombieEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	protected ZombieEntityModel(float scale, float f, int textureWidth, int i) {
		super(scale, f, textureWidth, i);
	}

	public boolean method_17790(T zombieEntity) {
		return zombieEntity.isAttacking();
	}
}
