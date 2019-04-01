package net.minecraft;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2781 implements class_2596<class_2602> {
	private int field_12719;
	private final List<class_2781.class_2782> field_12720 = Lists.<class_2781.class_2782>newArrayList();

	public class_2781() {
	}

	public class_2781(int i, Collection<class_1324> collection) {
		this.field_12719 = i;

		for (class_1324 lv : collection) {
			this.field_12720.add(new class_2781.class_2782(lv.method_6198().method_6167(), lv.method_6201(), lv.method_6195()));
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12719 = arg.method_10816();
		int i = arg.readInt();

		for (int j = 0; j < i; j++) {
			String string = arg.method_10800(64);
			double d = arg.readDouble();
			List<class_1322> list = Lists.<class_1322>newArrayList();
			int k = arg.method_10816();

			for (int l = 0; l < k; l++) {
				UUID uUID = arg.method_10790();
				list.add(new class_1322(uUID, "Unknown synced attribute modifier", arg.readDouble(), class_1322.class_1323.method_6190(arg.readByte())));
			}

			this.field_12720.add(new class_2781.class_2782(string, d, list));
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12719);
		arg.writeInt(this.field_12720.size());

		for (class_2781.class_2782 lv : this.field_12720) {
			arg.method_10814(lv.method_11940());
			arg.writeDouble(lv.method_11941());
			arg.method_10804(lv.method_11939().size());

			for (class_1322 lv2 : lv.method_11939()) {
				arg.method_10797(lv2.method_6189());
				arg.writeDouble(lv2.method_6186());
				arg.writeByte(lv2.method_6182().method_6191());
			}
		}
	}

	public void method_11936(class_2602 arg) {
		arg.method_11149(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11937() {
		return this.field_12719;
	}

	@Environment(EnvType.CLIENT)
	public List<class_2781.class_2782> method_11938() {
		return this.field_12720;
	}

	public class class_2782 {
		private final String field_12724;
		private final double field_12722;
		private final Collection<class_1322> field_12723;

		public class_2782(String string, double d, Collection<class_1322> collection) {
			this.field_12724 = string;
			this.field_12722 = d;
			this.field_12723 = collection;
		}

		public String method_11940() {
			return this.field_12724;
		}

		public double method_11941() {
			return this.field_12722;
		}

		public Collection<class_1322> method_11939() {
			return this.field_12723;
		}
	}
}
