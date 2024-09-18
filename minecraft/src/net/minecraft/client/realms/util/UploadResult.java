package net.minecraft.client.realms.util;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class UploadResult {
	public final int statusCode;
	@Nullable
	public final String errorMessage;

	UploadResult(int statusCode, String errorMessage) {
		this.statusCode = statusCode;
		this.errorMessage = errorMessage;
	}

	@Nullable
	public String getErrorMessage() {
		if (this.statusCode >= 200 && this.statusCode < 300) {
			return null;
		} else {
			return this.statusCode == 400 && this.errorMessage != null ? this.errorMessage : String.valueOf(this.statusCode);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private int statusCode = -1;
		private String errorMessage;

		public UploadResult.Builder withStatusCode(int statusCode) {
			this.statusCode = statusCode;
			return this;
		}

		public UploadResult.Builder withErrorMessage(@Nullable String errorMessage) {
			this.errorMessage = errorMessage;
			return this;
		}

		public UploadResult build() {
			return new UploadResult(this.statusCode, this.errorMessage);
		}
	}
}
