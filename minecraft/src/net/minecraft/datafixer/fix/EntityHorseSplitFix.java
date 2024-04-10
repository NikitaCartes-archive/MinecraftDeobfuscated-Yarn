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
	public EntityHorseSplitFix(Schema schema, boolean bl) {
		super("EntityHorseSplitFix", schema, bl);
	}

	@Override
	protected Pair<String, Typed<?>> transform(String choice, Typed<?> typed) {
		Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
		if (Objects.equals("EntityHorse", choice)) {
			int i = dynamic.get("Type").asInt(0);

			String string = switch (i) {
				case 1 -> "Donkey";
				case 2 -> "Mule";
				case 3 -> "ZombieHorse";
				case 4 -> "SkeletonHorse";
				default -> "Horse";
			};
			dynamic.remove("Type");
			Type<?> type = (Type<?>)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(string);
			return Pair.of(string, Util.apply(typed, type, dynamicx -> dynamicx));
		} else {
			return Pair.of(choice, typed);
		}
	}
}
