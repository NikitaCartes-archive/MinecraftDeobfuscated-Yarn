package net.minecraft.world;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum GameMode {
	NOT_SET(-1, ""),
	SURVIVAL(0, "survival"),
	CREATIVE(1, "creative"),
	ADVENTURE(2, "adventure"),
	SPECTATOR(3, "spectator");

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
		if (this == CREATIVE) {
			playerAbilities.allowFlying = true;
			playerAbilities.creativeMode = true;
			playerAbilities.invulnerable = true;
		} else if (this == SPECTATOR) {
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
		return this == ADVENTURE || this == SPECTATOR;
	}

	public boolean isCreative() {
		return this == CREATIVE;
	}

	public boolean isSurvivalLike() {
		return this == SURVIVAL || this == ADVENTURE;
	}

	public static GameMode byId(int i) {
		return byId(i, SURVIVAL);
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
		return byName(string, SURVIVAL);
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
