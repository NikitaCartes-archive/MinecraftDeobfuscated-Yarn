package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GoToSleepingChildTask extends Task<MobEntityWithAi> {
	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457),
			Pair.of(MemoryModuleType.field_18446, MemoryModuleState.field_18458),
			Pair.of(MemoryModuleType.field_18442, MemoryModuleState.field_18456)
		);
	}

	protected boolean method_19583(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		return serverWorld.getRandom().nextInt(10) == 0 && this.canSeeChild(mobEntityWithAi);
	}

	protected void method_19584(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		LivingEntity livingEntity = this.findVisibleChild(mobEntityWithAi);
		if (livingEntity != null) {
			this.method_19585(serverWorld, mobEntityWithAi, livingEntity);
		} else {
			Optional<LivingEntity> optional = this.method_19588(mobEntityWithAi);
			if (optional.isPresent()) {
				method_19580(mobEntityWithAi, (LivingEntity)optional.get());
			} else {
				this.method_19579(mobEntityWithAi).ifPresent(livingEntityx -> method_19580(mobEntityWithAi, livingEntityx));
			}
		}
	}

	private void method_19585(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, LivingEntity livingEntity) {
		for (int i = 0; i < 10; i++) {
			Vec3d vec3d = PathfindingUtil.findTargetStraight(mobEntityWithAi, 20, 8);
			if (vec3d != null && serverWorld.method_19500(new BlockPos(vec3d))) {
				mobEntityWithAi.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(vec3d, 0.6F, 0));
				return;
			}
		}
	}

	private static void method_19580(MobEntityWithAi mobEntityWithAi, LivingEntity livingEntity) {
		Brain<?> brain = mobEntityWithAi.getBrain();
		brain.putMemory(MemoryModuleType.field_18447, livingEntity);
		brain.putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(livingEntity));
		brain.putMemory(MemoryModuleType.field_18445, new WalkTarget(new EntityPosWrapper(livingEntity), 0.6F, 1));
	}

	private Optional<LivingEntity> method_19579(MobEntityWithAi mobEntityWithAi) {
		return this.getVisibleMobs(mobEntityWithAi).stream().filter(this::isSleepingChild).findAny();
	}

	private Optional<LivingEntity> method_19588(MobEntityWithAi mobEntityWithAi) {
		Map<LivingEntity, Integer> map = this.method_19592(mobEntityWithAi);
		return map.entrySet()
			.stream()
			.sorted(Comparator.comparingInt(Entry::getValue))
			.filter(entry -> (Integer)entry.getValue() > 0 && (Integer)entry.getValue() <= 5)
			.map(Entry::getKey)
			.findFirst();
	}

	private Map<LivingEntity, Integer> method_19592(MobEntityWithAi mobEntityWithAi) {
		Map<LivingEntity, Integer> map = Maps.<LivingEntity, Integer>newHashMap();
		this.getVisibleMobs(mobEntityWithAi).stream().filter(this::isSleepingChild).filter(this::hasInteractionTarget).forEach(livingEntity -> {
			Integer var10000 = (Integer)map.compute(this.method_19576(livingEntity), (livingEntityx, integer) -> integer == null ? 1 : integer + 1);
		});
		return map;
	}

	private List<LivingEntity> getVisibleMobs(MobEntityWithAi mobEntityWithAi) {
		return (List<LivingEntity>)mobEntityWithAi.getBrain().getMemory(MemoryModuleType.field_18442).get();
	}

	private LivingEntity method_19576(LivingEntity livingEntity) {
		return (LivingEntity)livingEntity.getBrain().getMemory(MemoryModuleType.field_18447).get();
	}

	@Nullable
	private LivingEntity findVisibleChild(LivingEntity livingEntity) {
		return (LivingEntity)((List)livingEntity.getBrain().getMemory(MemoryModuleType.field_18442).get())
			.stream()
			.filter(this::isSleepingChild)
			.filter(livingEntity2 -> this.method_19577(livingEntity, livingEntity2))
			.findAny()
			.orElse(null);
	}

	private boolean isSleepingChild(LivingEntity livingEntity) {
		return EntityType.VILLAGER.equals(livingEntity.getType()) && livingEntity.isChild() && livingEntity.isValid() && !livingEntity.isSleeping();
	}

	private boolean hasInteractionTarget(LivingEntity livingEntity) {
		return livingEntity.getBrain().getMemory(MemoryModuleType.field_18447).isPresent();
	}

	private boolean method_19577(LivingEntity livingEntity, LivingEntity livingEntity2) {
		return livingEntity2.getBrain().getMemory(MemoryModuleType.field_18447).filter(livingEntity2x -> livingEntity2x == livingEntity).isPresent();
	}

	private boolean canSeeChild(MobEntityWithAi mobEntityWithAi) {
		return this.getVisibleMobs(mobEntityWithAi).stream().anyMatch(this::isSleepingChild);
	}
}
