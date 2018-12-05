package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_530;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.network.ScoreboardEntry;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class SpectatorTeleportMenu implements SpectatorMenu, SpectatorMenuElement {
	private static final Ordering<ScoreboardEntry> field_3267 = Ordering.from(
		(scoreboardEntry, scoreboardEntry2) -> ComparisonChain.start().compare(scoreboardEntry.getProfile().getId(), scoreboardEntry2.getProfile().getId()).result()
	);
	private final List<SpectatorMenuElement> elements = Lists.<SpectatorMenuElement>newArrayList();

	public SpectatorTeleportMenu() {
		this(field_3267.<ScoreboardEntry>sortedCopy(MinecraftClient.getInstance().getNetworkHandler().method_2880()));
	}

	public SpectatorTeleportMenu(Collection<ScoreboardEntry> collection) {
		for (ScoreboardEntry scoreboardEntry : field_3267.sortedCopy(collection)) {
			if (scoreboardEntry.getGameMode() != GameMode.field_9219) {
				this.elements.add(new class_530(scoreboardEntry.getProfile()));
			}
		}
	}

	@Override
	public List<SpectatorMenuElement> getElements() {
		return this.elements;
	}

	@Override
	public TextComponent getMessage() {
		return new TranslatableTextComponent("spectatorMenu.teleport.prompt");
	}

	@Override
	public void selectElement(SpectatorMenuImpl spectatorMenuImpl) {
		spectatorMenuImpl.selectElement(this);
	}

	@Override
	public TextComponent method_16892() {
		return new TranslatableTextComponent("spectatorMenu.teleport");
	}

	@Override
	public void renderIcon(float f, int i) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
		Drawable.drawTexturedRect(0, 0, 0.0F, 0.0F, 16, 16, 256.0F, 256.0F);
	}

	@Override
	public boolean enabled() {
		return !this.elements.isEmpty();
	}
}
