package net.minecraft;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class class_2973 extends IOException {
	private final List<class_2973.class_2974> field_13371 = Lists.<class_2973.class_2974>newArrayList();
	private final String field_13372;

	public class_2973(String string) {
		this.field_13371.add(new class_2973.class_2974());
		this.field_13372 = string;
	}

	public class_2973(String string, Throwable throwable) {
		super(throwable);
		this.field_13371.add(new class_2973.class_2974());
		this.field_13372 = string;
	}

	public void method_12854(String string) {
		((class_2973.class_2974)this.field_13371.get(0)).method_12858(string);
	}

	public void method_12855(String string) {
		((class_2973.class_2974)this.field_13371.get(0)).field_13373 = string;
		this.field_13371.add(0, new class_2973.class_2974());
	}

	public String getMessage() {
		return "Invalid " + this.field_13371.get(this.field_13371.size() - 1) + ": " + this.field_13372;
	}

	public static class_2973 method_12856(Exception exception) {
		if (exception instanceof class_2973) {
			return (class_2973)exception;
		} else {
			String string = exception.getMessage();
			if (exception instanceof FileNotFoundException) {
				string = "File not found";
			}

			return new class_2973(string, exception);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_2974 {
		private String field_13373;
		private final List<String> field_13374 = Lists.<String>newArrayList();

		private class_2974() {
		}

		private void method_12858(String string) {
			this.field_13374.add(0, string);
		}

		public String method_12857() {
			return StringUtils.join(this.field_13374, "->");
		}

		public String toString() {
			if (this.field_13373 != null) {
				return this.field_13374.isEmpty() ? this.field_13373 : this.field_13373 + " " + this.method_12857();
			} else {
				return this.field_13374.isEmpty() ? "(Unknown file)" : "(Unknown file) " + this.method_12857();
			}
		}
	}
}
