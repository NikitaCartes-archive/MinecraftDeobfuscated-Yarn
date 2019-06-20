package net.minecraft;

public interface class_2780 {
	void method_11934(class_2784 arg, double d);

	void method_11931(class_2784 arg, double d, double e, long l);

	void method_11930(class_2784 arg, double d, double e);

	void method_11932(class_2784 arg, int i);

	void method_11933(class_2784 arg, int i);

	void method_11929(class_2784 arg, double d);

	void method_11935(class_2784 arg, double d);

	public static class class_3976 implements class_2780 {
		private final class_2784 field_17652;

		public class_3976(class_2784 arg) {
			this.field_17652 = arg;
		}

		@Override
		public void method_11934(class_2784 arg, double d) {
			this.field_17652.method_11969(d);
		}

		@Override
		public void method_11931(class_2784 arg, double d, double e, long l) {
			this.field_17652.method_11957(d, e, l);
		}

		@Override
		public void method_11930(class_2784 arg, double d, double e) {
			this.field_17652.method_11978(d, e);
		}

		@Override
		public void method_11932(class_2784 arg, int i) {
			this.field_17652.method_11975(i);
		}

		@Override
		public void method_11933(class_2784 arg, int i) {
			this.field_17652.method_11967(i);
		}

		@Override
		public void method_11929(class_2784 arg, double d) {
			this.field_17652.method_11955(d);
		}

		@Override
		public void method_11935(class_2784 arg, double d) {
			this.field_17652.method_11981(d);
		}
	}
}
