package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class class_4318 extends Task<VillagerEntity> {
	private List<ItemEntity> field_19425 = new ArrayList();

	public class_4318() {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
	}

	protected boolean method_20818(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		this.field_19425 = serverWorld.getEntities(ItemEntity.class, villagerEntity.getBoundingBox().expand(4.0, 2.0, 4.0));
		return !this.field_19425.isEmpty();
	}

	protected void method_20819(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		ItemEntity itemEntity = (ItemEntity)this.field_19425.get(serverWorld.random.nextInt(this.field_19425.size()));
		if (villagerEntity.method_20820(itemEntity.getStack().getItem())) {
			Vec3d vec3d = itemEntity.getPos();
			villagerEntity.getBrain().putMemory(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(new BlockPos(vec3d)));
			villagerEntity.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, 0.5F, 0));
		}
	}
}
