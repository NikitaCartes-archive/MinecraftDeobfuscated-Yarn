package net.minecraft.world.chunk.light;

import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface LightSourceView extends BlockView {
	void forEachLightSource(BiConsumer<BlockPos, BlockState> callback);

	ChunkSkyLight getChunkSkyLight();
}
