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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.ResettingWorldTask;
import net.minecraft.realms.SwitchSlotTask;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsResetWorldScreen extends RealmsScreenWithCallback {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Screen lastScreen;
	private final RealmsServer serverData;
	private RealmsLabel titleLabel;
	private RealmsLabel subtitleLabel;
	private String title = I18n.translate("mco.reset.world.title");
	private String subtitle = I18n.translate("mco.reset.world.warning");
	private String buttonTitle = I18n.translate("gui.cancel");
	private int subtitleColor = 16711680;
	private static final Identifier field_22713 = new Identifier("realms", "textures/gui/realms/slot_frame.png");
	private static final Identifier field_22714 = new Identifier("realms", "textures/gui/realms/upload.png");
	private static final Identifier field_22715 = new Identifier("realms", "textures/gui/realms/adventure.png");
	private static final Identifier field_22716 = new Identifier("realms", "textures/gui/realms/survival_spawn.png");
	private static final Identifier field_22708 = new Identifier("realms", "textures/gui/realms/new_world.png");
	private static final Identifier field_22709 = new Identifier("realms", "textures/gui/realms/experience.png");
	private static final Identifier field_22710 = new Identifier("realms", "textures/gui/realms/inspiration.png");
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

	public RealmsResetWorldScreen(Screen screen, RealmsServer realmsServer, Runnable runnable, Runnable runnable2) {
		this.lastScreen = screen;
		this.serverData = realmsServer;
		this.field_22711 = runnable;
		this.field_22712 = runnable2;
	}

	public RealmsResetWorldScreen(
		Screen screen, RealmsServer realmsServer, String string, String string2, int i, String string3, Runnable runnable, Runnable runnable2
	) {
		this(screen, realmsServer, runnable, runnable2);
		this.title = string;
		this.subtitle = string2;
		this.subtitleColor = i;
		this.buttonTitle = string3;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public void setResetTitle(String title) {
		this.field_20501 = title;
	}

	@Override
	public void init() {
		this.addButton(new ButtonWidget(this.width / 2 - 40, row(14) - 10, 80, 20, this.buttonTitle, buttonWidget -> this.client.openScreen(this.lastScreen)));
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
				I18n.translate("mco.reset.world.generate"),
				field_22708,
				buttonWidget -> this.client.openScreen(new RealmsResetNormalWorldScreen(this, this.title))
			)
		);
		this.addButton(new RealmsResetWorldScreen.FrameButton(this.frame(2), row(0) + 10, I18n.translate("mco.reset.world.upload"), field_22714, buttonWidget -> {
			Screen screen = new RealmsSelectFileToUploadScreen(this.serverData.id, this.slot != -1 ? this.slot : this.serverData.activeSlot, this, this.field_22712);
			this.client.openScreen(screen);
		}));
		this.addButton(new RealmsResetWorldScreen.FrameButton(this.frame(3), row(0) + 10, I18n.translate("mco.reset.world.template"), field_22716, buttonWidget -> {
			RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.NORMAL, this.field_20495);
			realmsSelectWorldTemplateScreen.setTitle(I18n.translate("mco.reset.world.template"));
			this.client.openScreen(realmsSelectWorldTemplateScreen);
		}));
		this.addButton(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(1),
				row(6) + 20,
				I18n.translate("mco.reset.world.adventure"),
				field_22715,
				buttonWidget -> {
					RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
						this, RealmsServer.WorldType.ADVENTUREMAP, this.field_20496
					);
					realmsSelectWorldTemplateScreen.setTitle(I18n.translate("mco.reset.world.adventure"));
					this.client.openScreen(realmsSelectWorldTemplateScreen);
				}
			)
		);
		this.addButton(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(2),
				row(6) + 20,
				I18n.translate("mco.reset.world.experience"),
				field_22709,
				buttonWidget -> {
					RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
						this, RealmsServer.WorldType.EXPERIENCE, this.field_20497
					);
					realmsSelectWorldTemplateScreen.setTitle(I18n.translate("mco.reset.world.experience"));
					this.client.openScreen(realmsSelectWorldTemplateScreen);
				}
			)
		);
		this.addButton(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(3),
				row(6) + 20,
				I18n.translate("mco.reset.world.inspiration"),
				field_22710,
				buttonWidget -> {
					RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
						this, RealmsServer.WorldType.INSPIRATION, this.field_20498
					);
					realmsSelectWorldTemplateScreen.setTitle(I18n.translate("mco.reset.world.inspiration"));
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
			this.client.openScreen(this.lastScreen);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private int frame(int i) {
		return this.width / 2 - 130 + (i - 1) * 100;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.titleLabel.render(this);
		this.subtitleLabel.render(this);
		super.render(mouseX, mouseY, delta);
	}

	private void drawFrame(int i, int y, String text, Identifier identifier, boolean bl, boolean bl2) {
		this.client.getTextureManager().bindTexture(identifier);
		if (bl) {
			RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		} else {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		DrawableHelper.blit(i + 2, y + 14, 0.0F, 0.0F, 56, 56, 56, 56);
		this.client.getTextureManager().bindTexture(field_22713);
		if (bl) {
			RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		} else {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		DrawableHelper.blit(i, y + 12, 0.0F, 0.0F, 60, 60, 60, 60);
		int j = bl ? 10526880 : 16777215;
		this.drawCenteredString(this.textRenderer, text, i + 30, y, j);
	}

	@Override
	protected void callback(@Nullable WorldTemplate worldTemplate) {
		if (worldTemplate != null) {
			if (this.slot == -1) {
				this.resetWorldWithTemplate(worldTemplate);
			} else {
				switch (worldTemplate.type) {
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

				this.field_20500 = worldTemplate;
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

	public void switchSlot(Runnable runnable) {
		this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(this.serverData.id, this.slot, runnable)));
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
					this.lastScreen, new ResettingWorldTask(string, worldTemplate, i, bl, this.serverData.id, this.field_20501, this.field_22711)
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

		public FrameButton(int x, int y, String text, Identifier identifier, ButtonWidget.PressAction pressAction) {
			super(x, y, 60, 72, text, pressAction);
			this.image = identifier;
		}

		@Override
		public void renderButton(int mouseX, int mouseY, float delta) {
			RealmsResetWorldScreen.this.drawFrame(this.x, this.y, this.getMessage(), this.image, this.isHovered(), this.isMouseOver((double)mouseX, (double)mouseY));
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
