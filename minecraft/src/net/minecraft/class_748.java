package net.minecraft;

import com.google.common.collect.ForwardingList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_748 extends ForwardingList<class_1799> {
	private final class_2371<class_1799> field_3948 = class_2371.method_10213(class_1661.method_7368(), class_1799.field_8037);

	@Override
	protected List<class_1799> delegate() {
		return this.field_3948;
	}

	public class_2499 method_3153() {
		class_2499 lv = new class_2499();

		for (class_1799 lv2 : this.delegate()) {
			lv.method_10606(lv2.method_7953(new class_2487()));
		}

		return lv;
	}

	public void method_3152(class_2499 arg) {
		List<class_1799> list = this.delegate();

		for (int i = 0; i < list.size(); i++) {
			list.set(i, class_1799.method_7915(arg.method_10602(i)));
		}
	}

	@Override
	public boolean isEmpty() {
		for (class_1799 lv : this.delegate()) {
			if (!lv.method_7960()) {
				return false;
			}
		}

		return true;
	}
}
