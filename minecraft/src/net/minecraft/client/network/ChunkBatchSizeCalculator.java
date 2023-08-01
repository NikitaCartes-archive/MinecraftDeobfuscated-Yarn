package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ChunkBatchSizeCalculator {
	private final int[] field_45584;
	private final int[] field_45585;
	private int field_45586;
	private int field_45587;

	public ChunkBatchSizeCalculator(int i) {
		this.field_45584 = new int[i];
		this.field_45585 = new int[i];
	}

	public void method_52769(int i, long l) {
		this.field_45584[this.field_45586] = i;
		this.field_45585[this.field_45586] = (int)MathHelper.clamp((float)l, 0.0F, 15000.0F);
		this.field_45586 = (this.field_45586 + 1) % this.field_45584.length;
		if (this.field_45587 < this.field_45584.length) {
			this.field_45587++;
		}
	}

	public double method_52768() {
		int i = 0;
		int j = 0;

		for (int k = 0; k < Math.min(this.field_45587, this.field_45584.length); k++) {
			i += this.field_45584[k];
			j += this.field_45585[k];
		}

		return (double)j * 1.0 / (double)i;
	}
}
