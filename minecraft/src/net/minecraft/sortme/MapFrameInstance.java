package net.minecraft.sortme;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;

public class MapFrameInstance {
	private final BlockPos pos;
	private final int rotation;
	private final int entityId;

	public MapFrameInstance(BlockPos blockPos, int i, int j) {
		this.pos = blockPos;
		this.rotation = i;
		this.entityId = j;
	}

	public static MapFrameInstance fromNbt(CompoundTag compoundTag) {
		BlockPos blockPos = TagHelper.deserializeBlockPos(compoundTag.getCompound("Pos"));
		int i = compoundTag.getInt("Rotation");
		int j = compoundTag.getInt("EntityId");
		return new MapFrameInstance(blockPos, i, j);
	}

	public CompoundTag getNbt() {
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

	public String method_82() {
		return method_81(this.pos);
	}

	public static String method_81(BlockPos blockPos) {
		return "frame-" + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ();
	}
}
