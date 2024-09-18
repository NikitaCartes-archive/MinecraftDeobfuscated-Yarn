package net.minecraft.client.realms.exception.upload;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.exception.RealmsUploadException;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class FailedRealmsUploadException extends RealmsUploadException {
	private final Text errorMessage;

	public FailedRealmsUploadException(Text errorMessage) {
		this.errorMessage = errorMessage;
	}

	public FailedRealmsUploadException(String errorMessage) {
		this(Text.literal(errorMessage));
	}

	@Override
	public Text getStatus() {
		return Text.translatable("mco.upload.failed", this.errorMessage);
	}
}
