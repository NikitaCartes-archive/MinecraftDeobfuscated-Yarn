package net.minecraft;

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
public class class_8219 extends Screen {
	private static final int field_43137 = 8;
	private static final int field_43138 = 210;
	private static final Text field_43139 = Text.translatable("credits_and_attribution.screen.title");
	private static final Text field_43140 = Text.translatable("credits_and_attribution.button.credits");
	private static final Text field_43141 = Text.translatable("credits_and_attribution.button.attribution");
	private static final Text field_43142 = Text.translatable("credits_and_attribution.button.licenses");
	private final Screen field_43143;
	private final ThreePartsLayoutWidget field_43144 = new ThreePartsLayoutWidget(this);

	public class_8219(Screen screen) {
		super(field_43139);
		this.field_43143 = screen;
	}

	@Override
	protected void init() {
		this.field_43144.addHeader(new TextWidget(this.getTitle(), this.textRenderer));
		GridWidget gridWidget = this.field_43144.addFooter(new GridWidget()).setSpacing(8);
		gridWidget.getMainPositioner().alignHorizontalCenter();
		GridWidget.Adder adder = gridWidget.createAdder(1);
		adder.add(ButtonWidget.builder(field_43140, buttonWidget -> this.method_49739()).width(210).build());
		adder.add(ButtonWidget.builder(field_43141, ConfirmLinkScreen.opening("https://aka.ms/MinecraftJavaAttribution", this, true)).width(210).build());
		adder.add(ButtonWidget.builder(field_43142, ConfirmLinkScreen.opening("https://aka.ms/MinecraftJavaLicenses", this, true)).width(210).build());
		this.field_43144.addBody(ButtonWidget.builder(ScreenTexts.DONE, buttonWidget -> this.close()).build());
		this.field_43144.refreshPositions();
		this.field_43144.forEachChild(this::addDrawableChild);
	}

	@Override
	protected void initTabNavigation() {
		this.field_43144.refreshPositions();
	}

	private void method_49739() {
		this.client.setScreen(new CreditsScreen(false, () -> this.client.setScreen(this)));
	}

	@Override
	public void close() {
		this.client.setScreen(this.field_43143);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
