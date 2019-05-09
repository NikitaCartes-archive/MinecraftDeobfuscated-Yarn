package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixers.TypeReferences;

public class BlockEntityBannerColorFix extends ChoiceFix {
	public BlockEntityBannerColorFix(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntityBannerColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
	}

	public Dynamic<?> fixBannerColor(Dynamic<?> dynamic) {
		dynamic = dynamic.update("Base", dynamicx -> dynamicx.createInt(15 - dynamicx.asInt(0)));
		return dynamic.update(
			"Patterns",
			dynamicx -> DataFixUtils.orElse(
					dynamicx.asStreamOpt()
						.map(stream -> stream.map(dynamicxx -> dynamicxx.update("Color", dynamicxxx -> dynamicxxx.createInt(15 - dynamicxxx.asInt(0)))))
						.map(dynamicx::createList),
					dynamicx
				)
		);
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::fixBannerColor);
	}
}
