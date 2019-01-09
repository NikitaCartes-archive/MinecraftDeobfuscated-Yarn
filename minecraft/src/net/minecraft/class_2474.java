package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_2474<T> implements class_2405 {
	private static final Logger field_11479 = LogManager.getLogger();
	private static final Gson field_11480 = new GsonBuilder().setPrettyPrinting().create();
	protected final class_2403 field_11483;
	protected final class_2378<T> field_11482;
	protected final Map<class_3494<T>, class_3494.class_3495<T>> field_11481 = Maps.<class_3494<T>, class_3494.class_3495<T>>newLinkedHashMap();

	protected class_2474(class_2403 arg, class_2378<T> arg2) {
		this.field_11483 = arg;
		this.field_11482 = arg2;
	}

	protected abstract void method_10514();

	@Override
	public void method_10319(class_2408 arg) throws IOException {
		this.field_11481.clear();
		this.method_10514();
		class_3503<T> lv = new class_3503<>(argx -> false, argx -> null, "", false, "generated");

		for (Entry<class_3494<T>, class_3494.class_3495<T>> entry : this.field_11481.entrySet()) {
			class_2960 lv2 = ((class_3494)entry.getKey()).method_15143();
			if (!((class_3494.class_3495)entry.getValue()).method_15152(lv::method_15193)) {
				throw new UnsupportedOperationException("Unsupported referencing of tags!");
			}

			class_3494<T> lv3 = ((class_3494.class_3495)entry.getValue()).method_15144(lv2);
			JsonObject jsonObject = lv3.method_15140(this.field_11482::method_10221);
			Path path = this.method_10510(lv2);
			lv.method_15190(lv3);
			this.method_10511(lv);

			try {
				String string = field_11480.toJson((JsonElement)jsonObject);
				String string2 = field_11280.hashUnencodedChars(string).toString();
				if (!Objects.equals(arg.method_10323(path), string2) || !Files.exists(path, new LinkOption[0])) {
					Files.createDirectories(path.getParent());
					BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
					Throwable var12 = null;

					try {
						bufferedWriter.write(string);
					} catch (Throwable var22) {
						var12 = var22;
						throw var22;
					} finally {
						if (bufferedWriter != null) {
							if (var12 != null) {
								try {
									bufferedWriter.close();
								} catch (Throwable var21) {
									var12.addSuppressed(var21);
								}
							} else {
								bufferedWriter.close();
							}
						}
					}
				}

				arg.method_10325(path, string2);
			} catch (IOException var24) {
				field_11479.error("Couldn't save tags to {}", path, var24);
			}
		}
	}

	protected abstract void method_10511(class_3503<T> arg);

	protected abstract Path method_10510(class_2960 arg);

	protected class_3494.class_3495<T> method_10512(class_3494<T> arg) {
		return (class_3494.class_3495<T>)this.field_11481.computeIfAbsent(arg, argx -> class_3494.class_3495.method_15146());
	}
}
