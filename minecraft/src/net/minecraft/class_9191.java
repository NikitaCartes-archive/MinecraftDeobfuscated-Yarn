package net.minecraft;

public class class_9191 extends class_9187 implements class_9194 {
	public static final int field_48813 = 240;
	private final long[][] field_48814;
	private int field_48815;
	private int field_48816;

	public class_9191(int i) {
		this(i, new long[i]);
	}

	public class_9191(int i, long[] ls) {
		super(i, ls);
		this.field_48814 = new long[240][i];
	}

	@Override
	protected void method_56649() {
		int i = this.method_56661(this.field_48815 + this.field_48816);
		System.arraycopy(this.array, 0, this.field_48814[i], 0, this.array.length);
		if (this.field_48816 < 240) {
			this.field_48816++;
		} else {
			this.field_48815 = this.method_56661(this.field_48815 + 1);
		}
	}

	@Override
	public int method_56662() {
		return this.field_48814.length;
	}

	@Override
	public int method_56663() {
		return this.field_48816;
	}

	@Override
	public long method_56659(int i) {
		return this.method_56660(i, 0);
	}

	@Override
	public long method_56660(int i, int j) {
		if (i >= 0 && i < this.field_48816) {
			long[] ls = this.field_48814[this.method_56661(this.field_48815 + i)];
			if (j >= 0 && j < ls.length) {
				return ls[j];
			} else {
				throw new IndexOutOfBoundsException(j + " out of bounds for dimensions " + ls.length);
			}
		} else {
			throw new IndexOutOfBoundsException(i + " out of bounds for length " + this.field_48816);
		}
	}

	private int method_56661(int i) {
		return i % 240;
	}

	@Override
	public void method_56664() {
		this.field_48815 = 0;
		this.field_48816 = 0;
	}
}
