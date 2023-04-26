package net.minecraft.potion;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class PotionUtil {
	public static final String CUSTOM_POTION_EFFECTS_KEY = "CustomPotionEffects";
	public static final String CUSTOM_POTION_COLOR_KEY = "CustomPotionColor";
	public static final String POTION_KEY = "Potion";
	private static final int DEFAULT_COLOR = 16253176;
	private static final Text NONE_TEXT = Text.translatable("effect.none").formatted(Formatting.GRAY);

	public static List<StatusEffectInstance> getPotionEffects(ItemStack stack) {
		return getPotionEffects(stack.getNbt());
	}

	public static List<StatusEffectInstance> getPotionEffects(Potion potion, Collection<StatusEffectInstance> custom) {
		List<StatusEffectInstance> list = Lists.<StatusEffectInstance>newArrayList();
		list.addAll(potion.getEffects());
		list.addAll(custom);
		return list;
	}

	public static List<StatusEffectInstance> getPotionEffects(@Nullable NbtCompound nbt) {
		List<StatusEffectInstance> list = Lists.<StatusEffectInstance>newArrayList();
		list.addAll(getPotion(nbt).getEffects());
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
		if (nbt != null && nbt.contains("CustomPotionEffects", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("CustomPotionEffects", NbtElement.COMPOUND_TYPE);

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
			return getPotion(stack) == Potions.EMPTY ? 16253176 : getColor(getPotionEffects(stack));
		}
	}

	public static int getColor(Potion potion) {
		return potion == Potions.EMPTY ? 16253176 : getColor(potion.getEffects());
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
					int k = statusEffectInstance.getEffectType().getColor();
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

	public static Potion getPotion(ItemStack stack) {
		return getPotion(stack.getNbt());
	}

	public static Potion getPotion(@Nullable NbtCompound compound) {
		return compound == null ? Potions.EMPTY : Potion.byId(compound.getString("Potion"));
	}

	public static ItemStack setPotion(ItemStack stack, Potion potion) {
		Identifier identifier = Registries.POTION.getId(potion);
		if (potion == Potions.EMPTY) {
			stack.removeSubNbt("Potion");
		} else {
			stack.getOrCreateNbt().putString("Potion", identifier.toString());
		}

		return stack;
	}

	public static ItemStack setCustomPotionEffects(ItemStack stack, Collection<StatusEffectInstance> effects) {
		if (effects.isEmpty()) {
			return stack;
		} else {
			NbtCompound nbtCompound = stack.getOrCreateNbt();
			NbtList nbtList = nbtCompound.getList("CustomPotionEffects", NbtElement.LIST_TYPE);

			for (StatusEffectInstance statusEffectInstance : effects) {
				nbtList.add(statusEffectInstance.writeNbt(new NbtCompound()));
			}

			nbtCompound.put("CustomPotionEffects", nbtList);
			return stack;
		}
	}

	public static void buildTooltip(ItemStack stack, List<Text> list, float durationMultiplier) {
		buildTooltip(getPotionEffects(stack), list, durationMultiplier);
	}

	public static void buildTooltip(List<StatusEffectInstance> statusEffects, List<Text> list, float durationMultiplier) {
		List<Pair<EntityAttribute, EntityAttributeModifier>> list2 = Lists.<Pair<EntityAttribute, EntityAttributeModifier>>newArrayList();
		if (statusEffects.isEmpty()) {
			list.add(NONE_TEXT);
		} else {
			for (StatusEffectInstance statusEffectInstance : statusEffects) {
				MutableText mutableText = Text.translatable(statusEffectInstance.getTranslationKey());
				StatusEffect statusEffect = statusEffectInstance.getEffectType();
				Map<EntityAttribute, EntityAttributeModifier> map = statusEffect.getAttributeModifiers();
				if (!map.isEmpty()) {
					for (Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
						EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)entry.getValue();
						EntityAttributeModifier entityAttributeModifier2 = new EntityAttributeModifier(
							entityAttributeModifier.getName(),
							statusEffect.adjustModifierAmount(statusEffectInstance.getAmplifier(), entityAttributeModifier),
							entityAttributeModifier.getOperation()
						);
						list2.add(new Pair<>((EntityAttribute)entry.getKey(), entityAttributeModifier2));
					}
				}

				if (statusEffectInstance.getAmplifier() > 0) {
					mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + statusEffectInstance.getAmplifier()));
				}

				if (!statusEffectInstance.isDurationBelow(20)) {
					mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.getDurationText(statusEffectInstance, durationMultiplier));
				}

				list.add(mutableText.formatted(statusEffect.getCategory().getFormatting()));
			}
		}

		if (!list2.isEmpty()) {
			list.add(ScreenTexts.EMPTY);
			list.add(Text.translatable("potion.whenDrank").formatted(Formatting.DARK_PURPLE));

			for (Pair<EntityAttribute, EntityAttributeModifier> pair : list2) {
				EntityAttributeModifier entityAttributeModifier3 = pair.getSecond();
				double d = entityAttributeModifier3.getValue();
				double e;
				if (entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE
					&& entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
					e = entityAttributeModifier3.getValue();
				} else {
					e = entityAttributeModifier3.getValue() * 100.0;
				}

				if (d > 0.0) {
					list.add(
						Text.translatable(
								"attribute.modifier.plus." + entityAttributeModifier3.getOperation().getId(),
								ItemStack.MODIFIER_FORMAT.format(e),
								Text.translatable(pair.getFirst().getTranslationKey())
							)
							.formatted(Formatting.BLUE)
					);
				} else if (d < 0.0) {
					e *= -1.0;
					list.add(
						Text.translatable(
								"attribute.modifier.take." + entityAttributeModifier3.getOperation().getId(),
								ItemStack.MODIFIER_FORMAT.format(e),
								Text.translatable(pair.getFirst().getTranslationKey())
							)
							.formatted(Formatting.RED)
					);
				}
			}
		}
	}
}
