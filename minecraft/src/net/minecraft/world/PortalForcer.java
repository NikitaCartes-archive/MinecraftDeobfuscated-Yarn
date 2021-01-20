package net.minecraft.world;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.class_5459;
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
	private final ServerWorld world;

	public PortalForcer(ServerWorld world) {
		this.world = world;
	}

	public Optional<class_5459.class_5460> method_30483(BlockPos blockPos, boolean bl) {
		PointOfInterestStorage pointOfInterestStorage = this.world.getPointOfInterestStorage();
		int i = bl ? 16 : 128;
		pointOfInterestStorage.preloadChunks(this.world, blockPos, i);
		Optional<PointOfInterest> optional = pointOfInterestStorage.getInSquare(
				pointOfInterestType -> pointOfInterestType == PointOfInterestType.NETHER_PORTAL, blockPos, i, PointOfInterestStorage.OccupationStatus.ANY
			)
			.sorted(
				Comparator.comparingDouble(pointOfInterest -> pointOfInterest.getPos().getSquaredDistance(blockPos))
					.thenComparingInt(pointOfInterest -> pointOfInterest.getPos().getY())
			)
			.filter(pointOfInterest -> this.world.getBlockState(pointOfInterest.getPos()).contains(Properties.HORIZONTAL_AXIS))
			.findFirst();
		return optional.map(
			pointOfInterest -> {
				BlockPos blockPosx = pointOfInterest.getPos();
				this.world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(blockPosx), 3, blockPosx);
				BlockState blockState = this.world.getBlockState(blockPosx);
				return class_5459.method_30574(
					blockPosx, blockState.get(Properties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, blockPosxx -> this.world.getBlockState(blockPosxx) == blockState
				);
			}
		);
	}

	public Optional<class_5459.class_5460> method_30482(BlockPos blockPos, Direction.Axis axis) {
		Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
		double d = -1.0;
		BlockPos blockPos2 = null;
		double e = -1.0;
		BlockPos blockPos3 = null;
		WorldBorder worldBorder = this.world.getWorldBorder();
		int i = this.world.getBottomSectionLimit() + this.world.getHeightLimit() - 1;
		BlockPos.Mutable mutable = blockPos.mutableCopy();

		for (BlockPos.Mutable mutable2 : BlockPos.method_30512(blockPos, 16, Direction.EAST, Direction.SOUTH)) {
			int j = Math.min(i, this.world.getTopY(Heightmap.Type.MOTION_BLOCKING, mutable2.getX(), mutable2.getZ()));
			int k = 1;
			if (worldBorder.contains(mutable2) && worldBorder.contains(mutable2.move(direction, 1))) {
				mutable2.move(direction.getOpposite(), 1);

				for (int l = j; l >= this.world.getBottomSectionLimit(); l--) {
					mutable2.setY(l);
					if (this.world.isAir(mutable2)) {
						int m = l;

						while (l > this.world.getBottomSectionLimit() && this.world.isAir(mutable2.move(Direction.DOWN))) {
							l--;
						}

						if (l + 4 <= i) {
							int n = m - l;
							if (n <= 0 || n >= 3) {
								mutable2.setY(l);
								if (this.method_30481(mutable2, mutable, direction, 0)) {
									double f = blockPos.getSquaredDistance(mutable2);
									if (this.method_30481(mutable2, mutable, direction, -1) && this.method_30481(mutable2, mutable, direction, 1) && (d == -1.0 || d > f)) {
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
			blockPos2 = new BlockPos(
					blockPos.getX(), MathHelper.clamp(blockPos.getY(), 70, this.world.getBottomSectionLimit() + this.world.getHeightLimit() - 10), blockPos.getZ()
				)
				.toImmutable();
			Direction direction2 = direction.rotateYClockwise();
			if (!worldBorder.contains(blockPos2)) {
				return Optional.empty();
			}

			for (int o = -1; o < 2; o++) {
				for (int j = 0; j < 2; j++) {
					for (int k = -1; k < 3; k++) {
						BlockState blockState = k < 0 ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState();
						mutable.set(blockPos2, j * direction.getOffsetX() + o * direction2.getOffsetX(), k, j * direction.getOffsetZ() + o * direction2.getOffsetZ());
						this.world.setBlockState(mutable, blockState);
					}
				}
			}
		}

		for (int p = -1; p < 3; p++) {
			for (int o = -1; o < 4; o++) {
				if (p == -1 || p == 2 || o == -1 || o == 3) {
					mutable.set(blockPos2, p * direction.getOffsetX(), o, p * direction.getOffsetZ());
					this.world.setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), 3);
				}
			}
		}

		BlockState blockState2 = Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, axis);

		for (int ox = 0; ox < 2; ox++) {
			for (int j = 0; j < 3; j++) {
				mutable.set(blockPos2, ox * direction.getOffsetX(), j, ox * direction.getOffsetZ());
				this.world.setBlockState(mutable, blockState2, 18);
			}
		}

		return Optional.of(new class_5459.class_5460(blockPos2.toImmutable(), 2, 3));
	}

	private boolean method_30481(BlockPos blockPos, BlockPos.Mutable mutable, Direction direction, int i) {
		Direction direction2 = direction.rotateYClockwise();

		for (int j = -1; j < 3; j++) {
			for (int k = -1; k < 4; k++) {
				mutable.set(blockPos, direction.getOffsetX() * j + direction2.getOffsetX() * i, k, direction.getOffsetZ() * j + direction2.getOffsetZ() * i);
				if (k < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid()) {
					return false;
				}

				if (k >= 0 && !this.world.isAir(mutable)) {
					return false;
				}
			}
		}

		return true;
	}
}
