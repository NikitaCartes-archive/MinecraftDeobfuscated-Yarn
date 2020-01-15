package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;

public class WriteAndReadFix extends DataFix {
	private final String name;
	private final TypeReference type;

	public WriteAndReadFix(Schema outputSchema, String name, TypeReference type) {
		super(outputSchema, true);
		this.name = name;
		this.type = type;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.writeAndRead(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type));
	}
}
