/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.class_5569;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public interface EntityLike {
    public int getId();

    public UUID getUuid();

    public BlockPos getBlockPos();

    public Box getBoundingBox();

    public void method_31744(class_5569 var1);

    public Stream<? extends EntityLike> streamPassengers();

    public void setRemoved(Entity.RemovalReason var1);

    public boolean shouldSave();

    public boolean isPlayer();
}

