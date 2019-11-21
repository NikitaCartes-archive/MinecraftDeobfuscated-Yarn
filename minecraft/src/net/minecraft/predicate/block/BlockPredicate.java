package net.minecraft.predicate.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BlockPredicate implements Predicate<BlockState> {
	private final Block block;

	public BlockPredicate(Block block) {
		this.block = block;
	}

	public static BlockPredicate make(Block block) {
		return new BlockPredicate(block);
	}

	public boolean test(@Nullable BlockState blockState) {
		return blockState != null && blockState.getBlock() == this.block;
	}
}
