package net.minecraft;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2612 implements class_2596<class_2602> {
	private int field_12012;
	private UUID field_12009;
	private class_2338 field_12008;
	private class_2350 field_12011;
	private int field_12010;

	public class_2612() {
	}

	public class_2612(class_1534 arg) {
		this.field_12012 = arg.method_5628();
		this.field_12009 = arg.method_5667();
		this.field_12008 = arg.method_6896();
		this.field_12011 = arg.method_5735();
		this.field_12010 = class_2378.field_11150.method_10249(arg.field_7134);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12012 = arg.method_10816();
		this.field_12009 = arg.method_10790();
		this.field_12010 = arg.method_10816();
		this.field_12008 = arg.method_10811();
		this.field_12011 = class_2350.method_10139(arg.readUnsignedByte());
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12012);
		arg.method_10797(this.field_12009);
		arg.method_10804(this.field_12010);
		arg.method_10807(this.field_12008);
		arg.writeByte(this.field_12011.method_10161());
	}

	public void method_11224(class_2602 arg) {
		arg.method_11114(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11225() {
		return this.field_12012;
	}

	@Environment(EnvType.CLIENT)
	public UUID method_11222() {
		return this.field_12009;
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_11226() {
		return this.field_12008;
	}

	@Environment(EnvType.CLIENT)
	public class_2350 method_11223() {
		return this.field_12011;
	}

	@Environment(EnvType.CLIENT)
	public class_1535 method_11221() {
		return class_2378.field_11150.method_10200(this.field_12010);
	}
}
