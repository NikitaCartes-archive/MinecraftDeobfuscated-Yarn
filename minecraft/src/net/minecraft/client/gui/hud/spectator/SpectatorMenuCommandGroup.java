package net.minecraft.client.gui.hud.spectator;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public interface SpectatorMenuCommandGroup {
	List<SpectatorMenuCommand> getCommands();

	TextComponent method_2781();
}
