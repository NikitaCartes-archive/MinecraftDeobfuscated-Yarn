package net.minecraft.block;

import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public interface FluidDrainable {
	ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state);

	/**
	 * {@return the sound played when filling a bucket with the fluid contained in this block}
	 * 
	 * @see net.minecraft.fluid.Fluid#getBucketFillSound()
	 */
	Optional<SoundEvent> getBucketFillSound();
}
