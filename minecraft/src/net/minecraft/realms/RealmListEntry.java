package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4280;

@Environment(EnvType.CLIENT)
public abstract class RealmListEntry extends class_4280.class_4281<RealmListEntry> {
	@Override
	public abstract void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f);

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return false;
	}
}
