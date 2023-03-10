/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsWorldSlotButton
extends ButtonWidget {
    public static final Identifier SLOT_FRAME = new Identifier("realms", "textures/gui/realms/slot_frame.png");
    public static final Identifier EMPTY_FRAME = new Identifier("realms", "textures/gui/realms/empty_frame.png");
    public static final Identifier CHECKMARK = new Identifier("minecraft", "textures/gui/checkmark.png");
    public static final Identifier PANORAMA_0 = new Identifier("minecraft", "textures/gui/title/background/panorama_0.png");
    public static final Identifier PANORAMA_2 = new Identifier("minecraft", "textures/gui/title/background/panorama_2.png");
    public static final Identifier PANORAMA_3 = new Identifier("minecraft", "textures/gui/title/background/panorama_3.png");
    private static final Text ACTIVE_TOOLTIP = Text.translatable("mco.configure.world.slot.tooltip.active");
    private static final Text MINIGAME_TOOLTIP = Text.translatable("mco.configure.world.slot.tooltip.minigame");
    private static final Text TOOLTIP = Text.translatable("mco.configure.world.slot.tooltip");
    private final Supplier<RealmsServer> serverDataProvider;
    private final Consumer<Text> tooltipSetter;
    private final int slotIndex;
    @Nullable
    private State state;

    public RealmsWorldSlotButton(int x, int y, int width, int height, Supplier<RealmsServer> serverDataProvider, Consumer<Text> tooltipSetter, int id, ButtonWidget.PressAction action) {
        super(x, y, width, height, ScreenTexts.EMPTY, action, DEFAULT_NARRATION_SUPPLIER);
        this.serverDataProvider = serverDataProvider;
        this.slotIndex = id;
        this.tooltipSetter = tooltipSetter;
    }

    @Nullable
    public State getState() {
        return this.state;
    }

    public void tick() {
        boolean bl3;
        String string2;
        long l;
        String string;
        boolean bl2;
        boolean bl;
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
        Action action = RealmsWorldSlotButton.getAction(realmsServer, bl2, bl);
        Pair<Text, Text> pair = this.getActionPromptAndMessage(realmsServer, string, bl3, bl, action);
        this.state = new State(bl2, string, l, string2, bl3, bl, action, pair.getFirst());
        this.setMessage(pair.getSecond());
    }

    private static Action getAction(RealmsServer server, boolean active, boolean minigame) {
        if (active) {
            if (!server.expired && server.state != RealmsServer.State.UNINITIALIZED) {
                return Action.JOIN;
            }
        } else if (minigame) {
            if (!server.expired) {
                return Action.SWITCH_SLOT;
            }
        } else {
            return Action.SWITCH_SLOT;
        }
        return Action.NOTHING;
    }

    private Pair<Text, Text> getActionPromptAndMessage(RealmsServer server, String text, boolean empty, boolean minigame, Action action) {
        if (action == Action.NOTHING) {
            return Pair.of(null, Text.literal(text));
        }
        Text text2 = minigame ? (empty ? ScreenTexts.EMPTY : ScreenTexts.space().append(text).append(ScreenTexts.SPACE).append(server.minigameName)) : ScreenTexts.space().append(text);
        Text text3 = action == Action.JOIN ? ACTIVE_TOOLTIP : (minigame ? MINIGAME_TOOLTIP : TOOLTIP);
        MutableText text4 = text3.copy().append(text2);
        return Pair.of(text3, text4);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.state == null) {
            return;
        }
        this.drawSlotFrame(matrices, this.getX(), this.getY(), mouseX, mouseY, this.state.isCurrentlyActiveSlot, this.state.slotName, this.slotIndex, this.state.imageId, this.state.image, this.state.empty, this.state.minigame, this.state.action, this.state.actionPrompt);
    }

    private void drawSlotFrame(MatrixStack matrices, int x, int y, int mouseX, int mouseY, boolean active, String slotName, int slotIndex, long imageId, @Nullable String image, boolean empty, boolean minigame, Action action, @Nullable Text actionPrompt) {
        boolean bl2;
        boolean bl = this.isSelected();
        if (this.isMouseOver(mouseX, mouseY) && actionPrompt != null) {
            this.tooltipSetter.accept(actionPrompt);
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minigame) {
            RenderSystem.setShaderTexture(0, RealmsTextureManager.getTextureId(String.valueOf(imageId), image));
        } else if (empty) {
            RenderSystem.setShaderTexture(0, EMPTY_FRAME);
        } else if (image != null && imageId != -1L) {
            RenderSystem.setShaderTexture(0, RealmsTextureManager.getTextureId(String.valueOf(imageId), image));
        } else if (slotIndex == 1) {
            RenderSystem.setShaderTexture(0, PANORAMA_0);
        } else if (slotIndex == 2) {
            RenderSystem.setShaderTexture(0, PANORAMA_2);
        } else if (slotIndex == 3) {
            RenderSystem.setShaderTexture(0, PANORAMA_3);
        }
        if (active) {
            RenderSystem.setShaderColor(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsWorldSlotButton.drawTexture(matrices, x + 3, y + 3, 0.0f, 0.0f, 74, 74, 74, 74);
        RenderSystem.setShaderTexture(0, SLOT_FRAME);
        boolean bl3 = bl2 = bl && action != Action.NOTHING;
        if (bl2) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        } else if (active) {
            RenderSystem.setShaderColor(0.8f, 0.8f, 0.8f, 1.0f);
        } else {
            RenderSystem.setShaderColor(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsWorldSlotButton.drawTexture(matrices, x, y, 0.0f, 0.0f, 80, 80, 80, 80);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (active) {
            this.drawCheckmark(matrices, x, y);
        }
        RealmsWorldSlotButton.drawCenteredTextWithShadow(matrices, minecraftClient.textRenderer, slotName, x + 40, y + 66, 0xFFFFFF);
    }

    private void drawCheckmark(MatrixStack matrices, int x, int y) {
        RenderSystem.setShaderTexture(0, CHECKMARK);
        RenderSystem.enableBlend();
        RealmsWorldSlotButton.drawTexture(matrices, x + 67, y + 4, 0.0f, 0.0f, 9, 8, 9, 8);
        RenderSystem.disableBlend();
    }

    @Environment(value=EnvType.CLIENT)
    public static class State {
        final boolean isCurrentlyActiveSlot;
        final String slotName;
        final long imageId;
        @Nullable
        final String image;
        public final boolean empty;
        public final boolean minigame;
        public final Action action;
        @Nullable
        final Text actionPrompt;

        State(boolean isCurrentlyActiveSlot, String slotName, long imageId, @Nullable String image, boolean empty, boolean minigame, Action action, @Nullable Text actionPrompt) {
            this.isCurrentlyActiveSlot = isCurrentlyActiveSlot;
            this.slotName = slotName;
            this.imageId = imageId;
            this.image = image;
            this.empty = empty;
            this.minigame = minigame;
            this.action = action;
            this.actionPrompt = actionPrompt;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Action {
        NOTHING,
        SWITCH_SLOT,
        JOIN;

    }
}

