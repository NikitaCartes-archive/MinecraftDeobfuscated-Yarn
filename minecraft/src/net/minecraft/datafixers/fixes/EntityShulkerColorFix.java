package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixers.TypeReferences;

public class EntityShulkerColorFix extends ChoiceFix {
	public EntityShulkerColorFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "EntityShulkerColorFix", TypeReferences.ENTITY, "minecraft:shulker");
	}

	public Dynamic<?> method_4985(Dynamic<?> dynamic) {
		return !dynamic.get("Color").map(Dynamic::asNumber).isPresent() ? dynamic.set("Color", dynamic.createByte((byte)10)) : dynamic;
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_4985);
	}
}
