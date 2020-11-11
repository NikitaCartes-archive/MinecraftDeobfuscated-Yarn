package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public enum GameModeSelection {
	SURVIVAL(GameMode.SURVIVAL),
	CREATIVE(GameMode.CREATIVE),
	ADVENTURE(GameMode.ADVENTURE),
	SPECTATOR(GameMode.SPECTATOR);

	/**
	 * The {@linkplain GameMode game mode} being chosen.
	 */
	private final GameMode gameMode;
	/**
	 * The name of the game mode.
	 */
	private final Text name;

	private GameModeSelection(GameMode gameMode) {
		this.gameMode = gameMode;
		this.name = new TranslatableText("selectWorld.gameMode." + gameMode.getName());
	}

	/**
	 * Gets the {@linkplain GameMode game mode} with the given name (i.e. enum field).
	 */
	public GameMode getGameMode() {
		return this.gameMode;
	}

	/**
	 * Gets the name of the given game mode.
	 */
	public Text getName() {
		return this.name;
	}
}
