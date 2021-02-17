package net.minecraft;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class class_5838 {
	private int field_28866;
	private int field_28867;

	public boolean method_33812(int i) {
		return this.field_28867 >= this.method_33816(i);
	}

	public boolean method_33813(int i, List<ServerPlayerEntity> list) {
		int j = (int)list.stream().filter(PlayerEntity::isSleepingLongEnough).count();
		return j >= this.method_33816(i);
	}

	public int method_33816(int i) {
		return Math.max(1, MathHelper.ceil((float)(this.field_28866 * i) / 100.0F));
	}

	public void method_33811() {
		this.field_28867 = 0;
	}

	public int method_33815() {
		return this.field_28867;
	}

	public boolean method_33814(List<ServerPlayerEntity> list) {
		int i = this.field_28866;
		int j = this.field_28867;
		this.field_28866 = 0;
		this.field_28867 = 0;

		for (ServerPlayerEntity serverPlayerEntity : list) {
			if (!serverPlayerEntity.isSpectator()) {
				this.field_28866++;
				if (serverPlayerEntity.isSleeping()) {
					this.field_28867++;
				}
			}
		}

		return (j > 0 || this.field_28867 > 0) && (i != this.field_28866 || j != this.field_28867);
	}
}
