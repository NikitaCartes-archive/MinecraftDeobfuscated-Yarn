package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public enum class_4139 {
	field_18424("major_negative", -5, 100, 1, 10),
	field_18425("minor_negative", -1, 200, 2, 20),
	field_18426("minor_positive", 1, 200, 2, 20),
	field_18427("major_positive", 5, 100, 1, 10),
	field_18428("trading", 1, 25, 2, 20),
	field_18429("golem", 1, 100, 1, 1);

	public final String field_18430;
	public final int field_18431;
	public final int field_18432;
	public final int field_18433;
	public final int field_18434;
	private static final Map<String, class_4139> field_18435 = (Map<String, class_4139>)Stream.of(values())
		.collect(ImmutableMap.toImmutableMap(arg -> arg.field_18430, Function.identity()));

	private class_4139(String string2, int j, int k, int l, int m) {
		this.field_18430 = string2;
		this.field_18431 = j;
		this.field_18432 = k;
		this.field_18433 = l;
		this.field_18434 = m;
	}

	@Nullable
	public static class_4139 method_19090(String string) {
		return (class_4139)field_18435.get(string);
	}
}
