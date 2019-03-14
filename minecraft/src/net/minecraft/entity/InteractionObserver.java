package net.minecraft.entity;

public interface InteractionObserver {
	void onInteractionWith(EntityInteraction entityInteraction, Entity entity);
}
