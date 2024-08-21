package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class VillagerCanPickUpLootFix extends ChoiceFix {
	private static final String FIELD_NAME = "CanPickUpLoot";

	public VillagerCanPickUpLootFix(Schema schema) {
		super(schema, true, "Villager CanPickUpLoot default value", TypeReferences.ENTITY, "Villager");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), VillagerCanPickUpLootFix::fix);
	}

	private static Dynamic<?> fix(Dynamic<?> dynamic) {
		return dynamic.set("CanPickUpLoot", dynamic.createBoolean(true));
	}
}
