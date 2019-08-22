/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
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
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BeaconScreen
extends AbstractContainerScreen<BeaconContainer> {
    private static final Identifier BG_TEX = new Identifier("textures/gui/container/beacon.png");
    private DoneButtonWidget doneButton;
    private boolean consumeGem;
    private StatusEffect primaryEffect;
    private StatusEffect secondaryEffect;

    public BeaconScreen(final BeaconContainer beaconContainer, PlayerInventory playerInventory, Text text) {
        super(beaconContainer, playerInventory, text);
        this.containerWidth = 230;
        this.containerHeight = 219;
        beaconContainer.addListener(new ContainerListener(){

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
                BeaconScreen.this.consumeGem = true;
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        this.doneButton = this.addButton(new DoneButtonWidget(this.left + 164, this.top + 107));
        this.addButton(new CancelButtonWidget(this.left + 190, this.top + 107));
        this.consumeGem = true;
        this.doneButton.active = false;
    }

    @Override
    public void tick() {
        super.tick();
        int i = ((BeaconContainer)this.container).getProperties();
        if (this.consumeGem && i >= 0) {
            EffectButtonWidget effectButtonWidget;
            StatusEffect statusEffect;
            int m;
            int l;
            int k;
            int j;
            this.consumeGem = false;
            for (j = 0; j <= 2; ++j) {
                k = BeaconBlockEntity.EFFECTS_BY_LEVEL[j].length;
                l = k * 22 + (k - 1) * 2;
                for (m = 0; m < k; ++m) {
                    statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[j][m];
                    effectButtonWidget = new EffectButtonWidget(this.left + 76 + m * 24 - l / 2, this.top + 22 + j * 25, statusEffect, true);
                    this.addButton(effectButtonWidget);
                    if (j >= i) {
                        effectButtonWidget.active = false;
                        continue;
                    }
                    if (statusEffect != this.primaryEffect) continue;
                    effectButtonWidget.setDisabled(true);
                }
            }
            j = 3;
            k = BeaconBlockEntity.EFFECTS_BY_LEVEL[3].length + 1;
            l = k * 22 + (k - 1) * 2;
            for (m = 0; m < k - 1; ++m) {
                statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[3][m];
                effectButtonWidget = new EffectButtonWidget(this.left + 167 + m * 24 - l / 2, this.top + 47, statusEffect, false);
                this.addButton(effectButtonWidget);
                if (3 >= i) {
                    effectButtonWidget.active = false;
                    continue;
                }
                if (statusEffect != this.secondaryEffect) continue;
                effectButtonWidget.setDisabled(true);
            }
            if (this.primaryEffect != null) {
                EffectButtonWidget effectButtonWidget2 = new EffectButtonWidget(this.left + 167 + (k - 1) * 24 - l / 2, this.top + 47, this.primaryEffect, false);
                this.addButton(effectButtonWidget2);
                if (3 >= i) {
                    effectButtonWidget2.active = false;
                } else if (this.primaryEffect == this.secondaryEffect) {
                    effectButtonWidget2.setDisabled(true);
                }
            }
        }
        this.doneButton.active = ((BeaconContainer)this.container).hasPayment() && this.primaryEffect != null;
    }

    @Override
    protected void drawForeground(int i, int j) {
        GuiLighting.disable();
        this.drawCenteredString(this.font, I18n.translate("block.minecraft.beacon.primary", new Object[0]), 62, 10, 0xE0E0E0);
        this.drawCenteredString(this.font, I18n.translate("block.minecraft.beacon.secondary", new Object[0]), 169, 10, 0xE0E0E0);
        for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
            if (!abstractButtonWidget.isHovered()) continue;
            abstractButtonWidget.renderToolTip(i - this.left, j - this.top);
            break;
        }
        GuiLighting.enableForItems();
    }

    @Override
    protected void drawBackground(float f, int i, int j) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BG_TEX);
        int k = (this.width - this.containerWidth) / 2;
        int l = (this.height - this.containerHeight) / 2;
        this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
        this.itemRenderer.zOffset = 100.0f;
        this.itemRenderer.renderGuiItem(new ItemStack(Items.EMERALD), k + 42, l + 109);
        this.itemRenderer.renderGuiItem(new ItemStack(Items.DIAMOND), k + 42 + 22, l + 109);
        this.itemRenderer.renderGuiItem(new ItemStack(Items.GOLD_INGOT), k + 42 + 44, l + 109);
        this.itemRenderer.renderGuiItem(new ItemStack(Items.IRON_INGOT), k + 42 + 66, l + 109);
        this.itemRenderer.zOffset = 0.0f;
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        super.render(i, j, f);
        this.drawMouseoverTooltip(i, j);
    }

    @Environment(value=EnvType.CLIENT)
    class CancelButtonWidget
    extends IconButtonWidget {
        public CancelButtonWidget(int i, int j) {
            super(i, j, 112, 220);
        }

        @Override
        public void onPress() {
            ((BeaconScreen)BeaconScreen.this).minecraft.player.networkHandler.sendPacket(new GuiCloseC2SPacket(((BeaconScreen)BeaconScreen.this).minecraft.player.container.syncId));
            BeaconScreen.this.minecraft.openScreen(null);
        }

        @Override
        public void renderToolTip(int i, int j) {
            BeaconScreen.this.renderTooltip(I18n.translate("gui.cancel", new Object[0]), i, j);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class DoneButtonWidget
    extends IconButtonWidget {
        public DoneButtonWidget(int i, int j) {
            super(i, j, 90, 220);
        }

        @Override
        public void onPress() {
            BeaconScreen.this.minecraft.getNetworkHandler().sendPacket(new UpdateBeaconC2SPacket(StatusEffect.getRawId(BeaconScreen.this.primaryEffect), StatusEffect.getRawId(BeaconScreen.this.secondaryEffect)));
            ((BeaconScreen)BeaconScreen.this).minecraft.player.networkHandler.sendPacket(new GuiCloseC2SPacket(((BeaconScreen)BeaconScreen.this).minecraft.player.container.syncId));
            BeaconScreen.this.minecraft.openScreen(null);
        }

        @Override
        public void renderToolTip(int i, int j) {
            BeaconScreen.this.renderTooltip(I18n.translate("gui.done", new Object[0]), i, j);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class IconButtonWidget
    extends BaseButtonWidget {
        private final int u;
        private final int v;

        protected IconButtonWidget(int i, int j, int k, int l) {
            super(i, j);
            this.u = k;
            this.v = l;
        }

        @Override
        protected void renderExtra() {
            this.blit(this.x + 2, this.y + 2, this.u, this.v, 18, 18);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class EffectButtonWidget
    extends BaseButtonWidget {
        private final StatusEffect effect;
        private final Sprite sprite;
        private final boolean primary;

        public EffectButtonWidget(int i, int j, StatusEffect statusEffect, boolean bl) {
            super(i, j);
            this.effect = statusEffect;
            this.sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(statusEffect);
            this.primary = bl;
        }

        @Override
        public void onPress() {
            if (this.isDisabled()) {
                return;
            }
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

        @Override
        public void renderToolTip(int i, int j) {
            String string = I18n.translate(this.effect.getTranslationKey(), new Object[0]);
            if (!this.primary && this.effect != StatusEffects.REGENERATION) {
                string = string + " II";
            }
            BeaconScreen.this.renderTooltip(string, i, j);
        }

        @Override
        protected void renderExtra() {
            MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.STATUS_EFFECT_ATLAS_TEX);
            EffectButtonWidget.blit(this.x + 2, this.y + 2, this.blitOffset, 18, 18, this.sprite);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class BaseButtonWidget
    extends AbstractPressableButtonWidget {
        private boolean disabled;

        protected BaseButtonWidget(int i, int j) {
            super(i, j, 22, 22, "");
        }

        @Override
        public void renderButton(int i, int j, float f) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(BG_TEX);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            int k = 219;
            int l = 0;
            if (!this.active) {
                l += this.width * 2;
            } else if (this.disabled) {
                l += this.width * 1;
            } else if (this.isHovered()) {
                l += this.width * 3;
            }
            this.blit(this.x, this.y, l, 219, this.width, this.height);
            this.renderExtra();
        }

        protected abstract void renderExtra();

        public boolean isDisabled() {
            return this.disabled;
        }

        public void setDisabled(boolean bl) {
            this.disabled = bl;
        }
    }
}

