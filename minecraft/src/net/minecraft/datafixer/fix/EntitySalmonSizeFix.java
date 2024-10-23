package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class EntitySalmonSizeFix extends ChoiceFix {
	public EntitySalmonSizeFix(Schema outputSchema) {
		super(outputSchema, false, "EntitySalmonSizeFix", TypeReferences.ENTITY, "minecraft:salmon");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), dynamic -> {
			String string = dynamic.get("type").asString("medium");
			return string.equals("large") ? dynamic : dynamic.set("type", dynamic.createString("medium"));
		});
	}
}
