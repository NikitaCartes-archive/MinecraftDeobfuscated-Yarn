package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class CreditsAndAttributionScreen extends Screen {
	private static final int SPACING = 8;
	private static final int BUTTON_WIDTH = 210;
	private static final Text TITLE = Text.translatable("credits_and_attribution.screen.title");
	private static final Text CREDITS_TEXT = Text.translatable("credits_and_attribution.button.credits");
	private static final Text ATTRIBUTION_TEXT = Text.translatable("credits_and_attribution.button.attribution");
	private static final Text LICENSE_TEXT = Text.translatable("credits_and_attribution.button.licenses");
	private final Screen parent;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

	public CreditsAndAttributionScreen(Screen parent) {
		super(TITLE);
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.layout.addHeader(new TextWidget(this.getTitle(), this.textRenderer));
		GridWidget gridWidget = this.layout.addFooter(new GridWidget()).setSpacing(8);
		gridWidget.getMainPositioner().alignHorizontalCenter();
		GridWidget.Adder adder = gridWidget.createAdder(1);
		adder.add(ButtonWidget.builder(CREDITS_TEXT, button -> this.openCredits()).width(210).build());
		adder.add(ButtonWidget.builder(ATTRIBUTION_TEXT, ConfirmLinkScreen.opening("https://aka.ms/MinecraftJavaAttribution", this, true)).width(210).build());
		adder.add(ButtonWidget.builder(LICENSE_TEXT, ConfirmLinkScreen.opening("https://aka.ms/MinecraftJavaLicenses", this, true)).width(210).build());
		this.layout.addBody(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).build());
		this.layout.refreshPositions();
		this.layout.forEachChild(this::addDrawableChild);
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
	}

	private void openCredits() {
		this.client.setScreen(new CreditsScreen(false, () -> this.client.setScreen(this)));
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
