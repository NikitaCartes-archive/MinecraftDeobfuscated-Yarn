package net.minecraft.potion;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PotionUtil {
	public static final String CUSTOM_POTION_EFFECTS_KEY = "custom_potion_effects";
	public static final String CUSTOM_POTION_COLOR_KEY = "CustomPotionColor";
	public static final String POTION_KEY = "Potion";
	private static final int DEFAULT_COLOR = 16253176;
	private static final Text NONE_TEXT = Text.translatable("effect.none").formatted(Formatting.GRAY);

	public static List<StatusEffectInstance> getPotionEffects(ItemStack stack) {
		return getPotionEffects(stack.getNbt());
	}

	public static List<StatusEffectInstance> getPotionEffects(RegistryEntry<Potion> potion, Collection<StatusEffectInstance> custom) {
		List<StatusEffectInstance> list = new ArrayList();
		list.addAll(potion.value().getEffects());
		list.addAll(custom);
		return list;
	}

	public static List<StatusEffectInstance> getPotionEffects(@Nullable NbtCompound nbt) {
		List<StatusEffectInstance> list = Lists.<StatusEffectInstance>newArrayList();
		list.addAll(getPotion(nbt).value().getEffects());
		getCustomPotionEffects(nbt, list);
		return list;
	}

	public static List<StatusEffectInstance> getCustomPotionEffects(ItemStack stack) {
		return getCustomPotionEffects(stack.getNbt());
	}

	public static List<StatusEffectInstance> getCustomPotionEffects(@Nullable NbtCompound nbt) {
		List<StatusEffectInstance> list = Lists.<StatusEffectInstance>newArrayList();
		getCustomPotionEffects(nbt, list);
		return list;
	}

	public static void getCustomPotionEffects(@Nullable NbtCompound nbt, List<StatusEffectInstance> list) {
		if (nbt != null && nbt.contains("custom_potion_effects", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("custom_potion_effects", NbtElement.COMPOUND_TYPE);

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtCompound);
				if (statusEffectInstance != null) {
					list.add(statusEffectInstance);
				}
			}
		}
	}

	public static int getColor(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("CustomPotionColor", NbtElement.NUMBER_TYPE)) {
			return nbtCompound.getInt("CustomPotionColor");
		} else {
			return getPotion(stack).matches(Potions.EMPTY) ? 16253176 : getColor(getPotionEffects(stack));
		}
	}

	public static int getColor(RegistryEntry<Potion> potion) {
		return potion.matches(Potions.EMPTY) ? 16253176 : getColor(potion.value().getEffects());
	}

	public static int getColor(Collection<StatusEffectInstance> effects) {
		int i = 3694022;
		if (effects.isEmpty()) {
			return 3694022;
		} else {
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			int j = 0;

			for (StatusEffectInstance statusEffectInstance : effects) {
				if (statusEffectInstance.shouldShowParticles()) {
					int k = statusEffectInstance.getEffectType().value().getColor();
					int l = statusEffectInstance.getAmplifier() + 1;
					f += (float)(l * (k >> 16 & 0xFF)) / 255.0F;
					g += (float)(l * (k >> 8 & 0xFF)) / 255.0F;
					h += (float)(l * (k >> 0 & 0xFF)) / 255.0F;
					j += l;
				}
			}

			if (j == 0) {
				return 0;
			} else {
				f = f / (float)j * 255.0F;
				g = g / (float)j * 255.0F;
				h = h / (float)j * 255.0F;
				return (int)f << 16 | (int)g << 8 | (int)h;
			}
		}
	}

	public static RegistryEntry<Potion> getPotion(ItemStack stack) {
		return getPotion(stack.getNbt());
	}

	public static RegistryEntry<Potion> getPotion(@Nullable NbtCompound compound) {
		return compound == null ? Potions.EMPTY : Potion.byId(compound.getString("Potion"));
	}

	public static ItemStack setPotion(ItemStack stack, RegistryEntry<Potion> potion) {
		Optional<RegistryKey<Potion>> optional = potion.getKey();
		if (!optional.isEmpty() && !potion.matches(Potions.EMPTY)) {
			stack.getOrCreateNbt().putString("Potion", ((RegistryKey)optional.get()).getValue().toString());
		} else {
			stack.removeSubNbt("Potion");
		}

		return stack;
	}

	public static ItemStack setCustomPotionEffects(ItemStack stack, Collection<StatusEffectInstance> effects) {
		if (effects.isEmpty()) {
			return stack;
		} else {
			NbtCompound nbtCompound = stack.getOrCreateNbt();
			NbtList nbtList = nbtCompound.getList("custom_potion_effects", NbtElement.LIST_TYPE);

			for (StatusEffectInstance statusEffectInstance : effects) {
				nbtList.add(statusEffectInstance.writeNbt());
			}

			nbtCompound.put("custom_potion_effects", nbtList);
			return stack;
		}
	}

	public static void buildTooltip(ItemStack stack, List<Text> list, float durationMultiplier, float tickRate) {
		buildTooltip(getPotionEffects(stack), list, durationMultiplier, tickRate);
	}

	public static void buildTooltip(List<StatusEffectInstance> statusEffects, List<Text> list, float durationMultiplier, float tickRate) {
		List<Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier>> list2 = Lists.<Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier>>newArrayList();
		if (statusEffects.isEmpty()) {
			list.add(NONE_TEXT);
		} else {
			for (StatusEffectInstance statusEffectInstance : statusEffects) {
				MutableText mutableText = Text.translatable(statusEffectInstance.getTranslationKey());
				RegistryEntry<StatusEffect> registryEntry = statusEffectInstance.getEffectType();
				registryEntry.value().forEachAttributeModifier(statusEffectInstance.getAmplifier(), (attribute, modifier) -> list2.add(new Pair<>(attribute, modifier)));
				if (statusEffectInstance.getAmplifier() > 0) {
					mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + statusEffectInstance.getAmplifier()));
				}

				if (!statusEffectInstance.isDurationBelow(20)) {
					mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.getDurationText(statusEffectInstance, durationMultiplier, tickRate));
				}

				list.add(mutableText.formatted(registryEntry.value().getCategory().getFormatting()));
			}
		}

		if (!list2.isEmpty()) {
			list.add(ScreenTexts.EMPTY);
			list.add(Text.translatable("potion.whenDrank").formatted(Formatting.DARK_PURPLE));

			for (Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier> pair : list2) {
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
					list.add(
						Text.translatable(
								"attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(),
								ItemStack.MODIFIER_FORMAT.format(e),
								Text.translatable(pair.getFirst().value().getTranslationKey())
							)
							.formatted(Formatting.BLUE)
					);
				} else if (d < 0.0) {
					e *= -1.0;
					list.add(
						Text.translatable(
								"attribute.modifier.take." + entityAttributeModifier.getOperation().getId(),
								ItemStack.MODIFIER_FORMAT.format(e),
								Text.translatable(pair.getFirst().value().getTranslationKey())
							)
							.formatted(Formatting.RED)
					);
				}
			}
		}
	}
}
