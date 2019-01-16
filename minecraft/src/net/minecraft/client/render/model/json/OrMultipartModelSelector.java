package net.minecraft.client.render.model.json;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;

@Environment(EnvType.CLIENT)
public class OrMultipartModelSelector implements MultipartModelSelector {
	private final Iterable<? extends MultipartModelSelector> field_4337;

	public OrMultipartModelSelector(Iterable<? extends MultipartModelSelector> iterable) {
		this.field_4337 = iterable;
	}

	@Override
	public Predicate<BlockState> getPredicate(StateFactory<Block, BlockState> stateFactory) {
		List<Predicate<BlockState>> list = (List<Predicate<BlockState>>)Streams.stream(this.field_4337)
			.map(multipartModelSelector -> multipartModelSelector.getPredicate(stateFactory))
			.collect(Collectors.toList());
		return blockState -> list.stream().anyMatch(predicate -> predicate.test(blockState));
	}
}
