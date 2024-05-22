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

	public Dynamic<?> fixCatTypeData(Dynamic<?> catDynamic) {
		return catDynamic.get("CatType").asInt(0) == 9 ? catDynamic.set("CatType", catDynamic.createInt(10)) : catDynamic;
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), this::fixCatTypeData);
	}
}
