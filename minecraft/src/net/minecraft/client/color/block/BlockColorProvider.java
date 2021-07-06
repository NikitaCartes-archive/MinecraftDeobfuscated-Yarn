package net.minecraft.client.color.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

@Environment(EnvType.CLIENT)
public interface BlockColorProvider {
	/**
	 * {@return the color of the block state for the specified tint index,
	 * or -1 if not tinted}
	 */
	int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex);
}
