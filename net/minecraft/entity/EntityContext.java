/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContextImpl;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public interface EntityContext {
    public static EntityContext absent() {
        return EntityContextImpl.ABSENT;
    }

    public static EntityContext of(Entity entity) {
        return new EntityContextImpl(entity);
    }

    public boolean isSneaking();

    public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3);

    public boolean isHolding(Item var1);
}

