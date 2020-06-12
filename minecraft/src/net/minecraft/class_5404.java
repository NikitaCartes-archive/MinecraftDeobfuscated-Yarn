package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class class_5404 extends DataFix {
	private final String field_25665;
	private final String field_25666;
	private final String field_25667;

	public class_5404(Schema schema, boolean bl, String string, String string2, String string3) {
		super(schema, bl);
		this.field_25665 = string;
		this.field_25666 = string2;
		this.field_25667 = string3;
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			this.field_25665,
			this.getInputSchema().getType(TypeReferences.OPTIONS),
			typed -> typed.update(
					DSL.remainderFinder(),
					dynamic -> DataFixUtils.orElse(
							dynamic.get(this.field_25666).result().map(dynamic2 -> dynamic.set(this.field_25667, dynamic2).remove(this.field_25666)), dynamic
						)
				)
		);
	}
}
