package net.minecraft.client.render.model.json;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface MultipartModelSelector {
	MultipartModelSelector TRUE = stateFactory -> blockState -> true;
	MultipartModelSelector FALSE = stateFactory -> blockState -> false;

	Predicate<BlockState> getPredicate(StateFactory<Block, BlockState> stateFactory);
}
