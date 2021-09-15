package net.minecraft.world.chunk;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.class_6490;
import net.minecraft.class_6502;
import net.minecraft.class_6564;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.thread.AtomicStack;
import net.minecraft.util.thread.LockHelper;

public class PalettedContainer<T> implements PaletteResizeListener<T> {
	private static final int field_34557 = 0;
	private final PaletteResizeListener<T> field_34558 = (newSize, added) -> 0;
	private final IndexedIterable<T> field_34559;
	private volatile PalettedContainer.class_6561<T> field_34560;
	private final PalettedContainer.class_6563 field_34561;
	private final Semaphore field_34562 = new Semaphore(1);
	@Nullable
	private final AtomicStack<Pair<Thread, StackTraceElement[]>> field_34563 = null;

	public void lock() {
		if (this.field_34563 != null) {
			Thread thread = Thread.currentThread();
			this.field_34563.push(Pair.of(thread, thread.getStackTrace()));
		}

		LockHelper.checkLock(this.field_34562, this.field_34563, "PalettedContainer");
	}

	public void unlock() {
		this.field_34562.release();
	}

	public static <T> Codec<PalettedContainer<T>> method_38298(IndexedIterable<T> indexedIterable, Codec<T> codec, PalettedContainer.class_6563 arg) {
		return RecordCodecBuilder.create(
				instance -> instance.group(
							codec.listOf().fieldOf("palette").forGetter(PalettedContainer.class_6562::comp_75),
							Codec.LONG_STREAM.optionalFieldOf("data").forGetter(PalettedContainer.class_6562::comp_76)
						)
						.apply(instance, PalettedContainer.class_6562::new)
			)
			.comapFlatMap(arg2 -> read(indexedIterable, arg, arg2), palettedContainer -> palettedContainer.write(indexedIterable, arg));
	}

	public PalettedContainer(
		IndexedIterable<T> indexedIterable, PalettedContainer.class_6563 arg, PalettedContainer.class_6560<T> arg2, class_6490 arg3, List<T> list
	) {
		this.field_34559 = indexedIterable;
		this.field_34561 = arg;
		Palette<T> palette = arg2.comp_72().create(arg2.comp_73(), indexedIterable, this);
		list.forEach(palette::getIndex);
		this.field_34560 = new PalettedContainer.class_6561(arg2, arg3, palette);
	}

	public PalettedContainer(IndexedIterable<T> indexedIterable, T object, PalettedContainer.class_6563 arg) {
		this.field_34561 = arg;
		this.field_34559 = indexedIterable;
		this.field_34560 = this.method_38297(null, 0);
		this.field_34560.field_34565.getIndex(object);
	}

	private PalettedContainer.class_6561<T> method_38297(@Nullable PalettedContainer.class_6561<T> arg, int i) {
		PalettedContainer.class_6560<T> lv = this.field_34561.method_38314(this.field_34559, i);
		return arg != null && lv.equals(arg.comp_74()) ? arg : lv.method_38305(this.field_34559, this, this.field_34561.method_38312(), null);
	}

	@Override
	public int onResize(int i, T object) {
		PalettedContainer.class_6561<T> lv = this.field_34560;
		PalettedContainer.class_6561<T> lv2 = this.method_38297(lv, i);
		lv2.method_38308(lv.field_34565, lv.field_34564);
		this.field_34560 = lv2;
		return lv2.field_34565.getIndex(object);
	}

	public T setSync(int x, int y, int z, T value) {
		this.lock();

		Object var5;
		try {
			var5 = this.setAndGetOldValue(this.field_34561.method_38313(x, y, z), value);
		} finally {
			this.unlock();
		}

		return (T)var5;
	}

	public T set(int x, int y, int z, T value) {
		return this.setAndGetOldValue(this.field_34561.method_38313(x, y, z), value);
	}

	private T setAndGetOldValue(int index, T value) {
		int i = this.field_34560.field_34565.getIndex(value);
		int j = this.field_34560.field_34564.setAndGetOldValue(index, i);
		return this.field_34560.field_34565.getByIndex(j);
	}

	public void method_35321(int i, int j, int k, T object) {
		this.lock();

		try {
			this.set(this.field_34561.method_38313(i, j, k), object);
		} finally {
			this.unlock();
		}
	}

	private void set(int index, T object) {
		this.field_34560.method_38307(index, object);
	}

	public T get(int x, int y, int z) {
		return this.get(this.field_34561.method_38313(x, y, z));
	}

