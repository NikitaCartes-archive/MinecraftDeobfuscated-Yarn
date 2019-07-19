/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class PotionUtil {
    public static List<StatusEffectInstance> getPotionEffects(ItemStack itemStack) {
        return PotionUtil.getPotionEffects(itemStack.getTag());
    }

    public static List<StatusEffectInstance> getPotionEffects(Potion potion, Collection<StatusEffectInstance> collection) {
        ArrayList<StatusEffectInstance> list = Lists.newArrayList();
        list.addAll(potion.getEffects());
        list.addAll(collection);
        return list;
    }

    public static List<StatusEffectInstance> getPotionEffects(@Nullable CompoundTag compoundTag) {
        ArrayList<StatusEffectInstance> list = Lists.newArrayList();
        list.addAll(PotionUtil.getPotion(compoundTag).getEffects());
        PotionUtil.getCustomPotionEffects(compoundTag, list);
        return list;
    }

    public static List<StatusEffectInstance> getCustomPotionEffects(ItemStack itemStack) {
        return PotionUtil.getCustomPotionEffects(itemStack.getTag());
    }

    public static List<StatusEffectInstance> getCustomPotionEffects(@Nullable CompoundTag compoundTag) {
        ArrayList<StatusEffectInstance> list = Lists.newArrayList();
        PotionUtil.getCustomPotionEffects(compoundTag, list);
        return list;
    }

    public static void getCustomPotionEffects(@Nullable CompoundTag compoundTag, List<StatusEffectInstance> list) {
        if (compoundTag != null && compoundTag.contains("CustomPotionEffects", 9)) {
            ListTag listTag = compoundTag.getList("CustomPotionEffects", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompound(i);
                StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromTag(compoundTag2);
                if (statusEffectInstance == null) continue;
                list.add(statusEffectInstance);
            }
        }
    }

    public static int getColor(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null && compoundTag.contains("CustomPotionColor", 99)) {
            return compoundTag.getInt("CustomPotionColor");
        }
        return PotionUtil.getPotion(itemStack) == Potions.EMPTY ? 0xF800F8 : PotionUtil.getColor(PotionUtil.getPotionEffects(itemStack));
    }

    public static int getColor(Potion potion) {
        return potion == Potions.EMPTY ? 0xF800F8 : PotionUtil.getColor(potion.getEffects());
    }

    public static int getColor(Collection<StatusEffectInstance> collection) {
        int i = 3694022;
        if (collection.isEmpty()) {
            return 3694022;
        }
        float f = 0.0f;
        float g = 0.0f;
        float h = 0.0f;
        int j = 0;
        for (StatusEffectInstance statusEffectInstance : collection) {
            if (!statusEffectInstance.shouldShowParticles()) continue;
            int k = statusEffectInstance.getEffectType().getColor();
            int l = statusEffectInstance.getAmplifier() + 1;
            f += (float)(l * (k >> 16 & 0xFF)) / 255.0f;
            g += (float)(l * (k >> 8 & 0xFF)) / 255.0f;
            h += (float)(l * (k >> 0 & 0xFF)) / 255.0f;
            j += l;
        }
        if (j == 0) {
            return 0;
        }
        f = f / (float)j * 255.0f;
        g = g / (float)j * 255.0f;
        h = h / (float)j * 255.0f;
        return (int)f << 16 | (int)g << 8 | (int)h;
    }

    public static Potion getPotion(ItemStack itemStack) {
        return PotionUtil.getPotion(itemStack.getTag());
    }

    public static Potion getPotion(@Nullable CompoundTag compoundTag) {
        if (compoundTag == null) {
            return Potions.EMPTY;
        }
        return Potion.byId(compoundTag.getString("Potion"));
    }

    public static ItemStack setPotion(ItemStack itemStack, Potion potion) {
        Identifier identifier = Registry.POTION.getId(potion);
        if (potion == Potions.EMPTY) {
            itemStack.removeSubTag("Potion");
        } else {
            itemStack.getOrCreateTag().putString("Potion", identifier.toString());
        }
        return itemStack;
    }

    public static ItemStack setCustomPotionEffects(ItemStack itemStack, Collection<StatusEffectInstance> collection) {
        if (collection.isEmpty()) {
            return itemStack;
        }
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        ListTag listTag = compoundTag.getList("CustomPotionEffects", 9);
        for (StatusEffectInstance statusEffectInstance : collection) {
            listTag.add(statusEffectInstance.toTag(new CompoundTag()));
        }
        compoundTag.put("CustomPotionEffects", listTag);
        return itemStack;
    }

    @Environment(value=EnvType.CLIENT)
    public static void buildTooltip(ItemStack itemStack, List<Text> list, float f) {
        List<StatusEffectInstance> list2 = PotionUtil.getPotionEffects(itemStack);
        ArrayList<Pair<String, EntityAttributeModifier>> list3 = Lists.newArrayList();
        if (list2.isEmpty()) {
            list.add(new TranslatableText("effect.none", new Object[0]).formatted(Formatting.GRAY));
        } else {
            for (StatusEffectInstance statusEffectInstance : list2) {
                TranslatableText text = new TranslatableText(statusEffectInstance.getTranslationKey(), new Object[0]);
                StatusEffect statusEffect = statusEffectInstance.getEffectType();
                Map<EntityAttribute, EntityAttributeModifier> map = statusEffect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
                        EntityAttributeModifier entityAttributeModifier = entry.getValue();
                        EntityAttributeModifier entityAttributeModifier2 = new EntityAttributeModifier(entityAttributeModifier.getName(), statusEffect.method_5563(statusEffectInstance.getAmplifier(), entityAttributeModifier), entityAttributeModifier.getOperation());
                        list3.add(new Pair<String, EntityAttributeModifier>(entry.getKey().getId(), entityAttributeModifier2));
                    }
                }
                if (statusEffectInstance.getAmplifier() > 0) {
                    text.append(" ").append(new TranslatableText("potion.potency." + statusEffectInstance.getAmplifier(), new Object[0]));
                }
                if (statusEffectInstance.getDuration() > 20) {
                    text.append(" (").append(StatusEffectUtil.durationToString(statusEffectInstance, f)).append(")");
                }
                list.add(text.formatted(statusEffect.getType().getFormatting()));
            }
        }
        if (!list3.isEmpty()) {
            list.add(new LiteralText(""));
            list.add(new TranslatableText("potion.whenDrank", new Object[0]).formatted(Formatting.DARK_PURPLE));
            for (Pair pair : list3) {
                EntityAttributeModifier entityAttributeModifier3 = (EntityAttributeModifier)pair.getRight();
                double d = entityAttributeModifier3.getAmount();
                double e = entityAttributeModifier3.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || entityAttributeModifier3.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL ? entityAttributeModifier3.getAmount() * 100.0 : entityAttributeModifier3.getAmount();
                if (d > 0.0) {
                    list.add(new TranslatableText("attribute.modifier.plus." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e), new TranslatableText("attribute.name." + (String)pair.getLeft(), new Object[0])).formatted(Formatting.BLUE));
                    continue;
                }
                if (!(d < 0.0)) continue;
                list.add(new TranslatableText("attribute.modifier.take." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(e *= -1.0), new TranslatableText("attribute.name." + (String)pair.getLeft(), new Object[0])).formatted(Formatting.RED));
            }
        }
    }
}

