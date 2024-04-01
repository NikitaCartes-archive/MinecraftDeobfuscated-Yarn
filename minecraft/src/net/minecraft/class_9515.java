package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FloataterBlock;
import net.minecraft.block.GridTickable;
import net.minecraft.block.PistonBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.GameRules;
import net.minecraft.world.Grid;
import net.minecraft.world.World;

public record class_9515(Grid blocks, LongSet mask, BlockPos minPos, int engines) {
	private static final Direction[] field_50526 = Direction.values();

	@Nullable
	public static class_9515 method_58990(World world, BlockPos blockPos, Direction direction) {
		Long2ObjectMap<BlockState> long2ObjectMap = new Long2ObjectOpenHashMap<>();
		LongArrayFIFOQueue longArrayFIFOQueue = new LongArrayFIFOQueue();
		long2ObjectMap.put(blockPos.asLong(), world.getBlockState(blockPos));
		longArrayFIFOQueue.enqueue(blockPos.asLong());
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		int l = blockPos.getX();
		int m = blockPos.getY();
		int n = blockPos.getZ();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();
		int o = world.getGameRules().getInt(GameRules.FLOATATER_SIZE_LIMIT);

		while (!longArrayFIFOQueue.isEmpty()) {
			long p = longArrayFIFOQueue.dequeueLastLong();
			mutable.set(p);
			BlockState blockState = long2ObjectMap.get(p);
			i = Math.min(i, mutable.getX());
			j = Math.min(j, mutable.getY());
			k = Math.min(k, mutable.getZ());
			l = Math.max(l, mutable.getX());
			m = Math.max(m, mutable.getY());
			n = Math.max(n, mutable.getZ());
			if (l - i + 1 > o || m - j + 1 > o || n - k + 1 > o) {
				return null;
			}

			VoxelShape voxelShape = blockState.getOutlineShape(world, mutable);

			for (Direction direction2 : field_50526) {
				mutable2.set(mutable, direction2);
				long q = mutable2.asLong();
				if (!long2ObjectMap.containsKey(q)) {
					BlockState blockState2 = world.getBlockState(mutable2);
					VoxelShape voxelShape2 = blockState2.getOutlineShape(world, mutable2);
					boolean bl = direction2 == direction && !blockState2.isReplaceable();
					if (bl || method_58993(direction2, voxelShape, voxelShape2, blockState, blockState2)) {
						longArrayFIFOQueue.enqueue(q);
						long2ObjectMap.put(q, blockState2);
					}
				}
			}
		}

		int r = l - i + 1;
		int s = m - j + 1;
		int t = n - k + 1;
		Grid grid = new Grid(r, s, t);
		int u = 0;

		for (Entry<BlockState> entry : Long2ObjectMaps.fastIterable(long2ObjectMap)) {
			mutable.set(entry.getLongKey());
			BlockState blockState3 = ((BlockState)entry.getValue()).withIfExists(Properties.WATERLOGGED, Boolean.valueOf(false));
			if (blockState3.isOf(Blocks.FLOATATER) && blockState3.get(FloataterBlock.FACING) == direction && (Boolean)blockState3.get(FloataterBlock.TRIGGERED)) {
				u++;
			}

			if (blockState3.getBlock() instanceof GridTickable) {
				grid.addPosition(new BlockPos(mutable.getX() - i, mutable.getY() - j, mutable.getZ() - k));
			}

			if (!blockState3.hasBlockEntity()) {
				grid.setBlockStateAt(mutable.getX() - i, mutable.getY() - j, mutable.getZ() - k, blockState3);
			}
		}

		return new class_9515(grid, new LongOpenHashSet(long2ObjectMap.keySet()), new BlockPos(i, j, k), u);
	}

	private static boolean method_58993(Direction direction, VoxelShape voxelShape, VoxelShape voxelShape2, BlockState blockState, BlockState blockState2) {
		if (method_58996(blockState, direction) || method_58996(blockState2, direction.getOpposite())) {
			return true;
		} else {
			return !method_58992(direction, voxelShape, voxelShape2)
				? false
				: !method_58991(blockState, direction) && !method_58991(blockState2, direction.getOpposite());
		}
	}

	private static boolean method_58992(Direction direction, VoxelShape voxelShape, VoxelShape voxelShape2) {
		if (voxelShape != VoxelShapes.empty() && voxelShape2 != VoxelShapes.empty()) {
			VoxelShape voxelShape3 = VoxelShapes.extrudeFace(voxelShape, direction);
			VoxelShape voxelShape4 = VoxelShapes.extrudeFace(voxelShape2, direction.getOpposite());
			return voxelShape3 == VoxelShapes.fullCube() && voxelShape4 == VoxelShapes.fullCube()
				? true
				: VoxelShapes.matchesAnywhere(voxelShape3, voxelShape4, BooleanBiFunction.AND);
		} else {
			return false;
		}
	}

	private static boolean method_58991(BlockState blockState, Direction direction) {
		return blockState.isOf(Blocks.FLOATATER) && blockState.get(FloataterBlock.FACING) != direction;
	}

	private static boolean method_58996(BlockState blockState, Direction direction) {
		if (!blockState.isOf(Blocks.SLIME_BLOCK) && !blockState.isOf(Blocks.HONEY_BLOCK)) {
			return blockState.isOf(Blocks.STICKY_PISTON) && blockState.get(PistonBlock.FACING) == direction
				? true
				: blockState.isOf(Blocks.FLOATATER) && blockState.get(FloataterBlock.FACING) == direction;
		} else {
			return true;
		}
	}

	public void method_58988(World world) {
		this.method_58994(blockPos -> {
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.hasBlockEntity()) {
				world.breakBlock(blockPos, true);
			} else {
				FluidState fluidState = blockState.getFluidState();
				BlockState blockState2 = fluidState.getBlockState();
				world.setBlockState(blockPos, blockState2, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
			}
		});
		this.method_58994(blockPos -> world.updateNeighbors(blockPos, world.getBlockState(blockPos).getBlock()));
	}

	private void method_58994(Consumer<BlockPos> consumer) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		LongIterator longIterator = this.mask.longIterator();

		while (longIterator.hasNext()) {
			mutable.set(longIterator.nextLong());
			consumer.accept(mutable);
		}
	}
}
