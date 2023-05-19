package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class StriderGravityFix extends ChoiceFix {
	public StriderGravityFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "StriderGravityFix", TypeReferences.ENTITY, "minecraft:strider");
	}

	public Dynamic<?> updateNoGravityNbt(Dynamic<?> dynamic) {
		return dynamic.get("NoGravity").asBoolean(false) ? dynamic.set("NoGravity", dynamic.createBoolean(false)) : dynamic;
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::updateNoGravityNbt);
	}
}
