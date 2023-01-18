package net.minecraft;

import java.util.Date;
import net.minecraft.resource.ResourceType;

/**
 * The game version interface used by Minecraft, replacing the javabridge
 * one's occurrences in Minecraft code.
 */
public interface GameVersion {
	/**
	 * {@return the save version information for this game version}
	 */
	SaveVersion getSaveVersion();

	String getId();

	String getName();

	int getProtocolVersion();

	int getResourceVersion(ResourceType type);

	Date getBuildTime();

	boolean isStable();
}
