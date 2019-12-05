/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class EntityContextImpl
implements EntityContext {
    protected static final EntityContext ABSENT = new EntityContextImpl(false, -1.7976931348623157E308, Items.AIR){

        @Override
        public boolean isAbove(VoxelShape voxelShape, BlockPos blockPos, boolean bl) {
            return bl;
        }
    };
    private final boolean descending;
    private final double minY;
    private final Item heldItem;

    protected EntityContextImpl(boolean bl, double d, Item item) {
        this.descending = bl;
        this.minY = d;
        this.heldItem = item;
    }

    @Deprecated
    protected EntityContextImpl(Entity entity) {
        this(entity.isDescending(), entity.getY(), entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandStack().getItem() : Items.AIR);
    }

    @Override
    public boolean isHolding(Item item) {
        return this.heldItem == item;
    }

    @Override
    public boolean isDescending() {
        return this.descending;
    }

    @Override
    public boolean isAbove(VoxelShape voxelShape, BlockPos blockPos, boolean bl) {
        return this.minY > (double)blockPos.getY() + voxelShape.getMaximum(Direction.Axis.Y) - (double)1.0E-5f;
    }
}

