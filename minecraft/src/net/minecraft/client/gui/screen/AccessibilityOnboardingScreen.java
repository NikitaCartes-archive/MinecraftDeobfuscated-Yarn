package net.minecraft.client.gui.screen;

import com.mojang.text2speech.Narrator;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.option.GameOptions;
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
	private final boolean isNarratorUsable;
	private boolean narratorPrompted;
	private float narratorPromptTimer;
	@Nullable
	private NarratedMultilineTextWidget textWidget;

	public AccessibilityOnboardingScreen(GameOptions gameOptions) {
		super(Text.translatable("accessibility.onboarding.screen.title"));
		this.gameOptions = gameOptions;
		this.logoDrawer = new LogoDrawer(true);
		this.isNarratorUsable = MinecraftClient.getInstance().getNarratorManager().isActive();
	}

	@Override
	public void init() {
		int i = this.yMargin();
		SimplePositioningWidget simplePositioningWidget = new SimplePositioningWidget(this.width, this.height - i);
		simplePositioningWidget.getMainPositioner().alignTop().margin(4);
		DirectionalLayoutWidget directionalLayoutWidget = simplePositioningWidget.add(DirectionalLayoutWidget.vertical());
		directionalLayoutWidget.getMainPositioner().alignHorizontalCenter().margin(2);
		this.textWidget = new NarratedMultilineTextWidget(this.width - 16, this.title, this.textRenderer);
		directionalLayoutWidget.add(this.textWidget, positioner -> positioner.marginBottom(16));
		ClickableWidget clickableWidget = this.gameOptions.getNarrator().createWidget(this.gameOptions, 0, 0, 150);
		clickableWidget.active = this.isNarratorUsable;
		directionalLayoutWidget.add(clickableWidget);
		if (this.isNarratorUsable) {
			this.setInitialFocus(clickableWidget);
		}

		directionalLayoutWidget.add(
			AccessibilityOnboardingButtons.createAccessibilityButton(150, button -> this.setScreen(new AccessibilityOptionsScreen(this, this.client.options)), false)
		);
		directionalLayoutWidget.add(
			AccessibilityOnboardingButtons.createLanguageButton(
				150, button -> this.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager())), false
			)
		);
		simplePositioningWidget.add(
			ButtonWidget.builder(ScreenTexts.CONTINUE, button -> this.close()).build(), simplePositioningWidget.copyPositioner().alignBottom().margin(8)
		);
		simplePositioningWidget.refreshPositions();
		SimplePositioningWidget.setPos(simplePositioningWidget, 0, i, this.width, this.height, 0.5F, 0.0F);
		simplePositioningWidget.forEachChild(this::addDrawableChild);
	}

	private int yMargin() {
		return 90;
	}

	@Override
	public void close() {
		this.setScreen(new TitleScreen(true, this.logoDrawer));
	}

	private void setScreen(Screen screen) {
		this.gameOptions.onboardAccessibility = false;
		this.gameOptions.write();
		Narrator.getNarrator().clear();
		this.client.setScreen(screen);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.tickNarratorPrompt();
		this.logoDrawer.draw(context, this.width, 1.0F);
		if (this.textWidget != null) {
			this.textWidget.render(context, mouseX, mouseY, delta);
		}
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.backgroundRenderer.render(0.0F, 1.0F);
		context.fill(0, 0, this.width, this.height, -1877995504);
	}

	private void tickNarratorPrompt() {
		if (!this.narratorPrompted && this.isNarratorUsable) {
			if (this.narratorPromptTimer < 40.0F) {
				this.narratorPromptTimer++;
			} else if (this.client.isWindowFocused()) {
				Narrator.getNarrator().say(NARRATOR_PROMPT.getString(), true);
				this.narratorPrompted = true;
			}
		}
	}
}
