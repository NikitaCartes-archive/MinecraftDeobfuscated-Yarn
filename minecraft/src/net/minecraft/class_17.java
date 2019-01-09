package net.minecraft;

import java.util.Objects;
import javax.annotation.Nullable;

public class class_17 {
	private final class_2338 field_69;
	private final class_1767 field_68;
	@Nullable
	private final class_2561 field_67;

	public class_17(class_2338 arg, class_1767 arg2, @Nullable class_2561 arg3) {
		this.field_69 = arg;
		this.field_68 = arg2;
		this.field_67 = arg3;
	}

	public static class_17 method_67(class_2487 arg) {
		class_2338 lv = class_2512.method_10691(arg.method_10562("Pos"));
		class_1767 lv2 = class_1767.method_7793(arg.method_10558("Color"), class_1767.field_7952);
		class_2561 lv3 = arg.method_10545("Name") ? class_2561.class_2562.method_10877(arg.method_10558("Name")) : null;
		return new class_17(lv, lv2, lv3);
	}

	@Nullable
	public static class_17 method_73(class_1922 arg, class_2338 arg2) {
		class_2586 lv = arg.method_8321(arg2);
		if (lv instanceof class_2573) {
			class_2573 lv2 = (class_2573)lv;
			class_1767 lv3 = lv2.method_10908(() -> arg.method_8320(arg2));
			class_2561 lv4 = lv2.method_16914() ? lv2.method_5797() : null;
			return new class_17(arg2, lv3, lv4);
		} else {
			return null;
		}
	}

	public class_2338 method_70() {
		return this.field_69;
	}

	public class_20.class_21 method_72() {
		switch (this.field_68) {
			case field_7952:
				return class_20.class_21.field_96;
			case field_7946:
				return class_20.class_21.field_92;
			case field_7958:
				return class_20.class_21.field_97;
			case field_7951:
				return class_20.class_21.field_90;
			case field_7947:
				return class_20.class_21.field_93;
			case field_7961:
				return class_20.class_21.field_94;
			case field_7954:
				return class_20.class_21.field_100;
			case field_7944:
				return class_20.class_21.field_101;
			case field_7967:
				return class_20.class_21.field_107;
			case field_7955:
				return class_20.class_21.field_108;
			case field_7945:
				return class_20.class_21.field_104;
			case field_7966:
				return class_20.class_21.field_105;
			case field_7957:
				return class_20.class_21.field_106;
			case field_7942:
				return class_20.class_21.field_102;
			case field_7964:
				return class_20.class_21.field_99;
			case field_7963:
			default:
				return class_20.class_21.field_103;
		}
	}

	@Nullable
	public class_2561 method_68() {
		return this.field_67;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_17 lv = (class_17)object;
			return Objects.equals(this.field_69, lv.field_69) && this.field_68 == lv.field_68 && Objects.equals(this.field_67, lv.field_67);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_69, this.field_68, this.field_67});
	}

	public class_2487 method_74() {
		class_2487 lv = new class_2487();
		lv.method_10566("Pos", class_2512.method_10692(this.field_69));
		lv.method_10582("Color", this.field_68.method_7792());
		if (this.field_67 != null) {
			lv.method_10582("Name", class_2561.class_2562.method_10867(this.field_67));
		}

		return lv;
	}

	public String method_71() {
		return "banner-" + this.field_69.method_10263() + "," + this.field_69.method_10264() + "," + this.field_69.method_10260();
	}
}
