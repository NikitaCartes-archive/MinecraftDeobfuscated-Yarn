package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.FileDownload;
import com.mojang.realmsclient.dto.WorldDownload;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.SizeUnit;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsDownloadLatestWorldScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ReentrantLock downloadLock = new ReentrantLock();
	private final Screen parent;
	private final WorldDownload worldDownload;
	private final Text downloadTitle;
	private final RateLimiter narrationRateLimiter;
	private ButtonWidget field_22694;
	private final String worldName;
	private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
	private volatile Text field_20494;
	private volatile Text status = new TranslatableText("mco.download.preparing");
	private volatile String progress;
	private volatile boolean cancelled;
	private volatile boolean showDots = true;
	private volatile boolean finished;
	private volatile boolean extracting;
	private Long previousWrittenBytes;
	private Long previousTimeSnapshot;
	private long bytesPersSecond;
	private int animTick;
	private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
	private int dotIndex;
	private boolean checked;
	private final BooleanConsumer field_22693;

	public RealmsDownloadLatestWorldScreen(Screen parent, WorldDownload worldDownload, String worldName, BooleanConsumer booleanConsumer) {
		this.field_22693 = booleanConsumer;
		this.parent = parent;
		this.worldName = worldName;
		this.worldDownload = worldDownload;
		this.downloadStatus = new RealmsDownloadLatestWorldScreen.DownloadStatus();
		this.downloadTitle = new TranslatableText("mco.download.title");
		this.narrationRateLimiter = RateLimiter.create(0.1F);
	}

	@Override
	public void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.field_22694 = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 42, 200, 20, ScreenTexts.CANCEL, buttonWidget -> {
			this.cancelled = true;
			this.backButtonClicked();
		}));
		this.checkDownloadSize();
	}

	private void checkDownloadSize() {
		if (!this.finished) {
			if (!this.checked && this.getContentLength(this.worldDownload.downloadLink) >= 5368709120L) {
				Text text = new TranslatableText("mco.download.confirmation.line1", SizeUnit.getUserFriendlyString(5368709120L));
				Text text2 = new TranslatableText("mco.download.confirmation.line2");
				this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
					this.checked = true;
					this.client.openScreen(this);
					this.downloadSave();
				}, RealmsLongConfirmationScreen.Type.Warning, text, text2, false));
			} else {
				this.downloadSave();
			}
		}
	}

	private long getContentLength(String downloadLink) {
		FileDownload fileDownload = new FileDownload();
		return fileDownload.contentLength(downloadLink);
	}

	@Override
	public void tick() {
		super.tick();
		this.animTick++;
		if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
			List<Text> list = Lists.<Text>newArrayList();
			list.add(this.downloadTitle);
			list.add(this.status);
			if (this.progress != null) {
				list.add(new LiteralText(this.progress + "%"));
				list.add(new LiteralText(SizeUnit.getUserFriendlyString(this.bytesPersSecond) + "/s"));
			}

			if (this.field_20494 != null) {
				list.add(this.field_20494);
			}

			String string = (String)list.stream().map(Text::getString).collect(Collectors.joining("\n"));
			Realms.narrateNow(string);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.cancelled = true;
			this.backButtonClicked();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private void backButtonClicked() {
		if (this.finished && this.field_22693 != null && this.field_20494 == null) {
			this.field_22693.accept(true);
		}

		this.client.openScreen(this.parent);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		if (this.extracting && !this.finished) {
			this.status = new TranslatableText("mco.download.extracting");
		}

		this.drawCenteredText(matrices, this.textRenderer, this.downloadTitle, this.width / 2, 20, 16777215);
		this.drawCenteredText(matrices, this.textRenderer, this.status, this.width / 2, 50, 16777215);
		if (this.showDots) {
			this.drawDots(matrices);
		}

		if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
			this.drawProgressBar(matrices);
			this.drawDownloadSpeed(matrices);
		}

		if (this.field_20494 != null) {
			this.drawCenteredText(matrices, this.textRenderer, this.field_20494, this.width / 2, 110, 16711680);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	private void drawDots(MatrixStack matrixStack) {
		int i = this.textRenderer.getWidth(this.status);
		if (this.animTick % 10 == 0) {
			this.dotIndex++;
		}

		this.textRenderer.draw(matrixStack, DOTS[this.dotIndex % DOTS.length], (float)(this.width / 2 + i / 2 + 5), 50.0F, 16777215);
	}

	private void drawProgressBar(MatrixStack matrixStack) {
		double d = this.downloadStatus.bytesWritten.doubleValue() / this.downloadStatus.totalBytes.doubleValue() * 100.0;
		this.progress = String.format(Locale.ROOT, "%.1f", d);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		double e = (double)(this.width / 2 - 100);
		double f = 0.5;
		bufferBuilder.vertex(e - 0.5, 95.5, 0.0).color(217, 210, 210, 255).next();
		bufferBuilder.vertex(e + 200.0 * d / 100.0 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).next();
		bufferBuilder.vertex(e + 200.0 * d / 100.0 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).next();
		bufferBuilder.vertex(e - 0.5, 79.5, 0.0).color(217, 210, 210, 255).next();
		bufferBuilder.vertex(e, 95.0, 0.0).color(128, 128, 128, 255).next();
		bufferBuilder.vertex(e + 200.0 * d / 100.0, 95.0, 0.0).color(128, 128, 128, 255).next();
		bufferBuilder.vertex(e + 200.0 * d / 100.0, 80.0, 0.0).color(128, 128, 128, 255).next();
		bufferBuilder.vertex(e, 80.0, 0.0).color(128, 128, 128, 255).next();
		tessellator.draw();
		RenderSystem.enableTexture();
		this.drawCenteredString(matrixStack, this.textRenderer, this.progress + " %", this.width / 2, 84, 16777215);
	}

	private void drawDownloadSpeed(MatrixStack matrixStack) {
		if (this.animTick % 20 == 0) {
			if (this.previousWrittenBytes != null) {
				long l = Util.getMeasuringTimeMs() - this.previousTimeSnapshot;
				if (l == 0L) {
					l = 1L;
				}

				this.bytesPersSecond = 1000L * (this.downloadStatus.bytesWritten - this.previousWrittenBytes) / l;
				this.drawDownloadSpeed0(matrixStack, this.bytesPersSecond);
			}

			this.previousWrittenBytes = this.downloadStatus.bytesWritten;
			this.previousTimeSnapshot = Util.getMeasuringTimeMs();
		} else {
			this.drawDownloadSpeed0(matrixStack, this.bytesPersSecond);
		}
	}

	private void drawDownloadSpeed0(MatrixStack matrixStack, long l) {
		if (l > 0L) {
			int i = this.textRenderer.getWidth(this.progress);
			String string = "(" + SizeUnit.getUserFriendlyString(l) + "/s)";
			this.textRenderer.draw(matrixStack, string, (float)(this.width / 2 + i / 2 + 15), 84.0F, 16777215);
		}
	}

	private void downloadSave() {
		new Thread(() -> {
			try {
				try {
					if (!downloadLock.tryLock(1L, TimeUnit.SECONDS)) {
						this.status = new TranslatableText("mco.download.failed");
						return;
					}

					if (this.cancelled) {
						this.downloadCancelled();
						return;
					}

					this.status = new TranslatableText("mco.download.downloading", this.worldName);
					FileDownload fileDownload = new FileDownload();
					fileDownload.contentLength(this.worldDownload.downloadLink);
					fileDownload.downloadWorld(this.worldDownload, this.worldName, this.downloadStatus, this.client.getLevelStorage());

					while (!fileDownload.isFinished()) {
						if (fileDownload.isError()) {
							fileDownload.cancel();
							this.field_20494 = new TranslatableText("mco.download.failed");
							this.field_22694.setMessage(ScreenTexts.DONE);
							return;
						}

						if (fileDownload.isExtracting()) {
							this.extracting = true;
						}

						if (this.cancelled) {
							fileDownload.cancel();
							this.downloadCancelled();
							return;
						}

						try {
							Thread.sleep(500L);
						} catch (InterruptedException var8) {
							LOGGER.error("Failed to check Realms backup download status");
						}
					}

					this.finished = true;
					this.status = new TranslatableText("mco.download.done");
					this.field_22694.setMessage(ScreenTexts.DONE);
					return;
				} catch (InterruptedException var9) {
					LOGGER.error("Could not acquire upload lock");
				} catch (Exception var10) {
					this.field_20494 = new TranslatableText("mco.download.failed");
					var10.printStackTrace();
				}
			} finally {
				if (!downloadLock.isHeldByCurrentThread()) {
					return;
				} else {
					downloadLock.unlock();
					this.showDots = false;
					this.finished = true;
				}
			}
		}).start();
	}

	private void downloadCancelled() {
		this.status = new TranslatableText("mco.download.cancelled");
	}

	@Environment(EnvType.CLIENT)
	public class DownloadStatus {
		public volatile Long bytesWritten = 0L;
		public volatile Long totalBytes = 0L;
	}
}
