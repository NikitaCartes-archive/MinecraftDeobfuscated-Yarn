package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
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
		this.containerWidth = 230;
		this.containerHeight = 219;
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
		this.doneButton = new BeaconScreen.WidgetButtonIconDone(-1, this.left + 164, this.top + 107);
		this.addButton(this.doneButton);
		this.addButton(new BeaconScreen.WidgetButtonIconCancel(-2, this.left + 190, this.top + 107));
		this.canConsumeGem = true;
		this.doneButton.enabled = false;
	}

	@Override
	public void update() {
		super.update();
		int i = this.container.method_17373();
		if (this.canConsumeGem && i >= 0) {
			this.canConsumeGem = false;
			int j = 100;

			for (int k = 0; k <= 2; k++) {
				int l = BeaconBlockEntity.EFFECTS_BY_LEVEL[k].length;
				int m = l * 22 + (l - 1) * 2;

				for (int n = 0; n < l; n++) {
					StatusEffect statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[k][n];
					BeaconScreen.WidgetButtonIconEffect widgetButtonIconEffect = new BeaconScreen.WidgetButtonIconEffect(
						j++, this.left + 76 + n * 24 - m / 2, this.top + 22 + k * 25, statusEffect, true
					);
					this.addButton(widgetButtonIconEffect);
					if (k >= i) {
						widgetButtonIconEffect.enabled = false;
					} else if (statusEffect == this.primaryEffect) {
						widgetButtonIconEffect.setDisabled(true);
					}
				}
			}

			int k = 3;
			int l = BeaconBlockEntity.EFFECTS_BY_LEVEL[3].length + 1;
			int m = l * 22 + (l - 1) * 2;

			for (int nx = 0; nx < l - 1; nx++) {
				StatusEffect statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[3][nx];
				BeaconScreen.WidgetButtonIconEffect widgetButtonIconEffect = new BeaconScreen.WidgetButtonIconEffect(
					j++, this.left + 167 + nx * 24 - m / 2, this.top + 47, statusEffect, false
				);
				this.addButton(widgetButtonIconEffect);
				if (3 >= i) {
					widgetButtonIconEffect.enabled = false;
				} else if (statusEffect == this.secondaryEffect) {
					widgetButtonIconEffect.setDisabled(true);
				}
			}

			if (this.primaryEffect != null) {
				BeaconScreen.WidgetButtonIconEffect widgetButtonIconEffect2 = new BeaconScreen.WidgetButtonIconEffect(
					j++, this.left + 167 + (l - 1) * 24 - m / 2, this.top + 47, this.primaryEffect, false
				);
				this.addButton(widgetButtonIconEffect2);
				if (3 >= i) {
					widgetButtonIconEffect2.enabled = false;
				} else if (this.primaryEffect == this.secondaryEffect) {
					widgetButtonIconEffect2.setDisabled(true);
				}
			}
		}

		this.doneButton.enabled = this.container.hasPayment() && this.primaryEffect != null;
	}

	@Override
	protected void drawForeground(int i, int j) {
		GuiLighting.disable();
		this.drawStringCentered(this.fontRenderer, I18n.translate("block.minecraft.beacon.primary"), 62, 10, 14737632);
		this.drawStringCentered(this.fontRenderer, I18n.translate("block.minecraft.beacon.secondary"), 169, 10, 14737632);

		for (ButtonWidget buttonWidget : this.buttons) {
			if (buttonWidget.isHovered()) {
				buttonWidget.onHover(i - this.left, j - this.top);
				break;
			}
		}

		GuiLighting.enableForItems();
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(BG_TEX);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		this.itemRenderer.zOffset = 100.0F;
		this.itemRenderer.renderGuiItem(new ItemStack(Items.field_8687), k + 42, l + 109);
		this.itemRenderer.renderGuiItem(new ItemStack(Items.field_8477), k + 42 + 22, l + 109);
		this.itemRenderer.renderGuiItem(new ItemStack(Items.field_8695), k + 42 + 44, l + 109);
		this.itemRenderer.renderGuiItem(new ItemStack(Items.field_8620), k + 42 + 66, l + 109);
		this.itemRenderer.zOffset = 0.0F;
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		super.draw(i, j, f);
		this.drawMouseoverTooltip(i, j);
	}

	@Environment(EnvType.CLIENT)
	abstract static class WidgetButtonIcon extends ButtonWidget {
		private final Identifier iconTexture;
		private final int iconU;
		private final int iconV;
		private boolean disabled;

		protected WidgetButtonIcon(int i, int j, int k, Identifier identifier, int l, int m) {
			super(i, j, k, 22, 22, "");
			this.iconTexture = identifier;
			this.iconU = l;
			this.iconV = m;
		}

		@Override
		public void draw(int i, int j, float f) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(BeaconScreen.BG_TEX);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int k = 219;
			int l = 0;
			if (!this.enabled) {
				l += this.width * 2;
			} else if (this.disabled) {
				l += this.width * 1;
			} else if (this.isHovered()) {
				l += this.width * 3;
			}

			this.drawTexturedRect(this.x, this.y, l, 219, this.width, this.height);
			if (!BeaconScreen.BG_TEX.equals(this.iconTexture)) {
				MinecraftClient.getInstance().getTextureManager().bindTexture(this.iconTexture);
			}

			this.drawTexturedRect(this.x + 2, this.y + 2, this.iconU, this.iconV, 18, 18);
		}

		public boolean isDisabled() {
			return this.disabled;
		}

		public void setDisabled(boolean bl) {
			this.disabled = bl;
		}
	}

	@Environment(EnvType.CLIENT)
	class WidgetButtonIconCancel extends BeaconScreen.WidgetButtonIcon {
		public WidgetButtonIconCancel(int i, int j, int k) {
			super(i, j, k, BeaconScreen.BG_TEX, 112, 220);
		}

		@Override
		public void onPressed(double d, double e) {
			BeaconScreen.this.client.player.networkHandler.sendPacket(new GuiCloseC2SPacket(BeaconScreen.this.client.player.container.syncId));
			BeaconScreen.this.client.openScreen(null);
		}

		@Override
		public void onHover(int i, int j) {
			BeaconScreen.this.drawTooltip(I18n.translate("gui.cancel"), i, j);
		}
	}

	@Environment(EnvType.CLIENT)
	class WidgetButtonIconDone extends BeaconScreen.WidgetButtonIcon {
		public WidgetButtonIconDone(int i, int j, int k) {
			super(i, j, k, BeaconScreen.BG_TEX, 90, 220);
		}

		@Override
		public void onPressed(double d, double e) {
			BeaconScreen.this.client
				.getNetworkHandler()
				.sendPacket(new UpdateBeaconC2SPacket(StatusEffect.getRawId(BeaconScreen.this.primaryEffect), StatusEffect.getRawId(BeaconScreen.this.secondaryEffect)));
			BeaconScreen.this.client.player.networkHandler.sendPacket(new GuiCloseC2SPacket(BeaconScreen.this.client.player.container.syncId));
			BeaconScreen.this.client.openScreen(null);
		}

		@Override
		public void onHover(int i, int j) {
			BeaconScreen.this.drawTooltip(I18n.translate("gui.done"), i, j);
		}
	}

	@Environment(EnvType.CLIENT)
	class WidgetButtonIconEffect extends BeaconScreen.WidgetButtonIcon {
		private final StatusEffect effect;
		private final boolean primary;

		public WidgetButtonIconEffect(int i, int j, int k, StatusEffect statusEffect, boolean bl) {
			super(i, j, k, ContainerScreen.BACKGROUND_TEXTURE, statusEffect.getIconIndex() % 12 * 18, 198 + statusEffect.getIconIndex() / 12 * 18);
			this.effect = statusEffect;
			this.primary = bl;
		}

		@Override
		public void onPressed(double d, double e) {
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
		public void onHover(int i, int j) {
			String string = I18n.translate(this.effect.getTranslationKey());
			if (!this.primary && this.effect != StatusEffects.field_5924) {
				string = string + " II";
			}

			BeaconScreen.this.drawTooltip(string, i, j);
		}
	}
}
