package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_268 extends class_270 {
	private final class_269 field_1420;
	private final String field_1421;
	private final Set<String> field_1415 = Sets.<String>newHashSet();
	private class_2561 field_1414;
	private class_2561 field_1418 = new class_2585("");
	private class_2561 field_1419 = new class_2585("");
	private boolean field_1417 = true;
	private boolean field_1416 = true;
	private class_270.class_272 field_1423 = class_270.class_272.field_1442;
	private class_270.class_272 field_1422 = class_270.class_272.field_1442;
	private class_124 field_1424 = class_124.field_1070;
	private class_270.class_271 field_1425 = class_270.class_271.field_1437;

	public class_268(class_269 arg, String string) {
		this.field_1420 = arg;
		this.field_1421 = string;
		this.field_1414 = new class_2585(string);
	}

	@Override
	public String method_1197() {
		return this.field_1421;
	}

	public class_2561 method_1140() {
		return this.field_1414;
	}

	public class_2561 method_1148() {
		class_2561 lv = class_2564.method_10885(
			this.field_1414
				.method_10853()
				.method_10859(arg -> arg.method_10975(this.field_1421).method_10949(new class_2568(class_2568.class_2569.field_11762, new class_2585(this.field_1421))))
		);
		class_124 lv2 = this.method_1202();
		if (lv2 != class_124.field_1070) {
			lv.method_10854(lv2);
		}

		return lv;
	}

	public void method_1137(class_2561 arg) {
		if (arg == null) {
			throw new IllegalArgumentException("Name cannot be null");
		} else {
			this.field_1414 = arg;
			this.field_1420.method_1154(this);
		}
	}

	public void method_1138(@Nullable class_2561 arg) {
		this.field_1418 = (class_2561)(arg == null ? new class_2585("") : arg.method_10853());
		this.field_1420.method_1154(this);
	}

	public class_2561 method_1144() {
		return this.field_1418;
	}

	public void method_1139(@Nullable class_2561 arg) {
		this.field_1419 = (class_2561)(arg == null ? new class_2585("") : arg.method_10853());
		this.field_1420.method_1154(this);
	}

	public class_2561 method_1136() {
		return this.field_1419;
	}

	@Override
	public Collection<String> method_1204() {
		return this.field_1415;
	}

	@Override
	public class_2561 method_1198(class_2561 arg) {
		class_2561 lv = new class_2585("").method_10852(this.field_1418).method_10852(arg).method_10852(this.field_1419);
		class_124 lv2 = this.method_1202();
		if (lv2 != class_124.field_1070) {
			lv.method_10854(lv2);
		}

		return lv;
	}

	public static class_2561 method_1142(@Nullable class_270 arg, class_2561 arg2) {
		return arg == null ? arg2.method_10853() : arg.method_1198(arg2);
	}

	@Override
	public boolean method_1205() {
		return this.field_1417;
	}

	public void method_1135(boolean bl) {
		this.field_1417 = bl;
		this.field_1420.method_1154(this);
	}

	@Override
	public boolean method_1199() {
		return this.field_1416;
	}

	public void method_1143(boolean bl) {
		this.field_1416 = bl;
		this.field_1420.method_1154(this);
	}

	@Override
	public class_270.class_272 method_1201() {
		return this.field_1423;
	}

	@Override
	public class_270.class_272 method_1200() {
		return this.field_1422;
	}

	public void method_1149(class_270.class_272 arg) {
		this.field_1423 = arg;
		this.field_1420.method_1154(this);
	}

	public void method_1133(class_270.class_272 arg) {
		this.field_1422 = arg;
		this.field_1420.method_1154(this);
	}

	@Override
	public class_270.class_271 method_1203() {
		return this.field_1425;
	}

	public void method_1145(class_270.class_271 arg) {
		this.field_1425 = arg;
		this.field_1420.method_1154(this);
	}

	public int method_1147() {
		int i = 0;
		if (this.method_1205()) {
			i |= 1;
		}

		if (this.method_1199()) {
			i |= 2;
		}

		return i;
	}

	@Environment(EnvType.CLIENT)
	public void method_1146(int i) {
		this.method_1135((i & 1) > 0);
		this.method_1143((i & 2) > 0);
	}

	public void method_1141(class_124 arg) {
		this.field_1424 = arg;
		this.field_1420.method_1154(this);
	}

	@Override
	public class_124 method_1202() {
		return this.field_1424;
	}
}
