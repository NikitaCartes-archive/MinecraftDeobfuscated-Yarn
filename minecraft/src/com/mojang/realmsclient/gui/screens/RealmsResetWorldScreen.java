package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.ResettingWorldTask;
import net.minecraft.realms.SwitchSlotTask;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsResetWorldScreen extends RealmsScreenWithCallback {
	private static final Logger LOGGER = LogManager.getLogger();
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
	private WorldTemplatePaginatedList field_20495;
	private WorldTemplatePaginatedList field_20496;
	private WorldTemplatePaginatedList field_20497;
	private WorldTemplatePaginatedList field_20498;
	public int slot = -1;
	private RealmsResetWorldScreen.ResetType typeToReset = RealmsResetWorldScreen.ResetType.NONE;
	private RealmsResetWorldScreen.ResetWorldInfo field_20499;
	private WorldTemplate field_20500;
	private String field_20501;
	private final Runnable field_22711;
	private final Runnable field_22712;

	public RealmsResetWorldScreen(Screen parent, RealmsServer realmsServer, Runnable runnable, Runnable runnable2) {
		this.parent = parent;
		this.serverData = realmsServer;
		this.field_22711 = runnable;
		this.field_22712 = runnable2;
	}

	public RealmsResetWorldScreen(Screen parent, RealmsServer realmsServer, Text text, Text text2, int i, Text text3, Runnable runnable, Runnable runnable2) {
		this(parent, realmsServer, runnable, runnable2);
		this.title = text;
		this.subtitle = text2;
		this.subtitleColor = i;
		this.buttonTitle = text3;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public void setResetTitle(String title) {
		this.field_20501 = title;
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
						RealmsResetWorldScreen.this.field_20495 = worldTemplatePaginatedList;
						RealmsResetWorldScreen.this.field_20496 = worldTemplatePaginatedList2;
						RealmsResetWorldScreen.this.field_20497 = worldTemplatePaginatedList3;
						RealmsResetWorldScreen.this.field_20498 = worldTemplatePaginatedList4;
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
				buttonWidget -> this.client.openScreen(new RealmsResetNormalWorldScreen(this, this.title))
			)
		);
		this.addButton(
			new RealmsResetWorldScreen.FrameButton(this.frame(2), row(0) + 10, new TranslatableText("mco.reset.world.upload"), UPLOAD_TEXTURE, buttonWidget -> {
				Screen screen = new RealmsSelectFileToUploadScreen(this.serverData.id, this.slot != -1 ? this.slot : this.serverData.activeSlot, this, this.field_22712);
				this.client.openScreen(screen);
			})
		);
		this.addButton(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(3),
				row(0) + 10,
				new TranslatableText("mco.reset.world.template"),
				SURVIVAL_SPAWN_TEXTURE,
				buttonWidget -> {
					RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
						this, RealmsServer.WorldType.NORMAL, this.field_20495
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
						this, RealmsServer.WorldType.ADVENTUREMAP, this.field_20496
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
						this, RealmsServer.WorldType.EXPERIENCE, this.field_20497
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
						this, RealmsServer.WorldType.INSPIRATION, this.field_20498
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
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
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

	private void drawFrame(MatrixStack matrices, int x, int y, Text text, Identifier identifier, boolean bl, boolean bl2) {
		this.client.getTextureManager().bindTexture(identifier);
		if (bl) {
			RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		} else {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		DrawableHelper.drawTexture(matrices, x + 2, y + 14, 0.0F, 0.0F, 56, 56, 56, 56);
		this.client.getTextureManager().bindTexture(SLOT_FRAME_TEXTURE);
		if (bl) {
			RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		} else {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		DrawableHelper.drawTexture(matrices, x, y + 12, 0.0F, 0.0F, 60, 60, 60, 60);
		int i = bl ? 10526880 : 16777215;
		this.drawCenteredText(matrices, this.textRenderer, text, x + 30, y, i);
	}

	@Override
	protected void callback(@Nullable WorldTemplate template) {
		if (template != null) {
			if (this.slot == -1) {
				this.resetWorldWithTemplate(template);
			} else {
				switch (template.type) {
					case WORLD_TEMPLATE:
						this.typeToReset = RealmsResetWorldScreen.ResetType.SURVIVAL_SPAWN;
						break;
					case ADVENTUREMAP:
						this.typeToReset = RealmsResetWorldScreen.ResetType.ADVENTURE;
						break;
					case EXPERIENCE:
						this.typeToReset = RealmsResetWorldScreen.ResetType.EXPERIENCE;
						break;
					case INSPIRATION:
						this.typeToReset = RealmsResetWorldScreen.ResetType.INSPIRATION;
				}

				this.field_20500 = template;
				this.switchSlot();
			}
		}
	}

	private void switchSlot() {
		this.switchSlot(() -> {
			switch (this.typeToReset) {
				case ADVENTURE:
				case SURVIVAL_SPAWN:
				case EXPERIENCE:
				case INSPIRATION:
					if (this.field_20500 != null) {
						this.resetWorldWithTemplate(this.field_20500);
					}
					break;
				case GENERATE:
					if (this.field_20499 != null) {
						this.triggerResetWorld(this.field_20499);
					}
			}
		});
	}

	public void switchSlot(Runnable callback) {
		this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new SwitchSlotTask(this.serverData.id, this.slot, callback)));
	}

	public void resetWorldWithTemplate(WorldTemplate template) {
		this.method_25207(null, template, -1, true);
	}

	private void triggerResetWorld(RealmsResetWorldScreen.ResetWorldInfo resetWorldInfo) {
		this.method_25207(resetWorldInfo.seed, null, resetWorldInfo.levelType, resetWorldInfo.generateStructures);
	}

	private void method_25207(@Nullable String string, @Nullable WorldTemplate worldTemplate, int i, boolean bl) {
		this.client
			.openScreen(
				new RealmsLongRunningMcoTaskScreen(
					this.parent, new ResettingWorldTask(string, worldTemplate, i, bl, this.serverData.id, this.field_20501, this.field_22711)
				)
			);
	}

	public void resetWorld(RealmsResetWorldScreen.ResetWorldInfo resetWorldInfo) {
		if (this.slot == -1) {
			this.triggerResetWorld(resetWorldInfo);
		} else {
			this.typeToReset = RealmsResetWorldScreen.ResetType.GENERATE;
			this.field_20499 = resetWorldInfo;
			this.switchSlot();
		}
	}

	@Environment(EnvType.CLIENT)
	class FrameButton extends ButtonWidget {
		private final Identifier image;

		public FrameButton(int x, int y, Text text, Identifier identifier, ButtonWidget.PressAction pressAction) {
			super(x, y, 60, 72, text, pressAction);
			this.image = identifier;
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			RealmsResetWorldScreen.this.drawFrame(
				matrices, this.x, this.y, this.getMessage(), this.image, this.isHovered(), this.isMouseOver((double)mouseX, (double)mouseY)
			);
		}
	}

	@Environment(EnvType.CLIENT)
	static enum ResetType {
		NONE,
		GENERATE,
		UPLOAD,
		ADVENTURE,
		SURVIVAL_SPAWN,
		EXPERIENCE,
		INSPIRATION;
	}

	@Environment(EnvType.CLIENT)
	public static class ResetWorldInfo {
		private final String seed;
		private final int levelType;
		private final boolean generateStructures;

		public ResetWorldInfo(String seed, int levelType, boolean generateStructures) {
			this.seed = seed;
			this.levelType = levelType;
			this.generateStructures = generateStructures;
		}
	}
}
