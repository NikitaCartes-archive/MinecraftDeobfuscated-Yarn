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
	private RealmsLabel titleLabel;
	private RealmsLabel subtitleLabel;
	private Text title = new TranslatableText("mco.reset.world.title");
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

	public RealmsResetWorldScreen(Screen parent, RealmsServer server, Runnable resetCallback, Runnable selectFileUploadCallback) {
		this.parent = parent;
		this.serverData = server;
		this.resetCallback = resetCallback;
		this.selectFileUploadCallback = selectFileUploadCallback;
	}

	public RealmsResetWorldScreen(
		Screen parent, RealmsServer server, Text title, Text subtitle, int subtitleColor, Text buttonTitle, Runnable resetCallback, Runnable selectFileUploadCallback
	) {
		this(parent, server, resetCallback, selectFileUploadCallback);
		this.title = title;
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
		this.addButton(new ButtonWidget(this.width / 2 - 40, row(14) - 10, 80, 20, this.buttonTitle, buttonWidget -> this.client.openScreen(this.parent)));
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
		this.titleLabel = this.addChild(new RealmsLabel(this.title, this.width / 2, 7, 16777215));
		this.subtitleLabel = this.addChild(new RealmsLabel(this.subtitle, this.width / 2, 22, this.subtitleColor));
		this.addButton(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(1),
				row(0) + 10,
				new TranslatableText("mco.reset.world.generate"),
				NEW_WORLD_TEXTURE,
				buttonWidget -> this.client.openScreen(new RealmsResetNormalWorldScreen(this::onResetNormalWorld, this.title))
			)
		);
		this.addButton(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(2),
				row(0) + 10,
				new TranslatableText("mco.reset.world.upload"),
				UPLOAD_TEXTURE,
				buttonWidget -> {
					Screen screen = new RealmsSelectFileToUploadScreen(
						this.serverData.id, this.slot != -1 ? this.slot : this.serverData.activeSlot, this, this.selectFileUploadCallback
					);
					this.client.openScreen(screen);
				}
			)
		);
		this.addButton(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(3),
				row(0) + 10,
				new TranslatableText("mco.reset.world.template"),
				SURVIVAL_SPAWN_TEXTURE,
				buttonWidget -> {
					RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
						this::onSelectWorldTemplate, RealmsServer.WorldType.NORMAL, this.normalWorldTemplates
					);
					realmsSelectWorldTemplateScreen.setTitle(new TranslatableText("mco.reset.world.template"));
					this.client.openScreen(realmsSelectWorldTemplateScreen);
				}
			)
		);
		this.addButton(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(1),
				row(6) + 20,
				new TranslatableText("mco.reset.world.adventure"),
				ADVENTURE_TEXTURE,
				buttonWidget -> {
					RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
						this::onSelectWorldTemplate, RealmsServer.WorldType.ADVENTUREMAP, this.adventureWorldTemplates
					);
					realmsSelectWorldTemplateScreen.setTitle(new TranslatableText("mco.reset.world.adventure"));
					this.client.openScreen(realmsSelectWorldTemplateScreen);
				}
			)
		);
		this.addButton(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(2),
				row(6) + 20,
				new TranslatableText("mco.reset.world.experience"),
				EXPERIENCE_TEXTURE,
				buttonWidget -> {
					RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
						this::onSelectWorldTemplate, RealmsServer.WorldType.EXPERIENCE, this.experienceWorldTemplates
					);
					realmsSelectWorldTemplateScreen.setTitle(new TranslatableText("mco.reset.world.experience"));
					this.client.openScreen(realmsSelectWorldTemplateScreen);
				}
			)
		);
		this.addButton(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(3),
				row(6) + 20,
				new TranslatableText("mco.reset.world.inspiration"),
				INSPIRATION_TEXTURE,
				buttonWidget -> {
					RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
						this::onSelectWorldTemplate, RealmsServer.WorldType.INSPIRATION, this.inspirationWorldTemplates
					);
					realmsSelectWorldTemplateScreen.setTitle(new TranslatableText("mco.reset.world.inspiration"));
					this.client.openScreen(realmsSelectWorldTemplateScreen);
				}
			)
		);
		this.narrateLabels();
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.client.openScreen(this.parent);
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
		this.titleLabel.render(this, matrices);
		this.subtitleLabel.render(this, matrices);
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

	private void method_32490(LongRunningTask longRunningTask) {
		this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent, longRunningTask));
	}

	public void switchSlot(Runnable callback) {
		this.method_32490(new SwitchSlotTask(this.serverData.id, this.slot, callback));
	}

	private void onSelectWorldTemplate(@Nullable WorldTemplate template) {
		this.client.openScreen(this);
		if (template != null) {
			this.method_32493(() -> this.method_32490(new ResettingWorldTemplateTask(template, this.serverData.id, this.resetTitle, this.resetCallback)));
		}
	}

	private void onResetNormalWorld(@Nullable ResetWorldInfo info) {
		this.client.openScreen(this);
		if (info != null) {
			this.method_32493(() -> this.method_32490(new ResettingNormalWorldTask(info, this.serverData.id, this.resetTitle, this.resetCallback)));
		}
	}

	private void method_32493(Runnable runnable) {
		if (this.slot == -1) {
			runnable.run();
		} else {
			this.switchSlot(runnable);
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
