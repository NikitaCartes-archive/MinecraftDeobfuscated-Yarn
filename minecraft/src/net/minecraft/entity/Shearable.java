package net.minecraft.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;

/**
 * Represents an entity that can be sheared, either by a player or a
 * dispenser.
 */
public interface Shearable {
	void sheared(SoundCategory shearedSoundCategory, ItemStack shears);

	boolean isShearable();
}
