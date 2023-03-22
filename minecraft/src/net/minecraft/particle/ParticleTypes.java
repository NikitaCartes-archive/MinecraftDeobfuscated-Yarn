package net.minecraft.particle;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ParticleTypes {
	public static final DefaultParticleType AMBIENT_ENTITY_EFFECT = register("ambient_entity_effect", false);
	public static final DefaultParticleType ANGRY_VILLAGER = register("angry_villager", false);
	public static final ParticleType<BlockStateParticleEffect> BLOCK = register(
		"block", false, BlockStateParticleEffect.PARAMETERS_FACTORY, BlockStateParticleEffect::createCodec
	);
	public static final ParticleType<BlockStateParticleEffect> BLOCK_MARKER = register(
		"block_marker", false, BlockStateParticleEffect.PARAMETERS_FACTORY, BlockStateParticleEffect::createCodec
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
	public static final ParticleType<DustParticleEffect> DUST = register(
		"dust", false, DustParticleEffect.PARAMETERS_FACTORY, particleType -> DustParticleEffect.CODEC
	);
	public static final ParticleType<DustColorTransitionParticleEffect> DUST_COLOR_TRANSITION = register(
		"dust_color_transition", false, DustColorTransitionParticleEffect.FACTORY, particleType -> DustColorTransitionParticleEffect.CODEC
	);
	public static final DefaultParticleType EFFECT = register("effect", false);
	public static final DefaultParticleType ELDER_GUARDIAN = register("elder_guardian", true);
	public static final DefaultParticleType ENCHANTED_HIT = register("enchanted_hit", false);
	public static final DefaultParticleType ENCHANT = register("enchant", false);
	public static final DefaultParticleType END_ROD = register("end_rod", false);
	public static final DefaultParticleType ENTITY_EFFECT = register("entity_effect", false);
	public static final DefaultParticleType EXPLOSION_EMITTER = register("explosion_emitter", true);
	public static final DefaultParticleType EXPLOSION = register("explosion", true);
	public static final DefaultParticleType SONIC_BOOM = register("sonic_boom", true);
	public static final ParticleType<BlockStateParticleEffect> FALLING_DUST = register(
		"falling_dust", false, BlockStateParticleEffect.PARAMETERS_FACTORY, BlockStateParticleEffect::createCodec
	);
	public static final DefaultParticleType FIREWORK = register("firework", false);
	public static final DefaultParticleType FISHING = register("fishing", false);
	public static final DefaultParticleType FLAME = register("flame", false);
	public static final DefaultParticleType CHERRY_LEAVES = register("cherry_leaves", false);
	public static final DefaultParticleType SCULK_SOUL = register("sculk_soul", false);
	public static final ParticleType<SculkChargeParticleEffect> SCULK_CHARGE = register(
		"sculk_charge", true, SculkChargeParticleEffect.FACTORY, particleType -> SculkChargeParticleEffect.CODEC
	);
	public static final DefaultParticleType SCULK_CHARGE_POP = register("sculk_charge_pop", true);
	public static final DefaultParticleType SOUL_FIRE_FLAME = register("soul_fire_flame", false);
	public static final DefaultParticleType SOUL = register("soul", false);
	public static final DefaultParticleType FLASH = register("flash", false);
	public static final DefaultParticleType HAPPY_VILLAGER = register("happy_villager", false);
	public static final DefaultParticleType COMPOSTER = register("composter", false);
	public static final DefaultParticleType HEART = register("heart", false);
	public static final DefaultParticleType INSTANT_EFFECT = register("instant_effect", false);
	public static final ParticleType<ItemStackParticleEffect> ITEM = register(
		"item", false, ItemStackParticleEffect.PARAMETERS_FACTORY, ItemStackParticleEffect::createCodec
	);
	public static final ParticleType<VibrationParticleEffect> VIBRATION = register(
		"vibration", true, VibrationParticleEffect.PARAMETERS_FACTORY, particleType -> VibrationParticleEffect.CODEC
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
	public static final DefaultParticleType FALLING_SPORE_BLOSSOM = register("falling_spore_blossom", false);
	public static final DefaultParticleType ASH = register("ash", false);
	public static final DefaultParticleType CRIMSON_SPORE = register("crimson_spore", false);
	public static final DefaultParticleType WARPED_SPORE = register("warped_spore", false);
	public static final DefaultParticleType SPORE_BLOSSOM_AIR = register("spore_blossom_air", false);
	public static final DefaultParticleType DRIPPING_OBSIDIAN_TEAR = register("dripping_obsidian_tear", false);
	public static final DefaultParticleType FALLING_OBSIDIAN_TEAR = register("falling_obsidian_tear", false);
	public static final DefaultParticleType LANDING_OBSIDIAN_TEAR = register("landing_obsidian_tear", false);
	public static final DefaultParticleType REVERSE_PORTAL = register("reverse_portal", false);
	public static final DefaultParticleType WHITE_ASH = register("white_ash", false);
	public static final DefaultParticleType SMALL_FLAME = register("small_flame", false);
	public static final DefaultParticleType SNOWFLAKE = register("snowflake", false);
	public static final DefaultParticleType DRIPPING_DRIPSTONE_LAVA = register("dripping_dripstone_lava", false);
	public static final DefaultParticleType FALLING_DRIPSTONE_LAVA = register("falling_dripstone_lava", false);
	public static final DefaultParticleType DRIPPING_DRIPSTONE_WATER = register("dripping_dripstone_water", false);
	public static final DefaultParticleType FALLING_DRIPSTONE_WATER = register("falling_dripstone_water", false);
	public static final DefaultParticleType GLOW_SQUID_INK = register("glow_squid_ink", true);
	public static final DefaultParticleType GLOW = register("glow", true);
	public static final DefaultParticleType WAX_ON = register("wax_on", true);
	public static final DefaultParticleType WAX_OFF = register("wax_off", true);
	public static final DefaultParticleType ELECTRIC_SPARK = register("electric_spark", true);
	public static final DefaultParticleType SCRAPE = register("scrape", true);
	public static final ParticleType<ShriekParticleEffect> SHRIEK = register("shriek", false, ShriekParticleEffect.FACTORY, type -> ShriekParticleEffect.CODEC);
	public static final DefaultParticleType EGG_CRACK = register("egg_crack", false);
	public static final Codec<ParticleEffect> TYPE_CODEC = Registries.PARTICLE_TYPE.getCodec().dispatch("type", ParticleEffect::getType, ParticleType::getCodec);

	private static DefaultParticleType register(String name, boolean alwaysShow) {
		return Registry.register(Registries.PARTICLE_TYPE, name, new DefaultParticleType(alwaysShow));
	}

	private static <T extends ParticleEffect> ParticleType<T> register(
		String name, boolean alwaysShow, ParticleEffect.Factory<T> factory, Function<ParticleType<T>, Codec<T>> codecGetter
	) {
		return Registry.register(Registries.PARTICLE_TYPE, name, new ParticleType<T>(alwaysShow, factory) {
			@Override
			public Codec<T> getCodec() {
				return (Codec<T>)codecGetter.apply(this);
			}
		});
	}
}
