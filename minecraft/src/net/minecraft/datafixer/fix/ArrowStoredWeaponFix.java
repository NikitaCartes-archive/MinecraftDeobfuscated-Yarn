package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;

public class ArrowStoredWeaponFix extends DataFix {
	public ArrowStoredWeaponFix(Schema outputSchema) {
		super(outputSchema, true);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ENTITY);
		Type<?> type2 = this.getOutputSchema().getType(TypeReferences.ENTITY);
		return this.fixTypeEverywhereTyped(
			"Fix Arrow stored weapon", type, type2, FixUtil.compose(this.fixFor("minecraft:arrow"), this.fixFor("minecraft:spectral_arrow"))
		);
	}

	private Function<Typed<?>, Typed<?>> fixFor(String entityId) {
		Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, entityId);
		Type<?> type2 = this.getOutputSchema().getChoiceType(TypeReferences.ENTITY, entityId);
		return method_59912(entityId, type, type2);
	}

	private static <T> Function<Typed<?>, Typed<?>> method_59912(String name, Type<?> type, Type<T> type2) {
		OpticFinder<?> opticFinder = DSL.namedChoice(name, type);
		return typed -> typed.updateTyped(opticFinder, type2, typedx -> Util.apply(typedx, type2, UnaryOperator.identity()));
	}
}
