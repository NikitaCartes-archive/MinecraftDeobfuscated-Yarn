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

public class ExplosionBehavior {
    public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (blockState.isAir() && fluidState.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Float.valueOf(Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance())));
    }

    public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
        return true;
    }
}

