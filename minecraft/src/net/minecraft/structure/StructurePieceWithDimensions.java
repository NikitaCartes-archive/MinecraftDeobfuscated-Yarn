package net.minecraft.structure;

import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;

public abstract class StructurePieceWithDimensions extends StructurePiece {
	protected final int width;
	protected final int height;
	protected final int depth;
	protected int hPos = -1;

	protected StructurePieceWithDimensions(StructurePieceType type, Random random, int x, int y, int z, int width, int height, int depth) {
		super(type, 0);
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.setOrientation(Direction.Type.HORIZONTAL.random(random));
		if (this.getFacing().getAxis() == Direction.Axis.Z) {
			this.boundingBox = new BlockBox(x, y, z, x + width - 1, y + height - 1, z + depth - 1);
		} else {
			this.boundingBox = new BlockBox(x, y, z, x + depth - 1, y + height - 1, z + width - 1);
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
	protected void toNbt(CompoundTag tag) {
		tag.putInt("Width", this.width);
		tag.putInt("Height", this.height);
		tag.putInt("Depth", this.depth);
		tag.putInt("HPos", this.hPos);
	}

	protected boolean method_14839(WorldAccess world, BlockBox boundingBox, int i) {
		if (this.hPos >= 0) {
			return true;
		} else {
			int j = 0;
			int k = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int l = this.boundingBox.minZ; l <= this.boundingBox.maxZ; l++) {
				for (int m = this.boundingBox.minX; m <= this.boundingBox.maxX; m++) {
					mutable.set(m, 64, l);
					if (boundingBox.contains(mutable)) {
						j += world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY();
						k++;
					}
				}
			}

			if (k == 0) {
				return false;
			} else {
				this.hPos = j / k;
				this.boundingBox.move(0, this.hPos - this.boundingBox.minY + i, 0);
				return true;
			}
		}
	}
}
