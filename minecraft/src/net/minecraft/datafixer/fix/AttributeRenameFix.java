package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;

public class AttributeRenameFix extends DataFix {
	private final String name;
	private final UnaryOperator<String> renamer;

	public AttributeRenameFix(Schema outputSchema, String name, UnaryOperator<String> renamer) {
		super(outputSchema, false);
		this.name = name;
		this.renamer = renamer;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return TypeRewriteRule.seq(
			this.fixTypeEverywhereTyped(this.name + " (Components)", this.getInputSchema().getType(TypeReferences.DATA_COMPONENTS), this::applyToComponents),
			this.fixTypeEverywhereTyped(this.name + " (Entity)", this.getInputSchema().getType(TypeReferences.ENTITY), this::applyToEntity),
			this.fixTypeEverywhereTyped(this.name + " (Player)", this.getInputSchema().getType(TypeReferences.PLAYER), this::applyToEntity)
		);
	}

	private Typed<?> applyToComponents(Typed<?> typed) {
		return typed.update(
			DSL.remainderFinder(),
			dynamic -> dynamic.update(
					"minecraft:attribute_modifiers",
					dynamicx -> dynamicx.update(
							"modifiers",
							dynamicxx -> DataFixUtils.orElse(
									dynamicxx.asStreamOpt().result().map(stream -> stream.map(this::applyToTypeField)).map(dynamicxx::createList), dynamicxx
								)
						)
				)
		);
	}

	private Typed<?> applyToEntity(Typed<?> typed) {
		return typed.update(
			DSL.remainderFinder(),
			dynamic -> dynamic.update(
					"attributes",
					dynamicx -> DataFixUtils.orElse(dynamicx.asStreamOpt().result().map(stream -> stream.map(this::applyToIdField)).map(dynamicx::createList), dynamicx)
				)
		);
	}

	private Dynamic<?> applyToIdField(Dynamic<?> dynamic) {
		return FixUtil.apply(dynamic, "id", this.renamer);
	}

	private Dynamic<?> applyToTypeField(Dynamic<?> dynamic) {
		return FixUtil.apply(dynamic, "type", this.renamer);
	}
}
