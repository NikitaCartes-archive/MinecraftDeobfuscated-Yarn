package net.minecraft;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.function.Supplier;

public class class_3279 implements class_3285 {
	private static final FileFilter field_14217 = file -> {
		boolean bl = file.isFile() && file.getName().endsWith(".zip");
		boolean bl2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
		return bl || bl2;
	};
	private final File field_14218;

	public class_3279(File file) {
		this.field_14218 = file;
	}

	@Override
	public <T extends class_3288> void method_14453(Map<String, T> map, class_3288.class_3290<T> arg) {
		if (!this.field_14218.isDirectory()) {
			this.field_14218.mkdirs();
		}

		File[] files = this.field_14218.listFiles(field_14217);
		if (files != null) {
			for (File file : files) {
				String string = "file/" + file.getName();
				T lv = class_3288.method_14456(string, false, this.method_14432(file), arg, class_3288.class_3289.field_14280);
				if (lv != null) {
					map.put(string, lv);
				}
			}
		}
	}

	private Supplier<class_3262> method_14432(File file) {
		return file.isDirectory() ? () -> new class_3259(file) : () -> new class_3258(file);
	}
}
