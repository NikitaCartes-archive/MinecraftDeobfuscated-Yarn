package net.minecraft.world.gen.tree;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;

public class LeaveVineTreeDecorator extends TreeDecorator {
	public static final Codec<LeaveVineTreeDecorator> CODEC = Codec.unit((Supplier<LeaveVineTreeDecorator>)(() -> LeaveVineTreeDecorator.INSTANCE));
	public static final LeaveVineTreeDecorator INSTANCE = new LeaveVineTreeDecorator();

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.LEAVE_VINE;
	}

	@Override
	public void generate(StructureWorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
		leavesPositions.forEach(blockPos -> {
			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = blockPos.west();
				if (Feature.isAir(world, blockPos2)) {
					this.method_23467(world, blockPos2, VineBlock.EAST, set, box);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = blockPos.east();
				if (Feature.isAir(world, blockPos2)) {
					this.method_23467(world, blockPos2, VineBlock.WEST, set, box);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = blockPos.north();
				if (Feature.isAir(world, blockPos2)) {
					this.method_23467(world, blockPos2, VineBlock.SOUTH, set, box);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = blockPos.south();
				if (Feature.isAir(world, blockPos2)) {
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

		for (BlockPos var7 = blockPos.down(); Feature.isAir(modifiableTestableWorld, var7) && i > 0; i--) {
			this.placeVine(modifiableTestableWorld, var7, booleanProperty, set, blockBox);
			var7 = var7.down();
		}
	}
}
