package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class CatTypeFix extends ChoiceFix {
	public CatTypeFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "CatTypeFix", TypeReferences.ENTITY, "minecraft:cat");
	}

	public Dynamic<?> fixCatTypeData(Dynamic<?> tag) {
		return tag.get("CatType").asInt(0) == 9 ? tag.set("CatType", tag.createInt(10)) : tag;
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::fixCatTypeData);
	}
}
