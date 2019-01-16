package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
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
public class SpectatorTeleportTeamMenu implements SpectatorMenu, SpectatorMenuElement {
	private final List<SpectatorMenuElement> field_3272 = Lists.<SpectatorMenuElement>newArrayList();

	public SpectatorTeleportTeamMenu() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();

		for (ScoreboardTeam scoreboardTeam : minecraftClient.world.getScoreboard().getTeams()) {
			this.field_3272.add(new SpectatorTeleportTeamMenu.class_541(scoreboardTeam));
		}
	}

	@Override
	public List<SpectatorMenuElement> getElements() {
		return this.field_3272;
	}

	@Override
	public TextComponent getMessage() {
		return new TranslatableTextComponent("spectatorMenu.team_teleport.prompt");
	}

	@Override
	public void selectElement(SpectatorMenuImpl spectatorMenuImpl) {
		spectatorMenuImpl.selectElement(this);
	}

	@Override
	public TextComponent method_16892() {
		return new TranslatableTextComponent("spectatorMenu.team_teleport");
	}

	@Override
	public void renderIcon(float f, int i) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
		Drawable.drawTexturedRect(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
	}

	@Override
	public boolean enabled() {
		for (SpectatorMenuElement spectatorMenuElement : this.field_3272) {
			if (spectatorMenuElement.enabled()) {
				return true;
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	class class_541 implements SpectatorMenuElement {
		private final ScoreboardTeam field_3275;
		private final Identifier field_3276;
		private final List<ScoreboardEntry> field_3274;

		public class_541(ScoreboardTeam scoreboardTeam) {
			this.field_3275 = scoreboardTeam;
			this.field_3274 = Lists.<ScoreboardEntry>newArrayList();

			for (String string : scoreboardTeam.getPlayerList()) {
				ScoreboardEntry scoreboardEntry = MinecraftClient.getInstance().getNetworkHandler().method_2874(string);
				if (scoreboardEntry != null) {
					this.field_3274.add(scoreboardEntry);
				}
			}

			if (this.field_3274.isEmpty()) {
				this.field_3276 = DefaultSkinHelper.getTexture();
			} else {
				String string2 = ((ScoreboardEntry)this.field_3274.get(new Random().nextInt(this.field_3274.size()))).getProfile().getName();
				this.field_3276 = AbstractClientPlayerEntity.method_3124(string2);
				AbstractClientPlayerEntity.method_3120(this.field_3276, string2);
			}
		}

		@Override
		public void selectElement(SpectatorMenuImpl spectatorMenuImpl) {
			spectatorMenuImpl.selectElement(new SpectatorTeleportMenu(this.field_3274));
		}

		@Override
		public TextComponent method_16892() {
			return this.field_3275.getDisplayName();
		}

		@Override
		public void renderIcon(float f, int i) {
			Integer integer = this.field_3275.getColor().getColor();
			if (integer != null) {
				float g = (float)(integer >> 16 & 0xFF) / 255.0F;
				float h = (float)(integer >> 8 & 0xFF) / 255.0F;
				float j = (float)(integer & 0xFF) / 255.0F;
				Drawable.drawRect(1, 1, 15, 15, MathHelper.packRgb(g * f, h * f, j * f) | i << 24);
			}

			MinecraftClient.getInstance().getTextureManager().bindTexture(this.field_3276);
			GlStateManager.color4f(f, f, f, (float)i / 255.0F);
			Drawable.drawTexturedRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
			Drawable.drawTexturedRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
		}

		@Override
		public boolean enabled() {
			return !this.field_3274.isEmpty();
		}
	}
}
