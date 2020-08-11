package net.minecraft.particle;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class ParticleTypes {
	public static final DefaultParticleType AMBIENT_ENTITY_EFFECT = register("ambient_entity_effect", false);
	public static final DefaultParticleType ANGRY_VILLAGER = register("angry_villager", false);
	public static final DefaultParticleType BARRIER = register("barrier", false);
	public static final ParticleType<BlockStateParticleEffect> BLOCK = register(
		"block", BlockStateParticleEffect.PARAMETERS_FACTORY, BlockStateParticleEffect::method_29128
	);
	public static final DefaultParticleType BUBBLE = register("bubble", false);
	public static final DefaultParticleType CLOUD = register("cloud", false);
	public static final DefaultParticleType CRIT = register("crit", false);
	public static final DefaultParticleType DAMAGE_INDICATOR = register("damage_indicator", true);
	public static final DefaultParticleType DRAGON_BREATH = register("dragon_breath", false);
	public static final DefaultParticleType DRIPPING_LAVA = register("dripping_lava", false);
	public static final DefaultParticleType FALLING_LAVA = register("falling_lava", false);
	public static final DefaultParticleType LANDING_LAVA = register("landing_lava", false);
	public static final DefaultParticleType DRIPPING_WATER = register("dripping_water", false);
	public static final DefaultParticleType FALLING_WATER = register("falling_water", false);
	public static final ParticleType<DustParticleEffect> DUST = register("dust", DustParticleEffect.PARAMETERS_FACTORY, particleType -> DustParticleEffect.CODEC);
	public static final DefaultParticleType EFFECT = register("effect", false);
	public static final DefaultParticleType ELDER_GUARDIAN = register("elder_guardian", true);
	public static final DefaultParticleType ENCHANTED_HIT = register("enchanted_hit", false);
	public static final DefaultParticleType ENCHANT = register("enchant", false);
	public static final DefaultParticleType END_ROD = register("end_rod", false);
	public static final DefaultParticleType ENTITY_EFFECT = register("entity_effect", false);
	public static final DefaultParticleType EXPLOSION_EMITTER = register("explosion_emitter", true);
	public static final DefaultParticleType EXPLOSION = register("explosion", true);
	public static final ParticleType<BlockStateParticleEffect> FALLING_DUST = register(
		"falling_dust", BlockStateParticleEffect.PARAMETERS_FACTORY, BlockStateParticleEffect::method_29128
	);
	public static final DefaultParticleType FIREWORK = register("firework", false);
	public static final DefaultParticleType FISHING = register("fishing", false);
	public static final DefaultParticleType FLAME = register("flame", false);
	public static final DefaultParticleType SOUL_FIRE_FLAME = register("soul_fire_flame", false);
	public static final DefaultParticleType SOUL = register("soul", false);
	public static final DefaultParticleType FLASH = register("flash", false);
	public static final DefaultParticleType HAPPY_VILLAGER = register("happy_villager", false);
	public static final DefaultParticleType COMPOSTER = register("composter", false);
	public static final DefaultParticleType HEART = register("heart", false);
	public static final DefaultParticleType INSTANT_EFFECT = register("instant_effect", false);
	public static final ParticleType<ItemStackParticleEffect> ITEM = register(
		"item", ItemStackParticleEffect.PARAMETERS_FACTORY, ItemStackParticleEffect::method_29136
	);
	public static final DefaultParticleType ITEM_SLIME = register("item_slime", false);
	public static final DefaultParticleType ITEM_SNOWBALL = register("item_snowball", false);
	public static final DefaultParticleType LARGE_SMOKE = register("large_smoke", false);
	public static final DefaultParticleType LAVA = register("lava", false);
	public static final DefaultParticleType MYCELIUM = register("mycelium", false);
	public static final DefaultParticleType NOTE = register("note", false);
	public static final DefaultParticleType POOF = register("poof", true);
	public static final DefaultParticleType PORTAL = register("portal", false);
	public static final DefaultParticleType RAIN = register("rain", false);
	public static final DefaultParticleType SMOKE = register("smoke", false);
	public static final DefaultParticleType SNEEZE = register("sneeze", false);
	public static final DefaultParticleType SPIT = register("spit", true);
	public static final DefaultParticleType SQUID_INK = register("squid_ink", true);
	public static final DefaultParticleType SWEEP_ATTACK = register("sweep_attack", true);
	public static final DefaultParticleType TOTEM_OF_UNDYING = register("totem_of_undying", false);
	public static final DefaultParticleType UNDERWATER = register("underwater", false);
	public static final DefaultParticleType SPLASH = register("splash", false);
	public static final DefaultParticleType WITCH = register("witch", false);
	public static final DefaultParticleType BUBBLE_POP = register("bubble_pop", false);
	public static final DefaultParticleType CURRENT_DOWN = register("current_down", false);
	public static final DefaultParticleType BUBBLE_COLUMN_UP = register("bubble_column_up", false);
	public static final DefaultParticleType NAUTILUS = register("nautilus", false);
	public static final DefaultParticleType DOLPHIN = register("dolphin", false);
	public static final DefaultParticleType CAMPFIRE_COSY_SMOKE = register("campfire_cosy_smoke", true);
	public static final DefaultParticleType CAMPFIRE_SIGNAL_SMOKE = register("campfire_signal_smoke", true);
	public static final DefaultParticleType DRIPPING_HONEY = register("dripping_honey", false);
	public static final DefaultParticleType FALLING_HONEY = register("falling_honey", false);
	public static final DefaultParticleType LANDING_HONEY = register("landing_honey", false);
	public static final DefaultParticleType FALLING_NECTAR = register("falling_nectar", false);
	public static final DefaultParticleType ASH = register("ash", false);
	public static final DefaultParticleType CRIMSON_SPORE = register("crimson_spore", false);
	public static final DefaultParticleType WARPED_SPORE = register("warped_spore", false);
	public static final DefaultParticleType DRIPPING_OBSIDIAN_TEAR = register("dripping_obsidian_tear", false);
	public static final DefaultParticleType FALLING_OBSIDIAN_TEAR = register("falling_obsidian_tear", false);
	public static final DefaultParticleType LANDING_OBSIDIAN_TEAR = register("landing_obsidian_tear", false);
	public static final DefaultParticleType REVERSE_PORTAL = register("reverse_portal", false);
	public static final DefaultParticleType WHITE_ASH = register("white_ash", false);
	public static final Codec<ParticleEffect> TYPE_CODEC = Registry.PARTICLE_TYPE.dispatch("type", ParticleEffect::getType, ParticleType::getCodec);

	private static DefaultParticleType register(String name, boolean alwaysShow) {
		return Registry.register(Registry.PARTICLE_TYPE, name, new DefaultParticleType(alwaysShow));
	}

	private static <T extends ParticleEffect> ParticleType<T> register(
		String name, ParticleEffect.Factory<T> factory, Function<ParticleType<T>, Codec<T>> function
	) {
		return Registry.register(Registry.PARTICLE_TYPE, name, new ParticleType<T>(false, factory) {
			@Override
			public Codec<T> getCodec() {
				return (Codec<T>)function.apply(this);
			}
		});
	}
}
