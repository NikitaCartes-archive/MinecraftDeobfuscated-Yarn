package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixers.TypeReferences;

public class ColorlessShulkerEntityFix extends ChoiceFix {
	public ColorlessShulkerEntityFix(Schema schema, boolean bl) {
		super(schema, bl, "Colorless shulker entity fix", TypeReferences.ENTITY, "minecraft:shulker");
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), dynamic -> dynamic.get("Color").asInt(0) == 10 ? dynamic.set("Color", dynamic.createByte((byte)16)) : dynamic);
	}
}
