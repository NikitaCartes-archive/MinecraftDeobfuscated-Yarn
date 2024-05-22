package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.collection.CollectionPredicate;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;

public record AttributeModifiersPredicate(
	Optional<CollectionPredicate<AttributeModifiersComponent.Entry, AttributeModifiersPredicate.AttributeModifierPredicate>> modifiers
) implements ComponentSubPredicate<AttributeModifiersComponent> {
	public static final Codec<AttributeModifiersPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					CollectionPredicate.createCodec(AttributeModifiersPredicate.AttributeModifierPredicate.CODEC)
						.optionalFieldOf("modifiers")
						.forGetter(AttributeModifiersPredicate::modifiers)
				)
				.apply(instance, AttributeModifiersPredicate::new)
	);

	@Override
	public ComponentType<AttributeModifiersComponent> getComponentType() {
		return DataComponentTypes.ATTRIBUTE_MODIFIERS;
	}

	public boolean test(ItemStack itemStack, AttributeModifiersComponent attributeModifiersComponent) {
		return !this.modifiers.isPresent() || ((CollectionPredicate)this.modifiers.get()).test((Iterable)attributeModifiersComponent.modifiers());
	}

	public static record AttributeModifierPredicate(
		Optional<RegistryEntryList<EntityAttribute>> attribute,
		Optional<Identifier> id,
		NumberRange.DoubleRange amount,
		Optional<EntityAttributeModifier.Operation> operation,
		Optional<AttributeModifierSlot> slot
	) implements Predicate<AttributeModifiersComponent.Entry> {
		public static final Codec<AttributeModifiersPredicate.AttributeModifierPredicate> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						RegistryCodecs.entryList(RegistryKeys.ATTRIBUTE)
							.optionalFieldOf("attribute")
							.forGetter(AttributeModifiersPredicate.AttributeModifierPredicate::attribute),
						Identifier.CODEC.optionalFieldOf("id").forGetter(AttributeModifiersPredicate.AttributeModifierPredicate::id),
						NumberRange.DoubleRange.CODEC
							.optionalFieldOf("amount", NumberRange.DoubleRange.ANY)
							.forGetter(AttributeModifiersPredicate.AttributeModifierPredicate::amount),
						EntityAttributeModifier.Operation.CODEC.optionalFieldOf("operation").forGetter(AttributeModifiersPredicate.AttributeModifierPredicate::operation),
						AttributeModifierSlot.CODEC.optionalFieldOf("slot").forGetter(AttributeModifiersPredicate.AttributeModifierPredicate::slot)
					)
					.apply(instance, AttributeModifiersPredicate.AttributeModifierPredicate::new)
		);

		public boolean test(AttributeModifiersComponent.Entry entry) {
			if (this.attribute.isPresent() && !((RegistryEntryList)this.attribute.get()).contains(entry.attribute())) {
				return false;
			} else if (this.id.isPresent() && !((Identifier)this.id.get()).equals(entry.modifier().id())) {
				return false;
			} else if (!this.amount.test(entry.modifier().value())) {
				return false;
			} else {
				return this.operation.isPresent() && this.operation.get() != entry.modifier().operation()
					? false
					: !this.slot.isPresent() || this.slot.get() == entry.slot();
			}
		}
	}
}
