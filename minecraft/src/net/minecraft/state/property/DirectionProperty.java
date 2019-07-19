package net.minecraft.state.property;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.math.Direction;

public class DirectionProperty extends EnumProperty<Direction> {
	protected DirectionProperty(String name, Collection<Direction> values) {
		super(name, Direction.class, values);
	}

	public static DirectionProperty of(String name, Predicate<Direction> filter) {
		return of(name, (Collection<Direction>)Arrays.stream(Direction.values()).filter(filter).collect(Collectors.toList()));
	}

	public static DirectionProperty of(String name, Direction... values) {
		return of(name, Lists.<Direction>newArrayList(values));
	}

	public static DirectionProperty of(String name, Collection<Direction> values) {
		return new DirectionProperty(name, values);
	}
}
