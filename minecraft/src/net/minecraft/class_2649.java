package net.minecraft;

import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2649 implements class_2596<class_2602> {
	private int field_12146;
	private List<class_1799> field_12147;

	public class_2649() {
	}

	public class_2649(int i, class_2371<class_1799> arg) {
		this.field_12146 = i;
		this.field_12147 = class_2371.<class_1799>method_10213(arg.size(), class_1799.field_8037);

		for (int j = 0; j < this.field_12147.size(); j++) {
			this.field_12147.set(j, arg.get(j).method_7972());
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12146 = arg.readUnsignedByte();
		int i = arg.readShort();
		this.field_12147 = class_2371.<class_1799>method_10213(i, class_1799.field_8037);

		for (int j = 0; j < i; j++) {
			this.field_12147.set(j, arg.method_10819());
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12146);
		arg.writeShort(this.field_12147.size());

		for (class_1799 lv : this.field_12147) {
			arg.method_10793(lv);
		}
	}

	public void method_11439(class_2602 arg) {
		arg.method_11153(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11440() {
		return this.field_12146;
	}

	@Environment(EnvType.CLIENT)
	public List<class_1799> method_11441() {
		return this.field_12147;
	}
}
