package net.minecraft.item.map;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;

public class MapFrameMarker {
	private final BlockPos pos;
	private final int rotation;
	private final int entityId;

	public MapFrameMarker(BlockPos pos, int rotation, int entityId) {
		this.pos = pos;
		this.rotation = rotation;
		this.entityId = entityId;
	}

	public static MapFrameMarker fromNbt(NbtCompound nbt) {
		BlockPos blockPos = NbtHelper.toBlockPos(nbt.getCompound("Pos"));
		int i = nbt.getInt("Rotation");
		int j = nbt.getInt("EntityId");
		return new MapFrameMarker(blockPos, i, j);
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.put("Pos", NbtHelper.fromBlockPos(this.pos));
		nbtCompound.putInt("Rotation", this.rotation);
		nbtCompound.putInt("EntityId", this.entityId);
		return nbtCompound;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public int getRotation() {
		return this.rotation;
	}

	public int getEntityId() {
		return this.entityId;
	}

	public String getKey() {
		return getKey(this.pos);
	}

	public static String getKey(BlockPos pos) {
		return "frame-" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
	}
}
