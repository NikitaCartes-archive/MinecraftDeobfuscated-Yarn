package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class EntityItemFrameDirectionFix extends ChoiceFix {
	public EntityItemFrameDirectionFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "EntityItemFrameDirectionFix", TypeReferences.ENTITY, "minecraft:item_frame");
	}

	public Dynamic<?> fixDirection(Dynamic<?> dynamic) {
		return dynamic.set("Facing", dynamic.createByte(updateDirection(dynamic.get("Facing").asByte((byte)0))));
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::fixDirection);
	}

	private static byte updateDirection(byte oldDirection) {
		switch (oldDirection) {
			case 0:
				return 3;
			case 1:
				return 4;
			case 2:
			default:
				return 2;
			case 3:
				return 5;
		}
	}
}
