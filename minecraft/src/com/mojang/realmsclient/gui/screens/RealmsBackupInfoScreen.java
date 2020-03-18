package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.dto.Backup;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RealmsBackupInfoScreen extends RealmsScreen {
	private final Screen lastScreen;
	private final Backup backup;
	private final List<String> keys = Lists.<String>newArrayList();
	private RealmsBackupInfoScreen.BackupInfoList backupInfoList;

	public RealmsBackupInfoScreen(Screen screen, Backup backup) {
		this.lastScreen = screen;
		this.backup = backup;
		if (backup.changeList != null) {
			for (Entry<String, String> entry : backup.changeList.entrySet()) {
				this.keys.add(entry.getKey());
			}
		}
	}

	@Override
	public void tick() {
	}

	@Override
	public void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20, I18n.translate("gui.back"), buttonWidget -> this.client.openScreen(this.lastScreen)
			)
		);
		this.backupInfoList = new RealmsBackupInfoScreen.BackupInfoList(this.client);
		this.addChild(this.backupInfoList);
		this.focusOn(this.backupInfoList);
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

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, "Changes from last backup", this.width / 2, 10, 16777215);
		this.backupInfoList.render(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}

	private String checkForSpecificMetadata(String key, String value) {
		String string = key.toLowerCase(Locale.ROOT);
		if (string.contains("game") && string.contains("mode")) {
			return this.gameModeMetadata(value);
		} else {
			return string.contains("game") && string.contains("difficulty") ? this.gameDifficultyMetadata(value) : value;
		}
	}

	private String gameDifficultyMetadata(String value) {
		try {
			return I18n.translate(RealmsSlotOptionsScreen.DIFFICULTIES[Integer.parseInt(value)]);
		} catch (Exception var3) {
			return "UNKNOWN";
		}
	}

	private String gameModeMetadata(String value) {
		try {
			return I18n.translate(RealmsSlotOptionsScreen.GAME_MODES[Integer.parseInt(value)]);
		} catch (Exception var3) {
			return "UNKNOWN";
		}
	}

	@Environment(EnvType.CLIENT)
	class BackupInfoList extends ListWidget {
		public BackupInfoList(MinecraftClient minecraftClient) {
			super(minecraftClient, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
		}

		@Override
		public int getItemCount() {
			return RealmsBackupInfoScreen.this.backup.changeList.size();
		}

		@Override
		protected void renderItem(int index, int x, int y, int itemHeight, int mouseX, int mouseY, float delta) {
			String string = (String)RealmsBackupInfoScreen.this.keys.get(index);
			TextRenderer textRenderer = this.client.textRenderer;
			this.drawString(textRenderer, string, this.width / 2 - 40, y, 10526880);
			String string2 = (String)RealmsBackupInfoScreen.this.backup.changeList.get(string);
			this.drawString(textRenderer, RealmsBackupInfoScreen.this.checkForSpecificMetadata(string, string2), this.width / 2 - 40, y + 12, 16777215);
		}

		@Override
		public boolean isSelectedItem(int index) {
			return false;
		}

		@Override
		public void renderBackground() {
		}

		@Override
		public void render(int mouseX, int mouseY, float delta) {
			if (this.visible) {
				this.renderBackground();
				int i = this.getScrollbarPosition();
				int j = i + 6;
				this.capYPosition();
				RenderSystem.disableFog();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				int k = this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
				int l = this.top + 4 - (int)this.scrollAmount;
				if (this.renderHeader) {
					this.renderHeader(k, l, tessellator);
				}

				this.renderList(k, l, mouseX, mouseY, delta);
				RenderSystem.disableDepthTest();
				this.renderHoleBackground(0, this.top, 255, 255);
				this.renderHoleBackground(this.bottom, this.height, 255, 255);
				RenderSystem.enableBlend();
				RenderSystem.blendFuncSeparate(
					GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE
				);
				RenderSystem.disableAlphaTest();
				RenderSystem.shadeModel(7425);
				RenderSystem.disableTexture();
				int m = this.getMaxScroll();
				if (m > 0) {
					int n = (this.bottom - this.top) * (this.bottom - this.top) / this.getMaxPosition();
					n = MathHelper.clamp(n, 32, this.bottom - this.top - 8);
					int o = (int)this.scrollAmount * (this.bottom - this.top - n) / m + this.top;
					if (o < this.top) {
						o = this.top;
					}

					bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
					bufferBuilder.vertex((double)i, (double)this.bottom, 0.0).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
					bufferBuilder.vertex((double)j, (double)this.bottom, 0.0).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
					bufferBuilder.vertex((double)j, (double)this.top, 0.0).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
					bufferBuilder.vertex((double)i, (double)this.top, 0.0).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
					tessellator.draw();
					bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
					bufferBuilder.vertex((double)i, (double)(o + n), 0.0).texture(0.0F, 1.0F).color(128, 128, 128, 255).next();
					bufferBuilder.vertex((double)j, (double)(o + n), 0.0).texture(1.0F, 1.0F).color(128, 128, 128, 255).next();
					bufferBuilder.vertex((double)j, (double)o, 0.0).texture(1.0F, 0.0F).color(128, 128, 128, 255).next();
					bufferBuilder.vertex((double)i, (double)o, 0.0).texture(0.0F, 0.0F).color(128, 128, 128, 255).next();
					tessellator.draw();
					bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
					bufferBuilder.vertex((double)i, (double)(o + n - 1), 0.0).texture(0.0F, 1.0F).color(192, 192, 192, 255).next();
					bufferBuilder.vertex((double)(j - 1), (double)(o + n - 1), 0.0).texture(1.0F, 1.0F).color(192, 192, 192, 255).next();
					bufferBuilder.vertex((double)(j - 1), (double)o, 0.0).texture(1.0F, 0.0F).color(192, 192, 192, 255).next();
					bufferBuilder.vertex((double)i, (double)o, 0.0).texture(0.0F, 0.0F).color(192, 192, 192, 255).next();
					tessellator.draw();
				}

				this.renderDecorations(mouseX, mouseY);
				RenderSystem.enableTexture();
				RenderSystem.shadeModel(7424);
				RenderSystem.enableAlphaTest();
				RenderSystem.disableBlend();
			}
		}
	}
}
