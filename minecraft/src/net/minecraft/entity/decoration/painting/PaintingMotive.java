package net.minecraft.entity.decoration.painting;

import net.minecraft.util.registry.Registry;

public class PaintingMotive {
	public static final PaintingMotive KEBAB = register("kebab", 16, 16);
	public static final PaintingMotive AZTEC = register("aztec", 16, 16);
	public static final PaintingMotive ALBAN = register("alban", 16, 16);
	public static final PaintingMotive AZTEC2 = register("aztec2", 16, 16);
	public static final PaintingMotive BOMB = register("bomb", 16, 16);
	public static final PaintingMotive PLANT = register("plant", 16, 16);
	public static final PaintingMotive WASTELAND = register("wasteland", 16, 16);
	public static final PaintingMotive POOL = register("pool", 32, 16);
	public static final PaintingMotive COURBET = register("courbet", 32, 16);
	public static final PaintingMotive SEA = register("sea", 32, 16);
	public static final PaintingMotive SUNSET = register("sunset", 32, 16);
	public static final PaintingMotive CREEBET = register("creebet", 32, 16);
	public static final PaintingMotive WANDERER = register("wanderer", 16, 32);
	public static final PaintingMotive GRAHAM = register("graham", 16, 32);
	public static final PaintingMotive MATCH = register("match", 32, 32);
	public static final PaintingMotive BUST = register("bust", 32, 32);
	public static final PaintingMotive STAGE = register("stage", 32, 32);
	public static final PaintingMotive VOID = register("void", 32, 32);
	public static final PaintingMotive SKULL_AND_ROSES = register("skull_and_roses", 32, 32);
	public static final PaintingMotive WITHER = register("wither", 32, 32);
	public static final PaintingMotive FIGHTERS = register("fighters", 64, 32);
	public static final PaintingMotive POINTER = register("pointer", 64, 64);
	public static final PaintingMotive PIGSCENE = register("pigscene", 64, 64);
	public static final PaintingMotive BURNING_SKULL = register("burning_skull", 64, 64);
	public static final PaintingMotive SKELETON = register("skeleton", 64, 48);
	public static final PaintingMotive DONKEY_KONG = register("donkey_kong", 64, 48);
	private final int width;
	private final int height;

	private static PaintingMotive register(String name, int width, int height) {
		return Registry.register(Registry.PAINTING_MOTIVE, name, new PaintingMotive(width, height));
	}

	public PaintingMotive(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}
