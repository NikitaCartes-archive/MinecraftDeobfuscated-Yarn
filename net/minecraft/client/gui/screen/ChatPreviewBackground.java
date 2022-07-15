/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
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

    public RenderData computeRenderData(long currentTime, @Nullable Text previewText) {
        long l = currentTime - this.lastRenderTime;
        this.lastRenderTime = currentTime;
        if (previewText != null) {
            return this.computeRenderDataWithText(l, previewText);
        }
        return this.computeRenderDataWithoutText(l);
    }

    private RenderData computeRenderDataWithText(long timeDelta, Text previewText) {
        this.previewText = previewText;
        if (this.currentFadeTime < 200L) {
            this.currentFadeTime = Math.min(this.currentFadeTime + timeDelta, 200L);
        }
        return new RenderData(previewText, ChatPreviewBackground.toAlpha(this.currentFadeTime));
    }

    private RenderData computeRenderDataWithoutText(long timeDelta) {
        if (this.currentFadeTime > 0L) {
            this.currentFadeTime = Math.max(this.currentFadeTime - timeDelta, 0L);
        }
        return this.currentFadeTime > 0L ? new RenderData(this.previewText, ChatPreviewBackground.toAlpha(this.currentFadeTime)) : RenderData.EMPTY;
    }

    private static float toAlpha(long timeDelta) {
        return (float)timeDelta / 200.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public record RenderData(@Nullable Text preview, float alpha) {
        public static final RenderData EMPTY = new RenderData(null, 0.0f);

        @Nullable
        public Text preview() {
            return this.preview;
        }
    }
}

