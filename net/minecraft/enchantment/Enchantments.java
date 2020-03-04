/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.AquaAffinityEnchantment;
import net.minecraft.enchantment.BindingCurseEnchantment;
import net.minecraft.enchantment.ChannelingEnchantment;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.DepthStriderEnchantment;
import net.minecraft.enchantment.EfficiencyEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.FireAspectEnchantment;
import net.minecraft.enchantment.FlameEnchantment;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.enchantment.ImpalingEnchantment;
import net.minecraft.enchantment.InfinityEnchantment;
import net.minecraft.enchantment.KnockbackEnchantment;
import net.minecraft.enchantment.LoyaltyEnchantment;
import net.minecraft.enchantment.LuckEnchantment;
import net.minecraft.enchantment.LureEnchantment;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.enchantment.MultishotEnchantment;
import net.minecraft.enchantment.PiercingEnchantment;
import net.minecraft.enchantment.PowerEnchantment;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.enchantment.PunchEnchantment;
import net.minecraft.enchantment.QuickChargeEnchantment;
import net.minecraft.enchantment.RespirationEnchantment;
import net.minecraft.enchantment.RiptideEnchantment;
import net.minecraft.enchantment.SilkTouchEnchantment;
import net.minecraft.enchantment.SweepingEnchantment;
import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.enchantment.VanishingCurseEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.registry.Registry;

