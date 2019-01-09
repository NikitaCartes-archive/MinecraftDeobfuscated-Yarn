package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2684 implements class_2596<class_2602> {
	protected int field_12310;
	protected int field_12309;
	protected int field_12308;
	protected int field_12307;
	protected byte field_12312;
	protected byte field_12311;
	protected boolean field_12306;
	protected boolean field_12305;

	public class_2684() {
	}

	public class_2684(int i) {
		this.field_12310 = i;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12310 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12310);
	}

	public void method_11651(class_2602 arg) {
		arg.method_11155(this);
	}

	public String toString() {
		return "Entity_" + super.toString();
	}

	@Environment(EnvType.CLIENT)
	public class_1297 method_11645(class_1937 arg) {
		return arg.method_8469(this.field_12310);
	}

	@Environment(EnvType.CLIENT)
	public int method_11648() {
		return this.field_12309;
	}

	@Environment(EnvType.CLIENT)
	public int method_11646() {
		return this.field_12308;
	}

	@Environment(EnvType.CLIENT)
	public int method_11647() {
		return this.field_12307;
	}

	@Environment(EnvType.CLIENT)
	public byte method_11649() {
		return this.field_12312;
	}

	@Environment(EnvType.CLIENT)
	public byte method_11650() {
		return this.field_12311;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11652() {
		return this.field_12305;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11653() {
		return this.field_12306;
	}

	public static class class_2685 extends class_2684 {
		public class_2685() {
		}

		public class_2685(int i, long l, long m, long n, boolean bl) {
			super(i);
			this.field_12309 = (int)l;
			this.field_12308 = (int)m;
			this.field_12307 = (int)n;
			this.field_12306 = bl;
		}

		@Override
		public void method_11053(class_2540 arg) throws IOException {
			super.method_11053(arg);
			this.field_12309 = arg.readShort();
			this.field_12308 = arg.readShort();
			this.field_12307 = arg.readShort();
			this.field_12306 = arg.readBoolean();
		}

		@Override
		public void method_11052(class_2540 arg) throws IOException {
			super.method_11052(arg);
			arg.writeShort(this.field_12309);
			arg.writeShort(this.field_12308);
			arg.writeShort(this.field_12307);
			arg.writeBoolean(this.field_12306);
		}
	}

	public static class class_2686 extends class_2684 {
		public class_2686() {
			this.field_12305 = true;
		}

		public class_2686(int i, long l, long m, long n, byte b, byte c, boolean bl) {
			super(i);
			this.field_12309 = (int)l;
			this.field_12308 = (int)m;
			this.field_12307 = (int)n;
			this.field_12312 = b;
			this.field_12311 = c;
			this.field_12306 = bl;
			this.field_12305 = true;
		}

		@Override
		public void method_11053(class_2540 arg) throws IOException {
			super.method_11053(arg);
			this.field_12309 = arg.readShort();
			this.field_12308 = arg.readShort();
			this.field_12307 = arg.readShort();
			this.field_12312 = arg.readByte();
			this.field_12311 = arg.readByte();
			this.field_12306 = arg.readBoolean();
		}

		@Override
		public void method_11052(class_2540 arg) throws IOException {
			super.method_11052(arg);
			arg.writeShort(this.field_12309);
			arg.writeShort(this.field_12308);
			arg.writeShort(this.field_12307);
			arg.writeByte(this.field_12312);
			arg.writeByte(this.field_12311);
			arg.writeBoolean(this.field_12306);
		}
	}

	public static class class_2687 extends class_2684 {
		public class_2687() {
			this.field_12305 = true;
		}

		public class_2687(int i, byte b, byte c, boolean bl) {
			super(i);
			this.field_12312 = b;
			this.field_12311 = c;
			this.field_12305 = true;
			this.field_12306 = bl;
		}

		@Override
		public void method_11053(class_2540 arg) throws IOException {
			super.method_11053(arg);
			this.field_12312 = arg.readByte();
			this.field_12311 = arg.readByte();
			this.field_12306 = arg.readBoolean();
		}

		@Override
		public void method_11052(class_2540 arg) throws IOException {
			super.method_11052(arg);
			arg.writeByte(this.field_12312);
			arg.writeByte(this.field_12311);
			arg.writeBoolean(this.field_12306);
		}
	}
}
