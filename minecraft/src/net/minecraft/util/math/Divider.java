package net.minecraft.util.math;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.NoSuchElementException;

/**
 * A class for dividing an integer into {@link #divisor} number of integers
 * such that the difference between any integers is {@code 0} or {@code 1}.
 * The resulting integers sum to the {@code dividend}.
 * For example, {@code new Divider(13, 5)} will produce {@code 2, 3, 2, 3, 3}.
 */
public class Divider implements IntIterator {
	private final int divisor;
	private final int quotient;
	private final int mod;
	private int returnedCount;
	private int remainder;

	public Divider(int dividend, int divisor) {
		this.divisor = divisor;
		if (divisor > 0) {
			this.quotient = dividend / divisor;
			this.mod = dividend % divisor;
		} else {
			this.quotient = 0;
			this.mod = 0;
		}
	}

	public boolean hasNext() {
		return this.returnedCount < this.divisor;
	}

	@Override
	public int nextInt() {
		if (!this.hasNext()) {
			throw new NoSuchElementException();
		} else {
			int i = this.quotient;
			this.remainder = this.remainder + this.mod;
			if (this.remainder >= this.divisor) {
				this.remainder = this.remainder - this.divisor;
				i++;
			}

			this.returnedCount++;
			return i;
		}
	}

	@VisibleForTesting
	public static Iterable<Integer> asIterable(int dividend, int divisor) {
		return () -> new Divider(dividend, divisor);
	}
}
