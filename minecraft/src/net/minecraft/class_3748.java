package net.minecraft;

import javax.annotation.Nullable;

public class class_3748 extends class_2318 implements class_2343 {
	protected class_3748(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10927, class_2350.field_11036));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10927);
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_10927, arg2.method_10503(arg.method_11654(field_10927)));
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_10927, arg.method_8038());
	}

	@Nullable
	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_3751();
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		class_2586 lv = arg2.method_8321(arg3);
		if (lv instanceof class_3751 && arg4.method_7338()) {
			arg4.method_16354((class_3751)lv);
			return true;
		} else {
			return false;
		}
	}

	public static boolean method_16546(class_3499.class_3501 arg, class_3499.class_3501 arg2) {
		return arg.field_15596.method_11654(field_10927) == ((class_2350)arg2.field_15596.method_11654(field_10927)).method_10153()
			&& arg.field_15595.method_10558("attachement_type").equals(arg2.field_15595.method_10558("attachement_type"));
	}
}
