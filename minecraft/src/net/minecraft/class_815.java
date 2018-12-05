package net.minecraft;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;

@Environment(EnvType.CLIENT)
public interface class_815 {
	class_815 TRUE = stateFactory -> blockState -> true;
	class_815 FALSE = stateFactory -> blockState -> false;

	Predicate<BlockState> getPredicate(StateFactory<Block, BlockState> stateFactory);
}
