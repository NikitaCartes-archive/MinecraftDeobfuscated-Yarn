package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class AddFlagIfNotPresentFix extends DataFix {
	private final String description;
	private final boolean value;
	private final String key;
	private final TypeReference typeReference;

	public AddFlagIfNotPresentFix(Schema outputSchema, TypeReference typeReference, String key, boolean value) {
		super(outputSchema, true);
		this.value = value;
		this.key = key;
		this.description = "AddFlagIfNotPresentFix_" + this.key + "=" + this.value + " for " + outputSchema.getVersionKey();
		this.typeReference = typeReference;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(this.typeReference);
		return this.fixTypeEverywhereTyped(
			this.description,
			type,
			typed -> typed.update(
					DSL.remainderFinder(), dynamic -> dynamic.set(this.key, DataFixUtils.orElseGet(dynamic.get(this.key).result(), () -> dynamic.createBoolean(this.value)))
				)
		);
	}
}
