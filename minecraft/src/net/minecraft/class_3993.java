package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_3993<T extends class_1308> extends class_1352 {
	private final T field_17755;
	private final class_1799 field_17756;
	private final Predicate<? super T> field_17757;
	private final class_3414 field_18280;

	public class_3993(T arg, class_1799 arg2, @Nullable class_3414 arg3, Predicate<? super T> predicate) {
		this.field_17755 = arg;
		this.field_17756 = arg2;
		this.field_18280 = arg3;
		this.field_17757 = predicate;
	}

	@Override
	public boolean method_6264() {
		return this.field_17757.test(this.field_17755);
	}

	@Override
	public boolean method_6266() {
		return this.field_17755.method_6115();
	}

	@Override
	public void method_6269() {
		this.field_17755.method_5673(class_1304.field_6173, this.field_17756.method_7972());
		this.field_17755.method_6019(class_1268.field_5808);
	}

	@Override
	public void method_6270() {
		this.field_17755.method_5673(class_1304.field_6173, class_1799.field_8037);
		if (this.field_18280 != null) {
			this.field_17755.method_5783(this.field_18280, 1.0F, this.field_17755.method_6051().nextFloat() * 0.2F + 0.9F);
		}
	}
}
