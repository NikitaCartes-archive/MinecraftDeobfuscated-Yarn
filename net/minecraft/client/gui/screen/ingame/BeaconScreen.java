/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
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
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BeaconScreen
extends HandledScreen<BeaconScreenHandler> {
    static final Identifier TEXTURE = new Identifier("textures/gui/container/beacon.png");
    private static final Text PRIMARY_POWER_TEXT = Text.translatable("block.minecraft.beacon.primary");
    private static final Text SECONDARY_POWER_TEXT = Text.translatable("block.minecraft.beacon.secondary");
    private final List<BeaconButtonWidget> buttons = Lists.newArrayList();
    @Nullable
    StatusEffect primaryEffect;
    @Nullable
    StatusEffect secondaryEffect;

    public BeaconScreen(final BeaconScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 230;
        this.backgroundHeight = 219;
        handler.addListener(new ScreenHandlerListener(){

            @Override
            public void onSlotUpdate(ScreenHandler handler2, int slotId, ItemStack stack) {
            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler2, int property, int value) {
                BeaconScreen.this.primaryEffect = handler.getPrimaryEffect();
                BeaconScreen.this.secondaryEffect = handler.getSecondaryEffect();
            }
        });
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        this.buttons.add((BeaconButtonWidget)((Object)button));
    }

    @Override
    protected void init() {
        EffectButtonWidget effectButtonWidget;
        StatusEffect statusEffect;
        int l;
        int k;
        int j;
        int i;
        super.init();
        this.buttons.clear();
        this.addButton(new DoneButtonWidget(this.x + 164, this.y + 107));
        this.addButton(new CancelButtonWidget(this.x + 190, this.y + 107));
        for (i = 0; i <= 2; ++i) {
            j = BeaconBlockEntity.EFFECTS_BY_LEVEL[i].length;
            k = j * 22 + (j - 1) * 2;
            for (l = 0; l < j; ++l) {
                statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[i][l];
                effectButtonWidget = new EffectButtonWidget(this.x + 76 + l * 24 - k / 2, this.y + 22 + i * 25, statusEffect, true, i);
                effectButtonWidget.active = false;
                this.addButton(effectButtonWidget);
            }
        }
        i = 3;
        j = BeaconBlockEntity.EFFECTS_BY_LEVEL[3].length + 1;
        k = j * 22 + (j - 1) * 2;
        for (l = 0; l < j - 1; ++l) {
            statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[3][l];
            effectButtonWidget = new EffectButtonWidget(this.x + 167 + l * 24 - k / 2, this.y + 47, statusEffect, false, 3);
            effectButtonWidget.active = false;
            this.addButton(effectButtonWidget);
        }
        LevelTwoEffectButtonWidget effectButtonWidget2 = new LevelTwoEffectButtonWidget(this.x + 167 + (j - 1) * 24 - k / 2, this.y + 47, BeaconBlockEntity.EFFECTS_BY_LEVEL[0][0]);
        effectButtonWidget2.visible = false;
        this.addButton(effectButtonWidget2);
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        this.tickButtons();
    }

    void tickButtons() {
        int i = ((BeaconScreenHandler)this.handler).getProperties();
        this.buttons.forEach(button -> button.tick(i));
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        BeaconScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, PRIMARY_POWER_TEXT, 62, 10, 0xE0E0E0);
        BeaconScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, SECONDARY_POWER_TEXT, 169, 10, 0xE0E0E0);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        BeaconScreen.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        matrices.push();
        matrices.translate(0.0f, 0.0f, 100.0f);
        this.itemRenderer.renderInGuiWithOverrides(matrices, new ItemStack(Items.NETHERITE_INGOT), i + 20, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(matrices, new ItemStack(Items.EMERALD), i + 41, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(matrices, new ItemStack(Items.DIAMOND), i + 41 + 22, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(matrices, new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(matrices, new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
        matrices.pop();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Environment(value=EnvType.CLIENT)
    static interface BeaconButtonWidget {
        public void tick(int var1);
    }

    @Environment(value=EnvType.CLIENT)
    class DoneButtonWidget
    extends IconButtonWidget {
        public DoneButtonWidget(int x, int y) {
            super(x, y, 90, 220, ScreenTexts.DONE);
        }

        @Override
        public void onPress() {
            BeaconScreen.this.client.getNetworkHandler().sendPacket(new UpdateBeaconC2SPacket(Optional.ofNullable(BeaconScreen.this.primaryEffect), Optional.ofNullable(BeaconScreen.this.secondaryEffect)));
            ((BeaconScreen)BeaconScreen.this).client.player.closeHandledScreen();
        }

        @Override
        public void tick(int level) {
            this.active = ((BeaconScreenHandler)BeaconScreen.this.handler).hasPayment() && BeaconScreen.this.primaryEffect != null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class CancelButtonWidget
    extends IconButtonWidget {
        public CancelButtonWidget(int x, int y) {
            super(x, y, 112, 220, ScreenTexts.CANCEL);
        }

        @Override
        public void onPress() {
            ((BeaconScreen)BeaconScreen.this).client.player.closeHandledScreen();
        }

        @Override
        public void tick(int level) {
        }
    }

    @Environment(value=EnvType.CLIENT)
    class EffectButtonWidget
    extends BaseButtonWidget {
        private final boolean primary;
        protected final int level;
        private StatusEffect effect;
        private Sprite sprite;

        public EffectButtonWidget(int x, int y, StatusEffect statusEffect, boolean primary, int level) {
            super(x, y);
            this.primary = primary;
            this.level = level;
            this.init(statusEffect);
        }

        protected void init(StatusEffect statusEffect) {
            this.effect = statusEffect;
            this.sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(statusEffect);
            this.setTooltip(Tooltip.of(this.getEffectName(statusEffect), null));
        }

        protected MutableText getEffectName(StatusEffect statusEffect) {
            return Text.translatable(statusEffect.getTranslationKey());
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
            BeaconScreen.this.tickButtons();
        }

        @Override
        protected void renderExtra(MatrixStack matrices) {
            RenderSystem.setShaderTexture(0, this.sprite.getAtlasId());
            EffectButtonWidget.drawSprite(matrices, this.getX() + 2, this.getY() + 2, 0, 18, 18, this.sprite);
        }

        @Override
        public void tick(int level) {
            this.active = this.level < level;
            this.setDisabled(this.effect == (this.primary ? BeaconScreen.this.primaryEffect : BeaconScreen.this.secondaryEffect));
        }

        @Override
        protected MutableText getNarrationMessage() {
            return this.getEffectName(this.effect);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class LevelTwoEffectButtonWidget
    extends EffectButtonWidget {
        public LevelTwoEffectButtonWidget(int x, int y, StatusEffect statusEffect) {
            super(x, y, statusEffect, false, 3);
        }

        @Override
        protected MutableText getEffectName(StatusEffect statusEffect) {
            return Text.translatable(statusEffect.getTranslationKey()).append(" II");
        }

        @Override
        public void tick(int level) {
            if (BeaconScreen.this.primaryEffect != null) {
                this.visible = true;
                this.init(BeaconScreen.this.primaryEffect);
                super.tick(level);
            } else {
                this.visible = false;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class IconButtonWidget
    extends BaseButtonWidget {
        private final int u;
        private final int v;

        protected IconButtonWidget(int i, int j, int k, int l, Text text) {
            super(i, j, text);
            this.u = k;
            this.v = l;
        }

        @Override
        protected void renderExtra(MatrixStack matrices) {
            IconButtonWidget.drawTexture(matrices, this.getX() + 2, this.getY() + 2, this.u, this.v, 18, 18);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class BaseButtonWidget
    extends PressableWidget
    implements BeaconButtonWidget {
        private boolean disabled;

        protected BaseButtonWidget(int x, int y) {
            super(x, y, 22, 22, ScreenTexts.EMPTY);
        }

        protected BaseButtonWidget(int x, int y, Text message) {
            super(x, y, 22, 22, message);
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RenderSystem.setShaderTexture(0, TEXTURE);
            int i = 219;
            int j = 0;
            if (!this.active) {
                j += this.width * 2;
            } else if (this.disabled) {
                j += this.width * 1;
            } else if (this.isSelected()) {
                j += this.width * 3;
            }
            BaseButtonWidget.drawTexture(matrices, this.getX(), this.getY(), j, 219, this.width, this.height);
            this.renderExtra(matrices);
        }

        protected abstract void renderExtra(MatrixStack var1);

        public boolean isDisabled() {
            return this.disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        @Override
        public void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }
    }
}

