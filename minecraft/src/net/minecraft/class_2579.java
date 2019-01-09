package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2579 extends class_2554 implements class_2566 {
	private static final Logger field_11791 = LogManager.getLogger();
	private final String field_11789;
	@Nullable
	private final class_2300 field_11790;

	public class_2579(String string) {
		this.field_11789 = string;
		class_2300 lv = null;

		try {
			class_2303 lv2 = new class_2303(new StringReader(string));
			lv = lv2.method_9882();
		} catch (CommandSyntaxException var4) {
			field_11791.warn("Invalid selector component: {}", string, var4.getMessage());
		}

		this.field_11790 = lv;
	}

	public String method_10932() {
		return this.field_11789;
	}

	@Override
	public class_2561 method_10890(@Nullable class_2168 arg, @Nullable class_1297 arg2) throws CommandSyntaxException {
		return (class_2561)(arg != null && this.field_11790 != null ? class_2300.method_9822(this.field_11790.method_9816(arg)) : new class_2585(""));
	}

	@Override
	public String method_10851() {
		return this.field_11789;
	}

	public class_2579 method_10931() {
		return new class_2579(this.field_11789);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2579)) {
			return false;
		} else {
			class_2579 lv = (class_2579)object;
			return this.field_11789.equals(lv.field_11789) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "SelectorComponent{pattern='" + this.field_11789 + '\'' + ", siblings=" + this.field_11729 + ", style=" + this.method_10866() + '}';
	}
}
