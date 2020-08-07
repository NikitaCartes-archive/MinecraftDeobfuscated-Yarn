package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class ColumnPlacer extends BlockPlacer {
	public static final Codec<ColumnPlacer> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("min_size").forGetter(columnPlacer -> columnPlacer.minSize),
					Codec.INT.fieldOf("extra_size").forGetter(columnPlacer -> columnPlacer.extraSize)
				)
				.apply(instance, ColumnPlacer::new)
	);
	private final int minSize;
	private final int extraSize;

	public ColumnPlacer(int i, int j) {
		this.minSize = i;
		this.extraSize = j;
	}

	@Override
	protected BlockPlacerType<?> method_28673() {
		return BlockPlacerType.field_21225;
	}

	@Override
	public void method_23403(WorldAccess worldAccess, BlockPos blockPos, BlockState blockState, Random random) {
		BlockPos.Mutable mutable = blockPos.mutableCopy();
		int i = this.minSize + random.nextInt(random.nextInt(this.extraSize + 1) + 1);

		for (int j = 0; j < i; j++) {
			worldAccess.setBlockState(mutable, blockState, 2);
			mutable.move(Direction.field_11036);
		}
	}
}
