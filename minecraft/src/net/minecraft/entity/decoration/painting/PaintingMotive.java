package net.minecraft.entity.decoration.painting;

import net.minecraft.util.registry.Registry;

public class PaintingMotive {
	public static final PaintingMotive field_7146 = register("kebab", 16, 16);
	public static final PaintingMotive field_7142 = register("aztec", 16, 16);
	public static final PaintingMotive field_7159 = register("alban", 16, 16);
	public static final PaintingMotive field_7144 = register("aztec2", 16, 16);
	public static final PaintingMotive field_7143 = register("bomb", 16, 16);
	public static final PaintingMotive field_7162 = register("plant", 16, 16);
	public static final PaintingMotive field_7152 = register("wasteland", 16, 16);
	public static final PaintingMotive field_7135 = register("pool", 32, 16);
	public static final PaintingMotive field_7163 = register("courbet", 32, 16);
	public static final PaintingMotive field_7155 = register("sea", 32, 16);
	public static final PaintingMotive field_7136 = register("sunset", 32, 16);
	public static final PaintingMotive field_7161 = register("creebet", 32, 16);
	public static final PaintingMotive field_7154 = register("wanderer", 16, 32);
	public static final PaintingMotive field_7147 = register("graham", 16, 32);
	public static final PaintingMotive field_7150 = register("match", 32, 32);
	public static final PaintingMotive field_7160 = register("bust", 32, 32);
	public static final PaintingMotive field_7145 = register("stage", 32, 32);
	public static final PaintingMotive field_7156 = register("void", 32, 32);
	public static final PaintingMotive field_7157 = register("skull_and_roses", 32, 32);
	public static final PaintingMotive field_7138 = register("wither", 32, 32);
	public static final PaintingMotive field_7153 = register("fighters", 64, 32);
	public static final PaintingMotive field_7164 = register("pointer", 64, 64);
	public static final PaintingMotive field_7141 = register("pigscene", 64, 64);
	public static final PaintingMotive field_7149 = register("burning_skull", 64, 64);
	public static final PaintingMotive field_7158 = register("skeleton", 64, 48);
	public static final PaintingMotive field_7140 = register("donkey_kong", 64, 48);
	private final int width;
	private final int height;

	private static PaintingMotive register(String string, int i, int j) {
		return Registry.register(Registry.MOTIVE, string, new PaintingMotive(i, j));
	}

	public PaintingMotive(int i, int j) {
		this.width = i;
		this.height = j;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}
