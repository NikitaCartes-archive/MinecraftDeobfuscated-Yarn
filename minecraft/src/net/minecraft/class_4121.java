package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class class_4121 extends class_4097<MobEntityWithAi> {
	private final class_4140<? extends Entity> field_18380;
	private final float field_18381;

	public class_4121(class_4140<? extends Entity> arg, float f) {
		this.field_18380 = arg;
		this.field_18381 = f;
	}

	protected boolean method_19002(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		return mobEntityWithAi.squaredDistanceTo((Entity)mobEntityWithAi.method_18868().method_18904(this.field_18380).get()) < 16.0;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18445, class_4141.field_18457), Pair.of(this.field_18380, class_4141.field_18456));
	}

	protected void method_19003(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		Entity entity = (Entity)mobEntityWithAi.method_18868().method_18904(this.field_18380).get();

		for (int i = 0; i < 10; i++) {
			Vec3d vec3d = class_1414.method_6377(
				mobEntityWithAi,
				16,
				7,
				new Vec3d(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z).subtract(new Vec3d(entity.x, entity.y, entity.z)).normalize(),
				(float) (Math.PI / 10)
			);
			if (vec3d != null) {
				mobEntityWithAi.method_18868().method_18878(class_4140.field_18445, new class_4142(vec3d, this.field_18381, 0));
				return;
			}
		}
	}
}
