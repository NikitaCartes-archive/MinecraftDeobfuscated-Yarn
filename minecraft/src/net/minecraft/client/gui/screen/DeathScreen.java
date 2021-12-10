package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class DeathScreen extends Screen {
	private int ticksSinceDeath;
	private final Text message;
	private final boolean isHardcore;
	private Text scoreText;
	private final List<ButtonWidget> buttons = Lists.<ButtonWidget>newArrayList();

	public DeathScreen(@Nullable Text message, boolean isHardcore) {
		super(new TranslatableText(isHardcore ? "deathScreen.title.hardcore" : "deathScreen.title"));
		this.message = message;
		this.isHardcore = isHardcore;
	}

	@Override
	protected void init() {
		this.ticksSinceDeath = 0;
		this.buttons.clear();
		this.buttons
			.add(
				this.addDrawableChild(
					new ButtonWidget(
						this.width / 2 - 100,
						this.height / 4 + 72,
						200,
						20,
						this.isHardcore ? new TranslatableText("deathScreen.spectate") : new TranslatableText("deathScreen.respawn"),
						button -> {
							this.client.player.requestRespawn();
							this.client.setScreen(null);
						}
					)
				)
			);
		this.buttons
			.add(
				this.addDrawableChild(
					new ButtonWidget(
						this.width / 2 - 100,
						this.height / 4 + 96,
						200,
						20,
						new TranslatableText("deathScreen.titleScreen"),
						button -> {
							if (this.isHardcore) {
								this.quitLevel();
							} else {
								ConfirmScreen confirmScreen = new ConfirmScreen(
									this::onConfirmQuit,
									new TranslatableText("deathScreen.quit.confirm"),
									LiteralText.EMPTY,
									new TranslatableText("deathScreen.titleScreen"),
									new TranslatableText("deathScreen.respawn")
								);
								this.client.setScreen(confirmScreen);
								confirmScreen.disableButtons(20);
							}
						}
					)
				)
			);

		for (ButtonWidget buttonWidget : this.buttons) {
			buttonWidget.active = false;
		}

		this.scoreText = new TranslatableText("deathScreen.score")
			.append(": ")
			.append(new LiteralText(Integer.toString(this.client.player.getScore())).formatted(Formatting.YELLOW));
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	private void onConfirmQuit(boolean quit) {
		if (quit) {
			this.quitLevel();
		} else {
			this.client.player.requestRespawn();
			this.client.setScreen(null);
		}
	}

	private void quitLevel() {
		if (this.client.world != null) {
			this.client.world.disconnect();
		}

		this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
		this.client.setScreen(new TitleScreen());
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.fillGradient(matrices, 0, 0, this.width, this.height, 1615855616, -1602211792);
		matrices.push();
		matrices.scale(2.0F, 2.0F, 2.0F);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2 / 2, 30, 16777215);
		matrices.pop();
		if (this.message != null) {
			drawCenteredText(matrices, this.textRenderer, this.message, this.width / 2, 85, 16777215);
		}

		drawCenteredText(matrices, this.textRenderer, this.scoreText, this.width / 2, 100, 16777215);
		if (this.message != null && mouseY > 85 && mouseY < 85 + 9) {
			Style style = this.getTextComponentUnderMouse(mouseX);
			this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Nullable
	private Style getTextComponentUnderMouse(int mouseX) {
		if (this.message == null) {
			return null;
		} else {
			int i = this.client.textRenderer.getWidth(this.message);
			int j = this.width / 2 - i / 2;
			int k = this.width / 2 + i / 2;
			return mouseX >= j && mouseX <= k ? this.client.textRenderer.getTextHandler().getStyleAt(this.message, mouseX - j) : null;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.message != null && mouseY > 85.0 && mouseY < (double)(85 + 9)) {
			Style style = this.getTextComponentUnderMouse((int)mouseX);
			if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
				this.handleTextClick(style);
				return false;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		this.ticksSinceDeath++;
		if (this.ticksSinceDeath == 20) {
			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.active = true;
			}
		}
	}
}
