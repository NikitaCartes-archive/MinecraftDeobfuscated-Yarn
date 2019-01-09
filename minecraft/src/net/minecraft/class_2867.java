package net.minecraft;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Nullable;

public class class_2867 {
	private static final Map<File, class_2861> field_13051 = Maps.<File, class_2861>newHashMap();

	public static synchronized class_2861 method_12440(File file, int i, int j) {
		File file2 = new File(file, "region");
		File file3 = new File(file2, "r." + (i >> 5) + "." + (j >> 5) + ".mca");
		class_2861 lv = (class_2861)field_13051.get(file3);
		if (lv != null) {
			return lv;
		} else {
			if (!file2.exists()) {
				file2.mkdirs();
			}

			if (field_13051.size() >= 256) {
				method_12438();
			}

			class_2861 lv2 = new class_2861(file3);
			field_13051.put(file3, lv2);
			return lv2;
		}
	}

	public static synchronized void method_12438() {
		for (class_2861 lv : field_13051.values()) {
			try {
				if (lv != null) {
					lv.method_12429();
				}
			} catch (IOException var3) {
				var3.printStackTrace();
			}
		}

		field_13051.clear();
	}

	@Nullable
	public static DataInputStream method_12439(File file, int i, int j) {
		class_2861 lv = method_12440(file, i, j);
		return lv.method_12421(i & 31, j & 31);
	}

	@Nullable
	public static DataOutputStream method_12437(File file, int i, int j) {
		class_2861 lv = method_12440(file, i, j);
		return lv.method_12425(i & 31, j & 31);
	}
}
