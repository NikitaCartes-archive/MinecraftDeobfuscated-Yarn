package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;

public abstract class class_2867 implements AutoCloseable {
	protected final Long2ObjectLinkedOpenHashMap<class_2861> field_17657 = new Long2ObjectLinkedOpenHashMap<>();
	private final File field_18690;

	protected class_2867(File file) {
		this.field_18690 = file;
	}

	private class_2861 method_12440(class_1923 arg) throws IOException {
		long l = class_1923.method_8331(arg.method_17885(), arg.method_17886());
		class_2861 lv = this.field_17657.getAndMoveToFirst(l);
		if (lv != null) {
			return lv;
		} else {
			if (this.field_17657.size() >= 256) {
				this.field_17657.removeLast().close();
			}

			if (!this.field_18690.exists()) {
				this.field_18690.mkdirs();
			}

			File file = new File(this.field_18690, "r." + arg.method_17885() + "." + arg.method_17886() + ".mca");
			class_2861 lv2 = new class_2861(file);
			this.field_17657.putAndMoveToFirst(l, lv2);
			return lv2;
		}
	}

	@Nullable
	public class_2487 method_17911(class_1923 arg) throws IOException {
		class_2861 lv = this.method_12440(arg);
		DataInputStream dataInputStream = lv.method_12421(arg);
		Throwable var4 = null;

		Object var5;
		try {
			if (dataInputStream != null) {
				return class_2507.method_10627(dataInputStream);
			}

			var5 = null;
		} catch (Throwable var15) {
			var4 = var15;
			throw var15;
		} finally {
			if (dataInputStream != null) {
				if (var4 != null) {
					try {
						dataInputStream.close();
					} catch (Throwable var14) {
						var4.addSuppressed(var14);
					}
				} else {
					dataInputStream.close();
				}
			}
		}

		return (class_2487)var5;
	}

	protected void method_17910(class_1923 arg, class_2487 arg2) throws IOException {
		class_2861 lv = this.method_12440(arg);
		DataOutputStream dataOutputStream = lv.method_12425(arg);
		Throwable var5 = null;

		try {
			class_2507.method_10628(arg2, dataOutputStream);
		} catch (Throwable var14) {
			var5 = var14;
			throw var14;
		} finally {
			if (dataOutputStream != null) {
				if (var5 != null) {
					try {
						dataOutputStream.close();
					} catch (Throwable var13) {
						var5.addSuppressed(var13);
					}
				} else {
					dataOutputStream.close();
				}
			}
		}
	}

	public void close() throws IOException {
		for (class_2861 lv : this.field_17657.values()) {
			lv.close();
		}
	}
}
