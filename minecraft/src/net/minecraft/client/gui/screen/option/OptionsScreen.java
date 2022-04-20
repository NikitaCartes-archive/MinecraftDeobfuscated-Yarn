package net.minecraft.client.gui.screen.option;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;

@Environment(EnvType.CLIENT)
public class OptionsScreen extends Screen {
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
		int i = 0;

		for (SimpleOption<?> simpleOption : new SimpleOption[]{this.settings.getFov()}) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = this.height / 6 - 12 + 24 * (i >> 1);
			this.addDrawableChild(simpleOption.createButton(this.client.options, j, k, 150));
			i++;
		}

		if (this.client.world != null && this.client.isIntegratedServerRunning()) {
			this.difficultyButton = this.addDrawableChild(createDifficultyButtonWidget(i, this.width, this.height, "options.difficulty", this.client));
			if (!this.client.world.getLevelProperties().isHardcore()) {
				this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
				this.lockDifficultyButton = this.addDrawableChild(
					new LockButtonWidget(
						this.difficultyButton.x + this.difficultyButton.getWidth(),
						this.difficultyButton.y,
						button -> this.client
								.setScreen(
									new ConfirmScreen(
										this::lockDifficulty,
										Text.translatable("difficulty.lock.title"),
										Text.translatable("difficulty.lock.question", this.client.world.getLevelProperties().getDifficulty().getTranslatableName())
									)
								)
					)
				);
				this.lockDifficultyButton.setLocked(this.client.world.getLevelProperties().isDifficultyLocked());
				this.lockDifficultyButton.active = !this.lockDifficultyButton.isLocked();
				this.difficultyButton.active = !this.lockDifficultyButton.isLocked();
			} else {
				this.difficultyButton.active = false;
			}
		} else {
			this.addDrawableChild(
				new ButtonWidget(
					this.width / 2 + 5,
					this.height / 6 - 12 + 24 * (i >> 1),
					150,
					20,
					Text.translatable("options.online"),
					button -> this.client.setScreen(new OnlineOptionsScreen(this, this.settings))
				)
			);
		}

		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 155,
				this.height / 6 + 48 - 6,
				150,
				20,
				Text.translatable("options.skinCustomisation"),
				button -> this.client.setScreen(new SkinOptionsScreen(this, this.settings))
			)
		);
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 + 5,
				this.height / 6 + 48 - 6,
				150,
				20,
				Text.translatable("options.sounds"),
				button -> this.client.setScreen(new SoundOptionsScreen(this, this.settings))
			)
		);
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 155,
				this.height / 6 + 72 - 6,
				150,
				20,
				Text.translatable("options.video"),
				button -> this.client.setScreen(new VideoOptionsScreen(this, this.settings))
			)
		);
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 + 5,
				this.height / 6 + 72 - 6,
				150,
				20,
				Text.translatable("options.controls"),
				button -> this.client.setScreen(new ControlsOptionsScreen(this, this.settings))
			)
		);
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 155,
				this.height / 6 + 96 - 6,
				150,
				20,
				Text.translatable("options.language"),
				button -> this.client.setScreen(new LanguageOptionsScreen(this, this.settings, this.client.getLanguageManager()))
			)
		);
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 + 5,
				this.height / 6 + 96 - 6,
				150,
				20,
				Text.translatable("options.chat.title"),
				button -> this.client.setScreen(new ChatOptionsScreen(this, this.settings))
			)
		);
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 - 155,
				this.height / 6 + 120 - 6,
				150,
				20,
				Text.translatable("options.resourcepack"),
				button -> this.client
						.setScreen(
							new PackScreen(
								this, this.client.getResourcePackManager(), this::refreshResourcePacks, this.client.getResourcePackDir(), Text.translatable("resourcePack.title")
							)
						)
			)
		);
		this.addDrawableChild(
			new ButtonWidget(
				this.width / 2 + 5,
				this.height / 6 + 120 - 6,
				150,
				20,
				Text.translatable("options.accessibility.title"),
				button -> this.client.setScreen(new AccessibilityOptionsScreen(this, this.settings))
			)
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, ScreenTexts.DONE, button -> this.client.setScreen(this.parent)));
	}

	public static CyclingButtonWidget<Difficulty> createDifficultyButtonWidget(
		int buttonIndex, int width, int height, String translationKey, MinecraftClient client
	) {
		return CyclingButtonWidget.<Difficulty>builder(Difficulty::getTranslatableName)
			.values(Difficulty.values())
			.initially(client.world.getDifficulty())
			.build(
				width / 2 - 155 + buttonIndex % 2 * 160,
				height / 6 - 12 + 24 * (buttonIndex >> 1),
				150,
				20,
				Text.translatable(translationKey),
				(button, difficulty) -> client.getNetworkHandler().sendPacket(new UpdateDifficultyC2SPacket(difficulty))
			);
	}

	private void refreshResourcePacks(ResourcePackManager resourcePackManager) {
		List<String> list = ImmutableList.copyOf(this.settings.resourcePacks);
		this.settings.resourcePacks.clear();
		this.settings.incompatibleResourcePacks.clear();

		for (ResourcePackProfile resourcePackProfile : resourcePackManager.getEnabledProfiles()) {
			if (!resourcePackProfile.isPinned()) {
				this.settings.resourcePacks.add(resourcePackProfile.getName());
				if (!resourcePackProfile.getCompatibility().isCompatible()) {
					this.settings.incompatibleResourcePacks.add(resourcePackProfile.getName());
				}
			}
		}

		this.settings.write();
		List<String> list2 = ImmutableList.copyOf(this.settings.resourcePacks);
		if (!list2.equals(list)) {
			this.client.reloadResources();
		}
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
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
