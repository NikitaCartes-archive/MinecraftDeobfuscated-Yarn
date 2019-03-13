package net.minecraft.particle;

import net.minecraft.util.registry.Registry;

public class ParticleTypes {
	public static final DefaultParticleType field_11225 = method_10303("ambient_entity_effect", false);
	public static final DefaultParticleType field_11231 = method_10303("angry_villager", false);
	public static final DefaultParticleType field_11235 = method_10303("barrier", false);
	public static final ParticleType<BlockStateParticleParameters> field_11217 = register("block", BlockStateParticleParameters.PARAMETERS_FACTORY);
	public static final DefaultParticleType field_11247 = method_10303("bubble", false);
	public static final DefaultParticleType field_11204 = method_10303("cloud", false);
	public static final DefaultParticleType field_11205 = method_10303("crit", false);
	public static final DefaultParticleType field_11209 = method_10303("damage_indicator", true);
	public static final DefaultParticleType field_11216 = method_10303("dragon_breath", false);
	public static final DefaultParticleType field_11223 = method_10303("dripping_lava", false);
	public static final DefaultParticleType field_18304 = method_10303("falling_lava", false);
	public static final DefaultParticleType field_18305 = method_10303("landing_lava", false);
	public static final DefaultParticleType field_11232 = method_10303("dripping_water", false);
	public static final DefaultParticleType field_18306 = method_10303("falling_water", false);
	public static final ParticleType<DustParticleParameters> field_11212 = register("dust", DustParticleParameters.PARAMETERS_FACTORY);
	public static final DefaultParticleType field_11245 = method_10303("effect", false);
	public static final DefaultParticleType field_11250 = method_10303("elder_guardian", true);
	public static final DefaultParticleType field_11208 = method_10303("enchanted_hit", false);
	public static final DefaultParticleType field_11215 = method_10303("enchant", false);
	public static final DefaultParticleType field_11207 = method_10303("end_rod", false);
	public static final DefaultParticleType field_11226 = method_10303("entity_effect", false);
	public static final DefaultParticleType field_11221 = method_10303("explosion_emitter", true);
	public static final DefaultParticleType field_11236 = method_10303("explosion", true);
	public static final ParticleType<BlockStateParticleParameters> field_11206 = register("falling_dust", BlockStateParticleParameters.PARAMETERS_FACTORY);
	public static final DefaultParticleType field_11248 = method_10303("firework", false);
	public static final DefaultParticleType field_11244 = method_10303("fishing", false);
	public static final DefaultParticleType field_11240 = method_10303("flame", false);
	public static final DefaultParticleType field_17909 = method_10303("flash", false);
	public static final DefaultParticleType field_11211 = method_10303("happy_villager", false);
	public static final DefaultParticleType field_17741 = method_10303("composter", false);
	public static final DefaultParticleType field_11201 = method_10303("heart", false);
	public static final DefaultParticleType field_11213 = method_10303("instant_effect", false);
	public static final ParticleType<ItemStackParticleParameters> field_11218 = register("item", ItemStackParticleParameters.PARAMETERS_FACTORY);
	public static final DefaultParticleType field_11246 = method_10303("item_slime", false);
	public static final DefaultParticleType field_11230 = method_10303("item_snowball", false);
	public static final DefaultParticleType field_11237 = method_10303("large_smoke", false);
	public static final DefaultParticleType field_11239 = method_10303("lava", false);
	public static final DefaultParticleType field_11219 = method_10303("mycelium", false);
	public static final DefaultParticleType field_11224 = method_10303("note", false);
	public static final DefaultParticleType field_11203 = method_10303("poof", true);
	public static final DefaultParticleType field_11214 = method_10303("portal", false);
	public static final DefaultParticleType field_11242 = method_10303("rain", false);
	public static final DefaultParticleType field_11251 = method_10303("smoke", false);
	public static final DefaultParticleType field_11234 = method_10303("sneeze", false);
	public static final DefaultParticleType field_11228 = method_10303("spit", true);
	public static final DefaultParticleType field_11233 = method_10303("squid_ink", true);
	public static final DefaultParticleType field_11227 = method_10303("sweep_attack", true);
	public static final DefaultParticleType field_11220 = method_10303("totem_of_undying", false);
	public static final DefaultParticleType field_11210 = method_10303("underwater", false);
	public static final DefaultParticleType field_11202 = method_10303("splash", false);
	public static final DefaultParticleType field_11249 = method_10303("witch", false);
	public static final DefaultParticleType field_11241 = method_10303("bubble_pop", false);
	public static final DefaultParticleType field_11243 = method_10303("current_down", false);
	public static final DefaultParticleType field_11238 = method_10303("bubble_column_up", false);
	public static final DefaultParticleType field_11229 = method_10303("nautilus", false);
	public static final DefaultParticleType field_11222 = method_10303("dolphin", false);
	public static final DefaultParticleType field_17430 = method_10303("campfire_cosy_smoke", true);
	public static final DefaultParticleType field_17431 = method_10303("campfire_signal_smoke", true);

	private static DefaultParticleType method_10303(String string, boolean bl) {
		return Registry.register(Registry.PARTICLE_TYPE, string, new DefaultParticleType(bl));
	}

	private static <T extends ParticleParameters> ParticleType<T> register(String string, ParticleParameters.Factory<T> factory) {
		return Registry.register(Registry.PARTICLE_TYPE, string, new ParticleType<>(false, factory));
	}
}
