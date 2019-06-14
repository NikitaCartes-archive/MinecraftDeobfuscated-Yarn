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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class TeleportSpectatorMenu implements SpectatorMenuCommandGroup, SpectatorMenuCommand {
	private static final Ordering<PlayerListEntry> ORDERING = Ordering.from(
		(playerListEntry, playerListEntry2) -> ComparisonChain.start().compare(playerListEntry.getProfile().getId(), playerListEntry2.getProfile().getId()).result()
	);
	private final List<SpectatorMenuCommand> elements = Lists.<SpectatorMenuCommand>newArrayList();

	public TeleportSpectatorMenu() {
		this(ORDERING.<PlayerListEntry>sortedCopy(MinecraftClient.getInstance().method_1562().getPlayerList()));
	}

	public TeleportSpectatorMenu(Collection<PlayerListEntry> collection) {
		for (PlayerListEntry playerListEntry : ORDERING.sortedCopy(collection)) {
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
	public Text getPrompt() {
		return new TranslatableText("spectatorMenu.teleport.prompt");
	}

	@Override
	public void use(SpectatorMenu spectatorMenu) {
		spectatorMenu.method_2778(this);
	}

	@Override
	public Text getName() {
		return new TranslatableText("spectatorMenu.teleport");
	}

	@Override
	public void renderIcon(float f, int i) {
		MinecraftClient.getInstance().method_1531().bindTexture(SpectatorHud.SPECTATOR_TEX);
		DrawableHelper.blit(0, 0, 0.0F, 0.0F, 16, 16, 256, 256);
	}

	@Override
	public boolean enabled() {
		return !this.elements.isEmpty();
	}
}
