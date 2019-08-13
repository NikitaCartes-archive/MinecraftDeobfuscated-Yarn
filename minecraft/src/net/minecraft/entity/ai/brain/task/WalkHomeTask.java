package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;

public class WalkHomeTask extends Task<LivingEntity> {
	private final float field_20290;
	private final Long2LongMap field_20291 = new Long2LongOpenHashMap();
	private int field_20292;
	private long lastRunTime;

	public WalkHomeTask(float f) {
		super(ImmutableMap.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457, MemoryModuleType.field_18438, MemoryModuleState.field_18457));
		this.field_20290 = f;
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		if (serverWorld.getTime() - this.lastRunTime < 20L) {
			return false;
		} else {
			MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)livingEntity;
			PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
			Optional<BlockPos> optional = pointOfInterestStorage.getNearestPosition(
				PointOfInterestType.field_18517.getCompletionCondition(),
				blockPos -> true,
				new BlockPos(livingEntity),
				48,
				PointOfInterestStorage.OccupationStatus.field_18489
			);
			return optional.isPresent() && !(((BlockPos)optional.get()).getSquaredDistance(new Vec3i(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z)) <= 4.0);
		}
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		this.field_20292 = 0;
		this.lastRunTime = serverWorld.getTime() + (long)serverWorld.getRandom().nextInt(20);
		MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)livingEntity;
		PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
		Predicate<BlockPos> predicate = blockPosx -> {
			long lx = blockPosx.asLong();
			if (this.field_20291.containsKey(lx)) {
				return false;
			} else if (++this.field_20292 >= 5) {
				return false;
			} else {
				this.field_20291.put(lx, this.lastRunTime + 40L);
				return true;
			}
		};
		Stream<BlockPos> stream = pointOfInterestStorage.method_21647(
			PointOfInterestType.field_18517.getCompletionCondition(), predicate, new BlockPos(livingEntity), 48, PointOfInterestStorage.OccupationStatus.field_18489
		);
		Path path = mobEntityWithAi.getNavigation().method_21643(stream, PointOfInterestType.field_18517.method_21648());
		if (path != null && path.method_21655()) {
			BlockPos blockPos = path.method_48();
			Optional<PointOfInterestType> optional = pointOfInterestStorage.getType(blockPos);
			if (optional.isPresent()) {
				livingEntity.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(blockPos, this.field_20290, 1));
				DebugRendererInfoManager.sendPointOfInterest(serverWorld, blockPos);
			}
		} else if (this.field_20292 < 5) {
			this.field_20291.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.lastRunTime);
		}
	}
}
