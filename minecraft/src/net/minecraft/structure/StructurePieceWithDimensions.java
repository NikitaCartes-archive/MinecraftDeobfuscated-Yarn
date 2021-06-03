package net.minecraft.structure;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
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

	protected StructurePieceWithDimensions(StructurePieceType type, int x, int y, int z, int width, int height, int depth, Direction orientation) {
		super(type, 0, StructurePiece.createBox(x, y, z, orientation, width, height, depth));
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.setOrientation(orientation);
	}

	protected StructurePieceWithDimensions(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
		super(structurePieceType, nbtCompound);
		this.width = nbtCompound.getInt("Width");
		this.height = nbtCompound.getInt("Height");
		this.depth = nbtCompound.getInt("Depth");
		this.hPos = nbtCompound.getInt("HPos");
	}

	@Override
	protected void writeNbt(ServerWorld world, NbtCompound nbt) {
		nbt.putInt("Width", this.width);
		nbt.putInt("Height", this.height);
		nbt.putInt("Depth", this.depth);
		nbt.putInt("HPos", this.hPos);
	}

	protected boolean method_14839(WorldAccess world, BlockBox boundingBox, int i) {
		if (this.hPos >= 0) {
			return true;
		} else {
			int j = 0;
			int k = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int l = this.boundingBox.getMinZ(); l <= this.boundingBox.getMaxZ(); l++) {
				for (int m = this.boundingBox.getMinX(); m <= this.boundingBox.getMaxX(); m++) {
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
				this.boundingBox.move(0, this.hPos - this.boundingBox.getMinY() + i, 0);
				return true;
			}
		}
	}
}
