package net.minecraft;

import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;

public abstract class class_3418 extends class_3443 {
	protected final int width;
	protected final int height;
	protected final int depth;
	protected int hpos = -1;

	protected class_3418(StructurePiece structurePiece, Random random, int i, int j, int k, int l, int m, int n) {
		super(structurePiece, 0);
		this.width = l;
		this.height = m;
		this.depth = n;
		this.method_14926(Direction.class_2353.HORIZONTAL.random(random));
		if (this.getFacing().getAxis() == Direction.Axis.Z) {
			this.structureBounds = new MutableIntBoundingBox(i, j, k, i + l - 1, j + m - 1, k + n - 1);
		} else {
			this.structureBounds = new MutableIntBoundingBox(i, j, k, i + n - 1, j + m - 1, k + l - 1);
		}
	}

	protected class_3418(StructurePiece structurePiece, CompoundTag compoundTag) {
		super(structurePiece, compoundTag);
		this.width = compoundTag.getInt("Width");
		this.height = compoundTag.getInt("Height");
		this.depth = compoundTag.getInt("Depth");
		this.hpos = compoundTag.getInt("HPos");
	}

	@Override
	protected void toNbt(CompoundTag compoundTag) {
		compoundTag.putInt("Width", this.width);
		compoundTag.putInt("Height", this.height);
		compoundTag.putInt("Depth", this.depth);
		compoundTag.putInt("HPos", this.hpos);
	}

	protected boolean method_14839(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, int i) {
		if (this.hpos >= 0) {
			return true;
		} else {
			int j = 0;
			int k = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int l = this.structureBounds.minZ; l <= this.structureBounds.maxZ; l++) {
				for (int m = this.structureBounds.minX; m <= this.structureBounds.maxX; m++) {
					mutable.set(m, 64, l);
					if (mutableIntBoundingBox.contains(mutable)) {
						j += iWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY();
						k++;
					}
				}
			}

			if (k == 0) {
				return false;
			} else {
				this.hpos = j / k;
				this.structureBounds.translate(0, this.hpos - this.structureBounds.minY + i, 0);
				return true;
			}
		}
	}
}
