package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.structure.processor.BlackstoneReplacementStructureProcessor;
import net.minecraft.structure.processor.BlockAgeStructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.LavaSubmergedBlockStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RuinedPortalStructurePiece extends SimpleStructurePiece {
	private static final Logger field_24992 = LogManager.getLogger();
	private static final float field_31620 = 0.3F;
	private static final float field_31621 = 0.07F;
	private static final float field_31622 = 0.2F;
	private static final float field_31623 = 0.2F;
	private final RuinedPortalStructurePiece.VerticalPlacement verticalPlacement;
	private final RuinedPortalStructurePiece.Properties properties;

	public RuinedPortalStructurePiece(
		StructureManager structureManager,
		BlockPos blockPos,
		RuinedPortalStructurePiece.VerticalPlacement verticalPlacement,
		RuinedPortalStructurePiece.Properties properties,
		Identifier identifier,
		Structure structure,
		BlockRotation blockRotation,
		BlockMirror blockMirror,
		BlockPos blockPos2
	) {
		super(
			StructurePieceType.RUINED_PORTAL,
			0,
			structureManager,
			identifier,
			identifier.toString(),
			method_35450(blockMirror, blockRotation, verticalPlacement, blockPos2, properties),
			blockPos
		);
		this.verticalPlacement = verticalPlacement;
		this.properties = properties;
	}

	public RuinedPortalStructurePiece(ServerWorld world, NbtCompound nbt) {
		super(StructurePieceType.RUINED_PORTAL, nbt, world, identifier -> method_35449(world, nbt, identifier));
		this.verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.getFromId(nbt.getString("VerticalPlacement"));
		this.properties = RuinedPortalStructurePiece.Properties.CODEC
			.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("Properties")))
			.getOrThrow(true, field_24992::error);
	}

	@Override
	protected void writeNbt(ServerWorld world, NbtCompound nbt) {
		super.writeNbt(world, nbt);
		nbt.putString("Rotation", this.placementData.getRotation().name());
		nbt.putString("Mirror", this.placementData.getMirror().name());
		nbt.putString("VerticalPlacement", this.verticalPlacement.getId());
		RuinedPortalStructurePiece.Properties.CODEC
			.encodeStart(NbtOps.INSTANCE, this.properties)
			.resultOrPartial(field_24992::error)
			.ifPresent(nbtElement -> nbt.put("Properties", nbtElement));
	}

	private static StructurePlacementData method_35449(ServerWorld world, NbtCompound nbt, Identifier id) {
		Structure structure = world.getStructureManager().getStructureOrBlank(id);
		BlockPos blockPos = new BlockPos(structure.getSize().getX() / 2, 0, structure.getSize().getZ() / 2);
		return method_35450(
			BlockMirror.valueOf(nbt.getString("Mirror")),
			BlockRotation.valueOf(nbt.getString("Rotation")),
			RuinedPortalStructurePiece.VerticalPlacement.getFromId(nbt.getString("VerticalPlacement")),
			blockPos,
			RuinedPortalStructurePiece.Properties.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("Properties"))).getOrThrow(true, field_24992::error)
		);
	}

	private static StructurePlacementData method_35450(
		BlockMirror blockMirror,
		BlockRotation blockRotation,
		RuinedPortalStructurePiece.VerticalPlacement verticalPlacement,
		BlockPos blockPos,
		RuinedPortalStructurePiece.Properties properties
	) {
		BlockIgnoreStructureProcessor blockIgnoreStructureProcessor = properties.airPocket
			? BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS
			: BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS;
		List<StructureProcessorRule> list = Lists.<StructureProcessorRule>newArrayList();
		list.add(createReplacementRule(Blocks.GOLD_BLOCK, 0.3F, Blocks.AIR));
		list.add(createLavaReplacementRule(verticalPlacement, properties));
		if (!properties.cold) {
			list.add(createReplacementRule(Blocks.NETHERRACK, 0.07F, Blocks.MAGMA_BLOCK));
		}

		StructurePlacementData structurePlacementData = new StructurePlacementData()
			.setRotation(blockRotation)
			.setMirror(blockMirror)
			.setPosition(blockPos)
			.addProcessor(blockIgnoreStructureProcessor)
			.addProcessor(new RuleStructureProcessor(list))
			.addProcessor(new BlockAgeStructureProcessor(properties.mossiness))
			.addProcessor(new LavaSubmergedBlockStructureProcessor());
		if (properties.replaceWithBlackstone) {
			structurePlacementData.addProcessor(BlackstoneReplacementStructureProcessor.INSTANCE);
		}

		return structurePlacementData;
	}

	private static StructureProcessorRule createLavaReplacementRule(
		RuinedPortalStructurePiece.VerticalPlacement verticalPlacement, RuinedPortalStructurePiece.Properties properties
	) {
		if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR) {
			return createReplacementRule(Blocks.LAVA, Blocks.MAGMA_BLOCK);
		} else {
			return properties.cold ? createReplacementRule(Blocks.LAVA, Blocks.NETHERRACK) : createReplacementRule(Blocks.LAVA, 0.2F, Blocks.MAGMA_BLOCK);
		}
	}

	@Override
	public boolean generate(
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox boundingBox,
		ChunkPos chunkPos,
		BlockPos pos
	) {
		if (!boundingBox.contains(this.pos)) {
			return true;
		} else {
			boundingBox.intersection(this.structure.calculateBoundingBox(this.placementData, this.pos));
			boolean bl = super.generate(world, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, pos);
			this.placeNetherrackBase(random, world);
			this.updateNetherracksInBound(random, world);
			if (this.properties.vines || this.properties.overgrown) {
				BlockPos.stream(this.getBoundingBox()).forEach(blockPos -> {
					if (this.properties.vines) {
						this.generateVines(random, world, blockPos);
					}

					if (this.properties.overgrown) {
						this.generateOvergrownLeaves(random, world, blockPos);
					}
				});
			}

			return bl;
		}
	}

	@Override
	protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
	}

	private void generateVines(Random random, WorldAccess world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (!blockState.isAir() && !blockState.isOf(Blocks.VINE)) {
			Direction direction = method_35457(random);
			BlockPos blockPos = pos.offset(direction);
			BlockState blockState2 = world.getBlockState(blockPos);
			if (blockState2.isAir()) {
				if (Block.isFaceFullSquare(blockState.getCollisionShape(world, pos), direction)) {
					BooleanProperty booleanProperty = VineBlock.getFacingProperty(direction.getOpposite());
					world.setBlockState(blockPos, Blocks.VINE.getDefaultState().with(booleanProperty, Boolean.valueOf(true)), Block.NOTIFY_ALL);
				}
			}
		}
	}

	private void generateOvergrownLeaves(Random random, WorldAccess world, BlockPos pos) {
		if (random.nextFloat() < 0.5F && world.getBlockState(pos).isOf(Blocks.NETHERRACK) && world.getBlockState(pos.up()).isAir()) {
			world.setBlockState(pos.up(), Blocks.JUNGLE_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, Boolean.valueOf(true)), Block.NOTIFY_ALL);
		}
	}

	private void updateNetherracksInBound(Random random, WorldAccess world) {
		for (int i = this.boundingBox.getMinX() + 1; i < this.boundingBox.getMaxX(); i++) {
			for (int j = this.boundingBox.getMinZ() + 1; j < this.boundingBox.getMaxZ(); j++) {
				BlockPos blockPos = new BlockPos(i, this.boundingBox.getMinY(), j);
				if (world.getBlockState(blockPos).isOf(Blocks.NETHERRACK)) {
					this.updateNetherracks(random, world, blockPos.down());
				}
			}
		}
	}

	private void updateNetherracks(Random random, WorldAccess world, BlockPos pos) {
		BlockPos.Mutable mutable = pos.mutableCopy();
		this.placeNetherrackBottom(random, world, mutable);
		int i = 8;

		while (i > 0 && random.nextFloat() < 0.5F) {
			mutable.move(Direction.DOWN);
			i--;
			this.placeNetherrackBottom(random, world, mutable);
		}
	}

	private void placeNetherrackBase(Random random, WorldAccess world) {
		boolean bl = this.verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE
			|| this.verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR;
		BlockPos blockPos = this.boundingBox.getCenter();
		int i = blockPos.getX();
		int j = blockPos.getZ();
		float[] fs = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.9F, 0.9F, 0.8F, 0.7F, 0.6F, 0.4F, 0.2F};
		int k = fs.length;
		int l = (this.boundingBox.getBlockCountX() + this.boundingBox.getBlockCountZ()) / 2;
		int m = random.nextInt(Math.max(1, 8 - l / 2));
		int n = 3;
		BlockPos.Mutable mutable = BlockPos.ORIGIN.mutableCopy();

		for (int o = i - k; o <= i + k; o++) {
			for (int p = j - k; p <= j + k; p++) {
				int q = Math.abs(o - i) + Math.abs(p - j);
				int r = Math.max(0, q + m);
				if (r < k) {
					float f = fs[r];
					if (random.nextDouble() < (double)f) {
						int s = getBaseHeight(world, o, p, this.verticalPlacement);
						int t = bl ? s : Math.min(this.boundingBox.getMinY(), s);
						mutable.set(o, t, p);
						if (Math.abs(t - this.boundingBox.getMinY()) <= 3 && this.canFillNetherrack(world, mutable)) {
							this.placeNetherrackBottom(random, world, mutable);
							if (this.properties.overgrown) {
								this.generateOvergrownLeaves(random, world, mutable);
							}

							this.updateNetherracks(random, world, mutable.down());
						}
					}
				}
			}
		}
	}

	private boolean canFillNetherrack(WorldAccess world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return !blockState.isOf(Blocks.AIR)
			&& !blockState.isOf(Blocks.OBSIDIAN)
			&& !blockState.isOf(Blocks.CHEST)
			&& (this.verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER || !blockState.isOf(Blocks.LAVA));
	}

	private void placeNetherrackBottom(Random random, WorldAccess world, BlockPos pos) {
		if (!this.properties.cold && random.nextFloat() < 0.07F) {
			world.setBlockState(pos, Blocks.MAGMA_BLOCK.getDefaultState(), Block.NOTIFY_ALL);
		} else {
			world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), Block.NOTIFY_ALL);
		}
	}

	private static int getBaseHeight(WorldAccess world, int x, int y, RuinedPortalStructurePiece.VerticalPlacement verticalPlacement) {
		return world.getTopY(getHeightmapType(verticalPlacement), x, y) - 1;
	}

	public static Heightmap.Type getHeightmapType(RuinedPortalStructurePiece.VerticalPlacement verticalPlacement) {
		return verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG;
	}

	private static StructureProcessorRule createReplacementRule(Block old, float chance, Block updated) {
		return new StructureProcessorRule(new RandomBlockMatchRuleTest(old, chance), AlwaysTrueRuleTest.INSTANCE, updated.getDefaultState());
	}

	private static StructureProcessorRule createReplacementRule(Block old, Block updated) {
		return new StructureProcessorRule(new BlockMatchRuleTest(old), AlwaysTrueRuleTest.INSTANCE, updated.getDefaultState());
	}

	public static class Properties {
		public static final Codec<RuinedPortalStructurePiece.Properties> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.BOOL.fieldOf("cold").forGetter(properties -> properties.cold),
						Codec.FLOAT.fieldOf("mossiness").forGetter(properties -> properties.mossiness),
						Codec.BOOL.fieldOf("air_pocket").forGetter(properties -> properties.airPocket),
						Codec.BOOL.fieldOf("overgrown").forGetter(properties -> properties.overgrown),
						Codec.BOOL.fieldOf("vines").forGetter(properties -> properties.vines),
						Codec.BOOL.fieldOf("replace_with_blackstone").forGetter(properties -> properties.replaceWithBlackstone)
					)
					.apply(instance, RuinedPortalStructurePiece.Properties::new)
		);
		public boolean cold;
		public float mossiness = 0.2F;
		public boolean airPocket;
		public boolean overgrown;
		public boolean vines;
		public boolean replaceWithBlackstone;

		public Properties() {
		}

		public <T> Properties(boolean cold, float mossiness, boolean airPocket, boolean overgrown, boolean vines, boolean replaceWithBlackstone) {
			this.cold = cold;
			this.mossiness = mossiness;
			this.airPocket = airPocket;
			this.overgrown = overgrown;
			this.vines = vines;
			this.replaceWithBlackstone = replaceWithBlackstone;
		}
	}

	public static enum VerticalPlacement {
		ON_LAND_SURFACE("on_land_surface"),
		PARTLY_BURIED("partly_buried"),
		ON_OCEAN_FLOOR("on_ocean_floor"),
		IN_MOUNTAIN("in_mountain"),
		UNDERGROUND("underground"),
		IN_NETHER("in_nether");

		private static final Map<String, RuinedPortalStructurePiece.VerticalPlacement> VERTICAL_PLACEMENTS = (Map<String, RuinedPortalStructurePiece.VerticalPlacement>)Arrays.stream(
				values()
			)
			.collect(Collectors.toMap(RuinedPortalStructurePiece.VerticalPlacement::getId, verticalPlacement -> verticalPlacement));
		private final String id;

		private VerticalPlacement(String id) {
			this.id = id;
		}

		public String getId() {
			return this.id;
		}

		public static RuinedPortalStructurePiece.VerticalPlacement getFromId(String id) {
			return (RuinedPortalStructurePiece.VerticalPlacement)VERTICAL_PLACEMENTS.get(id);
		}
	}
}
