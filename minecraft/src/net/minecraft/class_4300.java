package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import java.util.Optional;

public class class_4300 extends DataFix {
	private static final int[] field_19285 = new int[]{0, 10, 50, 100, 150};

	public static int method_20482(int i) {
		return field_19285[class_3532.method_15340(i - 1, 0, field_19285.length - 1)];
	}

	public class_4300(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getChoiceType(class_1208.field_5729, "minecraft:villager");
		OpticFinder<?> opticFinder = DSL.namedChoice("minecraft:villager", type);
		OpticFinder<?> opticFinder2 = type.findField("Offers");
		Type<?> type2 = opticFinder2.type();
		OpticFinder<?> opticFinder3 = type2.findField("Recipes");
		ListType<?> listType = (ListType<?>)opticFinder3.type();
		OpticFinder<?> opticFinder4 = listType.getElement().finder();
		return this.fixTypeEverywhereTyped(
			"Villager level and xp rebuild",
			this.getInputSchema().getType(class_1208.field_5729),
			typed -> typed.updateTyped(
					opticFinder,
					type,
					typedx -> {
						Dynamic<?> dynamic = typedx.get(DSL.remainderFinder());
						int i = ((Number)dynamic.get("VillagerData").get("level").asNumber().orElse(0)).intValue();
						Typed<?> typed2 = typedx;
						if (i == 0 || i == 1) {
							int j = (Integer)typedx.getOptionalTyped(opticFinder2)
								.flatMap(typedxx -> typedxx.getOptionalTyped(opticFinder3))
								.map(typedxx -> typedxx.getAllTyped(opticFinder4).size())
								.orElse(0);
							i = class_3532.method_15340(j / 2, 1, 5);
							if (i > 1) {
								typed2 = method_20487(typedx, i);
							}
						}

						Optional<Number> optional = dynamic.get("Xp").asNumber();
						if (!optional.isPresent()) {
							typed2 = method_20490(typed2, i);
						}

						return typed2;
					}
				)
		);
	}

	private static Typed<?> method_20487(Typed<?> typed, int i) {
		return typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("VillagerData", dynamicx -> dynamicx.set("level", dynamicx.createInt(i))));
	}

	private static Typed<?> method_20490(Typed<?> typed, int i) {
		int j = method_20482(i);
		return typed.update(DSL.remainderFinder(), dynamic -> dynamic.set("Xp", dynamic.createInt(j)));
	}
}
