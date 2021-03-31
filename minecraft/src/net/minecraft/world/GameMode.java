package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum GameMode {
	SURVIVAL(0, "survival"),
	CREATIVE(1, "creative"),
	ADVENTURE(2, "adventure"),
	SPECTATOR(3, "spectator");

	public static final GameMode DEFAULT = SURVIVAL;
	private static final int field_30964 = -1;
	private final int id;
	private final String name;
	private final Text simpleTranslatableName;
	private final Text translatableName;

	private GameMode(int id, String name) {
		this.id = id;
		this.name = name;
		this.simpleTranslatableName = new TranslatableText("selectWorld.gameMode." + name);
		this.translatableName = new TranslatableText("gameMode." + name);
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public Text getTranslatableName() {
		return this.translatableName;
	}

	public Text getSimpleTranslatableName() {
		return this.simpleTranslatableName;
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
		return byId(id, DEFAULT);
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

	public static int getId(@Nullable GameMode gameMode) {
		return gameMode != null ? gameMode.id : -1;
	}

	@Nullable
	public static GameMode getOrNull(int id) {
		return id == -1 ? null : byId(id);
	}
}
