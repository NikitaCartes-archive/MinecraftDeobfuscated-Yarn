package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class TeamTeleportSpectatorMenu implements SpectatorMenuCommandGroup, SpectatorMenuCommand {
	private static final Text TEAM_TELEPORT_TEXT = Text.translatable("spectatorMenu.team_teleport");
	private static final Text PROMPT_TEXT = Text.translatable("spectatorMenu.team_teleport.prompt");
	private final List<SpectatorMenuCommand> commands = Lists.<SpectatorMenuCommand>newArrayList();

	public TeamTeleportSpectatorMenu() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();

		for (Team team : minecraftClient.world.getScoreboard().getTeams()) {
			this.commands.add(new TeamTeleportSpectatorMenu.TeleportToSpecificTeamCommand(team));
		}
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
		DrawableHelper.drawTexture(matrices, 0, 0, 16.0F, 0.0F, 16, 16, 256, 256);
	}

	@Override
	public boolean isEnabled() {
		for (SpectatorMenuCommand spectatorMenuCommand : this.commands) {
			if (spectatorMenuCommand.isEnabled()) {
				return true;
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	static class TeleportToSpecificTeamCommand implements SpectatorMenuCommand {
		private final Team team;
		private final Identifier skinId;
		private final List<PlayerListEntry> scoreboardEntries;

		public TeleportToSpecificTeamCommand(Team team) {
			this.team = team;
			this.scoreboardEntries = Lists.<PlayerListEntry>newArrayList();

			for (String string : team.getPlayerList()) {
				PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(string);
				if (playerListEntry != null) {
					this.scoreboardEntries.add(playerListEntry);
				}
			}

			if (this.scoreboardEntries.isEmpty()) {
				this.skinId = DefaultSkinHelper.getTexture();
			} else {
				String string2 = ((PlayerListEntry)this.scoreboardEntries.get(Random.create().nextInt(this.scoreboardEntries.size()))).getProfile().getName();
				this.skinId = AbstractClientPlayerEntity.getSkinId(string2);
				AbstractClientPlayerEntity.loadSkin(this.skinId, string2);
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
		public void renderIcon(MatrixStack matrices, float brightness, int alpha) {
			Integer integer = this.team.getColor().getColorValue();
			if (integer != null) {
				float f = (float)(integer >> 16 & 0xFF) / 255.0F;
				float g = (float)(integer >> 8 & 0xFF) / 255.0F;
				float h = (float)(integer & 0xFF) / 255.0F;
				DrawableHelper.fill(matrices, 1, 1, 15, 15, MathHelper.packRgb(f * brightness, g * brightness, h * brightness) | alpha << 24);
			}

			RenderSystem.setShaderTexture(0, this.skinId);
			RenderSystem.setShaderColor(brightness, brightness, brightness, (float)alpha / 255.0F);
			PlayerSkinDrawer.draw(matrices, 2, 2, 12);
		}

		@Override
		public boolean isEnabled() {
			return !this.scoreboardEntries.isEmpty();
		}
	}
}
