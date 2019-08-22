package net.minecraft.item.map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;

public class MapFrameMarker {
	private final BlockPos pos;
	private final int rotation;
	private final int entityId;

	public MapFrameMarker(BlockPos blockPos, int i, int j) {
		this.pos = blockPos;
		this.rotation = i;
		this.entityId = j;
	}

	public static MapFrameMarker fromTag(CompoundTag compoundTag) {
		BlockPos blockPos = TagHelper.deserializeBlockPos(compoundTag.getCompound("Pos"));
		int i = compoundTag.getInt("Rotation");
		int j = compoundTag.getInt("EntityId");
		return new MapFrameMarker(blockPos, i, j);
	}

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.put("Pos", TagHelper.serializeBlockPos(this.pos));
		compoundTag.putInt("Rotation", this.rotation);
		compoundTag.putInt("EntityId", this.entityId);
		return compoundTag;
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

	public static String getKey(BlockPos blockPos) {
		return "frame-" + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ();
	}
}
