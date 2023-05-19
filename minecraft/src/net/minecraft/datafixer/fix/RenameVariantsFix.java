package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class RenameVariantsFix extends ChoiceFix {
	private final Map<String, String> oldToNewNames;

	public RenameVariantsFix(Schema outputSchema, String name, TypeReference type, String choiceName, Map<String, String> oldToNewNames) {
		super(outputSchema, false, name, type, choiceName);
		this.oldToNewNames = oldToNewNames;
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(
			DSL.remainderFinder(),
			dynamic -> dynamic.update(
					"variant",
					variant -> DataFixUtils.orElse(
							variant.asString().map(variantName -> variant.createString((String)this.oldToNewNames.getOrDefault(variantName, variantName))).result(), variant
						)
				)
		);
	}
}
