package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
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
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlackstoneReplacementStructureProcessor;
import net.minecraft.structure.processor.BlockAgeStructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RuinedPortalFeaturePiece extends SimpleStructurePiece {
	private final Identifier template;
	private final BlockRotation rotation;
	private final BlockMirror mirror;
	private final RuinedPortalFeaturePiece.VerticalPlacement verticalPlacement;
	private final RuinedPortalFeaturePiece.Properties properties;

	public RuinedPortalFeaturePiece(
		BlockPos pos,
		RuinedPortalFeaturePiece.VerticalPlacement verticalPlacement,
		RuinedPortalFeaturePiece.Properties properties,
		Identifier template,
		Structure structure,
		BlockRotation rotation,
		BlockMirror mirror,
		BlockPos center
	) {
		super(StructurePieceType.RUINED_PORTAL, 0);
		this.pos = pos;
		this.template = template;
		this.rotation = rotation;
		this.mirror = mirror;
		this.verticalPlacement = verticalPlacement;
		this.properties = properties;
		this.processProperties(structure, center);
	}

	public RuinedPortalFeaturePiece(StructureManager manager, CompoundTag tag) {
		super(StructurePieceType.RUINED_PORTAL, tag);
		this.template = new Identifier(tag.getString("Template"));
		this.rotation = BlockRotation.valueOf(tag.getString("Rotation"));
		this.mirror = BlockMirror.valueOf(tag.getString("Mirror"));
		this.verticalPlacement = RuinedPortalFeaturePiece.VerticalPlacement.getFromId(tag.getString("VerticalPlacement"));
		this.properties = new RuinedPortalFeaturePiece.Properties(new Dynamic<>(NbtOps.INSTANCE, tag.get("Properties")));
		Structure structure = manager.getStructureOrBlank(this.template);
		this.processProperties(structure, new BlockPos(structure.getSize().getX() / 2, 0, structure.getSize().getZ() / 2));
	}

	@Override
	protected void toNbt(CompoundTag tag) {
		super.toNbt(tag);
		tag.putString("Template", this.template.toString());
		tag.putString("Rotation", this.rotation.name());
		tag.putString("Mirror", this.mirror.name());
		tag.putString("VerticalPlacement", this.verticalPlacement.getId());
		tag.put("Properties", this.properties.serialize(NbtOps.INSTANCE));
	}

	private void processProperties(Structure structure, BlockPos center) {
		BlockIgnoreStructureProcessor blockIgnoreStructureProcessor = this.properties.airPocket
			? BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS
			: BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS;
		List<StructureProcessorRule> list = Lists.<StructureProcessorRule>newArrayList();
		list.add(createReplacementRule(Blocks.GOLD_BLOCK, 0.3F, Blocks.AIR));
		list.add(this.createLavaReplacementRule());
		if (!this.properties.cold) {
			list.add(createReplacementRule(Blocks.NETHERRACK, 0.07F, Blocks.MAGMA_BLOCK));
		}

		StructurePlacementData structurePlacementData = new StructurePlacementData()
			.setRotation(this.rotation)
			.setMirror(this.mirror)
			.setPosition(center)
			.addProcessor(blockIgnoreStructureProcessor)
			.addProcessor(new RuleStructureProcessor(list))
			.addProcessor(new BlockAgeStructureProcessor(this.properties.mossiness));
		if (this.properties.replaceWithBlackstone) {
			structurePlacementData.addProcessor(new BlackstoneReplacementStructureProcessor());
		}

		this.setStructureData(structure, this.pos, structurePlacementData);
	}

	private StructureProcessorRule createLavaReplacementRule() {
		if (this.verticalPlacement == RuinedPortalFeaturePiece.VerticalPlacement.ON_OCEAN_FLOOR) {
			return createReplacementRule(Blocks.LAVA, Blocks.MAGMA_BLOCK);
		} else {
			return this.properties.cold ? createReplacementRule(Blocks.LAVA, Blocks.NETHERRACK) : createReplacementRule(Blocks.LAVA, 0.2F, Blocks.MAGMA_BLOCK);
		}
	}

	@Override
	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox boundingBox,
		ChunkPos chunkPos,
		BlockPos blockPos
	) {
		if (!boundingBox.contains(this.pos)) {
			return true;
		} else {
			boundingBox.encompass(this.structure.calculateBoundingBox(this.placementData, this.pos));
			boolean bl = super.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, blockPos);
			this.placeNetherrackBase(random, serverWorldAccess);
			this.updateNetherracksInBound(random, serverWorldAccess);
			if (this.properties.vines || this.properties.overgrown) {
				BlockPos.stream(this.getBoundingBox()).forEach(blockPosx -> {
					if (this.properties.vines) {
						this.generateVines(random, serverWorldAccess, blockPosx);
					}

					if (this.properties.overgrown) {
						this.generateOvergrownLeaves(random, serverWorldAccess, blockPosx);
					}
				});
			}

			return bl;
		}
	}

	@Override
	protected void handleMetadata(String metadata, BlockPos pos, WorldAccess world, Random random, BlockBox boundingBox) {
	}

	private void generateVines(Random random, WorldAccess world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (!blockState.isAir() && !blockState.isOf(Blocks.VINE)) {
			Direction direction = Direction.Type.HORIZONTAL.random(random);
			BlockPos blockPos = pos.offset(direction);
			BlockState blockState2 = world.getBlockState(blockPos);
			if (blockState2.isAir()) {
				if (Block.isFaceFullSquare(blockState.getCollisionShape(world, pos), direction)) {
					BooleanProperty booleanProperty = VineBlock.getFacingProperty(direction.getOpposite());
					world.setBlockState(blockPos, Blocks.VINE.getDefaultState().with(booleanProperty, Boolean.valueOf(true)), 3);
				}
			}
		}
	}

	private void generateOvergrownLeaves(Random random, WorldAccess world, BlockPos pos) {
		if (random.nextFloat() < 0.5F && world.getBlockState(pos).isOf(Blocks.NETHERRACK) && world.getBlockState(pos.up()).isAir()) {
			world.setBlockState(pos.up(), Blocks.JUNGLE_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, Boolean.valueOf(true)), 3);
		}
	}

	private void updateNetherracksInBound(Random random, WorldAccess world) {
		for (int i = this.boundingBox.minX + 1; i < this.boundingBox.maxX; i++) {
			for (int j = this.boundingBox.minZ + 1; j < this.boundingBox.maxZ; j++) {
				BlockPos blockPos = new BlockPos(i, this.boundingBox.minY, j);
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
		boolean bl = this.verticalPlacement == RuinedPortalFeaturePiece.VerticalPlacement.ON_LAND_SURFACE
			|| this.verticalPlacement == RuinedPortalFeaturePiece.VerticalPlacement.ON_OCEAN_FLOOR;
		Vec3i vec3i = this.boundingBox.getCenter();
		int i = vec3i.getX();
		int j = vec3i.getZ();
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
						int t = bl ? s : Math.min(this.boundingBox.minY, s);
						mutable.set(o, t, p);
						if (Math.abs(t - this.boundingBox.minY) <= 3 && this.canFillNetherrack(world, mutable)) {
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
			&& (this.verticalPlacement == RuinedPortalFeaturePiece.VerticalPlacement.IN_NETHER || !blockState.isOf(Blocks.LAVA));
	}

	private void placeNetherrackBottom(Random random, WorldAccess world, BlockPos pos) {
		if (!this.properties.cold && random.nextFloat() < 0.07F) {
			world.setBlockState(pos, Blocks.MAGMA_BLOCK.getDefaultState(), 3);
		} else {
			world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 3);
		}
	}

	private static int getBaseHeight(WorldAccess world, int x, int y, RuinedPortalFeaturePiece.VerticalPlacement verticalPlacement) {
		return world.getTopY(getHeightmapType(verticalPlacement), x, y) - 1;
	}

	public static Heightmap.Type getHeightmapType(RuinedPortalFeaturePiece.VerticalPlacement verticalPlacement) {
		return verticalPlacement == RuinedPortalFeaturePiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG;
	}

	private static StructureProcessorRule createReplacementRule(Block old, float chance, Block updated) {
		return new StructureProcessorRule(new RandomBlockMatchRuleTest(old, chance), AlwaysTrueRuleTest.INSTANCE, updated.getDefaultState());
	}

	private static StructureProcessorRule createReplacementRule(Block old, Block updated) {
		return new StructureProcessorRule(new BlockMatchRuleTest(old), AlwaysTrueRuleTest.INSTANCE, updated.getDefaultState());
	}

	public static class Properties {
		public boolean cold;
		public float mossiness = 0.2F;
		public boolean airPocket;
		public boolean overgrown;
		public boolean vines;
		public boolean replaceWithBlackstone;

		public Properties() {
		}

		public <T> Properties(Dynamic<T> dynamic) {
			this.cold = dynamic.get("Cold").asBoolean(false);
			this.mossiness = dynamic.get("Mossiness").asFloat(0.2F);
			this.airPocket = dynamic.get("AirPocket").asBoolean(false);
			this.overgrown = dynamic.get("Overgrown").asBoolean(false);
			this.vines = dynamic.get("Vines").asBoolean(false);
			this.replaceWithBlackstone = dynamic.get("ReplaceWithBlackstone").asBoolean(false);
		}

		public <T> T serialize(DynamicOps<T> ops) {
			return ops.createMap(
				ImmutableMap.<T, T>builder()
					.put(ops.createString("Cold"), ops.createBoolean(this.cold))
					.put(ops.createString("Mossiness"), ops.createFloat(this.mossiness))
					.put(ops.createString("AirPocket"), ops.createBoolean(this.airPocket))
					.put(ops.createString("Overgrown"), ops.createBoolean(this.overgrown))
					.put(ops.createString("Vines"), ops.createBoolean(this.vines))
					.put(ops.createString("ReplaceWithBlackstone"), ops.createBoolean(this.replaceWithBlackstone))
					.build()
			);
		}
	}

	public static enum VerticalPlacement {
		ON_LAND_SURFACE("on_land_surface"),
		PARTLY_BURIED("partly_buried"),
		ON_OCEAN_FLOOR("on_ocean_floor"),
		IN_MOUNTAIN("in_mountain"),
		UNDERGROUND("underground"),
		IN_NETHER("in_nether");

		private static final Map<String, RuinedPortalFeaturePiece.VerticalPlacement> VERTICAL_PLACEMENTS = (Map<String, RuinedPortalFeaturePiece.VerticalPlacement>)Arrays.stream(
				values()
			)
			.collect(Collectors.toMap(RuinedPortalFeaturePiece.VerticalPlacement::getId, verticalPlacement -> verticalPlacement));
		private final String id;

		private VerticalPlacement(String id) {
			this.id = id;
		}

		public String getId() {
			return this.id;
		}

		public static RuinedPortalFeaturePiece.VerticalPlacement getFromId(String id) {
			return (RuinedPortalFeaturePiece.VerticalPlacement)VERTICAL_PLACEMENTS.get(id);
		}
	}
}
