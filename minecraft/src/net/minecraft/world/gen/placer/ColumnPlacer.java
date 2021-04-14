package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.WorldAccess;

public class ColumnPlacer extends BlockPlacer {
	public static final Codec<ColumnPlacer> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(IntProvider.field_33450.fieldOf("size").forGetter(columnPlacer -> columnPlacer.field_33515)).apply(instance, ColumnPlacer::new)
	);
	private final IntProvider field_33515;

	public ColumnPlacer(IntProvider intProvider) {
		this.field_33515 = intProvider;
	}

	@Override
	protected BlockPlacerType<?> getType() {
		return BlockPlacerType.COLUMN_PLACER;
	}

	@Override
	public void generate(WorldAccess world, BlockPos pos, BlockState state, Random random) {
		BlockPos.Mutable mutable = pos.mutableCopy();
		int i = this.field_33515.get(random);

		for (int j = 0; j < i; j++) {
			world.setBlockState(mutable, state, Block.NOTIFY_LISTENERS);
			mutable.move(Direction.UP);
		}
	}
}
