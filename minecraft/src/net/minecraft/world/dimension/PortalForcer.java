package net.minecraft.world.dimension;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.Heightmap;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;

public class PortalForcer {
	public static final int field_31810 = 3;
	private static final int field_52248 = 16;
	private static final int field_52249 = 128;
	private static final int field_31813 = 5;
	private static final int field_31814 = 4;
	private static final int field_31815 = 3;
	private static final int field_31816 = -1;
	private static final int field_31817 = 4;
	private static final int field_31818 = -1;
	private static final int field_31819 = 3;
	private static final int field_31820 = -1;
	private static final int field_31821 = 2;
	private static final int field_31822 = -1;
	private final ServerWorld world;

	public PortalForcer(ServerWorld world) {
		this.world = world;
	}

	public Optional<BlockPos> getPortalPos(BlockPos pos, boolean destIsNether, WorldBorder worldBorder) {
		PointOfInterestStorage pointOfInterestStorage = this.world.getPointOfInterestStorage();
		int i = destIsNether ? 16 : 128;
		pointOfInterestStorage.preloadChunks(this.world, pos, i);
		return pointOfInterestStorage.getInSquare(
				poiType -> poiType.matchesKey(PointOfInterestTypes.NETHER_PORTAL), pos, i, PointOfInterestStorage.OccupationStatus.ANY
			)
			.map(PointOfInterest::getPos)
			.filter(worldBorder::contains)
			.filter(blockPos -> this.world.getBlockState(blockPos).contains(Properties.HORIZONTAL_AXIS))
			.min(Comparator.comparingDouble(blockPos2 -> blockPos2.getSquaredDistance(pos)).thenComparingInt(Vec3i::getY));
	}

	public Optional<BlockLocating.Rectangle> createPortal(BlockPos pos, Direction.Axis axis) {
		Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
		double d = -1.0;
		BlockPos blockPos = null;
		double e = -1.0;
		BlockPos blockPos2 = null;
		WorldBorder worldBorder = this.world.getWorldBorder();
		int i = Math.min(this.world.getTopY(), this.world.getBottomY() + this.world.getLogicalHeight()) - 1;
		int j = 1;
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (BlockPos.Mutable mutable2 : BlockPos.iterateInSquare(pos, 16, Direction.EAST, Direction.SOUTH)) {
			int k = Math.min(i, this.world.getTopY(Heightmap.Type.MOTION_BLOCKING, mutable2.getX(), mutable2.getZ()));
			if (worldBorder.contains(mutable2) && worldBorder.contains(mutable2.move(direction, 1))) {
				mutable2.move(direction.getOpposite(), 1);

				for (int l = k; l >= this.world.getBottomY(); l--) {
					mutable2.setY(l);
					if (this.isBlockStateValid(mutable2)) {
						int m = l;

						while (l > this.world.getBottomY() && this.isBlockStateValid(mutable2.move(Direction.DOWN))) {
							l--;
						}

						if (l + 4 <= i) {
							int n = m - l;
							if (n <= 0 || n >= 3) {
								mutable2.setY(l);
								if (this.isValidPortalPos(mutable2, mutable, direction, 0)) {
									double f = pos.getSquaredDistance(mutable2);
									if (this.isValidPortalPos(mutable2, mutable, direction, -1) && this.isValidPortalPos(mutable2, mutable, direction, 1) && (d == -1.0 || d > f)) {
										d = f;
										blockPos = mutable2.toImmutable();
									}

									if (d == -1.0 && (e == -1.0 || e > f)) {
										e = f;
										blockPos2 = mutable2.toImmutable();
									}
								}
							}
						}
					}
				}
			}
		}

		if (d == -1.0 && e != -1.0) {
			blockPos = blockPos2;
			d = e;
		}

		if (d == -1.0) {
			int o = Math.max(this.world.getBottomY() - -1, 70);
			int p = i - 9;
			if (p < o) {
				return Optional.empty();
			}

			blockPos = new BlockPos(pos.getX() - direction.getOffsetX() * 1, MathHelper.clamp(pos.getY(), o, p), pos.getZ() - direction.getOffsetZ() * 1).toImmutable();
			blockPos = worldBorder.clamp(blockPos);
			Direction direction2 = direction.rotateYClockwise();

			for (int lx = -1; lx < 2; lx++) {
				for (int m = 0; m < 2; m++) {
					for (int n = -1; n < 3; n++) {
						BlockState blockState = n < 0 ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState();
						mutable.set(blockPos, m * direction.getOffsetX() + lx * direction2.getOffsetX(), n, m * direction.getOffsetZ() + lx * direction2.getOffsetZ());
						this.world.setBlockState(mutable, blockState);
					}
				}
			}
		}

		for (int o = -1; o < 3; o++) {
			for (int p = -1; p < 4; p++) {
				if (o == -1 || o == 2 || p == -1 || p == 3) {
					mutable.set(blockPos, o * direction.getOffsetX(), p, o * direction.getOffsetZ());
					this.world.setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), Block.NOTIFY_ALL);
				}
			}
		}

		BlockState blockState2 = Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, axis);

		for (int px = 0; px < 2; px++) {
			for (int k = 0; k < 3; k++) {
				mutable.set(blockPos, px * direction.getOffsetX(), k, px * direction.getOffsetZ());
				this.world.setBlockState(mutable, blockState2, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
			}
		}

		return Optional.of(new BlockLocating.Rectangle(blockPos.toImmutable(), 2, 3));
	}

	private boolean isBlockStateValid(BlockPos.Mutable pos) {
		BlockState blockState = this.world.getBlockState(pos);
		return blockState.isReplaceable() && blockState.getFluidState().isEmpty();
	}

	private boolean isValidPortalPos(BlockPos pos, BlockPos.Mutable temp, Direction portalDirection, int distanceOrthogonalToPortal) {
		Direction direction = portalDirection.rotateYClockwise();

		for (int i = -1; i < 3; i++) {
			for (int j = -1; j < 4; j++) {
				temp.set(
					pos,
					portalDirection.getOffsetX() * i + direction.getOffsetX() * distanceOrthogonalToPortal,
					j,
					portalDirection.getOffsetZ() * i + direction.getOffsetZ() * distanceOrthogonalToPortal
				);
				if (j < 0 && !this.world.getBlockState(temp).isSolid()) {
					return false;
				}

				if (j >= 0 && !this.isBlockStateValid(temp)) {
					return false;
				}
			}
		}

		return true;
	}
}
