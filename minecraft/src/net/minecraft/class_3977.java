package net.minecraft;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class class_3977 extends class_2867 {
	protected final DataFixer field_17655;
	@Nullable
	private class_3360 field_17654;

	public class_3977(File file, DataFixer dataFixer) {
		super(file);
		this.field_17655 = dataFixer;
	}

	public class_2487 method_17907(class_2874 arg, Supplier<class_26> supplier, class_2487 arg2) {
		int i = method_17908(arg2);
		int j = 1493;
		if (i < 1493) {
			arg2 = class_2512.method_10693(this.field_17655, class_4284.field_19214, arg2, i, 1493);
			if (arg2.method_10562("Level").method_10577("hasLegacyStructureData")) {
				if (this.field_17654 == null) {
					this.field_17654 = class_3360.method_14745(arg, (class_26)supplier.get());
				}

				arg2 = this.field_17654.method_14735(arg2);
			}
		}

		arg2 = class_2512.method_10688(this.field_17655, class_4284.field_19214, arg2, Math.max(1493, i));
		if (i < class_155.method_16673().getWorldVersion()) {
			arg2.method_10569("DataVersion", class_155.method_16673().getWorldVersion());
		}

		return arg2;
	}

	public static int method_17908(class_2487 arg) {
		return arg.method_10573("DataVersion", 99) ? arg.method_10550("DataVersion") : -1;
	}

	@Override
	public void method_17910(class_1923 arg, class_2487 arg2) throws IOException {
		super.method_17910(arg, arg2);
		if (this.field_17654 != null) {
			this.field_17654.method_14744(arg.method_8324());
		}
	}
}
