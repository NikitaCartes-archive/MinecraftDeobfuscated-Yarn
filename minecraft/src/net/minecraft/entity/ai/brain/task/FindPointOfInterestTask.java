package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.DebugRendererInfoManager;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;

public class FindPointOfInterestTask extends Task<LivingEntity> {
	private final Predicate<PointOfInterestType> pointOfInterestType;
	private final MemoryModuleType<GlobalPos> memoryType;
	private final boolean field_18854;
	private long lastRunTime;

	public FindPointOfInterestTask(PointOfInterestType pointOfInterestType, MemoryModuleType<GlobalPos> memoryModuleType, boolean bl) {
		this.pointOfInterestType = pointOfInterestType.getCompletedCondition();
		this.memoryType = memoryModuleType;
		this.field_18854 = bl;
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(Pair.of(this.memoryType, MemoryModuleState.field_18457));
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		return this.field_18854 && livingEntity.isChild() ? false : serverWorld.getTime() - this.lastRunTime >= 10L;
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		this.lastRunTime = serverWorld.getTime();
		MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)livingEntity;
		PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
		Predicate<BlockPos> predicate = blockPos -> {
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
			if (serverWorld.getBlockState(blockPos.down()).isAir()) {
				mutable.setOffset(Direction.DOWN);
			}

			while (serverWorld.getBlockState(mutable).isAir()) {
				mutable.setOffset(Direction.DOWN);
			}

			Path path = mobEntityWithAi.getNavigation().findPathTo(mutable.toImmutable());
			return path != null && path.method_19315();
		};
		pointOfInterestStorage.getNearestPosition(this.pointOfInterestType, predicate, new BlockPos(livingEntity), 48).ifPresent(blockPos -> {
			livingEntity.getBrain().putMemory(this.memoryType, GlobalPos.create(serverWorld.getDimension().getType(), blockPos));
			DebugRendererInfoManager.method_19778(serverWorld, blockPos);
		});
	}
}
