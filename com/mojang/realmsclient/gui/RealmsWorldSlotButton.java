/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsWorldSlotButton
extends ButtonWidget
implements TickableElement {
    public static final Identifier SLOT_FRAME = new Identifier("realms", "textures/gui/realms/slot_frame.png");
    public static final Identifier EMPTY_FRAME = new Identifier("realms", "textures/gui/realms/empty_frame.png");
    public static final Identifier PANORAMA_0 = new Identifier("minecraft", "textures/gui/title/background/panorama_0.png");
    public static final Identifier PANORAMA_2 = new Identifier("minecraft", "textures/gui/title/background/panorama_2.png");
    public static final Identifier PANORAMA_3 = new Identifier("minecraft", "textures/gui/title/background/panorama_3.png");
    private final Supplier<RealmsServer> serverDataProvider;
    private final Consumer<Text> toolTipSetter;
    private final int slotIndex;
    private int animTick;
    @Nullable
    private State state;

    public RealmsWorldSlotButton(int x, int y, int width, int height, Supplier<RealmsServer> serverDataProvider, Consumer<Text> toolTipSetter, int id, ButtonWidget.PressAction action) {
        super(x, y, width, height, LiteralText.EMPTY, action);
        this.serverDataProvider = serverDataProvider;
        this.slotIndex = id;
        this.toolTipSetter = toolTipSetter;
    }

    @Nullable
    public State getState() {
        return this.state;
    }

    @Override
    public void tick() {
        boolean bl3;
        String string2;
        long l;
        String string;
        boolean bl2;
        boolean bl;
        ++this.animTick;
        RealmsServer realmsServer = this.serverDataProvider.get();
        if (realmsServer == null) {
            return;
        }
        RealmsWorldOptions realmsWorldOptions = realmsServer.slots.get(this.slotIndex);
        boolean bl4 = bl = this.slotIndex == 4;
        if (bl) {
            bl2 = realmsServer.worldType == RealmsServer.WorldType.MINIGAME;
            string = "Minigame";
            l = realmsServer.minigameId;
            string2 = realmsServer.minigameImage;
            bl3 = realmsServer.minigameId == -1;
        } else {
            bl2 = realmsServer.activeSlot == this.slotIndex && realmsServer.worldType != RealmsServer.WorldType.MINIGAME;
            string = realmsWorldOptions.getSlotName(this.slotIndex);
            l = realmsWorldOptions.templateId;
            string2 = realmsWorldOptions.templateImage;
            bl3 = realmsWorldOptions.empty;
        }
        Action action = RealmsWorldSlotButton.method_27455(realmsServer, bl2, bl);
        Pair<Text, Text> pair = this.method_27454(realmsServer, string, bl3, bl, action);
        this.state = new State(bl2, string, l, string2, bl3, bl, action, pair.getFirst());
        this.setMessage(pair.getSecond());
    }

    private static Action method_27455(RealmsServer realmsServer, boolean bl, boolean bl2) {
        if (bl) {
            if (!realmsServer.expired && realmsServer.state != RealmsServer.State.UNINITIALIZED) {
                return Action.JOIN;
            }
        } else if (bl2) {
            if (!realmsServer.expired) {
                return Action.SWITCH_SLOT;
            }
        } else {
            return Action.SWITCH_SLOT;
        }
        return Action.NOTHING;
    }

    private Pair<Text, Text> method_27454(RealmsServer realmsServer, String string, boolean bl, boolean bl2, Action action) {
        if (action == Action.NOTHING) {
            return Pair.of(null, new LiteralText(string));
        }
        Text text = bl2 ? (bl ? LiteralText.EMPTY : new LiteralText(" ").append(string).append(" ").append(realmsServer.minigameName)) : new LiteralText(" ").append(string);
        TranslatableText text2 = action == Action.JOIN ? new TranslatableText("mco.configure.world.slot.tooltip.active") : (bl2 ? new TranslatableText("mco.configure.world.slot.tooltip.minigame") : new TranslatableText("mco.configure.world.slot.tooltip"));
        MutableText text3 = text2.shallowCopy().append(text);
        return Pair.of(text2, text3);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.state == null) {
            return;
        }
        this.drawSlotFrame(matrices, this.x, this.y, mouseX, mouseY, this.state.isCurrentlyActiveSlot, this.state.slotName, this.slotIndex, this.state.imageId, this.state.image, this.state.empty, this.state.minigame, this.state.action, this.state.actionPrompt);
    }

    private void drawSlotFrame(MatrixStack matrixStack, int i, int j, int k, int l, boolean bl, String string, int m, long n, @Nullable String string2, boolean bl2, boolean bl3, Action action, @Nullable Text text) {
        boolean bl5;
        boolean bl4 = this.isHovered();
        if (this.isMouseOver(k, l) && text != null) {
            this.toolTipSetter.accept(text);
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextureManager textureManager = minecraftClient.getTextureManager();
        if (bl3) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(n), string2);
        } else if (bl2) {
            textureManager.bindTexture(EMPTY_FRAME);
        } else if (string2 != null && n != -1L) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(n), string2);
        } else if (m == 1) {
            textureManager.bindTexture(PANORAMA_0);
        } else if (m == 2) {
            textureManager.bindTexture(PANORAMA_2);
        } else if (m == 3) {
            textureManager.bindTexture(PANORAMA_3);
        }
        if (bl) {
            float f = 0.85f + 0.15f * MathHelper.cos((float)this.animTick * 0.2f);
            RenderSystem.color4f(f, f, f, 1.0f);
        } else {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsWorldSlotButton.drawTexture(matrixStack, i + 3, j + 3, 0.0f, 0.0f, 74, 74, 74, 74);
        textureManager.bindTexture(SLOT_FRAME);
        boolean bl6 = bl5 = bl4 && action != Action.NOTHING;
        if (bl5) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else if (bl) {
            RenderSystem.color4f(0.8f, 0.8f, 0.8f, 1.0f);
        } else {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsWorldSlotButton.drawTexture(matrixStack, i, j, 0.0f, 0.0f, 80, 80, 80, 80);
        this.drawCenteredString(matrixStack, minecraftClient.textRenderer, string, i + 40, j + 66, 0xFFFFFF);
    }

    @Environment(value=EnvType.CLIENT)
    public static class State {
        private final boolean isCurrentlyActiveSlot;
        private final String slotName;
        private final long imageId;
        private final String image;
        public final boolean empty;
        public final boolean minigame;
        public final Action action;
        @Nullable
        private final Text actionPrompt;

        State(boolean isCurrentlyActiveSlot, String slotName, long imageId, @Nullable String image, boolean empty, boolean minigame, Action action, @Nullable Text text) {
            this.isCurrentlyActiveSlot = isCurrentlyActiveSlot;
            this.slotName = slotName;
            this.imageId = imageId;
            this.image = image;
            this.empty = empty;
            this.minigame = minigame;
            this.action = action;
            this.actionPrompt = text;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Action {
        NOTHING,
        SWITCH_SLOT,
        JOIN;

    }
}

