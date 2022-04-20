package net.minecraft;

import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class class_7408 {
	public static final RegistryKey<PaintingMotive> KEBAB = method_43407("kebab");
	public static final RegistryKey<PaintingMotive> AZTEC = method_43407("aztec");
	public static final RegistryKey<PaintingMotive> ALBAN = method_43407("alban");
	public static final RegistryKey<PaintingMotive> AZTEC2 = method_43407("aztec2");
	public static final RegistryKey<PaintingMotive> BOMB = method_43407("bomb");
	public static final RegistryKey<PaintingMotive> PLANT = method_43407("plant");
	public static final RegistryKey<PaintingMotive> WASTELAND = method_43407("wasteland");
	public static final RegistryKey<PaintingMotive> POOL = method_43407("pool");
	public static final RegistryKey<PaintingMotive> COURBET = method_43407("courbet");
	public static final RegistryKey<PaintingMotive> SEA = method_43407("sea");
	public static final RegistryKey<PaintingMotive> SUNSET = method_43407("sunset");
	public static final RegistryKey<PaintingMotive> CREEBET = method_43407("creebet");
	public static final RegistryKey<PaintingMotive> WANDERER = method_43407("wanderer");
	public static final RegistryKey<PaintingMotive> GRAHAM = method_43407("graham");
	public static final RegistryKey<PaintingMotive> MATCH = method_43407("match");
	public static final RegistryKey<PaintingMotive> BUST = method_43407("bust");
	public static final RegistryKey<PaintingMotive> STAGE = method_43407("stage");
	public static final RegistryKey<PaintingMotive> VOID = method_43407("void");
	public static final RegistryKey<PaintingMotive> SKULL_AND_ROSES = method_43407("skull_and_roses");
	public static final RegistryKey<PaintingMotive> WITHER = method_43407("wither");
	public static final RegistryKey<PaintingMotive> FIGHTERS = method_43407("fighters");
	public static final RegistryKey<PaintingMotive> POINTER = method_43407("pointer");
	public static final RegistryKey<PaintingMotive> PIGSCENE = method_43407("pigscene");
	public static final RegistryKey<PaintingMotive> BURNING_SKULL = method_43407("burning_skull");
	public static final RegistryKey<PaintingMotive> SKELETON = method_43407("skeleton");
	public static final RegistryKey<PaintingMotive> DONKEY_KONG = method_43407("donkey_kong");
	public static final RegistryKey<PaintingMotive> EARTH = method_43407("earth");
	public static final RegistryKey<PaintingMotive> WIND = method_43407("wind");
	public static final RegistryKey<PaintingMotive> WATER = method_43407("water");
	public static final RegistryKey<PaintingMotive> FIRE = method_43407("fire");

	public static PaintingMotive method_43406(Registry<PaintingMotive> registry) {
		Registry.register(registry, KEBAB, new PaintingMotive(16, 16));
		Registry.register(registry, AZTEC, new PaintingMotive(16, 16));
		Registry.register(registry, ALBAN, new PaintingMotive(16, 16));
		Registry.register(registry, AZTEC2, new PaintingMotive(16, 16));
		Registry.register(registry, BOMB, new PaintingMotive(16, 16));
		Registry.register(registry, PLANT, new PaintingMotive(16, 16));
		Registry.register(registry, WASTELAND, new PaintingMotive(16, 16));
		Registry.register(registry, POOL, new PaintingMotive(32, 16));
		Registry.register(registry, COURBET, new PaintingMotive(32, 16));
		Registry.register(registry, SEA, new PaintingMotive(32, 16));
		Registry.register(registry, SUNSET, new PaintingMotive(32, 16));
		Registry.register(registry, CREEBET, new PaintingMotive(32, 16));
		Registry.register(registry, WANDERER, new PaintingMotive(16, 32));
		Registry.register(registry, GRAHAM, new PaintingMotive(16, 32));
		Registry.register(registry, MATCH, new PaintingMotive(32, 32));
		Registry.register(registry, BUST, new PaintingMotive(32, 32));
		Registry.register(registry, STAGE, new PaintingMotive(32, 32));
		Registry.register(registry, VOID, new PaintingMotive(32, 32));
		Registry.register(registry, SKULL_AND_ROSES, new PaintingMotive(32, 32));
		Registry.register(registry, WITHER, new PaintingMotive(32, 32));
		Registry.register(registry, FIGHTERS, new PaintingMotive(64, 32));
		Registry.register(registry, POINTER, new PaintingMotive(64, 64));
		Registry.register(registry, PIGSCENE, new PaintingMotive(64, 64));
		Registry.register(registry, BURNING_SKULL, new PaintingMotive(64, 64));
		Registry.register(registry, SKELETON, new PaintingMotive(64, 48));
		Registry.register(registry, EARTH, new PaintingMotive(32, 32));
		Registry.register(registry, WIND, new PaintingMotive(32, 32));
		Registry.register(registry, WATER, new PaintingMotive(32, 32));
		Registry.register(registry, FIRE, new PaintingMotive(32, 32));
		return Registry.register(registry, DONKEY_KONG, new PaintingMotive(64, 48));
	}

	private static RegistryKey<PaintingMotive> method_43407(String string) {
		return RegistryKey.of(Registry.MOTIVE_KEY, new Identifier(string));
	}
}
