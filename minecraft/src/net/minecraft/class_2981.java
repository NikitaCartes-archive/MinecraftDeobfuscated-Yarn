package net.minecraft;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2981 {
	private static final Logger field_13381 = LogManager.getLogger();
	private final Path field_13380;
	private final boolean field_13379;

	public class_2981(Path path) {
		this.field_13380 = path;
		this.field_13379 = class_155.field_1125 || this.method_12867();
	}

	private boolean method_12867() {
		try {
			InputStream inputStream = Files.newInputStream(this.field_13380);
			Throwable var2 = null;

			boolean var4;
			try {
				Properties properties = new Properties();
				properties.load(inputStream);
				var4 = Boolean.parseBoolean(properties.getProperty("eula", "false"));
			} catch (Throwable var14) {
				var2 = var14;
				throw var14;
			} finally {
				if (inputStream != null) {
					if (var2 != null) {
						try {
							inputStream.close();
						} catch (Throwable var13) {
							var2.addSuppressed(var13);
						}
					} else {
						inputStream.close();
					}
				}
			}

			return var4;
		} catch (Exception var16) {
			field_13381.warn("Failed to load {}", this.field_13380);
			this.method_12868();
			return false;
		}
	}

	public boolean method_12866() {
		return this.field_13379;
	}

	private void method_12868() {
		if (!class_155.field_1125) {
			try {
				OutputStream outputStream = Files.newOutputStream(this.field_13380);
				Throwable var2 = null;

				try {
					Properties properties = new Properties();
					properties.setProperty("eula", "false");
					properties.store(
						outputStream,
						"By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula)."
					);
				} catch (Throwable var12) {
					var2 = var12;
					throw var12;
				} finally {
					if (outputStream != null) {
						if (var2 != null) {
							try {
								outputStream.close();
							} catch (Throwable var11) {
								var2.addSuppressed(var11);
							}
						} else {
							outputStream.close();
						}
					}
				}
			} catch (Exception var14) {
				field_13381.warn("Failed to save {}", this.field_13380, var14);
			}
		}
	}
}
