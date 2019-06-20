package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_318 {
	private static final Logger field_1974 = LogManager.getLogger();
	private static final DateFormat field_1973 = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	public static void method_1659(File file, int i, int j, class_276 arg, Consumer<class_2561> consumer) {
		method_1662(file, null, i, j, arg, consumer);
	}

	public static void method_1662(File file, @Nullable String string, int i, int j, class_276 arg, Consumer<class_2561> consumer) {
		class_1011 lv = method_1663(i, j, arg);
		File file2 = new File(file, "screenshots");
		file2.mkdir();
		File file3;
		if (string == null) {
			file3 = method_1660(file2);
		} else {
			file3 = new File(file2, string);
		}

		class_3306.field_14301
			.execute(
				() -> {
					try {
						lv.method_4325(file3);
						class_2561 lvx = new class_2585(file3.getName())
							.method_10854(class_124.field_1073)
							.method_10859(argxx -> argxx.method_10958(new class_2558(class_2558.class_2559.field_11746, file3.getAbsolutePath())));
						consumer.accept(new class_2588("screenshot.success", lvx));
					} catch (Exception var7x) {
						field_1974.warn("Couldn't save screenshot", (Throwable)var7x);
						consumer.accept(new class_2588("screenshot.failure", var7x.getMessage()));
					} finally {
						lv.close();
					}
				}
			);
	}

	public static class_1011 method_1663(int i, int j, class_276 arg) {
		if (GLX.isUsingFBOs()) {
			i = arg.field_1482;
			j = arg.field_1481;
		}

		class_1011 lv = new class_1011(i, j, false);
		if (GLX.isUsingFBOs()) {
			GlStateManager.bindTexture(arg.field_1475);
			lv.method_4327(0, true);
		} else {
			lv.method_4306(true);
		}

		lv.method_4319();
		return lv;
	}

	private static File method_1660(File file) {
		String string = field_1973.format(new Date());
		int i = 1;

		while (true) {
			File file2 = new File(file, string + (i == 1 ? "" : "_" + i) + ".png");
			if (!file2.exists()) {
				return file2;
			}

			i++;
		}
	}
}
