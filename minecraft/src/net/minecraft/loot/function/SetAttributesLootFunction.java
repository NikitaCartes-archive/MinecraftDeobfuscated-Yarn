package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

public class SetAttributesLootFunction extends ConditionalLootFunction {
	public static final MapCodec<SetAttributesLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<List<SetAttributesLootFunction.Attribute>, boolean>and(
					instance.group(
						SetAttributesLootFunction.Attribute.CODEC.listOf().fieldOf("modifiers").forGetter(function -> function.attributes),
						Codec.BOOL.optionalFieldOf("replace", Boolean.valueOf(true)).forGetter(lootFunction -> lootFunction.replace)
					)
				)
				.apply(instance, SetAttributesLootFunction::new)
	);
	private final List<SetAttributesLootFunction.Attribute> attributes;
	private final boolean replace;

	SetAttributesLootFunction(List<LootCondition> conditions, List<SetAttributesLootFunction.Attribute> attributes, boolean replace) {
		super(conditions);
		this.attributes = List.copyOf(attributes);
		this.replace = replace;
	}

	@Override
	public LootFunctionType<SetAttributesLootFunction> getType() {
		return LootFunctionTypes.SET_ATTRIBUTES;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return (Set<LootContextParameter<?>>)this.attributes
			.stream()
			.flatMap(attribute -> attribute.amount.getRequiredParameters().stream())
			.collect(ImmutableSet.toImmutableSet());
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (this.replace) {
			stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, this.applyTo(context, AttributeModifiersComponent.DEFAULT));
		} else {
			stack.apply(
				DataComponentTypes.ATTRIBUTE_MODIFIERS,
				AttributeModifiersComponent.DEFAULT,
				component -> component.modifiers().isEmpty() ? this.applyTo(context, stack.getItem().getAttributeModifiers()) : this.applyTo(context, component)
			);
		}

		return stack;
	}

	private AttributeModifiersComponent applyTo(LootContext context, AttributeModifiersComponent attributeModifiersComponent) {
		Random random = context.getRandom();

		for (SetAttributesLootFunction.Attribute attribute : this.attributes) {
			AttributeModifierSlot attributeModifierSlot = Util.getRandom(attribute.slots, random);
			attributeModifiersComponent = attributeModifiersComponent.with(
				attribute.attribute, new EntityAttributeModifier(attribute.id, (double)attribute.amount.nextFloat(context), attribute.operation), attributeModifierSlot
			);
		}

		return attributeModifiersComponent;
	}

	public static SetAttributesLootFunction.AttributeBuilder attributeBuilder(
		Identifier id, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier.Operation operation, LootNumberProvider amountRange
	) {
		return new SetAttributesLootFunction.AttributeBuilder(id, attribute, operation, amountRange);
	}

	public static SetAttributesLootFunction.Builder builder() {
		return new SetAttributesLootFunction.Builder();
	}

	static record Attribute(
		Identifier id,
		RegistryEntry<EntityAttribute> attribute,
		EntityAttributeModifier.Operation operation,
		LootNumberProvider amount,
		List<AttributeModifierSlot> slots
	) {
		private static final Codec<List<AttributeModifierSlot>> EQUIPMENT_SLOT_LIST_CODEC = Codecs.nonEmptyList(
			Codec.either(AttributeModifierSlot.CODEC, AttributeModifierSlot.CODEC.listOf())
				.xmap(
					either -> either.map(List::of, Function.identity()),
					slots -> slots.size() == 1 ? Either.left((AttributeModifierSlot)slots.getFirst()) : Either.right(slots)
				)
		);
		public static final Codec<SetAttributesLootFunction.Attribute> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Identifier.CODEC.fieldOf("id").forGetter(SetAttributesLootFunction.Attribute::id),
						EntityAttribute.CODEC.fieldOf("attribute").forGetter(SetAttributesLootFunction.Attribute::attribute),
						EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(SetAttributesLootFunction.Attribute::operation),
						LootNumberProviderTypes.CODEC.fieldOf("amount").forGetter(SetAttributesLootFunction.Attribute::amount),
						EQUIPMENT_SLOT_LIST_CODEC.fieldOf("slot").forGetter(SetAttributesLootFunction.Attribute::slots)
					)
					.apply(instance, SetAttributesLootFunction.Attribute::new)
		);
	}

	public static class AttributeBuilder {
		private final Identifier id;
		private final RegistryEntry<EntityAttribute> attribute;
		private final EntityAttributeModifier.Operation operation;
		private final LootNumberProvider amount;
		private final Set<AttributeModifierSlot> slots = EnumSet.noneOf(AttributeModifierSlot.class);

		public AttributeBuilder(Identifier id, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier.Operation operation, LootNumberProvider amount) {
			this.id = id;
			this.attribute = attribute;
			this.operation = operation;
			this.amount = amount;
		}

		public SetAttributesLootFunction.AttributeBuilder slot(AttributeModifierSlot slot) {
			this.slots.add(slot);
			return this;
		}

		public SetAttributesLootFunction.Attribute build() {
			return new SetAttributesLootFunction.Attribute(this.id, this.attribute, this.operation, this.amount, List.copyOf(this.slots));
		}
	}

	public static class Builder extends ConditionalLootFunction.Builder<SetAttributesLootFunction.Builder> {
		private final boolean replace;
		private final List<SetAttributesLootFunction.Attribute> attributes = Lists.<SetAttributesLootFunction.Attribute>newArrayList();

		public Builder(boolean replace) {
			this.replace = replace;
		}

		public Builder() {
			this(false);
		}

		protected SetAttributesLootFunction.Builder getThisBuilder() {
			return this;
		}

		public SetAttributesLootFunction.Builder attribute(SetAttributesLootFunction.AttributeBuilder attribute) {
			this.attributes.add(attribute.build());
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetAttributesLootFunction(this.getConditions(), this.attributes, this.replace);
		}
	}
}
