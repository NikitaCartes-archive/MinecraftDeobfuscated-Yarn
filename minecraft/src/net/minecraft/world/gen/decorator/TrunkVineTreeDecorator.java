package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class TrunkVineTreeDecorator extends TreeDecorator {
	public TrunkVineTreeDecorator() {
		super(TreeDecoratorType.TRUNK_VINE);
	}

	public <T> TrunkVineTreeDecorator(Dynamic<T> dynamic) {
		this();
	}

	@Override
	public void generate(IWorld iWorld, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox blockBox) {
		list.forEach(blockPos -> {
			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = blockPos.west();
				if (AbstractTreeFeature.isAir(iWorld, blockPos2)) {
					this.method_23471(iWorld, blockPos2, VineBlock.EAST, set, blockBox);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = blockPos.east();
				if (AbstractTreeFeature.isAir(iWorld, blockPos2)) {
					this.method_23471(iWorld, blockPos2, VineBlock.WEST, set, blockBox);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = blockPos.north();
				if (AbstractTreeFeature.isAir(iWorld, blockPos2)) {
					this.method_23471(iWorld, blockPos2, VineBlock.SOUTH, set, blockBox);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = blockPos.south();
				if (AbstractTreeFeature.isAir(iWorld, blockPos2)) {
					this.method_23471(iWorld, blockPos2, VineBlock.NORTH, set, blockBox);
				}
			}
		});
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
				ops, ops.createMap(ImmutableMap.of(ops.createString("type"), ops.createString(Registry.TREE_DECORATOR_TYPE.getId(this.field_21319).toString())))
			)
			.getValue();
	}
}
