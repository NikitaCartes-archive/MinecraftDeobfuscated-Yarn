/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client.model;

import org.jetbrains.annotations.Nullable;

public final class TextureKey {
    public static final TextureKey ALL = TextureKey.method_27043("all");
    public static final TextureKey TEXTURE = TextureKey.method_27044("texture", ALL);
    public static final TextureKey PARTICLE = TextureKey.method_27044("particle", TEXTURE);
    public static final TextureKey END = TextureKey.method_27044("end", ALL);
    public static final TextureKey BOTTOM = TextureKey.method_27044("bottom", END);
    public static final TextureKey TOP = TextureKey.method_27044("top", END);
    public static final TextureKey FRONT = TextureKey.method_27044("front", ALL);
    public static final TextureKey BACK = TextureKey.method_27044("back", ALL);
    public static final TextureKey SIDE = TextureKey.method_27044("side", ALL);
    public static final TextureKey NORTH = TextureKey.method_27044("north", SIDE);
    public static final TextureKey SOUTH = TextureKey.method_27044("south", SIDE);
    public static final TextureKey EAST = TextureKey.method_27044("east", SIDE);
    public static final TextureKey WEST = TextureKey.method_27044("west", SIDE);
    public static final TextureKey UP = TextureKey.method_27043("up");
    public static final TextureKey DOWN = TextureKey.method_27043("down");
    public static final TextureKey CROSS = TextureKey.method_27043("cross");
    public static final TextureKey PLANT = TextureKey.method_27043("plant");
    public static final TextureKey WALL = TextureKey.method_27044("wall", ALL);
    public static final TextureKey RAIL = TextureKey.method_27043("rail");
    public static final TextureKey WOOL = TextureKey.method_27043("wool");
    public static final TextureKey PATTERN = TextureKey.method_27043("pattern");
    public static final TextureKey PANE = TextureKey.method_27043("pane");
    public static final TextureKey EDGE = TextureKey.method_27043("edge");
    public static final TextureKey FAN = TextureKey.method_27043("fan");
    public static final TextureKey STEM = TextureKey.method_27043("stem");
    public static final TextureKey UPPERSTEM = TextureKey.method_27043("upperstem");
    public static final TextureKey CROP = TextureKey.method_27043("crop");
    public static final TextureKey DIRT = TextureKey.method_27043("dirt");
    public static final TextureKey FIRE = TextureKey.method_27043("fire");
    public static final TextureKey LANTERN = TextureKey.method_27043("lantern");
    public static final TextureKey PLATFORM = TextureKey.method_27043("platform");
    public static final TextureKey UNSTICKY = TextureKey.method_27043("unsticky");
    public static final TextureKey TORCH = TextureKey.method_27043("torch");
    public static final TextureKey LAYER0 = TextureKey.method_27043("layer0");
    public static final TextureKey LIT_LOG = TextureKey.method_27043("lit_log");
    private final String name;
    @Nullable
    private final TextureKey parent;

    private static TextureKey method_27043(String string) {
        return new TextureKey(string, null);
    }

    private static TextureKey method_27044(String string, TextureKey textureKey) {
        return new TextureKey(string, textureKey);
    }

    private TextureKey(String string, @Nullable TextureKey textureKey) {
        this.name = string;
        this.parent = textureKey;
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

