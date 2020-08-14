package net.minecraft.predicate.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public class BlockStatePredicate implements Predicate<BlockState> {
	public static final Predicate<BlockState> ANY = blockState -> true;
	private final StateManager<Block, BlockState> manager;
	private final Map<Property<?>, Predicate<Object>> propertyTests = Maps.<Property<?>, Predicate<Object>>newHashMap();

	private BlockStatePredicate(StateManager<Block, BlockState> manager) {
		this.manager = manager;
	}

	public static BlockStatePredicate forBlock(Block block) {
		return new BlockStatePredicate(block.getStateManager());
	}

	public boolean test(@Nullable BlockState blockState) {
		if (blockState != null && blockState.getBlock().equals(this.manager.getOwner())) {
			if (this.propertyTests.isEmpty()) {
				return true;
			} else {
				for (Entry<Property<?>, Predicate<Object>> entry : this.propertyTests.entrySet()) {
					if (!this.testProperty(blockState, (Property)entry.getKey(), (Predicate<Object>)entry.getValue())) {
						return false;
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	protected <T extends Comparable<T>> boolean testProperty(BlockState blockState, Property<T> property, Predicate<Object> predicate) {
		T comparable = blockState.get(property);
		return predicate.test(comparable);
	}

	public <V extends Comparable<V>> BlockStatePredicate with(Property<V> property, Predicate<Object> predicate) {
		if (!this.manager.getProperties().contains(property)) {
			throw new IllegalArgumentException(this.manager + " cannot support property " + property);
		} else {
			this.propertyTests.put(property, predicate);
			return this;
		}
	}
}
