package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixers.TypeReferences;

public class BlockEntityBlockStateFix extends ChoiceFix {
	public BlockEntityBlockStateFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "BlockEntityBlockStateFix", TypeReferences.BLOCK_ENTITY, "minecraft:piston");
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		Type<?> type = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:piston");
		Type<?> type2 = type.findFieldType("blockState");
		OpticFinder<?> opticFinder = DSL.fieldFinder("blockState", type2);
		Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
		int i = dynamic.get("blockId").asInt(0);
		dynamic = dynamic.remove("blockId");
		int j = dynamic.get("blockData").asInt(0) & 15;
		dynamic = dynamic.remove("blockData");
		Dynamic<?> dynamic2 = BlockStateFlattening.lookupState(i << 4 | j);
		Typed<?> typed2 = (Typed<?>)type.pointTyped(typed.getOps()).orElseThrow(() -> new IllegalStateException("Could not create new piston block entity."));
		return typed2.set(DSL.remainderFinder(), dynamic)
			.set(
				opticFinder, (Typed)type2.readTyped(dynamic2).getSecond().orElseThrow(() -> new IllegalStateException("Could not parse newly created block state tag."))
			);
	}
}
