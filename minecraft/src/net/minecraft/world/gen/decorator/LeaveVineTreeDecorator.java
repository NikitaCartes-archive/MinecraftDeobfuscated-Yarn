package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class LeaveVineTreeDecorator extends TreeDecorator {
	public LeaveVineTreeDecorator() {
		super(TreeDecoratorType.LEAVE_VINE);
	}

	public <T> LeaveVineTreeDecorator(Dynamic<T> dynamic) {
		this();
	}

	@Override
	public void generate(IWorld world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
		leavesPositions.forEach(blockPos -> {
			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = blockPos.west();
				if (AbstractTreeFeature.isAir(world, blockPos2)) {
					this.method_23467(world, blockPos2, VineBlock.EAST, set, box);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = blockPos.east();
				if (AbstractTreeFeature.isAir(world, blockPos2)) {
					this.method_23467(world, blockPos2, VineBlock.WEST, set, box);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = blockPos.north();
				if (AbstractTreeFeature.isAir(world, blockPos2)) {
					this.method_23467(world, blockPos2, VineBlock.SOUTH, set, box);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = blockPos.south();
				if (AbstractTreeFeature.isAir(world, blockPos2)) {
					this.method_23467(world, blockPos2, VineBlock.NORTH, set, box);
				}
			}
		});
	}

	private void method_23467(
		ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BooleanProperty booleanProperty, Set<BlockPos> set, BlockBox blockBox
	) {
		this.placeVine(modifiableTestableWorld, blockPos, booleanProperty, set, blockBox);
		int i = 4;

		for (BlockPos var7 = blockPos.down(); AbstractTreeFeature.isAir(modifiableTestableWorld, var7) && i > 0; i--) {
			this.placeVine(modifiableTestableWorld, var7, booleanProperty, set, blockBox);
			var7 = var7.down();
		}
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
				ops, ops.createMap(ImmutableMap.of(ops.createString("type"), ops.createString(Registry.TREE_DECORATOR_TYPE.getId(this.type).toString())))
			)
			.getValue();
	}

	public static LeaveVineTreeDecorator method_26666(Random random) {
		return new LeaveVineTreeDecorator();
	}
}
