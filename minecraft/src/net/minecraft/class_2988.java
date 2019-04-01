package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_2988 extends class_3031<class_2986> {
	public class_2988(Function<Dynamic<?>, ? extends class_2986> function) {
		super(function);
	}

	public boolean method_12892(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_2986 arg4) {
		return arg4.field_13398.method_14358(arg, arg2, random, arg3, arg4.field_13399);
	}

	public String toString() {
		return String.format("< %s [%s] >", this.getClass().getSimpleName(), class_2378.field_11138.method_10221(this));
	}
}
