package net.minecraft.world;

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

	private GameMode(int j, String string2) {
		this.id = j;
		this.name = string2;
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

	public void setAbilitites(PlayerAbilities playerAbilities) {
		if (this == field_9220) {
			playerAbilities.allowFlying = true;
			playerAbilities.creativeMode = true;
			playerAbilities.invulnerable = true;
		} else if (this == field_9219) {
			playerAbilities.allowFlying = true;
			playerAbilities.creativeMode = false;
			playerAbilities.invulnerable = true;
			playerAbilities.flying = true;
		} else {
			playerAbilities.allowFlying = false;
			playerAbilities.creativeMode = false;
			playerAbilities.invulnerable = false;
			playerAbilities.flying = false;
		}

		playerAbilities.allowModifyWorld = !this.shouldLimitWorldModification();
	}

	public boolean shouldLimitWorldModification() {
		return this == field_9216 || this == field_9219;
	}

	public boolean isCreative() {
		return this == field_9220;
	}

	public boolean isSurvivalLike() {
		return this == field_9215 || this == field_9216;
	}

	public static GameMode byId(int i) {
		return byId(i, field_9215);
	}

	public static GameMode byId(int i, GameMode gameMode) {
		for (GameMode gameMode2 : values()) {
			if (gameMode2.id == i) {
				return gameMode2;
			}
		}

		return gameMode;
	}

	public static GameMode byName(String string) {
		return byName(string, field_9215);
	}

	public static GameMode byName(String string, GameMode gameMode) {
		for (GameMode gameMode2 : values()) {
			if (gameMode2.name.equals(string)) {
				return gameMode2;
			}
		}

		return gameMode;
	}
}
