/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInteraction;

public interface InteractionObserver {
    public void onInteractionWith(EntityInteraction var1, Entity var2);
}

