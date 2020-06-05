/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.explosion;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;

public interface ExplosionBehavior {
    public Optional<Float> getBlastResistance(Explosion var1, BlockView var2, BlockPos var3, BlockState var4, FluidState var5);

    public boolean canDestroyBlock(Explosion var1, BlockView var2, BlockPos var3, BlockState var4, float var5);
}

