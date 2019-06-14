package net.minecraft.predicate.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;

public class BlockStatePredicate implements Predicate<BlockState> {
	public static final Predicate<BlockState> ANY = blockState -> true;
	private final StateFactory<Block, BlockState> factory;
	private final Map<Property<?>, Predicate<Object>> propertyTests = Maps.<Property<?>, Predicate<Object>>newHashMap();

	private BlockStatePredicate(StateFactory<Block, BlockState> stateFactory) {
		this.factory = stateFactory;
	}

	public static BlockStatePredicate forBlock(Block block) {
		return new BlockStatePredicate(block.method_9595());
	}

	public boolean method_11760(@Nullable BlockState blockState) {
		if (blockState != null && blockState.getBlock().equals(this.factory.getBaseObject())) {
			if (this.propertyTests.isEmpty()) {
				return true;
			} else {
				for (Entry<Property<?>, Predicate<Object>> entry : this.propertyTests.entrySet()) {
					if (!this.method_11761(blockState, (Property)entry.getKey(), (Predicate<Object>)entry.getValue())) {
						return false;
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	protected <T extends Comparable<T>> boolean method_11761(BlockState blockState, Property<T> property, Predicate<Object> predicate) {
		T comparable = blockState.method_11654(property);
		return predicate.test(comparable);
	}

	public <V extends Comparable<V>> BlockStatePredicate method_11762(Property<V> property, Predicate<Object> predicate) {
		if (!this.factory.getProperties().contains(property)) {
			throw new IllegalArgumentException(this.factory + " cannot support property " + property);
		} else {
			this.propertyTests.put(property, predicate);
			return this;
		}
	}
}
