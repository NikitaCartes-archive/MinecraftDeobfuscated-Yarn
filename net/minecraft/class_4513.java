/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.class_4512;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class class_4513
extends class_4512 {
    private final BlockPos field_20541;
    private final BlockPos field_20542;

    @Override
    public String getMessage() {
        String string = "" + this.field_20541.getX() + "," + this.field_20541.getY() + "," + this.field_20541.getZ() + " (relative: " + this.field_20542.getX() + "," + this.field_20542.getY() + "," + this.field_20542.getZ() + ")";
        return super.getMessage() + " at " + string;
    }

    @Nullable
    public String method_22150() {
        return super.getMessage() + " here";
    }

    @Nullable
    public BlockPos method_22151() {
        return this.field_20541;
    }
}

