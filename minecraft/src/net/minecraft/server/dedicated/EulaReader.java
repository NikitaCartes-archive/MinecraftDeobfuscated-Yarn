package net.minecraft.server.dedicated;

import com.mojang.logging.LogUtils;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraft.SharedConstants;
import org.slf4j.Logger;

public class EulaReader {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Path eulaFile;
	private final boolean eulaAgreedTo;

	public EulaReader(Path eulaFile) {
		this.eulaFile = eulaFile;
		this.eulaAgreedTo = SharedConstants.isDevelopment || this.checkEulaAgreement();
	}

	private boolean checkEulaAgreement() {
		try {
			InputStream inputStream = Files.newInputStream(this.eulaFile);

			boolean var3;
			try {
				Properties properties = new Properties();
				properties.load(inputStream);
				var3 = Boolean.parseBoolean(properties.getProperty("eula", "false"));
			} catch (Throwable var5) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var4) {
						var5.addSuppressed(var4);
					}
				}

				throw var5;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return var3;
		} catch (Exception var6) {
			LOGGER.warn("Failed to load {}", this.eulaFile);
			this.createEulaFile();
			return false;
		}
	}

	public boolean isEulaAgreedTo() {
		return this.eulaAgreedTo;
	}

	private void createEulaFile() {
		if (!SharedConstants.isDevelopment) {
			try {
				OutputStream outputStream = Files.newOutputStream(this.eulaFile);

				try {
					Properties properties = new Properties();
					properties.setProperty("eula", "false");
					properties.store(outputStream, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://aka.ms/MinecraftEULA).");
				} catch (Throwable var5) {
					if (outputStream != null) {
						try {
							outputStream.close();
						} catch (Throwable var4) {
							var5.addSuppressed(var4);
						}
					}

					throw var5;
				}

				if (outputStream != null) {
					outputStream.close();
				}
			} catch (Exception var6) {
				LOGGER.warn("Failed to save {}", this.eulaFile, var6);
			}
		}
	}
}
