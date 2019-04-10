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
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class TeleportSpectatorMenu implements SpectatorMenuCommandGroup, SpectatorMenuCommand {
	private static final Ordering<PlayerListEntry> field_3267 = Ordering.from(
		(playerListEntry, playerListEntry2) -> ComparisonChain.start().compare(playerListEntry.getProfile().getId(), playerListEntry2.getProfile().getId()).result()
	);
	private final List<SpectatorMenuCommand> elements = Lists.<SpectatorMenuCommand>newArrayList();

	public TeleportSpectatorMenu() {
		this(field_3267.<PlayerListEntry>sortedCopy(MinecraftClient.getInstance().getNetworkHandler().getScoreboardEntries()));
	}

	public TeleportSpectatorMenu(Collection<PlayerListEntry> collection) {
		for (PlayerListEntry playerListEntry : field_3267.sortedCopy(collection)) {
			if (playerListEntry.getGameMode() != GameMode.field_9219) {
				this.elements.add(new TeleportToSpecificPlayerSpectatorCommand(playerListEntry.getProfile()));
			}
		}
	}

	@Override
	public List<SpectatorMenuCommand> getCommands() {
		return this.elements;
	}

	@Override
	public TextComponent getPrompt() {
		return new TranslatableTextComponent("spectatorMenu.teleport.prompt");
	}

	@Override
	public void use(SpectatorMenu spectatorMenu) {
		spectatorMenu.selectElement(this);
	}

	@Override
	public TextComponent getName() {
		return new TranslatableTextComponent("spectatorMenu.teleport");
	}

	@Override
	public void renderIcon(float f, int i) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
		DrawableHelper.blit(0, 0, 0.0F, 0.0F, 16, 16, 256, 256);
	}

	@Override
	public boolean enabled() {
		return !this.elements.isEmpty();
	}
}
