package net.minecraft.client.toast;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AdvancementToast implements Toast {
	private static final Identifier TEXTURE = Identifier.ofVanilla("toast/advancement");
	public static final int DEFAULT_DURATION_MS = 5000;
	private final AdvancementEntry advancement;
	private boolean soundPlayed;
	private Toast.Visibility visibility = Toast.Visibility.HIDE;

	public AdvancementToast(AdvancementEntry advancement) {
		this.advancement = advancement;
	}

	@Override
	public Toast.Visibility getVisibility() {
		return this.visibility;
	}

	@Override
	public void update(ToastManager manager, long time) {
		AdvancementDisplay advancementDisplay = (AdvancementDisplay)this.advancement.value().display().orElse(null);
		if (advancementDisplay == null) {
			this.visibility = Toast.Visibility.HIDE;
		} else {
			if (!this.soundPlayed && time > 0L) {
				this.soundPlayed = true;
				if (advancementDisplay.getFrame() == AdvancementFrame.CHALLENGE) {
					manager.getClient().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
				}
			}

			this.visibility = (double)time >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
		}
	}

	@Override
	public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
		AdvancementDisplay advancementDisplay = (AdvancementDisplay)this.advancement.value().display().orElse(null);
		context.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURE, 0, 0, this.getWidth(), this.getHeight());
		if (advancementDisplay != null) {
			List<OrderedText> list = textRenderer.wrapLines(advancementDisplay.getTitle(), 125);
			int i = advancementDisplay.getFrame() == AdvancementFrame.CHALLENGE ? -30465 : Colors.YELLOW;
			if (list.size() == 1) {
				context.drawText(textRenderer, advancementDisplay.getFrame().getToastText(), 30, 7, i, false);
				context.drawText(textRenderer, (OrderedText)list.get(0), 30, 18, -1, false);
			} else {
				int j = 1500;
				float f = 300.0F;
				if (startTime < 1500L) {
					int k = MathHelper.floor(MathHelper.clamp((float)(1500L - startTime) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
					context.drawText(textRenderer, advancementDisplay.getFrame().getToastText(), 30, 11, i | k, false);
				} else {
					int k = MathHelper.floor(MathHelper.clamp((float)(startTime - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
					int l = this.getHeight() / 2 - list.size() * 9 / 2;

					for (OrderedText orderedText : list) {
						context.drawText(textRenderer, orderedText, 30, l, 16777215 | k, false);
						l += 9;
					}
				}
			}

			context.drawItemWithoutEntity(advancementDisplay.getIcon(), 8, 8);
		}
	}
}
