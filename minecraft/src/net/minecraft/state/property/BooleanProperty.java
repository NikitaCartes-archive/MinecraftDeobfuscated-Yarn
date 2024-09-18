package net.minecraft.state.property;

import java.util.List;
import java.util.Optional;

/**
 * Represents a property that has boolean values.
 * 
 * <p>See {@link net.minecraft.state.property.Properties} for example
 * usages.
 */
public final class BooleanProperty extends Property<Boolean> {
	private static final List<Boolean> values = List.of(true, false);
	private static final int TRUE_ORDINAL = 0;
	private static final int FALSE_ORDINAL = 1;

	private BooleanProperty(String name) {
		super(name, Boolean.class);
	}

	@Override
	public List<Boolean> getValues() {
		return values;
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
		return switch (name) {
			case "true" -> Optional.of(true);
			case "false" -> Optional.of(false);
			default -> Optional.empty();
		};
	}

	public String name(Boolean boolean_) {
		return boolean_.toString();
	}

	public int ordinal(Boolean boolean_) {
		return boolean_ ? 0 : 1;
	}
}
