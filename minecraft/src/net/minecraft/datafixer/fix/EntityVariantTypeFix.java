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

	private static <T> Dynamic<T> method_43072(Dynamic<T> dynamic, String string, String string2, Function<Dynamic<T>, Dynamic<T>> function) {
		return dynamic.map(object -> {
			DynamicOps<T> dynamicOps = dynamic.getOps();
			Function<T, T> function2 = objectx -> ((Dynamic)function.apply(new Dynamic<>(dynamicOps, (T)objectx))).getValue();
			return dynamicOps.get((T)object, string).map(object2 -> dynamicOps.set((T)object, string2, (T)function2.apply(object2))).result().orElse(object);
		});
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(
			DSL.remainderFinder(),
			dynamic -> method_43072(
					dynamic,
					this.variantKey,
					"variant",
					dynamicx -> DataFixUtils.orElse(
							dynamicx.asNumber().map(number -> dynamicx.createString((String)this.variantIntToId.apply(number.intValue()))).result(), dynamicx
						)
				)
		);
	}
}
