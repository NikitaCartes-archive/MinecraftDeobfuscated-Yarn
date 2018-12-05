package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.IdList;
import net.minecraft.util.PacketByteBuf;

public class class_2816<T> implements class_2837<T> {
	private final IdList<T> field_12828;
	private final T field_12829;

	public class_2816(IdList<T> idList, T object) {
		this.field_12828 = idList;
		this.field_12829 = object;
	}

	@Override
	public int method_12291(T object) {
		int i = this.field_12828.getId(object);
		return i == -1 ? 0 : i;
	}

	@Override
	public T method_12288(int i) {
		T object = this.field_12828.getInt(i);
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
	public int method_12290() {
		return PacketByteBuf.getVarIntSizeBytes(0);
	}

	@Override
	public void method_12286(ListTag listTag) {
	}
}
