package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixers.TypeReferences;

public class EntityWolfColorFix extends ChoiceFix {
	public EntityWolfColorFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "EntityWolfColorFix", TypeReferences.ENTITY, "minecraft:wolf");
	}

	public Dynamic<?> fixCollarColor(Dynamic<?> tag) {
		return tag.update("CollarColor", dynamic -> dynamic.createByte((byte)(15 - dynamic.asInt(0))));
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::fixCollarColor);
	}
}
