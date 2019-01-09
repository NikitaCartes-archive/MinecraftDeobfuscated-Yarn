package net.minecraft;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface class_1936 extends class_1941, class_3747, class_1943 {
	long method_8412();

	default float method_8391() {
		return class_2869.field_13059[this.method_8597().method_12454(this.method_8401().method_217())];
	}

	default float method_8400(float f) {
		return this.method_8597().method_12464(this.method_8401().method_217(), f);
	}

	@Environment(EnvType.CLIENT)
	default int method_8394() {
		return this.method_8597().method_12454(this.method_8401().method_217());
	}

	class_1951<class_2248> method_8397();

	class_1951<class_3611> method_8405();

	default <T extends class_1297> List<T> method_8403(Class<? extends T> class_, class_238 arg) {
		return this.method_8390(class_, arg, class_1301.field_6155);
	}

	<T extends class_1297> List<T> method_8390(Class<? extends T> class_, class_238 arg, @Nullable Predicate<? super T> predicate);

	class_1937 method_8410();

	class_31 method_8401();

	class_1266 method_8404(class_2338 arg);

	default class_1267 method_8407() {
		return this.method_8401().method_207();
	}

	class_2802 method_8398();

	@Override
	default boolean method_8393(int i, int j) {
		return this.method_8398().method_12123(i, j);
	}

	class_30 method_8411();

	Random method_8409();

	void method_8408(class_2338 arg, class_2248 arg2);

	class_2338 method_8395();

	void method_8396(@Nullable class_1657 arg, class_2338 arg2, class_3414 arg3, class_3419 arg4, float f, float g);

	void method_8406(class_2394 arg, double d, double e, double f, double g, double h, double i);
}
