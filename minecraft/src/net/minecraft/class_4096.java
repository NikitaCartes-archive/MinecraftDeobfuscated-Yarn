package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class class_4096 extends class_4097<LivingEntity> {
	private final Predicate<class_4158> field_18330;
	private final class_4140<class_4208> field_18331;
	private long field_18332;

	public class_4096(class_4158 arg, class_4140<class_4208> arg2) {
		this.field_18330 = arg.method_19164();
		this.field_18331 = arg2;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(this.field_18331, class_4141.field_18457));
	}

	@Override
	protected boolean method_18919(ServerWorld serverWorld, LivingEntity livingEntity) {
		return serverWorld.getTime() - this.field_18332 >= 10L;
	}

	@Override
	protected void method_18920(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		this.field_18332 = serverWorld.getTime();
		MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)livingEntity;
		class_4153 lv = serverWorld.method_19494();
		Predicate<BlockPos> predicate = blockPos -> {
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
			if (serverWorld.method_8320(blockPos.down()).isAir()) {
				mutable.method_10098(Direction.DOWN);
			}

			while (serverWorld.method_8320(mutable).isAir()) {
				mutable.method_10098(Direction.DOWN);
			}

			Path path = mobEntityWithAi.method_5942().method_6348(mutable.toImmutable());
			return path != null && path.method_19315();
		};
		lv.method_19131(this.field_18330, predicate, new BlockPos(livingEntity), 48)
			.ifPresent(
				blockPos -> livingEntity.method_18868().method_18878(this.field_18331, class_4208.method_19443(serverWorld.method_8597().method_12460(), blockPos))
			);
	}
}
