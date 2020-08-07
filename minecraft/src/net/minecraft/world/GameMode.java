package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum GameMode {
	field_9218(-1, ""),
	field_9215(0, "survival"),
	field_9220(1, "creative"),
	field_9216(2, "adventure"),
	field_9219(3, "spectator");

	private final int id;
	private final String name;

	private GameMode(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public Text getTranslatableName() {
		return new TranslatableText("gameMode." + this.name);
	}

	public void setAbilities(PlayerAbilities abilities) {
		if (this == field_9220) {
			abilities.allowFlying = true;
			abilities.creativeMode = true;
			abilities.invulnerable = true;
		} else if (this == field_9219) {
			abilities.allowFlying = true;
			abilities.creativeMode = false;
			abilities.invulnerable = true;
			abilities.flying = true;
		} else {
			abilities.allowFlying = false;
			abilities.creativeMode = false;
			abilities.invulnerable = false;
			abilities.flying = false;
		}

		abilities.allowModifyWorld = !this.isBlockBreakingRestricted();
	}

	public boolean isBlockBreakingRestricted() {
		return this == field_9216 || this == field_9219;
	}

	public boolean isCreative() {
		return this == field_9220;
	}

	public boolean isSurvivalLike() {
		return this == field_9215 || this == field_9216;
	}

	@Environment(EnvType.CLIENT)
	public float method_31218() {
		return 3.0F;
	}

	@Environment(EnvType.CLIENT)
	public float method_31219() {
		return 6.0F;
	}

	@Environment(EnvType.CLIENT)
	public float method_31220() {
		return this.isCreative() ? 5.0F : 4.5F;
	}

	public static GameMode byId(int id) {
		return byId(id, field_9215);
	}

	public static GameMode byId(int id, GameMode defaultMode) {
		for (GameMode gameMode : values()) {
			if (gameMode.id == id) {
				return gameMode;
			}
		}

		return defaultMode;
	}

	public static GameMode byName(String name) {
		return byName(name, field_9215);
	}

	public static GameMode byName(String name, GameMode defaultMode) {
		for (GameMode gameMode : values()) {
			if (gameMode.name.equals(name)) {
				return gameMode;
			}
		}

		return defaultMode;
	}
}
