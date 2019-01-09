package net.minecraft;

import java.util.concurrent.Executor;
import net.minecraft.server.MinecraftServer;

public class class_3202 extends class_3218 {
	public class_3202(MinecraftServer minecraftServer, Executor executor, class_30 arg, class_2874 arg2, class_3218 arg3, class_3695 arg4, class_3949 arg5) {
		super(minecraftServer, executor, arg, arg3.method_8646(), new class_27(arg3.method_8401()), arg2, arg4, arg5);
		arg3.method_8621().method_11983(new class_2780() {
			@Override
			public void method_11934(class_2784 arg, double d) {
				class_3202.this.method_8621().method_11969(d);
			}

			@Override
			public void method_11931(class_2784 arg, double d, double e, long l) {
				class_3202.this.method_8621().method_11957(d, e, l);
			}

			@Override
			public void method_11930(class_2784 arg, double d, double e) {
				class_3202.this.method_8621().method_11978(d, e);
			}

			@Override
			public void method_11932(class_2784 arg, int i) {
				class_3202.this.method_8621().method_11975(i);
			}

			@Override
			public void method_11933(class_2784 arg, int i) {
				class_3202.this.method_8621().method_11967(i);
			}

			@Override
			public void method_11929(class_2784 arg, double d) {
				class_3202.this.method_8621().method_11955(d);
			}

			@Override
			public void method_11935(class_2784 arg, double d) {
				class_3202.this.method_8621().method_11981(d);
			}
		});
	}

	@Override
	protected void method_14188() {
	}

	@Override
	protected void method_8560() {
	}

	public class_3202 method_14033() {
		String string = class_3767.method_16533(this.field_9247);
		class_3767 lv = this.method_8648(this.field_9247.method_12460(), class_3767::new, string);
		if (lv == null) {
			this.field_16642 = new class_3767(this);
			this.method_8647(this.field_9247.method_12460(), string, this.field_16642);
		} else {
			this.field_16642 = lv;
			this.field_16642.method_16530(this);
		}

		String string2 = class_1418.method_6434(this.field_9247);
		class_1418 lv2 = this.method_8648(class_2874.field_13072, class_1418::new, string2);
		if (lv2 == null) {
			this.field_9254 = new class_1418(this);
			this.method_8647(class_2874.field_13072, string2, this.field_9254);
		} else {
			this.field_9254 = lv2;
			this.field_9254.method_6433(this);
		}

		this.field_9254.method_16471();
		return this;
	}

	public void method_14032() {
		this.field_9247.method_12450();
	}
}
