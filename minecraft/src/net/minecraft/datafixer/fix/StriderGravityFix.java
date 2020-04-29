package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class StriderGravityFix extends ChoiceFix {
	public StriderGravityFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "StriderGravityFix", TypeReferences.ENTITY, "minecraft:strider");
	}

	public Dynamic<?> updateNoGravityTag(Dynamic<?> data) {
		return data.get("NoGravity").asBoolean(false) ? data.set("NoGravity", data.createBoolean(false)) : data;
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::updateNoGravityTag);
	}
}
