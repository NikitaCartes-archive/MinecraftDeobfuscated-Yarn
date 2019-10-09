package net.minecraft.client.render.entity.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZombieEntityModel<T extends ZombieEntity> extends AbstractZombieModel<T> {
	public ZombieEntityModel(Function<Identifier, RenderLayer> function, float f, boolean bl) {
		this(function, f, 0.0F, 64, bl ? 32 : 64);
	}

	protected ZombieEntityModel(Function<Identifier, RenderLayer> function, float f, float g, int i, int j) {
		super(function, f, g, i, j);
	}

	public boolean method_17793(T zombieEntity) {
		return zombieEntity.isAttacking();
	}
}
