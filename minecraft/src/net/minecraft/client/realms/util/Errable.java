package net.minecraft.client.realms.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public interface Errable {
	void error(Text errorMessage);

	default void error(Exception exception) {
		if (exception instanceof RealmsServiceException realmsServiceException) {
			this.error(realmsServiceException.error.getText());
		} else {
			this.error(Text.literal(exception.getMessage()));
		}
	}

	default void error(RealmsServiceException exception) {
		this.error(exception.error.getText());
	}
}
