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

	public BossBar(UUID uuid, Text name, BossBar.Color color, BossBar.Style style) {
		this.uuid = uuid;
		this.name = name;
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

	public void setName(Text name) {
		this.name = name;
	}

	public float getPercent() {
		return this.percent;
	}

	public void setPercent(float percentage) {
		this.percent = percentage;
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

	public boolean getThickenFog() {
		return this.thickenFog;
	}

	public static enum Color {
		field_5788("pink", Formatting.field_1061),
		field_5780("blue", Formatting.field_1078),
		field_5784("red", Formatting.field_1079),
		field_5785("green", Formatting.field_1060),
		field_5782("yellow", Formatting.field_1054),
		field_5783("purple", Formatting.field_1058),
		field_5786("white", Formatting.field_1068);

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

			return field_5786;
		}
	}

	public static enum Style {
		field_5795("progress"),
		field_5796("notched_6"),
		field_5791("notched_10"),
		field_5793("notched_12"),
		field_5790("notched_20");

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

			return field_5795;
		}
	}
}
