package net.minecraft.client.gui.hud.spectator;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class TeamTeleportSpectatorMenu implements SpectatorMenuCommandGroup, SpectatorMenuCommand {
	private static final Identifier TEXTURE = Identifier.ofVanilla("spectator/teleport_to_team");
	private static final Text TEAM_TELEPORT_TEXT = Text.translatable("spectatorMenu.team_teleport");
	private static final Text PROMPT_TEXT = Text.translatable("spectatorMenu.team_teleport.prompt");
	private final List<SpectatorMenuCommand> commands;

	public TeamTeleportSpectatorMenu() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		this.commands = getCommands(minecraftClient, minecraftClient.world.getScoreboard());
	}

	private static List<SpectatorMenuCommand> getCommands(MinecraftClient client, Scoreboard scoreboard) {
		return scoreboard.getTeams().stream().flatMap(team -> TeamTeleportSpectatorMenu.TeleportToSpecificTeamCommand.create(client, team).stream()).toList();
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
	public void renderIcon(DrawContext context, float brightness, int alpha) {
		context.drawGuiTexture(TEXTURE, 0, 0, 16, 16);
	}

	@Override
	public boolean isEnabled() {
		return !this.commands.isEmpty();
	}

	@Environment(EnvType.CLIENT)
	static class TeleportToSpecificTeamCommand implements SpectatorMenuCommand {
		private final Team team;
		private final Supplier<SkinTextures> skinTexturesSupplier;
		private final List<PlayerListEntry> scoreboardEntries;

		private TeleportToSpecificTeamCommand(Team team, List<PlayerListEntry> scoreboardEntries, Supplier<SkinTextures> skinTexturesSupplier) {
			this.team = team;
			this.scoreboardEntries = scoreboardEntries;
			this.skinTexturesSupplier = skinTexturesSupplier;
		}

		public static Optional<SpectatorMenuCommand> create(MinecraftClient client, Team team) {
			List<PlayerListEntry> list = new ArrayList();

			for (String string : team.getPlayerList()) {
				PlayerListEntry playerListEntry = client.getNetworkHandler().getPlayerListEntry(string);
				if (playerListEntry != null && playerListEntry.getGameMode() != GameMode.SPECTATOR) {
					list.add(playerListEntry);
				}
			}

			if (list.isEmpty()) {
				return Optional.empty();
			} else {
				GameProfile gameProfile = ((PlayerListEntry)list.get(Random.create().nextInt(list.size()))).getProfile();
				Supplier<SkinTextures> supplier = client.getSkinProvider().getSkinTexturesSupplier(gameProfile);
				return Optional.of(new TeamTeleportSpectatorMenu.TeleportToSpecificTeamCommand(team, list, supplier));
			}
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
		public void renderIcon(DrawContext context, float brightness, int alpha) {
			Integer integer = this.team.getColor().getColorValue();
			if (integer != null) {
				float f = (float)(integer >> 16 & 0xFF) / 255.0F;
				float g = (float)(integer >> 8 & 0xFF) / 255.0F;
				float h = (float)(integer & 0xFF) / 255.0F;
				context.fill(1, 1, 15, 15, MathHelper.packRgb(f * brightness, g * brightness, h * brightness) | alpha << 24);
			}

			context.setShaderColor(brightness, brightness, brightness, (float)alpha / 255.0F);
			PlayerSkinDrawer.draw(context, (SkinTextures)this.skinTexturesSupplier.get(), 2, 2, 12);
			context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}
}
