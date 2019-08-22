package net.minecraft.entity.boss;

import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class BossBar {
	private final UUID uuid;
	protected Text name;
	protected float percent;
	protected BossBar.Color color;
	protected BossBar.Style style;
	protected boolean darkenSky;
	protected boolean dragonMusic;
	protected boolean thickenFog;

	public BossBar(UUID uUID, Text text, BossBar.Color color, BossBar.Style style) {
		this.uuid = uUID;
		this.name = text;
		this.color = color;
		this.style = style;
		this.percent = 1.0F;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public Text getName() {
		return this.name;
	}

	public void setName(Text text) {
		this.name = text;
	}

	public float getPercent() {
		return this.percent;
	}

	public void setPercent(float f) {
		this.percent = f;
	}

	public BossBar.Color getColor() {
		return this.color;
	}

	public void setColor(BossBar.Color color) {
		this.color = color;
	}

	public BossBar.Style getOverlay() {
		return this.style;
	}

	public void setOverlay(BossBar.Style style) {
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

		private Color(String string2, Formatting formatting) {
			this.name = string2;
			this.format = formatting;
		}

		public Formatting getTextFormat() {
			return this.format;
		}

		public String getName() {
			return this.name;
		}

		public static BossBar.Color byName(String string) {
			for (BossBar.Color color : values()) {
				if (color.name.equals(string)) {
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

		private Style(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}

		public static BossBar.Style byName(String string) {
			for (BossBar.Style style : values()) {
				if (style.name.equals(string)) {
					return style;
				}
			}

			return PROGRESS;
		}
	}
}
