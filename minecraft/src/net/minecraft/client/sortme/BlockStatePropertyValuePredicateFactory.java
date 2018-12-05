package net.minecraft.client.sortme;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_815;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;

@Environment(EnvType.CLIENT)
public class BlockStatePropertyValuePredicateFactory implements class_815 {
	private static final Splitter VALUE_SPLITTER = Splitter.on('|').omitEmptyStrings();
	private final String key;
	private final String valueString;

	public BlockStatePropertyValuePredicateFactory(String string, String string2) {
		this.key = string;
		this.valueString = string2;
	}

	@Override
	public Predicate<BlockState> getPredicate(StateFactory<Block, BlockState> stateFactory) {
		Property<?> property = stateFactory.getProperty(this.key);
		if (property == null) {
			throw new RuntimeException(String.format("Unknown property '%s' on '%s'", this.key, stateFactory.getBaseObject().toString()));
		} else {
			String string = this.valueString;
			boolean bl = !string.isEmpty() && string.charAt(0) == '!';
			if (bl) {
				string = string.substring(1);
			}

			List<String> list = VALUE_SPLITTER.splitToList(string);
			if (list.isEmpty()) {
				throw new RuntimeException(String.format("Empty value '%s' for property '%s' on '%s'", this.valueString, this.key, stateFactory.getBaseObject().toString()));
			} else {
				Predicate<BlockState> predicate;
				if (list.size() == 1) {
					predicate = this.method_3525(stateFactory, property, string);
				} else {
					List<Predicate<BlockState>> list2 = (List<Predicate<BlockState>>)list.stream()
						.map(stringx -> this.method_3525(stateFactory, property, stringx))
						.collect(Collectors.toList());
					predicate = blockState -> list2.stream().anyMatch(predicatex -> predicatex.test(blockState));
				}

				return bl ? predicate.negate() : predicate;
			}
		}
	}

	private Predicate<BlockState> method_3525(StateFactory<Block, BlockState> stateFactory, Property<?> property, String string) {
		Optional<?> optional = property.getValue(string);
		if (!optional.isPresent()) {
			throw new RuntimeException(
				String.format("Unknown value '%s' for property '%s' on '%s' in '%s'", string, this.key, stateFactory.getBaseObject().toString(), this.valueString)
			);
		} else {
			return blockState -> blockState.get(property).equals(optional.get());
		}
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.valueString).toString();
	}
}
