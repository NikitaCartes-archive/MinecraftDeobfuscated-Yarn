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
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class VideoOptionsScreen extends GameOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("options.videoTitle");
	private static final Text GRAPHICS_FABULOUS_TEXT = Text.translatable("options.graphics.fabulous").formatted(Formatting.ITALIC);
	private static final Text GRAPHICS_WARNING_MESSAGE_TEXT = Text.translatable("options.graphics.warning.message", GRAPHICS_FABULOUS_TEXT, GRAPHICS_FABULOUS_TEXT);
	private static final Text GRAPHICS_WARNING_TITLE_TEXT = Text.translatable("options.graphics.warning.title").formatted(Formatting.RED);
	private static final Text GRAPHICS_WARNING_ACCEPT_TEXT = Text.translatable("options.graphics.warning.accept");
	private static final Text GRAPHICS_WARNING_CANCEL_TEXT = Text.translatable("options.graphics.warning.cancel");
	private OptionListWidget list;
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
			gameOptions.getCloudRenderMode(),
			gameOptions.getFullscreen(),
			gameOptions.getParticles(),
			gameOptions.getMipmapLevels(),
			gameOptions.getEntityShadows(),
			gameOptions.getDistortionEffectScale(),
			gameOptions.getEntityDistanceScaling(),
			gameOptions.getFovEffectScale(),
			gameOptions.getShowAutosaveIndicator(),
			gameOptions.getGlintSpeed(),
			gameOptions.getGlintStrength()
		};
	}

	public VideoOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, TITLE_TEXT);
		this.warningManager = parent.client.getVideoWarningManager();
		this.warningManager.reset();
		if (options.getGraphicsMode().getValue() == GraphicsMode.FABULOUS) {
			this.warningManager.acceptAfterWarnings();
		}

		this.mipmapLevels = options.getMipmapLevels().getValue();
	}

	@Override
	protected void init() {
		this.list = this.addDrawableChild(new OptionListWidget(this.client, this.width, this.height, this));
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
			(prefix, value) -> {
				if (monitor == null) {
					return Text.translatable("options.fullscreen.unavailable");
				} else if (value == -1) {
					return GameOptions.getGenericValueText(prefix, Text.translatable("options.fullscreen.current"));
				} else {
					VideoMode videoMode = monitor.getVideoMode(value);
					return GameOptions.getGenericValueText(
						prefix,
						Text.translatable(
							"options.fullscreen.entry",
							videoMode.getWidth(),
							videoMode.getHeight(),
							videoMode.getRefreshRate(),
							videoMode.getRedBits() + videoMode.getGreenBits() + videoMode.getBlueBits()
						)
					);
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
		super.init();
	}

	@Override
	public void close() {
		this.client.options.write();
		this.client.getWindow().applyVideoMode();
		super.close();
	}

	@Override
	protected void initTabNavigation() {
		super.initTabNavigation();
		this.list.position(this.width, this.layout);
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
					list.add(Text.translatable("options.graphics.warning.renderer", string).formatted(Formatting.GRAY));
				}

				String string2 = this.warningManager.getVendorWarning();
				if (string2 != null) {
					list.add(ScreenTexts.LINE_BREAK);
					list.add(Text.translatable("options.graphics.warning.vendor", string2).formatted(Formatting.GRAY));
				}

				String string3 = this.warningManager.getVersionWarning();
				if (string3 != null) {
					list.add(ScreenTexts.LINE_BREAK);
					list.add(Text.translatable("options.graphics.warning.version", string3).formatted(Formatting.GRAY));
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
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (Screen.hasControlDown()) {
			SimpleOption<Integer> simpleOption = this.gameOptions.getGuiScale();
			if (simpleOption.getCallbacks() instanceof SimpleOption.MaxSuppliableIntCallbacks maxSuppliableIntCallbacks) {
				int i = simpleOption.getValue() + (int)Math.signum(verticalAmount);
				if (i != 0 && i <= maxSuppliableIntCallbacks.maxInclusive()) {
					CyclingButtonWidget<Integer> cyclingButtonWidget = (CyclingButtonWidget<Integer>)this.list.getWidgetFor(simpleOption);
					if (cyclingButtonWidget != null) {
						simpleOption.setValue(i);
						cyclingButtonWidget.setValue(i);
					}

					if (simpleOption.getValue() == i) {
						this.client.onResolutionChanged();
						this.list.setScrollAmount(0.0);
						return true;
					}
				}
			}

			return false;
		} else {
			return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		}
	}
}
