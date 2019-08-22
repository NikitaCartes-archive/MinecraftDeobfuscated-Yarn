package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixers.TypeReferences;

public class CatTypeFix extends ChoiceFix {
	public CatTypeFix(Schema schema, boolean bl) {
		super(schema, bl, "CatTypeFix", TypeReferences.ENTITY, "minecraft:cat");
	}

	public Dynamic<?> fixCatTypeData(Dynamic<?> dynamic) {
		return dynamic.get("CatType").asInt(0) == 9 ? dynamic.set("CatType", dynamic.createInt(10)) : dynamic;
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::fixCatTypeData);
	}
}
