package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;

public class EntityHorseSplitFix extends EntityTransformFix {
	public EntityHorseSplitFix(Schema outputSchema, boolean changesType) {
		super("EntityHorseSplitFix", outputSchema, changesType);
	}

	@Override
	protected Pair<String, Typed<?>> transform(String choice, Typed<?> entityTyped) {
		if (Objects.equals("EntityHorse", choice)) {
			Dynamic<?> dynamic = entityTyped.get(DSL.remainderFinder());
			int i = dynamic.get("Type").asInt(0);

			String string = switch (i) {
				case 1 -> "Donkey";
				case 2 -> "Mule";
				case 3 -> "ZombieHorse";
				case 4 -> "SkeletonHorse";
				default -> "Horse";
			};
			Type<?> type = (Type<?>)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(string);
			return Pair.of(string, Util.apply(entityTyped, type, dynamicx -> dynamicx.remove("Type")));
		} else {
			return Pair.of(choice, entityTyped);
		}
	}
}
