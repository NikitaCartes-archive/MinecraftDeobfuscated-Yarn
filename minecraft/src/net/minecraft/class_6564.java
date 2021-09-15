package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;

public class class_6564<T> implements Palette<T> {
	private final IndexedIterable<T> field_34573;
	@Nullable
	private T field_34574;
	private final PaletteResizeListener<T> field_34575;

	public class_6564(IndexedIterable<T> indexedIterable, PaletteResizeListener<T> paletteResizeListener) {
		this.field_34573 = indexedIterable;
		this.field_34575 = paletteResizeListener;
	}

	public static <A> Palette<A> method_38316(int i, IndexedIterable<A> indexedIterable, PaletteResizeListener<A> paletteResizeListener) {
		return new class_6564<>(indexedIterable, paletteResizeListener);
	}

	@Override
	public int getIndex(T object) {
		if (this.field_34574 != null && this.field_34574 != object) {
			return this.field_34575.onResize(1, object);
		} else {
			this.field_34574 = object;
			return 0;
		}
	}

	@Override
	public boolean accepts(Predicate<T> predicate) {
		if (this.field_34574 == null) {
			throw new IllegalStateException("Use of an uninitialized palette");
		} else {
			return predicate.test(this.field_34574);
		}
	}

	@Override
	public T getByIndex(int index) {
		if (this.field_34574 != null && index == 0) {
			return this.field_34574;
		} else {
			throw new IllegalStateException("Missing Palette entry for id " + index + ".");
		}
	}

	@Override
	public void fromPacket(PacketByteBuf buf) {
		this.field_34574 = this.field_34573.get(buf.readVarInt());
	}

	@Override
	public void toPacket(PacketByteBuf buf) {
		if (this.field_34574 == null) {
			throw new IllegalStateException("Use of an uninitialized palette");
		} else {
			buf.writeVarInt(this.field_34573.getRawId(this.field_34574));
		}
	}

	@Override
	public int getPacketSize() {
		if (this.field_34574 == null) {
			throw new IllegalStateException("Use of an uninitialized palette");
		} else {
			return PacketByteBuf.getVarIntLength(this.field_34573.getRawId(this.field_34574));
		}
	}

	@Override
	public int getIndexBits() {
		return 1;
	}
}
