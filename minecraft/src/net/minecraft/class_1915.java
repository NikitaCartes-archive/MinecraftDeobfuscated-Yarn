package net.minecraft;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface class_1915 {
	void method_8259(@Nullable class_1657 arg);

	@Nullable
	class_1657 method_8257();

	class_1916 method_8264();

	@Environment(EnvType.CLIENT)
	void method_8261(@Nullable class_1916 arg);

	void method_8262(class_1914 arg);

	void method_8258(class_1799 arg);

	class_1937 method_8260();

	class_2338 method_8263();

	default void method_17449(class_1657 arg, class_2561 arg2) {
		OptionalInt optionalInt = arg.method_17355(new class_747((i, argx, arg2x) -> new class_1728(i, argx, this), arg2));
		if (optionalInt.isPresent()) {
			class_1916 lv = this.method_8264();
			if (!lv.isEmpty()) {
				arg.method_17354(optionalInt.getAsInt(), lv);
			}
		}
	}
}
