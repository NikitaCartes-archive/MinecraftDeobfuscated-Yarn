package net.minecraft;

import java.util.Comparator;
import java.util.List;

public record class_8384(class_8384.class_8386 total, List<class_8384.class_8385> options) {
	public static record class_8385(class_8373 optionId, class_8384.class_8386 counts) {
		public static final Comparator<class_8384.class_8385> field_44021 = Comparator.comparing(class_8384.class_8385::counts, class_8384.class_8386.field_44023);
	}

	public static record class_8386(int votesCount, int votersCount, class_8375 votes) {
		public static final class_8384.class_8386 field_44022 = new class_8384.class_8386(0, 0, class_8375.field_43991);
		public static final Comparator<class_8384.class_8386> field_44023 = Comparator.comparing(class_8384.class_8386::votesCount);

		public class_8386(class_8375 arg) {
			this(arg.method_50512(), arg.method_50507(), arg);
		}
	}
}
