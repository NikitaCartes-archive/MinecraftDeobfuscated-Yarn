package net.minecraft;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2664 implements class_2596<class_2602> {
	private double field_12180;
	private double field_12178;
	private double field_12177;
	private float field_12179;
	private List<class_2338> field_12181;
	private float field_12176;
	private float field_12183;
	private float field_12182;

	public class_2664() {
	}

	public class_2664(double d, double e, double f, float g, List<class_2338> list, class_243 arg) {
		this.field_12180 = d;
		this.field_12178 = e;
		this.field_12177 = f;
		this.field_12179 = g;
		this.field_12181 = Lists.<class_2338>newArrayList(list);
		if (arg != null) {
			this.field_12176 = (float)arg.field_1352;
			this.field_12183 = (float)arg.field_1351;
			this.field_12182 = (float)arg.field_1350;
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12180 = (double)arg.readFloat();
		this.field_12178 = (double)arg.readFloat();
		this.field_12177 = (double)arg.readFloat();
		this.field_12179 = arg.readFloat();
		int i = arg.readInt();
		this.field_12181 = Lists.<class_2338>newArrayListWithCapacity(i);
		int j = (int)this.field_12180;
		int k = (int)this.field_12178;
		int l = (int)this.field_12177;

		for (int m = 0; m < i; m++) {
			int n = arg.readByte() + j;
			int o = arg.readByte() + k;
			int p = arg.readByte() + l;
			this.field_12181.add(new class_2338(n, o, p));
		}

		this.field_12176 = arg.readFloat();
		this.field_12183 = arg.readFloat();
		this.field_12182 = arg.readFloat();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeFloat((float)this.field_12180);
		arg.writeFloat((float)this.field_12178);
		arg.writeFloat((float)this.field_12177);
		arg.writeFloat(this.field_12179);
		arg.writeInt(this.field_12181.size());
		int i = (int)this.field_12180;
		int j = (int)this.field_12178;
		int k = (int)this.field_12177;

		for (class_2338 lv : this.field_12181) {
			int l = lv.method_10263() - i;
			int m = lv.method_10264() - j;
			int n = lv.method_10260() - k;
			arg.writeByte(l);
			arg.writeByte(m);
			arg.writeByte(n);
		}

		arg.writeFloat(this.field_12176);
		arg.writeFloat(this.field_12183);
		arg.writeFloat(this.field_12182);
	}

	public void method_11480(class_2602 arg) {
		arg.method_11124(this);
	}

	@Environment(EnvType.CLIENT)
	public float method_11472() {
		return this.field_12176;
	}

	@Environment(EnvType.CLIENT)
	public float method_11473() {
		return this.field_12183;
	}

	@Environment(EnvType.CLIENT)
	public float method_11474() {
		return this.field_12182;
	}

	@Environment(EnvType.CLIENT)
	public double method_11475() {
		return this.field_12180;
	}

	@Environment(EnvType.CLIENT)
	public double method_11477() {
		return this.field_12178;
	}

	@Environment(EnvType.CLIENT)
	public double method_11478() {
		return this.field_12177;
	}

	@Environment(EnvType.CLIENT)
	public float method_11476() {
		return this.field_12179;
	}

	@Environment(EnvType.CLIENT)
	public List<class_2338> method_11479() {
		return this.field_12181;
	}
}
