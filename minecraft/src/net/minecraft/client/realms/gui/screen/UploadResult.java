package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class UploadResult {
	public final int statusCode;
	public final String errorMessage;

	UploadResult(int i, String string) {
		this.statusCode = i;
		this.errorMessage = string;
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private int statusCode = -1;
		private String errorMessage;

		public UploadResult.Builder withStatusCode(int statusCode) {
			this.statusCode = statusCode;
			return this;
		}

		public UploadResult.Builder withErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
			return this;
		}

		public UploadResult build() {
			return new UploadResult(this.statusCode, this.errorMessage);
		}
	}
}
