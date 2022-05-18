package net.minecraft.block.entity;

/**
 * An interface implemented by block entities with openable lids,
 * such as chests or ender chests.
 */
public interface LidOpenable {
	float getAnimationProgress(float tickDelta);
}
