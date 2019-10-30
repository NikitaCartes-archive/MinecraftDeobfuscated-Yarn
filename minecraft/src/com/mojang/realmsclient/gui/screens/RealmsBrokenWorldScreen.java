package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsBrokenWorldScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RealmsScreen lastScreen;
	private final RealmsMainScreen mainScreen;
	private RealmsServer field_20492;
	private final long serverId;
	private String title = getLocalizedString("mco.brokenworld.title");
	private final String message = getLocalizedString("mco.brokenworld.message.line1") + "\\n" + getLocalizedString("mco.brokenworld.message.line2");
	private int left_x;
	private int right_x;
	private final int default_button_width = 80;
	private final int default_button_offset = 5;
	private static final List<Integer> playButtonIds = Arrays.asList(1, 2, 3);
	private static final List<Integer> resetButtonIds = Arrays.asList(4, 5, 6);
	private static final List<Integer> downloadButtonIds = Arrays.asList(7, 8, 9);
	private static final List<Integer> downloadConfirmationIds = Arrays.asList(10, 11, 12);
	private final List<Integer> slotsThatHasBeenDownloaded = Lists.<Integer>newArrayList();
	private int animTick;

	public RealmsBrokenWorldScreen(RealmsScreen lastScreen, RealmsMainScreen mainScreen, long serverId) {
		this.lastScreen = lastScreen;
		this.mainScreen = mainScreen;
		this.serverId = serverId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void init() {
		this.left_x = this.width() / 2 - 150;
		this.right_x = this.width() / 2 + 190;
		this.buttonsAdd(new RealmsButton(0, this.right_x - 80 + 8, RealmsConstants.row(13) - 5, 70, 20, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				RealmsBrokenWorldScreen.this.backButtonClicked();
			}
		});
		if (this.field_20492 == null) {
			this.fetchServerData(this.serverId);
		} else {
			this.addButtons();
		}

		this.setKeyboardHandlerSendRepeatsToGui(true);
	}

	public void addButtons() {
		for (Entry<Integer, RealmsWorldOptions> entry : this.field_20492.slots.entrySet()) {
			RealmsWorldOptions realmsWorldOptions = (RealmsWorldOptions)entry.getValue();
			boolean bl = (Integer)entry.getKey() != this.field_20492.activeSlot || this.field_20492.worldType.equals(RealmsServer.WorldType.MINIGAME);
			RealmsButton realmsButton;
			if (bl) {
				realmsButton = new RealmsBrokenWorldScreen.PlayButton(
					(Integer)playButtonIds.get((Integer)entry.getKey() - 1), this.getFramePositionX((Integer)entry.getKey()), getLocalizedString("mco.brokenworld.play")
				);
			} else {
				realmsButton = new RealmsBrokenWorldScreen.DownloadButton(
					(Integer)downloadButtonIds.get((Integer)entry.getKey() - 1),
					this.getFramePositionX((Integer)entry.getKey()),
					getLocalizedString("mco.brokenworld.download")
				);
			}

			if (this.slotsThatHasBeenDownloaded.contains(entry.getKey())) {
				realmsButton.active(false);
				realmsButton.setMessage(getLocalizedString("mco.brokenworld.downloaded"));
			}

			this.buttonsAdd(realmsButton);
			this.buttonsAdd(
				new RealmsButton(
					(Integer)resetButtonIds.get((Integer)entry.getKey() - 1),
					this.getFramePositionX((Integer)entry.getKey()),
					RealmsConstants.row(10),
					80,
					20,
					getLocalizedString("mco.brokenworld.reset")
				) {
					@Override
					public void onPress() {
						int i = RealmsBrokenWorldScreen.resetButtonIds.indexOf(this.id()) + 1;
						RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(
							RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.field_20492, RealmsBrokenWorldScreen.this
						);
						if (i != RealmsBrokenWorldScreen.this.field_20492.activeSlot
							|| RealmsBrokenWorldScreen.this.field_20492.worldType.equals(RealmsServer.WorldType.MINIGAME)) {
							realmsResetWorldScreen.setSlot(i);
						}

						realmsResetWorldScreen.setConfirmationId(14);
						Realms.setScreen(realmsResetWorldScreen);
					}
				}
			);
		}
	}

	@Override
	public void tick() {
		this.animTick++;
	}

	@Override
	public void render(int xm, int ym, float a) {
		this.renderBackground();
		super.render(xm, ym, a);
		this.drawCenteredString(this.title, this.width() / 2, 17, 16777215);
		String[] strings = this.message.split("\\\\n");

		for (int i = 0; i < strings.length; i++) {
			this.drawCenteredString(strings[i], this.width() / 2, RealmsConstants.row(-1) + 3 + i * 12, 10526880);
		}

		if (this.field_20492 != null) {
			for (Entry<Integer, RealmsWorldOptions> entry : this.field_20492.slots.entrySet()) {
				if (((RealmsWorldOptions)entry.getValue()).templateImage != null && ((RealmsWorldOptions)entry.getValue()).templateId != -1L) {
					this.drawSlotFrame(
						this.getFramePositionX((Integer)entry.getKey()),
						RealmsConstants.row(1) + 5,
						xm,
						ym,
						this.field_20492.activeSlot == (Integer)entry.getKey() && !this.isMinigame(),
						((RealmsWorldOptions)entry.getValue()).getSlotName((Integer)entry.getKey()),
						(Integer)entry.getKey(),
						((RealmsWorldOptions)entry.getValue()).templateId,
						((RealmsWorldOptions)entry.getValue()).templateImage,
						((RealmsWorldOptions)entry.getValue()).empty
					);
				} else {
					this.drawSlotFrame(
						this.getFramePositionX((Integer)entry.getKey()),
						RealmsConstants.row(1) + 5,
						xm,
						ym,
						this.field_20492.activeSlot == (Integer)entry.getKey() && !this.isMinigame(),
						((RealmsWorldOptions)entry.getValue()).getSlotName((Integer)entry.getKey()),
						(Integer)entry.getKey(),
						-1L,
						null,
						((RealmsWorldOptions)entry.getValue()).empty
					);
				}
			}
		}
	}

	private int getFramePositionX(int i) {
		return this.left_x + (i - 1) * 110;
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public boolean keyPressed(int eventKey, int scancode, int mods) {
		if (eventKey == 256) {
			this.backButtonClicked();
			return true;
		} else {
			return super.keyPressed(eventKey, scancode, mods);
		}
	}

	private void backButtonClicked() {
		Realms.setScreen(this.lastScreen);
	}

	private void fetchServerData(long worldId) {
		new Thread(() -> {
			RealmsClient realmsClient = RealmsClient.createRealmsClient();

			try {
				this.field_20492 = realmsClient.getOwnWorld(worldId);
				this.addButtons();
			} catch (RealmsServiceException var5) {
				LOGGER.error("Couldn't get own world");
				Realms.setScreen(new RealmsGenericErrorScreen(var5.getMessage(), this.lastScreen));
			} catch (IOException var6) {
				LOGGER.error("Couldn't parse response getting own world");
			}
		}).start();
	}

	@Override
	public void confirmResult(boolean result, int id) {
		if (!result) {
			Realms.setScreen(this);
		} else {
			if (id != 13 && id != 14) {
				if (downloadButtonIds.contains(id)) {
					this.downloadWorld(downloadButtonIds.indexOf(id) + 1);
				} else if (downloadConfirmationIds.contains(id)) {
					this.slotsThatHasBeenDownloaded.add(downloadConfirmationIds.indexOf(id) + 1);
					this.childrenClear();
					this.addButtons();
				}
			} else {
				new Thread(() -> {
					RealmsClient realmsClient = RealmsClient.createRealmsClient();
					if (this.field_20492.state.equals(RealmsServer.State.CLOSED)) {
						RealmsTasks.OpenServerTask openServerTask = new RealmsTasks.OpenServerTask(this.field_20492, this, this.lastScreen, true);
						RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this, openServerTask);
						realmsLongRunningMcoTaskScreen.start();
						Realms.setScreen(realmsLongRunningMcoTaskScreen);
					} else {
						try {
							this.mainScreen.newScreen().play(realmsClient.getOwnWorld(this.serverId), this);
						} catch (RealmsServiceException var4) {
							LOGGER.error("Couldn't get own world");
							Realms.setScreen(this.lastScreen);
						} catch (IOException var5) {
							LOGGER.error("Couldn't parse response getting own world");
							Realms.setScreen(this.lastScreen);
						}
					}
				}).start();
			}
		}
	}

	private void downloadWorld(int slotId) {
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			WorldDownload worldDownload = realmsClient.download(this.field_20492.id, slotId);
			RealmsDownloadLatestWorldScreen realmsDownloadLatestWorldScreen = new RealmsDownloadLatestWorldScreen(
				this, worldDownload, this.field_20492.name + " (" + ((RealmsWorldOptions)this.field_20492.slots.get(slotId)).getSlotName(slotId) + ")"
			);
			realmsDownloadLatestWorldScreen.setConfirmationId((Integer)downloadConfirmationIds.get(slotId - 1));
			Realms.setScreen(realmsDownloadLatestWorldScreen);
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't download world data");
			Realms.setScreen(new RealmsGenericErrorScreen(var5, this));
		}
	}

	private boolean isMinigame() {
		return this.field_20492 != null && this.field_20492.worldType.equals(RealmsServer.WorldType.MINIGAME);
	}

	private void drawSlotFrame(int x, int y, int xm, int ym, boolean active, String text, int i, long imageId, String image, boolean empty) {
		if (empty) {
			bind("realms:textures/gui/realms/empty_frame.png");
		} else if (image != null && imageId != -1L) {
			RealmsTextureManager.bindWorldTemplate(String.valueOf(imageId), image);
		} else if (i == 1) {
			bind("textures/gui/title/background/panorama_0.png");
		} else if (i == 2) {
			bind("textures/gui/title/background/panorama_2.png");
		} else if (i == 3) {
			bind("textures/gui/title/background/panorama_3.png");
		} else {
			RealmsTextureManager.bindWorldTemplate(String.valueOf(this.field_20492.minigameId), this.field_20492.minigameImage);
		}

		if (!active) {
			RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		} else if (active) {
			float f = 0.9F + 0.1F * RealmsMth.cos((float)this.animTick * 0.2F);
			RenderSystem.color4f(f, f, f, 1.0F);
		}

		RealmsScreen.blit(x + 3, y + 3, 0.0F, 0.0F, 74, 74, 74, 74);
		bind("realms:textures/gui/realms/slot_frame.png");
		if (active) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		}

		RealmsScreen.blit(x, y, 0.0F, 0.0F, 80, 80, 80, 80);
		this.drawCenteredString(text, x + 40, y + 66, 16777215);
	}

	private void switchSlot(int id) {
		RealmsTasks.SwitchSlotTask switchSlotTask = new RealmsTasks.SwitchSlotTask(this.field_20492.id, id, this, 13);
		RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, switchSlotTask);
		realmsLongRunningMcoTaskScreen.start();
		Realms.setScreen(realmsLongRunningMcoTaskScreen);
	}

	@Environment(EnvType.CLIENT)
	class DownloadButton extends RealmsButton {
		public DownloadButton(int id, int x, String msg) {
			super(id, x, RealmsConstants.row(8), 80, 20, msg);
		}

		@Override
		public void onPress() {
			String string = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line1");
			String string2 = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line2");
			Realms.setScreen(new RealmsLongConfirmationScreen(RealmsBrokenWorldScreen.this, RealmsLongConfirmationScreen.Type.INFO, string, string2, true, this.id()));
		}
	}

	@Environment(EnvType.CLIENT)
	class PlayButton extends RealmsButton {
		public PlayButton(int id, int x, String msg) {
			super(id, x, RealmsConstants.row(8), 80, 20, msg);
		}

		@Override
		public void onPress() {
			int i = RealmsBrokenWorldScreen.playButtonIds.indexOf(this.id()) + 1;
			if (((RealmsWorldOptions)RealmsBrokenWorldScreen.this.field_20492.slots.get(i)).empty) {
				RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(
					RealmsBrokenWorldScreen.this,
					RealmsBrokenWorldScreen.this.field_20492,
					RealmsBrokenWorldScreen.this,
					RealmsScreen.getLocalizedString("mco.configure.world.switch.slot"),
					RealmsScreen.getLocalizedString("mco.configure.world.switch.slot.subtitle"),
					10526880,
					RealmsScreen.getLocalizedString("gui.cancel")
				);
				realmsResetWorldScreen.setSlot(i);
				realmsResetWorldScreen.setResetTitle(RealmsScreen.getLocalizedString("mco.create.world.reset.title"));
				realmsResetWorldScreen.setConfirmationId(14);
				Realms.setScreen(realmsResetWorldScreen);
			} else {
				RealmsBrokenWorldScreen.this.switchSlot(i);
			}
		}
	}
}
