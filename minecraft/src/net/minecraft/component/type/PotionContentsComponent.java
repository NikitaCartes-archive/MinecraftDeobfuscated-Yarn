package net.minecraft.component.type;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;

public record PotionContentsComponent(Optional<RegistryEntry<Potion>> potion, Optional<Integer> customColor, List<StatusEffectInstance> customEffects) {
	public static final PotionContentsComponent DEFAULT = new PotionContentsComponent(Optional.empty(), Optional.empty(), List.of());
	private static final Text NONE_TEXT = Text.translatable("effect.none").formatted(Formatting.GRAY);
	private static final int UNCRAFTABLE_COLOR = 16253176;
	private static final int EFFECTLESS_COLOR = 3694022;
	public static final Codec<PotionContentsComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(Registries.POTION.getEntryCodec(), "potion").forGetter(PotionContentsComponent::potion),
					Codecs.createStrictOptionalFieldCodec(Codec.INT, "custom_color").forGetter(PotionContentsComponent::customColor),
					Codecs.createStrictOptionalFieldCodec(StatusEffectInstance.CODEC.listOf(), "custom_effects", List.of()).forGetter(PotionContentsComponent::customEffects)
				)
				.apply(instance, PotionContentsComponent::new)
	);
	public static final PacketCodec<RegistryByteBuf, PotionContentsComponent> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryEntry(RegistryKeys.POTION).collect(PacketCodecs::optional),
		PotionContentsComponent::potion,
		PacketCodecs.INTEGER.collect(PacketCodecs::optional),
		PotionContentsComponent::customColor,
		StatusEffectInstance.PACKET_CODEC.collect(PacketCodecs.toList()),
		PotionContentsComponent::customEffects,
		PotionContentsComponent::new
	);

	public PotionContentsComponent(RegistryEntry<Potion> potion) {
		this(Optional.of(potion), Optional.empty(), List.of());
	}

	public static ItemStack createStack(Item item, RegistryEntry<Potion> potion) {
		ItemStack itemStack = new ItemStack(item);
		itemStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));
		return itemStack;
	}

	public boolean matches(RegistryEntry<Potion> potion) {
		return this.potion.isPresent() && ((RegistryEntry)this.potion.get()).matches(potion) && this.customEffects.isEmpty();
	}

	public Iterable<StatusEffectInstance> getEffects() {
		if (this.potion.isEmpty()) {
			return this.customEffects;
		} else {
			return (Iterable<StatusEffectInstance>)(this.customEffects.isEmpty()
				? ((Potion)((RegistryEntry)this.potion.get()).value()).getEffects()
				: Iterables.concat(((Potion)((RegistryEntry)this.potion.get()).value()).getEffects(), this.customEffects));
		}
	}

	public void forEachEffect(Consumer<StatusEffectInstance> effectConsumer) {
		if (this.potion.isPresent()) {
			for (StatusEffectInstance statusEffectInstance : ((Potion)((RegistryEntry)this.potion.get()).value()).getEffects()) {
				effectConsumer.accept(new StatusEffectInstance(statusEffectInstance));
			}
		}

		for (StatusEffectInstance statusEffectInstance : this.customEffects) {
			effectConsumer.accept(new StatusEffectInstance(statusEffectInstance));
		}
	}

	public PotionContentsComponent with(RegistryEntry<Potion> potion) {
		return new PotionContentsComponent(Optional.of(potion), this.customColor, this.customEffects);
	}

	public PotionContentsComponent with(StatusEffectInstance customEffect) {
		return new PotionContentsComponent(this.potion, this.customColor, Util.listWith(this.customEffects, customEffect));
	}

	public int getColor() {
		if (this.customColor.isPresent()) {
			return (Integer)this.customColor.get();
		} else {
			return this.potion.isEmpty() ? 16253176 : getColor(this.getEffects());
		}
	}

	public int getCustomColor() {
		return this.customColor.isPresent() ? (Integer)this.customColor.get() : getColor(this.getEffects());
	}

	public static int getColor(RegistryEntry<Potion> potion) {
		return getColor(potion.value().getEffects());
	}

	public static int getColor(Iterable<StatusEffectInstance> effects) {
		float f = 0.0F;
		float g = 0.0F;
		float h = 0.0F;
		int i = 0;

		for (StatusEffectInstance statusEffectInstance : effects) {
			if (statusEffectInstance.shouldShowParticles()) {
				int j = statusEffectInstance.getEffectType().value().getColor();
				int k = statusEffectInstance.getAmplifier() + 1;
				f += (float)(k * ColorHelper.Argb.getRed(j)) / 255.0F;
				g += (float)(k * ColorHelper.Argb.getGreen(j)) / 255.0F;
				h += (float)(k * ColorHelper.Argb.getBlue(j)) / 255.0F;
				i += k;
			}
		}

		return i == 0 ? 3694022 : ColorHelper.Argb.getArgb(0, (int)(f / (float)i * 255.0F), (int)(g / (float)i * 255.0F), (int)(h / (float)i * 255.0F));
	}

	public boolean hasEffects() {
		return !this.customEffects.isEmpty() ? true : this.potion.isPresent() && !((Potion)((RegistryEntry)this.potion.get()).value()).getEffects().isEmpty();
	}

	public List<StatusEffectInstance> customEffects() {
		return Lists.transform(this.customEffects, StatusEffectInstance::new);
	}

	public void buildTooltip(Consumer<Text> textConsumer, float durationMultiplier, float tickRate) {
		buildTooltip(this.getEffects(), textConsumer, durationMultiplier, tickRate);
	}

	public static void buildTooltip(Iterable<StatusEffectInstance> effects, Consumer<Text> textConsumer, float durationMultiplier, float tickRate) {
		List<Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier>> list = Lists.<Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier>>newArrayList();
		boolean bl = true;

		for (StatusEffectInstance statusEffectInstance : effects) {
			bl = false;
			MutableText mutableText = Text.translatable(statusEffectInstance.getTranslationKey());
			RegistryEntry<StatusEffect> registryEntry = statusEffectInstance.getEffectType();
			registryEntry.value().forEachAttributeModifier(statusEffectInstance.getAmplifier(), (attribute, modifier) -> list.add(new Pair<>(attribute, modifier)));
			if (statusEffectInstance.getAmplifier() > 0) {
				mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + statusEffectInstance.getAmplifier()));
			}

			if (!statusEffectInstance.isDurationBelow(20)) {
				mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.getDurationText(statusEffectInstance, durationMultiplier, tickRate));
			}

			textConsumer.accept(mutableText.formatted(registryEntry.value().getCategory().getFormatting()));
		}

		if (bl) {
			textConsumer.accept(NONE_TEXT);
		}

		if (!list.isEmpty()) {
			textConsumer.accept(ScreenTexts.EMPTY);
			textConsumer.accept(Text.translatable("potion.whenDrank").formatted(Formatting.DARK_PURPLE));

			for (Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier> pair : list) {
				EntityAttributeModifier entityAttributeModifier = pair.getSecond();
				double d = entityAttributeModifier.getValue();
				double e;
				if (entityAttributeModifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE
					&& entityAttributeModifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
					e = entityAttributeModifier.getValue();
				} else {
					e = entityAttributeModifier.getValue() * 100.0;
				}

				if (d > 0.0) {
					textConsumer.accept(
						Text.translatable(
								"attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(),
								AttributeModifiersComponent.DECIMAL_FORMAT.format(e),
								Text.translatable(pair.getFirst().value().getTranslationKey())
							)
							.formatted(Formatting.BLUE)
					);
				} else if (d < 0.0) {
					e *= -1.0;
					textConsumer.accept(
						Text.translatable(
								"attribute.modifier.take." + entityAttributeModifier.getOperation().getId(),
								AttributeModifiersComponent.DECIMAL_FORMAT.format(e),
								Text.translatable(pair.getFirst().value().getTranslationKey())
							)
							.formatted(Formatting.RED)
					);
				}
			}
		}
	}
}
