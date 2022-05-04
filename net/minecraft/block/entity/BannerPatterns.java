/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class BannerPatterns {
    public static final RegistryKey<BannerPattern> BASE = BannerPatterns.of("base");
    public static final RegistryKey<BannerPattern> SQUARE_BOTTOM_LEFT = BannerPatterns.of("square_bottom_left");
    public static final RegistryKey<BannerPattern> SQUARE_BOTTOM_RIGHT = BannerPatterns.of("square_bottom_right");
    public static final RegistryKey<BannerPattern> SQUARE_TOP_LEFT = BannerPatterns.of("square_top_left");
    public static final RegistryKey<BannerPattern> SQUARE_TOP_RIGHT = BannerPatterns.of("square_top_right");
    public static final RegistryKey<BannerPattern> STRIPE_BOTTOM = BannerPatterns.of("stripe_bottom");
    public static final RegistryKey<BannerPattern> STRIPE_TOP = BannerPatterns.of("stripe_top");
    public static final RegistryKey<BannerPattern> STRIPE_LEFT = BannerPatterns.of("stripe_left");
    public static final RegistryKey<BannerPattern> STRIPE_RIGHT = BannerPatterns.of("stripe_right");
    public static final RegistryKey<BannerPattern> STRIPE_CENTER = BannerPatterns.of("stripe_center");
    public static final RegistryKey<BannerPattern> STRIPE_MIDDLE = BannerPatterns.of("stripe_middle");
    public static final RegistryKey<BannerPattern> STRIPE_DOWNRIGHT = BannerPatterns.of("stripe_downright");
    public static final RegistryKey<BannerPattern> STRIPE_DOWNLEFT = BannerPatterns.of("stripe_downleft");
    public static final RegistryKey<BannerPattern> SMALL_STRIPES = BannerPatterns.of("small_stripes");
    public static final RegistryKey<BannerPattern> CROSS = BannerPatterns.of("cross");
    public static final RegistryKey<BannerPattern> STRAIGHT_CROSS = BannerPatterns.of("straight_cross");
    public static final RegistryKey<BannerPattern> TRIANGLE_BOTTOM = BannerPatterns.of("triangle_bottom");
    public static final RegistryKey<BannerPattern> TRIANGLE_TOP = BannerPatterns.of("triangle_top");
    public static final RegistryKey<BannerPattern> TRIANGLES_BOTTOM = BannerPatterns.of("triangles_bottom");
    public static final RegistryKey<BannerPattern> TRIANGLES_TOP = BannerPatterns.of("triangles_top");
    public static final RegistryKey<BannerPattern> DIAGONAL_LEFT = BannerPatterns.of("diagonal_left");
    public static final RegistryKey<BannerPattern> DIAGONAL_UP_RIGHT = BannerPatterns.of("diagonal_up_right");
    public static final RegistryKey<BannerPattern> DIAGONAL_UP_LEFT = BannerPatterns.of("diagonal_up_left");
    public static final RegistryKey<BannerPattern> DIAGONAL_RIGHT = BannerPatterns.of("diagonal_right");
    public static final RegistryKey<BannerPattern> CIRCLE = BannerPatterns.of("circle");
    public static final RegistryKey<BannerPattern> RHOMBUS = BannerPatterns.of("rhombus");
    public static final RegistryKey<BannerPattern> HALF_VERTICAL = BannerPatterns.of("half_vertical");
    public static final RegistryKey<BannerPattern> HALF_HORIZONTAL = BannerPatterns.of("half_horizontal");
    public static final RegistryKey<BannerPattern> HALF_VERTICAL_RIGHT = BannerPatterns.of("half_vertical_right");
    public static final RegistryKey<BannerPattern> HALF_HORIZONTAL_BOTTOM = BannerPatterns.of("half_horizontal_bottom");
    public static final RegistryKey<BannerPattern> BORDER = BannerPatterns.of("border");
    public static final RegistryKey<BannerPattern> CURLY_BORDER = BannerPatterns.of("curly_border");
    public static final RegistryKey<BannerPattern> GRADIENT = BannerPatterns.of("gradient");
    public static final RegistryKey<BannerPattern> GRADIENT_UP = BannerPatterns.of("gradient_up");
    public static final RegistryKey<BannerPattern> BRICKS = BannerPatterns.of("bricks");
    public static final RegistryKey<BannerPattern> GLOBE = BannerPatterns.of("globe");
    public static final RegistryKey<BannerPattern> CREEPER = BannerPatterns.of("creeper");
    public static final RegistryKey<BannerPattern> SKULL = BannerPatterns.of("skull");
    public static final RegistryKey<BannerPattern> FLOWER = BannerPatterns.of("flower");
    public static final RegistryKey<BannerPattern> MOJANG = BannerPatterns.of("mojang");
    public static final RegistryKey<BannerPattern> PIGLIN = BannerPatterns.of("piglin");

    private static RegistryKey<BannerPattern> of(String id) {
        return RegistryKey.of(Registry.BANNER_PATTERN_KEY, new Identifier(id));
    }

    public static BannerPattern initAndGetDefault(Registry<BannerPattern> registry) {
        Registry.register(registry, BASE, new BannerPattern("b"));
        Registry.register(registry, SQUARE_BOTTOM_LEFT, new BannerPattern("bl"));
        Registry.register(registry, SQUARE_BOTTOM_RIGHT, new BannerPattern("br"));
        Registry.register(registry, SQUARE_TOP_LEFT, new BannerPattern("tl"));
        Registry.register(registry, SQUARE_TOP_RIGHT, new BannerPattern("tr"));
        Registry.register(registry, STRIPE_BOTTOM, new BannerPattern("bs"));
        Registry.register(registry, STRIPE_TOP, new BannerPattern("ts"));
        Registry.register(registry, STRIPE_LEFT, new BannerPattern("ls"));
        Registry.register(registry, STRIPE_RIGHT, new BannerPattern("rs"));
        Registry.register(registry, STRIPE_CENTER, new BannerPattern("cs"));
        Registry.register(registry, STRIPE_MIDDLE, new BannerPattern("ms"));
        Registry.register(registry, STRIPE_DOWNRIGHT, new BannerPattern("drs"));
        Registry.register(registry, STRIPE_DOWNLEFT, new BannerPattern("dls"));
        Registry.register(registry, SMALL_STRIPES, new BannerPattern("ss"));
        Registry.register(registry, CROSS, new BannerPattern("cr"));
        Registry.register(registry, STRAIGHT_CROSS, new BannerPattern("sc"));
        Registry.register(registry, TRIANGLE_BOTTOM, new BannerPattern("bt"));
        Registry.register(registry, TRIANGLE_TOP, new BannerPattern("tt"));
        Registry.register(registry, TRIANGLES_BOTTOM, new BannerPattern("bts"));
        Registry.register(registry, TRIANGLES_TOP, new BannerPattern("tts"));
        Registry.register(registry, DIAGONAL_LEFT, new BannerPattern("ld"));
        Registry.register(registry, DIAGONAL_UP_RIGHT, new BannerPattern("rd"));
        Registry.register(registry, DIAGONAL_UP_LEFT, new BannerPattern("lud"));
        Registry.register(registry, DIAGONAL_RIGHT, new BannerPattern("rud"));
        Registry.register(registry, CIRCLE, new BannerPattern("mc"));
        Registry.register(registry, RHOMBUS, new BannerPattern("mr"));
        Registry.register(registry, HALF_VERTICAL, new BannerPattern("vh"));
        Registry.register(registry, HALF_HORIZONTAL, new BannerPattern("hh"));
        Registry.register(registry, HALF_VERTICAL_RIGHT, new BannerPattern("vhr"));
        Registry.register(registry, HALF_HORIZONTAL_BOTTOM, new BannerPattern("hhb"));
        Registry.register(registry, BORDER, new BannerPattern("bo"));
        Registry.register(registry, CURLY_BORDER, new BannerPattern("cbo"));
        Registry.register(registry, GRADIENT, new BannerPattern("gra"));
        Registry.register(registry, GRADIENT_UP, new BannerPattern("gru"));
        Registry.register(registry, BRICKS, new BannerPattern("bri"));
        Registry.register(registry, GLOBE, new BannerPattern("glb"));
        Registry.register(registry, CREEPER, new BannerPattern("cre"));
        Registry.register(registry, SKULL, new BannerPattern("sku"));
        Registry.register(registry, FLOWER, new BannerPattern("flo"));
        Registry.register(registry, MOJANG, new BannerPattern("moj"));
        return Registry.register(registry, PIGLIN, new BannerPattern("pig"));
    }
}

