package net.minecraft.world.chunk;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IdList;

public class IdListPalette<T> implements Palette<T> {
	private final IdList<T> idList;
	private final T defaultValue;

	public IdListPalette(IdList<T> idList, T defaultValue) {
		this.idList = idList;
		this.defaultValue = defaultValue;
	}

	@Override
	public int getIndex(T object) {
		int i = this.idList.getRawId(object);
		return i == -1 ? 0 : i;
	}

	@Override
	public boolean accepts(Predicate<T> predicate) {
		return true;
	}

	@Override
	public T getByIndex(int index) {
		T object = this.idList.get(index);
		return object == null ? this.defaultValue : object;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void fromPacket(PacketByteBuf buf) {
	}

	@Override
	public void toPacket(PacketByteBuf buf) {
	}

	@Override
	public int getPacketSize() {
		return PacketByteBuf.getVarIntSizeBytes(0);
	}

	@Override
	public void readNbt(NbtList tag) {
	}
}
