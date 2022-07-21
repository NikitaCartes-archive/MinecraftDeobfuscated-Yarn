package net.minecraft.client.gui.screen;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ChatPreviewBackground {
	private static final long MAX_FADE_TIME = 200L;
	@Nullable
	private Text previewText;
	private long currentFadeTime;
	private long lastRenderTime;

	public void init(long currentTime) {
		this.previewText = null;
		this.currentFadeTime = 0L;
		this.lastRenderTime = currentTime;
	}

	public ChatPreviewBackground.RenderData computeRenderData(long currentTime, @Nullable Text previewText) {
		long l = currentTime - this.lastRenderTime;
		this.lastRenderTime = currentTime;
		return previewText != null ? this.computeRenderDataWithText(l, previewText) : this.computeRenderDataWithoutText(l);
	}

	private ChatPreviewBackground.RenderData computeRenderDataWithText(long timeDelta, Text previewText) {
		this.previewText = previewText;
		if (this.currentFadeTime < 200L) {
			this.currentFadeTime = Math.min(this.currentFadeTime + timeDelta, 200L);
		}

		return new ChatPreviewBackground.RenderData(previewText, toAlpha(this.currentFadeTime));
	}

	private ChatPreviewBackground.RenderData computeRenderDataWithoutText(long timeDelta) {
		if (this.currentFadeTime > 0L) {
			this.currentFadeTime = Math.max(this.currentFadeTime - timeDelta, 0L);
		}

		return this.currentFadeTime > 0L
			? new ChatPreviewBackground.RenderData(this.previewText, toAlpha(this.currentFadeTime))
			: ChatPreviewBackground.RenderData.EMPTY;
	}

	private static float toAlpha(long timeDelta) {
		return (float)timeDelta / 200.0F;
	}

	@Environment(EnvType.CLIENT)
	public static record RenderData(@Nullable Text preview, float alpha) {
		public static final ChatPreviewBackground.RenderData EMPTY = new ChatPreviewBackground.RenderData(null, 0.0F);
	}
}
