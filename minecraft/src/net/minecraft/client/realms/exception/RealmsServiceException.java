package net.minecraft.client.realms.exception;

import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class RealmsServiceException extends Exception {
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

	public String getMessage() {
		if (this.error != null) {
			String string = "mco.errorMessage." + this.error.getErrorCode();
			String string2 = I18n.hasTranslation(string) ? I18n.translate(string) : this.error.getErrorMessage();
			return String.format(Locale.ROOT, "Realms service error (%d/%d) %s", this.httpResultCode, this.error.getErrorCode(), string2);
		} else {
			return String.format(Locale.ROOT, "Realms service error (%d) %s", this.httpResultCode, this.httpResponseText);
		}
	}

	public int getErrorCode(int fallback) {
		return this.error != null ? this.error.getErrorCode() : fallback;
	}
}
