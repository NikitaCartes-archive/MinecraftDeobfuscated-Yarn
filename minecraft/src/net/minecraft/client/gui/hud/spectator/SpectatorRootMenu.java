package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class SpectatorRootMenu implements SpectatorMenu {
	private final List<SpectatorMenuElement> elements = Lists.<SpectatorMenuElement>newArrayList();

	public SpectatorRootMenu() {
		this.elements.add(new SpectatorTeleportMenu());
		this.elements.add(new SpectatorTeleportTeamMenu());
	}

	@Override
	public List<SpectatorMenuElement> getElements() {
		return this.elements;
	}

	@Override
	public TextComponent getMessage() {
		return new TranslatableTextComponent("spectatorMenu.root.prompt");
	}
}
