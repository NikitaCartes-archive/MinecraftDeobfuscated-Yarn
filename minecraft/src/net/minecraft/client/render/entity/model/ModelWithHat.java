package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents a model with a hat.
 */
@Environment(EnvType.CLIENT)
public interface ModelWithHat {
	/**
	 * Sets whether the hat is visible or not.
	 * 
	 * @param visible {@code true} if the hat is visible, otherwise {@code false}
	 */
	void setHatVisible(boolean visible);
}
