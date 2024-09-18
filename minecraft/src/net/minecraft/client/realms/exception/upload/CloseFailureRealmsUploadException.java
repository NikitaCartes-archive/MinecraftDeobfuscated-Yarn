package net.minecraft.client.realms.exception.upload;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.exception.RealmsUploadException;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class CloseFailureRealmsUploadException extends RealmsUploadException {
	@Override
	public Text getStatus() {
		return Text.translatable("mco.upload.close.failure");
	}
}
