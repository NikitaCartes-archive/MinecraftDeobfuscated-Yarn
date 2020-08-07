package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class CatTypeFix extends ChoiceFix {
	public CatTypeFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "CatTypeFix", TypeReferences.ENTITY, "minecraft:cat");
	}

	public Dynamic<?> fixCatTypeData(Dynamic<?> dynamic) {
		return dynamic.get("CatType").asInt(0) == 9 ? dynamic.set("CatType", dynamic.createInt(10)) : dynamic;
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::fixCatTypeData);
	}
}
