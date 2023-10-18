package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.Util;

public abstract class ChoiceWriteReadFix extends DataFix {
	private final String name;
	private final String choiceName;
	private final TypeReference type;

	public ChoiceWriteReadFix(Schema outputSchema, boolean changesType, String name, TypeReference type, String choiceName) {
		super(outputSchema, changesType);
		this.name = name;
		this.type = type;
		this.choiceName = choiceName;
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(this.type);
		Type<?> type2 = this.getInputSchema().getChoiceType(this.type, this.choiceName);
		Type<?> type3 = this.getOutputSchema().getType(this.type);
		Type<?> type4 = this.getOutputSchema().getChoiceType(this.type, this.choiceName);
		OpticFinder<?> opticFinder = DSL.namedChoice(this.choiceName, type2);
		return this.fixTypeEverywhereTyped(
			this.name,
			type,
			type3,
			typed -> typed.updateTyped(
					opticFinder,
					type4,
					typedx -> (Typed)Util.getResult(
								typedx.write().map(this::transform).flatMap(type4::readTyped), string -> new IllegalStateException("Could not parse the value " + string)
							)
							.getFirst()
				)
		);
	}

	protected abstract <T> Dynamic<T> transform(Dynamic<T> data);
}
