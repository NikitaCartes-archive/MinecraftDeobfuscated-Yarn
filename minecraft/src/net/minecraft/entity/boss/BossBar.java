package net.minecraft.entity.boss;

import java.util.UUID;
import net.minecraft.ChatFormat;
import net.minecraft.network.chat.Component;

public abstract class BossBar {
	private final UUID uuid;
	protected Component name;
	protected float percent;
	protected BossBar.Color color;
	protected BossBar.Style style;
	protected boolean darkenSky;
	protected boolean dragonMusic;
	protected boolean thickenFog;

	public BossBar(UUID uUID, Component component, BossBar.Color color, BossBar.Style style) {
		this.uuid = uUID;
		this.name = component;
		this.color = color;
		this.style = style;
		this.percent = 1.0F;
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
		field_5788("pink", ChatFormat.field_1061),
		field_5780("blue", ChatFormat.field_1078),
		field_5784("red", ChatFormat.field_1079),
		field_5785("green", ChatFormat.field_1060),
		field_5782("yellow", ChatFormat.field_1054),
		field_5783("purple", ChatFormat.field_1058),
		field_5786("white", ChatFormat.field_1068);

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

		public static BossBar.Color byName(String string) {
			for (BossBar.Color color : values()) {
				if (color.name.equals(string)) {
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

			return field_5795;
		}
	}
}
