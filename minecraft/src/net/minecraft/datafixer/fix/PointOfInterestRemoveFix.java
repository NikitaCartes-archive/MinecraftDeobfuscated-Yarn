package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PointOfInterestRemoveFix extends PointOfInterestFix {
	private final Predicate<String> keepPredicate;

	public PointOfInterestRemoveFix(Schema schema, String name, Predicate<String> removePredicate) {
		super(schema, name);
		this.keepPredicate = removePredicate.negate();
	}

	@Override
	protected <T> Stream<Dynamic<T>> method_44186(Stream<Dynamic<T>> stream) {
		return stream.filter(this::shouldKeepRecord);
	}

	private <T> boolean shouldKeepRecord(Dynamic<T> dynamic) {
		return dynamic.get("type").asString().result().filter(this.keepPredicate).isPresent();
	}
}
