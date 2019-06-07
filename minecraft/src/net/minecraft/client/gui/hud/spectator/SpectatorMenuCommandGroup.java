package net.minecraft.client.gui.hud.spectator;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public interface SpectatorMenuCommandGroup {
	List<SpectatorMenuCommand> getCommands();

	Text getPrompt();
}
