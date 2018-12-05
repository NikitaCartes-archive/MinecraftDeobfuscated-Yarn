package net.minecraft.particle;

import net.minecraft.util.registry.Registry;

public class ParticleTypes {
	public static final TexturedParticle field_11225 = register("ambient_entity_effect", false);
	public static final TexturedParticle field_11231 = register("angry_villager", false);
	public static final TexturedParticle field_11235 = register("barrier", false);
	public static final ParticleType<BlockStateParticle> field_11217 = register("block", BlockStateParticle.field_11181);
	public static final TexturedParticle field_11247 = register("bubble", false);
	public static final TexturedParticle field_11204 = register("cloud", false);
	public static final TexturedParticle field_11205 = register("crit", false);
	public static final TexturedParticle field_11209 = register("damage_indicator", true);
	public static final TexturedParticle field_11216 = register("dragon_breath", false);
	public static final TexturedParticle field_11223 = register("dripping_lava", false);
	public static final TexturedParticle field_11232 = register("dripping_water", false);
	public static final ParticleType<DustParticle> field_11212 = register("dust", DustParticle.field_11189);
	public static final TexturedParticle field_11245 = register("effect", false);
	public static final TexturedParticle field_11250 = register("elder_guardian", true);
	public static final TexturedParticle field_11208 = register("enchanted_hit", false);
	public static final TexturedParticle field_11215 = register("enchant", false);
	public static final TexturedParticle field_11207 = register("end_rod", false);
	public static final TexturedParticle field_11226 = register("entity_effect", false);
	public static final TexturedParticle field_11221 = register("explosion_emitter", true);
	public static final TexturedParticle field_11236 = register("explosion", true);
	public static final ParticleType<BlockStateParticle> field_11206 = register("falling_dust", BlockStateParticle.field_11181);
	public static final TexturedParticle field_11248 = register("firework", false);
	public static final TexturedParticle field_11244 = register("fishing", false);
	public static final TexturedParticle field_11240 = register("flame", false);
	public static final TexturedParticle field_11211 = register("happy_villager", false);
	public static final TexturedParticle field_11201 = register("heart", false);
	public static final TexturedParticle field_11213 = register("instant_effect", false);
	public static final ParticleType<ItemStackParticle> field_11218 = register("item", ItemStackParticle.field_11191);
	public static final TexturedParticle field_11246 = register("item_slime", false);
	public static final TexturedParticle field_11230 = register("item_snowball", false);
	public static final TexturedParticle field_11237 = register("large_smoke", false);
	public static final TexturedParticle field_11239 = register("lava", false);
	public static final TexturedParticle field_11219 = register("mycelium", false);
	public static final TexturedParticle field_11224 = register("note", false);
	public static final TexturedParticle field_11203 = register("poof", true);
	public static final TexturedParticle field_11214 = register("portal", false);
	public static final TexturedParticle field_11242 = register("rain", false);
	public static final TexturedParticle field_11251 = register("smoke", false);
	public static final TexturedParticle field_11234 = register("sneeze", false);
	public static final TexturedParticle field_11228 = register("spit", true);
	public static final TexturedParticle field_11233 = register("squid_ink", true);
	public static final TexturedParticle field_11227 = register("sweep_attack", true);
	public static final TexturedParticle field_11220 = register("totem_of_undying", false);
	public static final TexturedParticle field_11210 = register("underwater", false);
	public static final TexturedParticle field_11202 = register("splash", false);
	public static final TexturedParticle field_11249 = register("witch", false);
	public static final TexturedParticle field_11241 = register("bubble_pop", false);
	public static final TexturedParticle field_11243 = register("current_down", false);
	public static final TexturedParticle field_11238 = register("bubble_column_up", false);
	public static final TexturedParticle field_11229 = register("nautilus", false);
	public static final TexturedParticle field_11222 = register("dolphin", false);

	private static TexturedParticle register(String string, boolean bl) {
		return Registry.register(Registry.PARTICLE_TYPE, string, new TexturedParticle(bl));
	}

	private static <T extends Particle> ParticleType<T> register(String string, Particle.class_2395<T> arg) {
		return Registry.register(Registry.PARTICLE_TYPE, string, new ParticleType<>(false, arg));
	}
}
