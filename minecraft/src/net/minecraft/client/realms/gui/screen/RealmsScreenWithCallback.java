package net.minecraft.client.realms.gui.screen;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.WorldTemplate;

@Environment(EnvType.CLIENT)
public abstract class RealmsScreenWithCallback extends RealmsScreen {
	protected abstract void callback(@Nullable WorldTemplate template);
}
