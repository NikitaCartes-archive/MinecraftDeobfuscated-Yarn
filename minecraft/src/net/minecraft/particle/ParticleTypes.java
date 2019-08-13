package net.minecraft.particle;

import net.minecraft.util.registry.Registry;

public class ParticleTypes {
	public static final DefaultParticleType field_11225 = register("ambient_entity_effect", false);
	public static final DefaultParticleType field_11231 = register("angry_villager", false);
	public static final DefaultParticleType field_11235 = register("barrier", false);
	public static final ParticleType<BlockStateParticleEffect> field_11217 = register("block", BlockStateParticleEffect.PARAMETERS_FACTORY);
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
	public static final ParticleType<DustParticleEffect> field_11212 = register("dust", DustParticleEffect.PARAMETERS_FACTORY);
	public static final DefaultParticleType field_11245 = register("effect", false);
	public static final DefaultParticleType field_11250 = register("elder_guardian", true);
	public static final DefaultParticleType field_11208 = register("enchanted_hit", false);
	public static final DefaultParticleType field_11215 = register("enchant", false);
	public static final DefaultParticleType field_11207 = register("end_rod", false);
	public static final DefaultParticleType field_11226 = register("entity_effect", false);
	public static final DefaultParticleType field_11221 = register("explosion_emitter", true);
	public static final DefaultParticleType field_11236 = register("explosion", true);
	public static final ParticleType<BlockStateParticleEffect> field_11206 = register("falling_dust", BlockStateParticleEffect.PARAMETERS_FACTORY);
	public static final DefaultParticleType field_11248 = register("firework", false);
	public static final DefaultParticleType field_11244 = register("fishing", false);
	public static final DefaultParticleType field_11240 = register("flame", false);
	public static final DefaultParticleType field_17909 = register("flash", false);
	public static final DefaultParticleType field_11211 = register("happy_villager", false);
	public static final DefaultParticleType field_17741 = register("composter", false);
	public static final DefaultParticleType field_11201 = register("heart", false);
	public static final DefaultParticleType field_11213 = register("instant_effect", false);
	public static final ParticleType<ItemStackParticleEffect> field_11218 = register("item", ItemStackParticleEffect.PARAMETERS_FACTORY);
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

	private static DefaultParticleType register(String string, boolean bl) {
		return Registry.register(Registry.PARTICLE_TYPE, string, new DefaultParticleType(bl));
	}

	private static <T extends ParticleEffect> ParticleType<T> register(String string, ParticleEffect.Factory<T> factory) {
		return Registry.register(Registry.PARTICLE_TYPE, string, new ParticleType<>(false, factory));
	}
}
