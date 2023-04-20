package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsWorldSlotButton;
import net.minecraft.client.realms.task.OpenServerTask;
import net.minecraft.client.realms.task.SwitchSlotTask;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsBrokenWorldScreen extends RealmsScreen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_32120 = 80;
	private final Screen parent;
	private final RealmsMainScreen mainScreen;
	@Nullable
	private RealmsServer serverData;
	private final long serverId;
	private final Text[] message = new Text[]{Text.translatable("mco.brokenworld.message.line1"), Text.translatable("mco.brokenworld.message.line2")};
	private int left_x;
	private int right_x;
	private final List<Integer> slotsThatHasBeenDownloaded = Lists.<Integer>newArrayList();
	private int animTick;

	public RealmsBrokenWorldScreen(Screen parent, RealmsMainScreen mainScreen, long serverId, boolean minigame) {
		super(minigame ? Text.translatable("mco.brokenworld.minigame.title") : Text.translatable("mco.brokenworld.title"));
		this.parent = parent;
		this.mainScreen = mainScreen;
		this.serverId = serverId;
	}

	@Override
	public void init() {
		this.left_x = this.width / 2 - 150;
		this.right_x = this.width / 2 + 190;
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.BACK, button -> this.backButtonClicked()).dimensions(this.right_x - 80 + 8, row(13) - 5, 70, 20).build()
		);
		if (this.serverData == null) {
			this.fetchServerData(this.serverId);
		} else {
			this.addButtons();
		}
	}

	@Override
	public Text getNarratedTitle() {
		return Texts.join((Collection<? extends Text>)Stream.concat(Stream.of(this.title), Stream.of(this.message)).collect(Collectors.toList()), ScreenTexts.SPACE);
	}

	private void addButtons() {
		for (Entry<Integer, RealmsWorldOptions> entry : this.serverData.slots.entrySet()) {
			int i = (Integer)entry.getKey();
			boolean bl = i != this.serverData.activeSlot || this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
			ButtonWidget buttonWidget;
			if (bl) {
				buttonWidget = ButtonWidget.builder(
						Text.translatable("mco.brokenworld.play"),
						button -> {
							if (((RealmsWorldOptions)this.serverData.slots.get(i)).empty) {
								RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(
									this,
									this.serverData,
									Text.translatable("mco.configure.world.switch.slot"),
									Text.translatable("mco.configure.world.switch.slot.subtitle"),
									10526880,
									ScreenTexts.CANCEL,
									this::play,
									() -> {
										this.client.setScreen(this);
										this.play();
									}
								);
								realmsResetWorldScreen.setSlot(i);
								realmsResetWorldScreen.setResetTitle(Text.translatable("mco.create.world.reset.title"));
								this.client.setScreen(realmsResetWorldScreen);
							} else {
								this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new SwitchSlotTask(this.serverData.id, i, this::play)));
							}
						}
					)
					.dimensions(this.getFramePositionX(i), row(8), 80, 20)
					.build();
			} else {
				buttonWidget = ButtonWidget.builder(Text.translatable("mco.brokenworld.download"), button -> {
					Text text = Text.translatable("mco.configure.world.restore.download.question.line1");
					Text text2 = Text.translatable("mco.configure.world.restore.download.question.line2");
					this.client.setScreen(new RealmsLongConfirmationScreen(confirmed -> {
						if (confirmed) {
							this.downloadWorld(i);
						} else {
							this.client.setScreen(this);
						}
					}, RealmsLongConfirmationScreen.Type.INFO, text, text2, true));
				}).dimensions(this.getFramePositionX(i), row(8), 80, 20).build();
			}

			if (this.slotsThatHasBeenDownloaded.contains(i)) {
				buttonWidget.active = false;
				buttonWidget.setMessage(Text.translatable("mco.brokenworld.downloaded"));
			}

			this.addDrawableChild(buttonWidget);
			this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.brokenworld.reset"), button -> {
				RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(this, this.serverData, this::play, () -> {
					this.client.setScreen(this);
					this.play();
				});
				if (i != this.serverData.activeSlot || this.serverData.worldType == RealmsServer.WorldType.MINIGAME) {
					realmsResetWorldScreen.setSlot(i);
				}

				this.client.setScreen(realmsResetWorldScreen);
			}).dimensions(this.getFramePositionX(i), row(10), 80, 20).build());
		}
	}

	@Override
	public void tick() {
		this.animTick++;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 17, 16777215);

		for (int i = 0; i < this.message.length; i++) {
			context.drawCenteredTextWithShadow(this.textRenderer, this.message[i], this.width / 2, row(-1) + 3 + i * 12, 10526880);
		}

		if (this.serverData != null) {
			for (Entry<Integer, RealmsWorldOptions> entry : this.serverData.slots.entrySet()) {
				if (((RealmsWorldOptions)entry.getValue()).templateImage != null && ((RealmsWorldOptions)entry.getValue()).templateId != -1L) {
					this.drawSlotFrame(
						context,
						this.getFramePositionX((Integer)entry.getKey()),
						row(1) + 5,
						mouseX,
						mouseY,
						this.serverData.activeSlot == (Integer)entry.getKey() && !this.isMinigame(),
						((RealmsWorldOptions)entry.getValue()).getSlotName((Integer)entry.getKey()),
						(Integer)entry.getKey(),
						((RealmsWorldOptions)entry.getValue()).templateId,
						((RealmsWorldOptions)entry.getValue()).templateImage,
						((RealmsWorldOptions)entry.getValue()).empty
					);
				} else {
					this.drawSlotFrame(
						context,
						this.getFramePositionX((Integer)entry.getKey()),
						row(1) + 5,
						mouseX,
						mouseY,
						this.serverData.activeSlot == (Integer)entry.getKey() && !this.isMinigame(),
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
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.backButtonClicked();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private void backButtonClicked() {
		this.client.setScreen(this.parent);
	}

	private void fetchServerData(long worldId) {
		new Thread(() -> {
			RealmsClient realmsClient = RealmsClient.create();

			try {
				this.serverData = realmsClient.getOwnWorld(worldId);
				this.addButtons();
			} catch (RealmsServiceException var5) {
				LOGGER.error("Couldn't get own world");
				this.client.setScreen(new RealmsGenericErrorScreen(Text.of(var5.getMessage()), this.parent));
			}
		}).start();
	}

	public void play() {
		new Thread(
				() -> {
					RealmsClient realmsClient = RealmsClient.create();
					if (this.serverData.state == RealmsServer.State.CLOSED) {
						this.client
							.execute(
								() -> this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this, new OpenServerTask(this.serverData, this, this.mainScreen, true, this.client)))
							);
					} else {
						try {
							RealmsServer realmsServer = realmsClient.getOwnWorld(this.serverId);
							this.client.execute(() -> this.mainScreen.newScreen().play(realmsServer, this));
						} catch (RealmsServiceException var3) {
							LOGGER.error("Couldn't get own world");
							this.client.execute(() -> this.client.setScreen(this.parent));
						}
					}
				}
			)
			.start();
	}

	private void downloadWorld(int slotId) {
		RealmsClient realmsClient = RealmsClient.create();

		try {
			WorldDownload worldDownload = realmsClient.download(this.serverData.id, slotId);
			RealmsDownloadLatestWorldScreen realmsDownloadLatestWorldScreen = new RealmsDownloadLatestWorldScreen(
				this, worldDownload, this.serverData.getWorldName(slotId), successful -> {
					if (successful) {
						this.slotsThatHasBeenDownloaded.add(slotId);
						this.clearChildren();
						this.addButtons();
					} else {
						this.client.setScreen(this);
					}
				}
			);
			this.client.setScreen(realmsDownloadLatestWorldScreen);
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't download world data");
			this.client.setScreen(new RealmsGenericErrorScreen(var5, this));
		}
	}

	private boolean isMinigame() {
		return this.serverData != null && this.serverData.worldType == RealmsServer.WorldType.MINIGAME;
	}

	private void drawSlotFrame(
		DrawContext context,
		int x,
		int y,
		int mouseX,
		int mouseY,
		boolean activeSlot,
		String slotName,
		int slotId,
		long templateId,
		@Nullable String templateImage,
		boolean empty
	) {
		Identifier identifier;
		if (empty) {
			identifier = RealmsWorldSlotButton.EMPTY_FRAME;
		} else if (templateImage != null && templateId != -1L) {
			identifier = RealmsTextureManager.getTextureId(String.valueOf(templateId), templateImage);
		} else if (slotId == 1) {
			identifier = RealmsWorldSlotButton.PANORAMA_0;
		} else if (slotId == 2) {
			identifier = RealmsWorldSlotButton.PANORAMA_2;
		} else if (slotId == 3) {
			identifier = RealmsWorldSlotButton.PANORAMA_3;
		} else {
			identifier = RealmsTextureManager.getTextureId(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage);
		}

		if (!activeSlot) {
			context.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
		} else if (activeSlot) {
			float f = 0.9F + 0.1F * MathHelper.cos((float)this.animTick * 0.2F);
			context.setShaderColor(f, f, f, 1.0F);
		}

		context.drawTexture(identifier, x + 3, y + 3, 0.0F, 0.0F, 74, 74, 74, 74);
		if (activeSlot) {
			context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			context.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
		}

		context.drawTexture(RealmsWorldSlotButton.SLOT_FRAME, x, y, 0.0F, 0.0F, 80, 80, 80, 80);
		context.drawCenteredTextWithShadow(this.textRenderer, slotName, x + 40, y + 66, 16777215);
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
