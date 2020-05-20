package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class BlockEntityJukeboxFix extends ChoiceFix {
	public BlockEntityJukeboxFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "BlockEntityJukeboxFix", TypeReferences.BLOCK_ENTITY, "minecraft:jukebox");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:jukebox");
		Type<?> type2 = type.findFieldType("RecordItem");
		OpticFinder<?> opticFinder = DSL.fieldFinder("RecordItem", type2);
		Dynamic<?> dynamic = inputType.get(DSL.remainderFinder());
		int i = dynamic.get("Record").asInt(0);
		if (i > 0) {
			dynamic.remove("Record");
			String string = ItemInstanceTheFlatteningFix.getItem(ItemIdFix.fromId(i), 0);
			if (string != null) {
				Dynamic<?> dynamic2 = dynamic.emptyMap();
				dynamic2 = dynamic2.set("id", dynamic2.createString(string));
				dynamic2 = dynamic2.set("Count", dynamic2.createByte((byte)1));
				return inputType.set(
						opticFinder,
						(Typed)((Pair)type2.readTyped(dynamic2).result().orElseThrow(() -> new IllegalStateException("Could not create record item stack."))).getFirst()
					)
					.set(DSL.remainderFinder(), dynamic);
			}
		}

		return inputType;
	}
}
