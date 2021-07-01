package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ConfirmScreen extends Screen {
	private static final int field_33535 = 90;
	private final Text message;
	private MultilineText messageSplit = MultilineText.EMPTY;
	protected Text yesTranslated;
	protected Text noTranslated;
	private int buttonEnableTimer;
	protected final BooleanConsumer callback;
	private final List<ButtonWidget> buttons = Lists.<ButtonWidget>newArrayList();

	public ConfirmScreen(BooleanConsumer callback, Text title, Text message) {
		this(callback, title, message, ScreenTexts.YES, ScreenTexts.NO);
	}

	public ConfirmScreen(BooleanConsumer callback, Text title, Text message, Text yesTranslated, Text noTranslated) {
		super(title);
		this.callback = callback;
		this.message = message;
		this.yesTranslated = yesTranslated;
		this.noTranslated = noTranslated;
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(super.getNarratedTitle(), this.message);
	}

	@Override
	protected void init() {
		super.init();
		this.messageSplit = MultilineText.create(this.textRenderer, this.message, this.width - 50);
		int i = this.messageSplit.count() * 9;
		int j = MathHelper.clamp(90 + i + 12, this.height / 6 + 96, this.height - 24);
		this.buttons.clear();
		this.addButtons(j);
	}

	protected void addButtons(int y) {
		this.addButton(new ButtonWidget(this.width / 2 - 155, y, 150, 20, this.yesTranslated, button -> this.callback.accept(true)));
		this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, y, 150, 20, this.noTranslated, button -> this.callback.accept(false)));
	}

	protected void addButton(ButtonWidget button) {
		this.buttons.add(this.addDrawableChild(button));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 70, 16777215);
		this.messageSplit.drawCenterWithShadow(matrices, this.width / 2, 90);
		super.render(matrices, mouseX, mouseY, delta);
	}

	public void disableButtons(int ticks) {
		this.buttonEnableTimer = ticks;

		for (ButtonWidget buttonWidget : this.buttons) {
			buttonWidget.active = false;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (--this.buttonEnableTimer == 0) {
			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.active = true;
			}
		}
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.callback.accept(false);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}
}
