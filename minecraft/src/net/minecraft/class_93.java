package net.minecraft;

public class class_93 extends class_69 {
	class_93(class_79[] args, class_209[] args2) {
		super(args, args2);
	}

	@Override
	protected class_64 method_394(class_64[] args) {
		switch (args.length) {
			case 0:
				return field_16884;
			case 1:
				return args[0];
			case 2:
				return args[0].method_16778(args[1]);
			default:
				return (arg, consumer) -> {
					for (class_64 lv : args) {
						if (!lv.expand(arg, consumer)) {
							return false;
						}
					}

					return true;
				};
		}
	}
}
