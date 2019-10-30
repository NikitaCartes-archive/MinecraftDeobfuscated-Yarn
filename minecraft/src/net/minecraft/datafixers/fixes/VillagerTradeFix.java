package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.datafixers.TypeReferences;

public class VillagerTradeFix extends ChoiceFix {
	public VillagerTradeFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "Villager trade fix", TypeReferences.ENTITY, "minecraft:villager");
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		OpticFinder<?> opticFinder = typed.getType().findField("Offers");
		OpticFinder<?> opticFinder2 = opticFinder.type().findField("Recipes");
		Type<?> type = opticFinder2.type();
		if (!(type instanceof ListType)) {
			throw new IllegalStateException("Recipes are expected to be a list.");
		} else {
			ListType<?> listType = (ListType<?>)type;
			Type<?> type2 = listType.getElement();
			OpticFinder<?> opticFinder3 = DSL.typeFinder(type2);
			OpticFinder<?> opticFinder4 = type2.findField("buy");
			OpticFinder<?> opticFinder5 = type2.findField("buyB");
			OpticFinder<?> opticFinder6 = type2.findField("sell");
			OpticFinder<Pair<String, String>> opticFinder7 = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
			Function<Typed<?>, Typed<?>> function = typedx -> this.fixPumpkinTrade(opticFinder7, typedx);
			return typed.updateTyped(
				opticFinder,
				typedx -> typedx.updateTyped(
						opticFinder2,
						typedxx -> typedxx.updateTyped(
								opticFinder3, typedxxx -> typedxxx.updateTyped(opticFinder4, function).updateTyped(opticFinder5, function).updateTyped(opticFinder6, function)
							)
					)
			);
		}
	}

	private Typed<?> fixPumpkinTrade(OpticFinder<Pair<String, String>> opticFinder, Typed<?> typed) {
		return typed.update(opticFinder, pair -> pair.mapSecond(string -> Objects.equals(string, "minecraft:carved_pumpkin") ? "minecraft:pumpkin" : string));
	}
}
