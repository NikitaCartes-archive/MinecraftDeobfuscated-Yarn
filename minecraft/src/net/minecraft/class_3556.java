package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;

public abstract class class_3556<M extends class_3556<M>> {
	private final long[] field_15789 = new long[2];
	private final class_2804[] field_15790 = new class_2804[2];
	private boolean field_16447;
	protected final Long2ObjectOpenHashMap<class_2804> field_15791;

	protected class_3556(Long2ObjectOpenHashMap<class_2804> long2ObjectOpenHashMap) {
		this.field_15791 = long2ObjectOpenHashMap;
		this.method_15505();
		this.field_16447 = true;
	}

	public abstract M method_15504();

	public void method_15502(long l) {
		this.field_15791.put(l, this.field_15791.get(l).method_12144());
		this.method_15505();
	}

	public boolean method_15503(long l) {
		return this.field_15791.containsKey(l);
	}

	@Nullable
	public class_2804 method_15501(long l) {
		if (this.field_16447) {
			for (int i = 0; i < 2; i++) {
				if (l == this.field_15789[i]) {
					return this.field_15790[i];
				}
			}
		}

		class_2804 lv = this.field_15791.get(l);
		if (lv == null) {
			return null;
		} else {
			if (this.field_16447) {
				for (int j = 1; j > 0; j--) {
					this.field_15789[j] = this.field_15789[j - 1];
					this.field_15790[j] = this.field_15790[j - 1];
				}

				this.field_15789[0] = l;
				this.field_15790[0] = lv;
			}

			return lv;
		}
	}

	@Nullable
	public class_2804 method_15500(long l) {
		return this.field_15791.remove(l);
	}

	public void method_15499(long l, class_2804 arg) {
		this.field_15791.put(l, arg);
	}

	public void method_15505() {
		for (int i = 0; i < 2; i++) {
			this.field_15789[i] = Long.MAX_VALUE;
			this.field_15790[i] = null;
		}
	}

	public void method_16188() {
		this.field_16447 = false;
	}
}
