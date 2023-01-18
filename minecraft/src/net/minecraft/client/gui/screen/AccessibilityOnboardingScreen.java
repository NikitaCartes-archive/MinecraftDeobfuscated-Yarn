package net.minecraft.client.gui.screen;

import com.mojang.text2speech.Narrator;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class AccessibilityOnboardingScreen extends Screen {
	private static final Text NARRATOR_PROMPT = Text.translatable("accessibility.onboarding.screen.narrator");
	private static final int field_41838 = 4;
	private static final int field_41839 = 16;
	private final RotatingCubeMapRenderer backgroundRenderer = new RotatingCubeMapRenderer(TitleScreen.PANORAMA_CUBE_MAP);
	private final LogoDrawer logoDrawer;
	private final GameOptions gameOptions;
	private boolean narratorPrompted;
	private float narratorPromptTimer;
	@Nullable
	private NarratedMultilineTextWidget textWidget;

	public AccessibilityOnboardingScreen(GameOptions gameOptions) {
		super(Text.translatable("accessibility.onboarding.screen.title"));
		this.gameOptions = gameOptions;
		this.logoDrawer = new LogoDrawer(true);
	}

	@Override
	public void init() {
		SimplePositioningWidget simplePositioningWidget = new SimplePositioningWidget();
		simplePositioningWidget.getMainPositioner().alignTop().margin(4);
		simplePositioningWidget.setDimensions(this.width, this.height - this.yMargin());
		GridWidget gridWidget = simplePositioningWidget.add(new GridWidget());
		gridWidget.getMainPositioner().alignHorizontalCenter().margin(4);
		GridWidget.Adder adder = gridWidget.createAdder(1);
		this.textWidget = new NarratedMultilineTextWidget(this.textRenderer, this.title, this.width);
		adder.add(this.textWidget, adder.copyPositioner().margin(16));
		ClickableWidget clickableWidget = this.gameOptions.getNarrator().createButton(this.gameOptions, 0, 0, 150);
		adder.add(clickableWidget);
		this.setInitialFocus(clickableWidget);
		adder.add(
			ButtonWidget.builder(
					Text.translatable("options.accessibility.title"),
					button -> this.client.setScreen(new AccessibilityOptionsScreen(new TitleScreen(true), this.client.options))
				)
				.build()
		);
		simplePositioningWidget.add(
			ButtonWidget.builder(ScreenTexts.CONTINUE, button -> this.client.setScreen(new TitleScreen(true, this.logoDrawer))).build(),
			simplePositioningWidget.copyPositioner().alignBottom().margin(8)
		);
		simplePositioningWidget.refreshPositions();
		SimplePositioningWidget.setPos(simplePositioningWidget, 0, this.yMargin(), this.width, this.height, 0.5F, 0.0F);
		simplePositioningWidget.forEachChild(this::addDrawableChild);
	}

	private int yMargin() {
		return 90;
	}

	@Override
	public void close() {
		this.client.getNarratorManager().clear();
		this.client.setScreen(new TitleScreen(true, this.logoDrawer));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.tickNarratorPrompt();
		this.backgroundRenderer.render(0.0F, 1.0F);
		fill(matrices, 0, 0, this.width, this.height, -1877995504);
		this.logoDrawer.draw(matrices, this.width, 1.0F);
		if (this.textWidget != null) {
			this.textWidget.render(matrices, mouseX, mouseY, delta);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	private void tickNarratorPrompt() {
		if (!this.narratorPrompted) {
			if (this.narratorPromptTimer < 40.0F) {
				this.narratorPromptTimer++;
			} else {
				Narrator.getNarrator().say(NARRATOR_PROMPT.getString(), true);
				this.narratorPrompted = true;
			}
		}
	}
}
