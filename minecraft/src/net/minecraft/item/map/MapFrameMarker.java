package net.minecraft.item.map;

import java.util.Optional;
import javax.annotation.Nullable;
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

	@Nullable
	public static MapFrameMarker fromNbt(NbtCompound nbt) {
		Optional<BlockPos> optional = NbtHelper.toBlockPos(nbt, "pos");
		if (optional.isEmpty()) {
			return null;
		} else {
			int i = nbt.getInt("rotation");
			int j = nbt.getInt("entity_id");
			return new MapFrameMarker((BlockPos)optional.get(), i, j);
		}
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.put("pos", NbtHelper.fromBlockPos(this.pos));
		nbtCompound.putInt("rotation", this.rotation);
		nbtCompound.putInt("entity_id", this.entityId);
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
