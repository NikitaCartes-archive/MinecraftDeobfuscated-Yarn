package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class GoatHornIdFix extends ItemNbtFix {
	private static final String[] GOAT_HORN_IDS = new String[]{
		"minecraft:ponder_goat_horn",
		"minecraft:sing_goat_horn",
		"minecraft:seek_goat_horn",
		"minecraft:feel_goat_horn",
		"minecraft:admire_goat_horn",
		"minecraft:call_goat_horn",
		"minecraft:yearn_goat_horn",
		"minecraft:dream_goat_horn"
	};

	public GoatHornIdFix(Schema outputSchema) {
		super(outputSchema, "GoatHornIdFix", itemId -> itemId.equals("minecraft:goat_horn"));
	}

	@Override
	protected <T> Dynamic<T> fixNbt(Dynamic<T> dynamic) {
		int i = dynamic.get("SoundVariant").asInt(0);
		String string = GOAT_HORN_IDS[i >= 0 && i < GOAT_HORN_IDS.length ? i : 0];
		return dynamic.remove("SoundVariant").set("instrument", dynamic.createString(string));
	}
}
