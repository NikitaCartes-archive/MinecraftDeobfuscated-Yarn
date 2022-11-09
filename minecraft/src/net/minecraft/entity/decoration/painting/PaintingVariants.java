package net.minecraft.entity.decoration.painting;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class PaintingVariants {
	public static final RegistryKey<PaintingVariant> KEBAB = of("kebab");
	public static final RegistryKey<PaintingVariant> AZTEC = of("aztec");
	public static final RegistryKey<PaintingVariant> ALBAN = of("alban");
	public static final RegistryKey<PaintingVariant> AZTEC2 = of("aztec2");
	public static final RegistryKey<PaintingVariant> BOMB = of("bomb");
	public static final RegistryKey<PaintingVariant> PLANT = of("plant");
	public static final RegistryKey<PaintingVariant> WASTELAND = of("wasteland");
	public static final RegistryKey<PaintingVariant> POOL = of("pool");
	public static final RegistryKey<PaintingVariant> COURBET = of("courbet");
	public static final RegistryKey<PaintingVariant> SEA = of("sea");
	public static final RegistryKey<PaintingVariant> SUNSET = of("sunset");
	public static final RegistryKey<PaintingVariant> CREEBET = of("creebet");
	public static final RegistryKey<PaintingVariant> WANDERER = of("wanderer");
	public static final RegistryKey<PaintingVariant> GRAHAM = of("graham");
	public static final RegistryKey<PaintingVariant> MATCH = of("match");
	public static final RegistryKey<PaintingVariant> BUST = of("bust");
	public static final RegistryKey<PaintingVariant> STAGE = of("stage");
	public static final RegistryKey<PaintingVariant> VOID = of("void");
	public static final RegistryKey<PaintingVariant> SKULL_AND_ROSES = of("skull_and_roses");
	public static final RegistryKey<PaintingVariant> WITHER = of("wither");
	public static final RegistryKey<PaintingVariant> FIGHTERS = of("fighters");
	public static final RegistryKey<PaintingVariant> POINTER = of("pointer");
	public static final RegistryKey<PaintingVariant> PIGSCENE = of("pigscene");
	public static final RegistryKey<PaintingVariant> BURNING_SKULL = of("burning_skull");
	public static final RegistryKey<PaintingVariant> SKELETON = of("skeleton");
	public static final RegistryKey<PaintingVariant> DONKEY_KONG = of("donkey_kong");
	public static final RegistryKey<PaintingVariant> EARTH = of("earth");
	public static final RegistryKey<PaintingVariant> WIND = of("wind");
	public static final RegistryKey<PaintingVariant> WATER = of("water");
	public static final RegistryKey<PaintingVariant> FIRE = of("fire");

	public static PaintingVariant registerAndGetDefault(Registry<PaintingVariant> registry) {
		Registry.register(registry, KEBAB, new PaintingVariant(16, 16));
		Registry.register(registry, AZTEC, new PaintingVariant(16, 16));
		Registry.register(registry, ALBAN, new PaintingVariant(16, 16));
		Registry.register(registry, AZTEC2, new PaintingVariant(16, 16));
		Registry.register(registry, BOMB, new PaintingVariant(16, 16));
		Registry.register(registry, PLANT, new PaintingVariant(16, 16));
		Registry.register(registry, WASTELAND, new PaintingVariant(16, 16));
		Registry.register(registry, POOL, new PaintingVariant(32, 16));
		Registry.register(registry, COURBET, new PaintingVariant(32, 16));
		Registry.register(registry, SEA, new PaintingVariant(32, 16));
		Registry.register(registry, SUNSET, new PaintingVariant(32, 16));
		Registry.register(registry, CREEBET, new PaintingVariant(32, 16));
		Registry.register(registry, WANDERER, new PaintingVariant(16, 32));
		Registry.register(registry, GRAHAM, new PaintingVariant(16, 32));
		Registry.register(registry, MATCH, new PaintingVariant(32, 32));
		Registry.register(registry, BUST, new PaintingVariant(32, 32));
		Registry.register(registry, STAGE, new PaintingVariant(32, 32));
		Registry.register(registry, VOID, new PaintingVariant(32, 32));
		Registry.register(registry, SKULL_AND_ROSES, new PaintingVariant(32, 32));
		Registry.register(registry, WITHER, new PaintingVariant(32, 32));
		Registry.register(registry, FIGHTERS, new PaintingVariant(64, 32));
		Registry.register(registry, POINTER, new PaintingVariant(64, 64));
		Registry.register(registry, PIGSCENE, new PaintingVariant(64, 64));
		Registry.register(registry, BURNING_SKULL, new PaintingVariant(64, 64));
		Registry.register(registry, SKELETON, new PaintingVariant(64, 48));
		Registry.register(registry, EARTH, new PaintingVariant(32, 32));
		Registry.register(registry, WIND, new PaintingVariant(32, 32));
		Registry.register(registry, WATER, new PaintingVariant(32, 32));
		Registry.register(registry, FIRE, new PaintingVariant(32, 32));
		return Registry.register(registry, DONKEY_KONG, new PaintingVariant(64, 48));
	}

	private static RegistryKey<PaintingVariant> of(String id) {
		return RegistryKey.of(RegistryKeys.PAINTING_VARIANT, new Identifier(id));
	}
}
