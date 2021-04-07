package net.minecraft.world;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class PortalForcer {
	private static final int field_31810 = 3;
	private static final int field_31811 = 128;
	private static final int field_31812 = 16;
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

	public Optional<PortalUtil.Rectangle> getPortalRect(BlockPos destPos, boolean destIsNether) {
		PointOfInterestStorage pointOfInterestStorage = this.world.getPointOfInterestStorage();
		int i = destIsNether ? 16 : 128;
		pointOfInterestStorage.preloadChunks(this.world, destPos, i);
		Optional<PointOfInterest> optional = pointOfInterestStorage.getInSquare(
				pointOfInterestType -> pointOfInterestType == PointOfInterestType.NETHER_PORTAL, destPos, i, PointOfInterestStorage.OccupationStatus.ANY
			)
			.sorted(
				Comparator.comparingDouble(pointOfInterest -> pointOfInterest.getPos().getSquaredDistance(destPos))
					.thenComparingInt(pointOfInterest -> pointOfInterest.getPos().getY())
			)
			.filter(pointOfInterest -> this.world.getBlockState(pointOfInterest.getPos()).contains(Properties.HORIZONTAL_AXIS))
			.findFirst();
		return optional.map(
			pointOfInterest -> {
				BlockPos blockPos = pointOfInterest.getPos();
				this.world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(blockPos), 3, blockPos);
				BlockState blockState = this.world.getBlockState(blockPos);
				return PortalUtil.getLargestRectangle(
					blockPos, blockState.get(Properties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, blockPosx -> this.world.getBlockState(blockPosx) == blockState
				);
			}
		);
	}

	public Optional<PortalUtil.Rectangle> createPortal(BlockPos blockPos, Direction.Axis axis) {
		Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
		double d = -1.0;
		BlockPos blockPos2 = null;
		double e = -1.0;
		BlockPos blockPos3 = null;
		WorldBorder worldBorder = this.world.getWorldBorder();
		int i = Math.min(this.world.getTopY(), this.world.getBottomY() + this.world.getLogicalHeight()) - 1;
		BlockPos.Mutable mutable = blockPos.mutableCopy();

		for (BlockPos.Mutable mutable2 : BlockPos.iterateInSquare(blockPos, 16, Direction.EAST, Direction.SOUTH)) {
			int j = Math.min(i, this.world.getTopY(Heightmap.Type.MOTION_BLOCKING, mutable2.getX(), mutable2.getZ()));
			int k = 1;
			if (worldBorder.contains(mutable2) && worldBorder.contains(mutable2.move(direction, 1))) {
				mutable2.move(direction.getOpposite(), 1);

				for (int l = j; l >= this.world.getBottomY(); l--) {
					mutable2.setY(l);
					if (this.world.isAir(mutable2)) {
						int m = l;

						while (l > this.world.getBottomY() && this.world.isAir(mutable2.move(Direction.DOWN))) {
							l--;
						}

						if (l + 4 <= i) {
							int n = m - l;
							if (n <= 0 || n >= 3) {
								mutable2.setY(l);
								if (this.isValidPortalPos(mutable2, mutable, direction, 0)) {
									double f = blockPos.getSquaredDistance(mutable2);
									if (this.isValidPortalPos(mutable2, mutable, direction, -1) && this.isValidPortalPos(mutable2, mutable, direction, 1) && (d == -1.0 || d > f)) {
										d = f;
										blockPos2 = mutable2.toImmutable();
									}

									if (d == -1.0 && (e == -1.0 || e > f)) {
										e = f;
										blockPos3 = mutable2.toImmutable();
									}
								}
							}
						}
					}
				}
			}
		}

		if (d == -1.0 && e != -1.0) {
			blockPos2 = blockPos3;
			d = e;
		}

		if (d == -1.0) {
			int o = Math.max(this.world.getBottomY() - -1, 70);
			int p = i - 9;
			if (p < o) {
				return Optional.empty();
			}

			blockPos2 = new BlockPos(blockPos.getX(), MathHelper.clamp(blockPos.getY(), o, p), blockPos.getZ()).toImmutable();
			Direction direction2 = direction.rotateYClockwise();
			if (!worldBorder.contains(blockPos2)) {
				return Optional.empty();
			}

			for (int k = -1; k < 2; k++) {
				for (int lx = 0; lx < 2; lx++) {
					for (int m = -1; m < 3; m++) {
						BlockState blockState = m < 0 ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState();
						mutable.set(blockPos2, lx * direction.getOffsetX() + k * direction2.getOffsetX(), m, lx * direction.getOffsetZ() + k * direction2.getOffsetZ());
						this.world.setBlockState(mutable, blockState);
					}
				}
			}
		}

		for (int ox = -1; ox < 3; ox++) {
			for (int px = -1; px < 4; px++) {
				if (ox == -1 || ox == 2 || px == -1 || px == 3) {
					mutable.set(blockPos2, ox * direction.getOffsetX(), px, ox * direction.getOffsetZ());
					this.world.setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), Block.NOTIFY_ALL);
				}
			}
		}

		BlockState blockState2 = Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, axis);

		for (int pxx = 0; pxx < 2; pxx++) {
			for (int j = 0; j < 3; j++) {
				mutable.set(blockPos2, pxx * direction.getOffsetX(), j, pxx * direction.getOffsetZ());
				this.world.setBlockState(mutable, blockState2, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
			}
		}

		return Optional.of(new PortalUtil.Rectangle(blockPos2.toImmutable(), 2, 3));
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
				if (j < 0 && !this.world.getBlockState(temp).getMaterial().isSolid()) {
					return false;
				}

				if (j >= 0 && !this.world.isAir(temp)) {
					return false;
				}
			}
		}

		return true;
	}
}
