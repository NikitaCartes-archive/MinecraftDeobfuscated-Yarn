/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public abstract class AbstractUuidFix
extends DataFix {
    protected DSL.TypeReference typeReference;

    public AbstractUuidFix(Schema outputSchema, DSL.TypeReference typeReference) {
        super(outputSchema, false);
        this.typeReference = typeReference;
    }

    protected Typed<?> updateTyped(Typed<?> typed2, String name, Function<Dynamic<?>, Dynamic<?>> updater) {
        Type<?> type = this.getInputSchema().getChoiceType(this.typeReference, name);
        Type<?> type2 = this.getOutputSchema().getChoiceType(this.typeReference, name);
        return typed2.updateTyped(DSL.namedChoice(name, type), type2, (Typed<?> typed) -> typed.update(DSL.remainderFinder(), updater));
    }

    protected static Optional<Dynamic<?>> updateStringUuid(Dynamic<?> dynamic, String oldKey, String newKey) {
        return AbstractUuidFix.createArrayFromStringUuid(dynamic, oldKey).map(dynamic2 -> dynamic.remove(oldKey).set(newKey, (Dynamic<?>)dynamic2));
    }

    protected static Optional<Dynamic<?>> updateCompoundUuid(Dynamic<?> dynamic, String oldKey, String newKey) {
        return dynamic.get(oldKey).result().flatMap(AbstractUuidFix::createArrayFromCompoundUuid).map(dynamic2 -> dynamic.remove(oldKey).set(newKey, (Dynamic<?>)dynamic2));
    }

    protected static Optional<Dynamic<?>> updateRegularMostLeast(Dynamic<?> dynamic, String oldKey, String newKey) {
        String string = oldKey + "Most";
        String string2 = oldKey + "Least";
        return AbstractUuidFix.createArrayFromMostLeastTags(dynamic, string, string2).map(dynamic2 -> dynamic.remove(string).remove(string2).set(newKey, (Dynamic<?>)dynamic2));
    }

    protected static Optional<Dynamic<?>> createArrayFromStringUuid(Dynamic<?> dynamic, String key) {
        return dynamic.get(key).result().flatMap(dynamic2 -> {
            String string = dynamic2.asString(null);
            if (string != null) {
                try {
                    UUID uUID = UUID.fromString(string);
                    return AbstractUuidFix.createArray(dynamic, uUID.getMostSignificantBits(), uUID.getLeastSignificantBits());
                } catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            return Optional.empty();
        });
    }

    protected static Optional<Dynamic<?>> createArrayFromCompoundUuid(Dynamic<?> dynamic) {
        return AbstractUuidFix.createArrayFromMostLeastTags(dynamic, "M", "L");
    }

    protected static Optional<Dynamic<?>> createArrayFromMostLeastTags(Dynamic<?> dynamic, String mostBitsKey, String leastBitsKey) {
        long l = dynamic.get(mostBitsKey).asLong(0L);
        long m = dynamic.get(leastBitsKey).asLong(0L);
        if (l == 0L || m == 0L) {
            return Optional.empty();
        }
        return AbstractUuidFix.createArray(dynamic, l, m);
    }

    protected static Optional<Dynamic<?>> createArray(Dynamic<?> dynamic, long mostBits, long leastBits) {
        return Optional.of(dynamic.createIntList(Arrays.stream(new int[]{(int)(mostBits >> 32), (int)mostBits, (int)(leastBits >> 32), (int)leastBits})));
    }
}

