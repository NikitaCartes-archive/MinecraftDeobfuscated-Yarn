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

	private static Dynamic<?> renameProperties(Dynamic<?> blockEntityDynamic) {
		String string = blockEntityDynamic.get("attachement_type").asString("minecraft:empty");
		String string2 = blockEntityDynamic.get("target_pool").asString("minecraft:empty");
		return blockEntityDynamic.set("name", blockEntityDynamic.createString(string))
			.set("target", blockEntityDynamic.createString(string))
			.remove("attachement_type")
			.set("pool", blockEntityDynamic.createString(string2))
			.remove("target_pool");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), JigsawPropertiesFix::renameProperties);
	}
}
