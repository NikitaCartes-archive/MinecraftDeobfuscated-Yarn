package net.minecraft;

public interface class_6468<C> {
	float apply(C object);

	default class_6468<C> method_37749(class_6468<C> arg, class_6462.class_6465 arg2) {
		return object -> arg2.combine(this.apply(object), arg.apply(object));
	}
}
