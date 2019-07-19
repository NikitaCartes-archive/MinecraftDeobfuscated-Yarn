package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsResetWorldScreen extends RealmsScreenWithCallback<WorldTemplate> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RealmsScreen lastScreen;
	private final RealmsServer serverData;
	private final RealmsScreen returnScreen;
	private RealmsLabel titleLabel;
	private RealmsLabel subtitleLabel;
	private String title = getLocalizedString("mco.reset.world.title");
	private String subtitle = getLocalizedString("mco.reset.world.warning");
	private String buttonTitle = getLocalizedString("gui.cancel");
	private int subtitleColor = 16711680;
	private final int BUTTON_CANCEL_ID = 0;
	private final int BUTTON_FRAME_START = 100;
	private WorldTemplatePaginatedList templates = null;
	private WorldTemplatePaginatedList adventuremaps = null;
	private WorldTemplatePaginatedList experiences = null;
	private WorldTemplatePaginatedList inspirations = null;
	public int slot = -1;
	private RealmsResetWorldScreen.ResetType typeToReset = RealmsResetWorldScreen.ResetType.NONE;
	private RealmsResetWorldScreen.ResetWorldInfo worldInfoToReset = null;
	private WorldTemplate worldTemplateToReset = null;
	private String resetTitle = null;
	private int confirmationId = -1;

	public RealmsResetWorldScreen(RealmsScreen lastScreen, RealmsServer serverData, RealmsScreen returnScreen) {
		this.lastScreen = lastScreen;
		this.serverData = serverData;
		this.returnScreen = returnScreen;
	}

	public RealmsResetWorldScreen(
		RealmsScreen lastScreen, RealmsServer serverData, RealmsScreen returnScreen, String title, String subtitle, int subtitleColor, String buttonTitle
	) {
		this(lastScreen, serverData, returnScreen);
		this.title = title;
		this.subtitle = subtitle;
		this.subtitleColor = subtitleColor;
		this.buttonTitle = buttonTitle;
	}

	public void setConfirmationId(int confirmationId) {
		this.confirmationId = confirmationId;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public void setResetTitle(String title) {
		this.resetTitle = title;
	}

	@Override
	public void init() {
		this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 40, RealmsConstants.row(14) - 10, 80, 20, this.buttonTitle) {
			@Override
			public void onPress() {
				Realms.setScreen(RealmsResetWorldScreen.this.lastScreen);
			}
		});
		(new Thread("Realms-reset-world-fetcher") {
			public void run() {
				RealmsClient realmsClient = RealmsClient.createRealmsClient();

				try {
					WorldTemplatePaginatedList worldTemplatePaginatedList = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
					WorldTemplatePaginatedList worldTemplatePaginatedList2 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
					WorldTemplatePaginatedList worldTemplatePaginatedList3 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
					WorldTemplatePaginatedList worldTemplatePaginatedList4 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
					Realms.execute((Runnable)(() -> {
						RealmsResetWorldScreen.this.templates = worldTemplatePaginatedList;
						RealmsResetWorldScreen.this.adventuremaps = worldTemplatePaginatedList2;
						RealmsResetWorldScreen.this.experiences = worldTemplatePaginatedList3;
						RealmsResetWorldScreen.this.inspirations = worldTemplatePaginatedList4;
					}));
				} catch (RealmsServiceException var6) {
					RealmsResetWorldScreen.LOGGER.error("Couldn't fetch templates in reset world", (Throwable)var6);
				}
			}
		}).start();
		this.addWidget(this.titleLabel = new RealmsLabel(this.title, this.width() / 2, 7, 16777215));
		this.addWidget(this.subtitleLabel = new RealmsLabel(this.subtitle, this.width() / 2, 22, this.subtitleColor));
		this.buttonsAdd(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(1),
				RealmsConstants.row(0) + 10,
				getLocalizedString("mco.reset.world.generate"),
				-1L,
				"realms:textures/gui/realms/new_world.png",
				RealmsResetWorldScreen.ResetType.GENERATE
			) {
				@Override
				public void onPress() {
					Realms.setScreen(new RealmsResetNormalWorldScreen(RealmsResetWorldScreen.this, RealmsResetWorldScreen.this.title));
				}
			}
		);
		this.buttonsAdd(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(2),
				RealmsConstants.row(0) + 10,
				getLocalizedString("mco.reset.world.upload"),
				-1L,
				"realms:textures/gui/realms/upload.png",
				RealmsResetWorldScreen.ResetType.UPLOAD
			) {
				@Override
				public void onPress() {
					int var10003 = RealmsResetWorldScreen.this.slot != -1 ? RealmsResetWorldScreen.this.slot : RealmsResetWorldScreen.this.serverData.activeSlot;
					Realms.setScreen(new RealmsSelectFileToUploadScreen(RealmsResetWorldScreen.this.serverData.id, var10003, RealmsResetWorldScreen.this));
				}
			}
		);
		this.buttonsAdd(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(3),
				RealmsConstants.row(0) + 10,
				getLocalizedString("mco.reset.world.template"),
				-1L,
				"realms:textures/gui/realms/survival_spawn.png",
				RealmsResetWorldScreen.ResetType.SURVIVAL_SPAWN
			) {
				@Override
				public void onPress() {
					RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
						RealmsResetWorldScreen.this, RealmsServer.WorldType.NORMAL, RealmsResetWorldScreen.this.templates
					);
					realmsSelectWorldTemplateScreen.setTitle(RealmsScreen.getLocalizedString("mco.reset.world.template"));
					Realms.setScreen(realmsSelectWorldTemplateScreen);
				}
			}
		);
		this.buttonsAdd(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(1),
				RealmsConstants.row(6) + 20,
				getLocalizedString("mco.reset.world.adventure"),
				-1L,
				"realms:textures/gui/realms/adventure.png",
				RealmsResetWorldScreen.ResetType.ADVENTURE
			) {
				@Override
				public void onPress() {
					RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
						RealmsResetWorldScreen.this, RealmsServer.WorldType.ADVENTUREMAP, RealmsResetWorldScreen.this.adventuremaps
					);
					realmsSelectWorldTemplateScreen.setTitle(RealmsScreen.getLocalizedString("mco.reset.world.adventure"));
					Realms.setScreen(realmsSelectWorldTemplateScreen);
				}
			}
		);
		this.buttonsAdd(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(2),
				RealmsConstants.row(6) + 20,
				getLocalizedString("mco.reset.world.experience"),
				-1L,
				"realms:textures/gui/realms/experience.png",
				RealmsResetWorldScreen.ResetType.EXPERIENCE
			) {
				@Override
				public void onPress() {
					RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
						RealmsResetWorldScreen.this, RealmsServer.WorldType.EXPERIENCE, RealmsResetWorldScreen.this.experiences
					);
					realmsSelectWorldTemplateScreen.setTitle(RealmsScreen.getLocalizedString("mco.reset.world.experience"));
					Realms.setScreen(realmsSelectWorldTemplateScreen);
				}
			}
		);
		this.buttonsAdd(
			new RealmsResetWorldScreen.FrameButton(
				this.frame(3),
				RealmsConstants.row(6) + 20,
				getLocalizedString("mco.reset.world.inspiration"),
				-1L,
				"realms:textures/gui/realms/inspiration.png",
				RealmsResetWorldScreen.ResetType.INSPIRATION
			) {
				@Override
				public void onPress() {
					RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen(
						RealmsResetWorldScreen.this, RealmsServer.WorldType.INSPIRATION, RealmsResetWorldScreen.this.inspirations
					);
					realmsSelectWorldTemplateScreen.setTitle(RealmsScreen.getLocalizedString("mco.reset.world.inspiration"));
					Realms.setScreen(realmsSelectWorldTemplateScreen);
				}
			}
		);
		this.narrateLabels();
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public boolean keyPressed(int eventKey, int scancode, int mods) {
		if (eventKey == 256) {
			Realms.setScreen(this.lastScreen);
			return true;
		} else {
			return super.keyPressed(eventKey, scancode, mods);
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int buttonNum) {
		return super.mouseClicked(x, y, buttonNum);
	}

	private int frame(int i) {
		return this.width() / 2 - 130 + (i - 1) * 100;
	}

	@Override
	public void render(int xm, int ym, float a) {
		this.renderBackground();
		this.titleLabel.render(this);
		this.subtitleLabel.render(this);
		super.render(xm, ym, a);
	}

	private void drawFrame(
		int x, int y, String text, long imageId, String image, RealmsResetWorldScreen.ResetType resetType, boolean hoveredOrFocused, boolean hovered
	) {
		if (imageId == -1L) {
			bind(image);
		} else {
			RealmsTextureManager.bindWorldTemplate(String.valueOf(imageId), image);
		}

		if (hoveredOrFocused) {
			GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		} else {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		RealmsScreen.blit(x + 2, y + 14, 0.0F, 0.0F, 56, 56, 56, 56);
		bind("realms:textures/gui/realms/slot_frame.png");
		if (hoveredOrFocused) {
			GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		} else {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		RealmsScreen.blit(x, y + 12, 0.0F, 0.0F, 60, 60, 60, 60);
		this.drawCenteredString(text, x + 30, y, hoveredOrFocused ? 10526880 : 16777215);
	}

	void callback(WorldTemplate worldTemplate) {
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

				this.worldTemplateToReset = worldTemplate;
				this.switchSlot();
			}
		}
	}

	private void switchSlot() {
		this.switchSlot(this);
	}

	public void switchSlot(RealmsScreen screen) {
		RealmsTasks.SwitchSlotTask switchSlotTask = new RealmsTasks.SwitchSlotTask(this.serverData.id, this.slot, screen, 100);
		RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, switchSlotTask);
		realmsLongRunningMcoTaskScreen.start();
		Realms.setScreen(realmsLongRunningMcoTaskScreen);
	}

	@Override
	public void confirmResult(boolean result, int id) {
		if (id == 100 && result) {
			switch (this.typeToReset) {
				case ADVENTURE:
				case SURVIVAL_SPAWN:
				case EXPERIENCE:
				case INSPIRATION:
					if (this.worldTemplateToReset != null) {
						this.resetWorldWithTemplate(this.worldTemplateToReset);
					}
					break;
				case GENERATE:
					if (this.worldInfoToReset != null) {
						this.triggerResetWorld(this.worldInfoToReset);
					}
					break;
				default:
					return;
			}
		} else {
			if (result) {
				Realms.setScreen(this.returnScreen);
				if (this.confirmationId != -1) {
					this.returnScreen.confirmResult(true, this.confirmationId);
				}
			}
		}
	}

	public void resetWorldWithTemplate(WorldTemplate template) {
		RealmsTasks.ResettingWorldTask resettingWorldTask = new RealmsTasks.ResettingWorldTask(this.serverData.id, this.returnScreen, template);
		if (this.resetTitle != null) {
			resettingWorldTask.setResetTitle(this.resetTitle);
		}

		if (this.confirmationId != -1) {
			resettingWorldTask.setConfirmationId(this.confirmationId);
		}

		RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, resettingWorldTask);
		realmsLongRunningMcoTaskScreen.start();
		Realms.setScreen(realmsLongRunningMcoTaskScreen);
	}

	public void resetWorld(RealmsResetWorldScreen.ResetWorldInfo resetWorldInfo) {
		if (this.slot == -1) {
			this.triggerResetWorld(resetWorldInfo);
		} else {
			this.typeToReset = RealmsResetWorldScreen.ResetType.GENERATE;
			this.worldInfoToReset = resetWorldInfo;
			this.switchSlot();
		}
	}

	private void triggerResetWorld(RealmsResetWorldScreen.ResetWorldInfo resetWorldInfo) {
		RealmsTasks.ResettingWorldTask resettingWorldTask = new RealmsTasks.ResettingWorldTask(
			this.serverData.id, this.returnScreen, resetWorldInfo.seed, resetWorldInfo.levelType, resetWorldInfo.generateStructures
		);
		if (this.resetTitle != null) {
			resettingWorldTask.setResetTitle(this.resetTitle);
		}

		if (this.confirmationId != -1) {
			resettingWorldTask.setConfirmationId(this.confirmationId);
		}

		RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, resettingWorldTask);
		realmsLongRunningMcoTaskScreen.start();
		Realms.setScreen(realmsLongRunningMcoTaskScreen);
	}

	@Environment(EnvType.CLIENT)
	abstract class FrameButton extends RealmsButton {
		private final long imageId;
		private final String image;
		private final RealmsResetWorldScreen.ResetType resetType;

		public FrameButton(int x, int y, String text, long imageId, String image, RealmsResetWorldScreen.ResetType resetType) {
			super(100 + resetType.ordinal(), x, y, 60, 72, text);
			this.imageId = imageId;
			this.image = image;
			this.resetType = resetType;
		}

		@Override
		public void tick() {
			super.tick();
		}

		@Override
		public void render(int xm, int ym, float a) {
			super.render(xm, ym, a);
		}

		@Override
		public void renderButton(int mouseX, int mouseY, float a) {
			RealmsResetWorldScreen.this.drawFrame(
				this.x(),
				this.y(),
				this.getProxy().getMessage(),
				this.imageId,
				this.image,
				this.resetType,
				this.getProxy().isHovered(),
				this.getProxy().isMouseOver((double)mouseX, (double)mouseY)
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
		String seed;
		int levelType;
		boolean generateStructures;

		public ResetWorldInfo(String seed, int levelType, boolean generateStructures) {
			this.seed = seed;
			this.levelType = levelType;
			this.generateStructures = generateStructures;
		}
	}
}
