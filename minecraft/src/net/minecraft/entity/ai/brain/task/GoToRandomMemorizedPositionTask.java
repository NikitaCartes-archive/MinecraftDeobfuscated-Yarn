package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;

public class GoToRandomMemorizedPositionTask extends Task<VillagerEntity> {
	private final MemoryModuleType<List<GlobalPos>> field_18866;
	private final MemoryModuleType<GlobalPos> field_18867;
	private final float field_18868;
	private final int field_18869;
	private final int field_18870;
	private long nextRunTime;
	@Nullable
	private GlobalPos field_18872;

	public GoToRandomMemorizedPositionTask(
		MemoryModuleType<List<GlobalPos>> memoryModuleType, float f, int i, int j, MemoryModuleType<GlobalPos> memoryModuleType2
	) {
		this.field_18866 = memoryModuleType;
		this.field_18868 = f;
		this.field_18869 = i;
		this.field_18870 = j;
		this.field_18867 = memoryModuleType2;
	}

	protected boolean method_19609(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		Optional<List<GlobalPos>> optional = villagerEntity.getBrain().getMemory(this.field_18866);
		Optional<GlobalPos> optional2 = villagerEntity.getBrain().getMemory(this.field_18867);
		if (optional.isPresent() && optional2.isPresent()) {
			List<GlobalPos> list = (List<GlobalPos>)optional.get();
			if (!list.isEmpty()) {
				this.field_18872 = (GlobalPos)list.get(serverWorld.getRandom().nextInt(list.size()));
				return this.field_18872 != null
					&& Objects.equals(serverWorld.getDimension().getType(), this.field_18872.getDimension())
					&& ((GlobalPos)optional2.get()).getPos().method_19769(villagerEntity.getPos(), (double)this.field_18870);
			}
		}

		return false;
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18445, MemoryModuleState.field_18458),
			Pair.of(this.field_18866, MemoryModuleState.field_18456),
			Pair.of(this.field_18867, MemoryModuleState.field_18456)
		);
	}

	protected void method_19610(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (l > this.nextRunTime && this.field_18872 != null) {
			villagerEntity.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(this.field_18872.getPos(), this.field_18868, this.field_18869));
			this.nextRunTime = l + 40L;
		}
	}
}
