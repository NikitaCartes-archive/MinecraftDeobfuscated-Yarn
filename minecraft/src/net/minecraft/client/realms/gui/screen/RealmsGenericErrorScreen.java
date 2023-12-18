package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

@Environment(EnvType.CLIENT)
public class RealmsGenericErrorScreen extends RealmsScreen {
	private final Screen parent;
	private final RealmsGenericErrorScreen.ErrorMessages errorMessages;
	private MultilineText description = MultilineText.EMPTY;

	public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, Screen parent) {
		super(NarratorManager.EMPTY);
		this.parent = parent;
		this.errorMessages = getErrorMessages(realmsServiceException);
	}

	public RealmsGenericErrorScreen(Text description, Screen parent) {
		super(NarratorManager.EMPTY);
		this.parent = parent;
		this.errorMessages = getErrorMessages(description);
	}

	public RealmsGenericErrorScreen(Text title, Text description, Screen parent) {
		super(NarratorManager.EMPTY);
		this.parent = parent;
		this.errorMessages = getErrorMessages(title, description);
	}

	private static RealmsGenericErrorScreen.ErrorMessages getErrorMessages(RealmsServiceException exception) {
		RealmsError realmsError = exception.error;
		return getErrorMessages(Text.translatable("mco.errorMessage.realmsService.realmsError", realmsError.getErrorCode()), realmsError.getText());
	}

	private static RealmsGenericErrorScreen.ErrorMessages getErrorMessages(Text description) {
		return getErrorMessages(Text.translatable("mco.errorMessage.generic"), description);
	}

	private static RealmsGenericErrorScreen.ErrorMessages getErrorMessages(Text title, Text description) {
		return new RealmsGenericErrorScreen.ErrorMessages(title, description);
	}

	@Override
	public void init() {
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.OK, button -> this.close()).dimensions(this.width / 2 - 100, this.height - 52, 200, 20).build());
		this.description = MultilineText.create(this.textRenderer, this.errorMessages.detail, this.width * 3 / 4);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Override
	public Text getNarratedTitle() {
		return Text.empty().append(this.errorMessages.title).append(": ").append(this.errorMessages.detail);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.errorMessages.title, this.width / 2, 80, Colors.WHITE);
		this.description.drawCenterWithShadow(context, this.width / 2, 100, 9, -2142128);
	}

	@Environment(EnvType.CLIENT)
	static record ErrorMessages(Text title, Text detail) {
	}
}
