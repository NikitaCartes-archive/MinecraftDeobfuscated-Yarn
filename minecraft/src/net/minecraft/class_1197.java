package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;

public abstract class class_1197 extends DataFix {
	private final String field_5703;
	private final String field_5705;
	private final TypeReference field_5704;

	public class_1197(Schema schema, boolean bl, String string, TypeReference typeReference, String string2) {
		super(schema, bl);
		this.field_5703 = string;
		this.field_5704 = typeReference;
		this.field_5705 = string2;
	}

	@Override
	public TypeRewriteRule makeRule() {
		OpticFinder<?> opticFinder = DSL.namedChoice(this.field_5705, this.getInputSchema().getChoiceType(this.field_5704, this.field_5705));
		return this.fixTypeEverywhereTyped(
			this.field_5703,
			this.getInputSchema().getType(this.field_5704),
			this.getOutputSchema().getType(this.field_5704),
			typed -> typed.updateTyped(opticFinder, this.getOutputSchema().getChoiceType(this.field_5704, this.field_5705), this::method_5105)
		);
	}

	protected abstract Typed<?> method_5105(Typed<?> typed);
}
