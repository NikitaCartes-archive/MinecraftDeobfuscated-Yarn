package net.minecraft.state.property;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a property that has boolean values.
 * 
 * <p>See {@link net.minecraft.state.property.Properties} for example
 * usages.
 */
public class BooleanProperty extends Property<Boolean> {
	private final ImmutableList<Boolean> values = ImmutableList.of(true, false);

	protected BooleanProperty(String name) {
		super(name, Boolean.class);
	}

	@Override
	public List<Boolean> getValues() {
		return this.values;
	}

	/**
	 * Creates a boolean property.
	 * 
	 * @param name the name of the property; see {@linkplain Property#name the note on the
	 * name}
	 */
	public static BooleanProperty of(String name) {
		return new BooleanProperty(name);
	}

	@Override
	public Optional<Boolean> parse(String name) {
		return !"true".equals(name) && !"false".equals(name) ? Optional.empty() : Optional.of(Boolean.valueOf(name));
	}

	public String name(Boolean boolean_) {
		return boolean_.toString();
	}

	public int ordinal(Boolean boolean_) {
		return boolean_ ? 0 : 1;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof BooleanProperty booleanProperty && super.equals(object)) {
				return this.values.equals(booleanProperty.values);
			}

			return false;
		}
	}

	@Override
	public int computeHashCode() {
		return 31 * super.computeHashCode() + this.values.hashCode();
	}
}
