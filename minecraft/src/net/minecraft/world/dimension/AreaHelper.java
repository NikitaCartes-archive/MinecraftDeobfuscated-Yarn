package net.minecraft.world.dimension;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PortalUtil;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.WorldAccess;

public class AreaHelper {
	private static final int field_31825 = 2;
	public static final int field_31823 = 21;
	private static final int field_31826 = 3;
	public static final int field_31824 = 21;
	private static final AbstractBlock.ContextPredicate IS_VALID_FRAME_BLOCK = (state, world, pos) -> state.isOf(Blocks.OBSIDIAN);
	private final WorldAccess world;
	private final Direction.Axis axis;
	private final Direction negativeDir;
	private int foundPortalBlocks;
	@Nullable
	private BlockPos lowerCorner;
	private int height;
	private final int width;

	public static Optional<AreaHelper> getNewPortal(WorldAccess world, BlockPos pos, Direction.Axis axis) {
		return getOrEmpty(world, pos, areaHelper -> areaHelper.isValid() && areaHelper.foundPortalBlocks == 0, axis);
	}

	public static Optional<AreaHelper> getOrEmpty(WorldAccess world, BlockPos pos, Predicate<AreaHelper> predicate, Direction.Axis axis) {
		Optional<AreaHelper> optional = Optional.of(new AreaHelper(world, pos, axis)).filter(predicate);
		if (optional.isPresent()) {
			return optional;
		} else {
			Direction.Axis axis2 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
			return Optional.of(new AreaHelper(world, pos, axis2)).filter(predicate);
		}
	}

	public AreaHelper(WorldAccess world, BlockPos pos, Direction.Axis axis) {
		this.world = world;
		this.axis = axis;
		this.negativeDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
		this.lowerCorner = this.getLowerCorner(pos);
		if (this.lowerCorner == null) {
			this.lowerCorner = pos;
			this.width = 1;
			this.height = 1;
		} else {
			this.width = this.getWidth();
			if (this.width > 0) {
				this.height = this.getHeight();
			}
		}
	}

	@Nullable
	private BlockPos getLowerCorner(BlockPos pos) {
		int i = Math.max(this.world.getBottomY(), pos.getY() - 21);

		while (pos.getY() > i && validStateInsidePortal(this.world.getBlockState(pos.down()))) {
			pos = pos.down();
		}

		Direction direction = this.negativeDir.getOpposite();
		int j = this.getWidth(pos, direction) - 1;
		return j < 0 ? null : pos.offset(direction, j);
	}

	private int getWidth() {
		int i = this.getWidth(this.lowerCorner, this.negativeDir);
		return i >= 2 && i <= 21 ? i : 0;
	}

