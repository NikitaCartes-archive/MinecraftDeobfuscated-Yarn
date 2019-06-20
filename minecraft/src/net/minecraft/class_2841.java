package net.minecraft;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2841<T> implements class_2835<T> {
	private final class_2837<T> field_12940;
	private final class_2835<T> field_12942 = (i, objectx) -> 0;
	private final class_2361<T> field_12938;
	private final Function<class_2487, T> field_12943;
	private final Function<T, class_2487> field_12939;
	private final T field_12935;
	protected class_3508 field_12941;
	private class_2837<T> field_12936;
	private int field_12934;
	private final ReentrantLock field_12937 = new ReentrantLock();

	public void method_12334() {
		if (this.field_12937.isLocked() && !this.field_12937.isHeldByCurrentThread()) {
			String string = (String)Thread.getAllStackTraces()
				.keySet()
				.stream()
				.filter(Objects::nonNull)
				.map(thread -> thread.getName() + ": \n\tat " + (String)Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat ")))
				.collect(Collectors.joining("\n"));
			class_128 lv = new class_128("Writing into PalettedContainer from multiple threads", new IllegalStateException());
			class_129 lv2 = lv.method_562("Thread dumps");
			lv2.method_578("Thread dumps", string);
			throw new class_148(lv);
		} else {
			this.field_12937.lock();
		}
	}

	public void method_12335() {
		this.field_12937.unlock();
	}

	public class_2841(class_2837<T> arg, class_2361<T> arg2, Function<class_2487, T> function, Function<T, class_2487> function2, T object) {
		this.field_12940 = arg;
		this.field_12938 = arg2;
		this.field_12943 = function;
		this.field_12939 = function2;
		this.field_12935 = object;
		this.method_12324(4);
	}

	private static int method_12323(int i, int j, int k) {
		return j << 8 | k << 4 | i;
	}

	private void method_12324(int i) {
		if (i != this.field_12934) {
			this.field_12934 = i;
			if (this.field_12934 <= 4) {
				this.field_12934 = 4;
				this.field_12936 = new class_2834<>(this.field_12938, this.field_12934, this, this.field_12943);
			} else if (this.field_12934 < 9) {
				this.field_12936 = new class_2814<>(this.field_12938, this.field_12934, this, this.field_12943, this.field_12939);
			} else {
				this.field_12936 = this.field_12940;
				this.field_12934 = class_3532.method_15342(this.field_12938.method_10204());
			}

			this.field_12936.method_12291(this.field_12935);
			this.field_12941 = new class_3508(this.field_12934, 4096);
		}
	}

	@Override
	public int onResize(int i, T object) {
		this.method_12334();
		class_3508 lv = this.field_12941;
		class_2837<T> lv2 = this.field_12936;
		this.method_12324(i);

		for (int j = 0; j < lv.method_15215(); j++) {
			T object2 = lv2.method_12288(lv.method_15211(j));
			if (object2 != null) {
				this.method_12322(j, object2);
			}
		}

		int jx = this.field_12936.method_12291(object);
		this.method_12335();
		return jx;
	}

	public T method_12328(int i, int j, int k, T object) {
		this.method_12334();
		T object2 = this.method_12336(method_12323(i, j, k), object);
		this.method_12335();
		return object2;
	}

	public T method_16678(int i, int j, int k, T object) {
		return this.method_12336(method_12323(i, j, k), object);
	}

	protected T method_12336(int i, T object) {
		int j = this.field_12936.method_12291(object);
		int k = this.field_12941.method_15214(i, j);
		T object2 = this.field_12936.method_12288(k);
		return object2 == null ? this.field_12935 : object2;
	}

	protected void method_12322(int i, T object) {
		int j = this.field_12936.method_12291(object);
		this.field_12941.method_15210(i, j);
	}

	public T method_12321(int i, int j, int k) {
		return this.method_12331(method_12323(i, j, k));
	}

	protected T method_12331(int i) {
		T object = this.field_12936.method_12288(this.field_12941.method_15211(i));
		return object == null ? this.field_12935 : object;
	}

	@Environment(EnvType.CLIENT)
	public void method_12326(class_2540 arg) {
		this.method_12334();
		int i = arg.readByte();
		if (this.field_12934 != i) {
			this.method_12324(i);
		}

		this.field_12936.method_12289(arg);
		arg.method_10801(this.field_12941.method_15212());
		this.method_12335();
	}

	public void method_12325(class_2540 arg) {
		this.method_12334();
		arg.writeByte(this.field_12934);
		this.field_12936.method_12287(arg);
		arg.method_10789(this.field_12941.method_15212());
		this.method_12335();
	}

	public void method_12329(class_2499 arg, long[] ls) {
		this.method_12334();
		int i = Math.max(4, class_3532.method_15342(arg.size()));
		if (i != this.field_12934) {
			this.method_12324(i);
		}

		this.field_12936.method_12286(arg);
		int j = ls.length * 64 / 4096;
		if (this.field_12936 == this.field_12940) {
			class_2837<T> lv = new class_2814<>(this.field_12938, i, this.field_12942, this.field_12943, this.field_12939);
			lv.method_12286(arg);
			class_3508 lv2 = new class_3508(i, 4096, ls);

			for (int k = 0; k < 4096; k++) {
				this.field_12941.method_15210(k, this.field_12940.method_12291(lv.method_12288(lv2.method_15211(k))));
			}
		} else if (j == this.field_12934) {
			System.arraycopy(ls, 0, this.field_12941.method_15212(), 0, ls.length);
		} else {
			class_3508 lv3 = new class_3508(j, 4096, ls);

			for (int l = 0; l < 4096; l++) {
				this.field_12941.method_15210(l, lv3.method_15211(l));
			}
		}

		this.method_12335();
	}

	public void method_12330(class_2487 arg, String string, String string2) {
		this.method_12334();
		class_2814<T> lv = new class_2814<>(this.field_12938, this.field_12934, this.field_12942, this.field_12943, this.field_12939);
		lv.method_12291(this.field_12935);
		int[] is = new int[4096];

		for (int i = 0; i < 4096; i++) {
			is[i] = lv.method_12291(this.method_12331(i));
		}

		class_2499 lv2 = new class_2499();
		lv.method_12196(lv2);
		arg.method_10566(string, lv2);
		int j = Math.max(4, class_3532.method_15342(lv2.size()));
		class_3508 lv3 = new class_3508(j, 4096);

		for (int k = 0; k < is.length; k++) {
			lv3.method_15210(k, is[k]);
		}

		arg.method_10564(string2, lv3.method_15212());
		this.method_12335();
	}

	public int method_12327() {
		return 1 + this.field_12936.method_12290() + class_2540.method_10815(this.field_12941.method_15215()) + this.field_12941.method_15212().length * 8;
	}

	public boolean method_19526(T object) {
		return this.field_12936.method_19525(object);
	}
}
