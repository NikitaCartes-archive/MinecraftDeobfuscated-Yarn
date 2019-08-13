package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class OpenDoorsTask extends Task<LivingEntity> {
	public OpenDoorsTask() {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_18449,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_18450,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_20312,
				MemoryModuleState.field_18458
			)
		);
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		Path path = (Path)brain.getOptionalMemory(MemoryModuleType.field_18449).get();
		List<GlobalPos> list = (List<GlobalPos>)brain.getOptionalMemory(MemoryModuleType.field_18450).get();
		List<BlockPos> list2 = (List<BlockPos>)path.getNodes()
			.stream()
			.map(pathNode -> new BlockPos(pathNode.x, pathNode.y, pathNode.z))
			.collect(Collectors.toList());
		Set<BlockPos> set = this.getDoorsOnPath(serverWorld, list, list2);
		int i = path.getCurrentNodeIndex() - 1;
		this.method_21698(serverWorld, list2, set, i, livingEntity, brain);
	}

	private Set<BlockPos> getDoorsOnPath(ServerWorld serverWorld, List<GlobalPos> list, List<BlockPos> list2) {
		return (Set<BlockPos>)list.stream()
			.filter(globalPos -> globalPos.getDimension() == serverWorld.getDimension().getType())
			.map(GlobalPos::getPos)
			.filter(list2::contains)
			.collect(Collectors.toSet());
	}

	private void method_21698(ServerWorld serverWorld, List<BlockPos> list, Set<BlockPos> set, int i, LivingEntity livingEntity, Brain<?> brain) {
		set.forEach(blockPos -> {
			int j = list.indexOf(blockPos);
			BlockState blockState = serverWorld.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (BlockTags.field_15494.contains(block) && block instanceof DoorBlock) {
				boolean bl = j >= i;
				((DoorBlock)block).setOpen(serverWorld, blockPos, bl);
				GlobalPos globalPos = GlobalPos.create(serverWorld.getDimension().getType(), blockPos);
				if (!brain.getOptionalMemory(MemoryModuleType.field_20312).isPresent() && bl) {
					brain.putMemory(MemoryModuleType.field_20312, Sets.<GlobalPos>newHashSet(globalPos));
				} else {
					brain.getOptionalMemory(MemoryModuleType.field_20312).ifPresent(setx -> {
						if (bl) {
							setx.add(globalPos);
						} else {
							setx.remove(globalPos);
						}
					});
				}
			}
		});
		method_21697(serverWorld, list, i, livingEntity, brain);
	}

	public static void method_21697(ServerWorld serverWorld, List<BlockPos> list, int i, LivingEntity livingEntity, Brain<?> brain) {
		brain.getOptionalMemory(MemoryModuleType.field_20312).ifPresent(set -> {
			Iterator<GlobalPos> iterator = set.iterator();

			while (iterator.hasNext()) {
				GlobalPos globalPos = (GlobalPos)iterator.next();
				BlockPos blockPos = globalPos.getPos();
				int j = list.indexOf(blockPos);
				if (serverWorld.getDimension().getType() != globalPos.getDimension()) {
					iterator.remove();
				} else {
					BlockState blockState = serverWorld.getBlockState(blockPos);
					Block block = blockState.getBlock();
					if (BlockTags.field_15494.contains(block) && block instanceof DoorBlock && j < i && blockPos.isWithinDistance(livingEntity.getPos(), 4.0)) {
						((DoorBlock)block).setOpen(serverWorld, blockPos, false);
						iterator.remove();
					}
				}
			}
		});
	}
}
