package net.minecraft.util.palette;

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
	private final IdList<T> field_12938;
	private final Function<CompoundTag, T> elementDeserializer;
	private final Function<T, CompoundTag> elementSerializer;
	private final T field_12935;
	protected PackedIntegerArray field_12941;
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
			CrashReportSection crashReportSection = crashReport.method_562("Thread dumps");
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
		this.field_12938 = idList;
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
				this.palette = new ArrayPalette<>(this.field_12938, this.paletteSize, this, this.elementDeserializer);
			} else if (this.paletteSize < 9) {
				this.palette = new BiMapPalette<>(this.field_12938, this.paletteSize, this, this.elementDeserializer, this.elementSerializer);
			} else {
				this.palette = this.fallbackPalette;
				this.paletteSize = MathHelper.log2DeBrujin(this.field_12938.size());
			}

			this.palette.getIndex(this.field_12935);
			this.field_12941 = new PackedIntegerArray(this.paletteSize, 4096);
		}
	}

	@Override
	public int onResize(int i, T object) {
		this.lock();
		PackedIntegerArray packedIntegerArray = this.field_12941;
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
		int k = this.field_12941.setAndGetOldValue(i, j);
		T object2 = this.palette.getByIndex(k);
		return object2 == null ? this.field_12935 : object2;
	}

	protected void set(int i, T object) {
		int j = this.palette.getIndex(object);
		this.field_12941.set(i, j);
	}

	public T get(int i, int j, int k) {
		return this.get(toIndex(i, j, k));
	}

	public T get(int i) {
		T object = this.palette.getByIndex(this.field_12941.get(i));
		return object == null ? this.field_12935 : object;
	}

	@Environment(EnvType.CLIENT)
	public void method_12326(PacketByteBuf packetByteBuf) {
		this.lock();
		int i = packetByteBuf.readByte();
		if (this.paletteSize != i) {
			this.setPaletteSize(i);
		}

		this.palette.method_12289(packetByteBuf);
		packetByteBuf.readLongArray(this.field_12941.getStorage());
		this.unlock();
	}

	public void method_12325(PacketByteBuf packetByteBuf) {
		this.lock();
		packetByteBuf.writeByte(this.paletteSize);
		this.palette.method_12287(packetByteBuf);
		packetByteBuf.writeLongArray(this.field_12941.getStorage());
		this.unlock();
	}

	public void method_12329(ListTag listTag, long[] ls) {
		this.lock();
		int i = Math.max(4, MathHelper.log2DeBrujin(listTag.size()));
		if (i != this.paletteSize) {
			this.setPaletteSize(i);
		}

		this.palette.method_12286(listTag);
		int j = ls.length * 64 / 4096;
		if (this.palette == this.fallbackPalette) {
			Palette<T> palette = new BiMapPalette<>(this.field_12938, i, this.noOpPaletteResizeHandler, this.elementDeserializer, this.elementSerializer);
			palette.method_12286(listTag);
			PackedIntegerArray packedIntegerArray = new PackedIntegerArray(i, 4096, ls);

			for (int k = 0; k < 4096; k++) {
				this.field_12941.set(k, this.fallbackPalette.getIndex(palette.getByIndex(packedIntegerArray.get(k))));
			}
		} else if (j == this.paletteSize) {
			System.arraycopy(ls, 0, this.field_12941.getStorage(), 0, ls.length);
		} else {
			PackedIntegerArray packedIntegerArray2 = new PackedIntegerArray(j, 4096, ls);

			for (int l = 0; l < 4096; l++) {
				this.field_12941.set(l, packedIntegerArray2.get(l));
			}
		}

		this.unlock();
	}

	public void method_12330(CompoundTag compoundTag, String string, String string2) {
		this.lock();
		BiMapPalette<T> biMapPalette = new BiMapPalette<>(
			this.field_12938, this.paletteSize, this.noOpPaletteResizeHandler, this.elementDeserializer, this.elementSerializer
		);
		biMapPalette.getIndex(this.field_12935);
		int[] is = new int[4096];

		for (int i = 0; i < 4096; i++) {
			is[i] = biMapPalette.getIndex(this.get(i));
		}

		ListTag listTag = new ListTag();
		biMapPalette.method_12196(listTag);
		compoundTag.method_10566(string, listTag);
		int j = Math.max(4, MathHelper.log2DeBrujin(listTag.size()));
		PackedIntegerArray packedIntegerArray = new PackedIntegerArray(j, 4096);

		for (int k = 0; k < is.length; k++) {
			packedIntegerArray.set(k, is[k]);
		}

		compoundTag.putLongArray(string2, packedIntegerArray.getStorage());
		this.unlock();
	}

	public int getPacketSize() {
		return 1 + this.palette.getPacketSize() + PacketByteBuf.getVarIntSizeBytes(this.field_12941.getSize()) + this.field_12941.getStorage().length * 8;
	}
}
