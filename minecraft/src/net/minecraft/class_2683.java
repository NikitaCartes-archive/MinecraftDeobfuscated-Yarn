package net.minecraft;

import java.io.IOException;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2683 implements class_2596<class_2602> {
	private int field_12303;
	private byte field_12296;
	private boolean field_12302;
	private boolean field_17433;
	private class_20[] field_12304;
	private int field_12301;
	private int field_12300;
	private int field_12299;
	private int field_12297;
	private byte[] field_12298;

	public class_2683() {
	}

	public class_2683(int i, byte b, boolean bl, boolean bl2, Collection<class_20> collection, byte[] bs, int j, int k, int l, int m) {
		this.field_12303 = i;
		this.field_12296 = b;
		this.field_12302 = bl;
		this.field_17433 = bl2;
		this.field_12304 = (class_20[])collection.toArray(new class_20[collection.size()]);
		this.field_12301 = j;
		this.field_12300 = k;
		this.field_12299 = l;
		this.field_12297 = m;
		this.field_12298 = new byte[l * m];

		for (int n = 0; n < l; n++) {
			for (int o = 0; o < m; o++) {
				this.field_12298[n + o * l] = bs[j + n + (k + o) * 128];
			}
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12303 = arg.method_10816();
		this.field_12296 = arg.readByte();
		this.field_12302 = arg.readBoolean();
		this.field_17433 = arg.readBoolean();
		this.field_12304 = new class_20[arg.method_10816()];

		for (int i = 0; i < this.field_12304.length; i++) {
			class_20.class_21 lv = arg.method_10818(class_20.class_21.class);
			this.field_12304[i] = new class_20(lv, arg.readByte(), arg.readByte(), (byte)(arg.readByte() & 15), arg.readBoolean() ? arg.method_10808() : null);
		}

		this.field_12299 = arg.readUnsignedByte();
		if (this.field_12299 > 0) {
			this.field_12297 = arg.readUnsignedByte();
			this.field_12301 = arg.readUnsignedByte();
			this.field_12300 = arg.readUnsignedByte();
			this.field_12298 = arg.method_10795();
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12303);
		arg.writeByte(this.field_12296);
		arg.writeBoolean(this.field_12302);
		arg.writeBoolean(this.field_17433);
		arg.method_10804(this.field_12304.length);

		for (class_20 lv : this.field_12304) {
			arg.method_10817(lv.method_93());
			arg.writeByte(lv.method_90());
			arg.writeByte(lv.method_91());
			arg.writeByte(lv.method_89() & 15);
			if (lv.method_88() != null) {
				arg.writeBoolean(true);
				arg.method_10805(lv.method_88());
			} else {
				arg.writeBoolean(false);
			}
		}

		arg.writeByte(this.field_12299);
		if (this.field_12299 > 0) {
			arg.writeByte(this.field_12297);
			arg.writeByte(this.field_12301);
			arg.writeByte(this.field_12300);
			arg.method_10813(this.field_12298);
		}
	}

	public void method_11643(class_2602 arg) {
		arg.method_11088(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11644() {
		return this.field_12303;
	}

	@Environment(EnvType.CLIENT)
	public void method_11642(class_22 arg) {
		arg.field_119 = this.field_12296;
		arg.field_114 = this.field_12302;
		arg.field_17403 = this.field_17433;
		arg.field_117.clear();

		for (int i = 0; i < this.field_12304.length; i++) {
			class_20 lv = this.field_12304[i];
			arg.field_117.put("icon-" + i, lv);
		}

		for (int i = 0; i < this.field_12299; i++) {
			for (int j = 0; j < this.field_12297; j++) {
				arg.field_122[this.field_12301 + i + (this.field_12300 + j) * 128] = this.field_12298[i + j * this.field_12299];
			}
		}
	}
}
