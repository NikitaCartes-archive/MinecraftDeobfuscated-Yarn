package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class JigsawPropertiesFix extends ChoiceFix {
	public JigsawPropertiesFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "JigsawPropertiesFix", TypeReferences.BLOCK_ENTITY, "minecraft:jigsaw");
	}

	private static Dynamic<?> renameProperties(Dynamic<?> data) {
		String string = data.get("attachement_type").asString("minecraft:empty");
		String string2 = data.get("target_pool").asString("minecraft:empty");
		return data.set("name", data.createString(string))
			.set("target", data.createString(string))
			.remove("attachement_type")
			.set("pool", data.createString(string2))
			.remove("target_pool");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), JigsawPropertiesFix::renameProperties);
	}
}
