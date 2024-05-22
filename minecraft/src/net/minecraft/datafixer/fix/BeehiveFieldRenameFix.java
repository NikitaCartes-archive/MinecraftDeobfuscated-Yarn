package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;

public class BeehiveFieldRenameFix extends DataFix {
	public BeehiveFieldRenameFix(Schema outputSchema) {
		super(outputSchema, true);
	}

	private Dynamic<?> removeBeesField(Dynamic<?> dynamic) {
		return dynamic.remove("Bees");
	}

	private Dynamic<?> renameFields(Dynamic<?> dynamic) {
		dynamic = dynamic.remove("EntityData");
		dynamic = dynamic.renameField("TicksInHive", "ticks_in_hive");
		return dynamic.renameField("MinOccupationTicks", "min_ticks_in_hive");
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:beehive");
		OpticFinder<?> opticFinder = DSL.namedChoice("minecraft:beehive", type);
		ListType<?> listType = (ListType<?>)type.findFieldType("Bees");
		Type<?> type2 = listType.getElement();
		OpticFinder<?> opticFinder2 = DSL.fieldFinder("Bees", listType);
		OpticFinder<?> opticFinder3 = DSL.typeFinder(type2);
		Type<?> type3 = this.getInputSchema().getType(TypeReferences.BLOCK_ENTITY);
		Type<?> type4 = this.getOutputSchema().getType(TypeReferences.BLOCK_ENTITY);
		return this.fixTypeEverywhereTyped(
			"BeehiveFieldRenameFix",
			type3,
			type4,
			typed -> FixUtil.withType(
					type4,
					typed.updateTyped(
						opticFinder,
						typedx -> typedx.update(DSL.remainderFinder(), this::removeBeesField)
								.updateTyped(opticFinder2, typedxx -> typedxx.updateTyped(opticFinder3, typedxxx -> typedxxx.update(DSL.remainderFinder(), this::renameFields)))
					)
				)
		);
	}
}
