package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class class_3602 extends class_1167 {
	public class_3602(Schema schema, boolean bl) {
		super("EntityHorseSplitFix", schema, bl);
	}

	@Override
	protected Pair<String, Typed<?>> method_4982(String string, Typed<?> typed) {
		Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
		if (Objects.equals("EntityHorse", string)) {
			int i = dynamic.get("Type").asInt(0);
			String string2;
			switch (i) {
				case 0:
				default:
					string2 = "Horse";
					break;
				case 1:
					string2 = "Donkey";
					break;
				case 2:
					string2 = "Mule";
					break;
				case 3:
					string2 = "ZombieHorse";
					break;
				case 4:
					string2 = "SkeletonHorse";
			}

			dynamic.remove("Type");
			Type<?> type = (Type<?>)this.getOutputSchema().findChoiceType(class_1208.field_5729).types().get(string2);
			return Pair.of(string2, (Typed<?>)type.readTyped(typed.write()).getSecond().orElseThrow(() -> new IllegalStateException("Could not parse the new horse")));
		} else {
			return Pair.of(string, typed);
		}
	}
}
