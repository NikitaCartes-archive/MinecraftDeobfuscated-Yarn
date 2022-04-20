package net.minecraft.client.gui.screen.option;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DialogScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class VideoOptionsScreen extends GameOptionsScreen {
	private static final Text GRAPHICS_FABULOUS_TEXT = Text.method_43471("options.graphics.fabulous").formatted(Formatting.ITALIC);
	private static final Text GRAPHICS_WARNING_MESSAGE_TEXT = Text.method_43469("options.graphics.warning.message", GRAPHICS_FABULOUS_TEXT, GRAPHICS_FABULOUS_TEXT);
	private static final Text GRAPHICS_WARNING_TITLE_TEXT = Text.method_43471("options.graphics.warning.title").formatted(Formatting.RED);
	private static final Text GRAPHICS_WARNING_ACCEPT_TEXT = Text.method_43471("options.graphics.warning.accept");
	private static final Text GRAPHICS_WARNING_CANCEL_TEXT = Text.method_43471("options.graphics.warning.cancel");
	private ButtonListWidget list;
	private final VideoWarningManager warningManager;
	private final int mipmapLevels;

	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[]{
			gameOptions.getGraphicsMode(),
			gameOptions.getViewDistance(),
			gameOptions.getChunkBuilderMode(),
			gameOptions.getSimulationDistance(),
			gameOptions.getAo(),
			gameOptions.getMaxFps(),
			gameOptions.getEnableVsync(),
			gameOptions.getBobView(),
			gameOptions.getGuiScale(),
			gameOptions.getAttackIndicator(),
			gameOptions.getGamma(),
			gameOptions.getCloudRenderMod(),
			gameOptions.getFullscreen(),
			gameOptions.getParticles(),
			gameOptions.getMipmapLevels(),
			gameOptions.getEntityShadows(),
			gameOptions.getDistortionEffectScale(),
			gameOptions.getEntityDistanceScaling(),
			gameOptions.getFovEffectScale(),
			gameOptions.getShowAutosaveIndicator()
		};
	}

	public VideoOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, Text.method_43471("options.videoTitle"));
		this.warningManager = parent.client.getVideoWarningManager();
		this.warningManager.reset();
		if (options.getGraphicsMode().getValue() == GraphicsMode.FABULOUS) {
			this.warningManager.acceptAfterWarnings();
		}

		this.mipmapLevels = options.getMipmapLevels().getValue();
	}

	@Override
	protected void init() {
		this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		int i = -1;
		Window window = this.client.getWindow();
		Monitor monitor = window.getMonitor();
		int j;
		if (monitor == null) {
			j = -1;
		} else {
			Optional<VideoMode> optional = window.getVideoMode();
			j = (Integer)optional.map(monitor::findClosestVideoModeIndex).orElse(-1);
		}

		SimpleOption<Integer> simpleOption = new SimpleOption<>(
			"options.fullscreen.resolution",
			SimpleOption.emptyTooltip(),
			(text, value) -> {
				if (monitor == null) {
					return Text.method_43471("options.fullscreen.unavailable");
				} else {
					return value == -1
						? GameOptions.getGenericValueText(text, Text.method_43471("options.fullscreen.current"))
						: GameOptions.getGenericValueText(text, Text.method_43470(monitor.getVideoMode(value).toString()));
				}
			},
			new SimpleOption.ValidatingIntSliderCallbacks(-1, monitor != null ? monitor.getVideoModeCount() - 1 : -1),
			j,
			value -> {
				if (monitor != null) {
					window.setVideoMode(value == -1 ? Optional.empty() : Optional.of(monitor.getVideoMode(value)));
				}
			}
		);
		this.list.addSingleOptionEntry(simpleOption);
		this.list.addSingleOptionEntry(this.gameOptions.getBiomeBlendRadius());
		this.list.addAll(getOptions(this.gameOptions));
		this.addSelectableChild(this.list);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, button -> {
			this.client.options.write();
			window.applyVideoMode();
			this.client.setScreen(this.parent);
		}));
	}

	@Override
	public void removed() {
		if (this.gameOptions.getMipmapLevels().getValue() != this.mipmapLevels) {
			this.client.setMipmapLevels(this.gameOptions.getMipmapLevels().getValue());
			this.client.reloadResourcesConcurrently();
		}

		super.removed();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int i = this.gameOptions.getGuiScale().getValue();
		if (super.mouseClicked(mouseX, mouseY, button)) {
			if (this.gameOptions.getGuiScale().getValue() != i) {
				this.client.onResolutionChanged();
			}

			if (this.warningManager.shouldWarn()) {
				List<Text> list = Lists.<Text>newArrayList(GRAPHICS_WARNING_MESSAGE_TEXT, ScreenTexts.LINE_BREAK);
				String string = this.warningManager.getRendererWarning();
				if (string != null) {
					list.add(ScreenTexts.LINE_BREAK);
					list.add(Text.method_43469("options.graphics.warning.renderer", string).formatted(Formatting.GRAY));
				}

				String string2 = this.warningManager.getVendorWarning();
				if (string2 != null) {
					list.add(ScreenTexts.LINE_BREAK);
					list.add(Text.method_43469("options.graphics.warning.vendor", string2).formatted(Formatting.GRAY));
				}

				String string3 = this.warningManager.getVersionWarning();
				if (string3 != null) {
					list.add(ScreenTexts.LINE_BREAK);
					list.add(Text.method_43469("options.graphics.warning.version", string3).formatted(Formatting.GRAY));
				}

				this.client
					.setScreen(new DialogScreen(GRAPHICS_WARNING_TITLE_TEXT, list, ImmutableList.of(new DialogScreen.ChoiceButton(GRAPHICS_WARNING_ACCEPT_TEXT, buttonx -> {
						this.gameOptions.getGraphicsMode().setValue(GraphicsMode.FABULOUS);
						MinecraftClient.getInstance().worldRenderer.reload();
						this.warningManager.acceptAfterWarnings();
						this.client.setScreen(this);
					}), new DialogScreen.ChoiceButton(GRAPHICS_WARNING_CANCEL_TEXT, buttonx -> {
						this.warningManager.cancelAfterWarnings();
						this.client.setScreen(this);
					}))));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		int i = this.gameOptions.getGuiScale().getValue();
		if (super.mouseReleased(mouseX, mouseY, button)) {
			return true;
		} else if (this.list.mouseReleased(mouseX, mouseY, button)) {
			if (this.gameOptions.getGuiScale().getValue() != i) {
				this.client.onResolutionChanged();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.list.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 5, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
		List<OrderedText> list = getHoveredButtonTooltip(this.list, mouseX, mouseY);
		this.renderOrderedTooltip(matrices, list, mouseX, mouseY);
	}
}
