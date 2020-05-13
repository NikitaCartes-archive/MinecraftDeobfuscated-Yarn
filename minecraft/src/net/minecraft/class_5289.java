package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class class_5289 extends Screen {
	private static final Identifier field_24566 = new Identifier("textures/gui/container/gamemode_switcher.png");
	private static final int field_24567 = class_5289.class_5290.values().length * 30 - 5;
	private final Optional<class_5289.class_5290> field_24568;
	private Optional<class_5289.class_5290> field_24569 = Optional.empty();
	private int field_24570;
	private int field_24571;
	private boolean field_24572;
	private final List<class_5289.class_5291> field_24573 = Lists.<class_5289.class_5291>newArrayList();

	public class_5289() {
		super(NarratorManager.EMPTY);
		this.field_24568 = class_5289.class_5290.method_28076(MinecraftClient.getInstance().interactionManager.method_28107());
	}

	@Override
	protected void init() {
		super.init();
		this.field_24569 = this.field_24568.isPresent() ? this.field_24568 : class_5289.class_5290.method_28076(this.client.interactionManager.getCurrentGameMode());

		for (int i = 0; i < class_5289.class_5290.field_24580.length; i++) {
			class_5289.class_5290 lv = class_5289.class_5290.field_24580[i];
			this.field_24573.add(new class_5289.class_5291(lv, this.width / 2 - field_24567 / 2 + i * 30, this.height / 2 - 30));
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (!this.method_28069()) {
			matrices.push();
			RenderSystem.enableBlend();
			this.client.getTextureManager().bindTexture(field_24566);
			int i = this.width / 2 - 62;
			int j = this.height / 2 - 30 - 27;
			drawTexture(matrices, i, j, 0.0F, 0.0F, 125, 75, 128, 128);
			matrices.pop();
			super.render(matrices, mouseX, mouseY, delta);
			this.field_24569.ifPresent(arg -> this.drawCenteredText(matrices, this.textRenderer, arg.method_28070(), this.width / 2, this.height / 2 - 30 - 20, -1));
			int k = this.textRenderer.getWidth(I18n.translate("debug.gamemodes.press_f4"));
			this.method_28063(matrices, I18n.translate("debug.gamemodes.press_f4"), I18n.translate("debug.gamemodes.select_next"), 5, k);
			if (!this.field_24572) {
				this.field_24570 = mouseX;
				this.field_24571 = mouseY;
				this.field_24572 = true;
			}

			boolean bl = this.field_24570 == mouseX && this.field_24571 == mouseY;

			for (class_5289.class_5291 lv : this.field_24573) {
				lv.render(matrices, mouseX, mouseY, delta);
				this.field_24569.ifPresent(arg2 -> lv.method_28083(arg2 == lv.field_24586));
				if (!bl && lv.isHovered()) {
					this.field_24569 = Optional.of(lv.field_24586);
				}
			}
		}
	}

	private void method_28063(MatrixStack matrixStack, String string, String string2, int i, int j) {
		int k = 5636095;
		int l = 16777215;
		this.drawStringWithShadow(matrixStack, this.textRenderer, "[", this.width / 2 - j - 18, this.height / 2 + i, 5636095);
		this.drawCenteredString(matrixStack, this.textRenderer, string, this.width / 2 - j / 2 - 10, this.height / 2 + i, 5636095);
		this.drawCenteredString(matrixStack, this.textRenderer, "]", this.width / 2 - 5, this.height / 2 + i, 5636095);
		this.drawStringWithShadow(matrixStack, this.textRenderer, string2, this.width / 2 + 5, this.height / 2 + i, 16777215);
	}

	private void method_28068() {
		method_28064(this.client, this.field_24569);
	}

	private static void method_28064(MinecraftClient minecraftClient, Optional<class_5289.class_5290> optional) {
		if (minecraftClient.interactionManager != null && minecraftClient.player != null && optional.isPresent()) {
			Optional<class_5289.class_5290> optional2 = class_5289.class_5290.method_28076(minecraftClient.interactionManager.getCurrentGameMode());
			class_5289.class_5290 lv = (class_5289.class_5290)optional.get();
			if (optional2.isPresent() && minecraftClient.player.hasPermissionLevel(2) && lv != optional2.get()) {
				minecraftClient.player.sendChatMessage(lv.method_28075());
			}
		}
	}

	private boolean method_28069() {
		if (!InputUtil.isKeyPressed(this.client.getWindow().getHandle(), 292)) {
			this.method_28068();
			this.client.openScreen(null);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 293 && this.field_24569.isPresent()) {
			this.field_24572 = false;
			this.field_24569 = ((class_5289.class_5290)this.field_24569.get()).method_28078();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	static enum class_5290 {
		field_24576(new TranslatableText("gameMode.creative"), "/gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
		field_24577(new TranslatableText("gameMode.survival"), "/gamemode survival", new ItemStack(Items.IRON_SWORD)),
		field_24578(new TranslatableText("gameMode.adventure"), "/gamemode adventure", new ItemStack(Items.MAP)),
		field_24579(new TranslatableText("gameMode.spectator"), "/gamemode spectator", new ItemStack(Items.ENDER_EYE));

		protected static final class_5289.class_5290[] field_24580 = values();
		final Text field_24581;
		final String field_24582;
		final ItemStack field_24583;

		private class_5290(Text text, String string2, ItemStack itemStack) {
			this.field_24581 = text;
			this.field_24582 = string2;
			this.field_24583 = itemStack;
		}

		private void method_28074(ItemRenderer itemRenderer, int i, int j) {
			itemRenderer.renderGuiItem(this.field_24583, i, j);
		}

		private Text method_28070() {
			return this.field_24581;
		}

		private String method_28075() {
			return this.field_24582;
		}

		private Optional<class_5289.class_5290> method_28078() {
			switch (this) {
				case field_24576:
					return Optional.of(field_24577);
				case field_24577:
					return Optional.of(field_24578);
				case field_24578:
					return Optional.of(field_24579);
				default:
					return Optional.of(field_24576);
			}
		}

		private static Optional<class_5289.class_5290> method_28076(GameMode gameMode) {
			switch (gameMode) {
				case SPECTATOR:
					return Optional.of(field_24579);
				case SURVIVAL:
					return Optional.of(field_24577);
				case CREATIVE:
					return Optional.of(field_24576);
				case ADVENTURE:
					return Optional.of(field_24578);
				default:
					return Optional.empty();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public class class_5291 extends AbstractButtonWidget {
		private final class_5289.class_5290 field_24586;
		private boolean field_24587;

		public class_5291(class_5289.class_5290 arg2, int i, int j) {
			super(i, j, 25, 25, arg2.method_28070());
			this.field_24586 = arg2;
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			this.method_28080(matrices, minecraftClient.getTextureManager());
			this.field_24586.method_28074(class_5289.this.itemRenderer, this.x + 5, this.y + 5);
			if (this.field_24587) {
				this.method_28082(matrices, minecraftClient.getTextureManager());
			}
		}

		@Override
		public boolean isHovered() {
			return super.isHovered() || this.field_24587;
		}

		public void method_28083(boolean bl) {
			this.field_24587 = bl;
			this.narrate();
		}

		private void method_28080(MatrixStack matrixStack, TextureManager textureManager) {
			textureManager.bindTexture(class_5289.field_24566);
			matrixStack.push();
			matrixStack.translate((double)this.x, (double)this.y, 0.0);
			drawTexture(matrixStack, 0, 0, 0.0F, 75.0F, 25, 25, 128, 128);
			matrixStack.pop();
		}

		private void method_28082(MatrixStack matrixStack, TextureManager textureManager) {
			textureManager.bindTexture(class_5289.field_24566);
			matrixStack.push();
			matrixStack.translate((double)this.x, (double)this.y, 0.0);
			drawTexture(matrixStack, 0, 0, 25.0F, 75.0F, 25, 25, 128, 128);
			matrixStack.pop();
		}
	}
}
