package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_24 {
	private static final Logger field_133 = LogManager.getLogger();

	static boolean method_234(Path path, DataFixer dataFixer, String string, class_3536 arg) {
		arg.method_15410(0);
		List<File> list = Lists.<File>newArrayList();
		List<File> list2 = Lists.<File>newArrayList();
		List<File> list3 = Lists.<File>newArrayList();
		File file = new File(path.toFile(), string);
		File file2 = class_2874.field_13076.method_12488(file);
		File file3 = class_2874.field_13078.method_12488(file);
		field_133.info("Scanning folders...");
		method_117(file, list);
		if (file2.exists()) {
			method_117(file2, list2);
		}

		if (file3.exists()) {
			method_117(file3, list3);
		}

		int i = list.size() + list2.size() + list3.size();
		field_133.info("Total conversion count is {}", i);
		class_31 lv = class_32.method_17928(path, dataFixer, string);
		class_1969<class_1991, class_1992> lv2 = class_1969.field_9401;
		class_1969<class_2084, class_2088> lv3 = class_1969.field_9402;
		class_1966 lv4;
		if (lv != null && lv.method_153() == class_1942.field_9277) {
			lv4 = lv2.method_8772(lv2.method_8774().method_8782(class_1972.field_9451));
		} else {
			lv4 = lv3.method_8772(lv3.method_8774().method_9002(lv).method_9004(class_2798.field_12769.method_12117()));
		}

		method_113(new File(file, "region"), list, lv4, 0, i, arg);
		method_113(new File(file2, "region"), list2, lv2.method_8772(lv2.method_8774().method_8782(class_1972.field_9461)), list.size(), i, arg);
		method_113(new File(file3, "region"), list3, lv2.method_8772(lv2.method_8774().method_8782(class_1972.field_9411)), list.size() + list2.size(), i, arg);
		lv.method_142(19133);
		if (lv.method_153() == class_1942.field_9268) {
			lv.method_225(class_1942.field_9265);
		}

		method_118(path, string);
		class_29 lv5 = class_32.method_17929(path, dataFixer, string, null);
		lv5.method_136(lv);
		return true;
	}

	private static void method_118(Path path, String string) {
		File file = new File(path.toFile(), string);
		if (!file.exists()) {
			field_133.warn("Unable to create level.dat_mcr backup");
		} else {
			File file2 = new File(file, "level.dat");
			if (!file2.exists()) {
				field_133.warn("Unable to create level.dat_mcr backup");
			} else {
				File file3 = new File(file, "level.dat_mcr");
				if (!file2.renameTo(file3)) {
					field_133.warn("Unable to create level.dat_mcr backup");
				}
			}
		}
	}

	private static void method_113(File file, Iterable<File> iterable, class_1966 arg, int i, int j, class_3536 arg2) {
		for (File file2 : iterable) {
			method_116(file, file2, arg, i, j, arg2);
			i++;
			int k = (int)Math.round(100.0 * (double)i / (double)j);
			arg2.method_15410(k);
		}
	}

	private static void method_116(File file, File file2, class_1966 arg, int i, int j, class_3536 arg2) {
		try {
			String string = file2.getName();

			try (
				class_2861 lv = new class_2861(file2);
				class_2861 lv2 = new class_2861(new File(file, string.substring(0, string.length() - ".mcr".length()) + ".mca"));
			) {
				for (int k = 0; k < 32; k++) {
					for (int l = 0; l < 32; l++) {
						class_1923 lv3 = new class_1923(k, l);
						if (lv.method_12423(lv3) && !lv2.method_12423(lv3)) {
							DataInputStream dataInputStream = lv.method_12421(lv3);
							Throwable lv6 = null;

							class_2487 lv4;
							try {
								if (dataInputStream == null) {
									field_133.warn("Failed to fetch input stream");
									continue;
								}

								lv4 = class_2507.method_10627(dataInputStream);
							} catch (Throwable var100) {
								lv6 = var100;
								throw var100;
							} finally {
								if (dataInputStream != null) {
									if (lv6 != null) {
										try {
											dataInputStream.close();
										} catch (Throwable var97) {
											lv6.addSuppressed(var97);
										}
									} else {
										dataInputStream.close();
									}
								}
							}

							class_2487 lv5 = lv4.method_10562("Level");
							class_2864.class_2865 lv6x = class_2864.method_12433(lv5);
							class_2487 lv7 = new class_2487();
							class_2487 lv8 = new class_2487();
							lv7.method_10566("Level", lv8);
							class_2864.method_12432(lv6x, lv8, arg);
							DataOutputStream dataOutputStream = lv2.method_12425(lv3);
							Throwable var20 = null;

							try {
								class_2507.method_10628(lv7, dataOutputStream);
							} catch (Throwable var98) {
								var20 = var98;
								throw var98;
							} finally {
								if (dataOutputStream != null) {
									if (var20 != null) {
										try {
											dataOutputStream.close();
										} catch (Throwable var96) {
											var20.addSuppressed(var96);
										}
									} else {
										dataOutputStream.close();
									}
								}
							}
						}
					}

					int lx = (int)Math.round(100.0 * (double)(i * 1024) / (double)(j * 1024));
					int m = (int)Math.round(100.0 * (double)((k + 1) * 32 + i * 1024) / (double)(j * 1024));
					if (m > lx) {
						arg2.method_15410(m);
					}
				}
			}
		} catch (IOException var106) {
			var106.printStackTrace();
		}
	}

	private static void method_117(File file, Collection<File> collection) {
		File file2 = new File(file, "region");
		File[] files = file2.listFiles((filex, string) -> string.endsWith(".mcr"));
		if (files != null) {
			Collections.addAll(collection, files);
		}
	}
}
