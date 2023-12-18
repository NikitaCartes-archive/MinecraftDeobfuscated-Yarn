package net.minecraft.client.gui.screen.option;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
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
	private static final Text SKIN_CUSTOMIZATION_TEXT = Text.translatable("options.skinCustomisation");
	private static final Text SOUNDS_TEXT = Text.translatable("options.sounds");
	private static final Text VIDEO_TEXT = Text.translatable("options.video");
	private static final Text CONTROL_TEXT = Text.translatable("options.controls");
	private static final Text LANGUAGE_TEXT = Text.translatable("options.language");
	private static final Text CHAT_TEXT = Text.translatable("options.chat");
	private static final Text RESOURCE_PACK_TEXT = Text.translatable("options.resourcepack");
	private static final Text ACCESSIBILITY_TEXT = Text.translatable("options.accessibility");
	private static final Text TELEMETRY_TEXT = Text.translatable("options.telemetry");
	private static final Text CREDITS_AND_ATTRIBUTION_TEXT = Text.translatable("options.credits_and_attribution");
	private static final int COLUMNS = 2;
	private final Screen parent;
	private final GameOptions settings;
	private CyclingButtonWidget<Difficulty> difficultyButton;
	private LockButtonWidget lockDifficultyButton;

	public OptionsScreen(Screen parent, GameOptions gameOptions) {
		super(Text.translatable("options.title"));
		this.parent = parent;
		this.settings = gameOptions;
	}

	@Override
	protected void init() {
		GridWidget gridWidget = new GridWidget();
		gridWidget.getMainPositioner().marginX(5).marginBottom(4).alignHorizontalCenter();
		GridWidget.Adder adder = gridWidget.createAdder(2);
		adder.add(this.settings.getFov().createWidget(this.client.options, 0, 0, 150));
		adder.add(this.createTopRightButton());
		adder.add(EmptyWidget.ofHeight(26), 2);
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
		adder.add(this.createButton(TELEMETRY_TEXT, () -> new TelemetryInfoScreen(this, this.settings)));
		adder.add(this.createButton(CREDITS_AND_ATTRIBUTION_TEXT, () -> new CreditsAndAttributionScreen(this)));
		adder.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).width(200).build(), 2, adder.copyPositioner().marginTop(6));
		gridWidget.refreshPositions();
		SimplePositioningWidget.setPos(gridWidget, 0, this.height / 6 - 12, this.width, this.height, 0.5F, 0.0F);
		gridWidget.forEachChild(this::addDrawableChild);
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
		if (difficultyLocked && this.client.world != null) {
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

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 16777215);
	}

	private ButtonWidget createButton(Text message, Supplier<Screen> screenSupplier) {
		return ButtonWidget.builder(message, button -> this.client.setScreen((Screen)screenSupplier.get())).build();
	}
}
