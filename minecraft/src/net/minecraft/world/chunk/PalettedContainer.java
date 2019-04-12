package net.minecraft.world.chunk;

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
	private final PaletteResizeListener<T> noOpPaletteResizeHandler = (i, objectx) -> 0;
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

	public PalettedContainer(Palette<T> palette, IdList<T> idList, Function<CompoundTag, T> function, Function<T, CompoundTag> function2, T object) {
		this.fallbackPalette = palette;
		this.idList = idList;
		this.elementDeserializer = function;
		this.elementSerializer = function2;
		this.field_12935 = object;
		this.setPaletteSize(4);
	}

	private static int toIndex(int i, int j, int k) {
		return j << 8 | k << 4 | i;
	}

	private void setPaletteSize(int i) {
		if (i != this.paletteSize) {
			this.paletteSize = i;
			if (this.paletteSize <= 4) {
				this.paletteSize = 4;
				this.palette = new ArrayPalette<>(this.idList, this.paletteSize, this, this.elementDeserializer);
			} else if (this.paletteSize < 9) {
				this.palette = new BiMapPalette<>(this.idList, this.paletteSize, this, this.elementDeserializer, this.elementSerializer);
			} else {
				this.palette = this.fallbackPalette;
				this.paletteSize = MathHelper.log2DeBrujin(this.idList.size());
			}

			this.palette.getIndex(this.field_12935);
			this.data = new PackedIntegerArray(this.paletteSize, 4096);
		}
	}

	@Override
	public int onResize(int i, T object) {
		this.lock();
		PackedIntegerArray packedIntegerArray = this.data;
		Palette<T> palette = this.palette;
		this.setPaletteSize(i);

		for (int j = 0; j < packedIntegerArray.getSize(); j++) {
			T object2 = palette.getByIndex(packedIntegerArray.get(j));
			if (object2 != null) {
				this.set(j, object2);
			}
		}

		int jx = this.palette.getIndex(object);
		this.unlock();
		return jx;
	}

	public T setSync(int i, int j, int k, T object) {
		this.lock();
		T object2 = this.setAndGetOldValue(toIndex(i, j, k), object);
		this.unlock();
		return object2;
	}

	public T set(int i, int j, int k, T object) {
		return this.setAndGetOldValue(toIndex(i, j, k), object);
	}

	protected T setAndGetOldValue(int i, T object) {
		int j = this.palette.getIndex(object);
		int k = this.data.setAndGetOldValue(i, j);
		T object2 = this.palette.getByIndex(k);
		return object2 == null ? this.field_12935 : object2;
	}

	protected void set(int i, T object) {
		int j = this.palette.getIndex(object);
		this.data.set(i, j);
	}

	public T get(int i, int j, int k) {
		return this.get(toIndex(i, j, k));
	}

	protected T get(int i) {
		T object = this.palette.getByIndex(this.data.get(i));
		return object == null ? this.field_12935 : object;
	}

	@Environment(EnvType.CLIENT)
	public void fromPacket(PacketByteBuf packetByteBuf) {
		this.lock();
		int i = packetByteBuf.readByte();
		if (this.paletteSize != i) {
			this.setPaletteSize(i);
		}

		this.palette.fromPacket(packetByteBuf);
		packetByteBuf.readLongArray(this.data.getStorage());
		this.unlock();
	}

	public void toPacket(PacketByteBuf packetByteBuf) {
		this.lock();
		packetByteBuf.writeByte(this.paletteSize);
		this.palette.toPacket(packetByteBuf);
		packetByteBuf.writeLongArray(this.data.getStorage());
		this.unlock();
	}

	public void read(ListTag listTag, long[] ls) {
		this.lock();
		int i = Math.max(4, MathHelper.log2DeBrujin(listTag.size()));
		if (i != this.paletteSize) {
			this.setPaletteSize(i);
		}

		this.palette.fromTag(listTag);
		int j = ls.length * 64 / 4096;
		if (this.palette == this.fallbackPalette) {
			Palette<T> palette = new BiMapPalette<>(this.idList, i, this.noOpPaletteResizeHandler, this.elementDeserializer, this.elementSerializer);
			palette.fromTag(listTag);
			PackedIntegerArray packedIntegerArray = new PackedIntegerArray(i, 4096, ls);

			for (int k = 0; k < 4096; k++) {
				this.data.set(k, this.fallbackPalette.getIndex(palette.getByIndex(packedIntegerArray.get(k))));
			}
		} else if (j == this.paletteSize) {
			System.arraycopy(ls, 0, this.data.getStorage(), 0, ls.length);
		} else {
			PackedIntegerArray packedIntegerArray2 = new PackedIntegerArray(j, 4096, ls);

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
		int j = Math.max(4, MathHelper.log2DeBrujin(listTag.size()));
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
}
