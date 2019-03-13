package net.minecraft.util.palette;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.IdList;
import net.minecraft.util.PacketByteBuf;

public class IdListPalette<T> implements Palette<T> {
	private final IdList<T> field_12828;
	private final T field_12829;

	public IdListPalette(IdList<T> idList, T object) {
		this.field_12828 = idList;
		this.field_12829 = object;
	}

	@Override
	public int getIndex(T object) {
		int i = this.field_12828.getId(object);
		return i == -1 ? 0 : i;
	}

	@Override
	public T getByIndex(int i) {
		T object = this.field_12828.get(i);
		return object == null ? this.field_12829 : object;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_12289(PacketByteBuf packetByteBuf) {
	}

	@Override
	public void method_12287(PacketByteBuf packetByteBuf) {
	}

	@Override
	public int getPacketSize() {
		return PacketByteBuf.getVarIntSizeBytes(0);
	}

	@Override
	public void method_12286(ListTag listTag) {
	}
}
