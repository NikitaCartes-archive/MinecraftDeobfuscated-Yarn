/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.mojang.text2speech.Narrator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.options.NarratorOption;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class NarratorManager
implements ClientChatListener {
    public static final Component EMPTY = new TextComponent("");
    private static final Logger LOGGER = LogManager.getLogger();
    public static final NarratorManager INSTANCE = new NarratorManager();
    private final Narrator narrator = Narrator.getNarrator();

    @Override
    public void onChatMessage(ChatMessageType chatMessageType, Component component) {
        NarratorOption narratorOption = MinecraftClient.getInstance().options.narrator;
        if (narratorOption == NarratorOption.OFF || !this.narrator.active()) {
            return;
        }
        if (narratorOption == NarratorOption.ALL || narratorOption == NarratorOption.CHAT && chatMessageType == ChatMessageType.CHAT || narratorOption == NarratorOption.SYSTEM && chatMessageType == ChatMessageType.SYSTEM) {
            Component component2 = component instanceof TranslatableComponent && "chat.type.text".equals(((TranslatableComponent)component).getKey()) ? new TranslatableComponent("chat.type.text.narrate", ((TranslatableComponent)component).getParams()) : component;
            this.narrate(chatMessageType.interruptsNarration(), component2.getString());
        }
    }

    public void narrate(String string) {
        NarratorOption narratorOption = MinecraftClient.getInstance().options.narrator;
        if (this.narrator.active() && narratorOption != NarratorOption.OFF && narratorOption != NarratorOption.CHAT && !string.isEmpty()) {
            this.narrator.clear();
            this.narrate(true, string);
        }
    }

    private void narrate(boolean bl, String string) {
        if (SharedConstants.isDevelopment) {
            LOGGER.debug("Narrating: {}", (Object)string);
        }
        this.narrator.say(string, bl);
    }

    public void addToast(NarratorOption narratorOption) {
        this.narrator.clear();
        this.narrator.say(new TranslatableComponent("options.narrator", new Object[0]).getString() + " : " + new TranslatableComponent(narratorOption.getTranslationKey(), new Object[0]).getString(), true);
        ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
        if (this.narrator.active()) {
            if (narratorOption == NarratorOption.OFF) {
                SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.disabled", new Object[0]), null);
            } else {
                SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.enabled", new Object[0]), new TranslatableComponent(narratorOption.getTranslationKey(), new Object[0]));
            }
        } else {
            SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.disabled", new Object[0]), new TranslatableComponent("options.narrator.notavailable", new Object[0]));
        }
    }

    public boolean isActive() {
        return this.narrator.active();
    }

    public void clear() {
        this.narrator.clear();
    }

    public void destroy() {
        this.narrator.destroy();
    }
}

