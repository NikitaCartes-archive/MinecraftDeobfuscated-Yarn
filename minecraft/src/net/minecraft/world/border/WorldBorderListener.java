package net.minecraft.world.border;

public interface WorldBorderListener {
	void method_11934(WorldBorder worldBorder, double d);

	void method_11931(WorldBorder worldBorder, double d, double e, long l);

	void method_11930(WorldBorder worldBorder, double d, double e);

	void method_11932(WorldBorder worldBorder, int i);

	void method_11933(WorldBorder worldBorder, int i);

	void method_11929(WorldBorder worldBorder, double d);

	void method_11935(WorldBorder worldBorder, double d);

	public static class WorldBorderSyncer implements WorldBorderListener {
		private final WorldBorder field_17652;

		public WorldBorderSyncer(WorldBorder worldBorder) {
			this.field_17652 = worldBorder;
		}

		@Override
		public void method_11934(WorldBorder worldBorder, double d) {
			this.field_17652.setSize(d);
		}

		@Override
		public void method_11931(WorldBorder worldBorder, double d, double e, long l) {
			this.field_17652.interpolateSize(d, e, l);
		}

		@Override
		public void method_11930(WorldBorder worldBorder, double d, double e) {
			this.field_17652.setCenter(d, e);
		}

		@Override
		public void method_11932(WorldBorder worldBorder, int i) {
			this.field_17652.setWarningTime(i);
		}

		@Override
		public void method_11933(WorldBorder worldBorder, int i) {
			this.field_17652.setWarningBlocks(i);
		}

		@Override
		public void method_11929(WorldBorder worldBorder, double d) {
			this.field_17652.setDamagePerBlock(d);
		}

		@Override
		public void method_11935(WorldBorder worldBorder, double d) {
			this.field_17652.setBuffer(d);
		}
	}
}
