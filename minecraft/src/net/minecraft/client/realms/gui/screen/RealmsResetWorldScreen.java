package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.ResettingNormalWorldTask;
import net.minecraft.client.realms.task.ResettingWorldTemplateTask;
import net.minecraft.client.realms.task.SwitchSlotTask;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RealmsResetWorldScreen extends RealmsScreen {
	static final Logger LOGGER = LogManager.getLogger();
	private final Screen parent;
	private final RealmsServer serverData;
	private Text subtitle = new TranslatableText("mco.reset.world.warning");
	private Text buttonTitle = ScreenTexts.CANCEL;
	private int subtitleColor = 16711680;
	private static final Identifier SLOT_FRAME_TEXTURE = new Identifier("realms", "textures/gui/realms/slot_frame.png");
	private static final Identifier UPLOAD_TEXTURE = new Identifier("realms", "textures/gui/realms/upload.png");
	private static final Identifier ADVENTURE_TEXTURE = new Identifier("realms", "textures/gui/realms/adventure.png");
	private static final Identifier SURVIVAL_SPAWN_TEXTURE = new Identifier("realms", "textures/gui/realms/survival_spawn.png");
	private static final Identifier NEW_WORLD_TEXTURE = new Identifier("realms", "textures/gui/realms/new_world.png");
	private static final Identifier EXPERIENCE_TEXTURE = new Identifier("realms", "textures/gui/realms/experience.png");
	private static final Identifier INSPIRATION_TEXTURE = new Identifier("realms", "textures/gui/realms/inspiration.png");
	WorldTemplatePaginatedList normalWorldTemplates;
	WorldTemplatePaginatedList adventureWorldTemplates;
	WorldTemplatePaginatedList experienceWorldTemplates;
	WorldTemplatePaginatedList inspirationWorldTemplates;
	public int slot = -1;
	private Text resetTitle = new TranslatableText("mco.reset.world.resetting.screen.title");
	private final Runnable resetCallback;
	private final Runnable selectFileUploadCallback;

	public RealmsResetWorldScreen(Screen parent, RealmsServer server, Text title, Runnable resetCallback, Runnable selectFileUploadCallback) {
		super(title);
		this.parent = parent;
		this.serverData = server;
		this.resetCallback = resetCallback;
		this.selectFileUploadCallback = selectFileUploadCallback;
	}

	public RealmsResetWorldScreen(Screen parent, RealmsServer serverData, Runnable resetCallback, Runnable selectFileUploadCallback) {
		this(parent, serverData, new TranslatableText("mco.reset.world.title"), resetCallback, selectFileUploadCallback);
	}

	public RealmsResetWorldScreen(
		Screen parent, RealmsServer server, Text title, Text subtitle, int subtitleColor, Text buttonTitle, Runnable resetCallback, Runnable selectFileUploadCallback
	) {
		this(parent, server, title, resetCallback, selectFileUploadCallback);
		this.subtitle = subtitle;
		this.subtitleColor = subtitleColor;
		this.buttonTitle = buttonTitle;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public void setResetTitle(Text resetTitle) {
		this.resetTitle = resetTitle;
	}

	@Override
	public void init() {
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 40, row(14) - 10, 80, 20, this.buttonTitle, button -> this.client.setScreen(this.parent)));
		(new Thread("Realms-reset-world-fetcher") {
			public void run() {
				RealmsClient realmsClient = RealmsClient.createRealmsClient();

				try {
					WorldTemplatePaginatedList worldTemplatePaginatedList = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
					WorldTemplatePaginatedList worldTemplatePaginatedList2 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
					WorldTemplatePaginatedList worldTemplatePaginatedList3 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
					WorldTemplatePaginatedList worldTemplatePaginatedList4 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
					RealmsResetWorldScreen.this.client.execute(() -> {
						RealmsResetWorldScreen.this.normalWorldTemplates = worldTemplatePaginatedList;
						RealmsResetWorldScreen.this.adventureWorldTemplates = worldTemplatePaginatedList2;
						RealmsResetWorldScreen.this.experienceWorldTemplates = worldTemplatePaginatedList3;
						RealmsResetWorldScreen.this.inspirationWorldTemplates = worldTemplatePaginatedList4;
					});
				} catch (RealmsServiceException var6) {
					RealmsResetWorldScreen.LOGGER.error("Couldn't fetch templates in reset world", (Throwable)var6);
				}
			}
		}).start();
		this.addLabel(new RealmsLabel(this.subtitle, this.width / 2, 22, this.subtitleColor));
		this.addDrawableChild(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(1),
				row(0) + 10,
				new TranslatableText("mco.reset.world.generate"),
				NEW_WORLD_TEXTURE,
				button -> this.client.setScreen(new RealmsResetNormalWorldScreen(this::onResetNormalWorld, this.title))
			)
		);
		this.addDrawableChild(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(2),
				row(0) + 10,
				new TranslatableText("mco.reset.world.upload"),
				UPLOAD_TEXTURE,
				button -> this.client
						.setScreen(
							new RealmsSelectFileToUploadScreen(this.serverData.id, this.slot != -1 ? this.slot : this.serverData.activeSlot, this, this.selectFileUploadCallback)
						)
			)
		);
		this.addDrawableChild(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(3),
				row(0) + 10,
				new TranslatableText("mco.reset.world.template"),
				SURVIVAL_SPAWN_TEXTURE,
				button -> this.client
						.setScreen(
							new RealmsSelectWorldTemplateScreen(
								new TranslatableText("mco.reset.world.template"), this::onSelectWorldTemplate, RealmsServer.WorldType.NORMAL, this.normalWorldTemplates
							)
						)
			)
		);
		this.addDrawableChild(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(1),
				row(6) + 20,
				new TranslatableText("mco.reset.world.adventure"),
				ADVENTURE_TEXTURE,
				button -> this.client
						.setScreen(
							new RealmsSelectWorldTemplateScreen(
								new TranslatableText("mco.reset.world.adventure"), this::onSelectWorldTemplate, RealmsServer.WorldType.ADVENTUREMAP, this.adventureWorldTemplates
							)
						)
			)
		);
		this.addDrawableChild(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(2),
				row(6) + 20,
				new TranslatableText("mco.reset.world.experience"),
				EXPERIENCE_TEXTURE,
				button -> this.client
						.setScreen(
							new RealmsSelectWorldTemplateScreen(
								new TranslatableText("mco.reset.world.experience"), this::onSelectWorldTemplate, RealmsServer.WorldType.EXPERIENCE, this.experienceWorldTemplates
							)
						)
			)
		);
		this.addDrawableChild(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(3),
				row(6) + 20,
				new TranslatableText("mco.reset.world.inspiration"),
				INSPIRATION_TEXTURE,
				button -> this.client
						.setScreen(
							new RealmsSelectWorldTemplateScreen(
								new TranslatableText("mco.reset.world.inspiration"), this::onSelectWorldTemplate, RealmsServer.WorldType.INSPIRATION, this.inspirationWorldTemplates
							)
						)
			)
		);
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinSentences(this.getTitle(), this.narrateLabels());
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.client.setScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private int frame(int i) {
		return this.width / 2 - 130 + (i - 1) * 100;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 7, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	void drawFrame(MatrixStack matrices, int x, int y, Text text, Identifier texture, boolean hovered, boolean mouseOver) {
		RenderSystem.setShaderTexture(0, texture);
		if (hovered) {
			RenderSystem.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
		} else {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}

		DrawableHelper.drawTexture(matrices, x + 2, y + 14, 0.0F, 0.0F, 56, 56, 56, 56);
		RenderSystem.setShaderTexture(0, SLOT_FRAME_TEXTURE);
		if (hovered) {
			RenderSystem.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
		} else {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}

		DrawableHelper.drawTexture(matrices, x, y + 12, 0.0F, 0.0F, 60, 60, 60, 60);
		int i = hovered ? 10526880 : 16777215;
		drawCenteredText(matrices, this.textRenderer, text, x + 30, y, i);
	}

	private void executeLongRunningTask(LongRunningTask task) {
		this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent, task));
	}

	public void switchSlot(Runnable callback) {
		this.executeLongRunningTask(new SwitchSlotTask(this.serverData.id, this.slot, () -> this.client.execute(callback)));
	}

	private void onSelectWorldTemplate(@Nullable WorldTemplate template) {
		this.client.setScreen(this);
		if (template != null) {
			this.switchSlotAndResetWorld(
				() -> this.executeLongRunningTask(new ResettingWorldTemplateTask(template, this.serverData.id, this.resetTitle, this.resetCallback))
			);
		}
	}

	private void onResetNormalWorld(@Nullable ResetWorldInfo info) {
		this.client.setScreen(this);
		if (info != null) {
			this.switchSlotAndResetWorld(() -> this.executeLongRunningTask(new ResettingNormalWorldTask(info, this.serverData.id, this.resetTitle, this.resetCallback)));
		}
	}

	private void switchSlotAndResetWorld(Runnable resetter) {
		if (this.slot == -1) {
			resetter.run();
		} else {
			this.switchSlot(resetter);
		}
	}

	@Environment(EnvType.CLIENT)
	class FrameButton extends ButtonWidget {
		private final Identifier image;

		public FrameButton(int x, int y, Text message, Identifier image, ButtonWidget.PressAction onPress) {
			super(x, y, 60, 72, message, onPress);
			this.image = image;
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			RealmsResetWorldScreen.this.drawFrame(
				matrices, this.x, this.y, this.getMessage(), this.image, this.isHovered(), this.isMouseOver((double)mouseX, (double)mouseY)
			);
		}
	}
}
