/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.spectator.RootSpectatorCommandGroup;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

@Environment(value=EnvType.CLIENT)
public class SpectatorMenu {
    private static final SpectatorMenuCommand CLOSE_COMMAND = new CloseSpectatorMenuCommand();
    private static final SpectatorMenuCommand PREVIOUS_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(-1, true);
    private static final SpectatorMenuCommand NEXT_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(1, true);
    private static final SpectatorMenuCommand DISABLED_NEXT_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(1, false);
    public static final SpectatorMenuCommand BLANK_COMMAND = new SpectatorMenuCommand(){

        @Override
        public void use(SpectatorMenu spectatorMenu) {
        }

        @Override
        public Component getName() {
            return new TextComponent("");
        }

        @Override
        public void renderIcon(float f, int i) {
        }

        @Override
        public boolean enabled() {
            return false;
        }
    };
    private final SpectatorMenuCloseCallback closeCallback;
    private final List<SpectatorMenuState> stateStack = Lists.newArrayList();
    private SpectatorMenuCommandGroup currentGroup = new RootSpectatorCommandGroup();
    private int selectedSlot = -1;
    private int page;

    public SpectatorMenu(SpectatorMenuCloseCallback spectatorMenuCloseCallback) {
        this.closeCallback = spectatorMenuCloseCallback;
    }

    public SpectatorMenuCommand getCommand(int i) {
        int j = i + this.page * 6;
        if (this.page > 0 && i == 0) {
            return PREVIOUS_PAGE_COMMAND;
        }
        if (i == 7) {
            if (j < this.currentGroup.getCommands().size()) {
                return NEXT_PAGE_COMMAND;
            }
            return DISABLED_NEXT_PAGE_COMMAND;
        }
        if (i == 8) {
            return CLOSE_COMMAND;
        }
        if (j < 0 || j >= this.currentGroup.getCommands().size()) {
            return BLANK_COMMAND;
        }
        return MoreObjects.firstNonNull(this.currentGroup.getCommands().get(j), BLANK_COMMAND);
    }

    public List<SpectatorMenuCommand> getCommands() {
        ArrayList<SpectatorMenuCommand> list = Lists.newArrayList();
        for (int i = 0; i <= 8; ++i) {
            list.add(this.getCommand(i));
        }
        return list;
    }

    public SpectatorMenuCommand getSelectedCommand() {
        return this.getCommand(this.selectedSlot);
    }

    public SpectatorMenuCommandGroup getCurrentGroup() {
        return this.currentGroup;
    }

    public void setSelectedSlot(int i) {
        SpectatorMenuCommand spectatorMenuCommand = this.getCommand(i);
        if (spectatorMenuCommand != BLANK_COMMAND) {
            if (this.selectedSlot == i && spectatorMenuCommand.enabled()) {
                spectatorMenuCommand.use(this);
            } else {
                this.selectedSlot = i;
            }
        }
    }

    public void close() {
        this.closeCallback.close(this);
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }

    public void selectElement(SpectatorMenuCommandGroup spectatorMenuCommandGroup) {
        this.stateStack.add(this.getCurrentState());
        this.currentGroup = spectatorMenuCommandGroup;
        this.selectedSlot = -1;
        this.page = 0;
    }

    public SpectatorMenuState getCurrentState() {
        return new SpectatorMenuState(this.currentGroup, this.getCommands(), this.selectedSlot);
    }

    @Environment(value=EnvType.CLIENT)
    static class ChangePageSpectatorMenuCommand
    implements SpectatorMenuCommand {
        private final int direction;
        private final boolean enabled;

        public ChangePageSpectatorMenuCommand(int i, boolean bl) {
            this.direction = i;
            this.enabled = bl;
        }

        @Override
        public void use(SpectatorMenu spectatorMenu) {
            spectatorMenu.page = spectatorMenu.page + this.direction;
        }

        @Override
        public Component getName() {
            if (this.direction < 0) {
                return new TranslatableComponent("spectatorMenu.previous_page", new Object[0]);
            }
            return new TranslatableComponent("spectatorMenu.next_page", new Object[0]);
        }

        @Override
        public void renderIcon(float f, int i) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
            if (this.direction < 0) {
                DrawableHelper.blit(0, 0, 144.0f, 0.0f, 16, 16, 256, 256);
            } else {
                DrawableHelper.blit(0, 0, 160.0f, 0.0f, 16, 16, 256, 256);
            }
        }

        @Override
        public boolean enabled() {
            return this.enabled;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CloseSpectatorMenuCommand
    implements SpectatorMenuCommand {
        private CloseSpectatorMenuCommand() {
        }

        @Override
        public void use(SpectatorMenu spectatorMenu) {
            spectatorMenu.close();
        }

        @Override
        public Component getName() {
            return new TranslatableComponent("spectatorMenu.close", new Object[0]);
        }

        @Override
        public void renderIcon(float f, int i) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
            DrawableHelper.blit(0, 0, 128.0f, 0.0f, 16, 16, 256, 256);
        }

        @Override
        public boolean enabled() {
            return true;
        }
    }
}

