package net.minecraft.entity.boss;

import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

public abstract class BossBar {
	private final UUID uuid;
	protected Text name;
	protected float percent;
	protected BossBar.Color color;
	protected BossBar.Style style;
	protected Vec3d field_50378;
	protected int field_50379;
	protected boolean darkenSky;
	protected boolean dragonMusic;
	protected boolean thickenFog;

	public BossBar(UUID uuid, Text name, BossBar.Color color, BossBar.Style style, Vec3d vec3d, int i) {
		this.uuid = uuid;
		this.name = name;
		this.color = color;
		this.style = style;
		this.field_50378 = vec3d;
		this.field_50379 = i;
		this.percent = 1.0F;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public Text getName() {
		return this.name;
	}

	public void setName(Text name) {
		this.name = name;
	}

	public float getPercent() {
		return this.percent;
	}

	public void setPercent(float percent) {
		this.percent = percent;
	}

	public BossBar.Color getColor() {
		return this.color;
	}

	public void setColor(BossBar.Color color) {
		this.color = color;
	}

	public BossBar.Style getStyle() {
		return this.style;
	}

	public void setStyle(BossBar.Style style) {
		this.style = style;
	}

	public boolean shouldDarkenSky() {
		return this.darkenSky;
	}

	public BossBar setDarkenSky(boolean darkenSky) {
		this.darkenSky = darkenSky;
		return this;
	}

	public boolean hasDragonMusic() {
		return this.dragonMusic;
	}

	public BossBar setDragonMusic(boolean dragonMusic) {
		this.dragonMusic = dragonMusic;
		return this;
	}

	public BossBar setThickenFog(boolean thickenFog) {
		this.thickenFog = thickenFog;
		return this;
	}

	public boolean shouldThickenFog() {
		return this.thickenFog;
	}

	public void method_58785(Vec3d vec3d, int i) {
		this.field_50378 = vec3d;
		this.field_50379 = i;
	}

	public Vec3d method_58815() {
		return this.field_50378;
	}

	public int method_58816() {
		return this.field_50379;
	}

	public static enum Color {
		PINK("pink", Formatting.RED),
		BLUE("blue", Formatting.BLUE),
		RED("red", Formatting.DARK_RED),
		GREEN("green", Formatting.GREEN),
		YELLOW("yellow", Formatting.YELLOW),
		PURPLE("purple", Formatting.DARK_BLUE),
		WHITE("white", Formatting.WHITE);

		private final String name;
		private final Formatting format;

		private Color(String name, Formatting format) {
			this.name = name;
			this.format = format;
		}

		public Formatting getTextFormat() {
			return this.format;
		}

		public String getName() {
			return this.name;
		}

		public static BossBar.Color byName(String name) {
			for (BossBar.Color color : values()) {
				if (color.name.equals(name)) {
					return color;
				}
			}

			return WHITE;
		}
	}

	public static enum Style {
		PROGRESS("progress"),
		NOTCHED_6("notched_6"),
		NOTCHED_10("notched_10"),
		NOTCHED_12("notched_12"),
		NOTCHED_20("notched_20");

		private final String name;

		private Style(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static BossBar.Style byName(String name) {
			for (BossBar.Style style : values()) {
				if (style.name.equals(name)) {
					return style;
				}
			}

			return PROGRESS;
		}
	}
}
