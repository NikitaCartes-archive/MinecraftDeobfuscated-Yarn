package net.minecraft.world.border;

public interface WorldBorderListener {
	void onSizeChange(WorldBorder border, double size);

	void onInterpolateSize(WorldBorder border, double fromSize, double toSize, long time);

	void onCenterChanged(WorldBorder border, double centerX, double centerZ);

	void onWarningTimeChanged(WorldBorder border, int warningTime);

	void onWarningBlocksChanged(WorldBorder border, int warningBlockDistance);

	void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock);

	void onSafeZoneChanged(WorldBorder border, double safeZoneRadius);

	public static class WorldBorderSyncer implements WorldBorderListener {
		private final WorldBorder border;

		public WorldBorderSyncer(WorldBorder border) {
			this.border = border;
		}

		@Override
		public void onSizeChange(WorldBorder border, double size) {
			this.border.setSize(size);
		}

		@Override
		public void onInterpolateSize(WorldBorder border, double fromSize, double toSize, long time) {
			this.border.interpolateSize(fromSize, toSize, time);
		}

		@Override
		public void onCenterChanged(WorldBorder border, double centerX, double centerZ) {
			this.border.setCenter(centerX, centerZ);
		}

		@Override
		public void onWarningTimeChanged(WorldBorder border, int warningTime) {
			this.border.setWarningTime(warningTime);
		}

		@Override
		public void onWarningBlocksChanged(WorldBorder border, int warningBlockDistance) {
			this.border.setWarningBlocks(warningBlockDistance);
		}

		@Override
		public void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock) {
			this.border.setDamagePerBlock(damagePerBlock);
		}

		@Override
		public void onSafeZoneChanged(WorldBorder border, double safeZoneRadius) {
			this.border.setSafeZone(safeZoneRadius);
		}
	}
}
