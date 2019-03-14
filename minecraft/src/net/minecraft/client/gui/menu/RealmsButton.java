package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsAbstractButton;

@Environment(EnvType.CLIENT)
public interface RealmsButton<T extends RealmsAbstractButton<?>> {
	T getRealmsProxy();

	boolean isEnabled();

	void setEnabled(boolean bl);

	boolean isVisible();

	void setVisible(boolean bl);
}
