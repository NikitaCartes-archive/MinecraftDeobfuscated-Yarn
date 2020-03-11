package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class BlockEntityBannerColorFix extends ChoiceFix {
	public BlockEntityBannerColorFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "BlockEntityBannerColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
	}

	public Dynamic<?> fixBannerColor(Dynamic<?> tag) {
		tag = tag.update("Base", tagx -> tagx.createInt(15 - tagx.asInt(0)));
		return tag.update(
			"Patterns",
			dynamic -> DataFixUtils.orElse(
					dynamic.asStreamOpt().map(stream -> stream.map(dynamicx -> dynamicx.update("Color", tagx -> tagx.createInt(15 - tagx.asInt(0))))).map(dynamic::createList),
					dynamic
				)
		);
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::fixBannerColor);
	}
}
