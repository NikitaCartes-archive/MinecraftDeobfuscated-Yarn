package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.container.BeaconContainer;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.packet.GuiCloseC2SPacket;
import net.minecraft.server.network.packet.UpdateBeaconC2SPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BeaconScreen extends ContainerScreen<BeaconContainer> {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/beacon.png");
	private BeaconScreen.WidgetButtonIconDone doneButton;
	private boolean canConsumeGem;
	private StatusEffect primaryEffect;
	private StatusEffect secondaryEffect;

	public BeaconScreen(BeaconContainer beaconContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(beaconContainer, playerInventory, textComponent);
		this.width = 230;
		this.height = 219;
		beaconContainer.addListener(new ContainerListener() {
			@Override
			public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
			}

			@Override
			public void onContainerSlotUpdate(Container container, int i, ItemStack itemStack) {
			}

			@Override
			public void onContainerPropertyUpdate(Container container, int i, int j) {
				BeaconScreen.this.primaryEffect = beaconContainer.getPrimaryEffect();
				BeaconScreen.this.secondaryEffect = beaconContainer.getSecondaryEffect();
				BeaconScreen.this.canConsumeGem = true;
			}
		});
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.doneButton = new BeaconScreen.WidgetButtonIconDone(this.left + 164, this.top + 107);
		this.addButton(this.doneButton);
		this.addButton(new BeaconScreen.WidgetButtonIconCancel(this.left + 190, this.top + 107));
		this.canConsumeGem = true;
		this.doneButton.active = false;
	}

	@Override
	public void update() {
		super.update();
		int i = this.container.method_17373();
		if (this.canConsumeGem && i >= 0) {
			this.canConsumeGem = false;

			for (int j = 0; j <= 2; j++) {
				int k = BeaconBlockEntity.EFFECTS_BY_LEVEL[j].length;
				int l = k * 22 + (k - 1) * 2;

				for (int m = 0; m < k; m++) {
					StatusEffect statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[j][m];
					BeaconScreen.WidgetButtonIconEffect widgetButtonIconEffect = new BeaconScreen.WidgetButtonIconEffect(
						this.left + 76 + m * 24 - l / 2, this.top + 22 + j * 25, statusEffect, true
					);
					this.addButton(widgetButtonIconEffect);
					if (j >= i) {
						widgetButtonIconEffect.active = false;
					} else if (statusEffect == this.primaryEffect) {
						widgetButtonIconEffect.setDisabled(true);
					}
				}
			}

			int j = 3;
			int k = BeaconBlockEntity.EFFECTS_BY_LEVEL[3].length + 1;
			int l = k * 22 + (k - 1) * 2;

			for (int mx = 0; mx < k - 1; mx++) {
				StatusEffect statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[3][mx];
				BeaconScreen.WidgetButtonIconEffect widgetButtonIconEffect = new BeaconScreen.WidgetButtonIconEffect(
					this.left + 167 + mx * 24 - l / 2, this.top + 47, statusEffect, false
				);
				this.addButton(widgetButtonIconEffect);
				if (3 >= i) {
					widgetButtonIconEffect.active = false;
				} else if (statusEffect == this.secondaryEffect) {
					widgetButtonIconEffect.setDisabled(true);
				}
			}

			if (this.primaryEffect != null) {
				BeaconScreen.WidgetButtonIconEffect widgetButtonIconEffect2 = new BeaconScreen.WidgetButtonIconEffect(
					this.left + 167 + (k - 1) * 24 - l / 2, this.top + 47, this.primaryEffect, false
				);
				this.addButton(widgetButtonIconEffect2);
				if (3 >= i) {
					widgetButtonIconEffect2.active = false;
				} else if (this.primaryEffect == this.secondaryEffect) {
					widgetButtonIconEffect2.setDisabled(true);
				}
			}
		}

		this.doneButton.active = this.container.hasPayment() && this.primaryEffect != null;
	}

	@Override
	protected void drawForeground(int i, int j) {
		GuiLighting.disable();
		this.drawStringCentered(this.fontRenderer, I18n.translate("block.minecraft.beacon.primary"), 62, 10, 14737632);
		this.drawStringCentered(this.fontRenderer, I18n.translate("block.minecraft.beacon.secondary"), 169, 10, 14737632);

		for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
			if (abstractButtonWidget.isHovered()) {
				abstractButtonWidget.renderToolTip(i - this.left, j - this.top);
				break;
			}
		}

		GuiLighting.enableForItems();
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(BG_TEX);
		int k = (this.screenWidth - this.width) / 2;
		int l = (this.screenHeight - this.height) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.width, this.height);
		this.itemRenderer.zOffset = 100.0F;
		this.itemRenderer.renderGuiItem(new ItemStack(Items.field_8687), k + 42, l + 109);
		this.itemRenderer.renderGuiItem(new ItemStack(Items.field_8477), k + 42 + 22, l + 109);
		this.itemRenderer.renderGuiItem(new ItemStack(Items.field_8695), k + 42 + 44, l + 109);
		this.itemRenderer.renderGuiItem(new ItemStack(Items.field_8620), k + 42 + 66, l + 109);
		this.itemRenderer.zOffset = 0.0F;
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		super.render(i, j, f);
		this.drawMouseoverTooltip(i, j);
	}

	@Environment(EnvType.CLIENT)
	abstract static class WidgetButtonIcon extends ButtonWidget {
		private boolean disabled;

		protected WidgetButtonIcon(int i, int j) {
			super(i, j, 22, 22, "");
		}

		@Override
		public void renderButton(int i, int j, float f) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(BeaconScreen.BG_TEX);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int k = 219;
			int l = 0;
			if (!this.active) {
				l += this.width * 2;
			} else if (this.disabled) {
				l += this.width * 1;
			} else if (this.isHovered()) {
				l += this.width * 3;
			}

			this.drawTexturedRect(this.x, this.y, l, 219, this.width, this.height);
			this.method_18641();
		}

		protected abstract void method_18641();

		public boolean isDisabled() {
			return this.disabled;
		}

		public void setDisabled(boolean bl) {
			this.disabled = bl;
		}
	}

	@Environment(EnvType.CLIENT)
	class WidgetButtonIconCancel extends BeaconScreen.class_4072 {
		public WidgetButtonIconCancel(int i, int j) {
			super(i, j, 112, 220);
		}

		@Override
		public void onPressed() {
			BeaconScreen.this.client.player.networkHandler.sendPacket(new GuiCloseC2SPacket(BeaconScreen.this.client.player.container.syncId));
			BeaconScreen.this.client.openScreen(null);
		}

		@Override
		public void renderToolTip(int i, int j) {
			BeaconScreen.this.drawTooltip(I18n.translate("gui.cancel"), i, j);
		}
	}

	@Environment(EnvType.CLIENT)
	class WidgetButtonIconDone extends BeaconScreen.class_4072 {
		public WidgetButtonIconDone(int i, int j) {
			super(i, j, 90, 220);
		}

		@Override
		public void onPressed() {
			BeaconScreen.this.client
				.getNetworkHandler()
				.sendPacket(new UpdateBeaconC2SPacket(StatusEffect.getRawId(BeaconScreen.this.primaryEffect), StatusEffect.getRawId(BeaconScreen.this.secondaryEffect)));
			BeaconScreen.this.client.player.networkHandler.sendPacket(new GuiCloseC2SPacket(BeaconScreen.this.client.player.container.syncId));
			BeaconScreen.this.client.openScreen(null);
		}

		@Override
		public void renderToolTip(int i, int j) {
			BeaconScreen.this.drawTooltip(I18n.translate("gui.done"), i, j);
		}
	}

	@Environment(EnvType.CLIENT)
	class WidgetButtonIconEffect extends BeaconScreen.WidgetButtonIcon {
		private final StatusEffect effect;
		private final Sprite field_18223;
		private final boolean primary;

		public WidgetButtonIconEffect(int i, int j, StatusEffect statusEffect, boolean bl) {
			super(i, j);
			this.effect = statusEffect;
			this.field_18223 = MinecraftClient.getInstance().method_18505().getSprite(statusEffect);
			this.primary = bl;
		}

		@Override
		public void onPressed() {
			if (!this.isDisabled()) {
				if (this.primary) {
					BeaconScreen.this.primaryEffect = this.effect;
				} else {
					BeaconScreen.this.secondaryEffect = this.effect;
				}

				BeaconScreen.this.buttons.clear();
				BeaconScreen.this.listeners.clear();
				BeaconScreen.this.onInitialized();
				BeaconScreen.this.update();
			}
		}

		@Override
		public void renderToolTip(int i, int j) {
			String string = I18n.translate(this.effect.getTranslationKey());
			if (!this.primary && this.effect != StatusEffects.field_5924) {
				string = string + " II";
			}

			BeaconScreen.this.drawTooltip(string, i, j);
		}

		@Override
		protected void method_18641() {
			MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.STATUS_EFFECT_ATLAS_TEX);
			this.drawTexturedRect(this.x + 2, this.y + 2, this.field_18223, 18, 18);
		}
	}

	@Environment(EnvType.CLIENT)
	abstract static class class_4072 extends BeaconScreen.WidgetButtonIcon {
		private final int field_18224;
		private final int field_18225;

		protected class_4072(int i, int j, int k, int l) {
			super(i, j);
			this.field_18224 = k;
			this.field_18225 = l;
		}

		@Override
		protected void method_18641() {
			this.drawTexturedRect(this.x + 2, this.y + 2, this.field_18224, this.field_18225, 18, 18);
		}
	}
}
