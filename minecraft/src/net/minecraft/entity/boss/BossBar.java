package net.minecraft.entity.boss;

import java.util.UUID;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;

public abstract class BossBar {
	private final UUID uuid;
	protected TextComponent name;
	protected float percent;
	protected BossBar.Color color;
	protected BossBar.Overlay overlay;
	protected boolean darkenSky;
	protected boolean dragonMusic;
	protected boolean thickenFog;

	public BossBar(UUID uUID, TextComponent textComponent, BossBar.Color color, BossBar.Overlay overlay) {
		this.uuid = uUID;
		this.name = textComponent;
		this.color = color;
		this.overlay = overlay;
		this.percent = 1.0F;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public TextComponent getName() {
		return this.name;
	}

	public void setName(TextComponent textComponent) {
		this.name = textComponent;
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

	public BossBar.Overlay getOverlay() {
		return this.overlay;
	}

	public void setOverlay(BossBar.Overlay overlay) {
		this.overlay = overlay;
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
		field_5788("pink", TextFormat.RED),
		field_5780("blue", TextFormat.BLUE),
		field_5784("red", TextFormat.DARK_RED),
		field_5785("green", TextFormat.GREEN),
		field_5782("yellow", TextFormat.YELLOW),
		field_5783("purple", TextFormat.DARK_BLUE),
		field_5786("white", TextFormat.WHITE);

		private final String name;
		private final TextFormat format;

		private Color(String string2, TextFormat textFormat) {
			this.name = string2;
			this.format = textFormat;
		}

		public TextFormat getTextFormat() {
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

			return field_5786;
		}
	}

	public static enum Overlay {
		field_5795("progress"),
		field_5796("notched_6"),
		field_5791("notched_10"),
		field_5793("notched_12"),
		field_5790("notched_20");

		private final String name;

		private Overlay(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}

		public static BossBar.Overlay byName(String string) {
			for (BossBar.Overlay overlay : values()) {
				if (overlay.name.equals(string)) {
					return overlay;
				}
			}

			return field_5795;
		}
	}
}
