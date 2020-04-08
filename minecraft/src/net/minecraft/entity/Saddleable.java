package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.sound.SoundCategory;

/**
 * Represents an entity that can be saddled, either by a player or a
 * dispenser.
 */
public interface Saddleable {
	boolean canBeSaddled();

	void saddle(@Nullable SoundCategory sound);

	boolean isSaddled();
}
