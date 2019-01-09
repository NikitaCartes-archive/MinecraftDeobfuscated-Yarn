package net.minecraft;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;

public class class_1218 extends DataFix {
	private final String field_5742;
	private final TypeReference field_5743;

	public class_1218(Schema schema, String string, TypeReference typeReference) {
		super(schema, true);
		this.field_5742 = string;
		this.field_5743 = typeReference;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.writeAndRead(this.field_5742, this.getInputSchema().getType(this.field_5743), this.getOutputSchema().getType(this.field_5743));
	}
}
