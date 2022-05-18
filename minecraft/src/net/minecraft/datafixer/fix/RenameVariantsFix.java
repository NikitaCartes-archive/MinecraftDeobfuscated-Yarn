package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class RenameVariantsFix extends ChoiceFix {
	private final Map<String, String> oldToNewNames;

	public RenameVariantsFix(Schema schema, String name, TypeReference type, String choiceName, Map<String, String> oldToNewNames) {
		super(schema, false, name, type, choiceName);
		this.oldToNewNames = oldToNewNames;
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(
			DSL.remainderFinder(),
			dynamic -> dynamic.update(
					"variant",
					dynamicx -> DataFixUtils.orElse(
							dynamicx.asString().map(variantName -> dynamicx.createString((String)this.oldToNewNames.getOrDefault(variantName, variantName))).result(), dynamicx
						)
				)
		);
	}
}