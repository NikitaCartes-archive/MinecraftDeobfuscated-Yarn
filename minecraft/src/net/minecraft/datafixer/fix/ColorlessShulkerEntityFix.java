package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class ColorlessShulkerEntityFix extends ChoiceFix {
	public ColorlessShulkerEntityFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "Colorless shulker entity fix", TypeReferences.ENTITY, "minecraft:shulker");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(
			DSL.remainderFinder(),
			shulkerDynamic -> shulkerDynamic.get("Color").asInt(0) == 10 ? shulkerDynamic.set("Color", shulkerDynamic.createByte((byte)16)) : shulkerDynamic
		);
	}
}
