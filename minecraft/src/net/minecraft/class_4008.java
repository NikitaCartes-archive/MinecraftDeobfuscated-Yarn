package net.minecraft;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4008 extends class_4080<List<String>> {
	private static final class_2960 field_17904 = new class_2960("texts/splashes.txt");
	private static final Random field_17905 = new Random();
	private final List<String> field_17906 = Lists.<String>newArrayList();
	private final class_320 field_18934;

	public class_4008(class_320 arg) {
		this.field_18934 = arg;
	}

	protected List<String> method_18176(class_3300 arg, class_3695 arg2) {
		try {
			class_3298 lv = class_310.method_1551().method_1478().method_14486(field_17904);
			Throwable var4 = null;

			List var7;
			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(lv.method_14482(), StandardCharsets.UTF_8));
				Throwable var6 = null;

				try {
					var7 = (List)bufferedReader.lines().map(String::trim).filter(string -> string.hashCode() != 125780783).collect(Collectors.toList());
				} catch (Throwable var32) {
					var6 = var32;
					throw var32;
				} finally {
					if (bufferedReader != null) {
						if (var6 != null) {
							try {
								bufferedReader.close();
							} catch (Throwable var31) {
								var6.addSuppressed(var31);
							}
						} else {
							bufferedReader.close();
						}
					}
				}
			} catch (Throwable var34) {
				var4 = var34;
				throw var34;
			} finally {
				if (lv != null) {
					if (var4 != null) {
						try {
							lv.close();
						} catch (Throwable var30) {
							var4.addSuppressed(var30);
						}
					} else {
						lv.close();
					}
				}
			}

			return var7;
		} catch (IOException var36) {
			return Collections.emptyList();
		}
	}

	protected void method_18175(List<String> list, class_3300 arg, class_3695 arg2) {
		this.field_17906.clear();
		this.field_17906.addAll(list);
	}

	@Nullable
	public String method_18174() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			return "Merry X-mas!";
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			return "Happy new year!";
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			return "OOoooOOOoooo! Spooky!";
		} else if (this.field_17906.isEmpty()) {
			return null;
		} else {
			return this.field_18934 != null && field_17905.nextInt(this.field_17906.size()) == 42
				? this.field_18934.method_1676().toUpperCase(Locale.ROOT) + " IS YOU"
				: (String)this.field_17906.get(field_17905.nextInt(this.field_17906.size()));
		}
	}
}
