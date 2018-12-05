package net.minecraft;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.IdList;
import net.minecraft.util.Int2ObjectBiMap;
import net.minecraft.util.PacketByteBuf;

public class class_2814<T> implements class_2837<T> {
	private final IdList<T> field_12821;
	private final Int2ObjectBiMap<T> field_12824;
	private final class_2835<T> field_12825;
	private final Function<CompoundTag, T> field_12823;
	private final Function<T, CompoundTag> field_12826;
	private final int field_12822;

	public class_2814(IdList<T> idList, int i, class_2835<T> arg, Function<CompoundTag, T> function, Function<T, CompoundTag> function2) {
		this.field_12821 = idList;
		this.field_12822 = i;
		this.field_12825 = arg;
		this.field_12823 = function;
		this.field_12826 = function2;
		this.field_12824 = new Int2ObjectBiMap<>(1 << i);
	}

	@Override
	public int method_12291(T object) {
		int i = this.field_12824.getId(object);
		if (i == -1) {
			i = this.field_12824.add(object);
			if (i >= 1 << this.field_12822) {
				i = this.field_12825.onResize(this.field_12822 + 1, object);
			}
		}

		return i;
	}

	@Nullable
	@Override
	public T method_12288(int i) {
		return this.field_12824.getInt(i);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_12289(PacketByteBuf packetByteBuf) {
		this.field_12824.clear();
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.field_12824.add(this.field_12821.getInt(packetByteBuf.readVarInt()));
		}
	}

	@Override
	public void method_12287(PacketByteBuf packetByteBuf) {
		int i = this.method_12197();
		packetByteBuf.writeVarInt(i);

		for (int j = 0; j < i; j++) {
			packetByteBuf.writeVarInt(this.field_12821.getId(this.field_12824.getInt(j)));
		}
	}

	@Override
	public int method_12290() {
		int i = PacketByteBuf.getVarIntSizeBytes(this.method_12197());

		for (int j = 0; j < this.method_12197(); j++) {
			i += PacketByteBuf.getVarIntSizeBytes(this.field_12821.getId(this.field_12824.getInt(j)));
		}

		return i;
	}

	public int method_12197() {
		return this.field_12824.size();
	}

	@Override
	public void method_12286(ListTag listTag) {
		this.field_12824.clear();

		for (int i = 0; i < listTag.size(); i++) {
			this.field_12824.add((T)this.field_12823.apply(listTag.getCompoundTag(i)));
		}
	}

	public void method_12196(ListTag listTag) {
		for (int i = 0; i < this.method_12197(); i++) {
			listTag.add((Tag)this.field_12826.apply(this.field_12824.getInt(i)));
		}
	}
}
