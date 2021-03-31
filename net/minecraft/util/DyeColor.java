/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import net.minecraft.block.MapColor;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public enum DyeColor implements StringIdentifiable
{
    WHITE(0, "white", 0xF9FFFE, MapColor.WHITE, 0xF0F0F0, 0xFFFFFF),
    ORANGE(1, "orange", 16351261, MapColor.ORANGE, 15435844, 16738335),
    MAGENTA(2, "magenta", 13061821, MapColor.MAGENTA, 12801229, 0xFF00FF),
    LIGHT_BLUE(3, "light_blue", 3847130, MapColor.LIGHT_BLUE, 6719955, 10141901),
    YELLOW(4, "yellow", 16701501, MapColor.YELLOW, 14602026, 0xFFFF00),
    LIME(5, "lime", 8439583, MapColor.LIME, 4312372, 0xBFFF00),
    PINK(6, "pink", 15961002, MapColor.PINK, 14188952, 16738740),
    GRAY(7, "gray", 4673362, MapColor.GRAY, 0x434343, 0x808080),
    LIGHT_GRAY(8, "light_gray", 0x9D9D97, MapColor.LIGHT_GRAY, 0xABABAB, 0xD3D3D3),
    CYAN(9, "cyan", 1481884, MapColor.CYAN, 2651799, 65535),
    PURPLE(10, "purple", 8991416, MapColor.PURPLE, 8073150, 10494192),
    BLUE(11, "blue", 3949738, MapColor.BLUE, 2437522, 255),
    BROWN(12, "brown", 8606770, MapColor.BROWN, 5320730, 9127187),
    GREEN(13, "green", 6192150, MapColor.GREEN, 3887386, 65280),
    RED(14, "red", 11546150, MapColor.RED, 11743532, 0xFF0000),
    BLACK(15, "black", 0x1D1D21, MapColor.BLACK, 0x1E1B1B, 0);

    private static final DyeColor[] VALUES;
    private static final Int2ObjectOpenHashMap<DyeColor> BY_FIREWORK_COLOR;
    private final int id;
    private final String name;
    private final MapColor mapColor;
    private final float[] colorComponents;
    private final int fireworkColor;
    private final int signColor;

    private DyeColor(int woolId, String name, int color, MapColor mapColor, int fireworkColor, int signColor) {
        this.id = woolId;
        this.name = name;
        this.mapColor = mapColor;
        this.signColor = signColor;
        int j = (color & 0xFF0000) >> 16;
        int k = (color & 0xFF00) >> 8;
        int l = (color & 0xFF) >> 0;
        this.colorComponents = new float[]{(float)j / 255.0f, (float)k / 255.0f, (float)l / 255.0f};
        this.fireworkColor = fireworkColor;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Returns the red, blue and green components of this dye color.
     * 
     * @return an array composed of the red, blue and green floats
     */
    public float[] getColorComponents() {
        return this.colorComponents;
    }

    public MapColor getMapColor() {
        return this.mapColor;
    }

    public int getFireworkColor() {
        return this.fireworkColor;
    }

    public int getSignColor() {
        return this.signColor;
    }

    public static DyeColor byId(int id) {
        if (id < 0 || id >= VALUES.length) {
            id = 0;
        }
        return VALUES[id];
    }

    public static DyeColor byName(String name, DyeColor defaultColor) {
        for (DyeColor dyeColor : DyeColor.values()) {
            if (!dyeColor.name.equals(name)) continue;
            return dyeColor;
        }
        return defaultColor;
    }

    @Nullable
    public static DyeColor byFireworkColor(int color) {
        return BY_FIREWORK_COLOR.get(color);
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    static {
        VALUES = (DyeColor[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).toArray(DyeColor[]::new);
        BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap<DyeColor>(Arrays.stream(DyeColor.values()).collect(Collectors.toMap(dyeColor -> dyeColor.fireworkColor, dyeColor -> dyeColor)));
    }
}

