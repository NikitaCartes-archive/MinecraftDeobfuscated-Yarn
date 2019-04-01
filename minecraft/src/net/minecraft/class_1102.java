package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_1102 implements class_1113 {
	protected class_1111 field_5444;
	@Nullable
	private class_1146 field_5443;
	protected final class_3419 field_5447;
	protected final class_2960 field_5448;
	protected float field_5442 = 1.0F;
	protected float field_5441 = 1.0F;
	protected float field_5439;
	protected float field_5450;
	protected float field_5449;
	protected boolean field_5446;
	protected int field_5451;
	protected class_1113.class_1114 field_5440 = class_1113.class_1114.field_5476;
	protected boolean field_18935;
	protected boolean field_18936;

	protected class_1102(class_3414 arg, class_3419 arg2) {
		this(arg.method_14833(), arg2);
	}

	protected class_1102(class_2960 arg, class_3419 arg2) {
		this.field_5448 = arg;
		this.field_5447 = arg2;
	}

	@Override
	public class_2960 method_4775() {
		return this.field_5448;
	}

	@Override
	public class_1146 method_4783(class_1144 arg) {
		this.field_5443 = arg.method_4869(this.field_5448);
		if (this.field_5443 == null) {
			this.field_5444 = class_1144.field_5592;
		} else {
			this.field_5444 = this.field_5443.method_4887();
		}

		return this.field_5443;
	}

	@Override
	public class_1111 method_4776() {
		return this.field_5444;
	}

	@Override
	public class_3419 method_4774() {
		return this.field_5447;
	}

	@Override
	public boolean method_4786() {
		return this.field_5446;
	}

	@Override
	public int method_4780() {
		return this.field_5451;
	}

	@Override
	public float method_4781() {
		return this.field_5442 * this.field_5444.method_4771();
	}

	@Override
	public float method_4782() {
		return this.field_5441 * this.field_5444.method_4772();
	}

	@Override
	public float method_4784() {
		return this.field_5439;
	}

	@Override
	public float method_4779() {
		return this.field_5450;
	}

	@Override
	public float method_4778() {
		return this.field_5449;
	}

	@Override
	public class_1113.class_1114 method_4777() {
		return this.field_5440;
	}

	@Override
	public boolean method_4787() {
		return this.field_18936;
	}
}
