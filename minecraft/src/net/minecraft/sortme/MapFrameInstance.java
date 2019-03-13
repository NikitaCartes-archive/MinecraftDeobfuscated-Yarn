package net.minecraft.sortme;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;

public class MapFrameInstance {
	private final BlockPos field_75;
	private final int rotation;
	private final int entityId;

	public MapFrameInstance(BlockPos blockPos, int i, int j) {
		this.field_75 = blockPos;
		this.rotation = i;
		this.entityId = j;
	}

	public static MapFrameInstance method_87(CompoundTag compoundTag) {
		BlockPos blockPos = TagHelper.deserializeBlockPos(compoundTag.getCompound("Pos"));
		int i = compoundTag.getInt("Rotation");
		int j = compoundTag.getInt("EntityId");
		return new MapFrameInstance(blockPos, i, j);
	}

	public CompoundTag method_84() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.method_10566("Pos", TagHelper.serializeBlockPos(this.field_75));
		compoundTag.putInt("Rotation", this.rotation);
		compoundTag.putInt("EntityId", this.entityId);
		return compoundTag;
	}

	public BlockPos method_86() {
		return this.field_75;
	}

	public int getRotation() {
		return this.rotation;
	}

	public int getEntityId() {
		return this.entityId;
	}

	public String method_82() {
		return method_81(this.field_75);
	}

	public static String method_81(BlockPos blockPos) {
		return "frame-" + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ();
	}
}
