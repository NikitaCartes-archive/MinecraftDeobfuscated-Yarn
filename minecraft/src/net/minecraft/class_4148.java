package net.minecraft;

import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public abstract class class_4148<E extends LivingEntity> {
	private final int field_18464 = 10;
	protected long field_18463 = 0L;

	public boolean method_19100(ServerWorld serverWorld, E livingEntity) {
		return serverWorld.getTime() - this.field_18463 >= 10L && this.method_19099().stream().anyMatch(livingEntity.method_18868()::method_18896);
	}

	public abstract void method_19101(ServerWorld serverWorld, E livingEntity);

	protected abstract Set<class_4140<?>> method_19099();
}
