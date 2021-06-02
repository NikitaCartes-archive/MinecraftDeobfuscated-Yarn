package net.minecraft.world.chunk;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.thread.AtomicStack;
import net.minecraft.util.thread.LockHelper;

public class PalettedContainer<T> implements PaletteResizeListener<T> {
	private static final int field_31411 = 4096;
	public static final int field_31409 = 9;
	public static final int field_31410 = 4;
	private final Palette<T> fallbackPalette;
	private final PaletteResizeListener<T> noOpPaletteResizeHandler = (newSize, added) -> 0;
	private final IdList<T> idList;
	private final Function<NbtCompound, T> elementDeserializer;
	private final Function<T, NbtCompound> elementSerializer;
	private final T defaultValue;
	protected PackedIntegerArray data;
	private Palette<T> palette;
	private int paletteSize;
	private final Semaphore writeLock = new Semaphore(1);
	@Nullable
	private final AtomicStack<Pair<Thread, StackTraceElement[]>> lockStack = null;

	public void lock() {
		if (this.lockStack != null) {
			Thread thread = Thread.currentThread();
			this.lockStack.push(Pair.of(thread, thread.getStackTrace()));
		}

		LockHelper.checkLock(this.writeLock, this.lockStack, "PalettedContainer");
	}

	public void unlock() {
		this.writeLock.release();
	}

	public PalettedContainer(
		Palette<T> fallbackPalette, IdList<T> idList, Function<NbtCompound, T> elementDeserializer, Function<T, NbtCompound> elementSerializer, T defaultElement
	) {
		this.fallbackPalette = fallbackPalette;
		this.idList = idList;
		this.elementDeserializer = elementDeserializer;
		this.elementSerializer = elementSerializer;
		this.defaultValue = defaultElement;
		this.setPaletteSize(4);
	}

	private static int toIndex(int x, int y, int z) {
		return y << 8 | z << 4 | x;
	}

	private void setPaletteSize(int size) {
		if (size != this.paletteSize) {
			this.paletteSize = size;
			if (this.paletteSize <= 4) {
				this.paletteSize = 4;
				this.palette = new ArrayPalette<>(this.idList, this.paletteSize, this, this.elementDeserializer);
			} else if (this.paletteSize < 9) {
				this.palette = new BiMapPalette<>(this.idList, this.paletteSize, this, this.elementDeserializer, this.elementSerializer);
			} else {
				this.palette = this.fallbackPalette;
				this.paletteSize = MathHelper.log2DeBruijn(this.idList.size());
			}

			this.palette.getIndex(this.defaultValue);
			this.data = new PackedIntegerArray(this.paletteSize, 4096);
		}
	}

	@Override
	public int onResize(int i, T object) {
		PackedIntegerArray packedIntegerArray = this.data;
		Palette<T> palette = this.palette;
		this.setPaletteSize(i);

		for (int j = 0; j < packedIntegerArray.getSize(); j++) {
			T object2 = palette.getByIndex(packedIntegerArray.get(j));
			if (object2 != null) {
				this.set(j, object2);
			}
		}

		return this.palette.getIndex(object);
	}

	public T setSync(int x, int y, int z, T value) {
		Object var6;
		try {
			this.lock();
			T object = this.setAndGetOldValue(toIndex(x, y, z), value);
			var6 = object;
		} finally {
			this.unlock();
		}

		return (T)var6;
	}

	public T set(int x, int y, int z, T value) {
		return this.setAndGetOldValue(toIndex(x, y, z), value);
	}

	private T setAndGetOldValue(int index, T value) {
		int i = this.palette.getIndex(value);
		int j = this.data.setAndGetOldValue(index, i);
		T object = this.palette.getByIndex(j);
		return object == null ? this.defaultValue : object;
	}

	public void method_35321(int i, int j, int k, T object) {
		try {
			this.lock();
			this.set(toIndex(i, j, k), object);
		} finally {
			this.unlock();
		}
	}

	private void set(int index, T object) {
		int i = this.palette.getIndex(object);
		this.data.set(index, i);
	}

	public T get(int x, int y, int z) {
		return this.get(toIndex(x, y, z));
	}

