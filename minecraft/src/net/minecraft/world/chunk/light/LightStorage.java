package net.minecraft.world.chunk.light;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2804;
import net.minecraft.class_3554;
import net.minecraft.class_3556;
import net.minecraft.class_3560;
import net.minecraft.class_3562;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkView;

public abstract class LightStorage<M extends class_3556<M>, S extends class_3560<M>> extends class_3554 implements class_3562 {
	private static final Direction[] field_16513 = Direction.values();
	protected final ChunkView view;
	protected final LightType type;
	protected final S field_15793;
	private boolean field_15794 = false;
	private final BlockPos.Mutable field_16514 = new BlockPos.Mutable();
	private final BlockPos.Mutable field_16515 = new BlockPos.Mutable();
	private final BlockPos.Mutable field_16512 = new BlockPos.Mutable();

	public LightStorage(ChunkView chunkView, LightType lightType, S arg) {
		super(16, 256, 8192);
		this.view = chunkView;
		this.type = lightType;
		this.field_15793 = arg;
	}

	@Override
	protected void queueLightCheck(long l) {
		this.field_15793.method_15539();
		if (this.field_15793.method_15524(BlockPos.method_10090(l))) {
			super.queueLightCheck(l);
		}
	}

	protected int method_16340(long l, long m) {
		this.field_15793.method_15539();
		if (!BlockPos.isHeightInvalid(l) && !BlockPos.isHeightInvalid(m)) {
			this.field_16514.method_16363(l);
			this.field_16515.method_16363(m);
			int i = this.field_16514.getX() >> 4;
			int j = this.field_16514.getZ() >> 4;
			int k = this.field_16515.getX() >> 4;
			int n = this.field_16515.getZ() >> 4;
			BlockView blockView = this.view.get(k, n);
			if (blockView == null) {
				return 16;
			} else {
				BlockState blockState = blockView.getBlockState(this.field_16515);
				BlockView blockView2 = this.view.method_16399();
				int o = blockState.method_11581(blockView2, this.field_16515);
				if (!blockState.method_16386() && o >= 15) {
					return 16;
				} else {
					BlockView blockView3;
					if (i == k && j == n) {
						blockView3 = blockView;
					} else {
						blockView3 = this.view.get(i, j);
					}

					if (blockView3 == null) {
						return 16;
					} else {
						int p = Integer.signum(this.field_16515.getX() - this.field_16514.getX());
						int q = Integer.signum(this.field_16515.getY() - this.field_16514.getY());
						int r = Integer.signum(this.field_16515.getZ() - this.field_16514.getZ());
						Direction direction = Direction.method_16365(this.field_16512.set(p, q, r));
						if (direction == null) {
							return 16;
						} else {
							BlockState blockState2 = blockView3.getBlockState(this.field_16514);
							boolean bl = blockState2.isFullBoundsCubeForCulling() && blockState2.method_16386();
							boolean bl2 = blockState.isFullBoundsCubeForCulling() && blockState.method_16386();
							if (!bl && !bl2) {
								return o;
							} else {
								VoxelShape voxelShape = bl ? blockState2.method_11615(blockView2, this.field_16514) : VoxelShapes.empty();
								VoxelShape voxelShape2 = bl2 ? blockState.method_11615(blockView2, this.field_16515) : VoxelShapes.empty();
								return VoxelShapes.method_1080(voxelShape, voxelShape2, direction) ? 16 : o;
							}
						}
					}
				}
			}
		} else {
			return 0;
		}
	}

	@Override
	protected boolean method_15494(long l) {
		return l == -1L;
	}

	@Override
	protected int method_15486(long l, long m, int i) {
		return 0;
	}

	@Override
	public int method_15480(long l) {
		return l == -1L ? 0 : 15 - this.field_15793.method_15537(l);
	}

	protected int method_15517(class_2804 arg, long l) {
		return 15 - arg.method_12139(BlockPos.unpackLongX(l) & 15, BlockPos.unpackLongY(l) & 15, BlockPos.unpackLongZ(l) & 15);
	}

	@Override
	protected void method_15485(long l, int i) {
		this.field_15793.method_15525(l, Math.min(15, 15 - i));
	}

	@Override
	public int method_15488(long l, long m, int i) {
		return 0;
	}

	public boolean method_15518() {
		return this.method_15489() || this.field_15793.method_15489() || this.field_15793.method_15528();
	}

	public int method_15516(int i, boolean bl, boolean bl2) {
		if (!this.field_15794) {
			if (this.field_15793.method_15489()) {
				i = this.field_15793.method_15492(i);
				if (i == 0) {
					return i;
				}
			}

			this.field_15793.method_15527(this, bl, bl2);
		}

		this.field_15794 = true;
		if (this.method_15489()) {
			i = this.method_15492(i);
			if (i == 0) {
				return i;
			}
		}

		this.field_15794 = false;
		this.field_15793.method_15530();
		return i;
	}

	protected void method_15515(int i, int j, int k, class_2804 arg) {
		long l = BlockPos.asLong(i << 4, j << 4, k << 4);
		this.field_15793.method_15532(l, arg);
	}

	@Nullable
	@Override
	public class_2804 method_15544(int i, int j, int k) {
		long l = BlockPos.asLong(i << 4, j << 4, k << 4);
		return this.field_15793.method_15522(l, false);
	}

	@Override
	public int getLightLevel(BlockPos blockPos) {
		return this.field_15793.method_15538(blockPos.asLong());
	}

	@Environment(EnvType.CLIENT)
	public String method_15520(long l) {
		return "" + this.field_15793.method_15480(l);
	}

	public void queueLightCheck(BlockPos blockPos) {
		long l = blockPos.asLong();
		this.queueLightCheck(l);

		for (Direction direction : field_16513) {
			this.queueLightCheck(BlockPos.method_10060(l, direction));
		}
	}

	public void method_15514(BlockPos blockPos, int i) {
	}

	@Override
	public void method_15551(int i, int j, int k, boolean bl) {
		long l = BlockPos.asLong(i << 4, j << 4, k << 4);
		this.field_15793.method_15526(l, bl);
	}

	public void method_15512(int i, int j, boolean bl) {
		long l = BlockPos.method_10065(BlockPos.asLong(i << 4, 0, j << 4));
		this.field_15793.method_15535(l, bl);
	}
}
