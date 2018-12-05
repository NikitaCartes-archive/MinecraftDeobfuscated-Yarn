package net.minecraft.world.chunk.light;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2804;
import net.minecraft.class_3552;
import net.minecraft.class_3562;
import net.minecraft.class_3572;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkView;

public class LightingProvider implements LightingView {
	private final LightStorage<?, ?> field_15814;
	@Nullable
	private final LightStorage<?, ?> field_15813;

	public LightingProvider(ChunkView chunkView, boolean bl) {
		this.field_15814 = new class_3552(chunkView);
		this.field_15813 = bl ? new class_3572(chunkView) : null;
	}

	public void queueLightCheck(BlockPos blockPos) {
		this.field_15814.queueLightCheck(blockPos);
		if (this.field_15813 != null) {
			this.field_15813.queueLightCheck(blockPos);
		}
	}

	public void method_15560(BlockPos blockPos, int i) {
		this.field_15814.method_15514(blockPos, i);
	}

	public boolean method_15561() {
		return this.field_15813 != null && this.field_15813.method_15518() ? true : this.field_15814.method_15518();
	}

	public int method_15563(int i, boolean bl, boolean bl2) {
		if (this.field_15813 != null) {
			int j = i / 2;
			int k = this.field_15814.method_15516(j, bl, bl2);
			int l = i - j + k;
			int m = this.field_15813.method_15516(l, bl, bl2);
			return k == 0 && m > 0 ? this.field_15814.method_15516(m, bl, bl2) : m;
		} else {
			return this.field_15814.method_15516(i, bl, bl2);
		}
	}

	@Override
	public void method_15551(int i, int j, int k, boolean bl) {
		this.field_15814.method_15551(i, j, k, bl);
		if (this.field_15813 != null) {
			this.field_15813.method_15551(i, j, k, bl);
		}
	}

	public void method_15557(int i, int j, boolean bl) {
		this.field_15814.method_15512(i, j, bl);
		if (this.field_15813 != null) {
			this.field_15813.method_15512(i, j, bl);
		}
	}

	public class_3562 get(LightType lightType) {
		if (lightType == LightType.field_9282) {
			return this.field_15814;
		} else {
			return (class_3562)(this.field_15813 == null ? class_3562.class_3563.field_15812 : this.field_15813);
		}
	}

	@Environment(EnvType.CLIENT)
	public String method_15564(LightType lightType, BlockPos blockPos) {
		if (lightType == LightType.field_9282) {
			return this.field_15814.method_15520(BlockPos.method_10090(blockPos.asLong()));
		} else {
			return this.field_15813 != null ? this.field_15813.method_15520(BlockPos.method_10090(blockPos.asLong())) : "n/a";
		}
	}

	public void method_15558(LightType lightType, int i, int j, int k, class_2804 arg) {
		if (lightType == LightType.field_9282) {
			this.field_15814.method_15515(i, j, k, arg);
		} else if (this.field_15813 != null) {
			this.field_15813.method_15515(i, j, k, arg);
		}
	}
}