	private int getWidth(BlockPos pos, Direction direction) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i <= 21; i++) {
			mutable.set(pos).move(direction, i);
			BlockState blockState = this.world.getBlockState(mutable);
			if (!validStateInsidePortal(blockState)) {
				if (IS_VALID_FRAME_BLOCK.test(blockState, this.world, mutable)) {
					return i;
				}
				break;
			}

			BlockState blockState2 = this.world.getBlockState(mutable.move(Direction.DOWN));
			if (!IS_VALID_FRAME_BLOCK.test(blockState2, this.world, mutable)) {
				break;
			}
		}

		return 0;
	}

	private int getHeight() {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = this.method_30490(mutable);
		return i >= 3 && i <= 21 && this.method_30491(mutable, i) ? i : 0;
	}

	private boolean method_30491(BlockPos.Mutable mutable, int i) {
		for (int j = 0; j < this.width; j++) {
			BlockPos.Mutable mutable2 = mutable.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, j);
			if (!IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(mutable2), this.world, mutable2)) {
				return false;
			}
		}

		return true;
	}

	private int method_30490(BlockPos.Mutable mutable) {
		for (int i = 0; i < 21; i++) {
			mutable.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, -1);
			if (!IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(mutable), this.world, mutable)) {
				return i;
			}

			mutable.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, this.width);
			if (!IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(mutable), this.world, mutable)) {
				return i;
			}

			for (int j = 0; j < this.width; j++) {
				mutable.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, j);
				BlockState blockState = this.world.getBlockState(mutable);
				if (!validStateInsidePortal(blockState)) {
					return i;
				}

				if (blockState.isOf(Blocks.NETHER_PORTAL)) {
					this.foundPortalBlocks++;
				}
			}
		}

		return 21;
	}

	private static boolean validStateInsidePortal(BlockState state) {
		return state.isAir() || state.isIn(BlockTags.FIRE) || state.isOf(Blocks.NETHER_PORTAL);
	}

	public boolean isValid() {
		return this.lowerCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
	}

	public void createPortal() {
		BlockState blockState = Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, this.axis);
		BlockPos.iterate(this.lowerCorner, this.lowerCorner.offset(Direction.UP, this.height - 1).offset(this.negativeDir, this.width - 1))
			.forEach(blockPos -> this.world.setBlockState(blockPos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE));
	}

	public boolean wasAlreadyValid() {
		return this.isValid() && this.foundPortalBlocks == this.width * this.height;
	}

	public static Vec3d entityPosInPortal(PortalUtil.Rectangle portalRect, Direction.Axis portalAxis, Vec3d entityPos, EntityDimensions entityDimensions) {
		double d = (double)portalRect.width - (double)entityDimensions.width;
		double e = (double)portalRect.height - (double)entityDimensions.height;
		BlockPos blockPos = portalRect.lowerLeft;
		double g;
		if (d > 0.0) {
			float f = (float)blockPos.getComponentAlongAxis(portalAxis) + entityDimensions.width / 2.0F;
			g = MathHelper.clamp(MathHelper.getLerpProgress(entityPos.getComponentAlongAxis(portalAxis) - (double)f, 0.0, d), 0.0, 1.0);
		} else {
			g = 0.5;
		}

		double h;
		if (e > 0.0) {
			Direction.Axis axis = Direction.Axis.Y;
			h = MathHelper.clamp(MathHelper.getLerpProgress(entityPos.getComponentAlongAxis(axis) - (double)blockPos.getComponentAlongAxis(axis), 0.0, e), 0.0, 1.0);
		} else {
			h = 0.0;
		}

		Direction.Axis axis = portalAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
		double i = entityPos.getComponentAlongAxis(axis) - ((double)blockPos.getComponentAlongAxis(axis) + 0.5);
		return new Vec3d(g, h, i);
	}

	public static TeleportTarget getNetherTeleportTarget(
		ServerWorld destination,
		PortalUtil.Rectangle portalRect,
		Direction.Axis portalAxis,
		Vec3d offset,
		EntityDimensions dimensions,
		Vec3d velocity,
		float yaw,
		float pitch
	) {
		BlockPos blockPos = portalRect.lowerLeft;
		BlockState blockState = destination.getBlockState(blockPos);
		Direction.Axis axis = blockState.get(Properties.HORIZONTAL_AXIS);
		double d = (double)portalRect.width;
		double e = (double)portalRect.height;
		int i = portalAxis == axis ? 0 : 90;
		Vec3d vec3d = portalAxis == axis ? velocity : new Vec3d(velocity.z, velocity.y, -velocity.x);
		double f = (double)dimensions.width / 2.0 + (d - (double)dimensions.width) * offset.getX();
		double g = (e - (double)dimensions.height) * offset.getY();
		double h = 0.5 + offset.getZ();
		boolean bl = axis == Direction.Axis.X;
		Vec3d vec3d2 = new Vec3d((double)blockPos.getX() + (bl ? f : h), (double)blockPos.getY() + g, (double)blockPos.getZ() + (bl ? h : f));
		return new TeleportTarget(vec3d2, vec3d, yaw + (float)i, pitch);
	}
}
