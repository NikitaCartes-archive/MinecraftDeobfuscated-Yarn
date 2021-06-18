package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BeaconScreen extends HandledScreen<BeaconScreenHandler> {
	static final Identifier TEXTURE = new Identifier("textures/gui/container/beacon.png");
	private static final Text PRIMARY_POWER_TEXT = new TranslatableText("block.minecraft.beacon.primary");
	private static final Text SECONDARY_POWER_TEXT = new TranslatableText("block.minecraft.beacon.secondary");
	private final List<BeaconScreen.class_6392> field_33832 = Lists.<BeaconScreen.class_6392>newArrayList();
	@Nullable
	StatusEffect primaryEffect;
	@Nullable
	StatusEffect secondaryEffect;

	public BeaconScreen(BeaconScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundWidth = 230;
		this.backgroundHeight = 219;
		handler.addListener(new ScreenHandlerListener() {
			@Override
			public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
			}

			@Override
			public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
				BeaconScreen.this.primaryEffect = handler.getPrimaryEffect();
				BeaconScreen.this.secondaryEffect = handler.getSecondaryEffect();
			}
		});
	}

	private <T extends ClickableWidget & BeaconScreen.class_6392> void method_37076(T clickableWidget) {
		this.addDrawableChild(clickableWidget);
		this.field_33832.add(clickableWidget);
	}

	@Override
	protected void init() {
		super.init();
		this.field_33832.clear();
		this.method_37076(new BeaconScreen.DoneButtonWidget(this.x + 164, this.y + 107));
		this.method_37076(new BeaconScreen.CancelButtonWidget(this.x + 190, this.y + 107));

		for (int i = 0; i <= 2; i++) {
			int j = BeaconBlockEntity.EFFECTS_BY_LEVEL[i].length;
			int k = j * 22 + (j - 1) * 2;

			for (int l = 0; l < j; l++) {
				StatusEffect statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[i][l];
				BeaconScreen.EffectButtonWidget effectButtonWidget = new BeaconScreen.EffectButtonWidget(
					this.x + 76 + l * 24 - k / 2, this.y + 22 + i * 25, statusEffect, true, i
				);
				effectButtonWidget.active = false;
				this.method_37076(effectButtonWidget);
			}
		}

		int i = 3;
		int j = BeaconBlockEntity.EFFECTS_BY_LEVEL[3].length + 1;
		int k = j * 22 + (j - 1) * 2;

		for (int l = 0; l < j - 1; l++) {
			StatusEffect statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[3][l];
			BeaconScreen.EffectButtonWidget effectButtonWidget = new BeaconScreen.EffectButtonWidget(this.x + 167 + l * 24 - k / 2, this.y + 47, statusEffect, false, 3);
			effectButtonWidget.active = false;
			this.method_37076(effectButtonWidget);
		}

		BeaconScreen.EffectButtonWidget effectButtonWidget2 = new BeaconScreen.LevelTwoEffectButtonWidget(
			this.x + 167 + (j - 1) * 24 - k / 2, this.y + 47, BeaconBlockEntity.EFFECTS_BY_LEVEL[0][0]
		);
		effectButtonWidget2.visible = false;
		this.method_37076(effectButtonWidget2);
	}

	@Override
	public void handledScreenTick() {
		super.handledScreenTick();
		this.method_37078();
	}

	void method_37078() {
		int i = this.handler.getProperties();
		this.field_33832.forEach(arg -> arg.method_37080(i));
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		drawCenteredText(matrices, this.textRenderer, PRIMARY_POWER_TEXT, 62, 10, 14737632);
		drawCenteredText(matrices, this.textRenderer, SECONDARY_POWER_TEXT, 169, 10, 14737632);

		for (BeaconScreen.class_6392 lv : this.field_33832) {
			if (lv.method_37079()) {
				lv.renderTooltip(matrices, mouseX - this.x, mouseY - this.y);
				break;
			}
		}
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		this.itemRenderer.zOffset = 100.0F;
		this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.NETHERITE_INGOT), i + 20, j + 109);
		this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.EMERALD), i + 41, j + 109);
		this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.DIAMOND), i + 41 + 22, j + 109);
		this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
		this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
		this.itemRenderer.zOffset = 0.0F;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Environment(EnvType.CLIENT)
	abstract static class BaseButtonWidget extends PressableWidget implements BeaconScreen.class_6392 {
		private boolean disabled;

		protected BaseButtonWidget(int x, int y) {
			super(x, y, 22, 22, LiteralText.EMPTY);
		}

		protected BaseButtonWidget(int x, int y, Text message) {
			super(x, y, 22, 22, message);
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, BeaconScreen.TEXTURE);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			int i = 219;
			int j = 0;
			if (!this.active) {
				j += this.width * 2;
			} else if (this.disabled) {
				j += this.width * 1;
			} else if (this.isHovered()) {
				j += this.width * 3;
			}

			this.drawTexture(matrices, this.x, this.y, j, 219, this.width, this.height);
			this.renderExtra(matrices);
		}

		protected abstract void renderExtra(MatrixStack matrices);

		public boolean isDisabled() {
			return this.disabled;
		}

		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}

		@Override
		public boolean method_37079() {
			return this.hovered;
		}

		@Override
		public void appendNarrations(NarrationMessageBuilder builder) {
			this.method_37021(builder);
		}
	}

	@Environment(EnvType.CLIENT)
	class CancelButtonWidget extends BeaconScreen.IconButtonWidget {
		public CancelButtonWidget(int x, int y) {
			super(x, y, 112, 220, ScreenTexts.CANCEL);
		}

		@Override
		public void onPress() {
			BeaconScreen.this.client.player.closeHandledScreen();
		}

		@Override
		public void method_37080(int i) {
		}
	}

	@Environment(EnvType.CLIENT)
	class DoneButtonWidget extends BeaconScreen.IconButtonWidget {
		public DoneButtonWidget(int x, int y) {
			super(x, y, 90, 220, ScreenTexts.DONE);
		}

		@Override
		public void onPress() {
			BeaconScreen.this.client
				.getNetworkHandler()
				.sendPacket(new UpdateBeaconC2SPacket(StatusEffect.getRawId(BeaconScreen.this.primaryEffect), StatusEffect.getRawId(BeaconScreen.this.secondaryEffect)));
			BeaconScreen.this.client.player.closeHandledScreen();
		}

		@Override
		public void method_37080(int i) {
			this.active = BeaconScreen.this.handler.hasPayment() && BeaconScreen.this.primaryEffect != null;
		}
	}

	@Environment(EnvType.CLIENT)
	class EffectButtonWidget extends BeaconScreen.BaseButtonWidget {
		private final boolean primary;
		protected final int field_33833;
		private StatusEffect effect;
		private Sprite sprite;
		private Text tooltip;

		public EffectButtonWidget(int x, int y, StatusEffect statusEffect, boolean primary, int i) {
			super(x, y);
			this.primary = primary;
			this.field_33833 = i;
			this.init(statusEffect);
		}

		protected void init(StatusEffect statusEffect) {
			this.effect = statusEffect;
			this.sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(statusEffect);
			this.tooltip = this.getEffectName(statusEffect);
		}

		protected MutableText getEffectName(StatusEffect statusEffect) {
			return new TranslatableText(statusEffect.getTranslationKey());
		}

		@Override
		public void onPress() {
			if (!this.isDisabled()) {
				if (this.primary) {
					BeaconScreen.this.primaryEffect = this.effect;
				} else {
					BeaconScreen.this.secondaryEffect = this.effect;
				}

				BeaconScreen.this.method_37078();
			}
		}

		@Override
		public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
			BeaconScreen.this.renderTooltip(matrices, this.tooltip, mouseX, mouseY);
		}

		@Override
		protected void renderExtra(MatrixStack matrices) {
			RenderSystem.setShaderTexture(0, this.sprite.getAtlas().getId());
			drawSprite(matrices, this.x + 2, this.y + 2, this.getZOffset(), 18, 18, this.sprite);
		}

		@Override
		public void method_37080(int i) {
			this.active = this.field_33833 < i;
			this.setDisabled(this.effect == (this.primary ? BeaconScreen.this.primaryEffect : BeaconScreen.this.secondaryEffect));
		}

		@Override
		protected MutableText getNarrationMessage() {
			return this.getEffectName(this.effect);
		}
	}

	@Environment(EnvType.CLIENT)
	abstract class IconButtonWidget extends BeaconScreen.BaseButtonWidget {
		private final int u;
		private final int v;

		protected IconButtonWidget(int x, int y, int u, int v, Text message) {
			super(x, y, message);
			this.u = u;
			this.v = v;
		}

		@Override
		protected void renderExtra(MatrixStack matrices) {
			this.drawTexture(matrices, this.x + 2, this.y + 2, this.u, this.v, 18, 18);
		}

		@Override
		public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
			BeaconScreen.this.renderTooltip(matrices, BeaconScreen.this.title, mouseX, mouseY);
		}
	}

	@Environment(EnvType.CLIENT)
	class LevelTwoEffectButtonWidget extends BeaconScreen.EffectButtonWidget {
		public LevelTwoEffectButtonWidget(int x, int y, StatusEffect statusEffect) {
			super(x, y, statusEffect, false, 3);
		}

		@Override
		protected MutableText getEffectName(StatusEffect statusEffect) {
			return new TranslatableText(statusEffect.getTranslationKey()).append(" II");
		}

		@Override
		public void method_37080(int i) {
			if (BeaconScreen.this.primaryEffect != null) {
				this.visible = true;
				this.init(BeaconScreen.this.primaryEffect);
				super.method_37080(i);
			} else {
				this.visible = false;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	interface class_6392 {
		boolean method_37079();

		void renderTooltip(MatrixStack matrices, int mouseX, int mouseY);

		void method_37080(int i);
	}
}
