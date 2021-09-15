package net.minecraft.util.collection;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.class_6490;
import net.minecraft.util.Util;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.mutable.MutableInt;

public class PackedIntegerArray implements class_6490 {
	/**
	 * Magic constants for faster integer division by a constant.
	 * 
	 * <p>This is computed as {@code (n * scale + offset) >> (32 + shift)}. For a divisor n,
	 * the constants are stored as such:
	 * 
	 * <ul>
	 * <li>scale at 3 * (n - 1)</li>
	 * <li>offset at 3 * (n - 1) + 1</li>
	 * <li>shift at 3 * (n - 1) + 2</li>
	 * </ul>
	 */
	private static final int[] INDEX_PARAMETERS = new int[]{
		-1,
		-1,
		0,
		Integer.MIN_VALUE,
		0,
		0,
		1431655765,
		1431655765,
		0,
		Integer.MIN_VALUE,
		0,
		1,
		858993459,
		858993459,
		0,
		715827882,
		715827882,
		0,
		613566756,
		613566756,
		0,
		Integer.MIN_VALUE,
		0,
		2,
		477218588,
		477218588,
		0,
		429496729,
		429496729,
		0,
		390451572,
		390451572,
		0,
		357913941,
		357913941,
		0,
		330382099,
		330382099,
		0,
		306783378,
		306783378,
		0,
		286331153,
		286331153,
		0,
		Integer.MIN_VALUE,
		0,
		3,
		252645135,
		252645135,
		0,
		238609294,
		238609294,
		0,
		226050910,
		226050910,
		0,
		214748364,
		214748364,
		0,
		204522252,
		204522252,
		0,
		195225786,
		195225786,
		0,
		186737708,
		186737708,
		0,
		178956970,
		178956970,
		0,
		171798691,
		171798691,
		0,
		165191049,
		165191049,
		0,
		159072862,
		159072862,
		0,
		153391689,
		153391689,
		0,
		148102320,
		148102320,
		0,
		143165576,
		143165576,
		0,
		138547332,
		138547332,
		0,
		Integer.MIN_VALUE,
		0,
		4,
		130150524,
		130150524,
		0,
		126322567,
		126322567,
		0,
		122713351,
		122713351,
		0,
		119304647,
		119304647,
		0,
		116080197,
		116080197,
		0,
		113025455,
		113025455,
		0,
		110127366,
		110127366,
		0,
		107374182,
		107374182,
		0,
		104755299,
		104755299,
		0,
		102261126,
		102261126,
		0,
		99882960,
		99882960,
		0,
		97612893,
		97612893,
		0,
		95443717,
		95443717,
		0,
		93368854,
		93368854,
		0,
		91382282,
		91382282,
		0,
		89478485,
		89478485,
		0,
		87652393,
		87652393,
		0,
		85899345,
		85899345,
		0,
		84215045,
		84215045,
		0,
		82595524,
		82595524,
		0,
		81037118,
		81037118,
		0,
		79536431,
		79536431,
		0,
		78090314,
		78090314,
		0,
		76695844,
		76695844,
		0,
		75350303,
		75350303,
		0,
		74051160,
		74051160,
		0,
		72796055,
		72796055,
		0,
		71582788,
		71582788,
		0,
		70409299,
		70409299,
		0,
		69273666,
		69273666,
		0,
		68174084,
		68174084,
		0,
		Integer.MIN_VALUE,
		0,
		5
	};
	private final long[] storage;
	private final int elementBits;
	private final long maxValue;
	private final int size;
	private final int elementsPerLong;
	private final int indexScale;
	private final int indexOffset;
	private final int indexShift;

	public PackedIntegerArray(int i, int j, IntStream intStream) {
		this(i, j);
		MutableInt mutableInt = new MutableInt();
		intStream.forEach(ix -> this.set(mutableInt.getAndIncrement(), ix));
	}

	public PackedIntegerArray(int elementBits, int size) {
		this(elementBits, size, (long[])null);
	}

	public PackedIntegerArray(int elementBits, int size, @Nullable long[] storage) {
		Validate.inclusiveBetween(1L, 32L, (long)elementBits);
		this.size = size;
		this.elementBits = elementBits;
		this.maxValue = (1L << elementBits) - 1L;
		this.elementsPerLong = (char)(64 / elementBits);
		int i = 3 * (this.elementsPerLong - 1);
		this.indexScale = INDEX_PARAMETERS[i + 0];
		this.indexOffset = INDEX_PARAMETERS[i + 1];
		this.indexShift = INDEX_PARAMETERS[i + 2];
		int j = (size + this.elementsPerLong - 1) / this.elementsPerLong;
		if (storage != null) {
			if (storage.length != j) {
				throw (RuntimeException)Util.throwOrPause(new RuntimeException("Invalid length given for storage, got: " + storage.length + " but expected: " + j));
			}

			this.storage = storage;
		} else {
			this.storage = new long[j];
		}
	}

	private int getStorageIndex(int index) {
		long l = Integer.toUnsignedLong(this.indexScale);
		long m = Integer.toUnsignedLong(this.indexOffset);
		return (int)((long)index * l + m >> 32 >> this.indexShift);
	}

	@Override
	public int setAndGetOldValue(int i, int j) {
		Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)i);
		Validate.inclusiveBetween(0L, this.maxValue, (long)j);
		int k = this.getStorageIndex(i);
		long l = this.storage[k];
		int m = (i - k * this.elementsPerLong) * this.elementBits;
		int n = (int)(l >> m & this.maxValue);
		this.storage[k] = l & ~(this.maxValue << m) | ((long)j & this.maxValue) << m;
		return n;
	}

	@Override
	public void set(int i, int j) {
		Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)i);
		Validate.inclusiveBetween(0L, this.maxValue, (long)j);
		int k = this.getStorageIndex(i);
		long l = this.storage[k];
		int m = (i - k * this.elementsPerLong) * this.elementBits;
		this.storage[k] = l & ~(this.maxValue << m) | ((long)j & this.maxValue) << m;
	}

	@Override
	public int get(int i) {
		Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)i);
		int j = this.getStorageIndex(i);
		long l = this.storage[j];
		int k = (i - j * this.elementsPerLong) * this.elementBits;
		return (int)(l >> k & this.maxValue);
	}

	@Override
	public long[] getStorage() {
		return this.storage;
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public int getElementBits() {
		return this.elementBits;
	}

	@Override
	public void forEach(IntConsumer intConsumer) {
		int i = 0;

		for (long l : this.storage) {
			for (int j = 0; j < this.elementsPerLong; j++) {
				intConsumer.accept((int)(l & this.maxValue));
				l >>= this.elementBits;
				if (++i >= this.size) {
					return;
				}
			}
		}
	}
}
