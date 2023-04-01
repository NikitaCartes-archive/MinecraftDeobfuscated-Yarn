package net.minecraft;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.EndRodBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;

public class class_8433 {
	private static final int field_44251 = 4;
	private final ServerWorld field_44252;
	private final BlockPos field_44253;
	private final Random field_44254;
	private final int field_44255;
	private final int field_44256;
	private final Map<BlockPos, class_8433.class_8434> field_44257 = new HashMap();
	private final List<class_8433.class_8434> field_44258 = new ArrayList();
	private final List<BlockPos> field_44259 = new ArrayList();
	private final Map<BlockPos, Integer> field_44260 = new HashMap();
	private final Block[] field_44261 = new Block[]{
		Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_OXIDIZED_COPPER
	};
	private final Block[] field_44262 = new Block[]{
		Blocks.WAXED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER
	};

	public class_8433(ServerWorld serverWorld, BlockPos blockPos, Random random, int i, int j) {
		this.field_44252 = serverWorld;
		this.field_44253 = blockPos;
		this.field_44254 = random;
		this.field_44255 = i;
		this.field_44256 = j;
	}

	public void method_50897() {
		if (this.field_44255 == 0) {
			this.method_50911();
		} else {
			this.method_50914();
		}
	}

	private void method_50911() {
		this.field_44258.addAll(this.method_50906(this.field_44252, this.field_44253, this.field_44254, 0, 0, 0));

		while (!this.field_44258.isEmpty()) {
			this.method_50921();
		}

		int i = this.method_50920();

		for (int j = 4; j >= i; j--) {
			Iterator<Entry<BlockPos, Integer>> iterator = this.field_44260.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<BlockPos, Integer> entry = (Entry<BlockPos, Integer>)iterator.next();
				if ((Integer)entry.getValue() == j) {
					this.method_50905(this.field_44252, (class_8433.class_8434)this.field_44257.get(entry.getKey()), this.field_44254, j, false);
					iterator.remove();
				}
			}
		}
	}

	private void method_50914() {
		this.field_44258.addAll(this.method_50906(this.field_44252, this.field_44253, this.field_44254, 0, 0, this.field_44255));
		this.field_44252.getServer().method_51106(this.field_44255, this::method_50916);
	}

	private void method_50916() {
		if (this.field_44258.isEmpty()) {
			this.method_50918();
		} else {
			this.method_50921();
			this.field_44252.getServer().method_51106(this.field_44255, this::method_50916);
		}
	}

	private void method_50918() {
		this.method_50920();
		this.field_44260
			.forEach(
				(blockPos, integer) -> {
					int i = this.field_44255 * (4 - integer) + this.field_44254.nextInt(this.field_44255);
					this.field_44252
						.getServer()
						.method_51106(i, () -> this.method_50905(this.field_44252, (class_8433.class_8434)this.field_44257.get(blockPos), this.field_44254, integer, true));
				}
			);
	}

	private int method_50920() {
		int i = 4;

		for (BlockPos blockPos : this.field_44259) {
			class_8433.class_8434 lv = (class_8433.class_8434)this.field_44257.get(blockPos);
			this.field_44260.put(lv.pos, 4);
			int j = 4;

			while (this.field_44257.containsKey(lv.start)) {
				lv = (class_8433.class_8434)this.field_44257.get(lv.start);
				i = Math.min(i, --j);
				this.field_44260.put(lv.pos, Math.min((Integer)this.field_44260.getOrDefault(lv.pos, j), j));
			}
		}

		return i;
	}

	private void method_50921() {
		List<class_8433.class_8434> list = new ArrayList();

		for (class_8433.class_8434 lv : this.field_44258) {
			List<class_8433.class_8434> list2 = this.method_50906(this.field_44252, lv.pos, this.field_44254, lv.depth, lv.rustLevel, this.field_44255);
			list.addAll(list2);
			if (list2.isEmpty()) {
				this.field_44259.add(lv.pos);
			}

			this.field_44257.put(lv.pos, lv);
		}

		this.field_44258.clear();
		this.field_44258.addAll(list);
	}

	private void method_50905(StructureWorldAccess structureWorldAccess, class_8433.class_8434 arg, Random random, int i, boolean bl) {
		if (i < 0) {
			this.method_50904(structureWorldAccess, arg, random);
			if (bl) {
				structureWorldAccess.toServerWorld()
					.playSound(null, arg.pos, SoundEvents.BLOCK_NETHER_WOOD_PLACE, SoundCategory.BLOCKS, 0.6F, 0.6F + 0.2F * random.nextFloat());
			}
		} else {
			if (bl) {
				structureWorldAccess.toServerWorld().playSound(null, arg.pos, SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 0.6F, 0.5F + 0.2F * random.nextFloat());
			}

			Direction.Axis axis = arg.direction.getAxis();

			for (int j = 0; j < i; j++) {
				int k = random.nextInt(arg.length);
				Direction direction = Util.getRandom(axis.method_51086(), random);
				BlockPos.Mutable mutable = arg.start.offset(arg.direction, k).offset(direction).mutableCopy();

				for (int l = 0; l < i && structureWorldAccess.isAir(mutable); l++) {
					BlockState blockState = Blocks.COPPER_SPLEAVES.getDefaultState();
					this.method_50907(structureWorldAccess, mutable, blockState);
					int m = random.nextInt(6);

					mutable.move(switch (m) {
						case 0, 1, 2 -> arg.direction;
						case 3 -> direction;
						case 4 -> direction.rotateClockwise(axis);
						case 5 -> direction.rotateCounterclockwise(axis);
						default -> throw new IllegalStateException("Unexpected value: " + m);
					});
				}
			}

			if (i == 4) {
				BlockPos blockPos = arg.pos.offset(arg.direction);
				boolean bl2 = structureWorldAccess.getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY() == blockPos.getY();

				BlockState blockState2 = switch (arg.direction) {
					case UP -> bl2 ? Blocks.CHEST.getDefaultState() : Blocks.GREEN_SHULKER_BOX.getDefaultState();
					case DOWN -> {
						switch (random.nextInt(10)) {
							case 0:
								yield Blocks.END_ROD.getDefaultState().with(EndRodBlock.FACING, Direction.DOWN);
							case 1:
							case 2:
								yield Blocks.SOUL_LANTERN.getDefaultState().with(LanternBlock.HANGING, Boolean.valueOf(true));
							default:
								yield Blocks.CHAIN.getDefaultState().with(ChainBlock.AXIS, Direction.Axis.Y);
						}
					}
					default -> Blocks.COPPER_SPLEAVES.getDefaultState();
				};
				this.method_50907(structureWorldAccess, blockPos, blockState2);
				if (structureWorldAccess.getBlockEntity(blockPos) instanceof LootableContainerBlockEntity lootableContainerBlockEntity) {
					if (bl2) {
						LootableContainerBlockEntity.setLootTable(structureWorldAccess, random, blockPos, LootTables.MOON_RESUPLY_CHEST);
						lootableContainerBlockEntity.setCustomName(Text.translatable("block.minecraft.chest.moon"));
					} else {
						LootableContainerBlockEntity.setLootTable(structureWorldAccess, random, blockPos, LootTables.MOON_LAB_CHEST);
						lootableContainerBlockEntity.setCustomName(Text.translatable("block.minecraft.chest.lab"));
					}
				}
			}
		}
	}

	private void method_50904(StructureWorldAccess structureWorldAccess, class_8433.class_8434 arg, Random random) {
		Direction.Axis axis = arg.direction.getAxis();

		for (int i = 0; i < arg.length; i++) {
			for (int j = random.nextInt(3); j < 2; j++) {
				Direction direction = Util.getRandom(axis.method_51086(), random);
				BlockPos blockPos = arg.start.offset(arg.direction, i).offset(direction);
				if (structureWorldAccess.isAir(blockPos)) {
					this.method_50907(
						structureWorldAccess, blockPos, ButtonBlock.method_50854(Blocks.POLISHED_BLACKSTONE_BUTTON.getDefaultState(), direction.getOpposite(), arg.direction)
					);
				}
			}
		}
	}

	private List<class_8433.class_8434> method_50906(StructureWorldAccess structureWorldAccess, BlockPos blockPos, Random random, int i, int j, int k) {
		if (i > this.field_44256) {
			return List.of();
		} else if (j > 100) {
			return List.of();
		} else {
			Map<Direction, Integer> map = new EnumMap(Direction.class);
			List<Direction> list = new ArrayList(this.method_50898(i, random).filter(directionx -> {
				int ix = random.nextInt(directionx == Direction.UP ? 3 : 4) + 3;
				map.put(directionx, ix);

				for (int jxx = 2; jxx < ix + 1; jxx++) {
					for (int kx = -1; kx < 2; kx++) {
						for (int lx = -1; lx < 2; lx++) {
							for (int mx = -1; mx < 2; mx++) {
								if (!structureWorldAccess.getBlockState(blockPos.offset(directionx, jxx).add(kx, lx, mx)).isAir()) {
									return false;
								}
							}
						}
					}
				}

				for (int jx = 3; jx < ix + 1; jx++) {
					for (Direction direction2 : Direction.values()) {
						if (!structureWorldAccess.getBlockState(blockPos.offset(directionx, jx).offset(direction2, 2)).isAir()) {
							return false;
						}
					}
				}

				return true;
			}).toList());
			Util.shuffle(list, random);

			for (Direction direction : list) {
				int l = (Integer)map.get(direction);

				for (int m = 1; m <= l; m++) {
					boolean bl = m == l || random.nextInt() == 1;
					BlockState blockState = this.method_50899(j + m, random, bl, direction);
					if (k > 0) {
						structureWorldAccess.spawnEntity(
							new DisplayEntity.class_8398(
								structureWorldAccess.toServerWorld(), blockState, blockPos.offset(direction, m), new DisplayEntity.class_8397(blockPos, 0.99F / (float)m, k)
							)
						);
					} else {
						this.method_50907(structureWorldAccess, blockPos.offset(direction, m), blockState);
					}
				}
			}

			if (list.isEmpty()) {
				return List.of();
			} else {
				if (k > 0) {
					structureWorldAccess.toServerWorld()
						.playSound(null, blockPos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 0.3F, 0.3F + 0.3F * random.nextFloat());
					structureWorldAccess.toServerWorld()
						.playSound(null, blockPos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 0.8F, 1.5F + 0.5F * random.nextFloat());
				}

				return (List<class_8433.class_8434>)list.stream()
					.map(
						directionx -> new class_8433.class_8434(
								blockPos, blockPos.offset(directionx, (Integer)map.get(directionx)), (Integer)map.get(directionx), i + 1, j + (Integer)map.get(directionx), directionx
							)
					)
					.collect(Collectors.toList());
			}
		}
	}

	private void method_50907(StructureWorldAccess structureWorldAccess, BlockPos blockPos, BlockState blockState) {
		if (structureWorldAccess.getBlockState(blockPos).isAir()) {
			structureWorldAccess.setBlockState(blockPos, blockState, Block.NOTIFY_ALL);
		}
	}

	private BlockState method_50899(int i, Random random, boolean bl, Direction direction) {
		int j = i + random.nextInt(20) - random.nextInt(20);
		Block[] blocks = this.field_44262;
		Block[] blocks2 = this.field_44261;
		BlockState blockState;
		if (j < 40) {
			blockState = (bl ? blocks2[0] : blocks[0]).getDefaultState();
		} else if (j < 60) {
			blockState = (bl ? blocks2[1] : blocks[1]).getDefaultState();
		} else if (j < 80) {
			blockState = (bl ? blocks2[2] : blocks[2]).getDefaultState();
		} else {
			blockState = (bl ? blocks2[3] : blocks[3]).getDefaultState();
		}

		if (blockState.contains(PillarBlock.AXIS)) {
			blockState = blockState.with(PillarBlock.AXIS, direction.getAxis());
		}

		return blockState;
	}

	private Stream<Direction> method_50898(int i, Random random) {
		List<Direction> list = new ArrayList();
		float f = i < 4 ? 1.0F : 2.0F / (float)(i + 1);
		float g = (float)Math.sin((double)i * Math.PI / 20.0);
		float h = (float)(i - 10) / 5.0F;
		if (random.nextFloat() < f) {
			list.add(Direction.UP);
		}

		if (random.nextFloat() < g) {
			list.add(Direction.NORTH);
		}

		if (random.nextFloat() < g) {
			list.add(Direction.SOUTH);
		}

		if (random.nextFloat() < g) {
			list.add(Direction.EAST);
		}

		if (random.nextFloat() < g) {
			list.add(Direction.WEST);
		}

		if (random.nextFloat() < h) {
			list.add(Direction.DOWN);
		}

		return list.stream();
	}

	public static DisplayEntity.class_8397[] method_50908(BlockPos blockPos) {
		return new DisplayEntity.class_8397[]{
			new DisplayEntity.class_8397(blockPos.up(50), 2.2F, 50),
			new DisplayEntity.class_8397(blockPos.up(3), 1.0F, 45),
			new DisplayEntity.class_8397(blockPos.up(4), 1.5F, 5)
		};
	}

	public static void method_50900(ServerWorld serverWorld, BlockPos blockPos) {
		serverWorld.createExplosion(null, (double)blockPos.getX(), (double)blockPos.getY() + 50.0, (double)blockPos.getZ(), 15.0F, World.ExplosionSourceType.NONE);
		Direction[] directions = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

		for (Direction direction : directions) {
			serverWorld.spawnEntity(
				new DisplayEntity.class_8398(serverWorld, Blocks.RAW_COPPER_BLOCK.getDefaultState(), blockPos.offset(direction), method_50908(blockPos.offset(direction)))
			);
		}

		serverWorld.spawnEntity(new DisplayEntity.class_8398(serverWorld, Blocks.RAW_COPPER_BLOCK.getDefaultState(), blockPos, method_50908(blockPos)));

		for (Direction direction : directions) {
			serverWorld.spawnEntity(
				new DisplayEntity.class_8398(
					serverWorld,
					Blocks.WAXED_CUT_COPPER_STAIRS.getDefaultState().with(StairsBlock.FACING, direction.getOpposite()),
					blockPos.up().offset(direction),
					method_50908(blockPos.up().offset(direction))
				)
			);
		}

		serverWorld.spawnEntity(new DisplayEntity.class_8398(serverWorld, Blocks.RAW_COPPER_BLOCK.getDefaultState(), blockPos.up(), method_50908(blockPos.up())));
		serverWorld.spawnEntity(
			new DisplayEntity.class_8398(serverWorld, Blocks.WAXED_COPPER_BLOCK.getDefaultState(), blockPos.up().up(), method_50908(blockPos.up().up()))
		);
		serverWorld.getServer()
			.method_51106(
				50,
				() -> {
					for (int i = 0; i < 40; i++) {
						serverWorld.getServer()
							.method_51106(
								i,
								() -> {
									serverWorld.playSound(
										null,
										(double)blockPos.getX(),
										(double)blockPos.getY() + 3.0,
										(double)blockPos.getZ(),
										SoundEvents.AMBIENT_BASALT_DELTAS_MOOD.value(),
										SoundCategory.BLOCKS,
										0.2F,
										1.0F
									);
									serverWorld.spawnParticles(
										ParticleTypes.SMOKE, (double)blockPos.getX(), (double)blockPos.getY() + 3.0, (double)blockPos.getZ(), 100, 1.0, 0.5, 1.0, 0.3
									);
								}
							);
					}
				}
			);
		serverWorld.getServer()
			.method_51106(
				95,
				() -> {
					for (int i = 0; i < 5; i++) {
						int j = i;
						serverWorld.getServer()
							.method_51106(
								i,
								() -> {
									serverWorld.playSound(
										null,
										(double)blockPos.getX(),
										(double)blockPos.getY() + 3.0 + (double)j,
										(double)blockPos.getZ(),
										SoundEvents.ITEM_FIRECHARGE_USE,
										SoundCategory.BLOCKS,
										0.4F,
										1.4F
									);
									serverWorld.spawnParticles(
										ParticleTypes.FLAME, (double)blockPos.getX(), (double)blockPos.getY() + 3.0 + (double)j, (double)blockPos.getZ(), 100, 0.7, 0.5, 0.7, 0.0
									);
								}
							);
					}
				}
			);
		serverWorld.getServer()
			.method_51106(
				100,
				() -> {
					serverWorld.playSound(
						null,
						(double)blockPos.getX(),
						(double)blockPos.getY() + 3.0,
						(double)blockPos.getZ(),
						SoundEvents.BLOCK_BEACON_POWER_SELECT,
						SoundCategory.BLOCKS,
						1.0F,
						1.4F
					);

					for (Direction directionx : directions) {
						serverWorld.spawnEntity(
							new DisplayEntity.class_8398(
								serverWorld,
								Blocks.LIGHTNING_ROD.getDefaultState().with(LightningRodBlock.FACING, directionx.getOpposite()),
								blockPos.up().up().offset(directionx),
								new DisplayEntity.class_8397(blockPos.up(2), 1.0F, 100)
							)
						);
					}

					BlockState blockState = Blocks.POLISHED_BASALT.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Y);
					BlockState blockState2 = Blocks.CHAIN.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Y);
					serverWorld.setBlockState(blockPos.north().east(), blockState, Block.NOTIFY_ALL);
					serverWorld.setBlockState(blockPos.north().east().up(), blockState2, Block.NOTIFY_ALL);
					serverWorld.setBlockState(blockPos.north().west(), blockState, Block.NOTIFY_ALL);
					serverWorld.setBlockState(blockPos.south().east(), blockState, Block.NOTIFY_ALL);
					serverWorld.setBlockState(blockPos.south().west().up(), blockState2, Block.NOTIFY_ALL);
					serverWorld.setBlockState(blockPos.south().west(), blockState, Block.NOTIFY_ALL);
				}
			);
		serverWorld.getServer().method_51106(200, () -> {
			ChunkRandom chunkRandom = new ChunkRandom(serverWorld.random);
			chunkRandom.setCarverSeed(serverWorld.getSeed(), blockPos.getX() >> 4, blockPos.getZ() >> 4);
			class_8433 lv = new class_8433(serverWorld, blockPos.up().up(), chunkRandom, 20, 20);
			lv.method_50897();
		});
	}

	static record class_8434(BlockPos start, BlockPos pos, int length, int depth, int rustLevel, Direction direction) {
	}
}
