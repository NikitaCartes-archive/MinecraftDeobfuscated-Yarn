package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixers.TypeReferences;

public class EntityItemFrameDirectionFix extends ChoiceFix {
	public EntityItemFrameDirectionFix(Schema schema, boolean bl) {
		super(schema, bl, "EntityItemFrameDirectionFix", TypeReferences.ENTITY, "minecraft:item_frame");
	}

	public Dynamic<?> method_15711(Dynamic<?> dynamic) {
		return dynamic.set("Facing", dynamic.createByte(method_15712(dynamic.get("Facing").asByte((byte)0))));
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_15711);
	}

	private static byte method_15712(byte b) {
		switch (b) {
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
