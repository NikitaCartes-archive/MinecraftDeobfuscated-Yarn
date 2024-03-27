package net.minecraft.client.gui.hud;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public record ChatHudLine(int creationTick, Text content, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator) {
	@Nullable
	public MessageIndicator.Icon getIcon() {
		return this.indicator != null ? this.indicator.icon() : null;
	}

	@Environment(EnvType.CLIENT)
	public static record Visible(int addedTime, OrderedText content, @Nullable MessageIndicator indicator, boolean endOfEntry) {
	}
}
