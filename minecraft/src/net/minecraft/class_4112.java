package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class class_4112 extends class_4097<MobEntity> {
	@Nullable
	private Path field_18369;
	@Nullable
	private BlockPos field_18370;
	private float field_18371;

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18449, class_4141.field_18457), Pair.of(class_4140.field_18445, class_4141.field_18456));
	}

	protected boolean method_18978(ServerWorld serverWorld, MobEntity mobEntity) {
		class_4095<?> lv = mobEntity.method_18868();
		class_4142 lv2 = (class_4142)lv.method_18904(class_4140.field_18445).get();
		if (!this.method_18980(mobEntity, lv2) && this.method_18977(mobEntity, lv2)) {
			this.field_18370 = lv2.method_19094().method_18989();
			return true;
		} else {
			lv.method_18875(class_4140.field_18445);
			return false;
		}
	}

	protected boolean method_18979(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (this.field_18369 != null && this.field_18370 != null) {
			Optional<class_4142> optional = mobEntity.method_18868().method_18904(class_4140.field_18445);
			EntityNavigation entityNavigation = mobEntity.method_5942();
			return !entityNavigation.isIdle() && optional.isPresent() && !this.method_18980(mobEntity, (class_4142)optional.get());
		} else {
			return false;
		}
	}

	protected void method_18981(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.method_5942().stop();
		mobEntity.method_18868().method_18875(class_4140.field_18445);
		mobEntity.method_18868().method_18875(class_4140.field_18449);
		this.field_18369 = null;
	}

	protected void method_18982(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.method_18868().method_18878(class_4140.field_18449, this.field_18369);
		mobEntity.method_5942().method_6334(this.field_18369, (double)this.field_18371);
	}

	protected void method_18983(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		Path path = mobEntity.method_5942().method_6345();
		if (this.field_18369 != path) {
			this.field_18369 = path;
			mobEntity.method_18868().method_18878(class_4140.field_18449, path);
		}

		if (path != null && this.field_18370 != null) {
			class_4142 lv = (class_4142)mobEntity.method_18868().method_18904(class_4140.field_18445).get();
			if (lv.method_19094().method_18989().squaredDistanceTo(this.field_18370) > 4.0 && this.method_18977(mobEntity, lv)) {
				this.field_18370 = lv.method_19094().method_18989();
				this.method_18982(serverWorld, mobEntity, l);
			}
		}
	}

	private boolean method_18977(MobEntity mobEntity, class_4142 arg) {
		BlockPos blockPos = arg.method_19094().method_18989();
		this.field_18369 = mobEntity.method_5942().method_6348(blockPos);
		this.field_18371 = arg.method_19095();
		if (!this.method_18980(mobEntity, arg)) {
			if (this.field_18369 != null) {
				return true;
			}

			Vec3d vec3d = class_1414.method_6373((MobEntityWithAi)mobEntity, 10, 7, new Vec3d(blockPos));
			if (vec3d != null) {
				this.field_18369 = mobEntity.method_5942().method_6352(vec3d.x, vec3d.y, vec3d.z);
				return this.field_18369 != null;
			}
		}

		return false;
	}

	private boolean method_18980(MobEntity mobEntity, class_4142 arg) {
		return arg.method_19094().method_18989().method_19455(new BlockPos(mobEntity)) <= arg.method_19096();
	}
}
