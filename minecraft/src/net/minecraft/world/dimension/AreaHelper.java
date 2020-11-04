package net.minecraft.world.dimension;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_5459;
import net.minecraft.block.AbstractBlock;
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
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.WorldAccess;

public class AreaHelper {
	private static final AbstractBlock.ContextPredicate IS_VALID_FRAME_BLOCK = (blockState, blockView, blockPos) -> blockState.isOf(Blocks.OBSIDIAN);
	private final WorldAccess world;
	private final Direction.Axis axis;
	private final Direction negativeDir;
	private int foundPortalBlocks;
	@Nullable
	private BlockPos lowerCorner;
	private int height;
	private final int width;

	public static Optional<AreaHelper> method_30485(WorldAccess worldAccess, BlockPos blockPos, Direction.Axis axis) {
		return method_30486(worldAccess, blockPos, areaHelper -> areaHelper.isValid() && areaHelper.foundPortalBlocks == 0, axis);
	}

	public static Optional<AreaHelper> method_30486(WorldAccess worldAccess, BlockPos blockPos, Predicate<AreaHelper> predicate, Direction.Axis axis) {
		Optional<AreaHelper> optional = Optional.of(new AreaHelper(worldAccess, blockPos, axis)).filter(predicate);
		if (optional.isPresent()) {
			return optional;
		} else {
			Direction.Axis axis2 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
			return Optional.of(new AreaHelper(worldAccess, blockPos, axis2)).filter(predicate);
		}
	}

	public AreaHelper(WorldAccess world, BlockPos blockPos, Direction.Axis axis) {
		this.world = world;
		this.axis = axis;
		this.negativeDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
		this.lowerCorner = this.method_30492(blockPos);
		if (this.lowerCorner == null) {
			this.lowerCorner = blockPos;
			this.width = 1;
			this.height = 1;
		} else {
			this.width = this.method_30495();
			if (this.width > 0) {
				this.height = this.method_30496();
			}
		}
	}

	@Nullable
	private BlockPos method_30492(BlockPos blockPos) {
		int i = Math.max(this.world.getBottomHeightLimit(), blockPos.getY() - 21);

		while (blockPos.getY() > i && validStateInsidePortal(this.world.getBlockState(blockPos.down()))) {
			blockPos = blockPos.down();
		}

		Direction direction = this.negativeDir.getOpposite();
		int j = this.method_30493(blockPos, direction) - 1;
		return j < 0 ? null : blockPos.offset(direction, j);
	}

	private int method_30495() {
		int i = this.method_30493(this.lowerCorner, this.negativeDir);
		return i >= 2 && i <= 21 ? i : 0;
	}

	private int method_30493(BlockPos blockPos, Direction direction) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i <= 21; i++) {
			mutable.set(blockPos).move(direction, i);
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

	private int method_30496() {
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

	private static boolean validStateInsidePortal(BlockState blockState) {
		return blockState.isAir() || blockState.isIn(BlockTags.FIRE) || blockState.isOf(Blocks.NETHER_PORTAL);
	}

	public boolean isValid() {
		return this.lowerCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
	}

	public void createPortal() {
		BlockState blockState = Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, this.axis);
		BlockPos.iterate(this.lowerCorner, this.lowerCorner.offset(Direction.UP, this.height - 1).offset(this.negativeDir, this.width - 1))
			.forEach(blockPos -> this.world.setBlockState(blockPos, blockState, 18));
	}

	public boolean wasAlreadyValid() {
		return this.isValid() && this.foundPortalBlocks == this.width * this.height;
	}

	public static Vec3d method_30494(class_5459.class_5460 arg, Direction.Axis axis, Vec3d vec3d, EntityDimensions entityDimensions) {
		double d = (double)arg.field_25937 - (double)entityDimensions.width;
		double e = (double)arg.field_25938 - (double)entityDimensions.height;
		BlockPos blockPos = arg.field_25936;
		double g;
		if (d > 0.0) {
			float f = (float)blockPos.getComponentAlongAxis(axis) + entityDimensions.width / 2.0F;
			g = MathHelper.clamp(MathHelper.getLerpProgress(vec3d.getComponentAlongAxis(axis) - (double)f, 0.0, d), 0.0, 1.0);
		} else {
			g = 0.5;
		}

		double h;
		if (e > 0.0) {
			Direction.Axis axis2 = Direction.Axis.Y;
			h = MathHelper.clamp(MathHelper.getLerpProgress(vec3d.getComponentAlongAxis(axis2) - (double)blockPos.getComponentAlongAxis(axis2), 0.0, e), 0.0, 1.0);
		} else {
			h = 0.0;
		}

		Direction.Axis axis2 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
		double i = vec3d.getComponentAlongAxis(axis2) - ((double)blockPos.getComponentAlongAxis(axis2) + 0.5);
		return new Vec3d(g, h, i);
	}

	public static TeleportTarget method_30484(
		ServerWorld serverWorld, class_5459.class_5460 arg, Direction.Axis axis, Vec3d vec3d, EntityDimensions entityDimensions, Vec3d vec3d2, float f, float g
	) {
		BlockPos blockPos = arg.field_25936;
		BlockState blockState = serverWorld.getBlockState(blockPos);
		Direction.Axis axis2 = blockState.get(Properties.HORIZONTAL_AXIS);
		double d = (double)arg.field_25937;
		double e = (double)arg.field_25938;
		int i = axis == axis2 ? 0 : 90;
		Vec3d vec3d3 = axis == axis2 ? vec3d2 : new Vec3d(vec3d2.z, vec3d2.y, -vec3d2.x);
		double h = (double)entityDimensions.width / 2.0 + (d - (double)entityDimensions.width) * vec3d.getX();
		double j = (e - (double)entityDimensions.height) * vec3d.getY();
		double k = 0.5 + vec3d.getZ();
		boolean bl = axis2 == Direction.Axis.X;
		Vec3d vec3d4 = new Vec3d((double)blockPos.getX() + (bl ? h : k), (double)blockPos.getY() + j, (double)blockPos.getZ() + (bl ? k : h));
		return new TeleportTarget(vec3d4, vec3d3, f + (float)i, g);
	}
}
