package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_3255 implements class_3262 {
	private static final Logger field_14182 = LogManager.getLogger();
	protected final File field_14181;

	public class_3255(File file) {
		this.field_14181 = file;
	}

	private static String method_14395(class_3264 arg, class_2960 arg2) {
		return String.format("%s/%s/%s", arg.method_14413(), arg2.method_12836(), arg2.method_12832());
	}

	protected static String method_14396(File file, File file2) {
		return file.toURI().relativize(file2.toURI()).getPath();
	}

	@Override
	public InputStream method_14405(class_3264 arg, class_2960 arg2) throws IOException {
		return this.method_14391(method_14395(arg, arg2));
	}

	@Override
	public boolean method_14411(class_3264 arg, class_2960 arg2) {
		return this.method_14393(method_14395(arg, arg2));
	}

	protected abstract InputStream method_14391(String string) throws IOException;

	@Environment(EnvType.CLIENT)
	@Override
	public InputStream method_14410(String string) throws IOException {
		if (!string.contains("/") && !string.contains("\\")) {
			return this.method_14391(string);
		} else {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
	}

	protected abstract boolean method_14393(String string);

	protected void method_14394(String string) {
		field_14182.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", string, this.field_14181);
	}

	@Nullable
	@Override
	public <T> T method_14407(class_3270<T> arg) throws IOException {
		return method_14392(arg, this.method_14391("pack.mcmeta"));
	}

	@Nullable
	public static <T> T method_14392(class_3270<T> arg, InputStream inputStream) {
		JsonObject jsonObject;
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			Throwable var4 = null;

			try {
				jsonObject = class_3518.method_15255(bufferedReader);
			} catch (Throwable var16) {
				var4 = var16;
				throw var16;
			} finally {
				if (bufferedReader != null) {
					if (var4 != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var14) {
							var4.addSuppressed(var14);
						}
					} else {
						bufferedReader.close();
					}
				}
			}
		} catch (JsonParseException | IOException var18) {
			field_14182.error("Couldn't load {} metadata", arg.method_14420(), var18);
			return null;
		}

		if (!jsonObject.has(arg.method_14420())) {
			return null;
		} else {
			try {
				return arg.method_14421(class_3518.method_15296(jsonObject, arg.method_14420()));
			} catch (JsonParseException var15) {
				field_14182.error("Couldn't load {} metadata", arg.method_14420(), var15);
				return null;
			}
		}
	}

	@Override
	public String method_14409() {
		return this.field_14181.getName();
	}
}
