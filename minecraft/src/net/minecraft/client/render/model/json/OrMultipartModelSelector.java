package net.minecraft.client.render.model.json;

import com.google.common.collect.Streams;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class OrMultipartModelSelector implements MultipartModelSelector {
	public static final String KEY = "OR";
	private final Iterable<? extends MultipartModelSelector> selectors;

	public OrMultipartModelSelector(Iterable<? extends MultipartModelSelector> selectors) {
		this.selectors = selectors;
	}

	@Override
	public Predicate<BlockState> getPredicate(StateManager<Block, BlockState> stateManager) {
		return Util.anyOf(Streams.stream(this.selectors).map(selector -> selector.getPredicate(stateManager)).toList());
	}
}
