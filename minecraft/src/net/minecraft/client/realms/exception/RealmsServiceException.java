package net.minecraft.client.realms.exception;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class RealmsServiceException extends Exception {
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

	public String toString() {
		if (this.field_36320 != null) {
			String string = "mco.errorMessage." + this.field_36320.getErrorCode();
			String string2 = I18n.hasTranslation(string) ? I18n.translate(string) : this.field_36320.getErrorMessage();
			return "Realms service error (%d/%d) %s".formatted(this.httpResultCode, this.field_36320.getErrorCode(), string2);
		} else {
			return "Realms service error (%d) %s".formatted(this.httpResultCode, this.field_36319);
		}
	}

	public int method_39980(int i) {
		return this.field_36320 != null ? this.field_36320.getErrorCode() : i;
	}
}
