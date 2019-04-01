package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_26 {
	private static final Logger field_136 = LogManager.getLogger();
	private final Map<String, class_18> field_134 = Maps.<String, class_18>newHashMap();
	private final DataFixer field_17663;
	private final File field_17664;

	public class_26(File file, DataFixer dataFixer) {
		this.field_17663 = dataFixer;
		this.field_17664 = file;
	}

	private File method_17922(String string) {
		return new File(this.field_17664, string + ".dat");
	}

	public <T extends class_18> T method_17924(Supplier<T> supplier, String string) {
		T lv = this.method_120(supplier, string);
		if (lv != null) {
			return lv;
		} else {
			T lv2 = (T)supplier.get();
			this.method_123(lv2);
			return lv2;
		}
	}

	@Nullable
	public <T extends class_18> T method_120(Supplier<T> supplier, String string) {
		class_18 lv = (class_18)this.field_134.get(string);
		if (lv == null) {
			try {
				File file = this.method_17922(string);
				if (file.exists()) {
					lv = (class_18)supplier.get();
					class_2487 lv2 = this.method_17923(string, class_155.method_16673().getWorldVersion());
					lv.method_77(lv2.method_10562("data"));
					this.field_134.put(string, lv);
				}
			} catch (Exception var6) {
				field_136.error("Error loading saved data: {}", string, var6);
			}
		}

		return (T)lv;
	}

	public void method_123(class_18 arg) {
		this.field_134.put(arg.method_76(), arg);
	}

	public class_2487 method_17923(String string, int i) throws IOException {
		File file = this.method_17922(string);
		PushbackInputStream pushbackInputStream = new PushbackInputStream(new FileInputStream(file), 2);
		Throwable var5 = null;

		class_2487 var36;
		try {
			class_2487 lv;
			if (this.method_17921(pushbackInputStream)) {
				lv = class_2507.method_10629(pushbackInputStream);
			} else {
				DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);
				Throwable var8 = null;

				try {
					lv = class_2507.method_10627(dataInputStream);
				} catch (Throwable var31) {
					var8 = var31;
					throw var31;
				} finally {
					if (dataInputStream != null) {
						if (var8 != null) {
							try {
								dataInputStream.close();
							} catch (Throwable var30) {
								var8.addSuppressed(var30);
							}
						} else {
							dataInputStream.close();
						}
					}
				}
			}

			int j = lv.method_10573("DataVersion", 99) ? lv.method_10550("DataVersion") : 1343;
			var36 = class_2512.method_10693(this.field_17663, DataFixTypes.SAVED_DATA, lv, j, i);
		} catch (Throwable var33) {
			var5 = var33;
			throw var33;
		} finally {
			if (pushbackInputStream != null) {
				if (var5 != null) {
					try {
						pushbackInputStream.close();
					} catch (Throwable var29) {
						var5.addSuppressed(var29);
					}
				} else {
					pushbackInputStream.close();
				}
			}
		}

		return var36;
	}

	private boolean method_17921(PushbackInputStream pushbackInputStream) throws IOException {
		byte[] bs = new byte[2];
		boolean bl = false;
		int i = pushbackInputStream.read(bs, 0, 2);
		if (i == 2) {
			int j = (bs[1] & 255) << 8 | bs[0] & 255;
			if (j == 35615) {
				bl = true;
			}
		}

		if (i != 0) {
			pushbackInputStream.unread(bs, 0, i);
		}

		return bl;
	}

	public void method_125() {
		for (class_18 lv : this.field_134.values()) {
			lv.method_17919(this.method_17922(lv.method_76()));
		}
	}
}
