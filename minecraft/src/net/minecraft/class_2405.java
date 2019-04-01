package net.minecraft;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;

public interface class_2405 {
	HashFunction field_11280 = Hashing.sha1();

	void method_10319(class_2408 arg) throws IOException;

	String method_10321();

	static void method_10320(Gson gson, class_2408 arg, JsonElement jsonElement, Path path) throws IOException {
		String string = gson.toJson(jsonElement);
		String string2 = field_11280.hashUnencodedChars(string).toString();
		if (!Objects.equals(arg.method_10323(path), string2) || !Files.exists(path, new LinkOption[0])) {
			Files.createDirectories(path.getParent());
			BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
			Throwable var7 = null;

			try {
				bufferedWriter.write(string);
			} catch (Throwable var16) {
				var7 = var16;
				throw var16;
			} finally {
				if (bufferedWriter != null) {
					if (var7 != null) {
						try {
							bufferedWriter.close();
						} catch (Throwable var15) {
							var7.addSuppressed(var15);
						}
					} else {
						bufferedWriter.close();
					}
				}
			}
		}

		arg.method_10325(path, string2);
	}
}
