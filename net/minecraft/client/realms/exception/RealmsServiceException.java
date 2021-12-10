/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.exception;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsServiceException
extends Exception {
    public final int httpResultCode;
    public final String httpResponseText;
    @Nullable
    public final RealmsError error;

    public RealmsServiceException(int httpResultCode, String httpResponseText, RealmsError error) {
        super(httpResponseText);
        this.httpResultCode = httpResultCode;
        this.httpResponseText = httpResponseText;
        this.error = error;
    }

    public RealmsServiceException(int httpResultCode, String httpResponseText) {
        super(httpResponseText);
        this.httpResultCode = httpResultCode;
        this.httpResponseText = httpResponseText;
        this.error = null;
    }

    @Override
    public String toString() {
        if (this.error != null) {
            String string = "mco.errorMessage." + this.error.getErrorCode();
            String string2 = I18n.hasTranslation(string) ? I18n.translate(string, new Object[0]) : this.error.getErrorMessage();
            return "Realms service error (%d/%d) %s".formatted(this.httpResultCode, this.error.getErrorCode(), string2);
        }
        return "Realms service error (%d) %s".formatted(this.httpResultCode, this.httpResponseText);
    }

    public int getErrorCode(int fallback) {
        return this.error != null ? this.error.getErrorCode() : fallback;
    }
}

