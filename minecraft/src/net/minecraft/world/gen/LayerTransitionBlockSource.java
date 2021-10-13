package net.minecraft.world.gen;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.RandomDeriver;
import org.apache.commons.lang3.Validate;

public class LayerTransitionBlockSource implements BlockSource {
	private final RandomDeriver randomDeriver;
	@Nullable
	private final BlockState belowState;
	@Nullable
	private final BlockState aboveState;
	private final int minY;
	private final int maxY;

	public LayerTransitionBlockSource(RandomDeriver randomDeriver, @Nullable BlockState belowState, @Nullable BlockState aboveState, int minY, int maxY) {
		this.randomDeriver = randomDeriver;
		this.belowState = belowState;
		this.aboveState = aboveState;
		this.minY = minY;
		this.maxY = maxY;
		Validate.isTrue(minY < maxY, "Below bounds (" + minY + ") need to be smaller than above bounds (" + maxY + ")");
	}

	@Nullable
	@Override
	public BlockState apply(ChunkNoiseSampler chunkNoiseSampler, int i, int j, int k) {
		if (j <= this.minY) {
			return this.belowState;
		} else if (j >= this.maxY) {
			return this.aboveState;
		} else {
			double d = MathHelper.lerpFromProgress((double)j, (double)this.minY, (double)this.maxY, 1.0, 0.0);
			AbstractRandom abstractRandom = this.randomDeriver.createRandom(i, j, k);
			return (double)abstractRandom.nextFloat() < d ? this.belowState : this.aboveState;
		}
	}
}
