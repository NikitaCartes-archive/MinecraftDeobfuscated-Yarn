package net.minecraft.client.gui.screen.option;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;

@Environment(EnvType.CLIENT)
public class OptionsScreen extends Screen {
	private static final Text TITLE_TEXT = Text.translatable("options.title");
	private static final Text SKIN_CUSTOMIZATION_TEXT = Text.translatable("options.skinCustomisation");
	private static final Text SOUNDS_TEXT = Text.translatable("options.sounds");
	private static final Text VIDEO_TEXT = Text.translatable("options.video");
	private static final Text CONTROL_TEXT = Text.translatable("options.controls");
	private static final Text LANGUAGE_TEXT = Text.translatable("options.language");
	private static final Text CHAT_TEXT = Text.translatable("options.chat");
	private static final Text RESOURCE_PACK_TEXT = Text.translatable("options.resourcepack");
	private static final Text ACCESSIBILITY_TEXT = Text.translatable("options.accessibility");
	private static final Text TELEMETRY_TEXT = Text.translatable("options.telemetry");
	private static final Tooltip TELEMETRY_DISABLED_TOOLTIP = Tooltip.of(Text.translatable("options.telemetry.disabled"));
	private static final Text CREDITS_AND_ATTRIBUTION_TEXT = Text.translatable("options.credits_and_attribution");
	private static final int COLUMNS = 2;
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 61, 33);
	private final Screen parent;
	private final GameOptions settings;
	@Nullable
	private CyclingButtonWidget<Difficulty> difficultyButton;
	@Nullable
	private LockButtonWidget lockDifficultyButton;

	public OptionsScreen(Screen parent, GameOptions gameOptions) {
		super(TITLE_TEXT);
		this.parent = parent;
		this.settings = gameOptions;
	}

	@Override
	protected void init() {
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addHeader(DirectionalLayoutWidget.vertical().spacing(8));
		directionalLayoutWidget.add(new TextWidget(TITLE_TEXT, this.textRenderer), Positioner::alignHorizontalCenter);
		DirectionalLayoutWidget directionalLayoutWidget2 = directionalLayoutWidget.add(DirectionalLayoutWidget.horizontal()).spacing(8);
		directionalLayoutWidget2.add(this.settings.getFov().createWidget(this.client.options));
		directionalLayoutWidget2.add(this.createTopRightButton());
		GridWidget gridWidget = new GridWidget();
		gridWidget.getMainPositioner().marginX(4).marginBottom(4).alignHorizontalCenter();
		GridWidget.Adder adder = gridWidget.createAdder(2);
		adder.add(this.createButton(SKIN_CUSTOMIZATION_TEXT, () -> new SkinOptionsScreen(this, this.settings)));
		adder.add(this.createButton(SOUNDS_TEXT, () -> new SoundOptionsScreen(this, this.settings)));
		adder.add(this.createButton(VIDEO_TEXT, () -> new VideoOptionsScreen(this, this.settings)));
		adder.add(this.createButton(CONTROL_TEXT, () -> new ControlsOptionsScreen(this, this.settings)));
		adder.add(this.createButton(LANGUAGE_TEXT, () -> new LanguageOptionsScreen(this, this.settings, this.client.getLanguageManager())));
		adder.add(this.createButton(CHAT_TEXT, () -> new ChatOptionsScreen(this, this.settings)));
		adder.add(
			this.createButton(
				RESOURCE_PACK_TEXT,
				() -> new PackScreen(
						this.client.getResourcePackManager(), this::refreshResourcePacks, this.client.getResourcePackDir(), Text.translatable("resourcePack.title")
					)
			)
		);
		adder.add(this.createButton(ACCESSIBILITY_TEXT, () -> new AccessibilityOptionsScreen(this, this.settings)));
		ButtonWidget buttonWidget = adder.add(this.createButton(TELEMETRY_TEXT, () -> new TelemetryInfoScreen(this, this.settings)));
		if (!this.client.isTelemetryEnabledByApi()) {
			buttonWidget.active = false;
			buttonWidget.setTooltip(TELEMETRY_DISABLED_TOOLTIP);
		}

		adder.add(this.createButton(CREDITS_AND_ATTRIBUTION_TEXT, () -> new CreditsAndAttributionScreen(this)));
		this.layout.addBody(gridWidget);
		this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).width(200).build());
		this.layout.forEachChild(this::addDrawableChild);
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	private void refreshResourcePacks(ResourcePackManager resourcePackManager) {
		this.settings.refreshResourcePacks(resourcePackManager);
		this.client.setScreen(this);
	}

	private Widget createTopRightButton() {
		if (this.client.world != null && this.client.isIntegratedServerRunning()) {
			this.difficultyButton = createDifficultyButtonWidget(0, 0, "options.difficulty", this.client);
			if (!this.client.world.getLevelProperties().isHardcore()) {
				this.lockDifficultyButton = new LockButtonWidget(
					0,
					0,
					button -> this.client
							.setScreen(
								new ConfirmScreen(
									this::lockDifficulty,
									Text.translatable("difficulty.lock.title"),
									Text.translatable("difficulty.lock.question", this.client.world.getLevelProperties().getDifficulty().getTranslatableName())
								)
							)
				);
				this.difficultyButton.setWidth(this.difficultyButton.getWidth() - this.lockDifficultyButton.getWidth());
				this.lockDifficultyButton.setLocked(this.client.world.getLevelProperties().isDifficultyLocked());
				this.lockDifficultyButton.active = !this.lockDifficultyButton.isLocked();
				this.difficultyButton.active = !this.lockDifficultyButton.isLocked();
				AxisGridWidget axisGridWidget = new AxisGridWidget(150, 0, AxisGridWidget.DisplayAxis.HORIZONTAL);
				axisGridWidget.add(this.difficultyButton);
				axisGridWidget.add(this.lockDifficultyButton);
				return axisGridWidget;
			} else {
				this.difficultyButton.active = false;
				return this.difficultyButton;
			}
		} else {
			return ButtonWidget.builder(
					Text.translatable("options.online"), button -> this.client.setScreen(OnlineOptionsScreen.create(this.client, this, this.settings))
				)
				.dimensions(this.width / 2 + 5, this.height / 6 - 12 + 24, 150, 20)
				.build();
		}
	}

	public static CyclingButtonWidget<Difficulty> createDifficultyButtonWidget(int x, int y, String translationKey, MinecraftClient client) {
		return CyclingButtonWidget.<Difficulty>builder(Difficulty::getTranslatableName)
			.values(Difficulty.values())
			.initially(client.world.getDifficulty())
			.build(
				x, y, 150, 20, Text.translatable(translationKey), (button, difficulty) -> client.getNetworkHandler().sendPacket(new UpdateDifficultyC2SPacket(difficulty))
			);
	}

	private void lockDifficulty(boolean difficultyLocked) {
		this.client.setScreen(this);
		if (difficultyLocked && this.client.world != null && this.lockDifficultyButton != null && this.difficultyButton != null) {
			this.client.getNetworkHandler().sendPacket(new UpdateDifficultyLockC2SPacket(true));
			this.lockDifficultyButton.setLocked(true);
			this.lockDifficultyButton.active = false;
			this.difficultyButton.active = false;
		}
	}

	@Override
	public void removed() {
		this.settings.write();
	}

	private ButtonWidget createButton(Text message, Supplier<Screen> screenSupplier) {
		return ButtonWidget.builder(message, button -> this.client.setScreen((Screen)screenSupplier.get())).build();
	}
}
