/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud.spectator;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.TeleportToSpecificPlayerSpectatorCommand;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class TeleportSpectatorMenu
implements SpectatorMenuCommandGroup,
SpectatorMenuCommand {
    private static final Comparator<PlayerListEntry> ORDERING = Comparator.comparing(a -> a.getProfile().getId());
    private static final Text TELEPORT_TEXT = Text.translatable("spectatorMenu.teleport");
    private static final Text PROMPT_TEXT = Text.translatable("spectatorMenu.teleport.prompt");
    private final List<SpectatorMenuCommand> elements;

    public TeleportSpectatorMenu() {
        this(MinecraftClient.getInstance().getNetworkHandler().getListedPlayerListEntries());
    }

    public TeleportSpectatorMenu(Collection<PlayerListEntry> entries) {
        this.elements = entries.stream().filter(entry -> entry.getGameMode() != GameMode.SPECTATOR).sorted(ORDERING).map(entry -> new TeleportToSpecificPlayerSpectatorCommand(entry.getProfile())).toList();
    }

    @Override
    public List<SpectatorMenuCommand> getCommands() {
        return this.elements;
    }

    @Override
    public Text getPrompt() {
        return PROMPT_TEXT;
    }

    @Override
    public void use(SpectatorMenu menu) {
        menu.selectElement(this);
    }

    @Override
    public Text getName() {
        return TELEPORT_TEXT;
    }

    @Override
    public void renderIcon(MatrixStack matrices, float brightness, int alpha) {
        RenderSystem.setShaderTexture(0, SpectatorHud.SPECTATOR_TEXTURE);
        DrawableHelper.drawTexture(matrices, 0, 0, 0.0f, 0.0f, 16, 16, 256, 256);
    }

    @Override
    public boolean isEnabled() {
        return !this.elements.isEmpty();
    }
}

