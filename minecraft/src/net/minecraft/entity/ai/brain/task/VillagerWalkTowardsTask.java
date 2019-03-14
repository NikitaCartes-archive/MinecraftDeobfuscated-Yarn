package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class VillagerWalkTowardsTask extends Task<VillagerEntity> {
	private final MemoryModuleType<GlobalPos> field_18382;
	private final float field_18383;
	private final int field_18384;
	private final int field_18385;

	public VillagerWalkTowardsTask(MemoryModuleType<GlobalPos> memoryModuleType, float f, int i, int j) {
		this.field_18382 = memoryModuleType;
		this.field_18383 = f;
		this.field_18384 = i;
		this.field_18385 = j;
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(Pair.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457), Pair.of(this.field_18382, MemoryModuleState.field_18456));
	}

	protected void method_19509(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		MinecraftServer minecraftServer = serverWorld.getServer();
		Brain<?> brain = villagerEntity.getBrain();
		brain.getMemory(this.field_18382).ifPresent(globalPos -> {
			boolean bl;
			if (!Objects.equals(globalPos.getDimension(), serverWorld.getDimension().getType())) {
				bl = true;
			} else {
				int i = globalPos.getPos().method_19455(new BlockPos(villagerEntity));
				bl = i >= this.field_18385;
			}

			if (bl) {
				villagerEntity.releaseTicketFor(this.field_18382);
				brain.forget(this.field_18382);
			} else {
				brain.putMemory(MemoryModuleType.field_18445, new WalkTarget(globalPos.getPos(), this.field_18383, this.field_18384));
			}
		});
	}
}
