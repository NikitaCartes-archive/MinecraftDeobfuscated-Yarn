package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class OpenDoorsTask extends Task<LivingEntity> {
	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18449, MemoryModuleState.field_18456), Pair.of(MemoryModuleType.field_18450, MemoryModuleState.field_18456)
		);
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		Path path = (Path)brain.getMemory(MemoryModuleType.field_18449).get();
		List<GlobalPos> list = (List<GlobalPos>)brain.getMemory(MemoryModuleType.field_18450).get();
		List<BlockPos> list2 = (List<BlockPos>)Arrays.stream(path.getNodes())
			.map(pathNode -> new BlockPos(pathNode.x, pathNode.y, pathNode.z))
			.collect(Collectors.toList());
		Set<BlockPos> set = (Set<BlockPos>)list.stream()
			.filter(globalPos -> globalPos.getDimension() == serverWorld.getDimension().getType())
			.map(GlobalPos::getPos)
			.filter(list2::contains)
			.collect(Collectors.toSet());
		int i = path.getCurrentNodeIndex() - 1;
		set.forEach(blockPos -> {
			int j = list2.indexOf(blockPos);
			BlockState blockState = serverWorld.getBlockState(blockPos);
			((DoorBlock)blockState.getBlock()).setOpen(serverWorld, blockPos, j >= i);
		});
	}
}
