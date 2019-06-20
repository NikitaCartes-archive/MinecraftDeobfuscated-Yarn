package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang3.ArrayUtils;

public class class_65 extends class_69 {
	class_65(class_79[] args, class_209[] args2) {
		super(args, args2);
	}

	@Override
	protected class_64 method_394(class_64[] args) {
		switch (args.length) {
			case 0:
				return field_16883;
			case 1:
				return args[0];
			case 2:
				return args[0].method_385(args[1]);
			default:
				return (arg, consumer) -> {
					for (class_64 lv : args) {
						if (lv.expand(arg, consumer)) {
							return true;
						}
					}

					return false;
				};
		}
	}

	@Override
	public void method_415(class_58 arg, Function<class_2960, class_52> function, Set<class_2960> set, class_176 arg2) {
		super.method_415(arg, function, set, arg2);

		for (int i = 0; i < this.field_982.length - 1; i++) {
			if (ArrayUtils.isEmpty((Object[])this.field_982[i].field_988)) {
				arg.method_360("Unreachable entry!");
			}
		}
	}

	public static class_65.class_66 method_386(class_79.class_80<?>... args) {
		return new class_65.class_66(args);
	}

	public static class class_66 extends class_79.class_80<class_65.class_66> {
		private final List<class_79> field_979 = Lists.<class_79>newArrayList();

		public class_66(class_79.class_80<?>... args) {
			for (class_79.class_80<?> lv : args) {
				this.field_979.add(lv.method_419());
			}
		}

		protected class_65.class_66 method_388() {
			return this;
		}

		@Override
		public class_65.class_66 method_417(class_79.class_80<?> arg) {
			this.field_979.add(arg.method_419());
			return this;
		}

		@Override
		public class_79 method_419() {
			return new class_65((class_79[])this.field_979.toArray(new class_79[0]), this.method_420());
		}
	}
}
