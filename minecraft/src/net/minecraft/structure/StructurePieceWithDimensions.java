package net.minecraft.structure;

import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;

public abstract class StructurePieceWithDimensions extends StructurePiece {
	protected final int width;
	protected final int height;
	protected final int depth;
	protected int hPos = -1;

	protected StructurePieceWithDimensions(StructurePieceType structurePieceType, Random random, int i, int j, int k, int l, int m, int n) {
		super(structurePieceType, 0);
		this.width = l;
		this.height = m;
		this.depth = n;
		this.setOrientation(Direction.Type.HORIZONTAL.random(random));
		if (this.getFacing().getAxis() == Direction.Axis.Z) {
			this.boundingBox = new BlockBox(i, j, k, i + l - 1, j + m - 1, k + n - 1);
		} else {
			this.boundingBox = new BlockBox(i, j, k, i + n - 1, j + m - 1, k + l - 1);
		}
	}

	protected StructurePieceWithDimensions(StructurePieceType structurePieceType, CompoundTag compoundTag) {
		super(structurePieceType, compoundTag);
		this.width = compoundTag.getInt("Width");
		this.height = compoundTag.getInt("Height");
		this.depth = compoundTag.getInt("Depth");
		this.hPos = compoundTag.getInt("HPos");
	}

	@Override
	protected void toNbt(CompoundTag compoundTag) {
		compoundTag.putInt("Width", this.width);
		compoundTag.putInt("Height", this.height);
		compoundTag.putInt("Depth", this.depth);
		compoundTag.putInt("HPos", this.hPos);
	}

	protected boolean method_14839(IWorld iWorld, BlockBox blockBox, int i) {
		if (this.hPos >= 0) {
			return true;
		} else {
			int j = 0;
			int k = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int l = this.boundingBox.minZ; l <= this.boundingBox.maxZ; l++) {
				for (int m = this.boundingBox.minX; m <= this.boundingBox.maxX; m++) {
					mutable.set(m, 64, l);
					if (blockBox.contains(mutable)) {
						j += iWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY();
						k++;
					}
				}
			}

			if (k == 0) {
				return false;
			} else {
				this.hPos = j / k;
				this.boundingBox.offset(0, this.hPos - this.boundingBox.minY + i, 0);
				return true;
			}
		}
	}
}
