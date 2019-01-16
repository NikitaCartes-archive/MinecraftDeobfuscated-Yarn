package net.minecraft.client.render.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

@Environment(EnvType.CLIENT)
public interface BlockColorMapper {
	int getColor(BlockState blockState, @Nullable ExtendedBlockView extendedBlockView, @Nullable BlockPos blockPos, int i);
}
