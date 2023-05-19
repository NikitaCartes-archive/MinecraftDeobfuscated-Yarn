package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PointOfInterestRemoveFix extends PointOfInterestFix {
	private final Predicate<String> keepPredicate;

	public PointOfInterestRemoveFix(Schema outputSchema, String name, Predicate<String> removePredicate) {
		super(outputSchema, name);
		this.keepPredicate = removePredicate.negate();
	}

	@Override
	protected <T> Stream<Dynamic<T>> update(Stream<Dynamic<T>> dynamics) {
		return dynamics.filter(this::shouldKeepRecord);
	}

	private <T> boolean shouldKeepRecord(Dynamic<T> dynamic) {
		return dynamic.get("type").asString().result().filter(this.keepPredicate).isPresent();
	}
}
