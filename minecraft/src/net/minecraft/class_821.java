package net.minecraft;

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
public class class_821 implements class_815 {
	private final Iterable<? extends class_815> field_4337;

	public class_821(Iterable<? extends class_815> iterable) {
		this.field_4337 = iterable;
	}

	@Override
	public Predicate<BlockState> getPredicate(StateFactory<Block, BlockState> stateFactory) {
		List<Predicate<BlockState>> list = (List<Predicate<BlockState>>)Streams.stream(this.field_4337)
			.map(arg -> arg.getPredicate(stateFactory))
			.collect(Collectors.toList());
		return blockState -> list.stream().anyMatch(predicate -> predicate.test(blockState));
	}
}
