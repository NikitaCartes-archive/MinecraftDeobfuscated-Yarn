package net.minecraft.block.entity;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class BannerPatterns {
	public static final RegistryKey<BannerPattern> BASE = of("base");
	public static final RegistryKey<BannerPattern> SQUARE_BOTTOM_LEFT = of("square_bottom_left");
	public static final RegistryKey<BannerPattern> SQUARE_BOTTOM_RIGHT = of("square_bottom_right");
	public static final RegistryKey<BannerPattern> SQUARE_TOP_LEFT = of("square_top_left");
	public static final RegistryKey<BannerPattern> SQUARE_TOP_RIGHT = of("square_top_right");
	public static final RegistryKey<BannerPattern> STRIPE_BOTTOM = of("stripe_bottom");
	public static final RegistryKey<BannerPattern> STRIPE_TOP = of("stripe_top");
	public static final RegistryKey<BannerPattern> STRIPE_LEFT = of("stripe_left");
	public static final RegistryKey<BannerPattern> STRIPE_RIGHT = of("stripe_right");
	public static final RegistryKey<BannerPattern> STRIPE_CENTER = of("stripe_center");
	public static final RegistryKey<BannerPattern> STRIPE_MIDDLE = of("stripe_middle");
	public static final RegistryKey<BannerPattern> STRIPE_DOWNRIGHT = of("stripe_downright");
	public static final RegistryKey<BannerPattern> STRIPE_DOWNLEFT = of("stripe_downleft");
	public static final RegistryKey<BannerPattern> SMALL_STRIPES = of("small_stripes");
	public static final RegistryKey<BannerPattern> CROSS = of("cross");
	public static final RegistryKey<BannerPattern> STRAIGHT_CROSS = of("straight_cross");
	public static final RegistryKey<BannerPattern> TRIANGLE_BOTTOM = of("triangle_bottom");
	public static final RegistryKey<BannerPattern> TRIANGLE_TOP = of("triangle_top");
	public static final RegistryKey<BannerPattern> TRIANGLES_BOTTOM = of("triangles_bottom");
	public static final RegistryKey<BannerPattern> TRIANGLES_TOP = of("triangles_top");
	public static final RegistryKey<BannerPattern> DIAGONAL_LEFT = of("diagonal_left");
	public static final RegistryKey<BannerPattern> DIAGONAL_UP_RIGHT = of("diagonal_up_right");
	public static final RegistryKey<BannerPattern> DIAGONAL_UP_LEFT = of("diagonal_up_left");
	public static final RegistryKey<BannerPattern> DIAGONAL_RIGHT = of("diagonal_right");
	public static final RegistryKey<BannerPattern> CIRCLE = of("circle");
	public static final RegistryKey<BannerPattern> RHOMBUS = of("rhombus");
	public static final RegistryKey<BannerPattern> HALF_VERTICAL = of("half_vertical");
	public static final RegistryKey<BannerPattern> HALF_HORIZONTAL = of("half_horizontal");
	public static final RegistryKey<BannerPattern> HALF_VERTICAL_RIGHT = of("half_vertical_right");
	public static final RegistryKey<BannerPattern> HALF_HORIZONTAL_BOTTOM = of("half_horizontal_bottom");
	public static final RegistryKey<BannerPattern> BORDER = of("border");
	public static final RegistryKey<BannerPattern> CURLY_BORDER = of("curly_border");
	public static final RegistryKey<BannerPattern> GRADIENT = of("gradient");
	public static final RegistryKey<BannerPattern> GRADIENT_UP = of("gradient_up");
	public static final RegistryKey<BannerPattern> BRICKS = of("bricks");
	public static final RegistryKey<BannerPattern> GLOBE = of("globe");
	public static final RegistryKey<BannerPattern> CREEPER = of("creeper");
	public static final RegistryKey<BannerPattern> SKULL = of("skull");
	public static final RegistryKey<BannerPattern> FLOWER = of("flower");
	public static final RegistryKey<BannerPattern> MOJANG = of("mojang");
	public static final RegistryKey<BannerPattern> PIGLIN = of("piglin");

	private static RegistryKey<BannerPattern> of(String id) {
		return RegistryKey.of(RegistryKeys.BANNER_PATTERN, new Identifier(id));
	}

	public static BannerPattern registerAndGetDefault(Registry<BannerPattern> registry) {
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
