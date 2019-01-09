package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class class_1256 {
	private static final Pattern field_5752 = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
	private final File field_5754;
	private final Map<class_2874, List<class_1923>> field_5753;

	public class_1256(File file) {
		this.field_5754 = file;
		Builder<class_2874, List<class_1923>> builder = ImmutableMap.builder();

		for (class_2874 lv : class_2874.method_12482()) {
			builder.put(lv, this.method_5389(lv));
		}

		this.field_5753 = builder.build();
	}

	private List<class_1923> method_5389(class_2874 arg) {
		List<class_1923> list = Lists.<class_1923>newArrayList();
		File file = arg.method_12488(this.field_5754);
		List<File> list2 = this.method_5392(file);

		for (File file2 : list2) {
			list.addAll(this.method_5388(file2));
		}

		list2.sort(File::compareTo);
		return list;
	}

	private List<class_1923> method_5388(File file) {
		List<class_1923> list = Lists.<class_1923>newArrayList();
		class_2861 lv = null;

		try {
			Matcher matcher = field_5752.matcher(file.getName());
			if (!matcher.matches()) {
				return list;
			}

			int i = Integer.parseInt(matcher.group(1)) << 5;
			int j = Integer.parseInt(matcher.group(2)) << 5;
			lv = new class_2861(file);

			for (int k = 0; k < 32; k++) {
				for (int l = 0; l < 32; l++) {
					if (lv.method_12420(k, l)) {
						list.add(new class_1923(k + i, l + j));
					}
				}
			}
		} catch (Throwable var18) {
			return Lists.<class_1923>newArrayList();
		} finally {
			if (lv != null) {
				try {
					lv.method_12429();
				} catch (IOException var17) {
				}
			}
		}

		return list;
	}

	private List<File> method_5392(File file) {
		File file2 = new File(file, "region");
		File[] files = file2.listFiles((filex, string) -> string.endsWith(".mca"));
		return files != null ? Lists.<File>newArrayList(files) : Lists.<File>newArrayList();
	}

	public List<class_1923> method_5391(class_2874 arg) {
		return (List<class_1923>)this.field_5753.get(arg);
	}
}
