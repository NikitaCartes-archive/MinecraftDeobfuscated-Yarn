package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2875 implements class_2596<class_2792> {
	private class_2338 field_13093;
	private class_2633.class_2634 field_13082;
	private class_2776 field_13084;
	private String field_13080;
	private class_2338 field_13091;
	private class_2338 field_13083;
	private class_2415 field_13081;
	private class_2470 field_13088;
	private String field_13085;
	private boolean field_13089;
	private boolean field_13087;
	private boolean field_13086;
	private float field_13090;
	private long field_13092;

	public class_2875() {
	}

	@Environment(EnvType.CLIENT)
	public class_2875(
		class_2338 arg,
		class_2633.class_2634 arg2,
		class_2776 arg3,
		String string,
		class_2338 arg4,
		class_2338 arg5,
		class_2415 arg6,
		class_2470 arg7,
		String string2,
		boolean bl,
		boolean bl2,
		boolean bl3,
		float f,
		long l
	) {
		this.field_13093 = arg;
		this.field_13082 = arg2;
		this.field_13084 = arg3;
		this.field_13080 = string;
		this.field_13091 = arg4;
		this.field_13083 = arg5;
		this.field_13081 = arg6;
		this.field_13088 = arg7;
		this.field_13085 = string2;
		this.field_13089 = bl;
		this.field_13087 = bl2;
		this.field_13086 = bl3;
		this.field_13090 = f;
		this.field_13092 = l;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13093 = arg.method_10811();
		this.field_13082 = arg.method_10818(class_2633.class_2634.class);
		this.field_13084 = arg.method_10818(class_2776.class);
		this.field_13080 = arg.method_10800(32767);
		this.field_13091 = new class_2338(
			class_3532.method_15340(arg.readByte(), -32, 32), class_3532.method_15340(arg.readByte(), -32, 32), class_3532.method_15340(arg.readByte(), -32, 32)
		);
		this.field_13083 = new class_2338(
			class_3532.method_15340(arg.readByte(), 0, 32), class_3532.method_15340(arg.readByte(), 0, 32), class_3532.method_15340(arg.readByte(), 0, 32)
		);
		this.field_13081 = arg.method_10818(class_2415.class);
		this.field_13088 = arg.method_10818(class_2470.class);
		this.field_13085 = arg.method_10800(12);
		this.field_13090 = class_3532.method_15363(arg.readFloat(), 0.0F, 1.0F);
		this.field_13092 = arg.method_10792();
		int i = arg.readByte();
		this.field_13089 = (i & 1) != 0;
		this.field_13087 = (i & 2) != 0;
		this.field_13086 = (i & 4) != 0;
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10807(this.field_13093);
		arg.method_10817(this.field_13082);
		arg.method_10817(this.field_13084);
		arg.method_10814(this.field_13080);
		arg.writeByte(this.field_13091.method_10263());
		arg.writeByte(this.field_13091.method_10264());
		arg.writeByte(this.field_13091.method_10260());
		arg.writeByte(this.field_13083.method_10263());
		arg.writeByte(this.field_13083.method_10264());
		arg.writeByte(this.field_13083.method_10260());
		arg.method_10817(this.field_13081);
		arg.method_10817(this.field_13088);
		arg.method_10814(this.field_13085);
		arg.writeFloat(this.field_13090);
		arg.method_10791(this.field_13092);
		int i = 0;
		if (this.field_13089) {
			i |= 1;
		}

		if (this.field_13087) {
			i |= 2;
		}

		if (this.field_13086) {
			i |= 4;
		}

		arg.writeByte(i);
	}

	public void method_12495(class_2792 arg) {
		arg.method_12051(this);
	}

	public class_2338 method_12499() {
		return this.field_13093;
	}

	public class_2633.class_2634 method_12500() {
		return this.field_13082;
	}

	public class_2776 method_12504() {
		return this.field_13084;
	}

	public String method_12502() {
		return this.field_13080;
	}

	public class_2338 method_12496() {
		return this.field_13091;
	}

	public class_2338 method_12492() {
		return this.field_13083;
	}

	public class_2415 method_12493() {
		return this.field_13081;
	}

	public class_2470 method_12498() {
		return this.field_13088;
	}

	public String method_12501() {
		return this.field_13085;
	}

	public boolean method_12506() {
		return this.field_13089;
	}

	public boolean method_12503() {
		return this.field_13087;
	}

	public boolean method_12505() {
		return this.field_13086;
	}

	public float method_12494() {
		return this.field_13090;
	}

	public long method_12497() {
		return this.field_13092;
	}
}
