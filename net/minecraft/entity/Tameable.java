/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.EntityView;
import org.jetbrains.annotations.Nullable;

public interface Tameable {
    @Nullable
    public UUID getOwnerUuid();

    public EntityView method_48926();

    @Nullable
    default public LivingEntity getOwner() {
        UUID uUID = this.getOwnerUuid();
        if (uUID == null) {
            return null;
        }
        return this.method_48926().getPlayerByUuid(uUID);
    }
}

