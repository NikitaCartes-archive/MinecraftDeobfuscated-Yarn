package net.minecraft.world.gen.random;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public interface RandomDeriver {
	default AbstractRandom createRandom(BlockPos pos) {
		return this.createRandom(pos.getX(), pos.getY(), pos.getZ());
	}

	default AbstractRandom createRandom(Identifier id) {
		return this.createRandom(id.toString());
	}

	AbstractRandom createRandom(String string);

	AbstractRandom createRandom(int x, int y, int z);

	@VisibleForTesting
	void addDebugInfo(StringBuilder info);
}
