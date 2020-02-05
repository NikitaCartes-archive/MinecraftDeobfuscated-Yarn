/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.decoration.painting;

import net.minecraft.util.registry.Registry;

public class PaintingMotive {
    public static final PaintingMotive KEBAB = PaintingMotive.register("kebab", 16, 16);
    public static final PaintingMotive AZTEC = PaintingMotive.register("aztec", 16, 16);
    public static final PaintingMotive ALBAN = PaintingMotive.register("alban", 16, 16);
    public static final PaintingMotive AZTEC2 = PaintingMotive.register("aztec2", 16, 16);
    public static final PaintingMotive BOMB = PaintingMotive.register("bomb", 16, 16);
    public static final PaintingMotive PLANT = PaintingMotive.register("plant", 16, 16);
    public static final PaintingMotive WASTELAND = PaintingMotive.register("wasteland", 16, 16);
    public static final PaintingMotive POOL = PaintingMotive.register("pool", 32, 16);
    public static final PaintingMotive COURBET = PaintingMotive.register("courbet", 32, 16);
    public static final PaintingMotive SEA = PaintingMotive.register("sea", 32, 16);
    public static final PaintingMotive SUNSET = PaintingMotive.register("sunset", 32, 16);
    public static final PaintingMotive CREEBET = PaintingMotive.register("creebet", 32, 16);
    public static final PaintingMotive WANDERER = PaintingMotive.register("wanderer", 16, 32);
    public static final PaintingMotive GRAHAM = PaintingMotive.register("graham", 16, 32);
    public static final PaintingMotive MATCH = PaintingMotive.register("match", 32, 32);
    public static final PaintingMotive BUST = PaintingMotive.register("bust", 32, 32);
    public static final PaintingMotive STAGE = PaintingMotive.register("stage", 32, 32);
    public static final PaintingMotive VOID = PaintingMotive.register("void", 32, 32);
    public static final PaintingMotive SKULL_AND_ROSES = PaintingMotive.register("skull_and_roses", 32, 32);
    public static final PaintingMotive WITHER = PaintingMotive.register("wither", 32, 32);
    public static final PaintingMotive FIGHTERS = PaintingMotive.register("fighters", 64, 32);
    public static final PaintingMotive POINTER = PaintingMotive.register("pointer", 64, 64);
    public static final PaintingMotive PIGSCENE = PaintingMotive.register("pigscene", 64, 64);
    public static final PaintingMotive BURNING_SKULL = PaintingMotive.register("burning_skull", 64, 64);
    public static final PaintingMotive SKELETON = PaintingMotive.register("skeleton", 64, 48);
    public static final PaintingMotive DONKEY_KONG = PaintingMotive.register("donkey_kong", 64, 48);
    private final int width;
    private final int height;

    private static PaintingMotive register(String string, int i, int j) {
        return Registry.register(Registry.PAINTING_MOTIVE, string, new PaintingMotive(i, j));
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

