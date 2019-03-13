package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class class_4107 extends class_4097<LivingEntity> {
	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_18449, class_4141.field_18456), Pair.of(class_4140.field_18450, class_4141.field_18456));
	}

	@Override
	protected void method_18920(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		class_4095<?> lv = livingEntity.method_18868();
		Path path = (Path)lv.method_18904(class_4140.field_18449).get();
		List<class_4208> list = (List<class_4208>)lv.method_18904(class_4140.field_18450).get();
		List<BlockPos> list2 = (List<BlockPos>)Arrays.stream(path.method_19314())
			.map(pathNode -> new BlockPos(pathNode.x, pathNode.y, pathNode.z))
			.collect(Collectors.toList());
		Set<BlockPos> set = (Set<BlockPos>)list.stream()
			.filter(arg -> arg.method_19442() == serverWorld.method_8597().method_12460())
			.map(class_4208::method_19446)
			.filter(list2::contains)
			.collect(Collectors.toSet());
		int i = path.getCurrentNodeIndex() - 1;
		set.forEach(blockPos -> {
			int j = list2.indexOf(blockPos);
			BlockState blockState = serverWorld.method_8320(blockPos);
			((DoorBlock)blockState.getBlock()).method_10033(serverWorld, blockPos, j >= i);
		});
	}
}
