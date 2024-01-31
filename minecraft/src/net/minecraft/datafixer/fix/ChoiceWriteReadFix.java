package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.View;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.BitSet;
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
		Type<?> type5 = type2.all(method_56640(type, type3), true, false).view().newType();
		return this.method_56641(type, type3, opticFinder, type4, type5);
	}

	private <S, T, A, B> TypeRewriteRule method_56641(Type<S> type, Type<T> type2, OpticFinder<A> opticFinder, Type<B> type3, Type<?> type4) {
		return this.fixTypeEverywhere(this.name, type, type2, dynamicOps -> object -> {
				Typed<S> typed = new Typed<>(type, dynamicOps, (S)object);
				return typed.update(opticFinder, type3, objectx -> {
					Typed<A> typedx = new Typed<>((Type<A>)type4, dynamicOps, (A)objectx);
					return Util.apply(typedx, type3, this::transform).getValue();
				}).getValue();
			});
	}

	private static <A, B> TypeRewriteRule method_56640(Type<A> type, Type<B> type2) {
		RewriteResult<A, B> rewriteResult = RewriteResult.create(View.create("Patcher", type, type2, dynamicOps -> object -> {
				throw new UnsupportedOperationException();
			}), new BitSet());
		return TypeRewriteRule.everywhere(TypeRewriteRule.ifSame(type, rewriteResult), PointFreeRule.nop(), true, true);
	}

	protected abstract <T> Dynamic<T> transform(Dynamic<T> data);
}
