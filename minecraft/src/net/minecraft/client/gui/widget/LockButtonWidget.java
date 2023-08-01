package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LockButtonWidget extends ButtonWidget {
	private boolean locked;

	public LockButtonWidget(int x, int y, ButtonWidget.PressAction action) {
		super(x, y, 20, 20, Text.translatable("narrator.button.difficulty_lock"), action, DEFAULT_NARRATION_SUPPLIER);
	}

	@Override
	protected MutableText getNarrationMessage() {
		return ScreenTexts.joinSentences(
			super.getNarrationMessage(),
			this.isLocked() ? Text.translatable("narrator.button.difficulty_lock.locked") : Text.translatable("narrator.button.difficulty_lock.unlocked")
		);
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		LockButtonWidget.Icon icon;
		if (!this.active) {
			icon = this.locked ? LockButtonWidget.Icon.LOCKED_DISABLED : LockButtonWidget.Icon.UNLOCKED_DISABLED;
		} else if (this.isSelected()) {
			icon = this.locked ? LockButtonWidget.Icon.LOCKED_HOVER : LockButtonWidget.Icon.UNLOCKED_HOVER;
		} else {
			icon = this.locked ? LockButtonWidget.Icon.LOCKED : LockButtonWidget.Icon.UNLOCKED;
		}

		context.drawGuiTexture(icon.texture, this.getX(), this.getY(), this.width, this.height);
	}

	@Environment(EnvType.CLIENT)
	static enum Icon {
		LOCKED(new Identifier("widget/locked_button")),
		LOCKED_HOVER(new Identifier("widget/locked_button_highlighted")),
		LOCKED_DISABLED(new Identifier("widget/locked_button_disabled")),
		UNLOCKED(new Identifier("widget/unlocked_button")),
		UNLOCKED_HOVER(new Identifier("widget/unlocked_button_highlighted")),
		UNLOCKED_DISABLED(new Identifier("widget/unlocked_button_disabled"));

		final Identifier texture;

		private Icon(Identifier texture) {
			this.texture = texture;
		}
	}
}
