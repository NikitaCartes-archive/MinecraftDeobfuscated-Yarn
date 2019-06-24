/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Collection;
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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class TeleportSpectatorMenu
implements SpectatorMenuCommandGroup,
SpectatorMenuCommand {
    private static final Ordering<PlayerListEntry> ORDERING = Ordering.from((playerListEntry, playerListEntry2) -> ComparisonChain.start().compare(playerListEntry.getProfile().getId(), playerListEntry2.getProfile().getId()).result());
    private final List<SpectatorMenuCommand> elements = Lists.newArrayList();

    public TeleportSpectatorMenu() {
        this(ORDERING.sortedCopy(MinecraftClient.getInstance().getNetworkHandler().getPlayerList()));
    }

    public TeleportSpectatorMenu(Collection<PlayerListEntry> collection) {
        for (PlayerListEntry playerListEntry : ORDERING.sortedCopy(collection)) {
            if (playerListEntry.getGameMode() == GameMode.SPECTATOR) continue;
            this.elements.add(new TeleportToSpecificPlayerSpectatorCommand(playerListEntry.getProfile()));
        }
    }

    @Override
    public List<SpectatorMenuCommand> getCommands() {
        return this.elements;
    }

    @Override
    public Text getPrompt() {
        return new TranslatableText("spectatorMenu.teleport.prompt", new Object[0]);
    }

    @Override
    public void use(SpectatorMenu spectatorMenu) {
        spectatorMenu.selectElement(this);
    }

    @Override
    public Text getName() {
        return new TranslatableText("spectatorMenu.teleport", new Object[0]);
    }

    @Override
    public void renderIcon(float f, int i) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
        DrawableHelper.blit(0, 0, 0.0f, 0.0f, 16, 16, 256, 256);
    }

    @Override
    public boolean isEnabled() {
        return !this.elements.isEmpty();
    }
}