public class Enchantments {
    private static final EquipmentSlot[] ALL_ARMOR = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static final Enchantment PROTECTION = Enchantments.register("protection", new ProtectionEnchantment(Enchantment.Weight.COMMON, ProtectionEnchantment.Type.ALL, ALL_ARMOR));
    public static final Enchantment FIRE_PROTECTION = Enchantments.register("fire_protection", new ProtectionEnchantment(Enchantment.Weight.UNCOMMON, ProtectionEnchantment.Type.FIRE, ALL_ARMOR));
    public static final Enchantment FEATHER_FALLING = Enchantments.register("feather_falling", new ProtectionEnchantment(Enchantment.Weight.UNCOMMON, ProtectionEnchantment.Type.FALL, ALL_ARMOR));
    public static final Enchantment BLAST_PROTECTION = Enchantments.register("blast_protection", new ProtectionEnchantment(Enchantment.Weight.RARE, ProtectionEnchantment.Type.EXPLOSION, ALL_ARMOR));
    public static final Enchantment PROJECTILE_PROTECTION = Enchantments.register("projectile_protection", new ProtectionEnchantment(Enchantment.Weight.UNCOMMON, ProtectionEnchantment.Type.PROJECTILE, ALL_ARMOR));
    public static final Enchantment RESPIRATION = Enchantments.register("respiration", new RespirationEnchantment(Enchantment.Weight.RARE, ALL_ARMOR));
    public static final Enchantment AQUA_AFFINITY = Enchantments.register("aqua_affinity", new AquaAffinityEnchantment(Enchantment.Weight.RARE, ALL_ARMOR));
    public static final Enchantment THORNS = Enchantments.register("thorns", new ThornsEnchantment(Enchantment.Weight.VERY_RARE, ALL_ARMOR));
    public static final Enchantment DEPTH_STRIDER = Enchantments.register("depth_strider", new DepthStriderEnchantment(Enchantment.Weight.RARE, ALL_ARMOR));
    public static final Enchantment FROST_WALKER = Enchantments.register("frost_walker", new FrostWalkerEnchantment(Enchantment.Weight.RARE, EquipmentSlot.FEET));
    public static final Enchantment BINDING_CURSE = Enchantments.register("binding_curse", new BindingCurseEnchantment(Enchantment.Weight.VERY_RARE, ALL_ARMOR));
    public static final Enchantment SHARPNESS = Enchantments.register("sharpness", new DamageEnchantment(Enchantment.Weight.COMMON, 0, EquipmentSlot.MAINHAND));
    public static final Enchantment SMITE = Enchantments.register("smite", new DamageEnchantment(Enchantment.Weight.UNCOMMON, 1, EquipmentSlot.MAINHAND));
    public static final Enchantment BANE_OF_ARTHROPODS = Enchantments.register("bane_of_arthropods", new DamageEnchantment(Enchantment.Weight.UNCOMMON, 2, EquipmentSlot.MAINHAND));
    public static final Enchantment KNOCKBACK = Enchantments.register("knockback", new KnockbackEnchantment(Enchantment.Weight.UNCOMMON, EquipmentSlot.MAINHAND));
    public static final Enchantment FIRE_ASPECT = Enchantments.register("fire_aspect", new FireAspectEnchantment(Enchantment.Weight.RARE, EquipmentSlot.MAINHAND));
    public static final Enchantment LOOTING = Enchantments.register("looting", new LuckEnchantment(Enchantment.Weight.RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND));
    public static final Enchantment SWEEPING = Enchantments.register("sweeping", new SweepingEnchantment(Enchantment.Weight.RARE, EquipmentSlot.MAINHAND));
    public static final Enchantment EFFICIENCY = Enchantments.register("efficiency", new EfficiencyEnchantment(Enchantment.Weight.COMMON, EquipmentSlot.MAINHAND));
    public static final Enchantment SILK_TOUCH = Enchantments.register("silk_touch", new SilkTouchEnchantment(Enchantment.Weight.VERY_RARE, EquipmentSlot.MAINHAND));
    public static final Enchantment UNBREAKING = Enchantments.register("unbreaking", new UnbreakingEnchantment(Enchantment.Weight.UNCOMMON, EquipmentSlot.MAINHAND));
    public static final Enchantment FORTUNE = Enchantments.register("fortune", new LuckEnchantment(Enchantment.Weight.RARE, EnchantmentTarget.DIGGER, EquipmentSlot.MAINHAND));
    public static final Enchantment POWER = Enchantments.register("power", new PowerEnchantment(Enchantment.Weight.COMMON, EquipmentSlot.MAINHAND));
    public static final Enchantment PUNCH = Enchantments.register("punch", new PunchEnchantment(Enchantment.Weight.RARE, EquipmentSlot.MAINHAND));
    public static final Enchantment FLAME = Enchantments.register("flame", new FlameEnchantment(Enchantment.Weight.RARE, EquipmentSlot.MAINHAND));
    public static final Enchantment INFINITY = Enchantments.register("infinity", new InfinityEnchantment(Enchantment.Weight.VERY_RARE, EquipmentSlot.MAINHAND));
    public static final Enchantment LUCK_OF_THE_SEA = Enchantments.register("luck_of_the_sea", new LuckEnchantment(Enchantment.Weight.RARE, EnchantmentTarget.FISHING_ROD, EquipmentSlot.MAINHAND));
    public static final Enchantment LURE = Enchantments.register("lure", new LureEnchantment(Enchantment.Weight.RARE, EnchantmentTarget.FISHING_ROD, EquipmentSlot.MAINHAND));
    public static final Enchantment LOYALTY = Enchantments.register("loyalty", new LoyaltyEnchantment(Enchantment.Weight.UNCOMMON, EquipmentSlot.MAINHAND));
    public static final Enchantment IMPALING = Enchantments.register("impaling", new ImpalingEnchantment(Enchantment.Weight.RARE, EquipmentSlot.MAINHAND));
    public static final Enchantment RIPTIDE = Enchantments.register("riptide", new RiptideEnchantment(Enchantment.Weight.RARE, EquipmentSlot.MAINHAND));
    public static final Enchantment CHANNELING = Enchantments.register("channeling", new ChannelingEnchantment(Enchantment.Weight.VERY_RARE, EquipmentSlot.MAINHAND));
    public static final Enchantment MULTISHOT = Enchantments.register("multishot", new MultishotEnchantment(Enchantment.Weight.RARE, EquipmentSlot.MAINHAND));
    public static final Enchantment QUICK_CHARGE = Enchantments.register("quick_charge", new QuickChargeEnchantment(Enchantment.Weight.UNCOMMON, EquipmentSlot.MAINHAND));
    public static final Enchantment PIERCING = Enchantments.register("piercing", new PiercingEnchantment(Enchantment.Weight.COMMON, EquipmentSlot.MAINHAND));
    public static final Enchantment MENDING = Enchantments.register("mending", new MendingEnchantment(Enchantment.Weight.RARE, EquipmentSlot.values()));
    public static final Enchantment VANISHING_CURSE = Enchantments.register("vanishing_curse", new VanishingCurseEnchantment(Enchantment.Weight.VERY_RARE, EquipmentSlot.values()));

    private static Enchantment register(String name, Enchantment enchantment) {
        return Registry.register(Registry.ENCHANTMENT, name, enchantment);
    }
}

