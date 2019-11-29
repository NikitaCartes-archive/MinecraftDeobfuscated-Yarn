package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
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
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BeaconScreen extends AbstractContainerScreen<BeaconContainer> {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/beacon.png");
	private BeaconScreen.DoneButtonWidget doneButton;
	private boolean consumeGem;
	private StatusEffect primaryEffect;
	private StatusEffect secondaryEffect;

	public BeaconScreen(BeaconContainer container, PlayerInventory inventory, Text title) {
		super(container, inventory, title);
		this.containerWidth = 230;
		this.containerHeight = 219;
		container.addListener(new ContainerListener() {
			@Override
			public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
			}

			@Override
			public void onContainerSlotUpdate(Container container, int slotId, ItemStack itemStack) {
			}

			@Override
			public void onContainerPropertyUpdate(Container container, int propertyId, int i) {
				BeaconScreen.this.primaryEffect = container.getPrimaryEffect();
				BeaconScreen.this.secondaryEffect = container.getSecondaryEffect();
				BeaconScreen.this.consumeGem = true;
			}
		});
	}

	@Override
	protected void init() {
		super.init();
		this.doneButton = this.addButton(new BeaconScreen.DoneButtonWidget(this.left + 164, this.top + 107));
		this.addButton(new BeaconScreen.CancelButtonWidget(this.left + 190, this.top + 107));
		this.consumeGem = true;
		this.doneButton.active = false;
	}

	@Override
	public void tick() {
		super.tick();
		int i = this.container.getProperties();
		if (this.consumeGem && i >= 0) {
			this.consumeGem = false;

			for (int j = 0; j <= 2; j++) {
				int k = BeaconBlockEntity.EFFECTS_BY_LEVEL[j].length;
				int l = k * 22 + (k - 1) * 2;

				for (int m = 0; m < k; m++) {
					StatusEffect statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[j][m];
					BeaconScreen.EffectButtonWidget effectButtonWidget = new BeaconScreen.EffectButtonWidget(
						this.left + 76 + m * 24 - l / 2, this.top + 22 + j * 25, statusEffect, true
					);
					this.addButton(effectButtonWidget);
					if (j >= i) {
						effectButtonWidget.active = false;
					} else if (statusEffect == this.primaryEffect) {
						effectButtonWidget.setDisabled(true);
					}
				}
			}

			int j = 3;
			int k = BeaconBlockEntity.EFFECTS_BY_LEVEL[3].length + 1;
			int l = k * 22 + (k - 1) * 2;

			for (int mx = 0; mx < k - 1; mx++) {
				StatusEffect statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[3][mx];
				BeaconScreen.EffectButtonWidget effectButtonWidget = new BeaconScreen.EffectButtonWidget(
					this.left + 167 + mx * 24 - l / 2, this.top + 47, statusEffect, false
				);
				this.addButton(effectButtonWidget);
				if (3 >= i) {
					effectButtonWidget.active = false;
				} else if (statusEffect == this.secondaryEffect) {
					effectButtonWidget.setDisabled(true);
				}
			}

			if (this.primaryEffect != null) {
				BeaconScreen.EffectButtonWidget effectButtonWidget2 = new BeaconScreen.EffectButtonWidget(
					this.left + 167 + (k - 1) * 24 - l / 2, this.top + 47, this.primaryEffect, false
				);
				this.addButton(effectButtonWidget2);
				if (3 >= i) {
					effectButtonWidget2.active = false;
				} else if (this.primaryEffect == this.secondaryEffect) {
					effectButtonWidget2.setDisabled(true);
				}
			}
		}

		this.doneButton.active = this.container.hasPayment() && this.primaryEffect != null;
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.drawCenteredString(this.font, I18n.translate("block.minecraft.beacon.primary"), 62, 10, 14737632);
		this.drawCenteredString(this.font, I18n.translate("block.minecraft.beacon.secondary"), 169, 10, 14737632);

		for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
			if (abstractButtonWidget.isHovered()) {
				abstractButtonWidget.renderToolTip(mouseX - this.left, mouseY - this.top);
				break;
			}
		}
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BG_TEX);
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
		this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
		this.itemRenderer.zOffset = 100.0F;
		this.itemRenderer.renderGuiItem(new ItemStack(Items.EMERALD), i + 42, j + 109);
		this.itemRenderer.renderGuiItem(new ItemStack(Items.DIAMOND), i + 42 + 22, j + 109);
		this.itemRenderer.renderGuiItem(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
		this.itemRenderer.renderGuiItem(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
		this.itemRenderer.zOffset = 0.0F;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		super.render(mouseX, mouseY, delta);
		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	@Environment(EnvType.CLIENT)
	abstract static class BaseButtonWidget extends AbstractPressableButtonWidget {
		private boolean disabled;

		protected BaseButtonWidget(int x, int y) {
			super(x, y, 22, 22, "");
		}

		@Override
		public void renderButton(int mouseX, int mouseY, float delta) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(BeaconScreen.BG_TEX);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int i = 219;
			int j = 0;
			if (!this.active) {
				j += this.width * 2;
			} else if (this.disabled) {
				j += this.width * 1;
			} else if (this.isHovered()) {
				j += this.width * 3;
			}

			this.blit(this.x, this.y, j, 219, this.width, this.height);
			this.renderExtra();
		}

		protected abstract void renderExtra();

		public boolean isDisabled() {
			return this.disabled;
		}

		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}
	}

	@Environment(EnvType.CLIENT)
	class CancelButtonWidget extends BeaconScreen.IconButtonWidget {
		public CancelButtonWidget(int x, int y) {
			super(x, y, 112, 220);
		}

		@Override
		public void onPress() {
			BeaconScreen.this.minecraft.player.networkHandler.sendPacket(new GuiCloseC2SPacket(BeaconScreen.this.minecraft.player.container.syncId));
			BeaconScreen.this.minecraft.openScreen(null);
		}

		@Override
		public void renderToolTip(int mouseX, int mouseY) {
			BeaconScreen.this.renderTooltip(I18n.translate("gui.cancel"), mouseX, mouseY);
		}
	}

	@Environment(EnvType.CLIENT)
	class DoneButtonWidget extends BeaconScreen.IconButtonWidget {
		public DoneButtonWidget(int x, int y) {
			super(x, y, 90, 220);
		}

		@Override
		public void onPress() {
			BeaconScreen.this.minecraft
				.getNetworkHandler()
				.sendPacket(new UpdateBeaconC2SPacket(StatusEffect.getRawId(BeaconScreen.this.primaryEffect), StatusEffect.getRawId(BeaconScreen.this.secondaryEffect)));
			BeaconScreen.this.minecraft.player.networkHandler.sendPacket(new GuiCloseC2SPacket(BeaconScreen.this.minecraft.player.container.syncId));
			BeaconScreen.this.minecraft.openScreen(null);
		}

		@Override
		public void renderToolTip(int mouseX, int mouseY) {
			BeaconScreen.this.renderTooltip(I18n.translate("gui.done"), mouseX, mouseY);
		}
	}

	@Environment(EnvType.CLIENT)
	class EffectButtonWidget extends BeaconScreen.BaseButtonWidget {
		private final StatusEffect effect;
		private final Sprite sprite;
		private final boolean primary;

		public EffectButtonWidget(int x, int y, StatusEffect statusEffect, boolean primary) {
			super(x, y);
			this.effect = statusEffect;
			this.sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(statusEffect);
			this.primary = primary;
		}

		@Override
		public void onPress() {
			if (!this.isDisabled()) {
				if (this.primary) {
					BeaconScreen.this.primaryEffect = this.effect;
				} else {
					BeaconScreen.this.secondaryEffect = this.effect;
				}

				BeaconScreen.this.buttons.clear();
				BeaconScreen.this.children.clear();
				BeaconScreen.this.init();
				BeaconScreen.this.tick();
			}
		}

		@Override
		public void renderToolTip(int mouseX, int mouseY) {
			String string = I18n.translate(this.effect.getTranslationKey());
			if (!this.primary && this.effect != StatusEffects.REGENERATION) {
				string = string + " II";
			}

			BeaconScreen.this.renderTooltip(string, mouseX, mouseY);
		}

		@Override
		protected void renderExtra() {
			MinecraftClient.getInstance().getTextureManager().bindTexture(this.sprite.getAtlas().getId());
			blit(this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.sprite);
		}
	}

	@Environment(EnvType.CLIENT)
	abstract static class IconButtonWidget extends BeaconScreen.BaseButtonWidget {
		private final int u;
		private final int v;

		protected IconButtonWidget(int x, int y, int u, int v) {
			super(x, y);
			this.u = u;
			this.v = v;
		}

		@Override
		protected void renderExtra() {
			this.blit(this.x + 2, this.y + 2, this.u, this.v, 18, 18);
		}
	}
}
