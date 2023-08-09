package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

public class SetAttributesLootFunction extends ConditionalLootFunction {
	public static final Codec<SetAttributesLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> method_53344(instance)
				.and(
					Codecs.nonEmptyList(SetAttributesLootFunction.Attribute.CODEC.listOf())
						.fieldOf("modifiers")
						.forGetter(setAttributesLootFunction -> setAttributesLootFunction.attributes)
				)
				.apply(instance, SetAttributesLootFunction::new)
	);
	private final List<SetAttributesLootFunction.Attribute> attributes;

	SetAttributesLootFunction(List<LootCondition> conditions, List<SetAttributesLootFunction.Attribute> attributes) {
		super(conditions);
		this.attributes = List.copyOf(attributes);
	}

	@Override
	public LootFunctionType getType() {
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
		Random random = context.getRandom();

		for (SetAttributesLootFunction.Attribute attribute : this.attributes) {
			UUID uUID = (UUID)attribute.id.orElseGet(UUID::randomUUID);
			EquipmentSlot equipmentSlot = Util.getRandom(attribute.slots, random);
			stack.addAttributeModifier(
				attribute.attribute.value(),
				new EntityAttributeModifier(uUID, attribute.name, (double)attribute.amount.nextFloat(context), attribute.operation),
				equipmentSlot
			);
		}

		return stack;
	}

	public static SetAttributesLootFunction.AttributeBuilder attributeBuilder(
		String name, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier.Operation operation, LootNumberProvider amountRange
	) {
		return new SetAttributesLootFunction.AttributeBuilder(name, attribute, operation, amountRange);
	}

	public static SetAttributesLootFunction.Builder builder() {
		return new SetAttributesLootFunction.Builder();
	}

	static record Attribute(
		String name,
		RegistryEntry<EntityAttribute> attribute,
		EntityAttributeModifier.Operation operation,
		LootNumberProvider amount,
		List<EquipmentSlot> slots,
		Optional<UUID> id
	) {
		private static final Codec<List<EquipmentSlot>> EQUIPMENT_SLOT_LIST_CODEC = Codecs.nonEmptyList(
			Codec.either(EquipmentSlot.CODEC, EquipmentSlot.CODEC.listOf())
				.xmap(either -> either.map(List::of, Function.identity()), list -> list.size() == 1 ? Either.left((EquipmentSlot)list.get(0)) : Either.right(list))
		);
		public static final Codec<SetAttributesLootFunction.Attribute> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.STRING.fieldOf("name").forGetter(SetAttributesLootFunction.Attribute::name),
						Registries.ATTRIBUTE.createEntryCodec().fieldOf("attribute").forGetter(SetAttributesLootFunction.Attribute::attribute),
						EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(SetAttributesLootFunction.Attribute::operation),
						LootNumberProviderTypes.CODEC.fieldOf("amount").forGetter(SetAttributesLootFunction.Attribute::amount),
						EQUIPMENT_SLOT_LIST_CODEC.fieldOf("slot").forGetter(SetAttributesLootFunction.Attribute::slots),
						Codecs.createStrictOptionalFieldCodec(Uuids.STRING_CODEC, "id").forGetter(SetAttributesLootFunction.Attribute::id)
					)
					.apply(instance, SetAttributesLootFunction.Attribute::new)
		);
	}

	public static class AttributeBuilder {
		private final String name;
		private final RegistryEntry<EntityAttribute> attribute;
		private final EntityAttributeModifier.Operation operation;
		private final LootNumberProvider amount;
		private Optional<UUID> uuid = Optional.empty();
		private final Set<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);

		public AttributeBuilder(String name, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier.Operation operation, LootNumberProvider amount) {
			this.name = name;
			this.attribute = attribute;
			this.operation = operation;
			this.amount = amount;
		}

		public SetAttributesLootFunction.AttributeBuilder slot(EquipmentSlot slot) {
			this.slots.add(slot);
			return this;
		}

		public SetAttributesLootFunction.AttributeBuilder uuid(UUID uuid) {
			this.uuid = Optional.of(uuid);
			return this;
		}

		public SetAttributesLootFunction.Attribute build() {
			return new SetAttributesLootFunction.Attribute(this.name, this.attribute, this.operation, this.amount, List.copyOf(this.slots), this.uuid);
		}
	}

	public static class Builder extends ConditionalLootFunction.Builder<SetAttributesLootFunction.Builder> {
		private final List<SetAttributesLootFunction.Attribute> attributes = Lists.<SetAttributesLootFunction.Attribute>newArrayList();

		protected SetAttributesLootFunction.Builder getThisBuilder() {
			return this;
		}

		public SetAttributesLootFunction.Builder attribute(SetAttributesLootFunction.AttributeBuilder attribute) {
			this.attributes.add(attribute.build());
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetAttributesLootFunction(this.getConditions(), this.attributes);
		}
	}
}
