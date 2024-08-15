package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.block.WireOrientation;

public class ExperimentalRedstoneController extends RedstoneController {
	private final Deque<BlockPos> powerIncreaseQueue = new ArrayDeque();
	private final Deque<BlockPos> powerDecreaseQueue = new ArrayDeque();
	private final Object2IntMap<BlockPos> wireOrientationsAndPowers = new Object2IntLinkedOpenHashMap<>();

	public ExperimentalRedstoneController(RedstoneWireBlock redstoneWireBlock) {
		super(redstoneWireBlock);
	}

	@Override
	public void update(World world, BlockPos pos, BlockState state, @Nullable WireOrientation orientation) {
		WireOrientation wireOrientation = tweakOrientation(world, orientation);
		this.propagatePowerUpdates(world, pos, wireOrientation);
		ObjectIterator<Entry<BlockPos>> objectIterator = this.wireOrientationsAndPowers.object2IntEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry<BlockPos> entry = (Entry<BlockPos>)objectIterator.next();
			BlockPos blockPos = (BlockPos)entry.getKey();
			int i = entry.getIntValue();
			int j = unpackPower(i);
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.isOf(this.wire) && !((Integer)blockState.get(RedstoneWireBlock.POWER)).equals(j)) {
				world.setBlockState(blockPos, blockState.with(RedstoneWireBlock.POWER, Integer.valueOf(j)), Block.NOTIFY_LISTENERS);
			} else {
				objectIterator.remove();
			}
		}

		this.method_61829(world);
	}

	private void method_61829(World world) {
		this.wireOrientationsAndPowers.forEach((pos, orientationAndPower) -> {
			WireOrientation wireOrientation = unpackOrientation(orientationAndPower);
			BlockState blockState = world.getBlockState(pos);

			for (Direction direction : wireOrientation.getDirectionsByPriority()) {
				if (canProvidePowerTo(blockState, direction)) {
					BlockPos blockPos = pos.offset(direction);
					BlockState blockState2 = world.getBlockState(blockPos);
					WireOrientation wireOrientation2 = wireOrientation.withFront(direction);
					world.updateNeighbor(blockState2, blockPos, this.wire, wireOrientation2, false);
					if (blockState2.isSolidBlock(world, blockPos)) {
						for (Direction direction2 : wireOrientation2.getDirectionsByPriority()) {
							if (direction2 != direction.getOpposite()) {
								world.updateNeighbor(blockPos.offset(direction2), this.wire, wireOrientation2.withFront(direction2));
							}
						}
					}
				}
			}
		});
	}

	private static boolean canProvidePowerTo(BlockState wireState, Direction direction) {
		EnumProperty<WireConnection> enumProperty = (EnumProperty<WireConnection>)RedstoneWireBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction);
		return enumProperty == null ? direction == Direction.DOWN : ((WireConnection)wireState.get(enumProperty)).isConnected();
	}

	private static WireOrientation tweakOrientation(World world, @Nullable WireOrientation orientation) {
		WireOrientation wireOrientation;
		if (orientation != null) {
			wireOrientation = orientation;
		} else {
			wireOrientation = WireOrientation.random(world.random);
		}

		return wireOrientation.withUp(Direction.UP);
	}

	private void propagatePowerUpdates(World world, BlockPos pos, WireOrientation orientation) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.isOf(this.wire)) {
			this.updatePowerAt(pos, (Integer)blockState.get(RedstoneWireBlock.POWER), orientation);
			this.powerIncreaseQueue.add(pos);
		} else {
			this.spreadPowerUpdateToNeighbors(world, pos, 0, orientation, true);
		}

		while (!this.powerIncreaseQueue.isEmpty()) {
			BlockPos blockPos = (BlockPos)this.powerIncreaseQueue.removeFirst();
			int i = this.wireOrientationsAndPowers.getInt(blockPos);
			WireOrientation wireOrientation = unpackOrientation(i);
			int j = unpackPower(i);
			int k = this.getStrongPowerAt(world, blockPos);
			int l = this.calculateWirePowerAt(world, blockPos);
			int m = Math.max(k, l);
			int n;
			if (m < j) {
				if (k > 0 && !this.powerDecreaseQueue.contains(blockPos)) {
					this.powerDecreaseQueue.add(blockPos);
				}

				n = 0;
			} else {
				n = m;
			}

			if (n != j) {
				this.updatePowerAt(blockPos, n, wireOrientation);
			}

			this.spreadPowerUpdateToNeighbors(world, blockPos, n, wireOrientation, true);
		}

		while (!this.powerDecreaseQueue.isEmpty()) {
			BlockPos blockPosx = (BlockPos)this.powerDecreaseQueue.removeFirst();
			int ix = this.wireOrientationsAndPowers.getInt(blockPosx);
			int o = unpackPower(ix);
			int jx = this.getStrongPowerAt(world, blockPosx);
			int kx = this.calculateWirePowerAt(world, blockPosx);
			int lx = Math.max(jx, kx);
			WireOrientation wireOrientation2 = unpackOrientation(ix);
			if (lx > o) {
				this.updatePowerAt(blockPosx, lx, wireOrientation2);
			} else if (lx < o) {
				throw new IllegalStateException("Turning off wire while trying to turn it on. Should not happen.");
			}

			this.spreadPowerUpdateToNeighbors(world, blockPosx, lx, wireOrientation2, false);
		}
	}

	private static int packOrientationAndPower(WireOrientation orientation, int power) {
		return orientation.ordinal() << 4 | power;
	}

	private static WireOrientation unpackOrientation(int packed) {
		return WireOrientation.fromOrdinal(packed >> 4);
	}

	private static int unpackPower(int packed) {
		return packed & 15;
	}

	private void updatePowerAt(BlockPos pos, int power, WireOrientation defaultOrientation) {
		this.wireOrientationsAndPowers
			.compute(
				pos,
				(pos2, orientationAndPower) -> orientationAndPower == null
						? packOrientationAndPower(defaultOrientation, power)
						: packOrientationAndPower(unpackOrientation(orientationAndPower), power)
			);
	}

	private void spreadPowerUpdateToNeighbors(World world, BlockPos pos, int power, WireOrientation orientation, boolean canIncreasePower) {
		for (Direction direction : orientation.getHorizontalDirections()) {
			BlockPos blockPos = pos.offset(direction);
			this.spreadPowerUpdateTo(world, blockPos, power, orientation.withFront(direction), canIncreasePower);
		}

		for (Direction direction : orientation.getVerticalDirections()) {
			BlockPos blockPos = pos.offset(direction);
			boolean bl = world.getBlockState(blockPos).isSolidBlock(world, blockPos);

			for (Direction direction2 : orientation.getHorizontalDirections()) {
				BlockPos blockPos2 = pos.offset(direction2);
				if (direction == Direction.UP && !bl || direction == Direction.DOWN && bl && !world.getBlockState(blockPos2).isSolidBlock(world, blockPos2)) {
					BlockPos blockPos3 = blockPos.offset(direction2);
					this.spreadPowerUpdateTo(world, blockPos3, power, orientation.withFront(direction2), canIncreasePower);
				}
			}
		}
	}

	private void spreadPowerUpdateTo(World world, BlockPos neighborPos, int power, WireOrientation orientation, boolean canIncreasePower) {
		BlockState blockState = world.getBlockState(neighborPos);
		if (blockState.isOf(this.wire)) {
			int i = this.getWirePowerAt(neighborPos, blockState);
			if (i < power - 1 && !this.powerDecreaseQueue.contains(neighborPos)) {
				this.powerDecreaseQueue.add(neighborPos);
				this.updatePowerAt(neighborPos, i, orientation);
			}

			if (canIncreasePower && i > power && !this.powerIncreaseQueue.contains(neighborPos)) {
				this.powerIncreaseQueue.add(neighborPos);
				this.updatePowerAt(neighborPos, i, orientation);
			}
		}
	}

	@Override
	protected int getWirePowerAt(BlockPos world, BlockState pos) {
		int i = this.wireOrientationsAndPowers.getOrDefault(world, -1);
		return i != -1 ? unpackPower(i) : super.getWirePowerAt(world, pos);
	}
}
