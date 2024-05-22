package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.TypeReferences;

public class RenameEntityAttributesFix extends DataFix {
	private final String description;
	private final UnaryOperator<String> renames;

	public RenameEntityAttributesFix(Schema outputSchema, String description, UnaryOperator<String> renames) {
		super(outputSchema, false);
		this.description = description;
		this.renames = renames;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<?> opticFinder = type.findField("tag");
		return TypeRewriteRule.seq(
			this.fixTypeEverywhereTyped(
				this.description + " (ItemStack)", type, itemStackTyped -> itemStackTyped.updateTyped(opticFinder, this::updateAttributeModifiers)
			),
			this.fixTypeEverywhereTyped(this.description + " (Entity)", this.getInputSchema().getType(TypeReferences.ENTITY), this::updateEntityAttributes),
			this.fixTypeEverywhereTyped(this.description + " (Player)", this.getInputSchema().getType(TypeReferences.PLAYER), this::updateEntityAttributes)
		);
	}

	private Dynamic<?> updateAttributeName(Dynamic<?> attributeNameDynamic) {
		return DataFixUtils.orElse(attributeNameDynamic.asString().result().map(this.renames).map(attributeNameDynamic::createString), attributeNameDynamic);
	}

	private Typed<?> updateAttributeModifiers(Typed<?> tagTyped) {
		return tagTyped.update(
			DSL.remainderFinder(),
			tagDynamic -> tagDynamic.update(
					"AttributeModifiers",
					attributeModifiersDynamic -> DataFixUtils.orElse(
							attributeModifiersDynamic.asStreamOpt()
								.result()
								.map(
									attributeModifiers -> attributeModifiers.map(attributeModifierDynamic -> attributeModifierDynamic.update("AttributeName", this::updateAttributeName))
								)
								.map(attributeModifiersDynamic::createList),
							attributeModifiersDynamic
						)
				)
		);
	}

	private Typed<?> updateEntityAttributes(Typed<?> entityTyped) {
		return entityTyped.update(
			DSL.remainderFinder(),
			entityDynamic -> entityDynamic.update(
					"Attributes",
					attributesDynamic -> DataFixUtils.orElse(
							attributesDynamic.asStreamOpt()
								.result()
								.map(attributes -> attributes.map(attributeDynamic -> attributeDynamic.update("Name", this::updateAttributeName)))
								.map(attributesDynamic::createList),
							attributesDynamic
						)
				)
		);
	}
}
