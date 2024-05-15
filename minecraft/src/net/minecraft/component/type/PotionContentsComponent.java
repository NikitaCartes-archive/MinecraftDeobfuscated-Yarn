package net.minecraft.component.type;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;

public record PotionContentsComponent(Optional<RegistryEntry<Potion>> potion, Optional<Integer> customColor, List<StatusEffectInstance> customEffects) {
	public static final PotionContentsComponent DEFAULT = new PotionContentsComponent(Optional.empty(), Optional.empty(), List.of());
	private static final Text NONE_TEXT = Text.translatable("effect.none").formatted(Formatting.GRAY);
	private static final int EFFECTLESS_COLOR = -13083194;
	private static final Codec<PotionContentsComponent> BASE_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Potion.CODEC.optionalFieldOf("potion").forGetter(PotionContentsComponent::potion),
					Codec.INT.optionalFieldOf("custom_color").forGetter(PotionContentsComponent::customColor),
					StatusEffectInstance.CODEC.listOf().optionalFieldOf("custom_effects", List.of()).forGetter(PotionContentsComponent::customEffects)
				)
				.apply(instance, PotionContentsComponent::new)
	);
	public static final Codec<PotionContentsComponent> CODEC = Codec.withAlternative(BASE_CODEC, Potion.CODEC, PotionContentsComponent::new);
	public static final PacketCodec<RegistryByteBuf, PotionContentsComponent> PACKET_CODEC = PacketCodec.tuple(
		Potion.PACKET_CODEC.collect(PacketCodecs::optional),
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
		return new PotionContentsComponent(this.potion, this.customColor, Util.withAppended(this.customEffects, customEffect));
	}

	public int getColor() {
		return this.customColor.isPresent() ? (Integer)this.customColor.get() : getColor(this.getEffects());
	}

	public static int getColor(RegistryEntry<Potion> potion) {
		return getColor(potion.value().getEffects());
	}

	public static int getColor(Iterable<StatusEffectInstance> effects) {
		return mixColors(effects).orElse(-13083194);
	}

	public static OptionalInt mixColors(Iterable<StatusEffectInstance> effects) {
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;

		for (StatusEffectInstance statusEffectInstance : effects) {
			if (statusEffectInstance.shouldShowParticles()) {
				int m = statusEffectInstance.getEffectType().value().getColor();
				int n = statusEffectInstance.getAmplifier() + 1;
				i += n * ColorHelper.Argb.getRed(m);
				j += n * ColorHelper.Argb.getGreen(m);
				k += n * ColorHelper.Argb.getBlue(m);
				l += n;
			}
		}

		return l == 0 ? OptionalInt.empty() : OptionalInt.of(ColorHelper.Argb.getArgb(i / l, j / l, k / l));
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
				double d = entityAttributeModifier.value();
				double e;
				if (entityAttributeModifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
					&& entityAttributeModifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
					e = entityAttributeModifier.value();
				} else {
					e = entityAttributeModifier.value() * 100.0;
				}

				if (d > 0.0) {
					textConsumer.accept(
						Text.translatable(
								"attribute.modifier.plus." + entityAttributeModifier.operation().getId(),
								AttributeModifiersComponent.DECIMAL_FORMAT.format(e),
								Text.translatable(pair.getFirst().value().getTranslationKey())
							)
							.formatted(Formatting.BLUE)
					);
				} else if (d < 0.0) {
					e *= -1.0;
					textConsumer.accept(
						Text.translatable(
								"attribute.modifier.take." + entityAttributeModifier.operation().getId(),
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
