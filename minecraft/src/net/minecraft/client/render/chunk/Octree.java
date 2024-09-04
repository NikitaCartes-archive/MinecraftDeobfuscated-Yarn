package net.minecraft.client.render.chunk;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class Octree {
	private final Octree.Branch root;
	final BlockPos centerPos;

	public Octree(ChunkSectionPos sectionPos, int viewDistance, int sizeY, int bottomY) {
		int i = viewDistance * 2 + 1;
		int j = MathHelper.smallestEncompassingPowerOfTwo(i);
		int k = viewDistance * 16;
		BlockPos blockPos = sectionPos.getMinPos();
		this.centerPos = sectionPos.getCenterPos();
		int l = blockPos.getX() - k;
		int m = l + j * 16 - 1;
		int n = j >= sizeY ? bottomY : blockPos.getY() - k;
		int o = n + j * 16 - 1;
		int p = blockPos.getZ() - k;
		int q = p + j * 16 - 1;
		this.root = new Octree.Branch(new BlockBox(l, n, p, m, o, q));
	}

	public boolean add(ChunkBuilder.BuiltChunk chunk) {
		return this.root.add(chunk);
	}

	public void visit(Octree.Visitor visitor, Frustum frustum, int i) {
		this.root.visit(visitor, false, frustum, 0, i, true);
	}

	boolean method_64061(double d, double e, double f, double g, double h, double i, int j) {
		int k = this.centerPos.getX();
		int l = this.centerPos.getY();
		int m = this.centerPos.getZ();
		return (double)k > d - (double)j
			&& (double)k < g + (double)j
			&& (double)l > e - (double)j
			&& (double)l < h + (double)j
			&& (double)m > f - (double)j
			&& (double)m < i + (double)j;
	}

	@Environment(EnvType.CLIENT)
	static enum AxisOrder {
		XYZ(4, 2, 1),
		XZY(4, 1, 2),
		YXZ(2, 4, 1),
		YZX(1, 4, 2),
		ZXY(2, 1, 4),
		ZYX(1, 2, 4);

		final int x;
		final int y;
		final int z;

		private AxisOrder(final int x, final int y, final int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public static Octree.AxisOrder fromPos(int x, int y, int z) {
			if (x > y && x > z) {
				return y > z ? XYZ : XZY;
			} else if (y > x && y > z) {
				return x > z ? YXZ : YZX;
			} else {
				return x > y ? ZXY : ZYX;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class Branch implements Octree.Node {
		private final Octree.Node[] children = new Octree.Node[8];
		private final BlockBox box;
		private final int centerX;
		private final int centerY;
		private final int centerZ;
		private final Octree.AxisOrder axisOrder;
		private final boolean easternSide;
		private final boolean topSide;
		private final boolean southernSide;

		public Branch(final BlockBox box) {
			this.box = box;
			this.centerX = this.box.getMinX() + this.box.getBlockCountX() / 2;
			this.centerY = this.box.getMinY() + this.box.getBlockCountY() / 2;
			this.centerZ = this.box.getMinZ() + this.box.getBlockCountZ() / 2;
			int i = Octree.this.centerPos.getX() - this.centerX;
			int j = Octree.this.centerPos.getY() - this.centerY;
			int k = Octree.this.centerPos.getZ() - this.centerZ;
			this.axisOrder = Octree.AxisOrder.fromPos(Math.abs(i), Math.abs(j), Math.abs(k));
			this.easternSide = i < 0;
			this.topSide = j < 0;
			this.southernSide = k < 0;
		}

		public boolean add(ChunkBuilder.BuiltChunk chunk) {
			boolean bl = chunk.getOrigin().getX() - this.centerX < 0;
			boolean bl2 = chunk.getOrigin().getY() - this.centerY < 0;
			boolean bl3 = chunk.getOrigin().getZ() - this.centerZ < 0;
			boolean bl4 = bl != this.easternSide;
			boolean bl5 = bl2 != this.topSide;
			boolean bl6 = bl3 != this.southernSide;
			int i = getIndex(this.axisOrder, bl4, bl5, bl6);
			if (this.areChildrenLeaves()) {
				boolean bl7 = this.children[i] != null;
				this.children[i] = Octree.this.new Leaf(chunk);
				return !bl7;
			} else if (this.children[i] != null) {
				Octree.Branch branch = (Octree.Branch)this.children[i];
				return branch.add(chunk);
			} else {
				BlockBox blockBox = this.getChildBox(bl, bl2, bl3);
				Octree.Branch branch2 = Octree.this.new Branch(blockBox);
				this.children[i] = branch2;
				return branch2.add(chunk);
			}
		}

		/**
		 * @param sameRelativeSideY whether the side of the chunk relative to this branch's center
		 * equals the side of the branch relative to the player, on the Y axis
		 * @param sameRelativeSideX whether the side of the chunk relative to this branch's center
		 * equals the side of the branch relative to the player, on the X axis
		 * @param sameRelativeSideZ whether the side of the chunk relative to this branch's center
		 * equals the side of the branch relative to the player, on the Z axis
		 */
		private static int getIndex(Octree.AxisOrder axisOrder, boolean sameRelativeSideX, boolean sameRelativeSideY, boolean sameRelativeSideZ) {
			int i = 0;
			if (sameRelativeSideX) {
				i += axisOrder.x;
			}

			if (sameRelativeSideY) {
				i += axisOrder.y;
			}

			if (sameRelativeSideZ) {
				i += axisOrder.z;
			}

			return i;
		}

		private boolean areChildrenLeaves() {
			return this.box.getBlockCountX() == 32;
		}

		private BlockBox getChildBox(boolean western, boolean bottom, boolean northern) {
			int i;
			int j;
			if (western) {
				i = this.box.getMinX();
				j = this.centerX - 1;
			} else {
				i = this.centerX;
				j = this.box.getMaxX();
			}

			int k;
			int l;
			if (bottom) {
				k = this.box.getMinY();
				l = this.centerY - 1;
			} else {
				k = this.centerY;
				l = this.box.getMaxY();
			}

			int m;
			int n;
			if (northern) {
				m = this.box.getMinZ();
				n = this.centerZ - 1;
			} else {
				m = this.centerZ;
				n = this.box.getMaxZ();
			}

			return new BlockBox(i, k, m, j, l, n);
		}

		@Override
		public void visit(Octree.Visitor visitor, boolean skipVisibilityCheck, Frustum frustum, int depth, int i, boolean bl) {
			boolean bl2 = skipVisibilityCheck;
			if (!skipVisibilityCheck) {
				int j = frustum.intersectAab(this.box);
				skipVisibilityCheck = j == -2;
				bl2 = j == -2 || j == -1;
			}

			if (bl2) {
				bl = bl
					&& Octree.this.method_64061(
						(double)this.box.getMinX(),
						(double)this.box.getMinY(),
						(double)this.box.getMinZ(),
						(double)this.box.getMaxX(),
						(double)this.box.getMaxY(),
						(double)this.box.getMaxZ(),
						i
					);
				visitor.visit(this, skipVisibilityCheck, depth, bl);

				for (Octree.Node node : this.children) {
					if (node != null) {
						node.visit(visitor, skipVisibilityCheck, frustum, depth + 1, i, bl);
					}
				}
			}
		}

		@Nullable
		@Override
		public ChunkBuilder.BuiltChunk getBuiltChunk() {
			return null;
		}

		@Override
		public Box getBoundingBox() {
			return new Box(
				(double)this.box.getMinX(),
				(double)this.box.getMinY(),
				(double)this.box.getMinZ(),
				(double)(this.box.getMaxX() + 1),
				(double)(this.box.getMaxY() + 1),
				(double)(this.box.getMaxZ() + 1)
			);
		}
	}

	@Environment(EnvType.CLIENT)
	final class Leaf implements Octree.Node {
		private final ChunkBuilder.BuiltChunk field_54166;

		Leaf(final ChunkBuilder.BuiltChunk builtChunk) {
			this.field_54166 = builtChunk;
		}

		@Override
		public void visit(Octree.Visitor visitor, boolean skipVisibilityCheck, Frustum frustum, int depth, int i, boolean bl) {
			Box box = this.field_54166.getBoundingBox();
			if (skipVisibilityCheck || frustum.isVisible(this.getBuiltChunk().getBoundingBox())) {
				bl = bl && Octree.this.method_64061(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, i);
				visitor.visit(this, skipVisibilityCheck, depth, bl);
			}
		}

		@Override
		public ChunkBuilder.BuiltChunk getBuiltChunk() {
			return this.field_54166;
		}

		@Override
		public Box getBoundingBox() {
			return this.field_54166.getBoundingBox();
		}
	}

	@Environment(EnvType.CLIENT)
	public interface Node {
		void visit(Octree.Visitor visitor, boolean skipVisibilityCheck, Frustum frustum, int depth, int i, boolean bl);

		@Nullable
		ChunkBuilder.BuiltChunk getBuiltChunk();

		Box getBoundingBox();
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface Visitor {
		void visit(Octree.Node node, boolean skipVisibilityCheck, int depth, boolean bl);
	}
}
