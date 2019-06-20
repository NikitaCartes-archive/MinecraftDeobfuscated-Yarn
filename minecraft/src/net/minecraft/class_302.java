package net.minecraft;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_302 {
	private static final Logger field_1647 = LogManager.getLogger();
	private final File field_1646;
	private final DataFixer field_1648;
	private final class_748[] field_1644 = new class_748[9];
	private boolean field_1645;

	public class_302(File file, DataFixer dataFixer) {
		this.field_1646 = new File(file, "hotbar.nbt");
		this.field_1648 = dataFixer;

		for (int i = 0; i < 9; i++) {
			this.field_1644[i] = new class_748();
		}
	}

	private void method_1411() {
		try {
			class_2487 lv = class_2507.method_10633(this.field_1646);
			if (lv == null) {
				return;
			}

			if (!lv.method_10573("DataVersion", 99)) {
				lv.method_10569("DataVersion", 1343);
			}

			lv = class_2512.method_10688(this.field_1648, class_4284.field_19215, lv, lv.method_10550("DataVersion"));

			for (int i = 0; i < 9; i++) {
				this.field_1644[i].method_3152(lv.method_10554(String.valueOf(i), 10));
			}
		} catch (Exception var3) {
			field_1647.error("Failed to load creative mode options", (Throwable)var3);
		}
	}

	public void method_1409() {
		try {
			class_2487 lv = new class_2487();
			lv.method_10569("DataVersion", class_155.method_16673().getWorldVersion());

			for (int i = 0; i < 9; i++) {
				lv.method_10566(String.valueOf(i), this.method_1410(i).method_3153());
			}

			class_2507.method_10630(lv, this.field_1646);
		} catch (Exception var3) {
			field_1647.error("Failed to save creative mode options", (Throwable)var3);
		}
	}

	public class_748 method_1410(int i) {
		if (!this.field_1645) {
			this.method_1411();
			this.field_1645 = true;
		}

		return this.field_1644[i];
	}
}
