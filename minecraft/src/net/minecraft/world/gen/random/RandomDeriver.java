package net.minecraft.world.gen.random;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public interface RandomDeriver {
	default AbstractRandom createRandom(BlockPos pos) {
		return this.createRandom(pos.getX(), pos.getY(), pos.getZ());
	}

	default AbstractRandom createRandom(Identifier id) {
		return this.createRandom(id.toString());
	}

	AbstractRandom createRandom(int x, int y, int z);

	AbstractRandom createRandom(String string);
}
