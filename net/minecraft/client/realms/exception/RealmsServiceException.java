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
    public final String field_36319;
    @Nullable
    public final RealmsError field_36320;

    public RealmsServiceException(int httpResultCode, String httpResponseText, RealmsError error) {
        super(httpResponseText);
        this.httpResultCode = httpResultCode;
        this.field_36319 = httpResponseText;
        this.field_36320 = error;
    }

    public RealmsServiceException(int httpResultCode, String httpResponseText) {
        super(httpResponseText);
        this.httpResultCode = httpResultCode;
        this.field_36319 = httpResponseText;
        this.field_36320 = null;
    }

    @Override
    public String toString() {
        if (this.field_36320 != null) {
            String string = "mco.errorMessage." + this.field_36320.getErrorCode();
            String string2 = I18n.hasTranslation(string) ? I18n.translate(string, new Object[0]) : this.field_36320.getErrorMessage();
            return "Realms service error (%d/%d) %s".formatted(this.httpResultCode, this.field_36320.getErrorCode(), string2);
        }
        return "Realms service error (%d) %s".formatted(this.httpResultCode, this.field_36319);
    }

    public int method_39980(int i) {
        return this.field_36320 != null ? this.field_36320.getErrorCode() : i;
    }
}

