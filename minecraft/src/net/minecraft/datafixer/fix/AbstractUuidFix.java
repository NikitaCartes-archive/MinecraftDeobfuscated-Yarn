package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractUuidFix extends DataFix {
	protected static final Logger LOGGER = LogManager.getLogger();
	protected TypeReference typeReference;

	public AbstractUuidFix(Schema outputSchema, TypeReference typeReference) {
		super(outputSchema, false);
		this.typeReference = typeReference;
	}

	protected Typed<?> updateTyped(Typed<?> typed, String name, Function<Dynamic<?>, Dynamic<?>> updater) {
		Type<?> type = this.getInputSchema().getChoiceType(this.typeReference, name);
		Type<?> type2 = this.getOutputSchema().getChoiceType(this.typeReference, name);
		return typed.updateTyped(DSL.namedChoice(name, type), type2, typedx -> typedx.update(DSL.remainderFinder(), updater));
	}

	protected static Optional<Dynamic<?>> updateStringUuid(Dynamic<?> parent, String oldKey, String newKey) {
		return createArrayFromStringUuid(parent, oldKey).map(dynamic2 -> parent.remove(oldKey).set(newKey, dynamic2));
	}

	protected static Optional<Dynamic<?>> updateCompoundUuid(Dynamic<?> parent, String oldKey, String newKey) {
		return parent.get(oldKey).get().flatMap(AbstractUuidFix::createArrayFromCompoundUuid).map(dynamic2 -> parent.remove(oldKey).set(newKey, dynamic2));
	}

	protected static Optional<Dynamic<?>> updateRegularMostLeast(Dynamic<?> parent, String oldKey, String newKey) {
		String string = oldKey + "Most";
		String string2 = oldKey + "Least";
		return createArrayFromMostLeastTags(parent, string, string2).map(dynamic2 -> parent.remove(string).remove(string2).set(newKey, dynamic2));
	}

	protected static Optional<Dynamic<?>> createArrayFromStringUuid(Dynamic<?> parent, String key) {
		return parent.get(key).get().flatMap(dynamic2 -> {
			String string = dynamic2.asString(null);
			if (string != null) {
				try {
					UUID uUID = UUID.fromString(string);
					return createArray(parent, uUID.getMostSignificantBits(), uUID.getLeastSignificantBits());
				} catch (IllegalArgumentException var4) {
				}
			}

			return Optional.empty();
		});
	}

	protected static Optional<Dynamic<?>> createArrayFromCompoundUuid(Dynamic<?> uuidCompound) {
		return createArrayFromMostLeastTags(uuidCompound, "M", "L");
	}

	protected static Optional<Dynamic<?>> createArrayFromMostLeastTags(Dynamic<?> parent, String mostBitsKey, String leastBitsKey) {
		long l = parent.get(mostBitsKey).asLong(0L);
		long m = parent.get(leastBitsKey).asLong(0L);
		return l != 0L && m != 0L ? createArray(parent, l, m) : Optional.empty();
	}

	protected static Optional<Dynamic<?>> createArray(Dynamic<?> parent, long mostBits, long leastBits) {
		return Optional.of(parent.createIntList(Arrays.stream(new int[]{(int)(mostBits >> 32), (int)mostBits, (int)(leastBits >> 32), (int)leastBits})));
	}
}
