package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class VillagerTradeFix extends DataFix {
	public VillagerTradeFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.VILLAGER_TRADE);
		OpticFinder<?> opticFinder = type.findField("buy");
		OpticFinder<?> opticFinder2 = type.findField("buyB");
		OpticFinder<?> opticFinder3 = type.findField("sell");
		OpticFinder<Pair<String, String>> opticFinder4 = DSL.fieldFinder(
			"id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType())
		);
		Function<Typed<?>, Typed<?>> function = itemTyped -> this.fixPumpkinTrade(opticFinder4, itemTyped);
		return this.fixTypeEverywhereTyped(
			"Villager trade fix",
			type,
			villagerTradeTyped -> villagerTradeTyped.updateTyped(opticFinder, function).updateTyped(opticFinder2, function).updateTyped(opticFinder3, function)
		);
	}

	private Typed<?> fixPumpkinTrade(OpticFinder<Pair<String, String>> idOpticFinder, Typed<?> itemTyped) {
		return itemTyped.update(idOpticFinder, entry -> entry.mapSecond(id -> Objects.equals(id, "minecraft:carved_pumpkin") ? "minecraft:pumpkin" : id));
	}
}
