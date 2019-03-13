package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.TheEndDimension;

public class FireBlock extends Block {
	public static final IntegerProperty field_11092 = Properties.field_12498;
	public static final BooleanProperty field_11096 = ConnectedPlantBlock.field_11332;
	public static final BooleanProperty field_11094 = ConnectedPlantBlock.field_11335;
	public static final BooleanProperty field_11089 = ConnectedPlantBlock.field_11331;
	public static final BooleanProperty field_11088 = ConnectedPlantBlock.field_11328;
	public static final BooleanProperty field_11093 = ConnectedPlantBlock.field_11327;
	private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES = (Map<Direction, BooleanProperty>)ConnectedPlantBlock.FACING_PROPERTIES
		.entrySet()
		.stream()
		.filter(entry -> entry.getKey() != Direction.DOWN)
		.collect(SystemUtil.toMap());
	private final Object2IntMap<Block> burnChances = new Object2IntOpenHashMap<>();
	private final Object2IntMap<Block> spreadChances = new Object2IntOpenHashMap<>();

	protected FireBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11092, Integer.valueOf(0))
				.method_11657(field_11096, Boolean.valueOf(false))
				.method_11657(field_11094, Boolean.valueOf(false))
				.method_11657(field_11089, Boolean.valueOf(false))
				.method_11657(field_11088, Boolean.valueOf(false))
				.method_11657(field_11093, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return VoxelShapes.method_1073();
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return this.method_9558(blockState, iWorld, blockPos)
			? this.method_10198(iWorld, blockPos).method_11657(field_11092, blockState.method_11654(field_11092))
			: Blocks.field_10124.method_9564();
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_10198(itemPlacementContext.method_8045(), itemPlacementContext.method_8037());
	}

	public BlockState method_10198(BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState = blockView.method_8320(blockPos2);
		if (!blockState.method_11631(blockView, blockPos2) && !this.method_10195(blockState)) {
			BlockState blockState2 = this.method_9564();

			for (Direction direction : Direction.values()) {
				BooleanProperty booleanProperty = (BooleanProperty)DIRECTION_PROPERTIES.get(direction);
				if (booleanProperty != null) {
					blockState2 = blockState2.method_11657(booleanProperty, Boolean.valueOf(this.method_10195(blockView.method_8320(blockPos.method_10093(direction)))));
				}
			}

			return blockState2;
		} else {
			return this.method_9564();
		}
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		return viewableWorld.method_8320(blockPos2).method_11631(viewableWorld, blockPos2) || this.method_10193(viewableWorld, blockPos);
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 30;
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (world.getGameRules().getBoolean("doFireTick")) {
			if (!blockState.method_11591(world, blockPos)) {
				world.method_8650(blockPos);
			}

			Block block = world.method_8320(blockPos.down()).getBlock();
			boolean bl = world.field_9247 instanceof TheEndDimension && block == Blocks.field_9987 || block == Blocks.field_10515 || block == Blocks.field_10092;
			int i = (Integer)blockState.method_11654(field_11092);
			if (!bl && world.isRaining() && this.method_10192(world, blockPos) && random.nextFloat() < 0.2F + (float)i * 0.03F) {
				world.method_8650(blockPos);
			} else {
				int j = Math.min(15, i + random.nextInt(3) / 2);
				if (i != j) {
					blockState = blockState.method_11657(field_11092, Integer.valueOf(j));
					world.method_8652(blockPos, blockState, 4);
				}

				if (!bl) {
					world.method_8397().method_8676(blockPos, this, this.getTickRate(world) + random.nextInt(10));
					if (!this.method_10193(world, blockPos)) {
						BlockPos blockPos2 = blockPos.down();
						if (!world.method_8320(blockPos2).method_11631(world, blockPos2) || i > 3) {
							world.method_8650(blockPos);
						}

						return;
					}

					if (i == 15 && random.nextInt(4) == 0 && !this.method_10195(world.method_8320(blockPos.down()))) {
						world.method_8650(blockPos);
						return;
					}
				}

				boolean bl2 = world.method_8480(blockPos);
				int k = bl2 ? -50 : 0;
				this.method_10196(world, blockPos.east(), 300 + k, random, i);
				this.method_10196(world, blockPos.west(), 300 + k, random, i);
				this.method_10196(world, blockPos.down(), 250 + k, random, i);
				this.method_10196(world, blockPos.up(), 250 + k, random, i);
				this.method_10196(world, blockPos.north(), 300 + k, random, i);
				this.method_10196(world, blockPos.south(), 300 + k, random, i);
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 4; n++) {
							if (l != 0 || n != 0 || m != 0) {
								int o = 100;
								if (n > 1) {
									o += (n - 1) * 100;
								}

								mutable.method_10101(blockPos).setOffset(l, n, m);
								int p = this.method_10194(world, mutable);
								if (p > 0) {
									int q = (p + 40 + world.getDifficulty().getId() * 7) / (i + 30);
									if (bl2) {
										q /= 2;
									}

									if (q > 0 && random.nextInt(o) <= q && (!world.isRaining() || !this.method_10192(world, mutable))) {
										int r = Math.min(15, i + random.nextInt(5) / 4);
										world.method_8652(mutable, this.method_10198(world, mutable).method_11657(field_11092, Integer.valueOf(r)), 3);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean method_10192(World world, BlockPos blockPos) {
		return world.method_8520(blockPos)
			|| world.method_8520(blockPos.west())
			|| world.method_8520(blockPos.east())
			|| world.method_8520(blockPos.north())
			|| world.method_8520(blockPos.south());
	}

	private int method_10190(BlockState blockState) {
		return blockState.method_11570(Properties.field_12508) && blockState.method_11654(Properties.field_12508)
			? 0
			: this.spreadChances.getInt(blockState.getBlock());
	}

	private int method_10191(BlockState blockState) {
		return blockState.method_11570(Properties.field_12508) && blockState.method_11654(Properties.field_12508)
			? 0
			: this.burnChances.getInt(blockState.getBlock());
	}

	private void method_10196(World world, BlockPos blockPos, int i, Random random, int j) {
		int k = this.method_10190(world.method_8320(blockPos));
		if (random.nextInt(i) < k) {
			BlockState blockState = world.method_8320(blockPos);
			if (random.nextInt(j + 10) < 5 && !world.method_8520(blockPos)) {
				int l = Math.min(j + random.nextInt(5) / 4, 15);
				world.method_8652(blockPos, this.method_10198(world, blockPos).method_11657(field_11092, Integer.valueOf(l)), 3);
			} else {
				world.method_8650(blockPos);
			}

			Block block = blockState.getBlock();
			if (block instanceof TntBlock) {
				TntBlock.method_10738(world, blockPos);
			}
		}
	}

	private boolean method_10193(BlockView blockView, BlockPos blockPos) {
		for (Direction direction : Direction.values()) {
			if (this.method_10195(blockView.method_8320(blockPos.method_10093(direction)))) {
				return true;
			}
		}

		return false;
	}

	private int method_10194(ViewableWorld viewableWorld, BlockPos blockPos) {
		if (!viewableWorld.method_8623(blockPos)) {
			return 0;
		} else {
			int i = 0;

			for (Direction direction : Direction.values()) {
				BlockState blockState = viewableWorld.method_8320(blockPos.method_10093(direction));
				i = Math.max(this.method_10191(blockState), i);
			}

			return i;
		}
	}

	public boolean method_10195(BlockState blockState) {
		return this.method_10191(blockState) > 0;
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			if (world.field_9247.method_12460() != DimensionType.field_13072 && world.field_9247.method_12460() != DimensionType.field_13076
				|| !((PortalBlock)Blocks.field_10316).method_10352(world, blockPos)) {
				if (!blockState.method_11591(world, blockPos)) {
					world.method_8650(blockPos);
				} else {
					world.method_8397().method_8676(blockPos, this, this.getTickRate(world) + world.random.nextInt(10));
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (random.nextInt(24) == 0) {
			world.method_8486(
				(double)((float)blockPos.getX() + 0.5F),
				(double)((float)blockPos.getY() + 0.5F),
				(double)((float)blockPos.getZ() + 0.5F),
				SoundEvents.field_14993,
				SoundCategory.field_15245,
				1.0F + random.nextFloat(),
				random.nextFloat() * 0.7F + 0.3F,
				false
			);
		}

		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = world.method_8320(blockPos2);
		if (!blockState2.method_11631(world, blockPos2) && !this.method_10195(blockState2)) {
			if (this.method_10195(world.method_8320(blockPos.west()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)blockPos.getX() + random.nextDouble() * 0.1F;
					double e = (double)blockPos.getY() + random.nextDouble();
					double f = (double)blockPos.getZ() + random.nextDouble();
					world.method_8406(ParticleTypes.field_11237, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.method_10195(world.method_8320(blockPos.east()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)(blockPos.getX() + 1) - random.nextDouble() * 0.1F;
					double e = (double)blockPos.getY() + random.nextDouble();
					double f = (double)blockPos.getZ() + random.nextDouble();
					world.method_8406(ParticleTypes.field_11237, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.method_10195(world.method_8320(blockPos.north()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)blockPos.getX() + random.nextDouble();
					double e = (double)blockPos.getY() + random.nextDouble();
					double f = (double)blockPos.getZ() + random.nextDouble() * 0.1F;
					world.method_8406(ParticleTypes.field_11237, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.method_10195(world.method_8320(blockPos.south()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)blockPos.getX() + random.nextDouble();
					double e = (double)blockPos.getY() + random.nextDouble();
					double f = (double)(blockPos.getZ() + 1) - random.nextDouble() * 0.1F;
					world.method_8406(ParticleTypes.field_11237, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.method_10195(world.method_8320(blockPos.up()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)blockPos.getX() + random.nextDouble();
					double e = (double)(blockPos.getY() + 1) - random.nextDouble() * 0.1F;
					double f = (double)blockPos.getZ() + random.nextDouble();
					world.method_8406(ParticleTypes.field_11237, d, e, f, 0.0, 0.0, 0.0);
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				double d = (double)blockPos.getX() + random.nextDouble();
				double e = (double)blockPos.getY() + random.nextDouble() * 0.5 + 0.5;
				double f = (double)blockPos.getZ() + random.nextDouble();
				world.method_8406(ParticleTypes.field_11237, d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11092, field_11096, field_11094, field_11089, field_11088, field_11093);
	}

	public void registerFlammableBlock(Block block, int i, int j) {
		this.burnChances.put(block, i);
		this.spreadChances.put(block, j);
	}

	public static void registerDefaultFlammables() {
		FireBlock fireBlock = (FireBlock)Blocks.field_10036;
		fireBlock.registerFlammableBlock(Blocks.field_10161, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_9975, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10148, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10334, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10218, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10075, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10119, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10071, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10257, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10617, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10031, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10500, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10188, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10291, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10513, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10041, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10196, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10457, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10620, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10020, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10299, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10319, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10132, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10144, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10563, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10408, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10569, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10122, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10256, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10616, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10431, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10037, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10511, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10306, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10533, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10010, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10519, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10436, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10366, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10254, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10622, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10244, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10250, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10558, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10204, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10084, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10103, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10374, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10126, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10155, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10307, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10303, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_9999, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10178, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10503, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_9988, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10539, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10335, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10098, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10035, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10504, 30, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10375, 15, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10479, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10112, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10428, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10583, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10378, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10430, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10003, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10214, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10313, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10182, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10449, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10086, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10226, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10573, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10270, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10048, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10156, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10315, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10554, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_9995, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10548, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10606, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10446, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10095, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10215, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10294, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10490, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10028, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10459, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10423, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10222, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10619, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10259, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10514, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10113, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10170, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10314, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10146, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10597, 15, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10381, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10359, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10466, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_9977, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10482, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10290, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10512, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10040, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10393, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10591, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10209, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10433, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10510, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10043, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10473, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10338, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10536, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10106, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10342, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10211, 60, 60);
		fireBlock.registerFlammableBlock(Blocks.field_16492, 60, 60);
		fireBlock.registerFlammableBlock(Blocks.field_16330, 30, 20);
		fireBlock.registerFlammableBlock(Blocks.field_17563, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_16999, 60, 100);
	}
}
