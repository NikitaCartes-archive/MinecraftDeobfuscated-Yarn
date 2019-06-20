package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class class_4170 {
	public static final class_4170 field_18603 = method_19217("empty").method_19221(0, class_4168.field_18595).method_19220();
	public static final class_4170 field_18604 = method_19217("simple")
		.method_19221(5000, class_4168.field_18596)
		.method_19221(11000, class_4168.field_18597)
		.method_19220();
	public static final class_4170 field_18605 = method_19217("villager_baby")
		.method_19221(10, class_4168.field_18595)
		.method_19221(3000, class_4168.field_18885)
		.method_19221(6000, class_4168.field_18595)
		.method_19221(10000, class_4168.field_18885)
		.method_19221(12000, class_4168.field_18597)
		.method_19220();
	public static final class_4170 field_18606 = method_19217("villager_default")
		.method_19221(10, class_4168.field_18595)
		.method_19221(2000, class_4168.field_18596)
		.method_19221(9000, class_4168.field_18598)
		.method_19221(11000, class_4168.field_18595)
		.method_19221(12000, class_4168.field_18597)
		.method_19220();
	private final Map<class_4168, class_4173> field_18607 = Maps.<class_4168, class_4173>newHashMap();

	protected static class_4171 method_19217(String string) {
		class_4170 lv = class_2378.method_10226(class_2378.field_18795, string, new class_4170());
		return new class_4171(lv);
	}

	protected void method_19215(class_4168 arg) {
		if (!this.field_18607.containsKey(arg)) {
			this.field_18607.put(arg, new class_4173());
		}
	}

	protected class_4173 method_19218(class_4168 arg) {
		return (class_4173)this.field_18607.get(arg);
	}

	protected List<class_4173> method_19219(class_4168 arg) {
		return (List<class_4173>)this.field_18607.entrySet().stream().filter(entry -> entry.getKey() != arg).map(Entry::getValue).collect(Collectors.toList());
	}

	public class_4168 method_19213(int i) {
		return (class_4168)this.field_18607
			.entrySet()
			.stream()
			.max(Comparator.comparingDouble(entry -> (double)((class_4173)entry.getValue()).method_19226(i)))
			.map(Entry::getKey)
			.orElse(class_4168.field_18595);
	}
}
