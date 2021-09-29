package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class AddFlagIfNotPresentFix extends DataFix {
	private final String field_35009;
	private final boolean field_35010;
	private final String field_35011;
	private final TypeReference field_35012;

	public AddFlagIfNotPresentFix(Schema schema, TypeReference typeReference, String string, boolean bl) {
		super(schema, true);
		this.field_35010 = bl;
		this.field_35011 = string;
		this.field_35009 = "AddFlagIfNotPresentFix_" + this.field_35011 + "=" + this.field_35010 + " for " + schema.getVersionKey();
		this.field_35012 = typeReference;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(this.field_35012);
		return this.fixTypeEverywhereTyped(
			this.field_35009,
			type,
			typed -> typed.update(
					DSL.remainderFinder(),
					dynamic -> dynamic.set(this.field_35011, DataFixUtils.orElseGet(dynamic.get(this.field_35011).result(), () -> dynamic.createBoolean(this.field_35010)))
				)
		);
	}
}
