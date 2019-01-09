package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1534 extends class_1530 {
	public class_1535 field_7134;

	public class_1534(class_1937 arg) {
		super(class_1299.field_6120, arg);
	}

	public class_1534(class_1937 arg, class_2338 arg2, class_2350 arg3) {
		super(class_1299.field_6120, arg, arg2);
		List<class_1535> list = Lists.<class_1535>newArrayList();
		int i = 0;

		for (class_1535 lv : class_2378.field_11150) {
			this.field_7134 = lv;
			this.method_6892(arg3);
			if (this.method_6888()) {
				list.add(lv);
				int j = lv.method_6945() * lv.method_6946();
				if (j > i) {
					i = j;
				}
			}
		}

		if (!list.isEmpty()) {
			Iterator<class_1535> iterator = list.iterator();

			while (iterator.hasNext()) {
				class_1535 lvx = (class_1535)iterator.next();
				if (lvx.method_6945() * lvx.method_6946() < i) {
					iterator.remove();
				}
			}

			this.field_7134 = (class_1535)list.get(this.field_5974.nextInt(list.size()));
		}

		this.method_6892(arg3);
	}

	@Environment(EnvType.CLIENT)
	public class_1534(class_1937 arg, class_2338 arg2, class_2350 arg3, class_1535 arg4) {
		this(arg, arg2, arg3);
		this.field_7134 = arg4;
		this.method_6892(arg3);
	}

	@Override
	public void method_5652(class_2487 arg) {
		arg.method_10582("Motive", class_2378.field_11150.method_10221(this.field_7134).toString());
		super.method_5652(arg);
	}

	@Override
	public void method_5749(class_2487 arg) {
		this.field_7134 = class_2378.field_11150.method_10223(class_2960.method_12829(arg.method_10558("Motive")));
		super.method_5749(arg);
	}

	@Override
	public int method_6897() {
		return this.field_7134.method_6945();
	}

	@Override
	public int method_6891() {
		return this.field_7134.method_6946();
	}

	@Override
	public void method_6889(@Nullable class_1297 arg) {
		if (this.field_6002.method_8450().method_8355("doEntityDrops")) {
			this.method_5783(class_3417.field_14809, 1.0F, 1.0F);
			if (arg instanceof class_1657) {
				class_1657 lv = (class_1657)arg;
				if (lv.field_7503.field_7477) {
					return;
				}
			}

			this.method_5706(class_1802.field_8892);
		}
	}

	@Override
	public void method_6894() {
		this.method_5783(class_3417.field_14875, 1.0F, 1.0F);
	}

	@Override
	public void method_5808(double d, double e, double f, float g, float h) {
		this.method_5814(d, e, f);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5759(double d, double e, double f, float g, float h, int i, boolean bl) {
		class_2338 lv = this.field_7100.method_10080(d - this.field_5987, e - this.field_6010, f - this.field_6035);
		this.method_5814((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260());
	}
}
