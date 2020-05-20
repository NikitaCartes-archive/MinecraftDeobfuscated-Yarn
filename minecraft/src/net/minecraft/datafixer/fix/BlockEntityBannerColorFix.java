package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class BlockEntityBannerColorFix extends ChoiceFix {
	public BlockEntityBannerColorFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "BlockEntityBannerColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
	}

	public Dynamic<?> fixBannerColor(Dynamic<?> dynamic) {
		dynamic = dynamic.update("Base", dynamicx -> dynamicx.createInt(15 - dynamicx.asInt(0)));
		return dynamic.update(
			"Patterns",
			dynamicx -> DataFixUtils.orElse(
					dynamicx.asStreamOpt()
						.map(stream -> stream.map(dynamicxx -> dynamicxx.update("Color", dynamicxxx -> dynamicxxx.createInt(15 - dynamicxxx.asInt(0)))))
						.map(dynamicx::createList)
						.result(),
					dynamicx
				)
		);
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::fixBannerColor);
	}
}
