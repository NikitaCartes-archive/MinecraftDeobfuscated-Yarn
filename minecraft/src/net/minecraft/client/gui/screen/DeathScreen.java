package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DeathScreen extends Screen {
	private static final Identifier DRAFT_REPORT_ICON_TEXTURE = Identifier.ofVanilla("icon/draft_report");
	private int ticksSinceDeath;
	private final Text message;
	private final boolean isHardcore;
	private Text scoreText;
	private final List<ButtonWidget> buttons = Lists.<ButtonWidget>newArrayList();
	@Nullable
	private ButtonWidget titleScreenButton;

	public DeathScreen(@Nullable Text message, boolean isHardcore) {
		super(Text.translatable(isHardcore ? "deathScreen.title.hardcore" : "deathScreen.title"));
		this.message = message;
		this.isHardcore = isHardcore;
	}

	@Override
	protected void init() {
		this.ticksSinceDeath = 0;
		this.buttons.clear();
		Text text = this.isHardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn");
		this.buttons.add(this.addDrawableChild(ButtonWidget.builder(text, button -> {
			this.client.player.requestRespawn();
			button.active = false;
		}).dimensions(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build()));
		this.titleScreenButton = this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("deathScreen.titleScreen"),
					button -> this.client.getAbuseReportContext().tryShowDraftScreen(this.client, this, this::onTitleScreenButtonClicked, true)
				)
				.dimensions(this.width / 2 - 100, this.height / 4 + 96, 200, 20)
				.build()
		);
		this.buttons.add(this.titleScreenButton);
		this.setButtonsActive(false);
		this.scoreText = Text.translatable("deathScreen.score.value", Text.literal(Integer.toString(this.client.player.getScore())).formatted(Formatting.YELLOW));
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	private void onTitleScreenButtonClicked() {
		if (this.isHardcore) {
			this.quitLevel();
		} else {
			ConfirmScreen confirmScreen = new DeathScreen.TitleScreenConfirmScreen(confirmed -> {
				if (confirmed) {
					this.quitLevel();
				} else {
					this.client.player.requestRespawn();
					this.client.setScreen(null);
				}
			}, Text.translatable("deathScreen.quit.confirm"), ScreenTexts.EMPTY, Text.translatable("deathScreen.titleScreen"), Text.translatable("deathScreen.respawn"));
			this.client.setScreen(confirmScreen);
			confirmScreen.disableButtons(20);
		}
	}

	private void quitLevel() {
		if (this.client.world != null) {
			this.client.world.disconnect();
		}

		this.client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
		this.client.setScreen(new TitleScreen());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.getMatrices().push();
		context.getMatrices().scale(2.0F, 2.0F, 2.0F);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2 / 2, 30, 16777215);
		context.getMatrices().pop();
		if (this.message != null) {
			context.drawCenteredTextWithShadow(this.textRenderer, this.message, this.width / 2, 85, 16777215);
		}

		context.drawCenteredTextWithShadow(this.textRenderer, this.scoreText, this.width / 2, 100, 16777215);
		if (this.message != null && mouseY > 85 && mouseY < 85 + 9) {
			Style style = this.getTextComponentUnderMouse(mouseX);
			context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
		}

		if (this.titleScreenButton != null && this.client.getAbuseReportContext().hasDraft()) {
			context.drawGuiTexture(
				RenderLayer::getGuiTextured,
				DRAFT_REPORT_ICON_TEXTURE,
				this.titleScreenButton.getX() + this.titleScreenButton.getWidth() - 17,
				this.titleScreenButton.getY() + 3,
				15,
				15
			);
		}
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		fillBackgroundGradient(context, this.width, this.height);
	}

	static void fillBackgroundGradient(DrawContext context, int width, int height) {
		context.fillGradient(0, 0, width, height, 1615855616, -1602211792);
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
			this.setButtonsActive(true);
		}
	}

	private void setButtonsActive(boolean active) {
		for (ButtonWidget buttonWidget : this.buttons) {
			buttonWidget.active = active;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class TitleScreenConfirmScreen extends ConfirmScreen {
		public TitleScreenConfirmScreen(BooleanConsumer booleanConsumer, Text text, Text text2, Text text3, Text text4) {
			super(booleanConsumer, text, text2, text3, text4);
		}

		@Override
		public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
			DeathScreen.fillBackgroundGradient(context, this.width, this.height);
		}
	}
}
