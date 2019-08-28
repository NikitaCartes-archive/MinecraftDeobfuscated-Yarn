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
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class NarratorManager
implements ClientChatListener {
    public static final Text EMPTY = new LiteralText("");
    private static final Logger LOGGER = LogManager.getLogger();
    public static final NarratorManager INSTANCE = new NarratorManager();
    private final Narrator narrator = Narrator.getNarrator();

    @Override
    public void onChatMessage(MessageType messageType, Text text) {
        NarratorOption narratorOption = NarratorManager.getNarratorOption();
        if (narratorOption == NarratorOption.OFF || !this.narrator.active()) {
            return;
        }
        if (narratorOption == NarratorOption.ALL || narratorOption == NarratorOption.CHAT && messageType == MessageType.CHAT || narratorOption == NarratorOption.SYSTEM && messageType == MessageType.SYSTEM) {
            Text text2 = text instanceof TranslatableText && "chat.type.text".equals(((TranslatableText)text).getKey()) ? new TranslatableText("chat.type.text.narrate", ((TranslatableText)text).getArgs()) : text;
            this.narrate(messageType.interruptsNarration(), text2.getString());
        }
    }

    public void narrate(String string) {
        NarratorOption narratorOption = NarratorManager.getNarratorOption();
        if (this.narrator.active() && narratorOption != NarratorOption.OFF && narratorOption != NarratorOption.CHAT && !string.isEmpty()) {
            this.narrator.clear();
            this.narrate(true, string);
        }
    }

    private static NarratorOption getNarratorOption() {
        return MinecraftClient.getInstance().options.narrator;
    }

    private void narrate(boolean bl, String string) {
        if (SharedConstants.isDevelopment) {
            LOGGER.debug("Narrating: {}", (Object)string);
        }
        this.narrator.say(string, bl);
    }

    public void addToast(NarratorOption narratorOption) {
        this.clear();
        this.narrator.say(new TranslatableText("options.narrator", new Object[0]).getString() + " : " + new TranslatableText(narratorOption.getTranslationKey(), new Object[0]).getString(), true);
        ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
        if (this.narrator.active()) {
            if (narratorOption == NarratorOption.OFF) {
                SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, new TranslatableText("narrator.toast.disabled", new Object[0]), null);
            } else {
                SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, new TranslatableText("narrator.toast.enabled", new Object[0]), new TranslatableText(narratorOption.getTranslationKey(), new Object[0]));
            }
        } else {
            SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, new TranslatableText("narrator.toast.disabled", new Object[0]), new TranslatableText("options.narrator.notavailable", new Object[0]));
        }
    }

    public boolean isActive() {
        return this.narrator.active();
    }

    public void clear() {
        if (NarratorManager.getNarratorOption() == NarratorOption.OFF || !this.narrator.active()) {
            return;
        }
        this.narrator.clear();
    }

    public void destroy() {
        this.narrator.destroy();
    }
}

