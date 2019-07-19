package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.IdList;
import net.minecraft.util.PackedIntegerArray;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;

public class PalettedContainer<T> implements PaletteResizeListener<T> {
	private final Palette<T> fallbackPalette;
	private final PaletteResizeListener<T> noOpPaletteResizeHandler = (i, object) -> 0;
	private final IdList<T> idList;
	private final Function<CompoundTag, T> elementDeserializer;
	private final Function<T, CompoundTag> elementSerializer;
	private final T field_12935;
	protected PackedIntegerArray data;
	private Palette<T> palette;
	private int paletteSize;
	private final ReentrantLock writeLock = new ReentrantLock();

	public void lock() {
		if (this.writeLock.isLocked() && !this.writeLock.isHeldByCurrentThread()) {
			String string = (String)Thread.getAllStackTraces()
				.keySet()
				.stream()
				.filter(Objects::nonNull)
				.map(thread -> thread.getName() + ": \n\tat " + (String)Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat ")))
				.collect(Collectors.joining("\n"));
			CrashReport crashReport = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
			CrashReportSection crashReportSection = crashReport.addElement("Thread dumps");
			crashReportSection.add("Thread dumps", string);
			throw new CrashException(crashReport);
		} else {
			this.writeLock.lock();
		}
	}

	public void unlock() {
		this.writeLock.unlock();
	}

	public PalettedContainer(
		Palette<T> fallbackPalette, IdList<T> idList, Function<CompoundTag, T> elementDeserializer, Function<T, CompoundTag> elementSerializer, T defaultElement
	) {
		this.fallbackPalette = fallbackPalette;
		this.idList = idList;
		this.elementDeserializer = elementDeserializer;
		this.elementSerializer = elementSerializer;
		this.field_12935 = defaultElement;
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

			this.palette.getIndex(this.field_12935);
			this.data = new PackedIntegerArray(this.paletteSize, 4096);
		}
	}

	@Override
	public int onResize(int newSize, T objectAdded) {
		this.lock();
		PackedIntegerArray packedIntegerArray = this.data;
		Palette<T> palette = this.palette;
		this.setPaletteSize(newSize);

		for (int i = 0; i < packedIntegerArray.getSize(); i++) {
			T object = palette.getByIndex(packedIntegerArray.get(i));
			if (object != null) {
				this.set(i, object);
			}
		}

		int ix = this.palette.getIndex(objectAdded);
		this.unlock();
		return ix;
	}

	public T setSync(int x, int y, int z, T value) {
		this.lock();
		T object = this.setAndGetOldValue(toIndex(x, y, z), value);
		this.unlock();
		return object;
	}

	public T set(int x, int y, int z, T value) {
		return this.setAndGetOldValue(toIndex(x, y, z), value);
	}

	protected T setAndGetOldValue(int index, T value) {
		int i = this.palette.getIndex(value);
		int j = this.data.setAndGetOldValue(index, i);
		T object = this.palette.getByIndex(j);
		return object == null ? this.field_12935 : object;
	}

	protected void set(int i, T object) {
		int j = this.palette.getIndex(object);
		this.data.set(i, j);
	}

	public T get(int x, int y, int z) {
		return this.get(toIndex(x, y, z));
	}

	protected T get(int index) {
		T object = this.palette.getByIndex(this.data.get(index));
		return object == null ? this.field_12935 : object;
	}

	@Environment(EnvType.CLIENT)
	public void fromPacket(PacketByteBuf buf) {
		this.lock();
		int i = buf.readByte();
		if (this.paletteSize != i) {
			this.setPaletteSize(i);
		}

		this.palette.fromPacket(buf);
		buf.readLongArray(this.data.getStorage());
		this.unlock();
	}

	public void toPacket(PacketByteBuf buf) {
		this.lock();
		buf.writeByte(this.paletteSize);
		this.palette.toPacket(buf);
		buf.writeLongArray(this.data.getStorage());
		this.unlock();
	}

	public void read(ListTag paletteTag, long[] data) {
		this.lock();
		int i = Math.max(4, MathHelper.log2DeBruijn(paletteTag.size()));
		if (i != this.paletteSize) {
			this.setPaletteSize(i);
		}

		this.palette.fromTag(paletteTag);
		int j = data.length * 64 / 4096;
		if (this.palette == this.fallbackPalette) {
			Palette<T> palette = new BiMapPalette<>(this.idList, i, this.noOpPaletteResizeHandler, this.elementDeserializer, this.elementSerializer);
			palette.fromTag(paletteTag);
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

		this.unlock();
	}

	public void write(CompoundTag compoundTag, String string, String string2) {
		this.lock();
		BiMapPalette<T> biMapPalette = new BiMapPalette<>(
			this.idList, this.paletteSize, this.noOpPaletteResizeHandler, this.elementDeserializer, this.elementSerializer
		);
		biMapPalette.getIndex(this.field_12935);
		int[] is = new int[4096];

		for (int i = 0; i < 4096; i++) {
			is[i] = biMapPalette.getIndex(this.get(i));
		}

		ListTag listTag = new ListTag();
		biMapPalette.toTag(listTag);
		compoundTag.put(string, listTag);
		int j = Math.max(4, MathHelper.log2DeBruijn(listTag.size()));
		PackedIntegerArray packedIntegerArray = new PackedIntegerArray(j, 4096);

		for (int k = 0; k < is.length; k++) {
			packedIntegerArray.set(k, is[k]);
		}

		compoundTag.putLongArray(string2, packedIntegerArray.getStorage());
		this.unlock();
	}

	public int getPacketSize() {
		return 1 + this.palette.getPacketSize() + PacketByteBuf.getVarIntSizeBytes(this.data.getSize()) + this.data.getStorage().length * 8;
	}

	public boolean method_19526(T object) {
		return this.palette.accepts(object);
	}

	public void method_21732(PalettedContainer.class_4464<T> arg) {
		Int2IntMap int2IntMap = new Int2IntOpenHashMap();
		this.data.method_21739(i -> int2IntMap.put(i, int2IntMap.get(i) + 1));
		int2IntMap.int2IntEntrySet().forEach(entry -> arg.accept(this.palette.getByIndex(entry.getIntKey()), entry.getIntValue()));
	}

	@FunctionalInterface
	public interface class_4464<T> {
		void accept(T object, int i);
	}
}
