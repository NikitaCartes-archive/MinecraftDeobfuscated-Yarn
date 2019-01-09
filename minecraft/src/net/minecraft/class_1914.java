package net.minecraft;

public class class_1914 {
	private final class_1799 field_9146;
	private final class_1799 field_9143;
	private final class_1799 field_9148;
	private int field_9147;
	private int field_9144 = 7;
	private boolean field_9145 = true;

	public class_1914(class_2487 arg) {
		this.field_9146 = class_1799.method_7915(arg.method_10562("buy"));
		this.field_9143 = class_1799.method_7915(arg.method_10562("buyB"));
		this.field_9148 = class_1799.method_7915(arg.method_10562("sell"));
		this.field_9147 = arg.method_10550("uses");
		if (arg.method_10573("maxUses", 99)) {
			this.field_9144 = arg.method_10550("maxUses");
		}

		if (arg.method_10573("rewardExp", 1)) {
			this.field_9145 = arg.method_10577("rewardExp");
		}
	}

	public class_1914(class_1799 arg, class_1799 arg2) {
		this(arg, class_1799.field_8037, arg2);
	}

	public class_1914(class_1799 arg, class_1799 arg2, class_1799 arg3) {
		this(arg, arg2, arg3, 0, 7);
	}

	public class_1914(class_1799 arg, class_1799 arg2, class_1799 arg3, int i, int j) {
		this.field_9146 = arg;
		this.field_9143 = arg2;
		this.field_9148 = arg3;
		this.field_9147 = i;
		this.field_9144 = j;
	}

	public class_1799 method_8246() {
		return this.field_9146;
	}

	public class_1799 method_8247() {
		return this.field_9143;
	}

	public class_1799 method_8250() {
		return this.field_9148;
	}

	public int method_8249() {
		return this.field_9147;
	}

	public int method_8248() {
		return this.field_9144;
	}

	public void method_8244() {
		this.field_9147++;
	}

	public void method_8245(int i) {
		this.field_9144 += i;
	}

	public boolean method_8255() {
		return this.field_9147 >= this.field_9144;
	}

	public void method_8254() {
		this.field_9147 = this.field_9144;
	}

	public boolean method_8256() {
		return this.field_9145;
	}

	public class_2487 method_8251() {
		class_2487 lv = new class_2487();
		lv.method_10566("buy", this.field_9146.method_7953(new class_2487()));
		lv.method_10566("sell", this.field_9148.method_7953(new class_2487()));
		lv.method_10566("buyB", this.field_9143.method_7953(new class_2487()));
		lv.method_10569("uses", this.field_9147);
		lv.method_10569("maxUses", this.field_9144);
		lv.method_10556("rewardExp", this.field_9145);
		return lv;
	}

	public boolean method_16952(class_1799 arg, class_1799 arg2) {
		return this.method_16954(arg, this.field_9146)
			&& arg.method_7947() >= this.field_9146.method_7947()
			&& this.method_16954(arg2, this.field_9143)
			&& arg2.method_7947() >= this.field_9143.method_7947();
	}

	private boolean method_16954(class_1799 arg, class_1799 arg2) {
		if (arg2.method_7960() && arg.method_7960()) {
			return true;
		} else {
			class_1799 lv = arg.method_7972();
			if (lv.method_7909().method_7846()) {
				lv.method_7974(lv.method_7919());
			}

			return class_1799.method_7984(lv, arg2) && (!arg2.method_7985() || lv.method_7985() && class_2512.method_10687(arg2.method_7969(), lv.method_7969(), false));
		}
	}

	public boolean method_16953(class_1799 arg, class_1799 arg2) {
		if (!this.method_16952(arg, arg2)) {
			return false;
		} else {
			arg.method_7934(this.method_8246().method_7947());
			if (!this.method_8247().method_7960()) {
				arg2.method_7934(this.method_8247().method_7947());
			}

			return true;
		}
	}
}
