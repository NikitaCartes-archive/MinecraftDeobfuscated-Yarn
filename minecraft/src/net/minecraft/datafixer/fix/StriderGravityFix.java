package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class StriderGravityFix extends ChoiceFix {
	public StriderGravityFix(Schema outputschema, boolean changesType) {
		super(outputschema, changesType, "StriderGravityFix", TypeReferences.ENTITY, "minecraft:strider");
	}

	public Dynamic<?> updateNoGravityNbt(Dynamic<?> striderDynamic) {
		return striderDynamic.get("NoGravity").asBoolean(false) ? striderDynamic.set("NoGravity", striderDynamic.createBoolean(false)) : striderDynamic;
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), this::updateNoGravityNbt);
	}
}
