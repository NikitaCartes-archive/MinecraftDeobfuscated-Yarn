package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.BeaconContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.packet.GuiCloseServerPacket;
import net.minecraft.server.network.packet.UpdateBeaconServerPacket;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class BeaconGui extends ContainerGui {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/beacon.png");
	private final Inventory inventory;
	private BeaconGui.WidgetButtonIconDone doneButton;
	private boolean canConsumeGem;

	public BeaconGui(PlayerInventory playerInventory, Inventory inventory) {
		super(new BeaconContainer(playerInventory, inventory));
		this.inventory = inventory;
		this.containerWidth = 230;
		this.containerHeight = 219;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.doneButton = new BeaconGui.WidgetButtonIconDone(-1, this.left + 164, this.top + 107);
		this.addButton(this.doneButton);
		this.addButton(new BeaconGui.WidgetButtonIconCancel(-2, this.left + 190, this.top + 107));
		this.canConsumeGem = true;
		this.doneButton.enabled = false;
	}

	@Override
	public void update() {
		super.update();
		int i = this.inventory.getInvProperty(0);
		StatusEffect statusEffect = StatusEffect.byRawId(this.inventory.getInvProperty(1));
		StatusEffect statusEffect2 = StatusEffect.byRawId(this.inventory.getInvProperty(2));
		if (this.canConsumeGem && i >= 0) {
			this.canConsumeGem = false;
			int j = 100;

			for (int k = 0; k <= 2; k++) {
				int l = BeaconBlockEntity.EFFECTS[k].length;
				int m = l * 22 + (l - 1) * 2;

				for (int n = 0; n < l; n++) {
					StatusEffect statusEffect3 = BeaconBlockEntity.EFFECTS[k][n];
					BeaconGui.WidgetButtonIconEffect widgetButtonIconEffect = new BeaconGui.WidgetButtonIconEffect(
						j++, this.left + 76 + n * 24 - m / 2, this.top + 22 + k * 25, statusEffect3, k
					);
					this.addButton(widgetButtonIconEffect);
					if (k >= i) {
						widgetButtonIconEffect.enabled = false;
					} else if (statusEffect3 == statusEffect) {
						widgetButtonIconEffect.setDisabled(true);
					}
				}
			}

			int k = 3;
			int l = BeaconBlockEntity.EFFECTS[3].length + 1;
			int m = l * 22 + (l - 1) * 2;

			for (int nx = 0; nx < l - 1; nx++) {
				StatusEffect statusEffect3 = BeaconBlockEntity.EFFECTS[3][nx];
				BeaconGui.WidgetButtonIconEffect widgetButtonIconEffect = new BeaconGui.WidgetButtonIconEffect(
					j++, this.left + 167 + nx * 24 - m / 2, this.top + 47, statusEffect3, 3
				);
				this.addButton(widgetButtonIconEffect);
				if (3 >= i) {
					widgetButtonIconEffect.enabled = false;
				} else if (statusEffect3 == statusEffect2) {
					widgetButtonIconEffect.setDisabled(true);
				}
			}

			if (statusEffect != null) {
				BeaconGui.WidgetButtonIconEffect widgetButtonIconEffect2 = new BeaconGui.WidgetButtonIconEffect(
					j++, this.left + 167 + (l - 1) * 24 - m / 2, this.top + 47, statusEffect, 3
				);
				this.addButton(widgetButtonIconEffect2);
				if (3 >= i) {
					widgetButtonIconEffect2.enabled = false;
				} else if (statusEffect == statusEffect2) {
					widgetButtonIconEffect2.setDisabled(true);
				}
			}
		}

		this.doneButton.enabled = !this.inventory.getInvStack(0).isEmpty() && statusEffect != null;
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
		this.itemRenderer.renderItemAndGlowInGui(new ItemStack(Items.field_8687), k + 42, l + 109);
		this.itemRenderer.renderItemAndGlowInGui(new ItemStack(Items.field_8477), k + 42 + 22, l + 109);
		this.itemRenderer.renderItemAndGlowInGui(new ItemStack(Items.field_8695), k + 42 + 44, l + 109);
		this.itemRenderer.renderItemAndGlowInGui(new ItemStack(Items.field_8620), k + 42 + 66, l + 109);
		this.itemRenderer.zOffset = 0.0F;
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		super.draw(i, j, f);
		this.drawMousoverTooltip(i, j);
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
			if (this.visible) {
				MinecraftClient.getInstance().getTextureManager().bindTexture(BeaconGui.BG_TEX);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
				int k = 219;
				int l = 0;
				if (!this.enabled) {
					l += this.width * 2;
				} else if (this.disabled) {
					l += this.width * 1;
				} else if (this.hovered) {
					l += this.width * 3;
				}

				this.drawTexturedRect(this.x, this.y, l, 219, this.width, this.height);
				if (!BeaconGui.BG_TEX.equals(this.iconTexture)) {
					MinecraftClient.getInstance().getTextureManager().bindTexture(this.iconTexture);
				}

				this.drawTexturedRect(this.x + 2, this.y + 2, this.iconU, this.iconV, 18, 18);
			}
		}

		public boolean isDisabled() {
			return this.disabled;
		}

		public void setDisabled(boolean bl) {
			this.disabled = bl;
		}
	}

	@Environment(EnvType.CLIENT)
	class WidgetButtonIconCancel extends BeaconGui.WidgetButtonIcon {
		public WidgetButtonIconCancel(int i, int j, int k) {
			super(i, j, k, BeaconGui.BG_TEX, 112, 220);
		}

		@Override
		public void onPressed(double d, double e) {
			BeaconGui.this.client.player.networkHandler.sendPacket(new GuiCloseServerPacket(BeaconGui.this.client.player.container.syncId));
			BeaconGui.this.client.openGui(null);
		}

		@Override
		public void onHover(int i, int j) {
			BeaconGui.this.drawTooltip(I18n.translate("gui.cancel"), i, j);
		}
	}

	@Environment(EnvType.CLIENT)
	class WidgetButtonIconDone extends BeaconGui.WidgetButtonIcon {
		public WidgetButtonIconDone(int i, int j, int k) {
			super(i, j, k, BeaconGui.BG_TEX, 90, 220);
		}

		@Override
		public void onPressed(double d, double e) {
			BeaconGui.this.client
				.getNetworkHandler()
				.sendPacket(new UpdateBeaconServerPacket(BeaconGui.this.inventory.getInvProperty(1), BeaconGui.this.inventory.getInvProperty(2)));
			BeaconGui.this.client.player.networkHandler.sendPacket(new GuiCloseServerPacket(BeaconGui.this.client.player.container.syncId));
			BeaconGui.this.client.openGui(null);
		}

		@Override
		public void onHover(int i, int j) {
			BeaconGui.this.drawTooltip(I18n.translate("gui.done"), i, j);
		}
	}

	@Environment(EnvType.CLIENT)
	class WidgetButtonIconEffect extends BeaconGui.WidgetButtonIcon {
		private final StatusEffect effect;
		private final int level;

		public WidgetButtonIconEffect(int i, int j, int k, StatusEffect statusEffect, int l) {
			super(i, j, k, ContainerGui.BACKGROUND_TEXTURE, statusEffect.getIconIndex() % 12 * 18, 198 + statusEffect.getIconIndex() / 12 * 18);
			this.effect = statusEffect;
			this.level = l;
		}

		@Override
		public void onPressed(double d, double e) {
			if (!this.isDisabled()) {
				int i = StatusEffect.getRawId(this.effect);
				if (this.level < 3) {
					BeaconGui.this.inventory.setInvProperty(1, i);
				} else {
					BeaconGui.this.inventory.setInvProperty(2, i);
				}

				BeaconGui.this.buttons.clear();
				BeaconGui.this.listeners.clear();
				BeaconGui.this.onInitialized();
				BeaconGui.this.update();
			}
		}

		@Override
		public void onHover(int i, int j) {
			String string = I18n.translate(this.effect.getTranslationKey());
			if (this.level >= 3 && this.effect != StatusEffects.field_5924) {
				string = string + " II";
			}

			BeaconGui.this.drawTooltip(string, i, j);
		}
	}
}
