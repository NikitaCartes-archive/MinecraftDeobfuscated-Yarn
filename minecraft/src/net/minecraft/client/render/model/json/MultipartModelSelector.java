package net.minecraft.client.render.model.json;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface MultipartModelSelector {
	MultipartModelSelector TRUE = stateManager -> blockState -> true;
	MultipartModelSelector FALSE = stateManager -> blockState -> false;

	Predicate<BlockState> getPredicate(StateManager<Block, BlockState> stateManager);
}
