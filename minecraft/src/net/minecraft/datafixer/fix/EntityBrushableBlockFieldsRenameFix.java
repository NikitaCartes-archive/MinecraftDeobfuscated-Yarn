package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class EntityBrushableBlockFieldsRenameFix extends ChoiceFix {
	public EntityBrushableBlockFieldsRenameFix(Schema outputSchema) {
		super(outputSchema, false, "EntityBrushableBlockFieldsRenameFix", TypeReferences.BLOCK_ENTITY, "minecraft:brushable_block");
	}

	public Dynamic<?> renameFields(Dynamic<?> dynamic) {
		return dynamic.renameField("loot_table", "LootTable").renameField("loot_table_seed", "LootTableSeed");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), this::renameFields);
	}
}
