package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum class_2470 {
	field_11467,
	field_11463,
	field_11464,
	field_11465;

	public class_2470 method_10501(class_2470 arg) {
		switch (arg) {
			case field_11464:
				switch (this) {
					case field_11467:
						return field_11464;
					case field_11463:
						return field_11465;
					case field_11464:
						return field_11467;
					case field_11465:
						return field_11463;
				}
			case field_11465:
				switch (this) {
					case field_11467:
						return field_11465;
					case field_11463:
						return field_11467;
					case field_11464:
						return field_11463;
					case field_11465:
						return field_11464;
				}
			case field_11463:
				switch (this) {
					case field_11467:
						return field_11463;
					case field_11463:
						return field_11464;
					case field_11464:
						return field_11465;
					case field_11465:
						return field_11467;
				}
			default:
				return this;
		}
	}

	public class_2350 method_10503(class_2350 arg) {
		if (arg.method_10166() == class_2350.class_2351.field_11052) {
			return arg;
		} else {
			switch (this) {
				case field_11463:
					return arg.method_10170();
				case field_11464:
					return arg.method_10153();
				case field_11465:
					return arg.method_10160();
				default:
					return arg;
			}
		}
	}

	public int method_10502(int i, int j) {
		switch (this) {
			case field_11463:
				return (i + j / 4) % j;
			case field_11464:
				return (i + j / 2) % j;
			case field_11465:
				return (i + j * 3 / 4) % j;
			default:
				return i;
		}
	}

	public static class_2470 method_16548(Random random) {
		class_2470[] lvs = values();
		return lvs[random.nextInt(lvs.length)];
	}

	public static List<class_2470> method_16547(Random random) {
		List<class_2470> list = Lists.<class_2470>newArrayList(values());
		Collections.shuffle(list, random);
		return list;
	}
}
