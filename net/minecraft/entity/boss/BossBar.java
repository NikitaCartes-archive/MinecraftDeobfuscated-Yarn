/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss;

import java.util.UUID;
import net.minecraft.ChatFormat;
import net.minecraft.network.chat.Component;

public abstract class BossBar {
    private final UUID uuid;
    protected Component name;
    protected float percent;
    protected Color color;
    protected Style style;
    protected boolean darkenSky;
    protected boolean dragonMusic;
    protected boolean thickenFog;

    public BossBar(UUID uUID, Component component, Color color, Style style) {
        this.uuid = uUID;
        this.name = component;
        this.color = color;
        this.style = style;
        this.percent = 1.0f;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Component getName() {
        return this.name;
    }

    public void setName(Component component) {
        this.name = component;
    }

    public float getPercent() {
        return this.percent;
    }

    public void setPercent(float f) {
        this.percent = f;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Style getOverlay() {
        return this.style;
    }

    public void setOverlay(Style style) {
        this.style = style;
    }

    public boolean getDarkenSky() {
        return this.darkenSky;
    }

    public BossBar setDarkenSky(boolean bl) {
        this.darkenSky = bl;
        return this;
    }

    public boolean hasDragonMusic() {
        return this.dragonMusic;
    }

    public BossBar setDragonMusic(boolean bl) {
        this.dragonMusic = bl;
        return this;
    }

    public BossBar setThickenFog(boolean bl) {
        this.thickenFog = bl;
        return this;
    }

    public boolean getThickenFog() {
        return this.thickenFog;
    }

    public static enum Style {
        PROGRESS("progress"),
        NOTCHED_6("notched_6"),
        NOTCHED_10("notched_10"),
        NOTCHED_12("notched_12"),
        NOTCHED_20("notched_20");

        private final String name;

        private Style(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static Style byName(String string) {
            for (Style style : Style.values()) {
                if (!style.name.equals(string)) continue;
                return style;
            }
            return PROGRESS;
        }
    }

    public static enum Color {
        PINK("pink", ChatFormat.RED),
        BLUE("blue", ChatFormat.BLUE),
        RED("red", ChatFormat.DARK_RED),
        GREEN("green", ChatFormat.GREEN),
        YELLOW("yellow", ChatFormat.YELLOW),
        PURPLE("purple", ChatFormat.DARK_BLUE),
        WHITE("white", ChatFormat.WHITE);

        private final String name;
        private final ChatFormat format;

        private Color(String string2, ChatFormat chatFormat) {
            this.name = string2;
            this.format = chatFormat;
        }

        public ChatFormat getTextFormat() {
            return this.format;
        }

        public String getName() {
            return this.name;
        }

        public static Color byName(String string) {
            for (Color color : Color.values()) {
                if (!color.name.equals(string)) continue;
                return color;
            }
            return WHITE;
        }
    }
}

