package net.minecraft.state.property;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.math.Direction;

public class DirectionProperty extends EnumProperty<Direction> {
	protected DirectionProperty(String string, Collection<Direction> collection) {
		super(string, Direction.class, collection);
	}

	public static DirectionProperty create(String string, Predicate<Direction> predicate) {
		return create(string, (Collection<Direction>)Arrays.stream(Direction.values()).filter(predicate).collect(Collectors.toList()));
	}

	public static DirectionProperty method_11845(String string, Direction... directions) {
		return create(string, Lists.<Direction>newArrayList(directions));
	}

	public static DirectionProperty create(String string, Collection<Direction> collection) {
		return new DirectionProperty(string, collection);
	}
}
