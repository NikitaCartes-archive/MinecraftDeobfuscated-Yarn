package net.minecraft.world.border;

public interface WorldBorderListener {
	void onSizeChange(WorldBorder worldBorder, double d);

	void method_11931(WorldBorder worldBorder, double d, double e, long l);

	void onCenterChanged(WorldBorder worldBorder, double d, double e);

	void onWarningTimeChanged(WorldBorder worldBorder, int i);

	void onWarningBlocksChanged(WorldBorder worldBorder, int i);

	void onDamagePerBlockChanged(WorldBorder worldBorder, double d);

	void onSafeZoneChanged(WorldBorder worldBorder, double d);

	public static class class_3976 implements WorldBorderListener {
		private final WorldBorder field_17652;

		public class_3976(WorldBorder worldBorder) {
			this.field_17652 = worldBorder;
		}

		@Override
		public void onSizeChange(WorldBorder worldBorder, double d) {
			this.field_17652.setSize(d);
		}

		@Override
		public void method_11931(WorldBorder worldBorder, double d, double e, long l) {
			this.field_17652.method_11957(d, e, l);
		}

		@Override
		public void onCenterChanged(WorldBorder worldBorder, double d, double e) {
			this.field_17652.setCenter(d, e);
		}

		@Override
		public void onWarningTimeChanged(WorldBorder worldBorder, int i) {
			this.field_17652.setWarningTime(i);
		}

		@Override
		public void onWarningBlocksChanged(WorldBorder worldBorder, int i) {
			this.field_17652.setWarningBlocks(i);
		}

		@Override
		public void onDamagePerBlockChanged(WorldBorder worldBorder, double d) {
			this.field_17652.setDamagePerBlock(d);
		}

		@Override
		public void onSafeZoneChanged(WorldBorder worldBorder, double d) {
			this.field_17652.setSafeZone(d);
		}
	}
}
