package net.minecraft.component.type;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public record AttributeModifiersComponent(List<AttributeModifiersComponent.Entry> modifiers, boolean showInTooltip) {
	public static final AttributeModifiersComponent DEFAULT = new AttributeModifiersComponent(List.of(), true);
	private static final Codec<AttributeModifiersComponent> BASE_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					AttributeModifiersComponent.Entry.CODEC.listOf().fieldOf("modifiers").forGetter(AttributeModifiersComponent::modifiers),
					Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.valueOf(true)).forGetter(AttributeModifiersComponent::showInTooltip)
				)
				.apply(instance, AttributeModifiersComponent::new)
	);
	public static final Codec<AttributeModifiersComponent> CODEC = Codec.withAlternative(
		BASE_CODEC, AttributeModifiersComponent.Entry.CODEC.listOf(), attributeModifiers -> new AttributeModifiersComponent(attributeModifiers, true)
	);
	public static final PacketCodec<RegistryByteBuf, AttributeModifiersComponent> PACKET_CODEC = PacketCodec.tuple(
		AttributeModifiersComponent.Entry.PACKET_CODEC.collect(PacketCodecs.toList()),
		AttributeModifiersComponent::modifiers,
		PacketCodecs.BOOL,
		AttributeModifiersComponent::showInTooltip,
		AttributeModifiersComponent::new
	);
	public static final DecimalFormat DECIMAL_FORMAT = Util.make(
		new DecimalFormat("#.##"), format -> format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT))
	);

	public AttributeModifiersComponent withShowInTooltip(boolean showInTooltip) {
		return new AttributeModifiersComponent(this.modifiers, showInTooltip);
	}

	public static AttributeModifiersComponent.Builder builder() {
		return new AttributeModifiersComponent.Builder();
	}

	public AttributeModifiersComponent with(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, AttributeModifierSlot slot) {
		ImmutableList.Builder<AttributeModifiersComponent.Entry> builder = ImmutableList.builderWithExpectedSize(this.modifiers.size() + 1);

		for (AttributeModifiersComponent.Entry entry : this.modifiers) {
			if (!entry.matches(attribute, modifier.id())) {
				builder.add(entry);
			}
		}

		builder.add(new AttributeModifiersComponent.Entry(attribute, modifier, slot));
		return new AttributeModifiersComponent(builder.build(), this.showInTooltip);
	}

	public void applyModifiers(AttributeModifierSlot slot, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeConsumer) {
		for (AttributeModifiersComponent.Entry entry : this.modifiers) {
			if (entry.slot.equals(slot)) {
				attributeConsumer.accept(entry.attribute, entry.modifier);
			}
		}
	}

	public void applyModifiers(EquipmentSlot slot, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeConsumer) {
		for (AttributeModifiersComponent.Entry entry : this.modifiers) {
			if (entry.slot.matches(slot)) {
				attributeConsumer.accept(entry.attribute, entry.modifier);
			}
		}
	}

	public double applyOperations(double base, EquipmentSlot slot) {
		double d = base;

		for (AttributeModifiersComponent.Entry entry : this.modifiers) {
			if (entry.slot.matches(slot)) {
				double e = entry.modifier.value();

				d += switch (entry.modifier.operation()) {
					case ADD_VALUE -> e;
					case ADD_MULTIPLIED_BASE -> e * base;
					case ADD_MULTIPLIED_TOTAL -> e * d;
				};
			}
		}

		return d;
	}

	public static class Builder {
		private final ImmutableList.Builder<AttributeModifiersComponent.Entry> entries = ImmutableList.builder();

		Builder() {
		}

		public AttributeModifiersComponent.Builder add(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, AttributeModifierSlot slot) {
			this.entries.add(new AttributeModifiersComponent.Entry(attribute, modifier, slot));
			return this;
		}

		public AttributeModifiersComponent build() {
			return new AttributeModifiersComponent(this.entries.build(), true);
		}
	}

	public static record Entry(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, AttributeModifierSlot slot) {
		public static final Codec<AttributeModifiersComponent.Entry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityAttribute.CODEC.fieldOf("type").forGetter(AttributeModifiersComponent.Entry::attribute),
						EntityAttributeModifier.MAP_CODEC.forGetter(AttributeModifiersComponent.Entry::modifier),
						AttributeModifierSlot.CODEC.optionalFieldOf("slot", AttributeModifierSlot.ANY).forGetter(AttributeModifiersComponent.Entry::slot)
					)
					.apply(instance, AttributeModifiersComponent.Entry::new)
		);
		public static final PacketCodec<RegistryByteBuf, AttributeModifiersComponent.Entry> PACKET_CODEC = PacketCodec.tuple(
			EntityAttribute.PACKET_CODEC,
			AttributeModifiersComponent.Entry::attribute,
			EntityAttributeModifier.PACKET_CODEC,
			AttributeModifiersComponent.Entry::modifier,
			AttributeModifierSlot.PACKET_CODEC,
			AttributeModifiersComponent.Entry::slot,
			AttributeModifiersComponent.Entry::new
		);

		public boolean matches(RegistryEntry<EntityAttribute> attribute, Identifier modifierId) {
			return attribute.equals(this.attribute) && this.modifier.idMatches(modifierId);
		}
	}
}
