package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2871 implements class_2596<class_2792> {
	private int field_13067;
	private String field_13068;
	private boolean field_13066;

	public class_2871() {
	}

	@Environment(EnvType.CLIENT)
	public class_2871(int i, String string, boolean bl) {
		this.field_13067 = i;
		this.field_13068 = string;
		this.field_13066 = bl;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13067 = arg.method_10816();
		this.field_13068 = arg.method_10800(32767);
		this.field_13066 = arg.readBoolean();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_13067);
		arg.method_10814(this.field_13068);
		arg.writeBoolean(this.field_13066);
	}

	public void method_12477(class_2792 arg) {
		arg.method_12049(this);
	}

	@Nullable
	public class_1918 method_12476(class_1937 arg) {
		class_1297 lv = arg.method_8469(this.field_13067);
		return lv instanceof class_1697 ? ((class_1697)lv).method_7567() : null;
	}

	public String method_12475() {
		return this.field_13068;
	}

	public boolean method_12478() {
		return this.field_13066;
	}
}