	protected T get(int index) {
		PalettedContainer.class_6561<T> lv = this.field_34560;
		return lv.field_34565.getByIndex(lv.field_34564.get(index));
	}

	public void fromPacket(PacketByteBuf buf) {
		this.lock();

		try {
			int i = buf.readByte();
			PalettedContainer.class_6561<T> lv = this.method_38297(this.field_34560, i);
			lv.field_34565.fromPacket(buf);
			buf.readLongArray(lv.field_34564.getStorage());
			this.field_34560 = lv;
		} finally {
			this.unlock();
		}
	}

	public void toPacket(PacketByteBuf packetByteBuf) {
		this.lock();

		try {
			this.field_34560.method_38309(packetByteBuf);
		} finally {
			this.unlock();
		}
	}

	private static <T> DataResult<PalettedContainer<T>> read(
		IndexedIterable<T> indexedIterable, PalettedContainer.class_6563 arg, PalettedContainer.class_6562<T> arg2
	) {
		List<T> list = arg2.comp_75();
		int i = arg.method_38312();
		int j = arg.method_38315(indexedIterable, list.size());
		PalettedContainer.class_6560<T> lv = arg.method_38314(indexedIterable, j);
		class_6490 lv2;
		if (j == 0) {
			lv2 = new class_6502(i);
		} else {
			Optional<LongStream> optional = arg2.comp_76();
			if (optional.isEmpty()) {
				return DataResult.error("Missing values for non-zero storage");
			}

			long[] ls = ((LongStream)optional.get()).toArray();
			if (lv.comp_72() == PalettedContainer.class_6563.field_34571) {
				Palette<T> palette = new BiMapPalette<>(indexedIterable, j, (ix, object) -> 0, list);
				PackedIntegerArray packedIntegerArray = new PackedIntegerArray(j, arg.method_38312(), ls);
				IntStream intStream = IntStream.range(0, packedIntegerArray.getSize()).map(ix -> indexedIterable.getRawId(palette.getByIndex(packedIntegerArray.get(ix))));
				lv2 = new PackedIntegerArray(lv.comp_73(), i, intStream);
			} else {
				lv2 = new PackedIntegerArray(lv.comp_73(), i, ls);
			}
		}

		return DataResult.success(new PalettedContainer<>(indexedIterable, arg, lv, lv2, list));
	}

	private PalettedContainer.class_6562<T> write(IndexedIterable<T> indexedIterable, PalettedContainer.class_6563 arg) {
		this.lock();

		PalettedContainer.class_6562 var17;
		try {
			BiMapPalette<T> biMapPalette = new BiMapPalette<>(indexedIterable, this.field_34560.field_34564.getElementBits(), this.field_34558);
			T object = null;
			int i = -1;
			int j = arg.method_38312();
			int[] is = new int[j];

			for (int k = 0; k < j; k++) {
				T object2 = this.get(k);
				if (object2 != object) {
					object = object2;
					i = biMapPalette.getIndex(object2);
				}

				is[k] = i;
			}

			int k = arg.method_38315(indexedIterable, biMapPalette.getIndexBits());
			Optional<LongStream> optional;
			if (k == 0) {
				optional = Optional.empty();
			} else {
				class_6490 lv = new PackedIntegerArray(k, j);

				for (int l = 0; l < is.length; l++) {
					lv.set(l, is[l]);
				}

				long[] ls = lv.getStorage();
				optional = Optional.of(Arrays.stream(ls));
			}

			var17 = new PalettedContainer.class_6562(biMapPalette.method_38288(), optional);
		} finally {
			this.unlock();
		}

		return var17;
	}

	public int getPacketSize() {
		return this.field_34560.method_38306();
	}

	public boolean hasAny(Predicate<T> predicate) {
		return this.field_34560.field_34565.accepts(predicate);
	}

	public void count(PalettedContainer.CountConsumer<T> consumer) {
		Int2IntMap int2IntMap = new Int2IntOpenHashMap();
		this.field_34560.field_34564.forEach(i -> int2IntMap.put(i, int2IntMap.get(i) + 1));
		int2IntMap.int2IntEntrySet().forEach(entry -> consumer.accept(this.field_34560.field_34565.getByIndex(entry.getIntKey()), entry.getIntValue()));
	}

	@FunctionalInterface
	public interface CountConsumer<T> {
		void accept(T object, int count);
	}

	static record class_6560<T>() {
		private final Palette.class_6559 comp_72;
		private final int comp_73;

		class_6560(Palette.class_6559 arg, int i) {
			this.comp_72 = arg;
			this.comp_73 = i;
		}

