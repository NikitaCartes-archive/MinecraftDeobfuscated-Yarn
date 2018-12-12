package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class ClientPersistentStateManager extends PersistentStateManager {
	public ClientPersistentStateManager() {
		super(null);
	}

	@Override
	public int getNextAvailableId(DimensionType dimensionType, String string) {
		return 0;
	}
}
