package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public interface BlockEntityProvider {
	@Nullable
	BlockEntity createBlockEntity(BlockView world);
}
