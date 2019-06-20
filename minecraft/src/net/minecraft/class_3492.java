package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class class_3492 {
	private class_2415 field_15564 = class_2415.field_11302;
	private class_2470 field_15569 = class_2470.field_11467;
	private class_2338 field_15566 = class_2338.field_10980;
	private boolean field_15571;
	@Nullable
	private class_1923 field_15563;
	@Nullable
	private class_3341 field_15565;
	private boolean field_15567 = true;
	@Nullable
	private Random field_15570;
	@Nullable
	private Integer field_15572;
	private int field_15575;
	private final List<class_3491> field_16446 = Lists.<class_3491>newArrayList();
	private boolean field_16587;

	public class_3492 method_15128() {
		class_3492 lv = new class_3492();
		lv.field_15564 = this.field_15564;
		lv.field_15569 = this.field_15569;
		lv.field_15566 = this.field_15566;
		lv.field_15571 = this.field_15571;
		lv.field_15563 = this.field_15563;
		lv.field_15565 = this.field_15565;
		lv.field_15567 = this.field_15567;
		lv.field_15570 = this.field_15570;
		lv.field_15572 = this.field_15572;
		lv.field_15575 = this.field_15575;
		lv.field_16446.addAll(this.field_16446);
		lv.field_16587 = this.field_16587;
		return lv;
	}

	public class_3492 method_15125(class_2415 arg) {
		this.field_15564 = arg;
		return this;
	}

	public class_3492 method_15123(class_2470 arg) {
		this.field_15569 = arg;
		return this;
	}

	public class_3492 method_15119(class_2338 arg) {
		this.field_15566 = arg;
		return this;
	}

	public class_3492 method_15133(boolean bl) {
		this.field_15571 = bl;
		return this;
	}

	public class_3492 method_15130(class_1923 arg) {
		this.field_15563 = arg;
		return this;
	}

	public class_3492 method_15126(class_3341 arg) {
		this.field_15565 = arg;
		return this;
	}

	public class_3492 method_15112(@Nullable Random random) {
		this.field_15570 = random;
		return this;
	}

	public class_3492 method_15131(boolean bl) {
		this.field_16587 = bl;
		return this;
	}

	public class_3492 method_16183() {
		this.field_16446.clear();
		return this;
	}

	public class_3492 method_16184(class_3491 arg) {
		this.field_16446.add(arg);
		return this;
	}

	public class_3492 method_16664(class_3491 arg) {
		this.field_16446.remove(arg);
		return this;
	}

	public class_2415 method_15114() {
		return this.field_15564;
	}

	public class_2470 method_15113() {
		return this.field_15569;
	}

	public class_2338 method_15134() {
		return this.field_15566;
	}

	public Random method_15115(@Nullable class_2338 arg) {
		if (this.field_15570 != null) {
			return this.field_15570;
		} else {
			return arg == null ? new Random(class_156.method_658()) : new Random(class_3532.method_15389(arg));
		}
	}

	public boolean method_15135() {
		return this.field_15571;
	}

	@Nullable
	public class_3341 method_15124() {
		if (this.field_15565 == null && this.field_15563 != null) {
			this.method_15132();
		}

		return this.field_15565;
	}

	public boolean method_16444() {
		return this.field_16587;
	}

	public List<class_3491> method_16182() {
		return this.field_16446;
	}

	void method_15132() {
		if (this.field_15563 != null) {
			this.field_15565 = this.method_15117(this.field_15563);
		}
	}

	public boolean method_15120() {
		return this.field_15567;
	}

	public List<class_3499.class_3501> method_15121(List<List<class_3499.class_3501>> list, @Nullable class_2338 arg) {
		this.field_15572 = 8;
		if (this.field_15572 != null && this.field_15572 >= 0 && this.field_15572 < list.size()) {
			return (List<class_3499.class_3501>)list.get(this.field_15572);
		} else {
			this.field_15572 = this.method_15115(arg).nextInt(list.size());
			return (List<class_3499.class_3501>)list.get(this.field_15572);
		}
	}

	@Nullable
	private class_3341 method_15117(@Nullable class_1923 arg) {
		if (arg == null) {
			return this.field_15565;
		} else {
			int i = arg.field_9181 * 16;
			int j = arg.field_9180 * 16;
			return new class_3341(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
		}
	}
}
