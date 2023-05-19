package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.math.MathHelper;

public class VillagerXpRebuildFix extends DataFix {
	private static final int field_29914 = 2;
	private static final int[] LEVEL_TO_XP = new int[]{0, 10, 50, 100, 150};

	public static int levelToXp(int level) {
		return LEVEL_TO_XP[MathHelper.clamp(level - 1, 0, LEVEL_TO_XP.length - 1)];
	}

	public VillagerXpRebuildFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "minecraft:villager");
		OpticFinder<?> opticFinder = DSL.namedChoice("minecraft:villager", type);
		OpticFinder<?> opticFinder2 = type.findField("Offers");
		Type<?> type2 = opticFinder2.type();
		OpticFinder<?> opticFinder3 = type2.findField("Recipes");
		ListType<?> listType = (ListType<?>)opticFinder3.type();
		OpticFinder<?> opticFinder4 = listType.getElement().finder();
		return this.fixTypeEverywhereTyped(
			"Villager level and xp rebuild",
			this.getInputSchema().getType(TypeReferences.ENTITY),
			typed -> typed.updateTyped(
					opticFinder,
					type,
					typedx -> {
						Dynamic<?> dynamic = typedx.get(DSL.remainderFinder());
						int i = dynamic.get("VillagerData").get("level").asInt(0);
						Typed<?> typed2 = typedx;
						if (i == 0 || i == 1) {
							int j = (Integer)typedx.getOptionalTyped(opticFinder2)
								.flatMap(typedxx -> typedxx.getOptionalTyped(opticFinder3))
								.map(typedxx -> typedxx.getAllTyped(opticFinder4).size())
								.orElse(0);
							i = MathHelper.clamp(j / 2, 1, 5);
							if (i > 1) {
								typed2 = fixLevel(typedx, i);
							}
						}

						Optional<Number> optional = dynamic.get("Xp").asNumber().result();
						if (!optional.isPresent()) {
							typed2 = fixXp(typed2, i);
						}

						return typed2;
					}
				)
		);
	}

	private static Typed<?> fixLevel(Typed<?> typed, int level) {
		return typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("VillagerData", dynamicx -> dynamicx.set("level", dynamicx.createInt(level))));
	}

	private static Typed<?> fixXp(Typed<?> typed, int level) {
		int i = levelToXp(level);
		return typed.update(DSL.remainderFinder(), dynamic -> dynamic.set("Xp", dynamic.createInt(i)));
	}
}
