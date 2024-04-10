package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class EmptyItemInVillagerTradeFix extends DataFix {
	public EmptyItemInVillagerTradeFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.VILLAGER_TRADE);
		return this.writeFixAndRead("EmptyItemInVillagerTradeFix", type, type, dynamic -> {
			Dynamic<?> dynamic2 = dynamic.get("buyB").orElseEmptyMap();
			String string = dynamic2.get("id").asString("");
			int i = dynamic2.get("count").asInt(0);
			return !string.equals("minecraft:air") && i != 0 ? dynamic : dynamic.remove("buyB");
		});
	}
}
