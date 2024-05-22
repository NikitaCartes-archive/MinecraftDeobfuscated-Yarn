package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class ItemStackUuidFix extends AbstractUuidFix {
	public ItemStackUuidFix(Schema outputSchema) {
		super(outputSchema, TypeReferences.ITEM_STACK);
	}

	@Override
	public TypeRewriteRule makeRule() {
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder(
			"id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType())
		);
		return this.fixTypeEverywhereTyped("ItemStackUUIDFix", this.getInputSchema().getType(this.typeReference), itemStackTyped -> {
			OpticFinder<?> opticFinder2 = itemStackTyped.getType().findField("tag");
			return itemStackTyped.updateTyped(opticFinder2, tagTyped -> tagTyped.update(DSL.remainderFinder(), tagDynamic -> {
					tagDynamic = this.fixAttributeModifiers(tagDynamic);
					if ((Boolean)itemStackTyped.getOptional(opticFinder).map(id -> "minecraft:player_head".equals(id.getSecond())).orElse(false)) {
						tagDynamic = this.fixSkullOwner(tagDynamic);
					}

					return tagDynamic;
				}));
		});
	}

	private Dynamic<?> fixAttributeModifiers(Dynamic<?> tagDynamic) {
		return tagDynamic.update(
			"AttributeModifiers",
			attributeModifiersDynamic -> tagDynamic.createList(
					attributeModifiersDynamic.asStream()
						.map(attributeModifier -> (Dynamic)updateRegularMostLeast(attributeModifier, "UUID", "UUID").orElse(attributeModifier))
				)
		);
	}

	private Dynamic<?> fixSkullOwner(Dynamic<?> tagDynamic) {
		return tagDynamic.update("SkullOwner", skullOwner -> (Dynamic)updateStringUuid(skullOwner, "Id", "Id").orElse(skullOwner));
	}
}
