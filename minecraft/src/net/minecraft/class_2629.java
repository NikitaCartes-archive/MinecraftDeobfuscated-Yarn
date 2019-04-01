package net.minecraft;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2629 implements class_2596<class_2602> {
	private UUID field_12074;
	private class_2629.class_2630 field_12075;
	private class_2561 field_12071;
	private float field_12069;
	private class_1259.class_1260 field_12073;
	private class_1259.class_1261 field_12076;
	private boolean field_12072;
	private boolean field_12070;
	private boolean field_12068;

	public class_2629() {
	}

	public class_2629(class_2629.class_2630 arg, class_1259 arg2) {
		this.field_12075 = arg;
		this.field_12074 = arg2.method_5407();
		this.field_12071 = arg2.method_5414();
		this.field_12069 = arg2.method_5412();
		this.field_12073 = arg2.method_5420();
		this.field_12076 = arg2.method_5415();
		this.field_12072 = arg2.method_5417();
		this.field_12070 = arg2.method_5418();
		this.field_12068 = arg2.method_5419();
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12074 = arg.method_10790();
		this.field_12075 = arg.method_10818(class_2629.class_2630.class);
		switch (this.field_12075) {
			case field_12078:
				this.field_12071 = arg.method_10808();
				this.field_12069 = arg.readFloat();
				this.field_12073 = arg.method_10818(class_1259.class_1260.class);
				this.field_12076 = arg.method_10818(class_1259.class_1261.class);
				this.method_11323(arg.readUnsignedByte());
			case field_12082:
			default:
				break;
			case field_12080:
				this.field_12069 = arg.readFloat();
				break;
			case field_12084:
				this.field_12071 = arg.method_10808();
				break;
			case field_12081:
				this.field_12073 = arg.method_10818(class_1259.class_1260.class);
				this.field_12076 = arg.method_10818(class_1259.class_1261.class);
				break;
			case field_12083:
				this.method_11323(arg.readUnsignedByte());
		}
	}

	private void method_11323(int i) {
		this.field_12072 = (i & 1) > 0;
		this.field_12070 = (i & 2) > 0;
		this.field_12068 = (i & 4) > 0;
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10797(this.field_12074);
		arg.method_10817(this.field_12075);
		switch (this.field_12075) {
			case field_12078:
				arg.method_10805(this.field_12071);
				arg.writeFloat(this.field_12069);
				arg.method_10817(this.field_12073);
				arg.method_10817(this.field_12076);
				arg.writeByte(this.method_11326());
			case field_12082:
			default:
				break;
			case field_12080:
				arg.writeFloat(this.field_12069);
				break;
			case field_12084:
				arg.method_10805(this.field_12071);
				break;
			case field_12081:
				arg.method_10817(this.field_12073);
				arg.method_10817(this.field_12076);
				break;
			case field_12083:
				arg.writeByte(this.method_11326());
		}
	}

	private int method_11326() {
		int i = 0;
		if (this.field_12072) {
			i |= 1;
		}

		if (this.field_12070) {
			i |= 2;
		}

		if (this.field_12068) {
			i |= 4;
		}

		return i;
	}

	public void method_11330(class_2602 arg) {
		arg.method_11078(this);
	}

	@Environment(EnvType.CLIENT)
	public UUID method_11322() {
		return this.field_12074;
	}

	@Environment(EnvType.CLIENT)
	public class_2629.class_2630 method_11324() {
		return this.field_12075;
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_11328() {
		return this.field_12071;
	}

	@Environment(EnvType.CLIENT)
	public float method_11327() {
		return this.field_12069;
	}

	@Environment(EnvType.CLIENT)
	public class_1259.class_1260 method_11325() {
		return this.field_12073;
	}

	@Environment(EnvType.CLIENT)
	public class_1259.class_1261 method_11329() {
		return this.field_12076;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11331() {
		return this.field_12072;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11321() {
		return this.field_12070;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11332() {
		return this.field_12068;
	}

	public static enum class_2630 {
		field_12078,
		field_12082,
		field_12080,
		field_12084,
		field_12081,
		field_12083;
	}
}
