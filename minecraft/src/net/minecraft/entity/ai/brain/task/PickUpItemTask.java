package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PickUpItemTask extends Task<VillagerEntity> {
	private List<ItemEntity> nearbyItems = new ArrayList();

	public PickUpItemTask() {
		super(ImmutableMap.of(MemoryModuleType.field_18446, MemoryModuleState.field_18457, MemoryModuleType.field_18445, MemoryModuleState.field_18457));
	}

	protected boolean method_20818(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		this.nearbyItems = serverWorld.getEntities(ItemEntity.class, villagerEntity.getBoundingBox().expand(4.0, 2.0, 4.0));
		return !this.nearbyItems.isEmpty();
	}

	protected void method_20819(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		ItemEntity itemEntity = (ItemEntity)this.nearbyItems.get(serverWorld.random.nextInt(this.nearbyItems.size()));
		if (villagerEntity.canGather(itemEntity.getStack().getItem())) {
			Vec3d vec3d = itemEntity.getPos();
			villagerEntity.getBrain().putMemory(MemoryModuleType.field_18446, new BlockPosLookTarget(new BlockPos(vec3d)));
			villagerEntity.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(vec3d, 0.5F, 0));
		}
	}
}