	protected T get(int index) {
		T object = this.palette.getByIndex(this.data.get(index));
		return object == null ? this.defaultValue : object;
	}

	public void fromPacket(PacketByteBuf buf) {
		try {
			this.lock();
			int i = buf.readByte();
			if (this.paletteSize != i) {
				this.setPaletteSize(i);
			}

			this.palette.fromPacket(buf);
			buf.readLongArray(this.data.getStorage());
		} finally {
			this.unlock();
		}
	}

	public void toPacket(PacketByteBuf buf) {
		try {
			this.lock();
			buf.writeByte(this.paletteSize);
			this.palette.toPacket(buf);
			buf.writeLongArray(this.data.getStorage());
		} finally {
			this.unlock();
		}
	}

	public void read(NbtList paletteNbt, long[] data) {
		try {
			this.lock();
			int i = Math.max(4, MathHelper.log2DeBruijn(paletteNbt.size()));
			if (i != this.paletteSize) {
				this.setPaletteSize(i);
			}

			this.palette.readNbt(paletteNbt);
			int j = data.length * 64 / 4096;
			if (this.palette == this.fallbackPalette) {
				Palette<T> palette = new BiMapPalette<>(this.idList, i, this.noOpPaletteResizeHandler, this.elementDeserializer, this.elementSerializer);
				palette.readNbt(paletteNbt);
				PackedIntegerArray packedIntegerArray = new PackedIntegerArray(i, 4096, data);

				for (int k = 0; k < 4096; k++) {
					this.data.set(k, this.fallbackPalette.getIndex(palette.getByIndex(packedIntegerArray.get(k))));
				}
			} else if (j == this.paletteSize) {
				System.arraycopy(data, 0, this.data.getStorage(), 0, data.length);
			} else {
				PackedIntegerArray packedIntegerArray2 = new PackedIntegerArray(j, 4096, data);

				for (int l = 0; l < 4096; l++) {
					this.data.set(l, packedIntegerArray2.get(l));
				}
			}
		} finally {
			this.unlock();
		}
	}

	public void write(NbtCompound nbt, String paletteKey, String dataKey) {
		try {
			this.lock();
			BiMapPalette<T> biMapPalette = new BiMapPalette<>(
				this.idList, this.paletteSize, this.noOpPaletteResizeHandler, this.elementDeserializer, this.elementSerializer
			);
			T object = this.defaultValue;
			int i = biMapPalette.getIndex(this.defaultValue);
			int[] is = new int[4096];

			for (int j = 0; j < 4096; j++) {
				T object2 = this.get(j);
				if (object2 != object) {
					object = object2;
					i = biMapPalette.getIndex(object2);
				}

				is[j] = i;
			}

			NbtList nbtList = new NbtList();
			biMapPalette.writeNbt(nbtList);
			nbt.put(paletteKey, nbtList);
			int k = Math.max(4, MathHelper.log2DeBruijn(nbtList.size()));
			PackedIntegerArray packedIntegerArray = new PackedIntegerArray(k, 4096);

			for (int l = 0; l < is.length; l++) {
				packedIntegerArray.set(l, is[l]);
			}

			nbt.putLongArray(dataKey, packedIntegerArray.getStorage());
		} finally {
			this.unlock();
		}
	}

	public int getPacketSize() {
		return 1 + this.palette.getPacketSize() + PacketByteBuf.getVarIntLength(this.data.getSize()) + this.data.getStorage().length * 8;
	}

	public boolean hasAny(Predicate<T> predicate) {
		return this.palette.accepts(predicate);
	}

	public void count(PalettedContainer.CountConsumer<T> consumer) {
		Int2IntMap int2IntMap = new Int2IntOpenHashMap();
		this.data.forEach(i -> int2IntMap.put(i, int2IntMap.get(i) + 1));
		int2IntMap.int2IntEntrySet().forEach(entry -> consumer.accept(this.palette.getByIndex(entry.getIntKey()), entry.getIntValue()));
	}

	@FunctionalInterface
	public interface CountConsumer<T> {
		void accept(T object, int count);
	}
}
