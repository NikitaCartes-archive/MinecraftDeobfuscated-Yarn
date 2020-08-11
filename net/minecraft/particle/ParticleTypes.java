/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.particle;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

public class ParticleTypes {
    public static final DefaultParticleType AMBIENT_ENTITY_EFFECT = ParticleTypes.register("ambient_entity_effect", false);
    public static final DefaultParticleType ANGRY_VILLAGER = ParticleTypes.register("angry_villager", false);
    public static final DefaultParticleType BARRIER = ParticleTypes.register("barrier", false);
    public static final ParticleType<BlockStateParticleEffect> BLOCK = ParticleTypes.register("block", BlockStateParticleEffect.PARAMETERS_FACTORY, BlockStateParticleEffect::method_29128);
    public static final DefaultParticleType BUBBLE = ParticleTypes.register("bubble", false);
    public static final DefaultParticleType CLOUD = ParticleTypes.register("cloud", false);
    public static final DefaultParticleType CRIT = ParticleTypes.register("crit", false);
    public static final DefaultParticleType DAMAGE_INDICATOR = ParticleTypes.register("damage_indicator", true);
    public static final DefaultParticleType DRAGON_BREATH = ParticleTypes.register("dragon_breath", false);
    public static final DefaultParticleType DRIPPING_LAVA = ParticleTypes.register("dripping_lava", false);
    public static final DefaultParticleType FALLING_LAVA = ParticleTypes.register("falling_lava", false);
    public static final DefaultParticleType LANDING_LAVA = ParticleTypes.register("landing_lava", false);
    public static final DefaultParticleType DRIPPING_WATER = ParticleTypes.register("dripping_water", false);
    public static final DefaultParticleType FALLING_WATER = ParticleTypes.register("falling_water", false);
    public static final ParticleType<DustParticleEffect> DUST = ParticleTypes.register("dust", DustParticleEffect.PARAMETERS_FACTORY, particleType -> DustParticleEffect.CODEC);
    public static final DefaultParticleType EFFECT = ParticleTypes.register("effect", false);
    public static final DefaultParticleType ELDER_GUARDIAN = ParticleTypes.register("elder_guardian", true);
    public static final DefaultParticleType ENCHANTED_HIT = ParticleTypes.register("enchanted_hit", false);
    public static final DefaultParticleType ENCHANT = ParticleTypes.register("enchant", false);
    public static final DefaultParticleType END_ROD = ParticleTypes.register("end_rod", false);
    public static final DefaultParticleType ENTITY_EFFECT = ParticleTypes.register("entity_effect", false);
    public static final DefaultParticleType EXPLOSION_EMITTER = ParticleTypes.register("explosion_emitter", true);
    public static final DefaultParticleType EXPLOSION = ParticleTypes.register("explosion", true);
    public static final ParticleType<BlockStateParticleEffect> FALLING_DUST = ParticleTypes.register("falling_dust", BlockStateParticleEffect.PARAMETERS_FACTORY, BlockStateParticleEffect::method_29128);
    public static final DefaultParticleType FIREWORK = ParticleTypes.register("firework", false);
    public static final DefaultParticleType FISHING = ParticleTypes.register("fishing", false);
    public static final DefaultParticleType FLAME = ParticleTypes.register("flame", false);
    public static final DefaultParticleType SOUL_FIRE_FLAME = ParticleTypes.register("soul_fire_flame", false);
    public static final DefaultParticleType SOUL = ParticleTypes.register("soul", false);
    public static final DefaultParticleType FLASH = ParticleTypes.register("flash", false);
    public static final DefaultParticleType HAPPY_VILLAGER = ParticleTypes.register("happy_villager", false);
    public static final DefaultParticleType COMPOSTER = ParticleTypes.register("composter", false);
    public static final DefaultParticleType HEART = ParticleTypes.register("heart", false);
    public static final DefaultParticleType INSTANT_EFFECT = ParticleTypes.register("instant_effect", false);
    public static final ParticleType<ItemStackParticleEffect> ITEM = ParticleTypes.register("item", ItemStackParticleEffect.PARAMETERS_FACTORY, ItemStackParticleEffect::method_29136);
    public static final DefaultParticleType ITEM_SLIME = ParticleTypes.register("item_slime", false);
    public static final DefaultParticleType ITEM_SNOWBALL = ParticleTypes.register("item_snowball", false);
    public static final DefaultParticleType LARGE_SMOKE = ParticleTypes.register("large_smoke", false);
    public static final DefaultParticleType LAVA = ParticleTypes.register("lava", false);
    public static final DefaultParticleType MYCELIUM = ParticleTypes.register("mycelium", false);
    public static final DefaultParticleType NOTE = ParticleTypes.register("note", false);
    public static final DefaultParticleType POOF = ParticleTypes.register("poof", true);
    public static final DefaultParticleType PORTAL = ParticleTypes.register("portal", false);
    public static final DefaultParticleType RAIN = ParticleTypes.register("rain", false);
    public static final DefaultParticleType SMOKE = ParticleTypes.register("smoke", false);
    public static final DefaultParticleType SNEEZE = ParticleTypes.register("sneeze", false);
    public static final DefaultParticleType SPIT = ParticleTypes.register("spit", true);
    public static final DefaultParticleType SQUID_INK = ParticleTypes.register("squid_ink", true);
    public static final DefaultParticleType SWEEP_ATTACK = ParticleTypes.register("sweep_attack", true);
    public static final DefaultParticleType TOTEM_OF_UNDYING = ParticleTypes.register("totem_of_undying", false);
    public static final DefaultParticleType UNDERWATER = ParticleTypes.register("underwater", false);
    public static final DefaultParticleType SPLASH = ParticleTypes.register("splash", false);
    public static final DefaultParticleType WITCH = ParticleTypes.register("witch", false);
    public static final DefaultParticleType BUBBLE_POP = ParticleTypes.register("bubble_pop", false);
    public static final DefaultParticleType CURRENT_DOWN = ParticleTypes.register("current_down", false);
    public static final DefaultParticleType BUBBLE_COLUMN_UP = ParticleTypes.register("bubble_column_up", false);
    public static final DefaultParticleType NAUTILUS = ParticleTypes.register("nautilus", false);
    public static final DefaultParticleType DOLPHIN = ParticleTypes.register("dolphin", false);
    public static final DefaultParticleType CAMPFIRE_COSY_SMOKE = ParticleTypes.register("campfire_cosy_smoke", true);
    public static final DefaultParticleType CAMPFIRE_SIGNAL_SMOKE = ParticleTypes.register("campfire_signal_smoke", true);
    public static final DefaultParticleType DRIPPING_HONEY = ParticleTypes.register("dripping_honey", false);
    public static final DefaultParticleType FALLING_HONEY = ParticleTypes.register("falling_honey", false);
    public static final DefaultParticleType LANDING_HONEY = ParticleTypes.register("landing_honey", false);
    public static final DefaultParticleType FALLING_NECTAR = ParticleTypes.register("falling_nectar", false);
    public static final DefaultParticleType ASH = ParticleTypes.register("ash", false);
    public static final DefaultParticleType CRIMSON_SPORE = ParticleTypes.register("crimson_spore", false);
    public static final DefaultParticleType WARPED_SPORE = ParticleTypes.register("warped_spore", false);
    public static final DefaultParticleType DRIPPING_OBSIDIAN_TEAR = ParticleTypes.register("dripping_obsidian_tear", false);
    public static final DefaultParticleType FALLING_OBSIDIAN_TEAR = ParticleTypes.register("falling_obsidian_tear", false);
    public static final DefaultParticleType LANDING_OBSIDIAN_TEAR = ParticleTypes.register("landing_obsidian_tear", false);
    public static final DefaultParticleType REVERSE_PORTAL = ParticleTypes.register("reverse_portal", false);
    public static final DefaultParticleType WHITE_ASH = ParticleTypes.register("white_ash", false);
    public static final Codec<ParticleEffect> TYPE_CODEC = Registry.PARTICLE_TYPE.dispatch("type", ParticleEffect::getType, ParticleType::getCodec);

    private static DefaultParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registry.PARTICLE_TYPE, name, new DefaultParticleType(alwaysShow));
    }

    private static <T extends ParticleEffect> ParticleType<T> register(String name, ParticleEffect.Factory<T> factory, final Function<ParticleType<T>, Codec<T>> function) {
        return Registry.register(Registry.PARTICLE_TYPE, name, new ParticleType<T>(false, factory){

            @Override
            public Codec<T> getCodec() {
                return (Codec)function.apply(this);
            }
        });
    }
}

