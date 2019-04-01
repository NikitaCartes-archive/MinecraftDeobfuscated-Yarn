package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public abstract class class_1966 {
	private static final List<class_1959> field_9391 = Lists.<class_1959>newArrayList(
		class_1972.field_9409,
		class_1972.field_9451,
		class_1972.field_9420,
		class_1972.field_9428,
		class_1972.field_9459,
		class_1972.field_9417,
		class_1972.field_9432
	);
	protected final Map<class_3195<?>, Boolean> field_9392 = Maps.<class_3195<?>, Boolean>newHashMap();
	protected final Set<class_2680> field_9390 = Sets.<class_2680>newHashSet();

	protected class_1966() {
	}

	public List<class_1959> method_8759() {
		return field_9391;
	}

	public class_1959 method_8758(class_2338 arg) {
		return this.method_16359(arg.method_10263(), arg.method_10260());
	}

	public abstract class_1959 method_16359(int i, int j);

	public class_1959 method_16360(int i, int j) {
		return this.method_16359(i << 2, j << 2);
	}

	public class_1959[] method_8756(int i, int j, int k, int l) {
		return this.method_8760(i, j, k, l, true);
	}

	public abstract class_1959[] method_8760(int i, int j, int k, int l, boolean bl);

	public abstract Set<class_1959> method_8763(int i, int j, int k);

	@Nullable
	public abstract class_2338 method_8762(int i, int j, int k, List<class_1959> list, Random random);

	public float method_8757(int i, int j) {
		return 0.0F;
	}

	public abstract boolean method_8754(class_3195<?> arg);

	public abstract Set<class_2680> method_8761();
}
