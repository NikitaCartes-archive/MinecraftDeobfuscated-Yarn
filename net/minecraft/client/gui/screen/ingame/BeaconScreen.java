/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(value=EnvType.CLIENT)
public class BeaconScreen
extends HandledScreen<BeaconScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/beacon.png");
    private static final Text PRIMARY_POWER_TEXT = new TranslatableText("block.minecraft.beacon.primary");
    private static final Text SECONDARY_POWER_TEXT = new TranslatableText("block.minecraft.beacon.secondary");
    private DoneButtonWidget doneButton;
    private boolean consumeGem;
    private StatusEffect primaryEffect;
    private StatusEffect secondaryEffect;

    public BeaconScreen(final BeaconScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 230;
        this.backgroundHeight = 219;
        handler.addListener(new ScreenHandlerListener(){

            @Override
            public void onHandlerRegistered(ScreenHandler handler2, DefaultedList<ItemStack> stacks) {
            }

            @Override
            public void onSlotUpdate(ScreenHandler handler2, int slotId, ItemStack stack) {
            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler2, int property, int value) {
                BeaconScreen.this.primaryEffect = handler.getPrimaryEffect();
                BeaconScreen.this.secondaryEffect = handler.getSecondaryEffect();
                BeaconScreen.this.consumeGem = true;
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        this.doneButton = this.addButton(new DoneButtonWidget(this.x + 164, this.y + 107));
        this.addButton(new CancelButtonWidget(this.x + 190, this.y + 107));
        this.consumeGem = true;
        this.doneButton.active = false;
    }

    @Override
    public void tick() {
        super.tick();
        int i = ((BeaconScreenHandler)this.handler).getProperties();
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
                    effectButtonWidget = new EffectButtonWidget(this.x + 76 + m * 24 - l / 2, this.y + 22 + j * 25, statusEffect, true);
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
                effectButtonWidget = new EffectButtonWidget(this.x + 167 + m * 24 - l / 2, this.y + 47, statusEffect, false);
                this.addButton(effectButtonWidget);
                if (3 >= i) {
                    effectButtonWidget.active = false;
                    continue;
                }
                if (statusEffect != this.secondaryEffect) continue;
                effectButtonWidget.setDisabled(true);
            }
            if (this.primaryEffect != null) {
                EffectButtonWidget effectButtonWidget2 = new EffectButtonWidget(this.x + 167 + (k - 1) * 24 - l / 2, this.y + 47, this.primaryEffect, false);
                this.addButton(effectButtonWidget2);
                if (3 >= i) {
                    effectButtonWidget2.active = false;
                } else if (this.primaryEffect == this.secondaryEffect) {
                    effectButtonWidget2.setDisabled(true);
                }
            }
        }
        this.doneButton.active = ((BeaconScreenHandler)this.handler).hasPayment() && this.primaryEffect != null;
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        BeaconScreen.drawCenteredText(matrices, this.textRenderer, PRIMARY_POWER_TEXT, 62, 10, 0xE0E0E0);
        BeaconScreen.drawCenteredText(matrices, this.textRenderer, SECONDARY_POWER_TEXT, 169, 10, 0xE0E0E0);
        for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
            if (!abstractButtonWidget.isHovered()) continue;
            abstractButtonWidget.renderToolTip(matrices, mouseX - this.x, mouseY - this.y);
            break;
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.itemRenderer.zOffset = 100.0f;
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.NETHERITE_INGOT), i + 20, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.EMERALD), i + 41, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.DIAMOND), i + 41 + 22, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
        this.itemRenderer.zOffset = 0.0f;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Environment(value=EnvType.CLIENT)
    class CancelButtonWidget
    extends IconButtonWidget {
        public CancelButtonWidget(int x, int y) {
            super(x, y, 112, 220);
        }

        @Override
        public void onPress() {
            ((BeaconScreen)BeaconScreen.this).client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(((BeaconScreen)BeaconScreen.this).client.player.currentScreenHandler.syncId));
            BeaconScreen.this.client.openScreen(null);
        }

        @Override
        public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
            BeaconScreen.this.renderTooltip(matrices, ScreenTexts.CANCEL, mouseX, mouseY);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class DoneButtonWidget
    extends IconButtonWidget {
        public DoneButtonWidget(int x, int y) {
            super(x, y, 90, 220);
        }

        @Override
        public void onPress() {
            BeaconScreen.this.client.getNetworkHandler().sendPacket(new UpdateBeaconC2SPacket(StatusEffect.getRawId(BeaconScreen.this.primaryEffect), StatusEffect.getRawId(BeaconScreen.this.secondaryEffect)));
            ((BeaconScreen)BeaconScreen.this).client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(((BeaconScreen)BeaconScreen.this).client.player.currentScreenHandler.syncId));
            BeaconScreen.this.client.openScreen(null);
        }

        @Override
        public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
            BeaconScreen.this.renderTooltip(matrices, ScreenTexts.DONE, mouseX, mouseY);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class IconButtonWidget
    extends BaseButtonWidget {
        private final int u;
        private final int v;

        protected IconButtonWidget(int x, int y, int u, int v) {
            super(x, y);
            this.u = u;
            this.v = v;
        }

        @Override
        protected void renderExtra(MatrixStack matrices) {
            this.drawTexture(matrices, this.x + 2, this.y + 2, this.u, this.v, 18, 18);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class EffectButtonWidget
    extends BaseButtonWidget {
        private final StatusEffect effect;
        private final Sprite sprite;
        private final boolean primary;
        private final Text text;

        public EffectButtonWidget(int x, int y, StatusEffect statusEffect, boolean primary) {
            super(x, y);
            this.effect = statusEffect;
            this.sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(statusEffect);
            this.primary = primary;
            this.text = this.getTextForEffect(statusEffect, primary);
        }

        private Text getTextForEffect(StatusEffect effect, boolean primary) {
            TranslatableText mutableText = new TranslatableText(effect.getTranslationKey());
            if (!primary && effect != StatusEffects.REGENERATION) {
                mutableText.append(" II");
            }
            return mutableText;
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
        public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
            BeaconScreen.this.renderTooltip(matrices, this.text, mouseX, mouseY);
        }

        @Override
        protected void renderExtra(MatrixStack matrices) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(this.sprite.getAtlas().getId());
            EffectButtonWidget.drawSprite(matrices, this.x + 2, this.y + 2, this.getZOffset(), 18, 18, this.sprite);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class BaseButtonWidget
    extends AbstractPressableButtonWidget {
        private boolean disabled;

        protected BaseButtonWidget(int x, int y) {
            super(x, y, 22, 22, LiteralText.EMPTY);
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
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

        protected abstract void renderExtra(MatrixStack var1);

        public boolean isDisabled() {
            return this.disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }
    }
}

