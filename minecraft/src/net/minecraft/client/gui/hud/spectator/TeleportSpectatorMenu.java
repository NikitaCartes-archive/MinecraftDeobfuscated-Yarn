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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class TeleportSpectatorMenu implements SpectatorMenuCommandGroup, SpectatorMenuCommand {
	private static final Ordering<PlayerListEntry> ORDERING = Ordering.from(
		(playerListEntry, playerListEntry2) -> ComparisonChain.start().compare(playerListEntry.getProfile().getId(), playerListEntry2.getProfile().getId()).result()
	);
	private static final Text TELEPORT_TEXT = new TranslatableText("spectatorMenu.teleport");
	private static final Text PROMPT_TEXT = new TranslatableText("spectatorMenu.teleport.prompt");
	private final List<SpectatorMenuCommand> elements = Lists.<SpectatorMenuCommand>newArrayList();

	public TeleportSpectatorMenu() {
		this(ORDERING.<PlayerListEntry>sortedCopy(MinecraftClient.getInstance().getNetworkHandler().getPlayerList()));
	}

	public TeleportSpectatorMenu(Collection<PlayerListEntry> entries) {
		for (PlayerListEntry playerListEntry : ORDERING.sortedCopy(entries)) {
			if (playerListEntry.getGameMode() != GameMode.SPECTATOR) {
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
	public void renderIcon(MatrixStack matrixStack, float f, int i) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEXTURE);
		DrawableHelper.drawTexture(matrixStack, 0, 0, 0.0F, 0.0F, 16, 16, 256, 256);
	}

	@Override
	public boolean isEnabled() {
		return !this.elements.isEmpty();
	}
}
