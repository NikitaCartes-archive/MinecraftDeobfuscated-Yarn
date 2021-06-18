package net.minecraft.world.gen.carver;

import net.minecraft.world.HeightLimitView;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CarverContext extends HeightContext {
	public CarverContext(ChunkGenerator chunkGenerator, HeightLimitView heightLimitView) {
		super(chunkGenerator, heightLimitView);
	}
}
