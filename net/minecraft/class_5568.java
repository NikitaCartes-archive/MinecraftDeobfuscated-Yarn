/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.class_5569;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public interface class_5568 {
    public int getEntityId();

    public UUID getUuid();

    public BlockPos getBlockPos();

    public Box getBoundingBox();

    public void method_31744(class_5569 var1);

    public Stream<? extends class_5568> method_31748();

    public void setRemoved(Entity.RemovalReason var1);

    public boolean method_31746();

    public boolean isPlayer();
}

