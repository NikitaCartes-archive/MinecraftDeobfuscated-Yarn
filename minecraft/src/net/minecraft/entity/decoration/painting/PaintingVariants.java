package net.minecraft.entity.decoration.painting;

import net.minecraft.registry.Registerable;
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
	public static final RegistryKey<PaintingVariant> BAROQUE = of("baroque");
	public static final RegistryKey<PaintingVariant> HUMBLE = of("humble");
	public static final RegistryKey<PaintingVariant> MEDITATIVE = of("meditative");
	public static final RegistryKey<PaintingVariant> PRAIRIE_RIDE = of("prairie_ride");
	public static final RegistryKey<PaintingVariant> UNPACKED = of("unpacked");
	public static final RegistryKey<PaintingVariant> BACKYARD = of("backyard");
	public static final RegistryKey<PaintingVariant> BOUQUET = of("bouquet");
	public static final RegistryKey<PaintingVariant> CAVEBIRD = of("cavebird");
	public static final RegistryKey<PaintingVariant> CHANGING = of("changing");
	public static final RegistryKey<PaintingVariant> COTAN = of("cotan");
	public static final RegistryKey<PaintingVariant> ENDBOSS = of("endboss");
	public static final RegistryKey<PaintingVariant> FERN = of("fern");
	public static final RegistryKey<PaintingVariant> FINDING = of("finding");
	public static final RegistryKey<PaintingVariant> LOWMIST = of("lowmist");
	public static final RegistryKey<PaintingVariant> ORB = of("orb");
	public static final RegistryKey<PaintingVariant> OWLEMONS = of("owlemons");
	public static final RegistryKey<PaintingVariant> PASSAGE = of("passage");
	public static final RegistryKey<PaintingVariant> POND = of("pond");
	public static final RegistryKey<PaintingVariant> SUNFLOWERS = of("sunflowers");
	public static final RegistryKey<PaintingVariant> TIDES = of("tides");

	public static void bootstrap(Registerable<PaintingVariant> registry) {
		register(registry, KEBAB, 1, 1);
		register(registry, AZTEC, 1, 1);
		register(registry, ALBAN, 1, 1);
		register(registry, AZTEC2, 1, 1);
		register(registry, BOMB, 1, 1);
		register(registry, PLANT, 1, 1);
		register(registry, WASTELAND, 1, 1);
		register(registry, POOL, 2, 1);
		register(registry, COURBET, 2, 1);
		register(registry, SEA, 2, 1);
		register(registry, SUNSET, 2, 1);
		register(registry, CREEBET, 2, 1);
		register(registry, WANDERER, 1, 2);
		register(registry, GRAHAM, 1, 2);
		register(registry, MATCH, 2, 2);
		register(registry, BUST, 2, 2);
		register(registry, STAGE, 2, 2);
		register(registry, VOID, 2, 2);
		register(registry, SKULL_AND_ROSES, 2, 2);
		register(registry, WITHER, 2, 2);
		register(registry, FIGHTERS, 4, 2);
		register(registry, POINTER, 4, 4);
		register(registry, PIGSCENE, 4, 4);
		register(registry, BURNING_SKULL, 4, 4);
		register(registry, SKELETON, 4, 3);
		register(registry, EARTH, 2, 2);
		register(registry, WIND, 2, 2);
		register(registry, WATER, 2, 2);
		register(registry, FIRE, 2, 2);
		register(registry, DONKEY_KONG, 4, 3);
		register(registry, BAROQUE, 2, 2);
		register(registry, HUMBLE, 2, 2);
		register(registry, MEDITATIVE, 1, 1);
		register(registry, PRAIRIE_RIDE, 1, 2);
		register(registry, UNPACKED, 4, 4);
		register(registry, BACKYARD, 3, 4);
		register(registry, BOUQUET, 3, 3);
		register(registry, CAVEBIRD, 3, 3);
		register(registry, CHANGING, 4, 2);
		register(registry, COTAN, 3, 3);
		register(registry, ENDBOSS, 3, 3);
		register(registry, FERN, 3, 3);
		register(registry, FINDING, 4, 2);
		register(registry, LOWMIST, 4, 2);
		register(registry, ORB, 4, 4);
		register(registry, OWLEMONS, 3, 3);
		register(registry, PASSAGE, 4, 2);
		register(registry, POND, 3, 4);
		register(registry, SUNFLOWERS, 3, 3);
		register(registry, TIDES, 3, 3);
	}

	private static void register(Registerable<PaintingVariant> registry, RegistryKey<PaintingVariant> key, int width, int height) {
		registry.register(key, new PaintingVariant(width, height, key.getValue()));
	}

	private static RegistryKey<PaintingVariant> of(String id) {
		return RegistryKey.of(RegistryKeys.PAINTING_VARIANT, Identifier.ofVanilla(id));
	}
}
