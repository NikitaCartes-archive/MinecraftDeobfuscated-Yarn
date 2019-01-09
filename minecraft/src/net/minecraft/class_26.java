package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_26 {
	private static final Logger field_136 = LogManager.getLogger();
	private final class_2874 field_135;
	private final Map<String, class_18> field_134 = Maps.<String, class_18>newHashMap();
	private final Object2IntMap<String> field_137 = new Object2IntOpenHashMap<>();
	@Nullable
	private final class_30 field_138;

	public class_26(class_2874 arg, @Nullable class_30 arg2) {
		this.field_135 = arg;
		this.field_138 = arg2;
		this.field_137.defaultReturnValue(-1);
	}

	@Nullable
	public <T extends class_18> T method_120(Function<String, T> function, String string) {
		class_18 lv = (class_18)this.field_134.get(string);
		if (lv == null && this.field_138 != null) {
			try {
				File file = this.field_138.method_138(this.field_135, string);
				if (file != null && file.exists()) {
					lv = (class_18)function.apply(string);
					lv.method_77(method_119(this.field_138, this.field_135, string, class_155.method_16673().getWorldVersion()).method_10562("data"));
					this.field_134.put(string, lv);
				}
			} catch (Exception var5) {
				field_136.error("Error loading saved data: {}", string, var5);
			}
		}

		return (T)lv;
	}

	public void method_123(String string, class_18 arg) {
		this.field_134.put(string, arg);
	}

	public void method_122() {
		try {
			this.field_137.clear();
			if (this.field_138 == null) {
				return;
			}

			File file = this.field_138.method_138(this.field_135, "idcounts");
			if (file != null && file.exists()) {
				DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
				class_2487 lv = class_2507.method_10627(dataInputStream);
				dataInputStream.close();

				for (String string : lv.method_10541()) {
					if (lv.method_10573(string, 99)) {
						this.field_137.put(string, lv.method_10550(string));
					}
				}
			}
		} catch (Exception var6) {
			field_136.error("Could not load aux values", (Throwable)var6);
		}
	}

	public int method_124(String string) {
		int i = this.field_137.getInt(string) + 1;
		this.field_137.put(string, i);
		if (this.field_138 == null) {
			return i;
		} else {
			try {
				File file = this.field_138.method_138(this.field_135, "idcounts");
				if (file != null) {
					class_2487 lv = new class_2487();

					for (Entry<String> entry : this.field_137.object2IntEntrySet()) {
						lv.method_10569((String)entry.getKey(), entry.getIntValue());
					}

					DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
					class_2507.method_10628(lv, dataOutputStream);
					dataOutputStream.close();
				}
			} catch (Exception var7) {
				field_136.error("Could not get free aux value {}", string, var7);
			}

			return i;
		}
	}

	public static class_2487 method_119(class_30 arg, class_2874 arg2, String string, int i) throws IOException {
		File file = arg.method_138(arg2, string);
		FileInputStream fileInputStream = new FileInputStream(file);
		Throwable var6 = null;

		class_2487 var9;
		try {
			class_2487 lv = class_2507.method_10629(fileInputStream);
			int j = lv.method_10573("DataVersion", 99) ? lv.method_10550("DataVersion") : 1343;
			var9 = class_2512.method_10693(arg.method_130(), DataFixTypes.SAVED_DATA, lv, j, i);
		} catch (Throwable var18) {
			var6 = var18;
			throw var18;
		} finally {
			if (fileInputStream != null) {
				if (var6 != null) {
					try {
						fileInputStream.close();
					} catch (Throwable var17) {
						var6.addSuppressed(var17);
					}
				} else {
					fileInputStream.close();
				}
			}
		}

		return var9;
	}

	public void method_125() {
		if (this.field_138 != null) {
			for (class_18 lv : this.field_134.values()) {
				if (lv.method_79()) {
					this.method_121(lv);
					lv.method_78(false);
				}
			}
		}
	}

	private void method_121(class_18 arg) {
		if (this.field_138 != null) {
			try {
				File file = this.field_138.method_138(this.field_135, arg.method_76());
				if (file != null) {
					class_2487 lv = new class_2487();
					lv.method_10566("data", arg.method_75(new class_2487()));
					lv.method_10569("DataVersion", class_155.method_16673().getWorldVersion());
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					class_2507.method_10634(lv, fileOutputStream);
					fileOutputStream.close();
				}
			} catch (Exception var5) {
				field_136.error("Could not save data {}", arg, var5);
			}
		}
	}
}
