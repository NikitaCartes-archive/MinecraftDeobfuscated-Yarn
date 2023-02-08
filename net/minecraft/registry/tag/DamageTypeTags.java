/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry.tag;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface DamageTypeTags {
    public static final TagKey<DamageType> DAMAGES_HELMET = DamageTypeTags.of("damages_helmet");
    public static final TagKey<DamageType> BYPASSES_ARMOR = DamageTypeTags.of("bypasses_armor");
    public static final TagKey<DamageType> BYPASSES_INVULNERABILITY = DamageTypeTags.of("bypasses_invulnerability");
    public static final TagKey<DamageType> BYPASSES_EFFECTS = DamageTypeTags.of("bypasses_effects");
    public static final TagKey<DamageType> BYPASSES_RESISTANCE = DamageTypeTags.of("bypasses_resistance");
    public static final TagKey<DamageType> BYPASSES_ENCHANTMENTS = DamageTypeTags.of("bypasses_enchantments");
    public static final TagKey<DamageType> IS_FIRE = DamageTypeTags.of("is_fire");
    public static final TagKey<DamageType> IS_PROJECTILE = DamageTypeTags.of("is_projectile");
    public static final TagKey<DamageType> WITCH_RESISTANT_TO = DamageTypeTags.of("witch_resistant_to");
    public static final TagKey<DamageType> IS_EXPLOSION = DamageTypeTags.of("is_explosion");
    public static final TagKey<DamageType> IS_FALL = DamageTypeTags.of("is_fall");
    public static final TagKey<DamageType> IS_DROWNING = DamageTypeTags.of("is_drowning");
    public static final TagKey<DamageType> IS_FREEZING = DamageTypeTags.of("is_freezing");
    public static final TagKey<DamageType> IS_LIGHTNING = DamageTypeTags.of("is_lightning");
    public static final TagKey<DamageType> NO_ANGER = DamageTypeTags.of("no_anger");
    public static final TagKey<DamageType> NO_IMPACT = DamageTypeTags.of("no_impact");
    public static final TagKey<DamageType> ALWAYS_MOST_SIGNIFICANT_FALL = DamageTypeTags.of("always_most_significant_fall");
    public static final TagKey<DamageType> WITHER_IMMUNE_TO = DamageTypeTags.of("wither_immune_to");
    public static final TagKey<DamageType> IGNITES_ARMOR_STANDS = DamageTypeTags.of("ignites_armor_stands");
    public static final TagKey<DamageType> BURNS_ARMOR_STANDS = DamageTypeTags.of("burns_armor_stands");
    public static final TagKey<DamageType> AVOIDS_GUARDIAN_THORNS = DamageTypeTags.of("avoids_guardian_thorns");
    public static final TagKey<DamageType> ALWAYS_TRIGGERS_SILVERFISH = DamageTypeTags.of("always_triggers_silverfish");

    private static TagKey<DamageType> of(String id) {
        return TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(id));
    }
}

