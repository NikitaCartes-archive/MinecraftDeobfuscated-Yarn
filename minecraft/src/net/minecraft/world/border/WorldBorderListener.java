package net.minecraft.world.border;

public interface WorldBorderListener {
	void onSizeChange(WorldBorder worldBorder, double d);

	void method_11931(WorldBorder worldBorder, double d, double e, long l);

	void onCenterChanged(WorldBorder worldBorder, double d, double e);

	void onWarningTimeChanged(WorldBorder worldBorder, int i);

	void onWarningBlocksChanged(WorldBorder worldBorder, int i);

	void onDamagePerBlockChanged(WorldBorder worldBorder, double d);

	void onSafeZoneChanged(WorldBorder worldBorder, double d);
}