		public PalettedContainer.class_6561<T> method_38305(
			IndexedIterable<T> indexedIterable, PaletteResizeListener<T> paletteResizeListener, int i, @Nullable long[] ls
		) {
			class_6490 lv = (class_6490)(this.comp_73 == 0 ? new class_6502(i) : new PackedIntegerArray(this.comp_73, i, ls));
			Palette<T> palette = this.comp_72.create(this.comp_73, indexedIterable, paletteResizeListener);
			return new PalettedContainer.class_6561(this, lv, palette);
		}
	}

	static record class_6561() {
		private final PalettedContainer.class_6560<T> comp_74;
		final class_6490 field_34564;
		final Palette<T> field_34565;

		class_6561(PalettedContainer.class_6560<T> arg, class_6490 arg2, Palette<T> palette) {
			this.comp_74 = arg;
			this.field_34564 = arg2;
			this.field_34565 = palette;
		}

		public void method_38308(Palette<T> palette, class_6490 arg) {
			for (int i = 0; i < arg.getSize(); i++) {
				this.method_38307(i, palette.getByIndex(arg.get(i)));
			}
		}

		public void method_38307(int i, T object) {
			this.field_34564.set(i, this.field_34565.getIndex(object));
		}

		public int method_38306() {
			return 1 + this.field_34565.getPacketSize() + PacketByteBuf.getVarIntLength(this.field_34564.getSize()) + this.field_34564.getStorage().length * 8;
		}

		public void method_38309(PacketByteBuf packetByteBuf) {
			packetByteBuf.writeByte(this.field_34564.getElementBits());
			this.field_34565.toPacket(packetByteBuf);
			packetByteBuf.writeLongArray(this.field_34564.getStorage());
		}

		public class_6490 method_38310() {
			return this.field_34564;
		}

		public Palette<T> method_38311() {
			return this.field_34565;
		}
	}

	static record class_6562() {
		private final List<T> comp_75;
		private final Optional<LongStream> comp_76;

		class_6562(List<T> list, Optional<LongStream> optional) {
			this.comp_75 = list;
			this.comp_76 = optional;
		}
	}

	public abstract static class class_6563 {
		public static final Palette.class_6559 field_34566 = class_6564::method_38316;
		public static final Palette.class_6559 field_34567 = ArrayPalette::method_38295;
		public static final Palette.class_6559 field_34568 = BiMapPalette::method_38287;
		static final Palette.class_6559 field_34571 = IdListPalette::method_38286;
		public static final PalettedContainer.class_6563 field_34569 = new PalettedContainer.class_6563(4) {
			@Override
			public <A> PalettedContainer.class_6560<A> method_38314(IndexedIterable<A> indexedIterable, int i) {
				return switch (i) {
					case 0 -> new PalettedContainer.class_6560(field_34566, i);
					case 1, 2, 3, 4 -> new PalettedContainer.class_6560(field_34567, 4);
					case 5, 6, 7, 8 -> new PalettedContainer.class_6560(field_34568, i);
					default -> new PalettedContainer.class_6560(PalettedContainer.class_6563.field_34571, MathHelper.log2DeBruijn(indexedIterable.size()));
				};
			}
		};
		public static final PalettedContainer.class_6563 field_34570 = new PalettedContainer.class_6563(2) {
			@Override
			public <A> PalettedContainer.class_6560<A> method_38314(IndexedIterable<A> indexedIterable, int i) {
				return switch (i) {
					case 0 -> new PalettedContainer.class_6560(field_34566, i);
					case 1, 2 -> new PalettedContainer.class_6560(field_34567, i);
					default -> new PalettedContainer.class_6560(PalettedContainer.class_6563.field_34571, MathHelper.log2DeBruijn(indexedIterable.size()));
				};
			}
		};
		private final int field_34572;

		class_6563(int i) {
			this.field_34572 = i;
		}

		public int method_38312() {
			return 1 << this.field_34572 * 3;
		}

		public int method_38313(int i, int j, int k) {
			return (j << this.field_34572 | k) << this.field_34572 | i;
		}

		public abstract <A> PalettedContainer.class_6560<A> method_38314(IndexedIterable<A> indexedIterable, int i);

		<A> int method_38315(IndexedIterable<A> indexedIterable, int i) {
			int j = MathHelper.log2DeBruijn(i);
			PalettedContainer.class_6560<A> lv = this.method_38314(indexedIterable, j);
			return lv.comp_72() == field_34571 ? j : lv.comp_73();
		}
	}
}
