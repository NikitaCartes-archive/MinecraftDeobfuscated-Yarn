package net.minecraft.particle;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class ParticleTypes {
	public static final DefaultParticleType field_11225 = register("ambient_entity_effect", false);
	public static final DefaultParticleType field_11231 = register("angry_villager", false);
	public static final DefaultParticleType field_11235 = register("barrier", false);
	public static final ParticleType<BlockStateParticleEffect> field_11217 = register(
		"block", BlockStateParticleEffect.PARAMETERS_FACTORY, BlockStateParticleEffect::method_29128
	);
	public static final DefaultParticleType field_11247 = register("bubble", false);
	public static final DefaultParticleType field_11204 = register("cloud", false);
	public static final DefaultParticleType field_11205 = register("crit", false);
	public static final DefaultParticleType field_11209 = register("damage_indicator", true);
	public static final DefaultParticleType field_11216 = register("dragon_breath", false);
	public static final DefaultParticleType field_11223 = register("dripping_lava", false);
	public static final DefaultParticleType field_18304 = register("falling_lava", false);
	public static final DefaultParticleType field_18305 = register("landing_lava", false);
	public static final DefaultParticleType field_11232 = register("dripping_water", false);
	public static final DefaultParticleType field_18306 = register("falling_water", false);
	public static final ParticleType<DustParticleEffect> field_11212 = register(
		"dust", DustParticleEffect.PARAMETERS_FACTORY, particleType -> DustParticleEffect.CODEC
	);
	public static final DefaultParticleType field_11245 = register("effect", false);
	public static final DefaultParticleType field_11250 = register("elder_guardian", true);
	public static final DefaultParticleType field_11208 = register("enchanted_hit", false);
	public static final DefaultParticleType field_11215 = register("enchant", false);
	public static final DefaultParticleType field_11207 = register("end_rod", false);
	public static final DefaultParticleType field_11226 = register("entity_effect", false);
	public static final DefaultParticleType field_11221 = register("explosion_emitter", true);
	public static final DefaultParticleType field_11236 = register("explosion", true);
	public static final ParticleType<BlockStateParticleEffect> field_11206 = register(
		"falling_dust", BlockStateParticleEffect.PARAMETERS_FACTORY, BlockStateParticleEffect::method_29128
	);
	public static final DefaultParticleType field_11248 = register("firework", false);
	public static final DefaultParticleType field_11244 = register("fishing", false);
	public static final DefaultParticleType field_11240 = register("flame", false);
	public static final DefaultParticleType field_22246 = register("soul_fire_flame", false);
	public static final DefaultParticleType field_23114 = register("soul", false);
	public static final DefaultParticleType field_17909 = register("flash", false);
	public static final DefaultParticleType field_11211 = register("happy_villager", false);
	public static final DefaultParticleType field_17741 = register("composter", false);
	public static final DefaultParticleType field_11201 = register("heart", false);
	public static final DefaultParticleType field_11213 = register("instant_effect", false);
	public static final ParticleType<ItemStackParticleEffect> field_11218 = register(
		"item", ItemStackParticleEffect.PARAMETERS_FACTORY, ItemStackParticleEffect::method_29136
	);
	public static final DefaultParticleType field_11246 = register("item_slime", false);
	public static final DefaultParticleType field_11230 = register("item_snowball", false);
	public static final DefaultParticleType field_11237 = register("large_smoke", false);
	public static final DefaultParticleType field_11239 = register("lava", false);
	public static final DefaultParticleType field_11219 = register("mycelium", false);
	public static final DefaultParticleType field_11224 = register("note", false);
	public static final DefaultParticleType field_11203 = register("poof", true);
	public static final DefaultParticleType field_11214 = register("portal", false);
	public static final DefaultParticleType field_11242 = register("rain", false);
	public static final DefaultParticleType field_11251 = register("smoke", false);
	public static final DefaultParticleType field_11234 = register("sneeze", false);
	public static final DefaultParticleType field_11228 = register("spit", true);
	public static final DefaultParticleType field_11233 = register("squid_ink", true);
	public static final DefaultParticleType field_11227 = register("sweep_attack", true);
	public static final DefaultParticleType field_11220 = register("totem_of_undying", false);
	public static final DefaultParticleType field_11210 = register("underwater", false);
	public static final DefaultParticleType field_11202 = register("splash", false);
	public static final DefaultParticleType field_11249 = register("witch", false);
	public static final DefaultParticleType field_11241 = register("bubble_pop", false);
	public static final DefaultParticleType field_11243 = register("current_down", false);
	public static final DefaultParticleType field_11238 = register("bubble_column_up", false);
	public static final DefaultParticleType field_11229 = register("nautilus", false);
	public static final DefaultParticleType field_11222 = register("dolphin", false);
	public static final DefaultParticleType field_17430 = register("campfire_cosy_smoke", true);
	public static final DefaultParticleType field_17431 = register("campfire_signal_smoke", true);
	public static final DefaultParticleType field_20534 = register("dripping_honey", false);
	public static final DefaultParticleType field_20535 = register("falling_honey", false);
	public static final DefaultParticleType field_20536 = register("landing_honey", false);
	public static final DefaultParticleType field_20537 = register("falling_nectar", false);
	public static final DefaultParticleType field_22247 = register("ash", false);
	public static final DefaultParticleType field_22248 = register("crimson_spore", false);
	public static final DefaultParticleType field_22249 = register("warped_spore", false);
	public static final DefaultParticleType field_22446 = register("dripping_obsidian_tear", false);
	public static final DefaultParticleType field_22447 = register("falling_obsidian_tear", false);
	public static final DefaultParticleType field_22448 = register("landing_obsidian_tear", false);
	public static final DefaultParticleType field_23190 = register("reverse_portal", false);
	public static final DefaultParticleType field_23956 = register("white_ash", false);
	public static final Codec<ParticleEffect> field_25125 = Registry.PARTICLE_TYPE.dispatch("type", ParticleEffect::getType, ParticleType::method_29138);

	private static DefaultParticleType register(String name, boolean alwaysShow) {
		return Registry.register(Registry.PARTICLE_TYPE, name, new DefaultParticleType(alwaysShow));
	}

	private static <T extends ParticleEffect> ParticleType<T> register(
		String name, ParticleEffect.Factory<T> factory, Function<ParticleType<T>, Codec<T>> function
	) {
		return Registry.register(Registry.PARTICLE_TYPE, name, new ParticleType<T>(false, factory) {
			@Override
			public Codec<T> method_29138() {
				return (Codec<T>)function.apply(this);
			}
		});
	}
}
