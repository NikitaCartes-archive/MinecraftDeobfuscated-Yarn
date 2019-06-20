package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2853 implements class_2596<class_2792> {
	private class_2853.class_2854 field_13009;
	private class_2960 field_13004;
	private boolean field_13008;
	private boolean field_13007;
	private boolean field_13006;
	private boolean field_13005;
	private boolean field_17203;
	private boolean field_17204;
	private boolean field_17205;
	private boolean field_17206;

	public class_2853() {
	}

	public class_2853(class_1860<?> arg) {
		this.field_13009 = class_2853.class_2854.field_13011;
		this.field_13004 = arg.method_8114();
	}

	@Environment(EnvType.CLIENT)
	public class_2853(boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6) {
		this.field_13009 = class_2853.class_2854.field_13010;
		this.field_13008 = bl;
		this.field_13007 = bl2;
		this.field_13006 = bl3;
		this.field_13005 = bl4;
		this.field_17203 = bl5;
		this.field_17204 = bl6;
		this.field_17205 = bl5;
		this.field_17206 = bl6;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13009 = arg.method_10818(class_2853.class_2854.class);
		if (this.field_13009 == class_2853.class_2854.field_13011) {
			this.field_13004 = arg.method_10810();
		} else if (this.field_13009 == class_2853.class_2854.field_13010) {
			this.field_13008 = arg.readBoolean();
			this.field_13007 = arg.readBoolean();
			this.field_13006 = arg.readBoolean();
			this.field_13005 = arg.readBoolean();
			this.field_17203 = arg.readBoolean();
			this.field_17204 = arg.readBoolean();
			this.field_17205 = arg.readBoolean();
			this.field_17206 = arg.readBoolean();
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_13009);
		if (this.field_13009 == class_2853.class_2854.field_13011) {
			arg.method_10812(this.field_13004);
		} else if (this.field_13009 == class_2853.class_2854.field_13010) {
			arg.writeBoolean(this.field_13008);
			arg.writeBoolean(this.field_13007);
			arg.writeBoolean(this.field_13006);
			arg.writeBoolean(this.field_13005);
			arg.writeBoolean(this.field_17203);
			arg.writeBoolean(this.field_17204);
			arg.writeBoolean(this.field_17205);
			arg.writeBoolean(this.field_17206);
		}
	}

	public void method_12400(class_2792 arg) {
		arg.method_12047(this);
	}

	public class_2853.class_2854 method_12402() {
		return this.field_13009;
	}

	public class_2960 method_12406() {
		return this.field_13004;
	}

	public boolean method_12403() {
		return this.field_13008;
	}

	public boolean method_12401() {
		return this.field_13007;
	}

	public boolean method_12404() {
		return this.field_13006;
	}

	public boolean method_12405() {
		return this.field_13005;
	}

	public boolean method_17192() {
		return this.field_17203;
	}

	public boolean method_17193() {
		return this.field_17204;
	}

	public boolean method_17194() {
		return this.field_17205;
	}

	public boolean method_17195() {
		return this.field_17206;
	}

	public static enum class_2854 {
		field_13011,
		field_13010;
	}
}
