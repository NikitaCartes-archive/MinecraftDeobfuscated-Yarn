/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.TeleportSpectatorMenu;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class TeamTeleportSpectatorMenu
implements SpectatorMenuCommandGroup,
SpectatorMenuCommand {
    private static final Text TEAM_TELEPORT_TEXT = Text.translatable("spectatorMenu.team_teleport");
    private static final Text PROMPT_TEXT = Text.translatable("spectatorMenu.team_teleport.prompt");
    private final List<SpectatorMenuCommand> commands;

    public TeamTeleportSpectatorMenu() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        this.commands = TeamTeleportSpectatorMenu.getCommands(minecraftClient, minecraftClient.world.getScoreboard());
    }

    private static List<SpectatorMenuCommand> getCommands(MinecraftClient client, Scoreboard scoreboard) {
        return scoreboard.getTeams().stream().flatMap(team -> TeleportToSpecificTeamCommand.create(client, team).stream()).toList();
    }

    @Override
    public List<SpectatorMenuCommand> getCommands() {
        return this.commands;
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
        return TEAM_TELEPORT_TEXT;
    }

    @Override
    public void renderIcon(MatrixStack matrices, float brightness, int alpha) {
        RenderSystem.setShaderTexture(0, SpectatorHud.SPECTATOR_TEXTURE);
        DrawableHelper.drawTexture(matrices, 0, 0, 16.0f, 0.0f, 16, 16, 256, 256);
    }

    @Override
    public boolean isEnabled() {
        return !this.commands.isEmpty();
    }

    @Environment(value=EnvType.CLIENT)
    static class TeleportToSpecificTeamCommand
    implements SpectatorMenuCommand {
        private final Team team;
        private final Identifier skinId;
        private final List<PlayerListEntry> scoreboardEntries;

        private TeleportToSpecificTeamCommand(Team team, List<PlayerListEntry> scoreboardEntries, Identifier skinId) {
            this.team = team;
            this.scoreboardEntries = scoreboardEntries;
            this.skinId = skinId;
        }

        public static Optional<SpectatorMenuCommand> create(MinecraftClient client, Team team) {
            ArrayList<PlayerListEntry> list = new ArrayList<PlayerListEntry>();
            for (String string : team.getPlayerList()) {
                PlayerListEntry playerListEntry = client.getNetworkHandler().getPlayerListEntry(string);
                if (playerListEntry == null || playerListEntry.getGameMode() == GameMode.SPECTATOR) continue;
                list.add(playerListEntry);
            }
            if (list.isEmpty()) {
                return Optional.empty();
            }
            GameProfile gameProfile = ((PlayerListEntry)list.get(Random.create().nextInt(list.size()))).getProfile();
            Identifier identifier = client.getSkinProvider().loadSkin(gameProfile);
            return Optional.of(new TeleportToSpecificTeamCommand(team, list, identifier));
        }

        @Override
        public void use(SpectatorMenu menu) {
            menu.selectElement(new TeleportSpectatorMenu(this.scoreboardEntries));
        }

        @Override
        public Text getName() {
            return this.team.getDisplayName();
        }

        @Override
        public void renderIcon(MatrixStack matrices, float brightness, int alpha) {
            Integer integer = this.team.getColor().getColorValue();
            if (integer != null) {
                float f = (float)(integer >> 16 & 0xFF) / 255.0f;
                float g = (float)(integer >> 8 & 0xFF) / 255.0f;
                float h = (float)(integer & 0xFF) / 255.0f;
                DrawableHelper.fill(matrices, 1, 1, 15, 15, MathHelper.packRgb(f * brightness, g * brightness, h * brightness) | alpha << 24);
            }
            RenderSystem.setShaderTexture(0, this.skinId);
            RenderSystem.setShaderColor(brightness, brightness, brightness, (float)alpha / 255.0f);
            PlayerSkinDrawer.draw(matrices, 2, 2, 12);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}

