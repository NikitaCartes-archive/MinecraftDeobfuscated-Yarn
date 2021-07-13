package net.minecraft;

/**
 * The game version interface used by Minecraft, replacing the javabridge
 * one's occurences in Minecraft code.
 */
public interface GameVersion extends com.mojang.bridge.game.GameVersion {
	@Deprecated
	@Override
	default int getWorldVersion() {
		return this.getSaveVersion().getId();
	}

	@Deprecated
	@Override
	default String getSeriesId() {
		return this.getSaveVersion().getSeries();
	}

	/**
	 * {@return the save version information for this game version}
	 */
	SaveVersion getSaveVersion();
}
