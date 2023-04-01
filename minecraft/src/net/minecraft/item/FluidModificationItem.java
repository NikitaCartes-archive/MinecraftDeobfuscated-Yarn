package net.minecraft.item;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.NetherPortal;

public interface FluidModificationItem {
	default void onEmptied(@Nullable PlayerEntity player, World world, ItemStack stack, BlockPos pos) {
	}

	boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult);

	static void method_50794(World world, BlockPos blockPos) {
		Optional<NetherPortal> optional = NetherPortal.getNewPortal(world, blockPos, Direction.Axis.X, true);
		optional.ifPresent(NetherPortal::createPortal);
	}
}
