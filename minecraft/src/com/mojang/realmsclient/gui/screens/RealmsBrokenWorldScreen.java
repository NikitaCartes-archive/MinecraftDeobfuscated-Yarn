package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.OpenServerTask;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.SwitchSlotTask;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsBrokenWorldScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Screen lastScreen;
	private final RealmsMainScreen mainScreen;
	private RealmsServer field_20492;
	private final long serverId;
	private final Text field_24204;
	private final Text[] message = new Text[]{new TranslatableText("mco.brokenworld.message.line1"), new TranslatableText("mco.brokenworld.message.line2")};
	private int left_x;
	private int right_x;
	private final List<Integer> slotsThatHasBeenDownloaded = Lists.<Integer>newArrayList();
	private int animTick;

	public RealmsBrokenWorldScreen(Screen screen, RealmsMainScreen mainScreen, long serverId, boolean bl) {
		this.lastScreen = screen;
		this.mainScreen = mainScreen;
		this.serverId = serverId;
		this.field_24204 = bl ? new TranslatableText("mco.brokenworld.minigame.title") : new TranslatableText("mco.brokenworld.title");
	}

	@Override
	public void init() {
		this.left_x = this.width / 2 - 150;
		this.right_x = this.width / 2 + 190;
		this.addButton(new ButtonWidget(this.right_x - 80 + 8, row(13) - 5, 70, 20, ScreenTexts.BACK, buttonWidget -> this.backButtonClicked()));
		if (this.field_20492 == null) {
			this.fetchServerData(this.serverId);
		} else {
			this.addButtons();
		}

		this.client.keyboard.enableRepeatEvents(true);
		Realms.narrateNow((String)Stream.concat(Stream.of(this.field_24204), Stream.of(this.message)).map(Text::getString).collect(Collectors.joining(" ")));
	}

	private void addButtons() {
		for (Entry<Integer, RealmsWorldOptions> entry : this.field_20492.slots.entrySet()) {
			int i = (Integer)entry.getKey();
			boolean bl = i != this.field_20492.activeSlot || this.field_20492.worldType == RealmsServer.WorldType.MINIGAME;
			ButtonWidget buttonWidget;
			if (bl) {
				buttonWidget = new ButtonWidget(
					this.getFramePositionX(i),
					row(8),
					80,
					20,
					new TranslatableText("mco.brokenworld.play"),
					buttonWidgetx -> {
						if (((RealmsWorldOptions)this.field_20492.slots.get(i)).empty) {
							RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(
								this,
								this.field_20492,
								new TranslatableText("mco.configure.world.switch.slot"),
								new TranslatableText("mco.configure.world.switch.slot.subtitle"),
								10526880,
								ScreenTexts.CANCEL,
								this::method_25123,
								() -> {
									this.client.openScreen(this);
									this.method_25123();
								}
							);
							realmsResetWorldScreen.setSlot(i);
							realmsResetWorldScreen.setResetTitle(I18n.translate("mco.create.world.reset.title"));
							this.client.openScreen(realmsResetWorldScreen);
						} else {
							this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(this.field_20492.id, i, this::method_25123)));
						}
					}
				);
			} else {
				buttonWidget = new ButtonWidget(this.getFramePositionX(i), row(8), 80, 20, new TranslatableText("mco.brokenworld.download"), buttonWidgetx -> {
					Text text = new TranslatableText("mco.configure.world.restore.download.question.line1");
					Text text2 = new TranslatableText("mco.configure.world.restore.download.question.line2");
					this.client.openScreen(new RealmsLongConfirmationScreen(blx -> {
						if (blx) {
							this.downloadWorld(i);
						} else {
							this.client.openScreen(this);
						}
					}, RealmsLongConfirmationScreen.Type.Info, text, text2, true));
				});
			}

			if (this.slotsThatHasBeenDownloaded.contains(i)) {
				buttonWidget.active = false;
				buttonWidget.setMessage(new TranslatableText("mco.brokenworld.downloaded"));
			}

			this.addButton(buttonWidget);
			this.addButton(new ButtonWidget(this.getFramePositionX(i), row(10), 80, 20, new TranslatableText("mco.brokenworld.reset"), buttonWidgetx -> {
				RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(this, this.field_20492, this::method_25123, () -> {
					this.client.openScreen(this);
					this.method_25123();
				});
				if (i != this.field_20492.activeSlot || this.field_20492.worldType == RealmsServer.WorldType.MINIGAME) {
					realmsResetWorldScreen.setSlot(i);
				}

				this.client.openScreen(realmsResetWorldScreen);
			}));
		}
	}

	@Override
	public void tick() {
		this.animTick++;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawStringWithShadow(matrices, this.textRenderer, this.field_24204, this.width / 2, 17, 16777215);

		for (int i = 0; i < this.message.length; i++) {
			this.drawStringWithShadow(matrices, this.textRenderer, this.message[i], this.width / 2, row(-1) + 3 + i * 12, 10526880);
		}

		if (this.field_20492 != null) {
			for (Entry<Integer, RealmsWorldOptions> entry : this.field_20492.slots.entrySet()) {
				if (((RealmsWorldOptions)entry.getValue()).templateImage != null && ((RealmsWorldOptions)entry.getValue()).templateId != -1L) {
					this.drawSlotFrame(
						matrices,
						this.getFramePositionX((Integer)entry.getKey()),
						row(1) + 5,
						mouseX,
						mouseY,
						this.field_20492.activeSlot == (Integer)entry.getKey() && !this.isMinigame(),
						((RealmsWorldOptions)entry.getValue()).getSlotName((Integer)entry.getKey()),
						(Integer)entry.getKey(),
						((RealmsWorldOptions)entry.getValue()).templateId,
						((RealmsWorldOptions)entry.getValue()).templateImage,
						((RealmsWorldOptions)entry.getValue()).empty
					);
				} else {
					this.drawSlotFrame(
						matrices,
						this.getFramePositionX((Integer)entry.getKey()),
						row(1) + 5,
						mouseX,
						mouseY,
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
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.backButtonClicked();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private void backButtonClicked() {
		this.client.openScreen(this.lastScreen);
	}

	private void fetchServerData(long worldId) {
		new Thread(() -> {
			RealmsClient realmsClient = RealmsClient.createRealmsClient();

			try {
				this.field_20492 = realmsClient.getOwnWorld(worldId);
				this.addButtons();
			} catch (RealmsServiceException var5) {
				LOGGER.error("Couldn't get own world");
				this.client.openScreen(new RealmsGenericErrorScreen(new LiteralText(var5.getMessage()), this.lastScreen));
			}
		}).start();
	}

	public void method_25123() {
		new Thread(
				() -> {
					RealmsClient realmsClient = RealmsClient.createRealmsClient();
					if (this.field_20492.state == RealmsServer.State.CLOSED) {
						this.client
							.execute(() -> this.client.openScreen(new RealmsLongRunningMcoTaskScreen(this, new OpenServerTask(this.field_20492, this, this.mainScreen, true))));
					} else {
						try {
							this.mainScreen.newScreen().play(realmsClient.getOwnWorld(this.serverId), this);
						} catch (RealmsServiceException var3) {
							LOGGER.error("Couldn't get own world");
							this.client.execute(() -> this.client.openScreen(this.lastScreen));
						}
					}
				}
			)
			.start();
	}

	private void downloadWorld(int slotId) {
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			WorldDownload worldDownload = realmsClient.download(this.field_20492.id, slotId);
			RealmsDownloadLatestWorldScreen realmsDownloadLatestWorldScreen = new RealmsDownloadLatestWorldScreen(
				this, worldDownload, this.field_20492.getWorldName(slotId), bl -> {
					if (bl) {
						this.slotsThatHasBeenDownloaded.add(slotId);
						this.children.clear();
						this.addButtons();
					} else {
						this.client.openScreen(this);
					}
				}
			);
			this.client.openScreen(realmsDownloadLatestWorldScreen);
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't download world data");
			this.client.openScreen(new RealmsGenericErrorScreen(var5, this));
		}
	}

	private boolean isMinigame() {
		return this.field_20492 != null && this.field_20492.worldType == RealmsServer.WorldType.MINIGAME;
	}

	private void drawSlotFrame(MatrixStack matrixStack, int y, int xm, int ym, int i, boolean bl, String string, int j, long l, String string2, boolean bl2) {
		if (bl2) {
			this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.EMPTY_FRAME);
		} else if (string2 != null && l != -1L) {
			RealmsTextureManager.bindWorldTemplate(String.valueOf(l), string2);
		} else if (j == 1) {
			this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.PANORAMA_0);
		} else if (j == 2) {
			this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.PANORAMA_2);
		} else if (j == 3) {
			this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.PANORAMA_3);
		} else {
			RealmsTextureManager.bindWorldTemplate(String.valueOf(this.field_20492.minigameId), this.field_20492.minigameImage);
		}

		if (!bl) {
			RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		} else if (bl) {
			float f = 0.9F + 0.1F * MathHelper.cos((float)this.animTick * 0.2F);
			RenderSystem.color4f(f, f, f, 1.0F);
		}

		DrawableHelper.drawTexture(matrixStack, y + 3, xm + 3, 0.0F, 0.0F, 74, 74, 74, 74);
		this.client.getTextureManager().bindTexture(RealmsWorldSlotButton.SLOT_FRAME);
		if (bl) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		}

		DrawableHelper.drawTexture(matrixStack, y, xm, 0.0F, 0.0F, 80, 80, 80, 80);
		this.drawCenteredString(matrixStack, this.textRenderer, string, y + 40, xm + 66, 16777215);
	}
}
