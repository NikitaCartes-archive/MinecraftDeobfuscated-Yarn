package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface FluidModificationItem {
	default void onEmptied(@Nullable PlayerEntity playerEntity, World world, ItemStack itemStack, BlockPos blockPos) {
	}

	boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult);
}
