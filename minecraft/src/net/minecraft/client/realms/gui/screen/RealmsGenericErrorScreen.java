package net.minecraft.client.realms.gui.screen;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RealmsGenericErrorScreen extends RealmsScreen {
	private final Screen parent;
	private final Pair<Text, Text> errorMessages;
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

	private static Pair<Text, Text> getErrorMessages(RealmsServiceException exception) {
		if (exception.error == null) {
			return Pair.of(Text.translatable("mco.errorMessage.realmsService", exception.httpResultCode), Text.literal(exception.httpResponseText));
		} else {
			String string = "mco.errorMessage." + exception.error.getErrorCode();
			return Pair.of(
				Text.translatable("mco.errorMessage.realmsService.realmsError", exception.error),
				(Text)(I18n.hasTranslation(string) ? Text.translatable(string) : Text.of(exception.error.getErrorMessage()))
			);
		}
	}

	private static Pair<Text, Text> getErrorMessages(Text description) {
		return Pair.of(Text.translatable("mco.errorMessage.generic"), description);
	}

	private static Pair<Text, Text> getErrorMessages(Text title, Text description) {
		return Pair.of(title, description);
	}

	@Override
	public void init() {
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.OK, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 - 100, this.height - 52, 200, 20).build()
		);
		this.description = MultilineText.create(this.textRenderer, this.errorMessages.getSecond(), this.width * 3 / 4);
	}

	@Override
	public Text getNarratedTitle() {
		return Text.empty().append(this.errorMessages.getFirst()).append(": ").append(this.errorMessages.getSecond());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		context.drawCenteredTextWithShadow(this.textRenderer, this.errorMessages.getFirst(), this.width / 2, 80, 16777215);
		this.description.drawCenterWithShadow(context, this.width / 2, 100, 9, 16711680);
		super.render(context, mouseX, mouseY, delta);
	}
}
