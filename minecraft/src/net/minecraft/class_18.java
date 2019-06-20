package net.minecraft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_18 {
	private static final Logger field_17661 = LogManager.getLogger();
	private final String field_71;
	private boolean field_72;

	public class_18(String string) {
		this.field_71 = string;
	}

	public abstract void method_77(class_2487 arg);

	public abstract class_2487 method_75(class_2487 arg);

	public void method_80() {
		this.method_78(true);
	}

	public void method_78(boolean bl) {
		this.field_72 = bl;
	}

	public boolean method_79() {
		return this.field_72;
	}

	public String method_76() {
		return this.field_71;
	}

	public void method_17919(File file) {
		if (this.method_79()) {
			class_2487 lv = new class_2487();
			lv.method_10566("data", this.method_75(new class_2487()));
			lv.method_10569("DataVersion", class_155.method_16673().getWorldVersion());

			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				Throwable var4 = null;

				try {
					class_2507.method_10634(lv, fileOutputStream);
				} catch (Throwable var14) {
					var4 = var14;
					throw var14;
				} finally {
					if (fileOutputStream != null) {
						if (var4 != null) {
							try {
								fileOutputStream.close();
							} catch (Throwable var13) {
								var4.addSuppressed(var13);
							}
						} else {
							fileOutputStream.close();
						}
					}
				}
			} catch (IOException var16) {
				field_17661.error("Could not save data {}", this, var16);
			}

			this.method_78(false);
		}
	}
}
