package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ScoreboardEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TeamTeleportSpectatorMenu implements SpectatorMenuCommandGroup, SpectatorMenuCommand {
	private final List<SpectatorMenuCommand> commands = Lists.<SpectatorMenuCommand>newArrayList();

	public TeamTeleportSpectatorMenu() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();

		for (ScoreboardTeam scoreboardTeam : minecraftClient.field_1687.method_8428().getTeams()) {
			this.commands.add(new TeamTeleportSpectatorMenu.TeleportToSpecificTeamCommand(scoreboardTeam));
		}
	}

	@Override
	public List<SpectatorMenuCommand> getCommands() {
		return this.commands;
	}

	@Override
	public TextComponent method_2781() {
		return new TranslatableTextComponent("spectatorMenu.team_teleport.prompt");
	}

	@Override
	public void use(SpectatorMenu spectatorMenu) {
		spectatorMenu.method_2778(this);
	}

	@Override
	public TextComponent method_16892() {
		return new TranslatableTextComponent("spectatorMenu.team_teleport");
	}

	@Override
	public void renderIcon(float f, int i) {
		MinecraftClient.getInstance().method_1531().method_4618(SpectatorHud.field_2199);
		DrawableHelper.drawTexturedRect(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
	}

	@Override
	public boolean enabled() {
		for (SpectatorMenuCommand spectatorMenuCommand : this.commands) {
			if (spectatorMenuCommand.enabled()) {
				return true;
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	class TeleportToSpecificTeamCommand implements SpectatorMenuCommand {
		private final ScoreboardTeam team;
		private final Identifier field_3276;
		private final List<ScoreboardEntry> scoreboardEntries;

		public TeleportToSpecificTeamCommand(ScoreboardTeam scoreboardTeam) {
			this.team = scoreboardTeam;
			this.scoreboardEntries = Lists.<ScoreboardEntry>newArrayList();

			for (String string : scoreboardTeam.getPlayerList()) {
				ScoreboardEntry scoreboardEntry = MinecraftClient.getInstance().method_1562().method_2874(string);
				if (scoreboardEntry != null) {
					this.scoreboardEntries.add(scoreboardEntry);
				}
			}

			if (this.scoreboardEntries.isEmpty()) {
				this.field_3276 = DefaultSkinHelper.method_4649();
			} else {
				String string2 = ((ScoreboardEntry)this.scoreboardEntries.get(new Random().nextInt(this.scoreboardEntries.size()))).getProfile().getName();
				this.field_3276 = AbstractClientPlayerEntity.method_3124(string2);
				AbstractClientPlayerEntity.method_3120(this.field_3276, string2);
			}
		}

		@Override
		public void use(SpectatorMenu spectatorMenu) {
			spectatorMenu.method_2778(new TeleportSpectatorMenu(this.scoreboardEntries));
		}

		@Override
		public TextComponent method_16892() {
			return this.team.method_1140();
		}

		@Override
		public void renderIcon(float f, int i) {
			Integer integer = this.team.getColor().getColor();
			if (integer != null) {
				float g = (float)(integer >> 16 & 0xFF) / 255.0F;
				float h = (float)(integer >> 8 & 0xFF) / 255.0F;
				float j = (float)(integer & 0xFF) / 255.0F;
				DrawableHelper.drawRect(1, 1, 15, 15, MathHelper.packRgb(g * f, h * f, j * f) | i << 24);
			}

			MinecraftClient.getInstance().method_1531().method_4618(this.field_3276);
			GlStateManager.color4f(f, f, f, (float)i / 255.0F);
			DrawableHelper.drawTexturedRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
			DrawableHelper.drawTexturedRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
		}

		@Override
		public boolean enabled() {
			return !this.scoreboardEntries.isEmpty();
		}
	}
}
