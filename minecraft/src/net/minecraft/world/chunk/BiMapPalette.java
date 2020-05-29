package net.minecraft.world.chunk;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.collection.Int2ObjectBiMap;

public class BiMapPalette<T> implements Palette<T> {
	private final IdList<T> idList;
	private final Int2ObjectBiMap<T> map;
	private final PaletteResizeListener<T> resizeHandler;
	private final Function<CompoundTag, T> elementDeserializer;
	private final Function<T, CompoundTag> elementSerializer;
	private final int indexBits;

	public BiMapPalette(
		IdList<T> idList,
		int indexBits,
		PaletteResizeListener<T> resizeHandler,
		Function<CompoundTag, T> elementDeserializer,
		Function<T, CompoundTag> elementSerializer
	) {
		this.idList = idList;
		this.indexBits = indexBits;
		this.resizeHandler = resizeHandler;
		this.elementDeserializer = elementDeserializer;
		this.elementSerializer = elementSerializer;
		this.map = new Int2ObjectBiMap<>(1 << indexBits);
	}

	@Override
	public int getIndex(T object) {
		int i = this.map.getId(object);
		if (i == -1) {
			i = this.map.add(object);
			if (i >= 1 << this.indexBits) {
				i = this.resizeHandler.onResize(this.indexBits + 1, object);
			}
		}

		return i;
	}

	@Override
	public boolean accepts(Predicate<T> predicate) {
		for (int i = 0; i < this.getIndexBits(); i++) {
			if (predicate.test(this.map.get(i))) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	@Override
	public T getByIndex(int index) {
		return this.map.get(index);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void fromPacket(PacketByteBuf buf) {
		this.map.clear();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.map.add(this.idList.get(buf.readVarInt()));
		}
	}

	@Override
	public void toPacket(PacketByteBuf buf) {
		int i = this.getIndexBits();
		buf.writeVarInt(i);

		for (int j = 0; j < i; j++) {
			buf.writeVarInt(this.idList.getId(this.map.get(j)));
		}
	}

	@Override
	public int getPacketSize() {
		int i = PacketByteBuf.getVarIntSizeBytes(this.getIndexBits());

		for (int j = 0; j < this.getIndexBits(); j++) {
			i += PacketByteBuf.getVarIntSizeBytes(this.idList.getId(this.map.get(j)));
		}

		return i;
	}

	public int getIndexBits() {
		return this.map.size();
	}

	@Override
	public void fromTag(ListTag tag) {
		this.map.clear();

		for (int i = 0; i < tag.size(); i++) {
			this.map.add((T)this.elementDeserializer.apply(tag.getCompound(i)));
		}
	}

	public void toTag(ListTag tag) {
		for (int i = 0; i < this.getIndexBits(); i++) {
			tag.add(this.elementSerializer.apply(this.map.get(i)));
		}
	}
}
