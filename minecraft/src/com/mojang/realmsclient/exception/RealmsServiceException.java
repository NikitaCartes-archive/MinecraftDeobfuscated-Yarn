package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class RealmsServiceException extends Exception {
	public final int httpResultCode;
	public final String httpResponseContent;
	public final int errorCode;
	public final String errorMsg;

	public RealmsServiceException(int httpResultCode, String httpResponseText, RealmsError error) {
		super(httpResponseText);
		this.httpResultCode = httpResultCode;
		this.httpResponseContent = httpResponseText;
		this.errorCode = error.getErrorCode();
		this.errorMsg = error.getErrorMessage();
	}

	public RealmsServiceException(int httpResultCode, String httpResponseText, int errorCode, String errorMsg) {
		super(httpResponseText);
		this.httpResultCode = httpResultCode;
		this.httpResponseContent = httpResponseText;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public String toString() {
		if (this.errorCode == -1) {
			return "Realms (" + this.httpResultCode + ") " + this.httpResponseContent;
		} else {
			String string = "mco.errorMessage." + this.errorCode;
			String string2 = RealmsScreen.getLocalizedString(string);
			return (string2.equals(string) ? this.errorMsg : string2) + " - " + this.errorCode;
		}
	}
}
