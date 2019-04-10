package net.minecraft.util.palette;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.IdList;
import net.minecraft.util.PacketByteBuf;

public class IdListPalette<T> implements Palette<T> {
	private final IdList<T> idList;
	private final T fallback;

	public IdListPalette(IdList<T> idList, T object) {
		this.idList = idList;
		this.fallback = object;
	}

	@Override
	public int getIndex(T object) {
		int i = this.idList.getId(object);
		return i == -1 ? 0 : i;
	}

	@Override
	public boolean accepts(T object) {
		return true;
	}

	@Override
	public T getByIndex(int i) {
		T object = this.idList.get(i);
		return object == null ? this.fallback : object;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void fromPacket(PacketByteBuf packetByteBuf) {
	}

	@Override
	public void toPacket(PacketByteBuf packetByteBuf) {
	}

	@Override
	public int getPacketSize() {
		return PacketByteBuf.getVarIntSizeBytes(0);
	}

	@Override
	public void fromTag(ListTag listTag) {
	}
}
