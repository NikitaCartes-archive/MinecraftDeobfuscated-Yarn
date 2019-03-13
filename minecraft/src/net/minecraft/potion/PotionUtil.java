package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

public class PotionUtil {
	public static List<StatusEffectInstance> getPotionEffects(ItemStack itemStack) {
		return method_8066(itemStack.method_7969());
	}

	public static List<StatusEffectInstance> getPotionEffects(Potion potion, Collection<StatusEffectInstance> collection) {
		List<StatusEffectInstance> list = Lists.<StatusEffectInstance>newArrayList();
		list.addAll(potion.getEffects());
		list.addAll(collection);
		return list;
	}

	public static List<StatusEffectInstance> method_8066(@Nullable CompoundTag compoundTag) {
		List<StatusEffectInstance> list = Lists.<StatusEffectInstance>newArrayList();
		list.addAll(method_8057(compoundTag).getEffects());
		method_8058(compoundTag, list);
		return list;
	}

	public static List<StatusEffectInstance> getCustomPotionEffects(ItemStack itemStack) {
		return method_8060(itemStack.method_7969());
	}

	public static List<StatusEffectInstance> method_8060(@Nullable CompoundTag compoundTag) {
		List<StatusEffectInstance> list = Lists.<StatusEffectInstance>newArrayList();
		method_8058(compoundTag, list);
		return list;
	}

	public static void method_8058(@Nullable CompoundTag compoundTag, List<StatusEffectInstance> list) {
		if (compoundTag != null && compoundTag.containsKey("CustomPotionEffects", 9)) {
			ListTag listTag = compoundTag.method_10554("CustomPotionEffects", 10);

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag2 = listTag.getCompoundTag(i);
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.method_5583(compoundTag2);
				if (statusEffectInstance != null) {
					list.add(statusEffectInstance);
				}
			}
		}
	}

	public static int getColor(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.method_7969();
		if (compoundTag != null && compoundTag.containsKey("CustomPotionColor", 99)) {
			return compoundTag.getInt("CustomPotionColor");
		} else {
			return getPotion(itemStack) == Potions.field_8984 ? 16253176 : getColor(getPotionEffects(itemStack));
		}
	}

	public static int getColor(Potion potion) {
		return potion == Potions.field_8984 ? 16253176 : getColor(potion.getEffects());
	}

	public static int getColor(Collection<StatusEffectInstance> collection) {
		int i = 3694022;
		if (collection.isEmpty()) {
			return 3694022;
		} else {
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			int j = 0;

			for (StatusEffectInstance statusEffectInstance : collection) {
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

	public static Potion getPotion(ItemStack itemStack) {
		return method_8057(itemStack.method_7969());
	}

	public static Potion method_8057(@Nullable CompoundTag compoundTag) {
		return compoundTag == null ? Potions.field_8984 : Potion.byId(compoundTag.getString("Potion"));
	}

	public static ItemStack setPotion(ItemStack itemStack, Potion potion) {
		Identifier identifier = Registry.POTION.method_10221(potion);
		if (potion == Potions.field_8984) {
			itemStack.removeSubTag("Potion");
		} else {
			itemStack.method_7948().putString("Potion", identifier.toString());
		}

		return itemStack;
	}

	public static ItemStack setCustomPotionEffects(ItemStack itemStack, Collection<StatusEffectInstance> collection) {
		if (collection.isEmpty()) {
			return itemStack;
		} else {
			CompoundTag compoundTag = itemStack.method_7948();
			ListTag listTag = compoundTag.method_10554("CustomPotionEffects", 9);

			for (StatusEffectInstance statusEffectInstance : collection) {
				listTag.add(statusEffectInstance.method_5582(new CompoundTag()));
			}

			compoundTag.method_10566("CustomPotionEffects", listTag);
			return itemStack;
		}
	}

	@Environment(EnvType.CLIENT)
	public static void buildTooltip(ItemStack itemStack, List<TextComponent> list, float f) {
		List<StatusEffectInstance> list2 = getPotionEffects(itemStack);
		List<Pair<String, EntityAttributeModifier>> list3 = Lists.<Pair<String, EntityAttributeModifier>>newArrayList();
		if (list2.isEmpty()) {
			list.add(new TranslatableTextComponent("effect.none").applyFormat(TextFormat.field_1080));
		} else {
			for (StatusEffectInstance statusEffectInstance : list2) {
				TextComponent textComponent = new TranslatableTextComponent(statusEffectInstance.getTranslationKey());
				StatusEffect statusEffect = statusEffectInstance.getEffectType();
				Map<EntityAttribute, EntityAttributeModifier> map = statusEffect.method_5565();
				if (!map.isEmpty()) {
					for (Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
						EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)entry.getValue();
						EntityAttributeModifier entityAttributeModifier2 = new EntityAttributeModifier(
							entityAttributeModifier.getName(),
							statusEffect.method_5563(statusEffectInstance.getAmplifier(), entityAttributeModifier),
							entityAttributeModifier.getOperation()
						);
						list3.add(new Pair<>(((EntityAttribute)entry.getKey()).getId(), entityAttributeModifier2));
					}
				}

				if (statusEffectInstance.getAmplifier() > 0) {
					textComponent.append(" ").append(new TranslatableTextComponent("potion.potency." + statusEffectInstance.getAmplifier()));
				}

				if (statusEffectInstance.getDuration() > 20) {
					textComponent.append(" (").append(StatusEffectUtil.durationToString(statusEffectInstance, f)).append(")");
				}

				list.add(textComponent.applyFormat(statusEffect.method_18792().method_18793()));
			}
		}

		if (!list3.isEmpty()) {
			list.add(new StringTextComponent(""));
			list.add(new TranslatableTextComponent("potion.whenDrank").applyFormat(TextFormat.field_1064));

			for (Pair<String, EntityAttributeModifier> pair : list3) {
				EntityAttributeModifier entityAttributeModifier3 = pair.getRight();
				double d = entityAttributeModifier3.getAmount();
				double e;
				if (entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.field_6330
					&& entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.field_6331) {
					e = entityAttributeModifier3.getAmount();
				} else {
					e = entityAttributeModifier3.getAmount() * 100.0;
				}

				if (d > 0.0) {
					list.add(
						new TranslatableTextComponent(
								"attribute.modifier.plus." + entityAttributeModifier3.getOperation().getId(),
								ItemStack.MODIFIER_FORMAT.format(e),
								new TranslatableTextComponent("attribute.name." + pair.getLeft())
							)
							.applyFormat(TextFormat.field_1078)
					);
				} else if (d < 0.0) {
					e *= -1.0;
					list.add(
						new TranslatableTextComponent(
								"attribute.modifier.take." + entityAttributeModifier3.getOperation().getId(),
								ItemStack.MODIFIER_FORMAT.format(e),
								new TranslatableTextComponent("attribute.name." + pair.getLeft())
							)
							.applyFormat(TextFormat.field_1061)
					);
				}
			}
		}
	}
}
