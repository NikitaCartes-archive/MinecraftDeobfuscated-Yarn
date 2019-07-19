package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class FindPointOfInterestTask extends Task<MobEntityWithAi> {
	private final PointOfInterestType poiType;
	private final MemoryModuleType<GlobalPos> field_20287;
	private final boolean onlyRunIfChild;
	private long positionExpireTimeLimit;
	private final Long2LongMap field_19289 = new Long2LongOpenHashMap();
	private int field_19290;

	public FindPointOfInterestTask(PointOfInterestType poiType, MemoryModuleType<GlobalPos> targetMemoryModule, boolean onlyRunIfChild) {
		super(ImmutableMap.of(targetMemoryModule, MemoryModuleState.VALUE_ABSENT));
		this.poiType = poiType;
		this.field_20287 = targetMemoryModule;
		this.onlyRunIfChild = onlyRunIfChild;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		return this.onlyRunIfChild && mobEntityWithAi.isBaby() ? false : serverWorld.getTime() - this.positionExpireTimeLimit >= 20L;
	}

	protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		this.field_19290 = 0;
		this.positionExpireTimeLimit = serverWorld.getTime() + (long)serverWorld.getRandom().nextInt(20);
		PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
		Predicate<BlockPos> predicate = blockPosx -> {
			long lx = blockPosx.asLong();
			if (this.field_19289.containsKey(lx)) {
				return false;
			} else if (++this.field_19290 >= 5) {
				return false;
			} else {
				this.field_19289.put(lx, this.positionExpireTimeLimit + 40L);
				return true;
			}
		};
		Stream<BlockPos> stream = pointOfInterestStorage.method_21647(
			this.poiType.getCompletionCondition(), predicate, new BlockPos(mobEntityWithAi), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE
		);
		Path path = mobEntityWithAi.getNavigation().method_21643(stream, this.poiType.method_21648());
		if (path != null && path.method_21655()) {
			BlockPos blockPos = path.method_48();
			pointOfInterestStorage.getType(blockPos).ifPresent(pointOfInterestType -> {
				pointOfInterestStorage.getPosition(this.poiType.getCompletionCondition(), blockPos2 -> blockPos2.equals(blockPos), blockPos, 1);
				mobEntityWithAi.getBrain().putMemory(this.field_20287, GlobalPos.create(serverWorld.getDimension().getType(), blockPos));
				DebugInfoSender.sendPointOfInterest(serverWorld, blockPos);
			});
		} else if (this.field_19290 < 5) {
			this.field_19289.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.positionExpireTimeLimit);
		}
	}
}
