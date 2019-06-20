package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_2290 implements Predicate<class_1799> {
	private static final Dynamic2CommandExceptionType field_10797 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("arguments.item.overstacked", object, object2)
	);
	private final class_1792 field_10796;
	@Nullable
	private final class_2487 field_10798;

	public class_2290(class_1792 arg, @Nullable class_2487 arg2) {
		this.field_10796 = arg;
		this.field_10798 = arg2;
	}

	public class_1792 method_9785() {
		return this.field_10796;
	}

	public boolean method_9783(class_1799 arg) {
		return arg.method_7909() == this.field_10796 && class_2512.method_10687(this.field_10798, arg.method_7969(), true);
	}

	public class_1799 method_9781(int i, boolean bl) throws CommandSyntaxException {
		class_1799 lv = new class_1799(this.field_10796, i);
		if (this.field_10798 != null) {
			lv.method_7980(this.field_10798);
		}

		if (bl && i > lv.method_7914()) {
			throw field_10797.create(class_2378.field_11142.method_10221(this.field_10796), lv.method_7914());
		} else {
			return lv;
		}
	}

	public String method_9782() {
		StringBuilder stringBuilder = new StringBuilder(class_2378.field_11142.method_10249(this.field_10796));
		if (this.field_10798 != null) {
			stringBuilder.append(this.field_10798);
		}

		return stringBuilder.toString();
	}
}
