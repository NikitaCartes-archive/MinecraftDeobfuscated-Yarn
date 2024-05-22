package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.function.Function;
import java.util.function.IntFunction;

public class EntityVariantTypeFix extends ChoiceFix {
	private final String variantKey;
	private final IntFunction<String> variantIntToId;

	public EntityVariantTypeFix(Schema outputSchema, String name, TypeReference type, String entityId, String variantKey, IntFunction<String> variantIntToId) {
		super(outputSchema, false, name, type, entityId);
		this.variantKey = variantKey;
		this.variantIntToId = variantIntToId;
	}

	private static <T> Dynamic<T> updateEntity(
		Dynamic<T> entityDynamic, String oldVariantKey, String newVariantKey, Function<Dynamic<T>, Dynamic<T>> variantIntToId
	) {
		return entityDynamic.map(
			object -> {
				DynamicOps<T> dynamicOps = entityDynamic.getOps();
				Function<T, T> function2 = objectx -> ((Dynamic)variantIntToId.apply(new Dynamic<>(dynamicOps, (T)objectx))).getValue();
				return dynamicOps.get((T)object, oldVariantKey)
					.map(object2 -> dynamicOps.set((T)object, newVariantKey, (T)function2.apply(object2)))
					.result()
					.orElse(object);
			}
		);
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(
			DSL.remainderFinder(),
			entityDynamic -> updateEntity(
					entityDynamic,
					this.variantKey,
					"variant",
					variantDynamic -> DataFixUtils.orElse(
							variantDynamic.asNumber().map(variantInt -> variantDynamic.createString((String)this.variantIntToId.apply(variantInt.intValue()))).result(),
							variantDynamic
						)
				)
		);
	}
}
