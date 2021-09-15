/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.class_6568;
import net.minecraft.class_6583;
import org.jetbrains.annotations.Nullable;

public class class_6582
implements class_6583 {
    private final List<class_6583> field_34719;

    public class_6582(List<class_6583> list) {
        this.field_34719 = list;
    }

    @Override
    @Nullable
    public BlockState apply(class_6568 arg, int i, int j, int k) {
        for (class_6583 lv : this.field_34719) {
            BlockState blockState = lv.apply(arg, i, j, k);
            if (blockState == null) continue;
            return blockState;
        }
        return null;
    }
}

