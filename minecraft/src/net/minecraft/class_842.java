package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_842 implements Comparable<class_842> {
	private final class_851 field_4418;
	private final ReentrantLock field_4412 = new ReentrantLock();
	private final List<Runnable> field_4411 = Lists.<Runnable>newArrayList();
	private final class_842.class_844 field_4416;
	private final double field_4419;
	@Nullable
	private class_853 field_4414;
	private class_750 field_4415;
	private class_849 field_4417;
	private class_842.class_843 field_4413 = class_842.class_843.field_4422;
	private boolean field_4410;

	public class_842(class_851 arg, class_842.class_844 arg2, double d, @Nullable class_853 arg3) {
		this.field_4418 = arg;
		this.field_4416 = arg2;
		this.field_4419 = d;
		this.field_4414 = arg3;
	}

	public class_842.class_843 method_3599() {
		return this.field_4413;
	}

	public class_851 method_3608() {
		return this.field_4418;
	}

	@Nullable
	public class_853 method_3606() {
		class_853 lv = this.field_4414;
		this.field_4414 = null;
		return lv;
	}

	public class_849 method_3609() {
		return this.field_4417;
	}

	public void method_3598(class_849 arg) {
		this.field_4417 = arg;
	}

	public class_750 method_3600() {
		return this.field_4415;
	}

	public void method_3603(class_750 arg) {
		this.field_4415 = arg;
	}

	public void method_3607(class_842.class_843 arg) {
		this.field_4412.lock();

		try {
			this.field_4413 = arg;
		} finally {
			this.field_4412.unlock();
		}
	}

	public void method_3596() {
		this.field_4412.lock();

		try {
			this.field_4414 = null;
			if (this.field_4416 == class_842.class_844.field_4426 && this.field_4413 != class_842.class_843.field_4423) {
				this.field_4418.method_3654(false);
			}

			this.field_4410 = true;
			this.field_4413 = class_842.class_843.field_4423;

			for (Runnable runnable : this.field_4411) {
				runnable.run();
			}
		} finally {
			this.field_4412.unlock();
		}
	}

	public void method_3597(Runnable runnable) {
		this.field_4412.lock();

		try {
			this.field_4411.add(runnable);
			if (this.field_4410) {
				runnable.run();
			}
		} finally {
			this.field_4412.unlock();
		}
	}

	public ReentrantLock method_3605() {
		return this.field_4412;
	}

	public class_842.class_844 method_3604() {
		return this.field_4416;
	}

	public boolean method_3595() {
		return this.field_4410;
	}

	public int method_3601(class_842 arg) {
		return Doubles.compare(this.field_4419, arg.field_4419);
	}

	public double method_3602() {
		return this.field_4419;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_843 {
		field_4422,
		field_4424,
		field_4421,
		field_4423;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_844 {
		field_4426,
		field_4427;
	}
}
