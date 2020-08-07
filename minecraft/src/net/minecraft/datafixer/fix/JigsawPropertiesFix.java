package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class JigsawPropertiesFix extends ChoiceFix {
	public JigsawPropertiesFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "JigsawPropertiesFix", TypeReferences.BLOCK_ENTITY, "minecraft:jigsaw");
	}

	private static Dynamic<?> renameProperties(Dynamic<?> dynamic) {
		String string = dynamic.get("attachement_type").asString("minecraft:empty");
		String string2 = dynamic.get("target_pool").asString("minecraft:empty");
		return dynamic.set("name", dynamic.createString(string))
			.set("target", dynamic.createString(string))
			.remove("attachement_type")
			.set("pool", dynamic.createString(string2))
			.remove("target_pool");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), JigsawPropertiesFix::renameProperties);
	}
}
