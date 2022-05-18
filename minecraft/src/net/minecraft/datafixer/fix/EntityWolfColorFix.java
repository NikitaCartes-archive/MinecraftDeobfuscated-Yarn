package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class EntityWolfColorFix extends ChoiceFix {
	public EntityWolfColorFix(Schema schema, boolean bl) {
		super(schema, bl, "EntityWolfColorFix", TypeReferences.ENTITY, "minecraft:wolf");
	}

	public Dynamic<?> fixCollarColor(Dynamic<?> dynamic) {
		return dynamic.update("CollarColor", dynamicx -> dynamicx.createByte((byte)(15 - dynamicx.asInt(0))));
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::fixCollarColor);
	}
}
