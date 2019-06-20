package net.minecraft;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class class_2697 {
	private static final Joiner field_12340 = Joiner.on(",");
	private final List<String[]> field_12344 = Lists.<String[]>newArrayList();
	private final Map<Character, Predicate<class_2694>> field_12343 = Maps.<Character, Predicate<class_2694>>newHashMap();
	private int field_12342;
	private int field_12341;

	private class_2697() {
		this.field_12343.put(' ', Predicates.alwaysTrue());
	}

	public class_2697 method_11702(String... strings) {
		if (!ArrayUtils.isEmpty((Object[])strings) && !StringUtils.isEmpty(strings[0])) {
			if (this.field_12344.isEmpty()) {
				this.field_12342 = strings.length;
				this.field_12341 = strings[0].length();
			}

			if (strings.length != this.field_12342) {
				throw new IllegalArgumentException("Expected aisle with height of " + this.field_12342 + ", but was given one with a height of " + strings.length + ")");
			} else {
				for (String string : strings) {
					if (string.length() != this.field_12341) {
						throw new IllegalArgumentException(
							"Not all rows in the given aisle are the correct width (expected " + this.field_12341 + ", found one with " + string.length() + ")"
						);
					}

					for (char c : string.toCharArray()) {
						if (!this.field_12343.containsKey(c)) {
							this.field_12343.put(c, null);
						}
					}
				}

				this.field_12344.add(strings);
				return this;
			}
		} else {
			throw new IllegalArgumentException("Empty pattern for aisle");
		}
	}

	public static class_2697 method_11701() {
		return new class_2697();
	}

	public class_2697 method_11700(char c, Predicate<class_2694> predicate) {
		this.field_12343.put(c, predicate);
		return this;
	}

	public class_2700 method_11704() {
		return new class_2700(this.method_11703());
	}

	private Predicate<class_2694>[][][] method_11703() {
		this.method_11705();
		Predicate<class_2694>[][][] predicates = (Predicate<class_2694>[][][])Array.newInstance(
			Predicate.class, new int[]{this.field_12344.size(), this.field_12342, this.field_12341}
		);

		for (int i = 0; i < this.field_12344.size(); i++) {
			for (int j = 0; j < this.field_12342; j++) {
				for (int k = 0; k < this.field_12341; k++) {
					predicates[i][j][k] = (Predicate<class_2694>)this.field_12343.get(((String[])this.field_12344.get(i))[j].charAt(k));
				}
			}
		}

		return predicates;
	}

	private void method_11705() {
		List<Character> list = Lists.<Character>newArrayList();

		for (Entry<Character, Predicate<class_2694>> entry : this.field_12343.entrySet()) {
			if (entry.getValue() == null) {
				list.add(entry.getKey());
			}
		}

		if (!list.isEmpty()) {
			throw new IllegalStateException("Predicates for character(s) " + field_12340.join(list) + " are missing");
		}
	}
}
