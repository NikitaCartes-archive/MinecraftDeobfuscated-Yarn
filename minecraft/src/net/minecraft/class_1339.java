package net.minecraft;

public class class_1339 extends class_1343 {
	protected int field_6398;
	protected int field_6397 = -1;
	protected int field_16596 = -1;

	public class_1339(class_1308 arg) {
		super(arg);
	}

	public class_1339(class_1308 arg, int i) {
		this(arg);
		this.field_16596 = i;
	}

	protected int method_16462() {
		return Math.max(240, this.field_16596);
	}

	@Override
	public boolean method_6264() {
		if (!super.method_6264()) {
			return false;
		} else {
			return !this.field_6413.field_6002.method_8450().method_8355("mobGriefing") ? false : !this.method_6256();
		}
	}

	@Override
	public void method_6269() {
		super.method_6269();
		this.field_6398 = 0;
	}

	@Override
	public boolean method_6266() {
		double d = this.field_6413.method_5831(this.field_6414);
		return this.field_6398 <= this.method_16462() && !this.method_6256() && d < 4.0;
	}

	@Override
	public void method_6270() {
		super.method_6270();
		this.field_6413.field_6002.method_8517(this.field_6413.method_5628(), this.field_6414, -1);
	}

	@Override
	public void method_6268() {
		super.method_6268();
		if (this.field_6413.method_6051().nextInt(20) == 0) {
			this.field_6413.field_6002.method_8535(1019, this.field_6414, 0);
			if (!this.field_6413.field_6252) {
				this.field_6413.method_6104(this.field_6413.method_6058());
			}
		}

		this.field_6398++;
		int i = (int)((float)this.field_6398 / (float)this.method_16462() * 10.0F);
		if (i != this.field_6397) {
			this.field_6413.field_6002.method_8517(this.field_6413.method_5628(), this.field_6414, i);
			this.field_6397 = i;
		}

		if (this.field_6398 == this.method_16462() && this.field_6413.field_6002.method_8407() == class_1267.field_5807) {
			this.field_6413.field_6002.method_8650(this.field_6414);
			this.field_6413.field_6002.method_8535(1021, this.field_6414, 0);
			this.field_6413.field_6002.method_8535(2001, this.field_6414, class_2248.method_9507(this.field_6413.field_6002.method_8320(this.field_6414)));
		}
	}
}
