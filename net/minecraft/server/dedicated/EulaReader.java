/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.dedicated;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraft.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EulaReader {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path eulaFile;
    private final boolean eulaAgreedTo;

    public EulaReader(Path eulaFile) {
        this.eulaFile = eulaFile;
        this.eulaAgreedTo = SharedConstants.isDevelopment || this.checkEulaAgreement();
    }

    private boolean checkEulaAgreement() {
        boolean bl;
        block8: {
            InputStream inputStream = Files.newInputStream(this.eulaFile, new OpenOption[0]);
            try {
                Properties properties = new Properties();
                properties.load(inputStream);
                bl = Boolean.parseBoolean(properties.getProperty("eula", "false"));
                if (inputStream == null) break block8;
            } catch (Throwable throwable) {
                try {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (Exception exception) {
                    LOGGER.warn("Failed to load {}", (Object)this.eulaFile);
                    this.createEulaFile();
                    return false;
                }
            }
            inputStream.close();
        }
        return bl;
    }

    public boolean isEulaAgreedTo() {
        return this.eulaAgreedTo;
    }

    private void createEulaFile() {
        if (SharedConstants.isDevelopment) {
            return;
        }
        try (OutputStream outputStream = Files.newOutputStream(this.eulaFile, new OpenOption[0]);){
            Properties properties = new Properties();
            properties.setProperty("eula", "false");
            properties.store(outputStream, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
        } catch (Exception exception) {
            LOGGER.warn("Failed to save {}", (Object)this.eulaFile, (Object)exception);
        }
    }
}

