package net.minecraft.world.border;

public interface WorldBorderListener {
	void onSizeChange(WorldBorder worldBorder, double d);

	void onInterpolateSize(WorldBorder worldBorder, double d, double e, long l);

	void onCenterChanged(WorldBorder worldBorder, double d, double e);

	void onWarningTimeChanged(WorldBorder worldBorder, int i);

	void onWarningBlocksChanged(WorldBorder worldBorder, int i);

	void onDamagePerBlockChanged(WorldBorder worldBorder, double d);

	void onSafeZoneChanged(WorldBorder worldBorder, double d);

	public static class WorldBorderSyncer implements WorldBorderListener {
		private final WorldBorder border;

		public WorldBorderSyncer(WorldBorder worldBorder) {
			this.border = worldBorder;
		}

		@Override
		public void onSizeChange(WorldBorder worldBorder, double d) {
			this.border.setSize(d);
		}

		@Override
		public void onInterpolateSize(WorldBorder worldBorder, double d, double e, long l) {
			this.border.interpolateSize(d, e, l);
		}

		@Override
		public void onCenterChanged(WorldBorder worldBorder, double d, double e) {
			this.border.setCenter(d, e);
		}

		@Override
		public void onWarningTimeChanged(WorldBorder worldBorder, int i) {
			this.border.setWarningTime(i);
		}

		@Override
		public void onWarningBlocksChanged(WorldBorder worldBorder, int i) {
			this.border.setWarningBlocks(i);
		}

		@Override
		public void onDamagePerBlockChanged(WorldBorder worldBorder, double d) {
			this.border.setDamagePerBlock(d);
		}

		@Override
		public void onSafeZoneChanged(WorldBorder worldBorder, double d) {
			this.border.setBuffer(d);
		}
	}
}
