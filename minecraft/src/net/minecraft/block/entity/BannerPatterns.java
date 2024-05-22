package net.minecraft.block.entity;

import net.minecraft.registry.Registerable;
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
	public static final RegistryKey<BannerPattern> FLOW = of("flow");
	public static final RegistryKey<BannerPattern> GUSTER = of("guster");

	private static RegistryKey<BannerPattern> of(String id) {
		return RegistryKey.of(RegistryKeys.BANNER_PATTERN, Identifier.ofVanilla(id));
	}

	public static void bootstrap(Registerable<BannerPattern> registry) {
		register(registry, BASE);
		register(registry, SQUARE_BOTTOM_LEFT);
		register(registry, SQUARE_BOTTOM_RIGHT);
		register(registry, SQUARE_TOP_LEFT);
		register(registry, SQUARE_TOP_RIGHT);
		register(registry, STRIPE_BOTTOM);
		register(registry, STRIPE_TOP);
		register(registry, STRIPE_LEFT);
		register(registry, STRIPE_RIGHT);
		register(registry, STRIPE_CENTER);
		register(registry, STRIPE_MIDDLE);
		register(registry, STRIPE_DOWNRIGHT);
		register(registry, STRIPE_DOWNLEFT);
		register(registry, SMALL_STRIPES);
		register(registry, CROSS);
		register(registry, STRAIGHT_CROSS);
		register(registry, TRIANGLE_BOTTOM);
		register(registry, TRIANGLE_TOP);
		register(registry, TRIANGLES_BOTTOM);
		register(registry, TRIANGLES_TOP);
		register(registry, DIAGONAL_LEFT);
		register(registry, DIAGONAL_UP_RIGHT);
		register(registry, DIAGONAL_UP_LEFT);
		register(registry, DIAGONAL_RIGHT);
		register(registry, CIRCLE);
		register(registry, RHOMBUS);
		register(registry, HALF_VERTICAL);
		register(registry, HALF_HORIZONTAL);
		register(registry, HALF_VERTICAL_RIGHT);
		register(registry, HALF_HORIZONTAL_BOTTOM);
		register(registry, BORDER);
		register(registry, CURLY_BORDER);
		register(registry, GRADIENT);
		register(registry, GRADIENT_UP);
		register(registry, BRICKS);
		register(registry, GLOBE);
		register(registry, CREEPER);
		register(registry, SKULL);
		register(registry, FLOWER);
		register(registry, MOJANG);
		register(registry, PIGLIN);
		register(registry, FLOW);
		register(registry, GUSTER);
	}

	public static void register(Registerable<BannerPattern> registry, RegistryKey<BannerPattern> key) {
		registry.register(key, new BannerPattern(key.getValue(), "block.minecraft.banner." + key.getValue().toShortTranslationKey()));
	}
}
