package net.minecraft.entity;

public interface InteractionObserver {
	void onInteractionWith(EntityInteraction interaction, Entity entity);
}
