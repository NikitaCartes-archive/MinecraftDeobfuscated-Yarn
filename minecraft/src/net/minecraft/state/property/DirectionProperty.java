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

	public static DirectionProperty of(String string, Predicate<Direction> predicate) {
		return of(string, (Collection<Direction>)Arrays.stream(Direction.values()).filter(predicate).collect(Collectors.toList()));
	}

	public static DirectionProperty of(String string, Direction... directions) {
		return of(string, Lists.<Direction>newArrayList(directions));
	}

	public static DirectionProperty of(String string, Collection<Direction> collection) {
		return new DirectionProperty(string, collection);
	}
}
