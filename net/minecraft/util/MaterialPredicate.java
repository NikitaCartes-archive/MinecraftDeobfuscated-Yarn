/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import org.jetbrains.annotations.Nullable;

public class MaterialPredicate
implements Predicate<BlockState> {
    private static final MaterialPredicate IS_AIR = new MaterialPredicate(Material.AIR){

        @Override
        public boolean method_11745(@Nullable BlockState blockState) {
            return blockState != null && blockState.isAir();
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return this.method_11745((BlockState)object);
        }
    };
    private final Material material;

    private MaterialPredicate(Material material) {
        this.material = material;
    }

    public static MaterialPredicate create(Material material) {
        return material == Material.AIR ? IS_AIR : new MaterialPredicate(material);
    }

    public boolean method_11745(@Nullable BlockState blockState) {
        return blockState != null && blockState.getMaterial() == this.material;
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object object) {
        return this.method_11745((BlockState)object);
    }
}

