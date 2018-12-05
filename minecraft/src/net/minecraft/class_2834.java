package net.minecraft;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.IdList;
import net.minecraft.util.PacketByteBuf;

public class class_2834<T> implements class_2837<T> {
	private final IdList<T> field_12900;
	private final T[] field_12904;
	private final class_2835<T> field_12905;
	private final Function<CompoundTag, T> field_12902;
	private final int field_12903;
	private int field_12901;

	public class_2834(IdList<T> idList, int i, class_2835<T> arg, Function<CompoundTag, T> function) {
		this.field_12900 = idList;
		this.field_12904 = (T[])(new Object[1 << i]);
		this.field_12903 = i;
		this.field_12905 = arg;
		this.field_12902 = function;
	}

	@Override
	public int method_12291(T object) {
		for (int i = 0; i < this.field_12901; i++) {
			if (this.field_12904[i] == object) {
				return i;
			}
		}

		int ix = this.field_12901;
		if (ix < this.field_12904.length) {
			this.field_12904[ix] = object;
			this.field_12901++;
			return ix;
		} else {
			return this.field_12905.onResize(this.field_12903 + 1, object);
		}
	}

	@Nullable
	@Override
	public T method_12288(int i) {
		return i >= 0 && i < this.field_12901 ? this.field_12904[i] : null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_12289(PacketByteBuf packetByteBuf) {
		this.field_12901 = packetByteBuf.readVarInt();

		for (int i = 0; i < this.field_12901; i++) {
			this.field_12904[i] = this.field_12900.getInt(packetByteBuf.readVarInt());
		}
	}

	@Override
	public void method_12287(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeVarInt(this.field_12901);

		for (int i = 0; i < this.field_12901; i++) {
			packetByteBuf.writeVarInt(this.field_12900.getId(this.field_12904[i]));
		}
	}

	@Override
	public int method_12290() {
		int i = PacketByteBuf.getVarIntSizeBytes(this.method_12282());

		for (int j = 0; j < this.method_12282(); j++) {
			i += PacketByteBuf.getVarIntSizeBytes(this.field_12900.getId(this.field_12904[j]));
		}

		return i;
	}

	public int method_12282() {
		return this.field_12901;
	}

	@Override
	public void method_12286(ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			this.field_12904[i] = (T)this.field_12902.apply(listTag.getCompoundTag(i));
		}

		this.field_12901 = listTag.size();
	}
}
