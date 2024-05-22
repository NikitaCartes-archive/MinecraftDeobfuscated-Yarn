package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;

public class ProjectileItemTypeFix extends DataFix {
	private static final String EMPTY_ID = "minecraft:empty";

	public ProjectileItemTypeFix(Schema outputSchema) {
		super(outputSchema, true);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ENTITY);
		Type<?> type2 = this.getOutputSchema().getType(TypeReferences.ENTITY);
		return this.fixTypeEverywhereTyped(
			"Fix AbstractArrow item type",
			type,
			type2,
			FixUtil.compose(
				this.createFixApplier("minecraft:trident", ProjectileItemTypeFix::fixTrident),
				this.createFixApplier("minecraft:arrow", ProjectileItemTypeFix::fixArrow),
				this.createFixApplier("minecraft:spectral_arrow", ProjectileItemTypeFix::fixSpectralArrow)
			)
		);
	}

	private Function<Typed<?>, Typed<?>> createFixApplier(String id, ProjectileItemTypeFix.Fixer<?> fixer) {
		Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, id);
		Type<?> type2 = this.getOutputSchema().getChoiceType(TypeReferences.ENTITY, id);
		return createFixApplier(id, fixer, type, type2);
	}

	private static <T> Function<Typed<?>, Typed<?>> createFixApplier(String id, ProjectileItemTypeFix.Fixer<?> fixer, Type<?> inputType, Type<T> outputType) {
		OpticFinder<?> opticFinder = DSL.namedChoice(id, inputType);
		return typed -> typed.updateTyped(opticFinder, outputType, typedx -> fixer.fix(typedx, outputType));
	}

	private static <T> Typed<T> fixArrow(Typed<?> typed, Type<T> type) {
		return Util.apply(typed, type, data -> data.set("item", createStack(data, getArrowId(data))));
	}

	private static String getArrowId(Dynamic<?> arrowData) {
		return arrowData.get("Potion").asString("minecraft:empty").equals("minecraft:empty") ? "minecraft:arrow" : "minecraft:tipped_arrow";
	}

	private static <T> Typed<T> fixSpectralArrow(Typed<?> typed, Type<T> type) {
		return Util.apply(typed, type, data -> data.set("item", createStack(data, "minecraft:spectral_arrow")));
	}

	private static Dynamic<?> createStack(Dynamic<?> projectileData, String id) {
		return projectileData.createMap(
			ImmutableMap.of(projectileData.createString("id"), projectileData.createString(id), projectileData.createString("Count"), projectileData.createInt(1))
		);
	}

	private static <T> Typed<T> fixTrident(Typed<?> typed, Type<T> type) {
		return new Typed<>(type, typed.getOps(), (T)typed.getValue());
	}

	interface Fixer<F> {
		Typed<F> fix(Typed<?> typed, Type<F> type);
	}
}
