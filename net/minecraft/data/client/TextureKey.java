/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client;

import org.jetbrains.annotations.Nullable;

public final class TextureKey {
    public static final TextureKey ALL = TextureKey.of("all");
    public static final TextureKey TEXTURE = TextureKey.of("texture", ALL);
    public static final TextureKey PARTICLE = TextureKey.of("particle", TEXTURE);
    public static final TextureKey END = TextureKey.of("end", ALL);
    public static final TextureKey BOTTOM = TextureKey.of("bottom", END);
    public static final TextureKey TOP = TextureKey.of("top", END);
    public static final TextureKey FRONT = TextureKey.of("front", ALL);
    public static final TextureKey BACK = TextureKey.of("back", ALL);
    public static final TextureKey SIDE = TextureKey.of("side", ALL);
    public static final TextureKey NORTH = TextureKey.of("north", SIDE);
    public static final TextureKey SOUTH = TextureKey.of("south", SIDE);
    public static final TextureKey EAST = TextureKey.of("east", SIDE);
    public static final TextureKey WEST = TextureKey.of("west", SIDE);
    public static final TextureKey UP = TextureKey.of("up");
    public static final TextureKey DOWN = TextureKey.of("down");
    public static final TextureKey CROSS = TextureKey.of("cross");
    public static final TextureKey PLANT = TextureKey.of("plant");
    public static final TextureKey WALL = TextureKey.of("wall", ALL);
    public static final TextureKey RAIL = TextureKey.of("rail");
    public static final TextureKey WOOL = TextureKey.of("wool");
    public static final TextureKey PATTERN = TextureKey.of("pattern");
    public static final TextureKey PANE = TextureKey.of("pane");
    public static final TextureKey EDGE = TextureKey.of("edge");
    public static final TextureKey FAN = TextureKey.of("fan");
    public static final TextureKey STEM = TextureKey.of("stem");
    public static final TextureKey UPPERSTEM = TextureKey.of("upperstem");
    public static final TextureKey CROP = TextureKey.of("crop");
    public static final TextureKey DIRT = TextureKey.of("dirt");
    public static final TextureKey FIRE = TextureKey.of("fire");
    public static final TextureKey LANTERN = TextureKey.of("lantern");
    public static final TextureKey PLATFORM = TextureKey.of("platform");
    public static final TextureKey UNSTICKY = TextureKey.of("unsticky");
    public static final TextureKey TORCH = TextureKey.of("torch");
    public static final TextureKey LAYER0 = TextureKey.of("layer0");
    public static final TextureKey LIT_LOG = TextureKey.of("lit_log");
    public static final TextureKey CANDLE = TextureKey.of("candle");
    public static final TextureKey INSIDE = TextureKey.of("inside");
    public static final TextureKey CONTENT = TextureKey.of("content");
    public static final TextureKey INNER_TOP = TextureKey.of("inner_top");
    private final String name;
    @Nullable
    private final TextureKey parent;

    private static TextureKey of(String name) {
        return new TextureKey(name, null);
    }

    private static TextureKey of(String name, TextureKey parent) {
        return new TextureKey(name, parent);
    }

    private TextureKey(String name, @Nullable TextureKey parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public TextureKey getParent() {
        return this.parent;
    }

    public String toString() {
        return "#" + this.name;
    }
}

