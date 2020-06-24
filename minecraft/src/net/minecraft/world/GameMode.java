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
		if (this == CREATIVE) {
			abilities.allowFlying = true;
			abilities.creativeMode = true;
			abilities.invulnerable = true;
		} else if (this == SPECTATOR) {
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
		return this == ADVENTURE || this == SPECTATOR;
	}

	public boolean isCreative() {
		return this == CREATIVE;
	}

	public boolean isSurvivalLike() {
		return this == SURVIVAL || this == ADVENTURE;
	}

	public static GameMode byId(int id) {
		return byId(id, SURVIVAL);
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
		return byName(name, SURVIVAL);
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
