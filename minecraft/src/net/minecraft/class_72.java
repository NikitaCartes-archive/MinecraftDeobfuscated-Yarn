package net.minecraft;

public class class_72 extends class_69 {
	class_72(class_79[] args, class_209[] args2) {
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
				class_64 lv = args[0];
				class_64 lv2 = args[1];
				return (arg3, consumer) -> {
					lv.expand(arg3, consumer);
					lv2.expand(arg3, consumer);
					return true;
				};
			default:
				return (arg, consumer) -> {
					for (class_64 lvx : args) {
						lvx.expand(arg, consumer);
					}

					return true;
				};
		}
	}
}
