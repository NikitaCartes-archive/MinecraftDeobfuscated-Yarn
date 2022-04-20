/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.decoration.painting;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class PaintingVariants {
    public static final RegistryKey<PaintingVariant> KEBAB = PaintingVariants.of("kebab");
    public static final RegistryKey<PaintingVariant> AZTEC = PaintingVariants.of("aztec");
    public static final RegistryKey<PaintingVariant> ALBAN = PaintingVariants.of("alban");
    public static final RegistryKey<PaintingVariant> AZTEC2 = PaintingVariants.of("aztec2");
    public static final RegistryKey<PaintingVariant> BOMB = PaintingVariants.of("bomb");
    public static final RegistryKey<PaintingVariant> PLANT = PaintingVariants.of("plant");
    public static final RegistryKey<PaintingVariant> WASTELAND = PaintingVariants.of("wasteland");
    public static final RegistryKey<PaintingVariant> POOL = PaintingVariants.of("pool");
    public static final RegistryKey<PaintingVariant> COURBET = PaintingVariants.of("courbet");
    public static final RegistryKey<PaintingVariant> SEA = PaintingVariants.of("sea");
    public static final RegistryKey<PaintingVariant> SUNSET = PaintingVariants.of("sunset");
    public static final RegistryKey<PaintingVariant> CREEBET = PaintingVariants.of("creebet");
    public static final RegistryKey<PaintingVariant> WANDERER = PaintingVariants.of("wanderer");
    public static final RegistryKey<PaintingVariant> GRAHAM = PaintingVariants.of("graham");
    public static final RegistryKey<PaintingVariant> MATCH = PaintingVariants.of("match");
    public static final RegistryKey<PaintingVariant> BUST = PaintingVariants.of("bust");
    public static final RegistryKey<PaintingVariant> STAGE = PaintingVariants.of("stage");
    public static final RegistryKey<PaintingVariant> VOID = PaintingVariants.of("void");
    public static final RegistryKey<PaintingVariant> SKULL_AND_ROSES = PaintingVariants.of("skull_and_roses");
    public static final RegistryKey<PaintingVariant> WITHER = PaintingVariants.of("wither");
    public static final RegistryKey<PaintingVariant> FIGHTERS = PaintingVariants.of("fighters");
    public static final RegistryKey<PaintingVariant> POINTER = PaintingVariants.of("pointer");
    public static final RegistryKey<PaintingVariant> PIGSCENE = PaintingVariants.of("pigscene");
    public static final RegistryKey<PaintingVariant> BURNING_SKULL = PaintingVariants.of("burning_skull");
    public static final RegistryKey<PaintingVariant> SKELETON = PaintingVariants.of("skeleton");
    public static final RegistryKey<PaintingVariant> DONKEY_KONG = PaintingVariants.of("donkey_kong");
    public static final RegistryKey<PaintingVariant> EARTH = PaintingVariants.of("earth");
    public static final RegistryKey<PaintingVariant> WIND = PaintingVariants.of("wind");
    public static final RegistryKey<PaintingVariant> WATER = PaintingVariants.of("water");
    public static final RegistryKey<PaintingVariant> FIRE = PaintingVariants.of("fire");

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
        return RegistryKey.of(Registry.PAINTING_VARIANT_KEY, new Identifier(id));
    }
}

