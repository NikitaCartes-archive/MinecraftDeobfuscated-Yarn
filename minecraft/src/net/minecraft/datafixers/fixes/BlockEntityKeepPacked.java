package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixers.TypeReferences;

public class BlockEntityKeepPacked extends ChoiceFix {
	public BlockEntityKeepPacked(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "BlockEntityKeepPacked", TypeReferences.BLOCK_ENTITY, "DUMMY");
	}

	private static Dynamic<?> keepPacked(Dynamic<?> tag) {
		return tag.set("keepPacked", tag.createBoolean(true));
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), BlockEntityKeepPacked::keepPacked);
	}
}
