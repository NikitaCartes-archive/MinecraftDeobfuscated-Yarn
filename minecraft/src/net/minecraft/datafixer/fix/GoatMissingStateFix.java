package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class GoatMissingStateFix extends ChoiceFix {
	public GoatMissingStateFix(Schema outputSchema) {
		super(outputSchema, false, "EntityGoatMissingStateFix", TypeReferences.ENTITY, "minecraft:goat");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(
			DSL.remainderFinder(), dynamic -> dynamic.set("HasLeftHorn", dynamic.createBoolean(true)).set("HasRightHorn", dynamic.createBoolean(true))
		);
	}
}
