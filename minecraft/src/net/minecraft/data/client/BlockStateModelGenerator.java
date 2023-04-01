package net.minecraft.data.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CheeseBlock;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.PropaguleBlock;
import net.minecraft.block.SnifferEggBlock;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.block.enums.Thickness;
import net.minecraft.block.enums.Tilt;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.enums.WallShape;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class BlockStateModelGenerator {
	final Consumer<BlockStateSupplier> blockStateCollector;
	final BiConsumer<Identifier, Supplier<JsonElement>> modelCollector;
	private final Consumer<Item> simpleItemModelExemptionCollector;
	final List<Block> nonOrientableTrapdoors = ImmutableList.of(Blocks.OAK_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.IRON_TRAPDOOR);
	final Map<Block, BlockStateModelGenerator.StateFactory> stoneStateFactories = ImmutableMap.<Block, BlockStateModelGenerator.StateFactory>builder()
		.put(Blocks.STONE, BlockStateModelGenerator::createStoneState)
		.put(Blocks.DEEPSLATE, BlockStateModelGenerator::createDeepslateState)
		.put(Blocks.MUD_BRICKS, BlockStateModelGenerator::createMudBrickState)
		.build();
	final Map<Block, TexturedModel> texturedModels = ImmutableMap.<Block, TexturedModel>builder()
		.put(Blocks.SANDSTONE, TexturedModel.SIDE_TOP_BOTTOM_WALL.get(Blocks.SANDSTONE))
		.put(Blocks.RED_SANDSTONE, TexturedModel.SIDE_TOP_BOTTOM_WALL.get(Blocks.RED_SANDSTONE))
		.put(Blocks.SMOOTH_SANDSTONE, TexturedModel.getCubeAll(TextureMap.getSubId(Blocks.SANDSTONE, "_top")))
		.put(Blocks.SMOOTH_RED_SANDSTONE, TexturedModel.getCubeAll(TextureMap.getSubId(Blocks.RED_SANDSTONE, "_top")))
		.put(
			Blocks.CUT_SANDSTONE,
			TexturedModel.CUBE_COLUMN.get(Blocks.SANDSTONE).textures(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getId(Blocks.CUT_SANDSTONE)))
		)
		.put(
			Blocks.CUT_RED_SANDSTONE,
			TexturedModel.CUBE_COLUMN.get(Blocks.RED_SANDSTONE).textures(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getId(Blocks.CUT_RED_SANDSTONE)))
		)
		.put(Blocks.QUARTZ_BLOCK, TexturedModel.CUBE_COLUMN.get(Blocks.QUARTZ_BLOCK))
		.put(Blocks.SMOOTH_QUARTZ, TexturedModel.getCubeAll(TextureMap.getSubId(Blocks.QUARTZ_BLOCK, "_bottom")))
		.put(Blocks.BLACKSTONE, TexturedModel.SIDE_END_WALL.get(Blocks.BLACKSTONE))
		.put(Blocks.DEEPSLATE, TexturedModel.SIDE_END_WALL.get(Blocks.DEEPSLATE))
		.put(
			Blocks.CHISELED_QUARTZ_BLOCK,
			TexturedModel.CUBE_COLUMN
				.get(Blocks.CHISELED_QUARTZ_BLOCK)
				.textures(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getId(Blocks.CHISELED_QUARTZ_BLOCK)))
		)
		.put(Blocks.CHISELED_SANDSTONE, TexturedModel.CUBE_COLUMN.get(Blocks.CHISELED_SANDSTONE).textures(textures -> {
			textures.put(TextureKey.END, TextureMap.getSubId(Blocks.SANDSTONE, "_top"));
			textures.put(TextureKey.SIDE, TextureMap.getId(Blocks.CHISELED_SANDSTONE));
		}))
		.put(Blocks.CHISELED_RED_SANDSTONE, TexturedModel.CUBE_COLUMN.get(Blocks.CHISELED_RED_SANDSTONE).textures(textures -> {
			textures.put(TextureKey.END, TextureMap.getSubId(Blocks.RED_SANDSTONE, "_top"));
			textures.put(TextureKey.SIDE, TextureMap.getId(Blocks.CHISELED_RED_SANDSTONE));
		}))
		.build();
	static final Map<BlockFamily.Variant, BiConsumer<BlockStateModelGenerator.BlockTexturePool, Block>> VARIANT_POOL_FUNCTIONS = ImmutableMap.<BlockFamily.Variant, BiConsumer<BlockStateModelGenerator.BlockTexturePool, Block>>builder()
		.put(BlockFamily.Variant.BUTTON, BlockStateModelGenerator.BlockTexturePool::button)
		.put(BlockFamily.Variant.DOOR, BlockStateModelGenerator.BlockTexturePool::door)
		.put(BlockFamily.Variant.CHISELED, BlockStateModelGenerator.BlockTexturePool::block)
		.put(BlockFamily.Variant.CRACKED, BlockStateModelGenerator.BlockTexturePool::block)
		.put(BlockFamily.Variant.CUSTOM_FENCE, BlockStateModelGenerator.BlockTexturePool::customFence)
		.put(BlockFamily.Variant.FENCE, BlockStateModelGenerator.BlockTexturePool::fence)
		.put(BlockFamily.Variant.CUSTOM_FENCE_GATE, BlockStateModelGenerator.BlockTexturePool::customFenceGate)
		.put(BlockFamily.Variant.FENCE_GATE, BlockStateModelGenerator.BlockTexturePool::fenceGate)
		.put(BlockFamily.Variant.SIGN, BlockStateModelGenerator.BlockTexturePool::sign)
		.put(BlockFamily.Variant.SLAB, BlockStateModelGenerator.BlockTexturePool::slab)
		.put(BlockFamily.Variant.STAIRS, BlockStateModelGenerator.BlockTexturePool::stairs)
		.put(BlockFamily.Variant.PRESSURE_PLATE, BlockStateModelGenerator.BlockTexturePool::pressurePlate)
		.put(BlockFamily.Variant.TRAPDOOR, BlockStateModelGenerator.BlockTexturePool::registerTrapdoor)
		.put(BlockFamily.Variant.WALL, BlockStateModelGenerator.BlockTexturePool::wall)
		.build();
	/**
	 * A map from a boolean property about connection on one direction to a
	 * function that creates a block state variant for connection on that
	 * direction with a given connection model.
	 */
	public static final List<Pair<BooleanProperty, Function<Identifier, BlockStateVariant>>> CONNECTION_VARIANT_FUNCTIONS = List.of(
		Pair.of(Properties.NORTH, model -> BlockStateVariant.create().put(VariantSettings.MODEL, model)),
		Pair.of(
			Properties.EAST,
			model -> BlockStateVariant.create().put(VariantSettings.MODEL, model).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
		),
		Pair.of(
			Properties.SOUTH,
			model -> BlockStateVariant.create()
					.put(VariantSettings.MODEL, model)
					.put(VariantSettings.Y, VariantSettings.Rotation.R180)
					.put(VariantSettings.UVLOCK, true)
		),
		Pair.of(
			Properties.WEST,
			model -> BlockStateVariant.create()
					.put(VariantSettings.MODEL, model)
					.put(VariantSettings.Y, VariantSettings.Rotation.R270)
					.put(VariantSettings.UVLOCK, true)
		),
		Pair.of(
			Properties.UP,
			model -> BlockStateVariant.create()
					.put(VariantSettings.MODEL, model)
					.put(VariantSettings.X, VariantSettings.Rotation.R270)
					.put(VariantSettings.UVLOCK, true)
		),
		Pair.of(
			Properties.DOWN,
			model -> BlockStateVariant.create().put(VariantSettings.MODEL, model).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
		)
	);
	private static final Map<BlockStateModelGenerator.ChiseledBookshelfModelCacheKey, Identifier> CHISELED_BOOKSHELF_MODEL_CACHE = new HashMap();

	private static BlockStateSupplier createStoneState(
		Block block, Identifier modelId, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector
	) {
		Identifier identifier = Models.CUBE_MIRRORED_ALL.upload(block, textures, modelCollector);
		return createBlockStateWithTwoModelAndRandomInversion(block, modelId, identifier);
	}

	private static BlockStateSupplier createMudBrickState(
		Block block, Identifier modelId, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector
	) {
		Identifier identifier = Models.CUBE_NORTH_WEST_MIRRORED_ALL.upload(block, textures, modelCollector);
		return createSingletonBlockState(block, identifier);
	}

	private static BlockStateSupplier createDeepslateState(
		Block block, Identifier modelId, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector
	) {
		Identifier identifier = Models.CUBE_COLUMN_MIRRORED.upload(block, textures, modelCollector);
		return createBlockStateWithTwoModelAndRandomInversion(block, modelId, identifier).coordinate(createAxisRotatedVariantMap());
	}

	public BlockStateModelGenerator(
		Consumer<BlockStateSupplier> blockStateCollector,
		BiConsumer<Identifier, Supplier<JsonElement>> modelCollector,
		Consumer<Item> simpleItemModelExemptionCollector
	) {
		this.blockStateCollector = blockStateCollector;
		this.modelCollector = modelCollector;
		this.simpleItemModelExemptionCollector = simpleItemModelExemptionCollector;
	}

	void excludeFromSimpleItemModelGeneration(Block block) {
		this.simpleItemModelExemptionCollector.accept(block.asItem());
	}

	void registerParentedItemModel(Block block, Identifier parentModelId) {
		this.modelCollector.accept(ModelIds.getItemModelId(block.asItem()), new SimpleModelSupplier(parentModelId));
	}

	private void registerParentedItemModel(Item item, Identifier parentModelId) {
		this.modelCollector.accept(ModelIds.getItemModelId(item), new SimpleModelSupplier(parentModelId));
	}

	void registerItemModel(Item item) {
		Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(item), this.modelCollector);
	}

	private void registerItemModel(Block block) {
		Item item = block.asItem();
		if (item != Items.AIR) {
			Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(block), this.modelCollector);
		}
	}

	private void registerItemModel(Block block, String textureSuffix) {
		Item item = block.asItem();
		Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(TextureMap.getSubId(block, textureSuffix)), this.modelCollector);
	}

	private static BlockStateVariantMap createNorthDefaultHorizontalRotationStates() {
		return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
			.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
			.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
			.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
			.register(Direction.NORTH, BlockStateVariant.create());
	}

	private static BlockStateVariantMap createSouthDefaultHorizontalRotationStates() {
		return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
			.register(Direction.SOUTH, BlockStateVariant.create())
			.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
			.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
			.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270));
	}

	private static BlockStateVariantMap createEastDefaultHorizontalRotationStates() {
		return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
			.register(Direction.EAST, BlockStateVariant.create())
			.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
			.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
			.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270));
	}

	private static BlockStateVariantMap createNorthDefaultRotationStates() {
		return BlockStateVariantMap.create(Properties.FACING)
			.register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
			.register(Direction.UP, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R270))
			.register(Direction.NORTH, BlockStateVariant.create())
			.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
			.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
			.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90));
	}

	private static VariantsBlockStateSupplier createBlockStateWithRandomHorizontalRotations(Block block, Identifier modelId) {
		return VariantsBlockStateSupplier.create(block, createModelVariantWithRandomHorizontalRotations(modelId));
	}

	private static BlockStateVariant[] createModelVariantWithRandomHorizontalRotations(Identifier modelId) {
		return new BlockStateVariant[]{
			BlockStateVariant.create().put(VariantSettings.MODEL, modelId),
			BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R90),
			BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R180),
			BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)
		};
	}

	private static VariantsBlockStateSupplier createBlockStateWithTwoModelAndRandomInversion(Block block, Identifier firstModelId, Identifier secondModelId) {
		return VariantsBlockStateSupplier.create(
			block,
			BlockStateVariant.create().put(VariantSettings.MODEL, firstModelId),
			BlockStateVariant.create().put(VariantSettings.MODEL, secondModelId),
			BlockStateVariant.create().put(VariantSettings.MODEL, firstModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180),
			BlockStateVariant.create().put(VariantSettings.MODEL, secondModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)
		);
	}

	private static BlockStateVariantMap createBooleanModelMap(BooleanProperty property, Identifier trueModel, Identifier falseModel) {
		return BlockStateVariantMap.create(property)
			.register(true, BlockStateVariant.create().put(VariantSettings.MODEL, trueModel))
			.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, falseModel));
	}

	private void registerMirrorable(Block block) {
		Identifier identifier = TexturedModel.CUBE_ALL.upload(block, this.modelCollector);
		Identifier identifier2 = TexturedModel.CUBE_MIRRORED_ALL.upload(block, this.modelCollector);
		this.blockStateCollector.accept(createBlockStateWithTwoModelAndRandomInversion(block, identifier, identifier2));
	}

	private void registerRotatable(Block block) {
		Identifier identifier = TexturedModel.CUBE_ALL.upload(block, this.modelCollector);
		this.blockStateCollector.accept(createBlockStateWithRandomHorizontalRotations(block, identifier));
	}

	private void registerBrushableBlock(Block block) {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block)
					.coordinate(
						BlockStateVariantMap.create(Properties.DUSTED)
							.register(
								dusted -> {
									String string = "_" + dusted;
									Identifier identifier = TextureMap.getSubId(block, string);
									return BlockStateVariant.create()
										.put(VariantSettings.MODEL, Models.CUBE_ALL.upload(block, string, new TextureMap().put(TextureKey.ALL, identifier), this.modelCollector));
								}
							)
					)
			);
		this.registerParentedItemModel(block, TextureMap.getSubId(block, "_0"));
	}

	static BlockStateSupplier createButtonBlockState(Block buttonBlock, Identifier regularModelId, Identifier pressedModelId) {
		return VariantsBlockStateSupplier.create(buttonBlock)
			.coordinate(
				BlockStateVariantMap.create(Properties.POWERED)
					.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId))
					.register(true, BlockStateVariant.create().put(VariantSettings.MODEL, pressedModelId))
			)
			.coordinate(
				BlockStateVariantMap.create(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING)
					.register(WallMountLocation.FLOOR, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
					.register(WallMountLocation.FLOOR, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
					.register(WallMountLocation.FLOOR, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
					.register(WallMountLocation.FLOOR, Direction.NORTH, BlockStateVariant.create())
					.register(
						WallMountLocation.WALL,
						Direction.EAST,
						BlockStateVariant.create()
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.X, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						WallMountLocation.WALL,
						Direction.WEST,
						BlockStateVariant.create()
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.X, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						WallMountLocation.WALL,
						Direction.SOUTH,
						BlockStateVariant.create()
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.X, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						WallMountLocation.WALL,
						Direction.NORTH,
						BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
					)
					.register(
						WallMountLocation.CEILING,
						Direction.EAST,
						BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.X, VariantSettings.Rotation.R180)
					)
					.register(
						WallMountLocation.CEILING,
						Direction.WEST,
						BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.X, VariantSettings.Rotation.R180)
					)
					.register(WallMountLocation.CEILING, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
					.register(
						WallMountLocation.CEILING,
						Direction.NORTH,
						BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.X, VariantSettings.Rotation.R180)
					)
			);
	}

	private static BlockStateVariantMap.QuadrupleProperty<Direction, DoubleBlockHalf, DoorHinge, Boolean> fillDoorVariantMap(
		BlockStateVariantMap.QuadrupleProperty<Direction, DoubleBlockHalf, DoorHinge, Boolean> variantMap,
		DoubleBlockHalf targetHalf,
		Identifier leftHingeClosedModelId,
		Identifier leftHingeOpenModelId,
		Identifier rightHingeClosedModelId,
		Identifier rightHingeOpenModelId
	) {
		return variantMap.register(Direction.EAST, targetHalf, DoorHinge.LEFT, false, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeClosedModelId))
			.register(
				Direction.SOUTH,
				targetHalf,
				DoorHinge.LEFT,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)
			)
			.register(
				Direction.WEST,
				targetHalf,
				DoorHinge.LEFT,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)
			)
			.register(
				Direction.NORTH,
				targetHalf,
				DoorHinge.LEFT,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)
			)
			.register(Direction.EAST, targetHalf, DoorHinge.RIGHT, false, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeClosedModelId))
			.register(
				Direction.SOUTH,
				targetHalf,
				DoorHinge.RIGHT,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)
			)
			.register(
				Direction.WEST,
				targetHalf,
				DoorHinge.RIGHT,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)
			)
			.register(
				Direction.NORTH,
				targetHalf,
				DoorHinge.RIGHT,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)
			)
			.register(
				Direction.EAST,
				targetHalf,
				DoorHinge.LEFT,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)
			)
			.register(
				Direction.SOUTH,
				targetHalf,
				DoorHinge.LEFT,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)
			)
			.register(
				Direction.WEST,
				targetHalf,
				DoorHinge.LEFT,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)
			)
			.register(Direction.NORTH, targetHalf, DoorHinge.LEFT, true, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeOpenModelId))
			.register(
				Direction.EAST,
				targetHalf,
				DoorHinge.RIGHT,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)
			)
			.register(Direction.SOUTH, targetHalf, DoorHinge.RIGHT, true, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeOpenModelId))
			.register(
				Direction.WEST,
				targetHalf,
				DoorHinge.RIGHT,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)
			)
			.register(
				Direction.NORTH,
				targetHalf,
				DoorHinge.RIGHT,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)
			);
	}

	private static BlockStateSupplier createDoorBlockState(
		Block doorBlock,
		Identifier bottomLeftHingeClosedModelId,
		Identifier bottomLeftHingeOpenModelId,
		Identifier bottomRightHingeClosedModelId,
		Identifier bottomRightHingeOpenModelId,
		Identifier topLeftHingeClosedModelId,
		Identifier topLeftHingeOpenModelId,
		Identifier topRightHingeClosedModelId,
		Identifier topRightHingeOpenModelId
	) {
		return VariantsBlockStateSupplier.create(doorBlock)
			.coordinate(
				fillDoorVariantMap(
					fillDoorVariantMap(
						BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.DOUBLE_BLOCK_HALF, Properties.DOOR_HINGE, Properties.OPEN),
						DoubleBlockHalf.LOWER,
						bottomLeftHingeClosedModelId,
						bottomLeftHingeOpenModelId,
						bottomRightHingeClosedModelId,
						bottomRightHingeOpenModelId
					),
					DoubleBlockHalf.UPPER,
					topLeftHingeClosedModelId,
					topLeftHingeOpenModelId,
					topRightHingeClosedModelId,
					topRightHingeOpenModelId
				)
			);
	}

	static BlockStateSupplier createCustomFenceBlockState(
		Block customFenceBlock, Identifier postModelId, Identifier northModelId, Identifier eastModelId, Identifier southModelId, Identifier westModelId
	) {
		return MultipartBlockStateSupplier.create(customFenceBlock)
			.with(BlockStateVariant.create().put(VariantSettings.MODEL, postModelId))
			.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, northModelId).put(VariantSettings.UVLOCK, false))
			.with(When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, eastModelId).put(VariantSettings.UVLOCK, false))
			.with(When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, southModelId).put(VariantSettings.UVLOCK, false))
			.with(When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, westModelId).put(VariantSettings.UVLOCK, false));
	}

	static BlockStateSupplier createFenceBlockState(Block fenceBlock, Identifier postModelId, Identifier sideModelId) {
		return MultipartBlockStateSupplier.create(fenceBlock)
			.with(BlockStateVariant.create().put(VariantSettings.MODEL, postModelId))
			.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, sideModelId).put(VariantSettings.UVLOCK, true))
			.with(
				When.create().set(Properties.EAST, true),
				BlockStateVariant.create().put(VariantSettings.MODEL, sideModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.SOUTH, true),
				BlockStateVariant.create().put(VariantSettings.MODEL, sideModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.WEST, true),
				BlockStateVariant.create().put(VariantSettings.MODEL, sideModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)
			);
	}

	static BlockStateSupplier createWallBlockState(Block wallBlock, Identifier postModelId, Identifier lowSideModelId, Identifier tallSideModelId) {
		return MultipartBlockStateSupplier.create(wallBlock)
			.with(When.create().set(Properties.UP, true), BlockStateVariant.create().put(VariantSettings.MODEL, postModelId))
			.with(
				When.create().set(Properties.NORTH_WALL_SHAPE, WallShape.LOW),
				BlockStateVariant.create().put(VariantSettings.MODEL, lowSideModelId).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.EAST_WALL_SHAPE, WallShape.LOW),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, lowSideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.R90)
					.put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.SOUTH_WALL_SHAPE, WallShape.LOW),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, lowSideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.R180)
					.put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.WEST_WALL_SHAPE, WallShape.LOW),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, lowSideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.R270)
					.put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.NORTH_WALL_SHAPE, WallShape.TALL),
				BlockStateVariant.create().put(VariantSettings.MODEL, tallSideModelId).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.EAST_WALL_SHAPE, WallShape.TALL),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, tallSideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.R90)
					.put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.SOUTH_WALL_SHAPE, WallShape.TALL),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, tallSideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.R180)
					.put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.WEST_WALL_SHAPE, WallShape.TALL),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, tallSideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.R270)
					.put(VariantSettings.UVLOCK, true)
			);
	}

	static BlockStateSupplier createFenceGateBlockState(
		Block fenceGateBlock, Identifier openModelId, Identifier closedModelId, Identifier openWallModelId, Identifier closedWallModelId, boolean uvlock
	) {
		return VariantsBlockStateSupplier.create(fenceGateBlock, BlockStateVariant.create().put(VariantSettings.UVLOCK, uvlock))
			.coordinate(createSouthDefaultHorizontalRotationStates())
			.coordinate(
				BlockStateVariantMap.create(Properties.IN_WALL, Properties.OPEN)
					.register(false, false, BlockStateVariant.create().put(VariantSettings.MODEL, closedModelId))
					.register(true, false, BlockStateVariant.create().put(VariantSettings.MODEL, closedWallModelId))
					.register(false, true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId))
					.register(true, true, BlockStateVariant.create().put(VariantSettings.MODEL, openWallModelId))
			);
	}

	static BlockStateSupplier createStairsBlockState(Block stairsBlock, Identifier innerModelId, Identifier regularModelId, Identifier outerModelId) {
		return VariantsBlockStateSupplier.create(stairsBlock)
			.coordinate(
				BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.STAIR_SHAPE)
					.register(Direction.EAST, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId))
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						StairShape.STRAIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.BOTTOM,
						StairShape.STRAIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.BOTTOM,
						StairShape.STRAIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId))
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.BOTTOM,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.BOTTOM,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.BOTTOM,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId))
					.register(
						Direction.NORTH,
						BlockHalf.BOTTOM,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId))
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.BOTTOM,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.BOTTOM,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.BOTTOM,
						StairShape.INNER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						StairShape.INNER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId))
					.register(
						Direction.NORTH,
						BlockHalf.BOTTOM,
						StairShape.INNER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						StairShape.STRAIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						StairShape.STRAIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						StairShape.STRAIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.TOP,
						StairShape.STRAIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.TOP,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.TOP,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.TOP,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						StairShape.INNER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						StairShape.INNER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						StairShape.INNER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.TOP,
						StairShape.INNER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
			);
	}

	private static BlockStateSupplier createOrientableTrapdoorBlockState(
		Block trapdoorBlock, Identifier topModelId, Identifier bottomModelId, Identifier openModelId
	) {
		return VariantsBlockStateSupplier.create(trapdoorBlock)
			.coordinate(
				BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.OPEN)
					.register(Direction.NORTH, BlockHalf.BOTTOM, false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
					.register(
						Direction.SOUTH,
						BlockHalf.BOTTOM,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.register(
						Direction.EAST,
						BlockHalf.BOTTOM,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.register(Direction.NORTH, BlockHalf.TOP, false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, topModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, topModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, topModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.register(Direction.NORTH, BlockHalf.BOTTOM, true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId))
					.register(
						Direction.SOUTH,
						BlockHalf.BOTTOM,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.register(
						Direction.EAST,
						BlockHalf.BOTTOM,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.register(
						Direction.NORTH,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, openModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, openModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R0)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, openModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, openModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
			);
	}

	private static BlockStateSupplier createTrapdoorBlockState(Block trapdoorBlock, Identifier topModelId, Identifier bottomModelId, Identifier openModelId) {
		return VariantsBlockStateSupplier.create(trapdoorBlock)
			.coordinate(
				BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.OPEN)
					.register(Direction.NORTH, BlockHalf.BOTTOM, false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
					.register(Direction.SOUTH, BlockHalf.BOTTOM, false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
					.register(Direction.EAST, BlockHalf.BOTTOM, false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
					.register(Direction.WEST, BlockHalf.BOTTOM, false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
					.register(Direction.NORTH, BlockHalf.TOP, false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
					.register(Direction.SOUTH, BlockHalf.TOP, false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
					.register(Direction.EAST, BlockHalf.TOP, false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
					.register(Direction.WEST, BlockHalf.TOP, false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
					.register(Direction.NORTH, BlockHalf.BOTTOM, true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId))
					.register(
						Direction.SOUTH,
						BlockHalf.BOTTOM,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.register(
						Direction.EAST,
						BlockHalf.BOTTOM,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.register(Direction.NORTH, BlockHalf.TOP, true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId))
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
			);
	}

	static VariantsBlockStateSupplier createSingletonBlockState(Block block, Identifier modelId) {
		return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, modelId));
	}

	private static BlockStateVariantMap createAxisRotatedVariantMap() {
		return BlockStateVariantMap.create(Properties.AXIS)
			.register(Direction.Axis.Y, BlockStateVariant.create())
			.register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
			.register(
				Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90)
			);
	}

	static BlockStateSupplier createUvLockedColumnBlockState(Block block, TextureMap textureMap, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
		Identifier identifier = Models.CUBE_COLUMN_UV_LOCKED_X.upload(block, textureMap, modelCollector);
		Identifier identifier2 = Models.CUBE_COLUMN_UV_LOCKED_Y.upload(block, textureMap, modelCollector);
		Identifier identifier3 = Models.CUBE_COLUMN_UV_LOCKED_Z.upload(block, textureMap, modelCollector);
		Identifier identifier4 = Models.CUBE_COLUMN.upload(block, textureMap, modelCollector);
		return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, identifier4))
			.coordinate(
				BlockStateVariantMap.create(Properties.AXIS)
					.register(Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.register(Direction.Axis.Y, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
			);
	}

	static BlockStateSupplier createAxisRotatedBlockState(Block block, Identifier modelId) {
		return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, modelId)).coordinate(createAxisRotatedVariantMap());
	}

	private void registerAxisRotated(Block block, Identifier modelId) {
		this.blockStateCollector.accept(createAxisRotatedBlockState(block, modelId));
	}

	private void registerAxisRotated(Block block, TexturedModel.Factory modelFactory) {
		Identifier identifier = modelFactory.upload(block, this.modelCollector);
		this.blockStateCollector.accept(createAxisRotatedBlockState(block, identifier));
	}

	private void registerNorthDefaultHorizontalRotated(Block block, TexturedModel.Factory modelFactory) {
		Identifier identifier = modelFactory.upload(block, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
	}

	static BlockStateSupplier createAxisRotatedBlockState(Block block, Identifier verticalModelId, Identifier horizontalModelId) {
		return VariantsBlockStateSupplier.create(block)
			.coordinate(
				BlockStateVariantMap.create(Properties.AXIS)
					.register(Direction.Axis.Y, BlockStateVariant.create().put(VariantSettings.MODEL, verticalModelId))
					.register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, horizontalModelId).put(VariantSettings.X, VariantSettings.Rotation.R90))
					.register(
						Direction.Axis.X,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, horizontalModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.R90)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
			);
	}

	private void registerAxisRotated(Block block, TexturedModel.Factory verticalModelFactory, TexturedModel.Factory horizontalModelFactory) {
		Identifier identifier = verticalModelFactory.upload(block, this.modelCollector);
		Identifier identifier2 = horizontalModelFactory.upload(block, this.modelCollector);
		this.blockStateCollector.accept(createAxisRotatedBlockState(block, identifier, identifier2));
	}

	private Identifier createSubModel(Block block, String suffix, Model model, Function<Identifier, TextureMap> texturesFactory) {
		return model.upload(block, suffix, (TextureMap)texturesFactory.apply(TextureMap.getSubId(block, suffix)), this.modelCollector);
	}

	static BlockStateSupplier createPressurePlateBlockState(Block pressurePlateBlock, Identifier upModelId, Identifier downModelId) {
		return VariantsBlockStateSupplier.create(pressurePlateBlock).coordinate(createBooleanModelMap(Properties.POWERED, downModelId, upModelId));
	}

	static BlockStateSupplier createSlabBlockState(Block slabBlock, Identifier bottomModelId, Identifier topModelId, Identifier fullModelId) {
		return VariantsBlockStateSupplier.create(slabBlock)
			.coordinate(
				BlockStateVariantMap.create(Properties.SLAB_TYPE)
					.register(SlabType.BOTTOM, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
					.register(SlabType.TOP, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
					.register(SlabType.DOUBLE, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId))
			);
	}

	private void registerSimpleCubeAll(Block block) {
		this.registerSingleton(block, TexturedModel.CUBE_ALL);
	}

	private void registerSingleton(Block block, TexturedModel.Factory modelFactory) {
		this.blockStateCollector.accept(createSingletonBlockState(block, modelFactory.upload(block, this.modelCollector)));
	}

	private void registerSingleton(Block block, TextureMap textures, Model model) {
		Identifier identifier = model.upload(block, textures, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(block, identifier));
	}

	private BlockStateModelGenerator.BlockTexturePool registerCubeAllModelTexturePool(Block block) {
		TexturedModel texturedModel = (TexturedModel)this.texturedModels.getOrDefault(block, TexturedModel.CUBE_ALL.get(block));
		return new BlockStateModelGenerator.BlockTexturePool(texturedModel.getTextures()).base(block, texturedModel.getModel());
	}

	public void registerHangingSign(Block strippedLog, Block hangingSign, Block wallHangingSign) {
		TextureMap textureMap = TextureMap.particle(strippedLog);
		Identifier identifier = Models.PARTICLE.upload(hangingSign, textureMap, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(hangingSign, identifier));
		this.blockStateCollector.accept(createSingletonBlockState(wallHangingSign, identifier));
		this.registerItemModel(hangingSign.asItem());
		this.excludeFromSimpleItemModelGeneration(wallHangingSign);
	}

	void registerDoor(Block doorBlock) {
		TextureMap textureMap = TextureMap.topBottom(doorBlock);
		Identifier identifier = Models.DOOR_BOTTOM_LEFT.upload(doorBlock, textureMap, this.modelCollector);
		Identifier identifier2 = Models.DOOR_BOTTOM_LEFT_OPEN.upload(doorBlock, textureMap, this.modelCollector);
		Identifier identifier3 = Models.DOOR_BOTTOM_RIGHT.upload(doorBlock, textureMap, this.modelCollector);
		Identifier identifier4 = Models.DOOR_BOTTOM_RIGHT_OPEN.upload(doorBlock, textureMap, this.modelCollector);
		Identifier identifier5 = Models.DOOR_TOP_LEFT.upload(doorBlock, textureMap, this.modelCollector);
		Identifier identifier6 = Models.DOOR_TOP_LEFT_OPEN.upload(doorBlock, textureMap, this.modelCollector);
		Identifier identifier7 = Models.DOOR_TOP_RIGHT.upload(doorBlock, textureMap, this.modelCollector);
		Identifier identifier8 = Models.DOOR_TOP_RIGHT_OPEN.upload(doorBlock, textureMap, this.modelCollector);
		this.registerItemModel(doorBlock.asItem());
		this.blockStateCollector
			.accept(createDoorBlockState(doorBlock, identifier, identifier2, identifier3, identifier4, identifier5, identifier6, identifier7, identifier8));
	}

	void registerOrientableTrapdoor(Block trapdoorBlock) {
		TextureMap textureMap = TextureMap.texture(trapdoorBlock);
		Identifier identifier = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_TOP.upload(trapdoorBlock, textureMap, this.modelCollector);
		Identifier identifier2 = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_BOTTOM.upload(trapdoorBlock, textureMap, this.modelCollector);
		Identifier identifier3 = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_OPEN.upload(trapdoorBlock, textureMap, this.modelCollector);
		this.blockStateCollector.accept(createOrientableTrapdoorBlockState(trapdoorBlock, identifier, identifier2, identifier3));
		this.registerParentedItemModel(trapdoorBlock, identifier2);
	}

	void registerTrapdoor(Block trapdoorBlock) {
		TextureMap textureMap = TextureMap.texture(trapdoorBlock);
		Identifier identifier = Models.TEMPLATE_TRAPDOOR_TOP.upload(trapdoorBlock, textureMap, this.modelCollector);
		Identifier identifier2 = Models.TEMPLATE_TRAPDOOR_BOTTOM.upload(trapdoorBlock, textureMap, this.modelCollector);
		Identifier identifier3 = Models.TEMPLATE_TRAPDOOR_OPEN.upload(trapdoorBlock, textureMap, this.modelCollector);
		this.blockStateCollector.accept(createTrapdoorBlockState(trapdoorBlock, identifier, identifier2, identifier3));
		this.registerParentedItemModel(trapdoorBlock, identifier2);
	}

	private void registerBigDripleaf() {
		this.excludeFromSimpleItemModelGeneration(Blocks.BIG_DRIPLEAF);
		Identifier identifier = ModelIds.getBlockModelId(Blocks.BIG_DRIPLEAF);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.BIG_DRIPLEAF, "_partial_tilt");
		Identifier identifier3 = ModelIds.getBlockSubModelId(Blocks.BIG_DRIPLEAF, "_full_tilt");
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.BIG_DRIPLEAF)
					.coordinate(createNorthDefaultHorizontalRotationStates())
					.coordinate(
						BlockStateVariantMap.create(Properties.TILT)
							.register(Tilt.NONE, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(Tilt.UNSTABLE, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(Tilt.PARTIAL, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
							.register(Tilt.FULL, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
					)
			);
	}

	private BlockStateModelGenerator.LogTexturePool registerLog(Block logBlock) {
		return new BlockStateModelGenerator.LogTexturePool(TextureMap.sideAndEndForTop(logBlock));
	}

	private void registerSimpleState(Block block) {
		this.registerStateWithModelReference(block, block);
	}

	private void registerStateWithModelReference(Block block, Block modelReference) {
		this.blockStateCollector.accept(createSingletonBlockState(block, ModelIds.getBlockModelId(modelReference)));
	}

	private void registerTintableCross(Block block, BlockStateModelGenerator.TintType tintType) {
		this.registerItemModel(block);
		this.registerTintableCrossBlockState(block, tintType);
	}

	private void registerTintableCross(Block block, BlockStateModelGenerator.TintType tintType, TextureMap texture) {
		this.registerItemModel(block);
		this.registerTintableCrossBlockState(block, tintType, texture);
	}

	private void registerTintableCrossBlockState(Block block, BlockStateModelGenerator.TintType tintType) {
		TextureMap textureMap = TextureMap.cross(block);
		this.registerTintableCrossBlockState(block, tintType, textureMap);
	}

	private void registerTintableCrossBlockState(Block block, BlockStateModelGenerator.TintType tintType, TextureMap crossTexture) {
		Identifier identifier = tintType.getCrossModel().upload(block, crossTexture, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(block, identifier));
	}

	private void registerTintableCrossBlockStateWithStages(Block block, BlockStateModelGenerator.TintType tintType, Property<Integer> stageProperty, int... stages) {
		if (stageProperty.getValues().size() != stages.length) {
			throw new IllegalArgumentException("missing values for property: " + stageProperty);
		} else {
			BlockStateVariantMap blockStateVariantMap = BlockStateVariantMap.create(stageProperty).register(integer -> {
				String string = "_stage" + stages[integer];
				TextureMap textureMap = TextureMap.cross(TextureMap.getSubId(block, string));
				Identifier identifier = tintType.getCrossModel().upload(block, string, textureMap, this.modelCollector);
				return BlockStateVariant.create().put(VariantSettings.MODEL, identifier);
			});
			this.registerItemModel(block.asItem());
			this.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(blockStateVariantMap));
		}
	}

	private void registerFlowerPotPlant(Block plantBlock, Block flowerPotBlock, BlockStateModelGenerator.TintType tintType) {
		this.registerTintableCross(plantBlock, tintType);
		TextureMap textureMap = TextureMap.plant(plantBlock);
		Identifier identifier = tintType.getFlowerPotCrossModel().upload(flowerPotBlock, textureMap, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(flowerPotBlock, identifier));
	}

	private void registerCoralFan(Block coralFanBlock, Block coralWallFanBlock) {
		TexturedModel texturedModel = TexturedModel.CORAL_FAN.get(coralFanBlock);
		Identifier identifier = texturedModel.upload(coralFanBlock, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(coralFanBlock, identifier));
		Identifier identifier2 = Models.CORAL_WALL_FAN.upload(coralWallFanBlock, texturedModel.getTextures(), this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(coralWallFanBlock, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
		this.registerItemModel(coralFanBlock);
	}

	private void registerGourd(Block stemBlock, Block attachedStemBlock) {
		this.registerItemModel(stemBlock.asItem());
		TextureMap textureMap = TextureMap.stem(stemBlock);
		TextureMap textureMap2 = TextureMap.stemAndUpper(stemBlock, attachedStemBlock);
		Identifier identifier = Models.STEM_FRUIT.upload(attachedStemBlock, textureMap2, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(attachedStemBlock, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.coordinate(
						BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
							.register(Direction.WEST, BlockStateVariant.create())
							.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
							.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
							.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
					)
			);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(stemBlock)
					.coordinate(
						BlockStateVariantMap.create(Properties.AGE_7)
							.register(
								age -> BlockStateVariant.create().put(VariantSettings.MODEL, Models.STEM_GROWTH_STAGES[age].upload(stemBlock, textureMap, this.modelCollector))
							)
					)
			);
	}

	private void registerPitcherPlant() {
		Block block = Blocks.PITCHER_PLANT;
		this.registerItemModel(block.asItem());
		Identifier identifier = ModelIds.getBlockSubModelId(block, "_top");
		Identifier identifier2 = ModelIds.getBlockSubModelId(block, "_bottom");
		this.registerDoubleBlock(block, identifier, identifier2);
	}

	private void registerPitcherCrop() {
		Block block = Blocks.PITCHER_CROP;
		this.registerItemModel(block.asItem());
		BlockStateVariantMap blockStateVariantMap = BlockStateVariantMap.create(PitcherCropBlock.AGE, Properties.DOUBLE_BLOCK_HALF).register((age, half) -> {
			return switch (half) {
				case UPPER -> BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(block, "_top_stage_" + age));
				case LOWER -> BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(block, "_bottom_stage_" + age));
			};
		});
		this.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(blockStateVariantMap));
	}

	private void registerCoral(
		Block coral, Block deadCoral, Block coralBlock, Block deadCoralBlock, Block coralFan, Block deadCoralFan, Block coralWallFan, Block deadCoralWallFan
	) {
		this.registerTintableCross(coral, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerTintableCross(deadCoral, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerSimpleCubeAll(coralBlock);
		this.registerSimpleCubeAll(deadCoralBlock);
		this.registerCoralFan(coralFan, coralWallFan);
		this.registerCoralFan(deadCoralFan, deadCoralWallFan);
	}

	private void registerDoubleBlock(Block doubleBlock, BlockStateModelGenerator.TintType tintType) {
		this.registerItemModel(doubleBlock, "_top");
		Identifier identifier = this.createSubModel(doubleBlock, "_top", tintType.getCrossModel(), TextureMap::cross);
		Identifier identifier2 = this.createSubModel(doubleBlock, "_bottom", tintType.getCrossModel(), TextureMap::cross);
		this.registerDoubleBlock(doubleBlock, identifier, identifier2);
	}

	private void registerSunflower() {
		this.registerItemModel(Blocks.SUNFLOWER, "_front");
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.SUNFLOWER, "_top");
		Identifier identifier2 = this.createSubModel(Blocks.SUNFLOWER, "_bottom", BlockStateModelGenerator.TintType.NOT_TINTED.getCrossModel(), TextureMap::cross);
		this.registerDoubleBlock(Blocks.SUNFLOWER, identifier, identifier2);
	}

	private void registerTallSeagrass() {
		Identifier identifier = this.createSubModel(Blocks.TALL_SEAGRASS, "_top", Models.TEMPLATE_SEAGRASS, TextureMap::texture);
		Identifier identifier2 = this.createSubModel(Blocks.TALL_SEAGRASS, "_bottom", Models.TEMPLATE_SEAGRASS, TextureMap::texture);
		this.registerDoubleBlock(Blocks.TALL_SEAGRASS, identifier, identifier2);
	}

	private void registerSmallDripleaf() {
		this.excludeFromSimpleItemModelGeneration(Blocks.SMALL_DRIPLEAF);
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.SMALL_DRIPLEAF, "_top");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.SMALL_DRIPLEAF, "_bottom");
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.SMALL_DRIPLEAF)
					.coordinate(createNorthDefaultHorizontalRotationStates())
					.coordinate(
						BlockStateVariantMap.create(Properties.DOUBLE_BLOCK_HALF)
							.register(DoubleBlockHalf.LOWER, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
							.register(DoubleBlockHalf.UPPER, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					)
			);
	}

	private void registerDoubleBlock(Block block, Identifier upperHalfModelId, Identifier lowerHalfModelId) {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block)
					.coordinate(
						BlockStateVariantMap.create(Properties.DOUBLE_BLOCK_HALF)
							.register(DoubleBlockHalf.LOWER, BlockStateVariant.create().put(VariantSettings.MODEL, lowerHalfModelId))
							.register(DoubleBlockHalf.UPPER, BlockStateVariant.create().put(VariantSettings.MODEL, upperHalfModelId))
					)
			);
	}

	private void registerTurnableRail(Block rail) {
		TextureMap textureMap = TextureMap.rail(rail);
		TextureMap textureMap2 = TextureMap.rail(TextureMap.getSubId(rail, "_corner"));
		Identifier identifier = Models.RAIL_FLAT.upload(rail, textureMap, this.modelCollector);
		Identifier identifier2 = Models.RAIL_CURVED.upload(rail, textureMap2, this.modelCollector);
		Identifier identifier3 = Models.TEMPLATE_RAIL_RAISED_NE.upload(rail, textureMap, this.modelCollector);
		Identifier identifier4 = Models.TEMPLATE_RAIL_RAISED_SW.upload(rail, textureMap, this.modelCollector);
		this.registerItemModel(rail);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(rail)
					.coordinate(
						BlockStateVariantMap.create(Properties.RAIL_SHAPE)
							.register(RailShape.NORTH_SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(RailShape.EAST_WEST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90))
							.register(
								RailShape.ASCENDING_EAST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								RailShape.ASCENDING_WEST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(RailShape.ASCENDING_NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
							.register(RailShape.ASCENDING_SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, identifier4))
							.register(RailShape.SOUTH_EAST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
							.register(RailShape.SOUTH_WEST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90))
							.register(RailShape.NORTH_WEST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R180))
							.register(RailShape.NORTH_EAST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R270))
					)
			);
	}

	private void registerStraightRail(Block rail) {
		Identifier identifier = this.createSubModel(rail, "", Models.RAIL_FLAT, TextureMap::rail);
		Identifier identifier2 = this.createSubModel(rail, "", Models.TEMPLATE_RAIL_RAISED_NE, TextureMap::rail);
		Identifier identifier3 = this.createSubModel(rail, "", Models.TEMPLATE_RAIL_RAISED_SW, TextureMap::rail);
		Identifier identifier4 = this.createSubModel(rail, "_on", Models.RAIL_FLAT, TextureMap::rail);
		Identifier identifier5 = this.createSubModel(rail, "_on", Models.TEMPLATE_RAIL_RAISED_NE, TextureMap::rail);
		Identifier identifier6 = this.createSubModel(rail, "_on", Models.TEMPLATE_RAIL_RAISED_SW, TextureMap::rail);
		BlockStateVariantMap blockStateVariantMap = BlockStateVariantMap.create(Properties.POWERED, Properties.STRAIGHT_RAIL_SHAPE).register((on, shape) -> {
			switch (shape) {
				case NORTH_SOUTH:
					return BlockStateVariant.create().put(VariantSettings.MODEL, on ? identifier4 : identifier);
				case EAST_WEST:
					return BlockStateVariant.create().put(VariantSettings.MODEL, on ? identifier4 : identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90);
				case ASCENDING_EAST:
					return BlockStateVariant.create().put(VariantSettings.MODEL, on ? identifier5 : identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90);
				case ASCENDING_WEST:
					return BlockStateVariant.create().put(VariantSettings.MODEL, on ? identifier6 : identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90);
				case ASCENDING_NORTH:
					return BlockStateVariant.create().put(VariantSettings.MODEL, on ? identifier5 : identifier2);
				case ASCENDING_SOUTH:
					return BlockStateVariant.create().put(VariantSettings.MODEL, on ? identifier6 : identifier3);
				default:
					throw new UnsupportedOperationException("Fix you generator!");
			}
		});
		this.registerItemModel(rail);
		this.blockStateCollector.accept(VariantsBlockStateSupplier.create(rail).coordinate(blockStateVariantMap));
	}

	private BlockStateModelGenerator.BuiltinModelPool registerBuiltin(Identifier modelId, Block particleBlock) {
		return new BlockStateModelGenerator.BuiltinModelPool(modelId, particleBlock);
	}

	private BlockStateModelGenerator.BuiltinModelPool registerBuiltin(Block block, Block particleBlock) {
		return new BlockStateModelGenerator.BuiltinModelPool(ModelIds.getBlockModelId(block), particleBlock);
	}

	private void registerBuiltinWithParticle(Block block, Item particleSource) {
		Identifier identifier = Models.PARTICLE.upload(block, TextureMap.particle(particleSource), this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(block, identifier));
	}

	private void registerBuiltinWithParticle(Block block, Identifier particleSource) {
		Identifier identifier = Models.PARTICLE.upload(block, TextureMap.particle(particleSource), this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(block, identifier));
	}

	private void registerWoolAndCarpet(Block wool, Block carpet) {
		this.registerSimpleCubeAll(wool);
		Identifier identifier = TexturedModel.CARPET.get(wool).upload(carpet, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(carpet, identifier));
	}

	private void registerFlowerbed(Block flowerbed) {
		this.registerItemModel(flowerbed.asItem());
		Identifier identifier = TexturedModel.FLOWERBED_1.upload(flowerbed, this.modelCollector);
		Identifier identifier2 = TexturedModel.FLOWERBED_2.upload(flowerbed, this.modelCollector);
		Identifier identifier3 = TexturedModel.FLOWERBED_3.upload(flowerbed, this.modelCollector);
		Identifier identifier4 = TexturedModel.FLOWERBED_4.upload(flowerbed, this.modelCollector);
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(flowerbed)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 1, 2, 3, 4).set(Properties.HORIZONTAL_FACING, Direction.NORTH),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 1, 2, 3, 4).set(Properties.HORIZONTAL_FACING, Direction.EAST),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 1, 2, 3, 4).set(Properties.HORIZONTAL_FACING, Direction.SOUTH),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 1, 2, 3, 4).set(Properties.HORIZONTAL_FACING, Direction.WEST),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 2, 3, 4).set(Properties.HORIZONTAL_FACING, Direction.NORTH),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier2)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 2, 3, 4).set(Properties.HORIZONTAL_FACING, Direction.EAST),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 2, 3, 4).set(Properties.HORIZONTAL_FACING, Direction.SOUTH),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 2, 3, 4).set(Properties.HORIZONTAL_FACING, Direction.WEST),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 3, 4).set(Properties.HORIZONTAL_FACING, Direction.NORTH),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 3, 4).set(Properties.HORIZONTAL_FACING, Direction.EAST),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 3, 4).set(Properties.HORIZONTAL_FACING, Direction.SOUTH),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 3, 4).set(Properties.HORIZONTAL_FACING, Direction.WEST),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 4).set(Properties.HORIZONTAL_FACING, Direction.NORTH),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier4)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 4).set(Properties.HORIZONTAL_FACING, Direction.EAST),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 4).set(Properties.HORIZONTAL_FACING, Direction.SOUTH),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.with(
						When.create().set(Properties.FLOWER_AMOUNT, 4).set(Properties.HORIZONTAL_FACING, Direction.WEST),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
			);
	}

	private void registerRandomHorizontalRotations(TexturedModel.Factory modelFactory, Block... blocks) {
		for (Block block : blocks) {
			Identifier identifier = modelFactory.upload(block, this.modelCollector);
			this.blockStateCollector.accept(createBlockStateWithRandomHorizontalRotations(block, identifier));
		}
	}

	private void registerSouthDefaultHorizontalFacing(TexturedModel.Factory modelFactory, Block... blocks) {
		for (Block block : blocks) {
			Identifier identifier = modelFactory.upload(block, this.modelCollector);
			this.blockStateCollector
				.accept(
					VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
						.coordinate(createSouthDefaultHorizontalRotationStates())
				);
		}
	}

	private void registerGlassPane(Block glass, Block glassPane) {
		this.registerSimpleCubeAll(glass);
		TextureMap textureMap = TextureMap.paneAndTopForEdge(glass, glassPane);
		Identifier identifier = Models.TEMPLATE_GLASS_PANE_POST.upload(glassPane, textureMap, this.modelCollector);
		Identifier identifier2 = Models.TEMPLATE_GLASS_PANE_SIDE.upload(glassPane, textureMap, this.modelCollector);
		Identifier identifier3 = Models.TEMPLATE_GLASS_PANE_SIDE_ALT.upload(glassPane, textureMap, this.modelCollector);
		Identifier identifier4 = Models.TEMPLATE_GLASS_PANE_NOSIDE.upload(glassPane, textureMap, this.modelCollector);
		Identifier identifier5 = Models.TEMPLATE_GLASS_PANE_NOSIDE_ALT.upload(glassPane, textureMap, this.modelCollector);
		Item item = glassPane.asItem();
		Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(glass), this.modelCollector);
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(glassPane)
					.with(BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.with(
						When.create().set(Properties.EAST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.with(When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
					.with(
						When.create().set(Properties.WEST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.with(When.create().set(Properties.NORTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier4))
					.with(When.create().set(Properties.EAST, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier5))
					.with(
						When.create().set(Properties.SOUTH, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier5).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.with(
						When.create().set(Properties.WEST, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
			);
	}

	private void registerCommandBlock(Block commandBlock) {
		TextureMap textureMap = TextureMap.sideFrontBack(commandBlock);
		Identifier identifier = Models.TEMPLATE_COMMAND_BLOCK.upload(commandBlock, textureMap, this.modelCollector);
		Identifier identifier2 = this.createSubModel(commandBlock, "_conditional", Models.TEMPLATE_COMMAND_BLOCK, id -> textureMap.copyAndAdd(TextureKey.SIDE, id));
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(commandBlock)
					.coordinate(createBooleanModelMap(Properties.CONDITIONAL, identifier2, identifier))
					.coordinate(createNorthDefaultRotationStates())
			);
	}

	private void registerAnvil(Block anvil) {
		Identifier identifier = TexturedModel.TEMPLATE_ANVIL.upload(anvil, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(anvil, identifier).coordinate(createSouthDefaultHorizontalRotationStates()));
	}

	private List<BlockStateVariant> getBambooBlockStateVariants(int age) {
		String string = "_age" + age;
		return (List<BlockStateVariant>)IntStream.range(1, 5)
			.mapToObj(i -> BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.BAMBOO, i + string)))
			.collect(Collectors.toList());
	}

	private void registerBamboo() {
		this.excludeFromSimpleItemModelGeneration(Blocks.BAMBOO);
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.BAMBOO)
					.with(When.create().set(Properties.AGE_1, 0), this.getBambooBlockStateVariants(0))
					.with(When.create().set(Properties.AGE_1, 1), this.getBambooBlockStateVariants(1))
					.with(
						When.create().set(Properties.BAMBOO_LEAVES, BambooLeaves.SMALL),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.BAMBOO, "_small_leaves"))
					)
					.with(
						When.create().set(Properties.BAMBOO_LEAVES, BambooLeaves.LARGE),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.BAMBOO, "_large_leaves"))
					)
			);
	}

	private BlockStateVariantMap createUpDefaultFacingVariantMap() {
		return BlockStateVariantMap.create(Properties.FACING)
			.register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
			.register(Direction.UP, BlockStateVariant.create())
			.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
			.register(
				Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R180)
			)
			.register(
				Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270)
			)
			.register(
				Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90)
			);
	}

	private void registerBarrel() {
		Identifier identifier = TextureMap.getSubId(Blocks.BARREL, "_top_open");
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.BARREL)
					.coordinate(this.createUpDefaultFacingVariantMap())
					.coordinate(
						BlockStateVariantMap.create(Properties.OPEN)
							.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, TexturedModel.CUBE_BOTTOM_TOP.upload(Blocks.BARREL, this.modelCollector)))
							.register(
								true,
								BlockStateVariant.create()
									.put(
										VariantSettings.MODEL,
										TexturedModel.CUBE_BOTTOM_TOP
											.get(Blocks.BARREL)
											.textures(textureMap -> textureMap.put(TextureKey.TOP, identifier))
											.upload(Blocks.BARREL, "_open", this.modelCollector)
									)
							)
					)
			);
	}

	private static <T extends Comparable<T>> BlockStateVariantMap createValueFencedModelMap(
		Property<T> property, T fence, Identifier higherOrEqualModelId, Identifier lowerModelId
	) {
		BlockStateVariant blockStateVariant = BlockStateVariant.create().put(VariantSettings.MODEL, higherOrEqualModelId);
		BlockStateVariant blockStateVariant2 = BlockStateVariant.create().put(VariantSettings.MODEL, lowerModelId);
		return BlockStateVariantMap.create(property).register(comparable2 -> {
			boolean bl = comparable2.compareTo(fence) >= 0;
			return bl ? blockStateVariant : blockStateVariant2;
		});
	}

	private void registerBeehive(Block beehive, Function<Block, TextureMap> texturesFactory) {
		TextureMap textureMap = ((TextureMap)texturesFactory.apply(beehive)).inherit(TextureKey.SIDE, TextureKey.PARTICLE);
		TextureMap textureMap2 = textureMap.copyAndAdd(TextureKey.FRONT, TextureMap.getSubId(beehive, "_front_honey"));
		Identifier identifier = Models.ORIENTABLE_WITH_BOTTOM.upload(beehive, textureMap, this.modelCollector);
		Identifier identifier2 = Models.ORIENTABLE_WITH_BOTTOM.upload(beehive, "_honey", textureMap2, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(beehive)
					.coordinate(createNorthDefaultHorizontalRotationStates())
					.coordinate(createValueFencedModelMap(Properties.HONEY_LEVEL, 5, identifier2, identifier))
			);
	}

	private void registerCrop(Block crop, Property<Integer> ageProperty, int... ageTextureIndices) {
		if (ageProperty.getValues().size() != ageTextureIndices.length) {
			throw new IllegalArgumentException();
		} else {
			Int2ObjectMap<Identifier> int2ObjectMap = new Int2ObjectOpenHashMap<>();
			BlockStateVariantMap blockStateVariantMap = BlockStateVariantMap.create(ageProperty)
				.register(
					integer -> {
						int i = ageTextureIndices[integer];
						Identifier identifier = int2ObjectMap.computeIfAbsent(
							i, (Int2ObjectFunction<? extends Identifier>)(j -> this.createSubModel(crop, "_stage" + i, Models.CROP, TextureMap::crop))
						);
						return BlockStateVariant.create().put(VariantSettings.MODEL, identifier);
					}
				);
			this.registerItemModel(crop.asItem());
			this.blockStateCollector.accept(VariantsBlockStateSupplier.create(crop).coordinate(blockStateVariantMap));
		}
	}

	private void registerBell() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.BELL, "_floor");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.BELL, "_ceiling");
		Identifier identifier3 = ModelIds.getBlockSubModelId(Blocks.BELL, "_wall");
		Identifier identifier4 = ModelIds.getBlockSubModelId(Blocks.BELL, "_between_walls");
		this.registerItemModel(Items.BELL);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.BELL)
					.coordinate(
						BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.ATTACHMENT)
							.register(Direction.NORTH, Attachment.FLOOR, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(
								Direction.SOUTH,
								Attachment.FLOOR,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								Direction.EAST,
								Attachment.FLOOR,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								Direction.WEST,
								Attachment.FLOOR,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(Direction.NORTH, Attachment.CEILING, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
							.register(
								Direction.SOUTH,
								Attachment.CEILING,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								Direction.EAST,
								Attachment.CEILING,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								Direction.WEST,
								Attachment.CEILING,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(
								Direction.NORTH,
								Attachment.SINGLE_WALL,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(
								Direction.SOUTH,
								Attachment.SINGLE_WALL,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(Direction.EAST, Attachment.SINGLE_WALL, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
							.register(
								Direction.WEST,
								Attachment.SINGLE_WALL,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								Direction.SOUTH,
								Attachment.DOUBLE_WALL,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								Direction.NORTH,
								Attachment.DOUBLE_WALL,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(Direction.EAST, Attachment.DOUBLE_WALL, BlockStateVariant.create().put(VariantSettings.MODEL, identifier4))
							.register(
								Direction.WEST,
								Attachment.DOUBLE_WALL,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
					)
			);
	}

	private void registerGrindstone() {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.GRINDSTONE, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.GRINDSTONE)))
					.coordinate(
						BlockStateVariantMap.create(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING)
							.register(WallMountLocation.FLOOR, Direction.NORTH, BlockStateVariant.create())
							.register(WallMountLocation.FLOOR, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
							.register(WallMountLocation.FLOOR, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
							.register(WallMountLocation.FLOOR, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
							.register(WallMountLocation.WALL, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
							.register(
								WallMountLocation.WALL,
								Direction.EAST,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								WallMountLocation.WALL,
								Direction.SOUTH,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								WallMountLocation.WALL,
								Direction.WEST,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(WallMountLocation.CEILING, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
							.register(
								WallMountLocation.CEILING,
								Direction.WEST,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								WallMountLocation.CEILING,
								Direction.NORTH,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								WallMountLocation.CEILING,
								Direction.EAST,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
					)
			);
	}

	private void registerCooker(Block cooker, TexturedModel.Factory modelFactory) {
		Identifier identifier = modelFactory.upload(cooker, this.modelCollector);
		Identifier identifier2 = TextureMap.getSubId(cooker, "_front_on");
		Identifier identifier3 = modelFactory.get(cooker)
			.textures(textures -> textures.put(TextureKey.FRONT, identifier2))
			.upload(cooker, "_on", this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(cooker)
					.coordinate(createBooleanModelMap(Properties.LIT, identifier3, identifier))
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
	}

	private void registerCampfire(Block... blocks) {
		Identifier identifier = ModelIds.getMinecraftNamespacedBlock("campfire_off");

		for (Block block : blocks) {
			Identifier identifier2 = Models.TEMPLATE_CAMPFIRE.upload(block, TextureMap.campfire(block), this.modelCollector);
			this.registerItemModel(block.asItem());
			this.blockStateCollector
				.accept(
					VariantsBlockStateSupplier.create(block)
						.coordinate(createBooleanModelMap(Properties.LIT, identifier2, identifier))
						.coordinate(createSouthDefaultHorizontalRotationStates())
				);
		}
	}

	private void registerAzalea(Block block) {
		Identifier identifier = Models.TEMPLATE_AZALEA.upload(block, TextureMap.sideAndTop(block), this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(block, identifier));
	}

	private void registerPottedAzaleaBush(Block block) {
		Identifier identifier;
		if (block == Blocks.POTTED_FLOWERING_AZALEA_BUSH) {
			identifier = Models.TEMPLATE_POTTED_FLOWERING_AZALEA_BUSH.upload(block, TextureMap.pottedAzaleaBush(block), this.modelCollector);
		} else {
			identifier = Models.TEMPLATE_POTTED_AZALEA_BUSH.upload(block, TextureMap.pottedAzaleaBush(block), this.modelCollector);
		}

		this.blockStateCollector.accept(createSingletonBlockState(block, identifier));
	}

	private void registerBookshelf() {
		TextureMap textureMap = TextureMap.sideEnd(TextureMap.getId(Blocks.BOOKSHELF), TextureMap.getId(Blocks.OAK_PLANKS));
		Identifier identifier = Models.CUBE_COLUMN.upload(Blocks.BOOKSHELF, textureMap, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(Blocks.BOOKSHELF, identifier));
	}

	private void registerPackedAir() {
		Identifier identifier = Models.CUBE_ALL.upload(Blocks.PACKED_AIR, TextureMap.all(TextureMap.getId(Blocks.PACKED_AIR)), this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(Blocks.PACKED_AIR, identifier));
	}

	private void registerRedstone() {
		this.registerItemModel(Items.REDSTONE);
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.REDSTONE_WIRE)
					.with(
						When.anyOf(
							When.create()
								.set(Properties.NORTH_WIRE_CONNECTION, WireConnection.NONE)
								.set(Properties.EAST_WIRE_CONNECTION, WireConnection.NONE)
								.set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.NONE)
								.set(Properties.WEST_WIRE_CONNECTION, WireConnection.NONE),
							When.create()
								.set(Properties.NORTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP)
								.set(Properties.EAST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
							When.create()
								.set(Properties.EAST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP)
								.set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
							When.create()
								.set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP)
								.set(Properties.WEST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
							When.create()
								.set(Properties.WEST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP)
								.set(Properties.NORTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP)
						),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_dot"))
					)
					.with(
						When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side0"))
					)
					.with(
						When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt0"))
					)
					.with(
						When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt1"))
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.with(
						When.create().set(Properties.WEST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side1"))
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.with(
						When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.UP),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up"))
					)
					.with(
						When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.UP),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up"))
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.with(
						When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.UP),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up"))
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.with(
						When.create().set(Properties.WEST_WIRE_CONNECTION, WireConnection.UP),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up"))
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
			);
	}

	private void registerComparator() {
		this.registerItemModel(Items.COMPARATOR);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.COMPARATOR)
					.coordinate(createSouthDefaultHorizontalRotationStates())
					.coordinate(
						BlockStateVariantMap.create(Properties.COMPARATOR_MODE, Properties.POWERED)
							.register(ComparatorMode.COMPARE, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.COMPARATOR)))
							.register(ComparatorMode.COMPARE, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COMPARATOR, "_on")))
							.register(
								ComparatorMode.SUBTRACT, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COMPARATOR, "_subtract"))
							)
							.register(
								ComparatorMode.SUBTRACT, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COMPARATOR, "_on_subtract"))
							)
					)
			);
	}

	private void registerSmoothStone() {
		TextureMap textureMap = TextureMap.all(Blocks.SMOOTH_STONE);
		TextureMap textureMap2 = TextureMap.sideEnd(TextureMap.getSubId(Blocks.SMOOTH_STONE_SLAB, "_side"), textureMap.getTexture(TextureKey.TOP));
		Identifier identifier = Models.SLAB.upload(Blocks.SMOOTH_STONE_SLAB, textureMap2, this.modelCollector);
		Identifier identifier2 = Models.SLAB_TOP.upload(Blocks.SMOOTH_STONE_SLAB, textureMap2, this.modelCollector);
		Identifier identifier3 = Models.CUBE_COLUMN.uploadWithoutVariant(Blocks.SMOOTH_STONE_SLAB, "_double", textureMap2, this.modelCollector);
		this.blockStateCollector.accept(createSlabBlockState(Blocks.SMOOTH_STONE_SLAB, identifier, identifier2, identifier3));
		this.blockStateCollector.accept(createSingletonBlockState(Blocks.SMOOTH_STONE, Models.CUBE_ALL.upload(Blocks.SMOOTH_STONE, textureMap, this.modelCollector)));
	}

	private void registerBrewingStand() {
		this.registerItemModel(Items.BREWING_STAND);
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.BREWING_STAND)
					.with(BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getId(Blocks.BREWING_STAND)))
					.with(
						When.create().set(Properties.HAS_BOTTLE_0, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.BREWING_STAND, "_bottle0"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_1, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.BREWING_STAND, "_bottle1"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_2, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.BREWING_STAND, "_bottle2"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_0, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.BREWING_STAND, "_empty0"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_1, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.BREWING_STAND, "_empty1"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_2, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.BREWING_STAND, "_empty2"))
					)
			);
	}

	private void registerMushroomBlock(Block mushroomBlock) {
		Identifier identifier = Models.TEMPLATE_SINGLE_FACE.upload(mushroomBlock, TextureMap.texture(mushroomBlock), this.modelCollector);
		Identifier identifier2 = ModelIds.getMinecraftNamespacedBlock("mushroom_block_inside");
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(mushroomBlock)
					.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.with(
						When.create().set(Properties.EAST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.SOUTH, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.WEST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.UP, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.DOWN, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
					)
					.with(When.create().set(Properties.NORTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.with(
						When.create().set(Properties.EAST, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, false)
					)
					.with(
						When.create().set(Properties.SOUTH, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, false)
					)
					.with(
						When.create().set(Properties.WEST, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, false)
					)
					.with(
						When.create().set(Properties.UP, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.X, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, false)
					)
					.with(
						When.create().set(Properties.DOWN, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.X, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, false)
					)
			);
		this.registerParentedItemModel(mushroomBlock, TexturedModel.CUBE_ALL.upload(mushroomBlock, "_inventory", this.modelCollector));
	}

	private void registerCake() {
		this.registerItemModel(Items.CAKE);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.CAKE)
					.coordinate(
						BlockStateVariantMap.create(Properties.BITES)
							.register(0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.CAKE)))
							.register(1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice1")))
							.register(2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice2")))
							.register(3, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice3")))
							.register(4, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice4")))
							.register(5, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice5")))
							.register(6, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice6")))
					)
			);
	}

	private void registerCartographyTable() {
		TextureMap textureMap = new TextureMap()
			.put(TextureKey.PARTICLE, TextureMap.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side3"))
			.put(TextureKey.DOWN, TextureMap.getId(Blocks.DARK_OAK_PLANKS))
			.put(TextureKey.UP, TextureMap.getSubId(Blocks.CARTOGRAPHY_TABLE, "_top"))
			.put(TextureKey.NORTH, TextureMap.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side3"))
			.put(TextureKey.EAST, TextureMap.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side3"))
			.put(TextureKey.SOUTH, TextureMap.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side1"))
			.put(TextureKey.WEST, TextureMap.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side2"));
		this.blockStateCollector
			.accept(createSingletonBlockState(Blocks.CARTOGRAPHY_TABLE, Models.CUBE.upload(Blocks.CARTOGRAPHY_TABLE, textureMap, this.modelCollector)));
	}

	private void registerSmithingTable() {
		TextureMap textureMap = new TextureMap()
			.put(TextureKey.PARTICLE, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_front"))
			.put(TextureKey.DOWN, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_bottom"))
			.put(TextureKey.UP, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_top"))
			.put(TextureKey.NORTH, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_front"))
			.put(TextureKey.SOUTH, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_front"))
			.put(TextureKey.EAST, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_side"))
			.put(TextureKey.WEST, TextureMap.getSubId(Blocks.SMITHING_TABLE, "_side"));
		this.blockStateCollector.accept(createSingletonBlockState(Blocks.SMITHING_TABLE, Models.CUBE.upload(Blocks.SMITHING_TABLE, textureMap, this.modelCollector)));
	}

	private void registerCubeWithCustomTextures(Block block, Block otherTextureSource, BiFunction<Block, Block, TextureMap> texturesFactory) {
		TextureMap textureMap = (TextureMap)texturesFactory.apply(block, otherTextureSource);
		this.blockStateCollector.accept(createSingletonBlockState(block, Models.CUBE.upload(block, textureMap, this.modelCollector)));
	}

	private void registerPumpkins() {
		TextureMap textureMap = TextureMap.sideEnd(Blocks.PUMPKIN);
		this.blockStateCollector.accept(createSingletonBlockState(Blocks.PUMPKIN, ModelIds.getBlockModelId(Blocks.PUMPKIN)));
		this.registerNorthDefaultHorizontalRotatable(Blocks.CARVED_PUMPKIN, textureMap);
		this.registerNorthDefaultHorizontalRotatable(Blocks.JACK_O_LANTERN, textureMap);
	}

	private void registerNorthDefaultHorizontalRotatable(Block block, TextureMap texture) {
		Identifier identifier = Models.ORIENTABLE.upload(block, texture.copyAndAdd(TextureKey.FRONT, TextureMap.getId(block)), this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
	}

	private void registerCauldrons() {
		this.registerItemModel(Items.CAULDRON);
		this.registerSimpleState(Blocks.CAULDRON);
		this.blockStateCollector
			.accept(
				createSingletonBlockState(
					Blocks.LAVA_CAULDRON,
					Models.TEMPLATE_CAULDRON_FULL.upload(Blocks.LAVA_CAULDRON, TextureMap.cauldron(TextureMap.getSubId(Blocks.LAVA, "_still")), this.modelCollector)
				)
			);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.WATER_CAULDRON)
					.coordinate(
						BlockStateVariantMap.create(LeveledCauldronBlock.LEVEL)
							.register(
								1,
								BlockStateVariant.create()
									.put(
										VariantSettings.MODEL,
										Models.TEMPLATE_CAULDRON_LEVEL1
											.upload(Blocks.WATER_CAULDRON, "_level1", TextureMap.cauldron(TextureMap.getSubId(Blocks.WATER, "_still")), this.modelCollector)
									)
							)
							.register(
								2,
								BlockStateVariant.create()
									.put(
										VariantSettings.MODEL,
										Models.TEMPLATE_CAULDRON_LEVEL2
											.upload(Blocks.WATER_CAULDRON, "_level2", TextureMap.cauldron(TextureMap.getSubId(Blocks.WATER, "_still")), this.modelCollector)
									)
							)
							.register(
								3,
								BlockStateVariant.create()
									.put(
										VariantSettings.MODEL,
										Models.TEMPLATE_CAULDRON_FULL
											.upload(Blocks.WATER_CAULDRON, "_full", TextureMap.cauldron(TextureMap.getSubId(Blocks.WATER, "_still")), this.modelCollector)
									)
							)
					)
			);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.POWDER_SNOW_CAULDRON)
					.coordinate(
						BlockStateVariantMap.create(LeveledCauldronBlock.LEVEL)
							.register(
								1,
								BlockStateVariant.create()
									.put(
										VariantSettings.MODEL,
										Models.TEMPLATE_CAULDRON_LEVEL1
											.upload(Blocks.POWDER_SNOW_CAULDRON, "_level1", TextureMap.cauldron(TextureMap.getId(Blocks.POWDER_SNOW)), this.modelCollector)
									)
							)
							.register(
								2,
								BlockStateVariant.create()
									.put(
										VariantSettings.MODEL,
										Models.TEMPLATE_CAULDRON_LEVEL2
											.upload(Blocks.POWDER_SNOW_CAULDRON, "_level2", TextureMap.cauldron(TextureMap.getId(Blocks.POWDER_SNOW)), this.modelCollector)
									)
							)
							.register(
								3,
								BlockStateVariant.create()
									.put(
										VariantSettings.MODEL,
										Models.TEMPLATE_CAULDRON_FULL
											.upload(Blocks.POWDER_SNOW_CAULDRON, "_full", TextureMap.cauldron(TextureMap.getId(Blocks.POWDER_SNOW)), this.modelCollector)
									)
							)
					)
			);
		this.registerSimpleState(Blocks.COPPER_SINK);
		this.registerParentedItemModel(Blocks.COPPER_SINK, ModelIds.getBlockModelId(Blocks.COPPER_SINK));
		this.blockStateCollector
			.accept(
				createSingletonBlockState(
					Blocks.FILLED_COPPER_SINK,
					Models.TEMPLATE_COPPER_SINK_FULL
						.upload(Blocks.FILLED_COPPER_SINK, TextureMap.method_51102(TextureMap.getSubId(Blocks.WATER, "_still")), this.modelCollector)
				)
			);
	}

	private void registerChorusFlower() {
		TextureMap textureMap = TextureMap.texture(Blocks.CHORUS_FLOWER);
		Identifier identifier = Models.TEMPLATE_CHORUS_FLOWER.upload(Blocks.CHORUS_FLOWER, textureMap, this.modelCollector);
		Identifier identifier2 = this.createSubModel(
			Blocks.CHORUS_FLOWER, "_dead", Models.TEMPLATE_CHORUS_FLOWER, id -> textureMap.copyAndAdd(TextureKey.TEXTURE, id)
		);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(Blocks.CHORUS_FLOWER).coordinate(createValueFencedModelMap(Properties.AGE_5, 5, identifier2, identifier)));
	}

	private void registerDispenserLikeOrientable(Block block) {
		TextureMap textureMap = new TextureMap()
			.put(TextureKey.TOP, TextureMap.getSubId(Blocks.FURNACE, "_top"))
			.put(TextureKey.SIDE, TextureMap.getSubId(Blocks.FURNACE, "_side"))
			.put(TextureKey.FRONT, TextureMap.getSubId(block, "_front"));
		TextureMap textureMap2 = new TextureMap()
			.put(TextureKey.SIDE, TextureMap.getSubId(Blocks.FURNACE, "_top"))
			.put(TextureKey.FRONT, TextureMap.getSubId(block, "_front_vertical"));
		Identifier identifier = Models.ORIENTABLE.upload(block, textureMap, this.modelCollector);
		Identifier identifier2 = Models.ORIENTABLE_VERTICAL.upload(block, textureMap2, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block)
					.coordinate(
						BlockStateVariantMap.create(Properties.FACING)
							.register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.X, VariantSettings.Rotation.R180))
							.register(Direction.UP, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
							.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90))
							.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180))
							.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R270))
					)
			);
	}

	private void registerEndPortalFrame() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.END_PORTAL_FRAME);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.END_PORTAL_FRAME, "_filled");
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.END_PORTAL_FRAME)
					.coordinate(
						BlockStateVariantMap.create(Properties.EYE)
							.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					)
					.coordinate(createSouthDefaultHorizontalRotationStates())
			);
	}

	private void registerChorusPlant() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_side");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside");
		Identifier identifier3 = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside1");
		Identifier identifier4 = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside2");
		Identifier identifier5 = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside3");
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.CHORUS_PLANT)
					.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.with(
						When.create().set(Properties.EAST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.SOUTH, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.WEST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.UP, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.DOWN, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.NORTH, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.WEIGHT, 2),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier4),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier5)
					)
					.with(
						When.create().set(Properties.EAST, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier5).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.WEIGHT, 2)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.SOUTH, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier4)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier5)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.WEIGHT, 2)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.WEST, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier5)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.WEIGHT, 2)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier4)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.UP, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.WEIGHT, 2)
							.put(VariantSettings.X, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier5)
							.put(VariantSettings.X, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier4)
							.put(VariantSettings.X, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.DOWN, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier5).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.WEIGHT, 2)
							.put(VariantSettings.X, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
			);
	}

	private void registerComposter() {
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.COMPOSTER)
					.with(BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getId(Blocks.COMPOSTER)))
					.with(When.create().set(Properties.LEVEL_8, 1), BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.COMPOSTER, "_contents1")))
					.with(When.create().set(Properties.LEVEL_8, 2), BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.COMPOSTER, "_contents2")))
					.with(When.create().set(Properties.LEVEL_8, 3), BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.COMPOSTER, "_contents3")))
					.with(When.create().set(Properties.LEVEL_8, 4), BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.COMPOSTER, "_contents4")))
					.with(When.create().set(Properties.LEVEL_8, 5), BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.COMPOSTER, "_contents5")))
					.with(When.create().set(Properties.LEVEL_8, 6), BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.COMPOSTER, "_contents6")))
					.with(When.create().set(Properties.LEVEL_8, 7), BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.COMPOSTER, "_contents7")))
					.with(
						When.create().set(Properties.LEVEL_8, 8), BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.COMPOSTER, "_contents_ready"))
					)
			);
	}

	private void registerAmethyst(Block block) {
		this.excludeFromSimpleItemModelGeneration(block);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(
						block, BlockStateVariant.create().put(VariantSettings.MODEL, Models.CROSS.upload(block, TextureMap.cross(block), this.modelCollector))
					)
					.coordinate(this.createUpDefaultFacingVariantMap())
			);
	}

	private void registerAmethysts() {
		this.registerAmethyst(Blocks.SMALL_AMETHYST_BUD);
		this.registerAmethyst(Blocks.MEDIUM_AMETHYST_BUD);
		this.registerAmethyst(Blocks.LARGE_AMETHYST_BUD);
		this.registerAmethyst(Blocks.AMETHYST_CLUSTER);
	}

	private void registerPointedDripstone() {
		this.excludeFromSimpleItemModelGeneration(Blocks.POINTED_DRIPSTONE);
		BlockStateVariantMap.DoubleProperty<Direction, Thickness> doubleProperty = BlockStateVariantMap.create(Properties.VERTICAL_DIRECTION, Properties.THICKNESS);

		for (Thickness thickness : Thickness.values()) {
			doubleProperty.register(Direction.UP, thickness, this.getDripstoneVariant(Direction.UP, thickness));
		}

		for (Thickness thickness : Thickness.values()) {
			doubleProperty.register(Direction.DOWN, thickness, this.getDripstoneVariant(Direction.DOWN, thickness));
		}

		this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.POINTED_DRIPSTONE).coordinate(doubleProperty));
	}

	private BlockStateVariant getDripstoneVariant(Direction direction, Thickness thickness) {
		String string = "_" + direction.asString() + "_" + thickness.asString();
		TextureMap textureMap = TextureMap.cross(TextureMap.getSubId(Blocks.POINTED_DRIPSTONE, string));
		return BlockStateVariant.create()
			.put(VariantSettings.MODEL, Models.POINTED_DRIPSTONE.upload(Blocks.POINTED_DRIPSTONE, string, textureMap, this.modelCollector));
	}

	private void registerNetherrackBottomCustomTop(Block block) {
		TextureMap textureMap = new TextureMap()
			.put(TextureKey.BOTTOM, TextureMap.getId(Blocks.NETHERRACK))
			.put(TextureKey.TOP, TextureMap.getId(block))
			.put(TextureKey.SIDE, TextureMap.getSubId(block, "_side"));
		this.blockStateCollector.accept(createSingletonBlockState(block, Models.CUBE_BOTTOM_TOP.upload(block, textureMap, this.modelCollector)));
	}

	private void registerDaylightDetector() {
		Identifier identifier = TextureMap.getSubId(Blocks.DAYLIGHT_DETECTOR, "_side");
		TextureMap textureMap = new TextureMap().put(TextureKey.TOP, TextureMap.getSubId(Blocks.DAYLIGHT_DETECTOR, "_top")).put(TextureKey.SIDE, identifier);
		TextureMap textureMap2 = new TextureMap()
			.put(TextureKey.TOP, TextureMap.getSubId(Blocks.DAYLIGHT_DETECTOR, "_inverted_top"))
			.put(TextureKey.SIDE, identifier);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.DAYLIGHT_DETECTOR)
					.coordinate(
						BlockStateVariantMap.create(Properties.INVERTED)
							.register(
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, Models.TEMPLATE_DAYLIGHT_DETECTOR.upload(Blocks.DAYLIGHT_DETECTOR, textureMap, this.modelCollector))
							)
							.register(
								true,
								BlockStateVariant.create()
									.put(
										VariantSettings.MODEL,
										Models.TEMPLATE_DAYLIGHT_DETECTOR.upload(ModelIds.getBlockSubModelId(Blocks.DAYLIGHT_DETECTOR, "_inverted"), textureMap2, this.modelCollector)
									)
							)
					)
			);
	}

	private void registerRod(Block block) {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(block)))
					.coordinate(this.createUpDefaultFacingVariantMap())
			);
	}

	private void registerLightningRod() {
		Block block = Blocks.LIGHTNING_ROD;
		Identifier identifier = ModelIds.getBlockSubModelId(block, "_on");
		Identifier identifier2 = ModelIds.getBlockModelId(block);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(block)))
					.coordinate(this.createUpDefaultFacingVariantMap())
					.coordinate(createBooleanModelMap(Properties.POWERED, identifier, identifier2))
			);
	}

	private void registerFarmland() {
		TextureMap textureMap = new TextureMap().put(TextureKey.DIRT, TextureMap.getId(Blocks.DIRT)).put(TextureKey.TOP, TextureMap.getId(Blocks.FARMLAND));
		TextureMap textureMap2 = new TextureMap()
			.put(TextureKey.DIRT, TextureMap.getId(Blocks.DIRT))
			.put(TextureKey.TOP, TextureMap.getSubId(Blocks.FARMLAND, "_moist"));
		Identifier identifier = Models.TEMPLATE_FARMLAND.upload(Blocks.FARMLAND, textureMap, this.modelCollector);
		Identifier identifier2 = Models.TEMPLATE_FARMLAND.upload(TextureMap.getSubId(Blocks.FARMLAND, "_moist"), textureMap2, this.modelCollector);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(Blocks.FARMLAND).coordinate(createValueFencedModelMap(Properties.MOISTURE, 7, identifier2, identifier)));
	}

	private List<Identifier> getFireFloorModels(Block texture) {
		Identifier identifier = Models.TEMPLATE_FIRE_FLOOR.upload(ModelIds.getBlockSubModelId(texture, "_floor0"), TextureMap.fire0(texture), this.modelCollector);
		Identifier identifier2 = Models.TEMPLATE_FIRE_FLOOR.upload(ModelIds.getBlockSubModelId(texture, "_floor1"), TextureMap.fire1(texture), this.modelCollector);
		return ImmutableList.of(identifier, identifier2);
	}

	private List<Identifier> getFireSideModels(Block texture) {
		Identifier identifier = Models.TEMPLATE_FIRE_SIDE.upload(ModelIds.getBlockSubModelId(texture, "_side0"), TextureMap.fire0(texture), this.modelCollector);
		Identifier identifier2 = Models.TEMPLATE_FIRE_SIDE.upload(ModelIds.getBlockSubModelId(texture, "_side1"), TextureMap.fire1(texture), this.modelCollector);
		Identifier identifier3 = Models.TEMPLATE_FIRE_SIDE_ALT
			.upload(ModelIds.getBlockSubModelId(texture, "_side_alt0"), TextureMap.fire0(texture), this.modelCollector);
		Identifier identifier4 = Models.TEMPLATE_FIRE_SIDE_ALT
			.upload(ModelIds.getBlockSubModelId(texture, "_side_alt1"), TextureMap.fire1(texture), this.modelCollector);
		return ImmutableList.of(identifier, identifier2, identifier3, identifier4);
	}

	private List<Identifier> getFireUpModels(Block texture) {
		Identifier identifier = Models.TEMPLATE_FIRE_UP.upload(ModelIds.getBlockSubModelId(texture, "_up0"), TextureMap.fire0(texture), this.modelCollector);
		Identifier identifier2 = Models.TEMPLATE_FIRE_UP.upload(ModelIds.getBlockSubModelId(texture, "_up1"), TextureMap.fire1(texture), this.modelCollector);
		Identifier identifier3 = Models.TEMPLATE_FIRE_UP_ALT.upload(ModelIds.getBlockSubModelId(texture, "_up_alt0"), TextureMap.fire0(texture), this.modelCollector);
		Identifier identifier4 = Models.TEMPLATE_FIRE_UP_ALT.upload(ModelIds.getBlockSubModelId(texture, "_up_alt1"), TextureMap.fire1(texture), this.modelCollector);
		return ImmutableList.of(identifier, identifier2, identifier3, identifier4);
	}

	private static List<BlockStateVariant> buildBlockStateVariants(List<Identifier> modelIds, UnaryOperator<BlockStateVariant> processor) {
		return (List<BlockStateVariant>)modelIds.stream()
			.map(modelId -> BlockStateVariant.create().put(VariantSettings.MODEL, modelId))
			.map(processor)
			.collect(Collectors.toList());
	}

	private void registerFire() {
		When when = When.create()
			.set(Properties.NORTH, false)
			.set(Properties.EAST, false)
			.set(Properties.SOUTH, false)
			.set(Properties.WEST, false)
			.set(Properties.UP, false);
		List<Identifier> list = this.getFireFloorModels(Blocks.FIRE);
		List<Identifier> list2 = this.getFireSideModels(Blocks.FIRE);
		List<Identifier> list3 = this.getFireUpModels(Blocks.FIRE);
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.FIRE)
					.with(when, buildBlockStateVariants(list, blockStateVariant -> blockStateVariant))
					.with(When.anyOf(When.create().set(Properties.NORTH, true), when), buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant))
					.with(
						When.anyOf(When.create().set(Properties.EAST, true), when),
						buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90))
					)
					.with(
						When.anyOf(When.create().set(Properties.SOUTH, true), when),
						buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180))
					)
					.with(
						When.anyOf(When.create().set(Properties.WEST, true), when),
						buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270))
					)
					.with(When.create().set(Properties.UP, true), buildBlockStateVariants(list3, blockStateVariant -> blockStateVariant))
			);
	}

	private void registerSoulFire() {
		List<Identifier> list = this.getFireFloorModels(Blocks.SOUL_FIRE);
		List<Identifier> list2 = this.getFireSideModels(Blocks.SOUL_FIRE);
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.SOUL_FIRE)
					.with(buildBlockStateVariants(list, blockStateVariant -> blockStateVariant))
					.with(buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant))
					.with(buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90)))
					.with(buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180)))
					.with(buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270)))
			);
	}

	private void registerLantern(Block lantern) {
		Identifier identifier = TexturedModel.TEMPLATE_LANTERN.upload(lantern, this.modelCollector);
		Identifier identifier2 = TexturedModel.TEMPLATE_HANGING_LANTERN.upload(lantern, this.modelCollector);
		this.registerItemModel(lantern.asItem());
		this.blockStateCollector.accept(VariantsBlockStateSupplier.create(lantern).coordinate(createBooleanModelMap(Properties.HANGING, identifier2, identifier)));
	}

	private void registerMuddyMangroveRoots() {
		TextureMap textureMap = TextureMap.sideEnd(
			TextureMap.getSubId(Blocks.MUDDY_MANGROVE_ROOTS, "_side"), TextureMap.getSubId(Blocks.MUDDY_MANGROVE_ROOTS, "_top")
		);
		Identifier identifier = Models.CUBE_COLUMN.upload(Blocks.MUDDY_MANGROVE_ROOTS, textureMap, this.modelCollector);
		this.blockStateCollector.accept(createAxisRotatedBlockState(Blocks.MUDDY_MANGROVE_ROOTS, identifier));
	}

	private void registerMangrovePropagule() {
		this.registerItemModel(Items.MANGROVE_PROPAGULE);
		Block block = Blocks.MANGROVE_PROPAGULE;
		BlockStateVariantMap.DoubleProperty<Boolean, Integer> doubleProperty = BlockStateVariantMap.create(PropaguleBlock.HANGING, PropaguleBlock.AGE);
		Identifier identifier = ModelIds.getBlockModelId(block);

		for (int i = 0; i <= 4; i++) {
			Identifier identifier2 = ModelIds.getBlockSubModelId(block, "_hanging_" + i);
			doubleProperty.register(true, i, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2));
			doubleProperty.register(false, i, BlockStateVariant.create().put(VariantSettings.MODEL, identifier));
		}

		this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.MANGROVE_PROPAGULE).coordinate(doubleProperty));
	}

	private void registerFrostedIce() {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.FROSTED_ICE)
					.coordinate(
						BlockStateVariantMap.create(Properties.AGE_3)
							.register(0, BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.FROSTED_ICE, "_0", Models.CUBE_ALL, TextureMap::all)))
							.register(1, BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.FROSTED_ICE, "_1", Models.CUBE_ALL, TextureMap::all)))
							.register(2, BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.FROSTED_ICE, "_2", Models.CUBE_ALL, TextureMap::all)))
							.register(3, BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.FROSTED_ICE, "_3", Models.CUBE_ALL, TextureMap::all)))
					)
			);
	}

	private void registerTopSoils() {
		Identifier identifier = TextureMap.getId(Blocks.DIRT);
		TextureMap textureMap = new TextureMap()
			.put(TextureKey.BOTTOM, identifier)
			.inherit(TextureKey.BOTTOM, TextureKey.PARTICLE)
			.put(TextureKey.TOP, TextureMap.getSubId(Blocks.GRASS_BLOCK, "_top"))
			.put(TextureKey.SIDE, TextureMap.getSubId(Blocks.GRASS_BLOCK, "_snow"));
		BlockStateVariant blockStateVariant = BlockStateVariant.create()
			.put(VariantSettings.MODEL, Models.CUBE_BOTTOM_TOP.upload(Blocks.GRASS_BLOCK, "_snow", textureMap, this.modelCollector));
		this.registerTopSoil(Blocks.GRASS_BLOCK, ModelIds.getBlockModelId(Blocks.GRASS_BLOCK), blockStateVariant);
		Identifier identifier2 = TexturedModel.CUBE_BOTTOM_TOP
			.get(Blocks.MYCELIUM)
			.textures(textures -> textures.put(TextureKey.BOTTOM, identifier))
			.upload(Blocks.MYCELIUM, this.modelCollector);
		this.registerTopSoil(Blocks.MYCELIUM, identifier2, blockStateVariant);
		Identifier identifier3 = TexturedModel.CUBE_BOTTOM_TOP
			.get(Blocks.PODZOL)
			.textures(textures -> textures.put(TextureKey.BOTTOM, identifier))
			.upload(Blocks.PODZOL, this.modelCollector);
		this.registerTopSoil(Blocks.PODZOL, identifier3, blockStateVariant);
	}

	private void registerTopSoil(Block topSoil, Identifier modelId, BlockStateVariant snowyVariant) {
		List<BlockStateVariant> list = Arrays.asList(createModelVariantWithRandomHorizontalRotations(modelId));
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(topSoil).coordinate(BlockStateVariantMap.create(Properties.SNOWY).register(true, snowyVariant).register(false, list))
			);
	}

	private void registerCocoa() {
		this.registerItemModel(Items.COCOA_BEANS);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.COCOA)
					.coordinate(
						BlockStateVariantMap.create(Properties.AGE_2)
							.register(0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage0")))
							.register(1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage1")))
							.register(2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage2")))
					)
					.coordinate(createSouthDefaultHorizontalRotationStates())
			);
	}

	private void registerGrassPath() {
		this.blockStateCollector.accept(createBlockStateWithRandomHorizontalRotations(Blocks.DIRT_PATH, ModelIds.getBlockModelId(Blocks.DIRT_PATH)));
	}

	private void registerPressurePlate(Block pressurePlate, Block textureSource) {
		TextureMap textureMap = TextureMap.texture(textureSource);
		Identifier identifier = Models.PRESSURE_PLATE_UP.upload(pressurePlate, textureMap, this.modelCollector);
		Identifier identifier2 = Models.PRESSURE_PLATE_DOWN.upload(pressurePlate, textureMap, this.modelCollector);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(pressurePlate).coordinate(createValueFencedModelMap(Properties.POWER, 1, identifier2, identifier)));
	}

	private void registerHopper() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.HOPPER);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.HOPPER, "_side");
		this.registerItemModel(Items.HOPPER);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.HOPPER)
					.coordinate(
						BlockStateVariantMap.create(Properties.HOPPER_FACING)
							.register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
							.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90))
							.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R180))
							.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R270))
					)
			);
	}

	/**
	 * Used for a block that shares a block model with another block, for example waxed copper or infested stone bricks.
	 */
	private void registerParented(Block modelSource, Block child) {
		Identifier identifier = ModelIds.getBlockModelId(modelSource);
		this.blockStateCollector.accept(VariantsBlockStateSupplier.create(child, BlockStateVariant.create().put(VariantSettings.MODEL, identifier)));
		this.registerParentedItemModel(child, identifier);
	}

	private void registerIronBars() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_post_ends");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_post");
		Identifier identifier3 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_cap");
		Identifier identifier4 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_cap_alt");
		Identifier identifier5 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_side");
		Identifier identifier6 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_side_alt");
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.IRON_BARS)
					.with(BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.with(
						When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier2)
					)
					.with(
						When.create().set(Properties.NORTH, true).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3)
					)
					.with(
						When.create().set(Properties.NORTH, false).set(Properties.EAST, true).set(Properties.SOUTH, false).set(Properties.WEST, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.with(
						When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, true).set(Properties.WEST, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier4)
					)
					.with(
						When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier5))
					.with(
						When.create().set(Properties.EAST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier5).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.with(When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier6))
					.with(
						When.create().set(Properties.WEST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier6).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
			);
		this.registerItemModel(Blocks.IRON_BARS);
	}

	private void registerNorthDefaultHorizontalRotation(Block block) {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(block)))
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
	}

	private void registerLever() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.LEVER);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.LEVER, "_on");
		this.registerItemModel(Blocks.LEVER);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.LEVER)
					.coordinate(createBooleanModelMap(Properties.POWERED, identifier, identifier2))
					.coordinate(
						BlockStateVariantMap.create(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING)
							.register(
								WallMountLocation.CEILING,
								Direction.NORTH,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								WallMountLocation.CEILING,
								Direction.EAST,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(WallMountLocation.CEILING, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
							.register(
								WallMountLocation.CEILING,
								Direction.WEST,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(WallMountLocation.FLOOR, Direction.NORTH, BlockStateVariant.create())
							.register(WallMountLocation.FLOOR, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
							.register(WallMountLocation.FLOOR, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
							.register(WallMountLocation.FLOOR, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
							.register(WallMountLocation.WALL, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
							.register(
								WallMountLocation.WALL,
								Direction.EAST,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								WallMountLocation.WALL,
								Direction.SOUTH,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								WallMountLocation.WALL,
								Direction.WEST,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
					)
			);
	}

	private void registerLilyPad() {
		this.registerItemModel(Blocks.LILY_PAD);
		this.blockStateCollector.accept(createBlockStateWithRandomHorizontalRotations(Blocks.LILY_PAD, ModelIds.getBlockModelId(Blocks.LILY_PAD)));
	}

	private void registerFrogspawn() {
		this.registerItemModel(Blocks.FROGSPAWN);
		this.blockStateCollector.accept(createSingletonBlockState(Blocks.FROGSPAWN, ModelIds.getBlockModelId(Blocks.FROGSPAWN)));
	}

	private void registerNetherPortal(Block block) {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block)
					.coordinate(
						BlockStateVariantMap.create(Properties.HORIZONTAL_AXIS)
							.register(Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(block, "_ns")))
							.register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(block, "_ew")))
					)
			);
	}

	private void registerNetherrack() {
		Identifier identifier = TexturedModel.CUBE_ALL.upload(Blocks.NETHERRACK, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(
					Blocks.NETHERRACK,
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier),
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.R90),
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.R180),
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.R270),
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.R90)
						.put(VariantSettings.X, VariantSettings.Rotation.R90),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.R90)
						.put(VariantSettings.X, VariantSettings.Rotation.R180),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.R90)
						.put(VariantSettings.X, VariantSettings.Rotation.R270),
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.R180)
						.put(VariantSettings.X, VariantSettings.Rotation.R90),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.R180)
						.put(VariantSettings.X, VariantSettings.Rotation.R180),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.R180)
						.put(VariantSettings.X, VariantSettings.Rotation.R270),
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R270),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.R270)
						.put(VariantSettings.X, VariantSettings.Rotation.R90),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.R270)
						.put(VariantSettings.X, VariantSettings.Rotation.R180),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.R270)
						.put(VariantSettings.X, VariantSettings.Rotation.R270)
				)
			);
	}

	private void registerObserver() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.OBSERVER);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.OBSERVER, "_on");
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.OBSERVER)
					.coordinate(createBooleanModelMap(Properties.POWERED, identifier2, identifier))
					.coordinate(createNorthDefaultRotationStates())
			);
	}

	private void registerSpleaves(Block block) {
		Identifier identifier = ModelIds.getBlockModelId(block);
		Identifier identifier2 = ModelIds.getBlockSubModelId(block, "_broken");
		this.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(createBooleanModelMap(Properties.FALLING, identifier2, identifier)));
	}

	private void registerWorldModifyingBlock(Block block) {
		TextureMap textureMap = TextureMap.sideFrontBack(block);
		Identifier identifier = Models.TEMPLATE_COMMAND_BLOCK.upload(block, textureMap, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(block, identifier).coordinate(createNorthDefaultRotationStates()));
	}

	private void registerPistons() {
		TextureMap textureMap = new TextureMap()
			.put(TextureKey.BOTTOM, TextureMap.getSubId(Blocks.PISTON, "_bottom"))
			.put(TextureKey.SIDE, TextureMap.getSubId(Blocks.PISTON, "_side"));
		Identifier identifier = TextureMap.getSubId(Blocks.PISTON, "_top_sticky");
		Identifier identifier2 = TextureMap.getSubId(Blocks.PISTON, "_top");
		TextureMap textureMap2 = textureMap.copyAndAdd(TextureKey.PLATFORM, identifier);
		TextureMap textureMap3 = textureMap.copyAndAdd(TextureKey.PLATFORM, identifier2);
		Identifier identifier3 = ModelIds.getBlockSubModelId(Blocks.PISTON, "_base");
		this.registerPiston(Blocks.PISTON, identifier3, textureMap3);
		this.registerPiston(Blocks.STICKY_PISTON, identifier3, textureMap2);
		Identifier identifier4 = Models.CUBE_BOTTOM_TOP.upload(Blocks.PISTON, "_inventory", textureMap.copyAndAdd(TextureKey.TOP, identifier2), this.modelCollector);
		Identifier identifier5 = Models.CUBE_BOTTOM_TOP
			.upload(Blocks.STICKY_PISTON, "_inventory", textureMap.copyAndAdd(TextureKey.TOP, identifier), this.modelCollector);
		this.registerParentedItemModel(Blocks.PISTON, identifier4);
		this.registerParentedItemModel(Blocks.STICKY_PISTON, identifier5);
	}

	private void registerPiston(Block piston, Identifier extendedModelId, TextureMap textures) {
		Identifier identifier = Models.TEMPLATE_PISTON.upload(piston, textures, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(piston)
					.coordinate(createBooleanModelMap(Properties.EXTENDED, extendedModelId, identifier))
					.coordinate(createNorthDefaultRotationStates())
			);
	}

	private void registerPistonHead() {
		TextureMap textureMap = new TextureMap()
			.put(TextureKey.UNSTICKY, TextureMap.getSubId(Blocks.PISTON, "_top"))
			.put(TextureKey.SIDE, TextureMap.getSubId(Blocks.PISTON, "_side"));
		TextureMap textureMap2 = textureMap.copyAndAdd(TextureKey.PLATFORM, TextureMap.getSubId(Blocks.PISTON, "_top_sticky"));
		TextureMap textureMap3 = textureMap.copyAndAdd(TextureKey.PLATFORM, TextureMap.getSubId(Blocks.PISTON, "_top"));
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.PISTON_HEAD)
					.coordinate(
						BlockStateVariantMap.create(Properties.SHORT, Properties.PISTON_TYPE)
							.register(
								false,
								PistonType.DEFAULT,
								BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD.upload(Blocks.PISTON, "_head", textureMap3, this.modelCollector))
							)
							.register(
								false,
								PistonType.STICKY,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD.upload(Blocks.PISTON, "_head_sticky", textureMap2, this.modelCollector))
							)
							.register(
								true,
								PistonType.DEFAULT,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD_SHORT.upload(Blocks.PISTON, "_head_short", textureMap3, this.modelCollector))
							)
							.register(
								true,
								PistonType.STICKY,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD_SHORT.upload(Blocks.PISTON, "_head_short_sticky", textureMap2, this.modelCollector))
							)
					)
					.coordinate(createNorthDefaultRotationStates())
			);
	}

	private void registerSculkSensor() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.SCULK_SENSOR, "_inactive");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.SCULK_SENSOR, "_active");
		this.registerParentedItemModel(Blocks.SCULK_SENSOR, identifier);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.SCULK_SENSOR)
					.coordinate(
						BlockStateVariantMap.create(Properties.SCULK_SENSOR_PHASE)
							.register(phase -> BlockStateVariant.create().put(VariantSettings.MODEL, phase == SculkSensorPhase.ACTIVE ? identifier2 : identifier))
					)
			);
	}

	private void registerCalibratedSculkSensor() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.CALIBRATED_SCULK_SENSOR, "_inactive");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.CALIBRATED_SCULK_SENSOR, "_active");
		this.registerParentedItemModel(Blocks.CALIBRATED_SCULK_SENSOR, identifier);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.CALIBRATED_SCULK_SENSOR)
					.coordinate(
						BlockStateVariantMap.create(Properties.SCULK_SENSOR_PHASE)
							.register(phase -> BlockStateVariant.create().put(VariantSettings.MODEL, phase == SculkSensorPhase.ACTIVE ? identifier2 : identifier))
					)
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
	}

	private void registerSculkShrieker() {
		Identifier identifier = Models.TEMPLATE_SCULK_SHRIEKER.upload(Blocks.SCULK_SHRIEKER, TextureMap.sculkShrieker(false), this.modelCollector);
		Identifier identifier2 = Models.TEMPLATE_SCULK_SHRIEKER.upload(Blocks.SCULK_SHRIEKER, "_can_summon", TextureMap.sculkShrieker(true), this.modelCollector);
		this.registerParentedItemModel(Blocks.SCULK_SHRIEKER, identifier);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(Blocks.SCULK_SHRIEKER).coordinate(createBooleanModelMap(Properties.CAN_SUMMON, identifier2, identifier)));
	}

	private void registerScaffolding() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.SCAFFOLDING, "_stable");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.SCAFFOLDING, "_unstable");
		this.registerParentedItemModel(Blocks.SCAFFOLDING, identifier);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(Blocks.SCAFFOLDING).coordinate(createBooleanModelMap(Properties.BOTTOM, identifier2, identifier)));
	}

	private void registerCaveVines() {
		Identifier identifier = this.createSubModel(Blocks.CAVE_VINES, "", Models.CROSS, TextureMap::cross);
		Identifier identifier2 = this.createSubModel(Blocks.CAVE_VINES, "_lit", Models.CROSS, TextureMap::cross);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(Blocks.CAVE_VINES).coordinate(createBooleanModelMap(Properties.BERRIES, identifier2, identifier)));
		Identifier identifier3 = this.createSubModel(Blocks.CAVE_VINES_PLANT, "", Models.CROSS, TextureMap::cross);
		Identifier identifier4 = this.createSubModel(Blocks.CAVE_VINES_PLANT, "_lit", Models.CROSS, TextureMap::cross);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(Blocks.CAVE_VINES_PLANT).coordinate(createBooleanModelMap(Properties.BERRIES, identifier4, identifier3)));
	}

	private void registerRedstoneLamp() {
		Identifier identifier = TexturedModel.CUBE_ALL.upload(Blocks.REDSTONE_LAMP, this.modelCollector);
		Identifier identifier2 = this.createSubModel(Blocks.REDSTONE_LAMP, "_on", Models.CUBE_ALL, TextureMap::all);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(Blocks.REDSTONE_LAMP).coordinate(createBooleanModelMap(Properties.LIT, identifier2, identifier)));
	}

	private void registerTorch(Block torch, Block wallTorch) {
		TextureMap textureMap = TextureMap.torch(torch);
		this.blockStateCollector.accept(createSingletonBlockState(torch, Models.TEMPLATE_TORCH.upload(torch, textureMap, this.modelCollector)));
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(
						wallTorch, BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_TORCH_WALL.upload(wallTorch, textureMap, this.modelCollector))
					)
					.coordinate(createEastDefaultHorizontalRotationStates())
			);
		this.registerItemModel(torch);
		this.excludeFromSimpleItemModelGeneration(wallTorch);
	}

	private void registerRedstoneTorch() {
		TextureMap textureMap = TextureMap.torch(Blocks.REDSTONE_TORCH);
		TextureMap textureMap2 = TextureMap.torch(TextureMap.getSubId(Blocks.REDSTONE_TORCH, "_off"));
		Identifier identifier = Models.TEMPLATE_TORCH.upload(Blocks.REDSTONE_TORCH, textureMap, this.modelCollector);
		Identifier identifier2 = Models.TEMPLATE_TORCH.upload(Blocks.REDSTONE_TORCH, "_off", textureMap2, this.modelCollector);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(Blocks.REDSTONE_TORCH).coordinate(createBooleanModelMap(Properties.LIT, identifier, identifier2)));
		Identifier identifier3 = Models.TEMPLATE_TORCH_WALL.upload(Blocks.REDSTONE_WALL_TORCH, textureMap, this.modelCollector);
		Identifier identifier4 = Models.TEMPLATE_TORCH_WALL.upload(Blocks.REDSTONE_WALL_TORCH, "_off", textureMap2, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.REDSTONE_WALL_TORCH)
					.coordinate(createBooleanModelMap(Properties.LIT, identifier3, identifier4))
					.coordinate(createEastDefaultHorizontalRotationStates())
			);
		this.registerItemModel(Blocks.REDSTONE_TORCH);
		this.excludeFromSimpleItemModelGeneration(Blocks.REDSTONE_WALL_TORCH);
	}

	private void registerRepeater() {
		this.registerItemModel(Items.REPEATER);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.REPEATER)
					.coordinate(BlockStateVariantMap.create(Properties.DELAY, Properties.LOCKED, Properties.POWERED).register((tick, locked, on) -> {
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append('_').append(tick).append("tick");
						if (on) {
							stringBuilder.append("_on");
						}

						if (locked) {
							stringBuilder.append("_locked");
						}

						return BlockStateVariant.create().put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.REPEATER, stringBuilder.toString()));
					}))
					.coordinate(createSouthDefaultHorizontalRotationStates())
			);
	}

	private void registerSeaPickle() {
		this.registerItemModel(Items.SEA_PICKLE);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.SEA_PICKLE)
					.coordinate(
						BlockStateVariantMap.create(Properties.PICKLES, Properties.WATERLOGGED)
							.register(1, false, Arrays.asList(createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("dead_sea_pickle"))))
							.register(2, false, Arrays.asList(createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("two_dead_sea_pickles"))))
							.register(3, false, Arrays.asList(createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("three_dead_sea_pickles"))))
							.register(4, false, Arrays.asList(createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("four_dead_sea_pickles"))))
							.register(1, true, Arrays.asList(createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("sea_pickle"))))
							.register(2, true, Arrays.asList(createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("two_sea_pickles"))))
							.register(3, true, Arrays.asList(createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("three_sea_pickles"))))
							.register(4, true, Arrays.asList(createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("four_sea_pickles"))))
					)
			);
	}

	private void registerSnows() {
		TextureMap textureMap = TextureMap.all(Blocks.SNOW);
		Identifier identifier = Models.CUBE_ALL.upload(Blocks.SNOW_BLOCK, textureMap, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.SNOW)
					.coordinate(
						BlockStateVariantMap.create(Properties.LAYERS)
							.register(
								height -> BlockStateVariant.create()
										.put(VariantSettings.MODEL, height < 8 ? ModelIds.getBlockSubModelId(Blocks.SNOW, "_height" + height * 2) : identifier)
							)
					)
			);
		this.registerParentedItemModel(Blocks.SNOW, ModelIds.getBlockSubModelId(Blocks.SNOW, "_height2"));
		this.blockStateCollector.accept(createSingletonBlockState(Blocks.SNOW_BLOCK, identifier));
	}

	private void registerStonecutter() {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.STONECUTTER, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.STONECUTTER)))
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
	}

	private void registerStructureBlock() {
		Identifier identifier = TexturedModel.CUBE_ALL.upload(Blocks.STRUCTURE_BLOCK, this.modelCollector);
		this.registerParentedItemModel(Blocks.STRUCTURE_BLOCK, identifier);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.STRUCTURE_BLOCK)
					.coordinate(
						BlockStateVariantMap.create(Properties.STRUCTURE_BLOCK_MODE)
							.register(
								mode -> BlockStateVariant.create()
										.put(VariantSettings.MODEL, this.createSubModel(Blocks.STRUCTURE_BLOCK, "_" + mode.asString(), Models.CUBE_ALL, TextureMap::all))
							)
					)
			);
	}

	private void registerSweetBerryBush() {
		this.registerItemModel(Items.SWEET_BERRIES);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.SWEET_BERRY_BUSH)
					.coordinate(
						BlockStateVariantMap.create(Properties.AGE_3)
							.register(
								stage -> BlockStateVariant.create()
										.put(VariantSettings.MODEL, this.createSubModel(Blocks.SWEET_BERRY_BUSH, "_stage" + stage, Models.CROSS, TextureMap::cross))
							)
					)
			);
	}

	private void registerTripwire() {
		this.registerItemModel(Items.STRING);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.TRIPWIRE)
					.coordinate(
						BlockStateVariantMap.create(Properties.ATTACHED, Properties.EAST, Properties.NORTH, Properties.SOUTH, Properties.WEST)
							.register(false, false, false, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ns")))
							.register(
								false,
								true,
								false,
								false,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_n"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(false, false, true, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_n")))
							.register(
								false,
								false,
								false,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_n"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								false,
								false,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_n"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(false, true, true, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ne")))
							.register(
								false,
								true,
								false,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ne"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								false,
								false,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ne"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								false,
								false,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ne"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(false, false, true, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ns")))
							.register(
								false,
								true,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ns"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(false, true, true, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nse")))
							.register(
								false,
								true,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nse"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								false,
								false,
								true,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nse"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								false,
								true,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nse"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(false, true, true, true, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nsew")))
							.register(
								true, false, false, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ns"))
							)
							.register(
								true, false, true, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_n"))
							)
							.register(
								true,
								false,
								false,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_n"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								true,
								true,
								false,
								false,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_n"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								true,
								false,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_n"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(
								true, true, true, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ne"))
							)
							.register(
								true,
								true,
								false,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ne"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								true,
								false,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ne"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								true,
								false,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ne"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(
								true, false, true, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ns"))
							)
							.register(
								true,
								true,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ns"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								true, true, true, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nse"))
							)
							.register(
								true,
								true,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nse"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								true,
								false,
								true,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nse"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								true,
								true,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nse"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(
								true, true, true, true, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nsew"))
							)
					)
			);
	}

	private void registerTripwireHook() {
		this.registerItemModel(Blocks.TRIPWIRE_HOOK);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.TRIPWIRE_HOOK)
					.coordinate(
						BlockStateVariantMap.create(Properties.ATTACHED, Properties.POWERED)
							.register(
								(boolean_, boolean2) -> BlockStateVariant.create()
										.put(VariantSettings.MODEL, TextureMap.getSubId(Blocks.TRIPWIRE_HOOK, (boolean_ ? "_attached" : "") + (boolean2 ? "_on" : "")))
							)
					)
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
	}

	private Identifier getTurtleEggModel(int eggs, String prefix, TextureMap textures) {
		switch (eggs) {
			case 1:
				return Models.TEMPLATE_TURTLE_EGG.upload(ModelIds.getMinecraftNamespacedBlock(prefix + "turtle_egg"), textures, this.modelCollector);
			case 2:
				return Models.TEMPLATE_TWO_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("two_" + prefix + "turtle_eggs"), textures, this.modelCollector);
			case 3:
				return Models.TEMPLATE_THREE_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("three_" + prefix + "turtle_eggs"), textures, this.modelCollector);
			case 4:
				return Models.TEMPLATE_FOUR_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("four_" + prefix + "turtle_eggs"), textures, this.modelCollector);
			default:
				throw new UnsupportedOperationException();
		}
	}

	private Identifier getTurtleEggModel(Integer eggs, Integer hatch) {
		switch (hatch) {
			case 0:
				return this.getTurtleEggModel(eggs, "", TextureMap.all(TextureMap.getId(Blocks.TURTLE_EGG)));
			case 1:
				return this.getTurtleEggModel(eggs, "slightly_cracked_", TextureMap.all(TextureMap.getSubId(Blocks.TURTLE_EGG, "_slightly_cracked")));
			case 2:
				return this.getTurtleEggModel(eggs, "very_cracked_", TextureMap.all(TextureMap.getSubId(Blocks.TURTLE_EGG, "_very_cracked")));
			default:
				throw new UnsupportedOperationException();
		}
	}

	private void registerTurtleEgg() {
		this.registerItemModel(Items.TURTLE_EGG);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.TURTLE_EGG)
					.coordinate(
						BlockStateVariantMap.create(Properties.EGGS, Properties.HATCH)
							.registerVariants((integer, integer2) -> Arrays.asList(createModelVariantWithRandomHorizontalRotations(this.getTurtleEggModel(integer, integer2))))
					)
			);
	}

	private void registerSnifferEgg() {
		this.registerItemModel(Items.SNIFFER_EGG);
		Function<Integer, Identifier> function = age -> {
			String string = switch (age) {
				case 1 -> "_slightly_cracked";
				case 2 -> "_very_cracked";
				default -> "_not_cracked";
			};
			TextureMap textureMap = TextureMap.snifferEgg(string);
			return Models.SNIFFER_EGG.upload(Blocks.SNIFFER_EGG, string, textureMap, this.modelCollector);
		};
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.SNIFFER_EGG)
					.coordinate(
						BlockStateVariantMap.create(SnifferEggBlock.AGE).register(age -> BlockStateVariant.create().put(VariantSettings.MODEL, (Identifier)function.apply(age)))
					)
			);
	}

	private void registerWallPlant(Block block) {
		this.registerItemModel(block);
		Identifier identifier = ModelIds.getBlockModelId(block);
		MultipartBlockStateSupplier multipartBlockStateSupplier = MultipartBlockStateSupplier.create(block);
		When.PropertyCondition propertyCondition = Util.make(
			When.create(), propertyConditionx -> CONNECTION_VARIANT_FUNCTIONS.stream().map(Pair::getFirst).forEach(property -> {
					if (block.getDefaultState().contains(property)) {
						propertyConditionx.set(property, false);
					}
				})
		);

		for (Pair<BooleanProperty, Function<Identifier, BlockStateVariant>> pair : CONNECTION_VARIANT_FUNCTIONS) {
			BooleanProperty booleanProperty = pair.getFirst();
			Function<Identifier, BlockStateVariant> function = pair.getSecond();
			if (block.getDefaultState().contains(booleanProperty)) {
				multipartBlockStateSupplier.with(When.create().set(booleanProperty, true), (BlockStateVariant)function.apply(identifier));
				multipartBlockStateSupplier.with(propertyCondition, (BlockStateVariant)function.apply(identifier));
			}
		}

		this.blockStateCollector.accept(multipartBlockStateSupplier);
	}

	private void registerSculkCatalyst() {
		Identifier identifier = TextureMap.getSubId(Blocks.SCULK_CATALYST, "_bottom");
		TextureMap textureMap = new TextureMap()
			.put(TextureKey.BOTTOM, identifier)
			.put(TextureKey.TOP, TextureMap.getSubId(Blocks.SCULK_CATALYST, "_top"))
			.put(TextureKey.SIDE, TextureMap.getSubId(Blocks.SCULK_CATALYST, "_side"));
		TextureMap textureMap2 = new TextureMap()
			.put(TextureKey.BOTTOM, identifier)
			.put(TextureKey.TOP, TextureMap.getSubId(Blocks.SCULK_CATALYST, "_top_bloom"))
			.put(TextureKey.SIDE, TextureMap.getSubId(Blocks.SCULK_CATALYST, "_side_bloom"));
		Identifier identifier2 = Models.CUBE_BOTTOM_TOP.upload(Blocks.SCULK_CATALYST, "", textureMap, this.modelCollector);
		Identifier identifier3 = Models.CUBE_BOTTOM_TOP.upload(Blocks.SCULK_CATALYST, "_bloom", textureMap2, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.SCULK_CATALYST)
					.coordinate(
						BlockStateVariantMap.create(Properties.BLOOM).register(bloom -> BlockStateVariant.create().put(VariantSettings.MODEL, bloom ? identifier3 : identifier2))
					)
			);
		this.registerParentedItemModel(Items.SCULK_CATALYST, identifier2);
	}

	private void registerChiseledBookshelf() {
		Block block = Blocks.CHISELED_BOOKSHELF;
		Identifier identifier = ModelIds.getBlockModelId(block);
		MultipartBlockStateSupplier multipartBlockStateSupplier = MultipartBlockStateSupplier.create(block);
		Map.of(
				Direction.NORTH,
				VariantSettings.Rotation.R0,
				Direction.EAST,
				VariantSettings.Rotation.R90,
				Direction.SOUTH,
				VariantSettings.Rotation.R180,
				Direction.WEST,
				VariantSettings.Rotation.R270
			)
			.forEach(
				(direction, rotation) -> {
					When.PropertyCondition propertyCondition = When.create().set(Properties.HORIZONTAL_FACING, direction);
					multipartBlockStateSupplier.with(
						propertyCondition, BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, rotation).put(VariantSettings.UVLOCK, true)
					);
					this.supplyChiseledBookshelfModels(multipartBlockStateSupplier, propertyCondition, rotation);
				}
			);
		this.blockStateCollector.accept(multipartBlockStateSupplier);
		this.registerParentedItemModel(block, ModelIds.getBlockSubModelId(block, "_inventory"));
		CHISELED_BOOKSHELF_MODEL_CACHE.clear();
	}

	private void supplyChiseledBookshelfModels(
		MultipartBlockStateSupplier blockStateSupplier, When.PropertyCondition facingCondition, VariantSettings.Rotation rotation
	) {
		Map.of(
				Properties.SLOT_0_OCCUPIED,
				Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_LEFT,
				Properties.SLOT_1_OCCUPIED,
				Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_MID,
				Properties.SLOT_2_OCCUPIED,
				Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_RIGHT,
				Properties.SLOT_3_OCCUPIED,
				Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT,
				Properties.SLOT_4_OCCUPIED,
				Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_MID,
				Properties.SLOT_5_OCCUPIED,
				Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT
			)
			.forEach((property, model) -> {
				this.supplyChiseledBookshelfModel(blockStateSupplier, facingCondition, rotation, property, model, true);
				this.supplyChiseledBookshelfModel(blockStateSupplier, facingCondition, rotation, property, model, false);
			});
	}

	private void supplyChiseledBookshelfModel(
		MultipartBlockStateSupplier blockStateSupplier,
		When.PropertyCondition facingCondition,
		VariantSettings.Rotation rotation,
		BooleanProperty property,
		Model model,
		boolean occupied
	) {
		String string = occupied ? "_occupied" : "_empty";
		TextureMap textureMap = new TextureMap().put(TextureKey.TEXTURE, TextureMap.getSubId(Blocks.CHISELED_BOOKSHELF, string));
		BlockStateModelGenerator.ChiseledBookshelfModelCacheKey chiseledBookshelfModelCacheKey = new BlockStateModelGenerator.ChiseledBookshelfModelCacheKey(
			model, string
		);
		Identifier identifier = (Identifier)CHISELED_BOOKSHELF_MODEL_CACHE.computeIfAbsent(
			chiseledBookshelfModelCacheKey, key -> model.upload(Blocks.CHISELED_BOOKSHELF, string, textureMap, this.modelCollector)
		);
		blockStateSupplier.with(
			When.allOf(facingCondition, When.create().set(property, occupied)),
			BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, rotation)
		);
	}

	private void registerMagmaBlock() {
		this.blockStateCollector
			.accept(
				createSingletonBlockState(
					Blocks.MAGMA_BLOCK, Models.CUBE_ALL.upload(Blocks.MAGMA_BLOCK, TextureMap.all(ModelIds.getMinecraftNamespacedBlock("magma")), this.modelCollector)
				)
			);
	}

	private void registerShulkerBox(Block shulkerBox) {
		this.registerSingleton(shulkerBox, TexturedModel.PARTICLE);
		Models.TEMPLATE_SHULKER_BOX.upload(ModelIds.getItemModelId(shulkerBox.asItem()), TextureMap.particle(shulkerBox), this.modelCollector);
	}

	private void registerPlantPart(Block plant, Block plantStem, BlockStateModelGenerator.TintType tintType) {
		this.registerTintableCrossBlockState(plant, tintType);
		this.registerTintableCrossBlockState(plantStem, tintType);
	}

	private void registerBed(Block bed, Block particleSource) {
		Models.TEMPLATE_BED.upload(ModelIds.getItemModelId(bed.asItem()), TextureMap.particle(particleSource), this.modelCollector);
	}

	private void registerInfestedStone() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.STONE);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.STONE, "_mirrored");
		this.blockStateCollector.accept(createBlockStateWithTwoModelAndRandomInversion(Blocks.INFESTED_STONE, identifier, identifier2));
		this.registerParentedItemModel(Blocks.INFESTED_STONE, identifier);
	}

	private void registerInfestedDeepslate() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.DEEPSLATE);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.DEEPSLATE, "_mirrored");
		this.blockStateCollector
			.accept(createBlockStateWithTwoModelAndRandomInversion(Blocks.INFESTED_DEEPSLATE, identifier, identifier2).coordinate(createAxisRotatedVariantMap()));
		this.registerParentedItemModel(Blocks.INFESTED_DEEPSLATE, identifier);
	}

	private void registerRoots(Block root, Block pottedRoot) {
		this.registerTintableCross(root, BlockStateModelGenerator.TintType.NOT_TINTED);
		TextureMap textureMap = TextureMap.plant(TextureMap.getSubId(root, "_pot"));
		Identifier identifier = BlockStateModelGenerator.TintType.NOT_TINTED.getFlowerPotCrossModel().upload(pottedRoot, textureMap, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(pottedRoot, identifier));
	}

	private void registerRespawnAnchor() {
		Identifier identifier = TextureMap.getSubId(Blocks.RESPAWN_ANCHOR, "_bottom");
		Identifier identifier2 = TextureMap.getSubId(Blocks.RESPAWN_ANCHOR, "_top_off");
		Identifier identifier3 = TextureMap.getSubId(Blocks.RESPAWN_ANCHOR, "_top");
		Identifier[] identifiers = new Identifier[5];

		for (int i = 0; i < 5; i++) {
			TextureMap textureMap = new TextureMap()
				.put(TextureKey.BOTTOM, identifier)
				.put(TextureKey.TOP, i == 0 ? identifier2 : identifier3)
				.put(TextureKey.SIDE, TextureMap.getSubId(Blocks.RESPAWN_ANCHOR, "_side" + i));
			identifiers[i] = Models.CUBE_BOTTOM_TOP.upload(Blocks.RESPAWN_ANCHOR, "_" + i, textureMap, this.modelCollector);
		}

		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.RESPAWN_ANCHOR)
					.coordinate(
						BlockStateVariantMap.create(Properties.CHARGES).register(charges -> BlockStateVariant.create().put(VariantSettings.MODEL, identifiers[charges]))
					)
			);
		this.registerParentedItemModel(Items.RESPAWN_ANCHOR, identifiers[0]);
	}

	private BlockStateVariant addJigsawOrientationToVariant(JigsawOrientation orientation, BlockStateVariant variant) {
		switch (orientation) {
			case DOWN_NORTH:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.R90);
			case DOWN_SOUTH:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R180);
			case DOWN_WEST:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270);
			case DOWN_EAST:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90);
			case UP_NORTH:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.Y, VariantSettings.Rotation.R180);
			case UP_SOUTH:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.R270);
			case UP_WEST:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.Y, VariantSettings.Rotation.R90);
			case UP_EAST:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.Y, VariantSettings.Rotation.R270);
			case NORTH_UP:
				return variant;
			case SOUTH_UP:
				return variant.put(VariantSettings.Y, VariantSettings.Rotation.R180);
			case WEST_UP:
				return variant.put(VariantSettings.Y, VariantSettings.Rotation.R270);
			case EAST_UP:
				return variant.put(VariantSettings.Y, VariantSettings.Rotation.R90);
			default:
				throw new UnsupportedOperationException("Rotation " + orientation + " can't be expressed with existing x and y values");
		}
	}

	private void registerJigsaw() {
		Identifier identifier = TextureMap.getSubId(Blocks.JIGSAW, "_top");
		Identifier identifier2 = TextureMap.getSubId(Blocks.JIGSAW, "_bottom");
		Identifier identifier3 = TextureMap.getSubId(Blocks.JIGSAW, "_side");
		Identifier identifier4 = TextureMap.getSubId(Blocks.JIGSAW, "_lock");
		TextureMap textureMap = new TextureMap()
			.put(TextureKey.DOWN, identifier3)
			.put(TextureKey.WEST, identifier3)
			.put(TextureKey.EAST, identifier3)
			.put(TextureKey.PARTICLE, identifier)
			.put(TextureKey.NORTH, identifier)
			.put(TextureKey.SOUTH, identifier2)
			.put(TextureKey.UP, identifier4);
		Identifier identifier5 = Models.CUBE_DIRECTIONAL.upload(Blocks.JIGSAW, textureMap, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.JIGSAW, BlockStateVariant.create().put(VariantSettings.MODEL, identifier5))
					.coordinate(
						BlockStateVariantMap.create(Properties.ORIENTATION)
							.register(jigsawOrientation -> this.addJigsawOrientationToVariant(jigsawOrientation, BlockStateVariant.create()))
					)
			);
	}

	private void registerPetrifiedOakSlab() {
		Block block = Blocks.OAK_PLANKS;
		Identifier identifier = ModelIds.getBlockModelId(block);
		TexturedModel texturedModel = TexturedModel.CUBE_ALL.get(block);
		Block block2 = Blocks.PETRIFIED_OAK_SLAB;
		Identifier identifier2 = Models.SLAB.upload(block2, texturedModel.getTextures(), this.modelCollector);
		Identifier identifier3 = Models.SLAB_TOP.upload(block2, texturedModel.getTextures(), this.modelCollector);
		this.blockStateCollector.accept(createSlabBlockState(block2, identifier2, identifier3, identifier));
	}

	public void register() {
		BlockFamilies.getFamilies()
			.filter(BlockFamily::shouldGenerateModels)
			.forEach(family -> this.registerCubeAllModelTexturePool(family.getBaseBlock()).family(family));
		this.registerCubeAllModelTexturePool(Blocks.CUT_COPPER).family(BlockFamilies.CUT_COPPER).same(Blocks.WAXED_CUT_COPPER).family(BlockFamilies.WAXED_CUT_COPPER);
		this.registerCubeAllModelTexturePool(Blocks.EXPOSED_CUT_COPPER)
			.family(BlockFamilies.EXPOSED_CUT_COPPER)
			.same(Blocks.WAXED_EXPOSED_CUT_COPPER)
			.family(BlockFamilies.WAXED_EXPOSED_CUT_COPPER);
		this.registerCubeAllModelTexturePool(Blocks.WEATHERED_CUT_COPPER)
			.family(BlockFamilies.WEATHERED_CUT_COPPER)
			.same(Blocks.WAXED_WEATHERED_CUT_COPPER)
			.family(BlockFamilies.WAXED_WEATHERED_CUT_COPPER);
		this.registerCubeAllModelTexturePool(Blocks.OXIDIZED_CUT_COPPER)
			.family(BlockFamilies.OXIDIZED_CUT_COPPER)
			.same(Blocks.WAXED_OXIDIZED_CUT_COPPER)
			.family(BlockFamilies.WAXED_OXIDIZED_CUT_COPPER);
		this.registerSimpleState(Blocks.AIR);
		this.registerStateWithModelReference(Blocks.CAVE_AIR, Blocks.AIR);
		this.registerStateWithModelReference(Blocks.VOID_AIR, Blocks.AIR);
		this.registerPackedAir();
		this.registerSimpleState(Blocks.BEACON);
		this.registerSimpleState(Blocks.CACTUS);
		this.registerStateWithModelReference(Blocks.BUBBLE_COLUMN, Blocks.WATER);
		this.registerSimpleState(Blocks.DRAGON_EGG);
		this.registerSimpleState(Blocks.DRIED_KELP_BLOCK);
		this.registerSimpleState(Blocks.ENCHANTING_TABLE);
		this.registerSimpleState(Blocks.FLOWER_POT);
		this.registerItemModel(Items.FLOWER_POT);
		this.registerSimpleState(Blocks.HONEY_BLOCK);
		this.registerSimpleState(Blocks.WATER);
		this.registerSimpleState(Blocks.LAVA);
		this.registerSimpleState(Blocks.SLIME_BLOCK);
		this.registerItemModel(Items.CHAIN);
		this.registerCandle(Blocks.WHITE_CANDLE, Blocks.WHITE_CANDLE_CAKE);
		this.registerCandle(Blocks.ORANGE_CANDLE, Blocks.ORANGE_CANDLE_CAKE);
		this.registerCandle(Blocks.MAGENTA_CANDLE, Blocks.MAGENTA_CANDLE_CAKE);
		this.registerCandle(Blocks.LIGHT_BLUE_CANDLE, Blocks.LIGHT_BLUE_CANDLE_CAKE);
		this.registerCandle(Blocks.YELLOW_CANDLE, Blocks.YELLOW_CANDLE_CAKE);
		this.registerCandle(Blocks.LIME_CANDLE, Blocks.LIME_CANDLE_CAKE);
		this.registerCandle(Blocks.PINK_CANDLE, Blocks.PINK_CANDLE_CAKE);
		this.registerCandle(Blocks.GRAY_CANDLE, Blocks.GRAY_CANDLE_CAKE);
		this.registerCandle(Blocks.LIGHT_GRAY_CANDLE, Blocks.LIGHT_GRAY_CANDLE_CAKE);
		this.registerCandle(Blocks.CYAN_CANDLE, Blocks.CYAN_CANDLE_CAKE);
		this.registerCandle(Blocks.PURPLE_CANDLE, Blocks.PURPLE_CANDLE_CAKE);
		this.registerCandle(Blocks.BLUE_CANDLE, Blocks.BLUE_CANDLE_CAKE);
		this.registerCandle(Blocks.BROWN_CANDLE, Blocks.BROWN_CANDLE_CAKE);
		this.registerCandle(Blocks.GREEN_CANDLE, Blocks.GREEN_CANDLE_CAKE);
		this.registerCandle(Blocks.RED_CANDLE, Blocks.RED_CANDLE_CAKE);
		this.registerCandle(Blocks.BLACK_CANDLE, Blocks.BLACK_CANDLE_CAKE);
		this.registerCandle(Blocks.CANDLE, Blocks.CANDLE_CAKE);
		this.registerSimpleState(Blocks.POTTED_BAMBOO);
		this.registerSimpleState(Blocks.POTTED_CACTUS);
		this.registerSimpleState(Blocks.POWDER_SNOW);
		this.registerSimpleState(Blocks.SPORE_BLOSSOM);
		this.registerAzalea(Blocks.AZALEA);
		this.registerAzalea(Blocks.FLOWERING_AZALEA);
		this.registerPottedAzaleaBush(Blocks.POTTED_AZALEA_BUSH);
		this.registerPottedAzaleaBush(Blocks.POTTED_FLOWERING_AZALEA_BUSH);
		this.registerCaveVines();
		this.registerWoolAndCarpet(Blocks.MOSS_BLOCK, Blocks.MOSS_CARPET);
		this.registerFlowerbed(Blocks.PINK_PETALS);
		this.registerBuiltinWithParticle(Blocks.BARRIER, Items.BARRIER);
		this.registerItemModel(Items.BARRIER);
		this.registerLightBlock();
		this.registerBuiltinWithParticle(Blocks.STRUCTURE_VOID, Items.STRUCTURE_VOID);
		this.registerItemModel(Items.STRUCTURE_VOID);
		this.registerBuiltinWithParticle(Blocks.MOVING_PISTON, TextureMap.getSubId(Blocks.PISTON, "_side"));
		this.registerSimpleCubeAll(Blocks.COAL_ORE);
		this.registerSimpleCubeAll(Blocks.DEEPSLATE_COAL_ORE);
		this.registerSimpleCubeAll(Blocks.COAL_BLOCK);
		this.registerSimpleCubeAll(Blocks.DIAMOND_ORE);
		this.registerSimpleCubeAll(Blocks.DEEPSLATE_DIAMOND_ORE);
		this.registerSimpleCubeAll(Blocks.DIAMOND_BLOCK);
		this.registerSimpleCubeAll(Blocks.EMERALD_ORE);
		this.registerSimpleCubeAll(Blocks.DEEPSLATE_EMERALD_ORE);
		this.registerSimpleCubeAll(Blocks.EMERALD_BLOCK);
		this.registerSimpleCubeAll(Blocks.GOLD_ORE);
		this.registerSimpleCubeAll(Blocks.NETHER_GOLD_ORE);
		this.registerSimpleCubeAll(Blocks.DEEPSLATE_GOLD_ORE);
		this.registerSimpleCubeAll(Blocks.GOLD_BLOCK);
		this.registerSimpleCubeAll(Blocks.IRON_ORE);
		this.registerSimpleCubeAll(Blocks.DEEPSLATE_IRON_ORE);
		this.registerSimpleCubeAll(Blocks.IRON_BLOCK);
		this.registerSingleton(Blocks.ANCIENT_DEBRIS, TexturedModel.CUBE_COLUMN);
		this.registerSimpleCubeAll(Blocks.NETHERITE_BLOCK);
		this.registerSimpleCubeAll(Blocks.LAPIS_ORE);
		this.registerSimpleCubeAll(Blocks.DEEPSLATE_LAPIS_ORE);
		this.registerSimpleCubeAll(Blocks.LAPIS_BLOCK);
		this.registerSimpleCubeAll(Blocks.NETHER_QUARTZ_ORE);
		this.registerSimpleCubeAll(Blocks.REDSTONE_ORE);
		this.registerSimpleCubeAll(Blocks.DEEPSLATE_REDSTONE_ORE);
		this.registerSimpleCubeAll(Blocks.REDSTONE_BLOCK);
		this.registerSimpleCubeAll(Blocks.GILDED_BLACKSTONE);
		this.registerSimpleCubeAll(Blocks.BLUE_ICE);
		this.registerSimpleCubeAll(Blocks.CLAY);
		this.registerSimpleCubeAll(Blocks.COARSE_DIRT);
		this.registerSimpleCubeAll(Blocks.CRYING_OBSIDIAN);
		this.registerSimpleCubeAll(Blocks.END_STONE);
		this.registerSimpleCubeAll(Blocks.GLOWSTONE);
		this.registerSimpleCubeAll(Blocks.GRAVEL);
		this.registerSimpleCubeAll(Blocks.HONEYCOMB_BLOCK);
		this.registerSimpleCubeAll(Blocks.ICE);
		this.registerSingleton(Blocks.JUKEBOX, TexturedModel.CUBE_TOP);
		this.registerSingleton(Blocks.LODESTONE, TexturedModel.CUBE_COLUMN);
		this.registerSingleton(Blocks.MELON, TexturedModel.CUBE_COLUMN);
		this.registerSimpleState(Blocks.MANGROVE_ROOTS);
		this.registerSimpleState(Blocks.POTTED_MANGROVE_PROPAGULE);
		this.registerSimpleCubeAll(Blocks.NETHER_WART_BLOCK);
		this.registerSimpleCubeAll(Blocks.NOTE_BLOCK);
		this.registerSimpleCubeAll(Blocks.PACKED_ICE);
		this.registerSimpleCubeAll(Blocks.OBSIDIAN);
		this.registerSimpleCubeAll(Blocks.QUARTZ_BRICKS);
		this.registerSimpleCubeAll(Blocks.SEA_LANTERN);
		this.registerSimpleCubeAll(Blocks.SHROOMLIGHT);
		this.registerSimpleCubeAll(Blocks.SOUL_SAND);
		this.registerSimpleCubeAll(Blocks.SOUL_SOIL);
		this.registerSimpleCubeAll(Blocks.SPAWNER);
		this.registerSimpleCubeAll(Blocks.SPONGE);
		this.registerSingleton(Blocks.SEAGRASS, TexturedModel.TEMPLATE_SEAGRASS);
		this.registerItemModel(Items.SEAGRASS);
		this.registerSingleton(Blocks.TNT, TexturedModel.CUBE_BOTTOM_TOP);
		this.registerSingleton(Blocks.TARGET, TexturedModel.CUBE_COLUMN);
		this.registerSimpleCubeAll(Blocks.WARPED_WART_BLOCK);
		this.registerSimpleCubeAll(Blocks.WET_SPONGE);
		this.registerSimpleCubeAll(Blocks.AMETHYST_BLOCK);
		this.registerSimpleCubeAll(Blocks.BUDDING_AMETHYST);
		this.registerSimpleCubeAll(Blocks.CALCITE);
		this.registerSimpleCubeAll(Blocks.TUFF);
		this.registerSimpleCubeAll(Blocks.DRIPSTONE_BLOCK);
		this.registerSimpleCubeAll(Blocks.RAW_IRON_BLOCK);
		this.registerSimpleCubeAll(Blocks.RAW_COPPER_BLOCK);
		this.registerSimpleCubeAll(Blocks.RAW_GOLD_BLOCK);
		this.registerMirrorable(Blocks.SCULK);
		this.registerPetrifiedOakSlab();
		this.registerSimpleCubeAll(Blocks.COPPER_ORE);
		this.registerSimpleCubeAll(Blocks.DEEPSLATE_COPPER_ORE);
		this.registerSimpleCubeAll(Blocks.COPPER_BLOCK);
		this.registerSimpleCubeAll(Blocks.EXPOSED_COPPER);
		this.registerSimpleCubeAll(Blocks.WEATHERED_COPPER);
		this.registerSimpleCubeAll(Blocks.OXIDIZED_COPPER);
		this.registerParented(Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK);
		this.registerParented(Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER);
		this.registerParented(Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER);
		this.registerParented(Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER);
		this.registerPressurePlate(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.GOLD_BLOCK);
		this.registerPressurePlate(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.IRON_BLOCK);
		this.registerAmethysts();
		this.registerBookshelf();
		this.registerChiseledBookshelf();
		this.registerBrewingStand();
		this.registerCake();
		this.registerCampfire(Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
		this.registerCartographyTable();
		this.registerCauldrons();
		this.registerChorusFlower();
		this.registerChorusPlant();
		this.registerComposter();
		this.registerDaylightDetector();
		this.registerEndPortalFrame();
		this.registerRod(Blocks.END_ROD);
		this.registerLightningRod();
		this.registerFarmland();
		this.registerFire();
		this.registerSoulFire();
		this.registerFrostedIce();
		this.registerTopSoils();
		this.registerCocoa();
		this.registerGrassPath();
		this.registerGrindstone();
		this.registerHopper();
		this.registerIronBars();
		this.registerLever();
		this.registerLilyPad();
		this.registerNetherPortal(Blocks.NETHER_PORTAL);
		this.registerNetherPortal(Blocks.OTHER_PORTAL);
		this.registerNetherrack();
		this.registerObserver();
		this.registerWorldModifyingBlock(Blocks.PICKAXE_BLOCK);
		this.registerWorldModifyingBlock(Blocks.PLACE_BLOCK);
		this.registerPistons();
		this.registerPistonHead();
		this.registerScaffolding();
		this.registerRedstoneTorch();
		this.registerRedstoneLamp();
		this.registerRepeater();
		this.registerSeaPickle();
		this.registerSmithingTable();
		this.registerSnows();
		this.registerStonecutter();
		this.registerStructureBlock();
		this.registerSweetBerryBush();
		this.registerTripwire();
		this.registerTripwireHook();
		this.registerTurtleEgg();
		this.registerSnifferEgg();
		this.registerWallPlant(Blocks.VINE);
		this.registerWallPlant(Blocks.GLOW_LICHEN);
		this.registerWallPlant(Blocks.SCULK_VEIN);
		this.registerMagmaBlock();
		this.registerJigsaw();
		this.registerSculkSensor();
		this.registerCalibratedSculkSensor();
		this.registerSculkShrieker();
		this.registerFrogspawn();
		this.registerMangrovePropagule();
		this.registerMuddyMangroveRoots();
		this.registerCheese();
		this.registerNorthDefaultHorizontalRotation(Blocks.LADDER);
		this.registerItemModel(Blocks.LADDER);
		this.registerNorthDefaultHorizontalRotation(Blocks.LECTERN);
		this.registerBigDripleaf();
		this.registerNorthDefaultHorizontalRotation(Blocks.BIG_DRIPLEAF_STEM);
		this.registerTorch(Blocks.TORCH, Blocks.WALL_TORCH);
		this.registerTorch(Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH);
		this.registerCubeWithCustomTextures(Blocks.CRAFTING_TABLE, Blocks.OAK_PLANKS, TextureMap::frontSideWithCustomBottom);
		this.registerCubeWithCustomTextures(Blocks.FLETCHING_TABLE, Blocks.BIRCH_PLANKS, TextureMap::frontTopSide);
		this.registerNetherrackBottomCustomTop(Blocks.CRIMSON_NYLIUM);
		this.registerNetherrackBottomCustomTop(Blocks.WARPED_NYLIUM);
		this.registerDispenserLikeOrientable(Blocks.DISPENSER);
		this.registerDispenserLikeOrientable(Blocks.DROPPER);
		this.registerLantern(Blocks.LANTERN);
		this.registerLantern(Blocks.SOUL_LANTERN);
		this.registerAxisRotated(Blocks.CHAIN, ModelIds.getBlockModelId(Blocks.CHAIN));
		this.registerAxisRotated(Blocks.BASALT, TexturedModel.CUBE_COLUMN);
		this.registerAxisRotated(Blocks.POLISHED_BASALT, TexturedModel.CUBE_COLUMN);
		this.registerSimpleCubeAll(Blocks.SMOOTH_BASALT);
		this.registerAxisRotated(Blocks.BONE_BLOCK, TexturedModel.CUBE_COLUMN);
		this.registerRotatable(Blocks.DIRT);
		this.registerRotatable(Blocks.ROOTED_DIRT);
		this.registerRotatable(Blocks.SAND);
		this.registerBrushableBlock(Blocks.SUSPICIOUS_SAND);
		this.registerBrushableBlock(Blocks.SUSPICIOUS_GRAVEL);
		this.registerRotatable(Blocks.RED_SAND);
		this.registerMirrorable(Blocks.BEDROCK);
		this.registerSingleton(Blocks.REINFORCED_DEEPSLATE, TexturedModel.CUBE_BOTTOM_TOP);
		this.registerAxisRotated(Blocks.HAY_BLOCK, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
		this.registerAxisRotated(Blocks.PURPUR_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
		this.registerAxisRotated(Blocks.QUARTZ_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
		this.registerAxisRotated(Blocks.OCHRE_FROGLIGHT, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
		this.registerAxisRotated(Blocks.VERDANT_FROGLIGHT, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
		this.registerAxisRotated(Blocks.PEARLESCENT_FROGLIGHT, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
		this.registerNorthDefaultHorizontalRotated(Blocks.LOOM, TexturedModel.ORIENTABLE_WITH_BOTTOM);
		this.registerPumpkins();
		this.registerBeehive(Blocks.BEE_NEST, TextureMap::sideFrontTopBottom);
		this.registerBeehive(Blocks.BEEHIVE, TextureMap::sideFrontEnd);
		this.registerCrop(Blocks.BEETROOTS, Properties.AGE_3, 0, 1, 2, 3);
		this.registerCrop(Blocks.CARROTS, Properties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
		this.registerCrop(Blocks.NETHER_WART, Properties.AGE_3, 0, 1, 1, 2);
		this.registerCrop(Blocks.POTATOES, Properties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
		this.registerCrop(Blocks.WHEAT, Properties.AGE_7, 0, 1, 2, 3, 4, 5, 6, 7);
		this.registerTintableCrossBlockStateWithStages(Blocks.TORCHFLOWER_CROP, BlockStateModelGenerator.TintType.NOT_TINTED, Properties.AGE_1, 0, 1);
		this.registerPitcherCrop();
		this.registerPitcherPlant();
		this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("decorated_pot"), Blocks.TERRACOTTA).includeWithoutItem(Blocks.DECORATED_POT);
		this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("banner"), Blocks.OAK_PLANKS)
			.includeWithItem(
				Models.TEMPLATE_BANNER,
				Blocks.WHITE_BANNER,
				Blocks.ORANGE_BANNER,
				Blocks.MAGENTA_BANNER,
				Blocks.LIGHT_BLUE_BANNER,
				Blocks.YELLOW_BANNER,
				Blocks.LIME_BANNER,
				Blocks.PINK_BANNER,
				Blocks.GRAY_BANNER,
				Blocks.LIGHT_GRAY_BANNER,
				Blocks.CYAN_BANNER,
				Blocks.PURPLE_BANNER,
				Blocks.BLUE_BANNER,
				Blocks.BROWN_BANNER,
				Blocks.GREEN_BANNER,
				Blocks.RED_BANNER,
				Blocks.BLACK_BANNER
			)
			.includeWithoutItem(
				Blocks.WHITE_WALL_BANNER,
				Blocks.ORANGE_WALL_BANNER,
				Blocks.MAGENTA_WALL_BANNER,
				Blocks.LIGHT_BLUE_WALL_BANNER,
				Blocks.YELLOW_WALL_BANNER,
				Blocks.LIME_WALL_BANNER,
				Blocks.PINK_WALL_BANNER,
				Blocks.GRAY_WALL_BANNER,
				Blocks.LIGHT_GRAY_WALL_BANNER,
				Blocks.CYAN_WALL_BANNER,
				Blocks.PURPLE_WALL_BANNER,
				Blocks.BLUE_WALL_BANNER,
				Blocks.BROWN_WALL_BANNER,
				Blocks.GREEN_WALL_BANNER,
				Blocks.RED_WALL_BANNER,
				Blocks.BLACK_WALL_BANNER
			);
		this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("bed"), Blocks.OAK_PLANKS)
			.includeWithoutItem(
				Blocks.WHITE_BED,
				Blocks.ORANGE_BED,
				Blocks.MAGENTA_BED,
				Blocks.LIGHT_BLUE_BED,
				Blocks.YELLOW_BED,
				Blocks.LIME_BED,
				Blocks.PINK_BED,
				Blocks.GRAY_BED,
				Blocks.LIGHT_GRAY_BED,
				Blocks.CYAN_BED,
				Blocks.PURPLE_BED,
				Blocks.BLUE_BED,
				Blocks.BROWN_BED,
				Blocks.GREEN_BED,
				Blocks.RED_BED,
				Blocks.BLACK_BED
			);
		this.registerBed(Blocks.WHITE_BED, Blocks.WHITE_WOOL);
		this.registerBed(Blocks.ORANGE_BED, Blocks.ORANGE_WOOL);
		this.registerBed(Blocks.MAGENTA_BED, Blocks.MAGENTA_WOOL);
		this.registerBed(Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
		this.registerBed(Blocks.YELLOW_BED, Blocks.YELLOW_WOOL);
		this.registerBed(Blocks.LIME_BED, Blocks.LIME_WOOL);
		this.registerBed(Blocks.PINK_BED, Blocks.PINK_WOOL);
		this.registerBed(Blocks.GRAY_BED, Blocks.GRAY_WOOL);
		this.registerBed(Blocks.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
		this.registerBed(Blocks.CYAN_BED, Blocks.CYAN_WOOL);
		this.registerBed(Blocks.PURPLE_BED, Blocks.PURPLE_WOOL);
		this.registerBed(Blocks.BLUE_BED, Blocks.BLUE_WOOL);
		this.registerBed(Blocks.BROWN_BED, Blocks.BROWN_WOOL);
		this.registerBed(Blocks.GREEN_BED, Blocks.GREEN_WOOL);
		this.registerBed(Blocks.RED_BED, Blocks.RED_WOOL);
		this.registerBed(Blocks.BLACK_BED, Blocks.BLACK_WOOL);
		this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("skull"), Blocks.SOUL_SAND)
			.includeWithItem(
				Models.TEMPLATE_SKULL, Blocks.CREEPER_HEAD, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PIGLIN_HEAD
			)
			.includeWithItem(Blocks.DRAGON_HEAD)
			.includeWithoutItem(
				Blocks.CREEPER_WALL_HEAD,
				Blocks.DRAGON_WALL_HEAD,
				Blocks.PLAYER_WALL_HEAD,
				Blocks.ZOMBIE_WALL_HEAD,
				Blocks.SKELETON_WALL_SKULL,
				Blocks.WITHER_SKELETON_WALL_SKULL,
				Blocks.PIGLIN_WALL_HEAD
			);
		this.registerShulkerBox(Blocks.SHULKER_BOX);
		this.registerShulkerBox(Blocks.WHITE_SHULKER_BOX);
		this.registerShulkerBox(Blocks.ORANGE_SHULKER_BOX);
		this.registerShulkerBox(Blocks.MAGENTA_SHULKER_BOX);
		this.registerShulkerBox(Blocks.LIGHT_BLUE_SHULKER_BOX);
		this.registerShulkerBox(Blocks.YELLOW_SHULKER_BOX);
		this.registerShulkerBox(Blocks.LIME_SHULKER_BOX);
		this.registerShulkerBox(Blocks.PINK_SHULKER_BOX);
		this.registerShulkerBox(Blocks.GRAY_SHULKER_BOX);
		this.registerShulkerBox(Blocks.LIGHT_GRAY_SHULKER_BOX);
		this.registerShulkerBox(Blocks.CYAN_SHULKER_BOX);
		this.registerShulkerBox(Blocks.PURPLE_SHULKER_BOX);
		this.registerShulkerBox(Blocks.BLUE_SHULKER_BOX);
		this.registerShulkerBox(Blocks.BROWN_SHULKER_BOX);
		this.registerShulkerBox(Blocks.GREEN_SHULKER_BOX);
		this.registerShulkerBox(Blocks.RED_SHULKER_BOX);
		this.registerShulkerBox(Blocks.BLACK_SHULKER_BOX);
		this.registerSingleton(Blocks.CONDUIT, TexturedModel.PARTICLE);
		this.excludeFromSimpleItemModelGeneration(Blocks.CONDUIT);
		this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("chest"), Blocks.OAK_PLANKS).includeWithoutItem(Blocks.CHEST, Blocks.TRAPPED_CHEST);
		this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("ender_chest"), Blocks.OBSIDIAN).includeWithoutItem(Blocks.ENDER_CHEST);
		this.registerBuiltin(Blocks.END_PORTAL, Blocks.OBSIDIAN).includeWithItem(Blocks.END_PORTAL, Blocks.END_GATEWAY);
		this.registerSimpleCubeAll(Blocks.AZALEA_LEAVES);
		this.registerSimpleCubeAll(Blocks.FLOWERING_AZALEA_LEAVES);
		this.registerSimpleCubeAll(Blocks.WHITE_CONCRETE);
		this.registerSimpleCubeAll(Blocks.ORANGE_CONCRETE);
		this.registerSimpleCubeAll(Blocks.MAGENTA_CONCRETE);
		this.registerSimpleCubeAll(Blocks.LIGHT_BLUE_CONCRETE);
		this.registerSimpleCubeAll(Blocks.YELLOW_CONCRETE);
		this.registerSimpleCubeAll(Blocks.LIME_CONCRETE);
		this.registerSimpleCubeAll(Blocks.PINK_CONCRETE);
		this.registerSimpleCubeAll(Blocks.GRAY_CONCRETE);
		this.registerSimpleCubeAll(Blocks.LIGHT_GRAY_CONCRETE);
		this.registerSimpleCubeAll(Blocks.CYAN_CONCRETE);
		this.registerSimpleCubeAll(Blocks.PURPLE_CONCRETE);
		this.registerSimpleCubeAll(Blocks.BLUE_CONCRETE);
		this.registerSimpleCubeAll(Blocks.BROWN_CONCRETE);
		this.registerSimpleCubeAll(Blocks.GREEN_CONCRETE);
		this.registerSimpleCubeAll(Blocks.RED_CONCRETE);
		this.registerSimpleCubeAll(Blocks.BLACK_CONCRETE);
		this.registerRandomHorizontalRotations(
			TexturedModel.CUBE_ALL,
			Blocks.WHITE_CONCRETE_POWDER,
			Blocks.ORANGE_CONCRETE_POWDER,
			Blocks.MAGENTA_CONCRETE_POWDER,
			Blocks.LIGHT_BLUE_CONCRETE_POWDER,
			Blocks.YELLOW_CONCRETE_POWDER,
			Blocks.LIME_CONCRETE_POWDER,
			Blocks.PINK_CONCRETE_POWDER,
			Blocks.GRAY_CONCRETE_POWDER,
			Blocks.LIGHT_GRAY_CONCRETE_POWDER,
			Blocks.CYAN_CONCRETE_POWDER,
			Blocks.PURPLE_CONCRETE_POWDER,
			Blocks.BLUE_CONCRETE_POWDER,
			Blocks.BROWN_CONCRETE_POWDER,
			Blocks.GREEN_CONCRETE_POWDER,
			Blocks.RED_CONCRETE_POWDER,
			Blocks.BLACK_CONCRETE_POWDER
		);
		this.registerSimpleCubeAll(Blocks.TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.WHITE_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.ORANGE_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.MAGENTA_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.LIGHT_BLUE_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.YELLOW_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.LIME_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.PINK_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.GRAY_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.LIGHT_GRAY_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.CYAN_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.PURPLE_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.BLUE_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.BROWN_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.GREEN_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.RED_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.BLACK_TERRACOTTA);
		this.registerSimpleCubeAll(Blocks.TINTED_GLASS);
		this.registerGlassPane(Blocks.GLASS, Blocks.GLASS_PANE);
		this.registerGlassPane(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.PINK_STAINED_GLASS, Blocks.PINK_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE);
		this.registerGlassPane(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE);
		this.registerSouthDefaultHorizontalFacing(
			TexturedModel.TEMPLATE_GLAZED_TERRACOTTA,
			Blocks.WHITE_GLAZED_TERRACOTTA,
			Blocks.ORANGE_GLAZED_TERRACOTTA,
			Blocks.MAGENTA_GLAZED_TERRACOTTA,
			Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
			Blocks.YELLOW_GLAZED_TERRACOTTA,
			Blocks.LIME_GLAZED_TERRACOTTA,
			Blocks.PINK_GLAZED_TERRACOTTA,
			Blocks.GRAY_GLAZED_TERRACOTTA,
			Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA,
			Blocks.CYAN_GLAZED_TERRACOTTA,
			Blocks.PURPLE_GLAZED_TERRACOTTA,
			Blocks.BLUE_GLAZED_TERRACOTTA,
			Blocks.BROWN_GLAZED_TERRACOTTA,
			Blocks.GREEN_GLAZED_TERRACOTTA,
			Blocks.RED_GLAZED_TERRACOTTA,
			Blocks.BLACK_GLAZED_TERRACOTTA
		);
		this.registerWoolAndCarpet(Blocks.WHITE_WOOL, Blocks.WHITE_CARPET);
		this.registerWoolAndCarpet(Blocks.ORANGE_WOOL, Blocks.ORANGE_CARPET);
		this.registerWoolAndCarpet(Blocks.MAGENTA_WOOL, Blocks.MAGENTA_CARPET);
		this.registerWoolAndCarpet(Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_CARPET);
		this.registerWoolAndCarpet(Blocks.YELLOW_WOOL, Blocks.YELLOW_CARPET);
		this.registerWoolAndCarpet(Blocks.LIME_WOOL, Blocks.LIME_CARPET);
		this.registerWoolAndCarpet(Blocks.PINK_WOOL, Blocks.PINK_CARPET);
		this.registerWoolAndCarpet(Blocks.GRAY_WOOL, Blocks.GRAY_CARPET);
		this.registerWoolAndCarpet(Blocks.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_CARPET);
		this.registerWoolAndCarpet(Blocks.CYAN_WOOL, Blocks.CYAN_CARPET);
		this.registerWoolAndCarpet(Blocks.PURPLE_WOOL, Blocks.PURPLE_CARPET);
		this.registerWoolAndCarpet(Blocks.BLUE_WOOL, Blocks.BLUE_CARPET);
		this.registerWoolAndCarpet(Blocks.BROWN_WOOL, Blocks.BROWN_CARPET);
		this.registerWoolAndCarpet(Blocks.GREEN_WOOL, Blocks.GREEN_CARPET);
		this.registerWoolAndCarpet(Blocks.RED_WOOL, Blocks.RED_CARPET);
		this.registerWoolAndCarpet(Blocks.BLACK_WOOL, Blocks.BLACK_CARPET);
		this.registerSimpleCubeAll(Blocks.MUD);
		this.registerSimpleCubeAll(Blocks.PACKED_MUD);
		this.registerFlowerPotPlant(Blocks.FERN, Blocks.POTTED_FERN, BlockStateModelGenerator.TintType.TINTED);
		this.registerFlowerPotPlant(Blocks.DANDELION, Blocks.POTTED_DANDELION, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.POPPY, Blocks.POTTED_POPPY, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.BLUE_ORCHID, Blocks.POTTED_BLUE_ORCHID, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.ALLIUM, Blocks.POTTED_ALLIUM, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.AZURE_BLUET, Blocks.POTTED_AZURE_BLUET, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.RED_TULIP, Blocks.POTTED_RED_TULIP, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.ORANGE_TULIP, Blocks.POTTED_ORANGE_TULIP, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.WHITE_TULIP, Blocks.POTTED_WHITE_TULIP, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.PINK_TULIP, Blocks.POTTED_PINK_TULIP, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.OXEYE_DAISY, Blocks.POTTED_OXEYE_DAISY, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.CORNFLOWER, Blocks.POTTED_CORNFLOWER, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.LILY_OF_THE_VALLEY, Blocks.POTTED_LILY_OF_THE_VALLEY, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.WITHER_ROSE, Blocks.POTTED_WITHER_ROSE, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.BROWN_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.DEAD_BUSH, Blocks.POTTED_DEAD_BUSH, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerFlowerPotPlant(Blocks.TORCHFLOWER, Blocks.POTTED_TORCHFLOWER, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerPointedDripstone();
		this.registerMushroomBlock(Blocks.BROWN_MUSHROOM_BLOCK);
		this.registerMushroomBlock(Blocks.RED_MUSHROOM_BLOCK);
		this.registerMushroomBlock(Blocks.MUSHROOM_STEM);
		this.registerTintableCross(Blocks.GRASS, BlockStateModelGenerator.TintType.TINTED);
		this.registerTintableCrossBlockState(Blocks.SUGAR_CANE, BlockStateModelGenerator.TintType.TINTED);
		this.registerItemModel(Items.SUGAR_CANE);
		this.registerPlantPart(Blocks.KELP, Blocks.KELP_PLANT, BlockStateModelGenerator.TintType.TINTED);
		this.registerItemModel(Items.KELP);
		this.excludeFromSimpleItemModelGeneration(Blocks.KELP_PLANT);
		this.registerTintableCrossBlockState(Blocks.HANGING_ROOTS, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.excludeFromSimpleItemModelGeneration(Blocks.HANGING_ROOTS);
		this.excludeFromSimpleItemModelGeneration(Blocks.CAVE_VINES_PLANT);
		this.registerPlantPart(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerPlantPart(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerItemModel(Blocks.WEEPING_VINES, "_plant");
		this.excludeFromSimpleItemModelGeneration(Blocks.WEEPING_VINES_PLANT);
		this.registerItemModel(Blocks.TWISTING_VINES, "_plant");
		this.excludeFromSimpleItemModelGeneration(Blocks.TWISTING_VINES_PLANT);
		this.registerTintableCross(Blocks.BAMBOO_SAPLING, BlockStateModelGenerator.TintType.TINTED, TextureMap.cross(TextureMap.getSubId(Blocks.BAMBOO, "_stage0")));
		this.registerBamboo();
		this.registerTintableCross(Blocks.COBWEB, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerDoubleBlock(Blocks.LILAC, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerDoubleBlock(Blocks.ROSE_BUSH, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerDoubleBlock(Blocks.PEONY, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerDoubleBlock(Blocks.TALL_GRASS, BlockStateModelGenerator.TintType.TINTED);
		this.registerDoubleBlock(Blocks.LARGE_FERN, BlockStateModelGenerator.TintType.TINTED);
		this.registerSunflower();
		this.registerTallSeagrass();
		this.registerSmallDripleaf();
		this.registerCoral(
			Blocks.TUBE_CORAL,
			Blocks.DEAD_TUBE_CORAL,
			Blocks.TUBE_CORAL_BLOCK,
			Blocks.DEAD_TUBE_CORAL_BLOCK,
			Blocks.TUBE_CORAL_FAN,
			Blocks.DEAD_TUBE_CORAL_FAN,
			Blocks.TUBE_CORAL_WALL_FAN,
			Blocks.DEAD_TUBE_CORAL_WALL_FAN
		);
		this.registerCoral(
			Blocks.BRAIN_CORAL,
			Blocks.DEAD_BRAIN_CORAL,
			Blocks.BRAIN_CORAL_BLOCK,
			Blocks.DEAD_BRAIN_CORAL_BLOCK,
			Blocks.BRAIN_CORAL_FAN,
			Blocks.DEAD_BRAIN_CORAL_FAN,
			Blocks.BRAIN_CORAL_WALL_FAN,
			Blocks.DEAD_BRAIN_CORAL_WALL_FAN
		);
		this.registerCoral(
			Blocks.BUBBLE_CORAL,
			Blocks.DEAD_BUBBLE_CORAL,
			Blocks.BUBBLE_CORAL_BLOCK,
			Blocks.DEAD_BUBBLE_CORAL_BLOCK,
			Blocks.BUBBLE_CORAL_FAN,
			Blocks.DEAD_BUBBLE_CORAL_FAN,
			Blocks.BUBBLE_CORAL_WALL_FAN,
			Blocks.DEAD_BUBBLE_CORAL_WALL_FAN
		);
		this.registerCoral(
			Blocks.FIRE_CORAL,
			Blocks.DEAD_FIRE_CORAL,
			Blocks.FIRE_CORAL_BLOCK,
			Blocks.DEAD_FIRE_CORAL_BLOCK,
			Blocks.FIRE_CORAL_FAN,
			Blocks.DEAD_FIRE_CORAL_FAN,
			Blocks.FIRE_CORAL_WALL_FAN,
			Blocks.DEAD_FIRE_CORAL_WALL_FAN
		);
		this.registerCoral(
			Blocks.HORN_CORAL,
			Blocks.DEAD_HORN_CORAL,
			Blocks.HORN_CORAL_BLOCK,
			Blocks.DEAD_HORN_CORAL_BLOCK,
			Blocks.HORN_CORAL_FAN,
			Blocks.DEAD_HORN_CORAL_FAN,
			Blocks.HORN_CORAL_WALL_FAN,
			Blocks.DEAD_HORN_CORAL_WALL_FAN
		);
		this.registerGourd(Blocks.MELON_STEM, Blocks.ATTACHED_MELON_STEM);
		this.registerGourd(Blocks.PUMPKIN_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
		this.registerLog(Blocks.MANGROVE_LOG).log(Blocks.MANGROVE_LOG).wood(Blocks.MANGROVE_WOOD);
		this.registerLog(Blocks.STRIPPED_MANGROVE_LOG).log(Blocks.STRIPPED_MANGROVE_LOG).wood(Blocks.STRIPPED_MANGROVE_WOOD);
		this.registerHangingSign(Blocks.STRIPPED_MANGROVE_LOG, Blocks.MANGROVE_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN);
		this.registerSingleton(Blocks.MANGROVE_LEAVES, TexturedModel.LEAVES);
		this.registerLog(Blocks.ACACIA_LOG).log(Blocks.ACACIA_LOG).wood(Blocks.ACACIA_WOOD);
		this.registerLog(Blocks.STRIPPED_ACACIA_LOG).log(Blocks.STRIPPED_ACACIA_LOG).wood(Blocks.STRIPPED_ACACIA_WOOD);
		this.registerHangingSign(Blocks.STRIPPED_ACACIA_LOG, Blocks.ACACIA_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN);
		this.registerFlowerPotPlant(Blocks.ACACIA_SAPLING, Blocks.POTTED_ACACIA_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerSingleton(Blocks.ACACIA_LEAVES, TexturedModel.LEAVES);
		this.registerLog(Blocks.CHERRY_LOG).uvLockedLog(Blocks.CHERRY_LOG).wood(Blocks.CHERRY_WOOD);
		this.registerLog(Blocks.STRIPPED_CHERRY_LOG).uvLockedLog(Blocks.STRIPPED_CHERRY_LOG).wood(Blocks.STRIPPED_CHERRY_WOOD);
		this.registerHangingSign(Blocks.STRIPPED_CHERRY_LOG, Blocks.CHERRY_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN);
		this.registerFlowerPotPlant(Blocks.CHERRY_SAPLING, Blocks.POTTED_CHERRY_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerSingleton(Blocks.CHERRY_LEAVES, TexturedModel.LEAVES);
		this.registerSpleaves(Blocks.COPPER_SPLEAVES);
		this.registerLog(Blocks.BIRCH_LOG).log(Blocks.BIRCH_LOG).wood(Blocks.BIRCH_WOOD);
		this.registerLog(Blocks.STRIPPED_BIRCH_LOG).log(Blocks.STRIPPED_BIRCH_LOG).wood(Blocks.STRIPPED_BIRCH_WOOD);
		this.registerHangingSign(Blocks.STRIPPED_BIRCH_LOG, Blocks.BIRCH_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN);
		this.registerFlowerPotPlant(Blocks.BIRCH_SAPLING, Blocks.POTTED_BIRCH_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerSingleton(Blocks.BIRCH_LEAVES, TexturedModel.LEAVES);
		this.registerLog(Blocks.OAK_LOG).log(Blocks.OAK_LOG).wood(Blocks.OAK_WOOD);
		this.registerLog(Blocks.STRIPPED_OAK_LOG).log(Blocks.STRIPPED_OAK_LOG).wood(Blocks.STRIPPED_OAK_WOOD);
		this.registerHangingSign(Blocks.STRIPPED_OAK_LOG, Blocks.OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN);
		this.registerFlowerPotPlant(Blocks.OAK_SAPLING, Blocks.POTTED_OAK_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerSingleton(Blocks.OAK_LEAVES, TexturedModel.LEAVES);
		this.registerLog(Blocks.SPRUCE_LOG).log(Blocks.SPRUCE_LOG).wood(Blocks.SPRUCE_WOOD);
		this.registerLog(Blocks.STRIPPED_SPRUCE_LOG).log(Blocks.STRIPPED_SPRUCE_LOG).wood(Blocks.STRIPPED_SPRUCE_WOOD);
		this.registerHangingSign(Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN);
		this.registerFlowerPotPlant(Blocks.SPRUCE_SAPLING, Blocks.POTTED_SPRUCE_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerSingleton(Blocks.SPRUCE_LEAVES, TexturedModel.LEAVES);
		this.registerLog(Blocks.DARK_OAK_LOG).log(Blocks.DARK_OAK_LOG).wood(Blocks.DARK_OAK_WOOD);
		this.registerLog(Blocks.STRIPPED_DARK_OAK_LOG).log(Blocks.STRIPPED_DARK_OAK_LOG).wood(Blocks.STRIPPED_DARK_OAK_WOOD);
		this.registerHangingSign(Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN);
		this.registerFlowerPotPlant(Blocks.DARK_OAK_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerSingleton(Blocks.DARK_OAK_LEAVES, TexturedModel.LEAVES);
		this.registerLog(Blocks.JUNGLE_LOG).log(Blocks.JUNGLE_LOG).wood(Blocks.JUNGLE_WOOD);
		this.registerLog(Blocks.STRIPPED_JUNGLE_LOG).log(Blocks.STRIPPED_JUNGLE_LOG).wood(Blocks.STRIPPED_JUNGLE_WOOD);
		this.registerHangingSign(Blocks.STRIPPED_JUNGLE_LOG, Blocks.JUNGLE_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN);
		this.registerFlowerPotPlant(Blocks.JUNGLE_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerSingleton(Blocks.JUNGLE_LEAVES, TexturedModel.LEAVES);
		this.registerLog(Blocks.CRIMSON_STEM).stem(Blocks.CRIMSON_STEM).wood(Blocks.CRIMSON_HYPHAE);
		this.registerLog(Blocks.STRIPPED_CRIMSON_STEM).stem(Blocks.STRIPPED_CRIMSON_STEM).wood(Blocks.STRIPPED_CRIMSON_HYPHAE);
		this.registerHangingSign(Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN);
		this.registerFlowerPotPlant(Blocks.CRIMSON_FUNGUS, Blocks.POTTED_CRIMSON_FUNGUS, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerRoots(Blocks.CRIMSON_ROOTS, Blocks.POTTED_CRIMSON_ROOTS);
		this.registerLog(Blocks.WARPED_STEM).stem(Blocks.WARPED_STEM).wood(Blocks.WARPED_HYPHAE);
		this.registerLog(Blocks.STRIPPED_WARPED_STEM).stem(Blocks.STRIPPED_WARPED_STEM).wood(Blocks.STRIPPED_WARPED_HYPHAE);
		this.registerHangingSign(Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN);
		this.registerFlowerPotPlant(Blocks.WARPED_FUNGUS, Blocks.POTTED_WARPED_FUNGUS, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerRoots(Blocks.WARPED_ROOTS, Blocks.POTTED_WARPED_ROOTS);
		this.registerLog(Blocks.BAMBOO_BLOCK).uvLockedLog(Blocks.BAMBOO_BLOCK);
		this.registerLog(Blocks.STRIPPED_BAMBOO_BLOCK).uvLockedLog(Blocks.STRIPPED_BAMBOO_BLOCK);
		this.registerHangingSign(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
		this.registerTintableCrossBlockState(Blocks.NETHER_SPROUTS, BlockStateModelGenerator.TintType.NOT_TINTED);
		this.registerItemModel(Items.NETHER_SPROUTS);
		this.registerDoor(Blocks.IRON_DOOR);
		this.registerTrapdoor(Blocks.IRON_TRAPDOOR);
		this.registerSmoothStone();
		this.registerTurnableRail(Blocks.RAIL);
		this.registerStraightRail(Blocks.POWERED_RAIL);
		this.registerStraightRail(Blocks.DETECTOR_RAIL);
		this.registerStraightRail(Blocks.ACTIVATOR_RAIL);
		this.registerComparator();
		this.registerCommandBlock(Blocks.COMMAND_BLOCK);
		this.registerCommandBlock(Blocks.REPEATING_COMMAND_BLOCK);
		this.registerCommandBlock(Blocks.CHAIN_COMMAND_BLOCK);
		this.registerAnvil(Blocks.ANVIL);
		this.registerAnvil(Blocks.CHIPPED_ANVIL);
		this.registerAnvil(Blocks.DAMAGED_ANVIL);
		this.registerBarrel();
		this.registerBell();
		this.registerCooker(Blocks.FURNACE, TexturedModel.ORIENTABLE);
		this.registerCooker(Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE);
		this.registerCooker(Blocks.SMOKER, TexturedModel.ORIENTABLE_WITH_BOTTOM);
		this.registerRedstone();
		this.registerRespawnAnchor();
		this.registerSculkCatalyst();
		this.registerParented(Blocks.CHISELED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS);
		this.registerParented(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE);
		this.registerParented(Blocks.CRACKED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
		this.registerParented(Blocks.MOSSY_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS);
		this.registerInfestedStone();
		this.registerParented(Blocks.STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS);
		this.registerInfestedDeepslate();
		SpawnEggItem.getAll().forEach(item -> this.registerParentedItemModel(item, ModelIds.getMinecraftNamespacedItem("template_spawn_egg")));
	}

	private void registerLightBlock() {
		this.excludeFromSimpleItemModelGeneration(Blocks.LIGHT);
		BlockStateVariantMap.SingleProperty<Integer> singleProperty = BlockStateVariantMap.create(Properties.LEVEL_15);

		for (int i = 0; i < 16; i++) {
			String string = String.format(Locale.ROOT, "_%02d", i);
			Identifier identifier = TextureMap.getSubId(Items.LIGHT, string);
			singleProperty.register(
				i,
				BlockStateVariant.create().put(VariantSettings.MODEL, Models.PARTICLE.upload(Blocks.LIGHT, string, TextureMap.particle(identifier), this.modelCollector))
			);
			Models.GENERATED.upload(ModelIds.getItemSubModelId(Items.LIGHT, string), TextureMap.layer0(identifier), this.modelCollector);
		}

		this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.LIGHT).coordinate(singleProperty));
	}

	private void registerCandle(Block candle, Block cake) {
		this.registerItemModel(candle.asItem());
		TextureMap textureMap = TextureMap.all(TextureMap.getId(candle));
		TextureMap textureMap2 = TextureMap.all(TextureMap.getSubId(candle, "_lit"));
		Identifier identifier = Models.TEMPLATE_CANDLE.upload(candle, "_one_candle", textureMap, this.modelCollector);
		Identifier identifier2 = Models.TEMPLATE_TWO_CANDLES.upload(candle, "_two_candles", textureMap, this.modelCollector);
		Identifier identifier3 = Models.TEMPLATE_THREE_CANDLES.upload(candle, "_three_candles", textureMap, this.modelCollector);
		Identifier identifier4 = Models.TEMPLATE_FOUR_CANDLES.upload(candle, "_four_candles", textureMap, this.modelCollector);
		Identifier identifier5 = Models.TEMPLATE_CANDLE.upload(candle, "_one_candle_lit", textureMap2, this.modelCollector);
		Identifier identifier6 = Models.TEMPLATE_TWO_CANDLES.upload(candle, "_two_candles_lit", textureMap2, this.modelCollector);
		Identifier identifier7 = Models.TEMPLATE_THREE_CANDLES.upload(candle, "_three_candles_lit", textureMap2, this.modelCollector);
		Identifier identifier8 = Models.TEMPLATE_FOUR_CANDLES.upload(candle, "_four_candles_lit", textureMap2, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(candle)
					.coordinate(
						BlockStateVariantMap.create(Properties.CANDLES, Properties.LIT)
							.register(1, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(2, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
							.register(3, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
							.register(4, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier4))
							.register(1, true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier5))
							.register(2, true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier6))
							.register(3, true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier7))
							.register(4, true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier8))
					)
			);
		Identifier identifier9 = Models.TEMPLATE_CAKE_WITH_CANDLE.upload(cake, TextureMap.candleCake(candle, false), this.modelCollector);
		Identifier identifier10 = Models.TEMPLATE_CAKE_WITH_CANDLE.upload(cake, "_lit", TextureMap.candleCake(candle, true), this.modelCollector);
		this.blockStateCollector.accept(VariantsBlockStateSupplier.create(cake).coordinate(createBooleanModelMap(Properties.LIT, identifier10, identifier9)));
	}

	private void registerCheese() {
		Identifier identifier = TextureMap.getId(Blocks.CHEESE);
		BlockStateVariantMap.SingleProperty<Integer> singleProperty = BlockStateVariantMap.create(CheeseBlock.SLICES);

		for (int i : CheeseBlock.SLICES.getValues()) {
			Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.CHEESE, "_" + Integer.toBinaryString(i));
			singleProperty.register(i, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2));
			VoxelShape voxelShape = CheeseBlock.SHAPES[i];
			this.modelCollector.accept(identifier2, (Supplier)() -> createModelFromShape(identifier, voxelShape));
		}

		this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.CHEESE).coordinate(singleProperty));
		this.registerParentedItemModel(Blocks.CHEESE, ModelIds.getBlockSubModelId(Blocks.CHEESE, "_" + Integer.toBinaryString(255)));
	}

	private static JsonObject createModelFromShape(Identifier id, VoxelShape shape) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("parent", "block/block");
		JsonObject jsonObject2 = new JsonObject();
		jsonObject2.addProperty("all", id.toString());
		jsonObject2.addProperty("particle", id.toString());
		jsonObject.add("textures", jsonObject2);
		JsonArray jsonArray = new JsonArray();
		shape.forEachBox((fromX, fromY, fromZ, toX, toY, toZ) -> {
			JsonObject jsonObjectx = new JsonObject();
			jsonObjectx.add("from", Util.make(new JsonArray(), from -> {
				from.add(fromX * 16.0);
				from.add(fromY * 16.0);
				from.add(fromZ * 16.0);
			}));
			jsonObjectx.add("to", Util.make(new JsonArray(), to -> {
				to.add(toX * 16.0);
				to.add(toY * 16.0);
				to.add(toZ * 16.0);
			}));
			JsonObject jsonObject2x = new JsonObject();

			for (Direction direction : Direction.values()) {
				JsonObject jsonObject3 = new JsonObject();
				jsonObject3.addProperty("texture", "#all");
				if (shouldCullFace(fromX, fromY, fromZ, toX, toY, toZ, direction)) {
					jsonObject3.addProperty("cullface", direction.asString());
				}

				jsonObject2x.add(direction.asString(), jsonObject3);
			}

			jsonObjectx.add("faces", jsonObject2x);
			jsonArray.add(jsonObjectx);
		});
		jsonObject.add("elements", jsonArray);
		return jsonObject;
	}

	private static boolean shouldCullFace(double fromX, double fromY, double fromZ, double toX, double toY, double toZ, Direction face) {
		if (face.getDirection() == Direction.AxisDirection.POSITIVE) {
			double d = face.getAxis().choose(toX, toY, toZ);
			return d >= 0.99999F;
		} else {
			double d = face.getAxis().choose(fromX, fromY, fromZ);
			return d <= 1.0E-5F;
		}
	}

	class BlockTexturePool {
		private final TextureMap textures;
		private final Map<Model, Identifier> knownModels = Maps.<Model, Identifier>newHashMap();
		@Nullable
		private BlockFamily family;
		@Nullable
		private Identifier baseModelId;

		public BlockTexturePool(TextureMap textures) {
			this.textures = textures;
		}

		public BlockStateModelGenerator.BlockTexturePool base(Block block, Model model) {
			this.baseModelId = model.upload(block, this.textures, BlockStateModelGenerator.this.modelCollector);
			if (BlockStateModelGenerator.this.stoneStateFactories.containsKey(block)) {
				BlockStateModelGenerator.this.blockStateCollector
					.accept(
						((BlockStateModelGenerator.StateFactory)BlockStateModelGenerator.this.stoneStateFactories.get(block))
							.create(block, this.baseModelId, this.textures, BlockStateModelGenerator.this.modelCollector)
					);
			} else {
				BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, this.baseModelId));
			}

			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool same(Block... blocks) {
			if (this.baseModelId == null) {
				throw new IllegalStateException("Full block not generated yet");
			} else {
				for (Block block : blocks) {
					BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, this.baseModelId));
					BlockStateModelGenerator.this.registerParentedItemModel(block, this.baseModelId);
				}

				return this;
			}
		}

		public BlockStateModelGenerator.BlockTexturePool button(Block buttonBlock) {
			Identifier identifier = Models.BUTTON.upload(buttonBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.BUTTON_PRESSED.upload(buttonBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createButtonBlockState(buttonBlock, identifier, identifier2));
			Identifier identifier3 = Models.BUTTON_INVENTORY.upload(buttonBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.registerParentedItemModel(buttonBlock, identifier3);
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool wall(Block wallBlock) {
			Identifier identifier = Models.TEMPLATE_WALL_POST.upload(wallBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.TEMPLATE_WALL_SIDE.upload(wallBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier3 = Models.TEMPLATE_WALL_SIDE_TALL.upload(wallBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createWallBlockState(wallBlock, identifier, identifier2, identifier3));
			Identifier identifier4 = Models.WALL_INVENTORY.upload(wallBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.registerParentedItemModel(wallBlock, identifier4);
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool customFence(Block customFenceBlock) {
			TextureMap textureMap = TextureMap.textureParticle(customFenceBlock);
			Identifier identifier = Models.CUSTOM_FENCE_POST.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.CUSTOM_FENCE_SIDE_NORTH.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier3 = Models.CUSTOM_FENCE_SIDE_EAST.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier4 = Models.CUSTOM_FENCE_SIDE_SOUTH.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier5 = Models.CUSTOM_FENCE_SIDE_WEST.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector
				.accept(BlockStateModelGenerator.createCustomFenceBlockState(customFenceBlock, identifier, identifier2, identifier3, identifier4, identifier5));
			Identifier identifier6 = Models.CUSTOM_FENCE_INVENTORY.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.registerParentedItemModel(customFenceBlock, identifier6);
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool fence(Block fenceBlock) {
			Identifier identifier = Models.FENCE_POST.upload(fenceBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.FENCE_SIDE.upload(fenceBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createFenceBlockState(fenceBlock, identifier, identifier2));
			Identifier identifier3 = Models.FENCE_INVENTORY.upload(fenceBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.registerParentedItemModel(fenceBlock, identifier3);
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool customFenceGate(Block customFenceGateBlock) {
			TextureMap textureMap = TextureMap.textureParticle(customFenceGateBlock);
			Identifier identifier = Models.TEMPLATE_CUSTOM_FENCE_GATE_OPEN.upload(customFenceGateBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.TEMPLATE_CUSTOM_FENCE_GATE.upload(customFenceGateBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier3 = Models.TEMPLATE_CUSTOM_FENCE_GATE_WALL_OPEN.upload(customFenceGateBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier4 = Models.TEMPLATE_CUSTOM_FENCE_GATE_WALL.upload(customFenceGateBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector
				.accept(BlockStateModelGenerator.createFenceGateBlockState(customFenceGateBlock, identifier, identifier2, identifier3, identifier4, false));
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool fenceGate(Block fenceGateBlock) {
			Identifier identifier = Models.TEMPLATE_FENCE_GATE_OPEN.upload(fenceGateBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.TEMPLATE_FENCE_GATE.upload(fenceGateBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier3 = Models.TEMPLATE_FENCE_GATE_WALL_OPEN.upload(fenceGateBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier4 = Models.TEMPLATE_FENCE_GATE_WALL.upload(fenceGateBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector
				.accept(BlockStateModelGenerator.createFenceGateBlockState(fenceGateBlock, identifier, identifier2, identifier3, identifier4, true));
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool pressurePlate(Block pressurePlateBlock) {
			Identifier identifier = Models.PRESSURE_PLATE_UP.upload(pressurePlateBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.PRESSURE_PLATE_DOWN.upload(pressurePlateBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector
				.accept(BlockStateModelGenerator.createPressurePlateBlockState(pressurePlateBlock, identifier, identifier2));
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool sign(Block signBlock) {
			if (this.family == null) {
				throw new IllegalStateException("Family not defined");
			} else {
				Block block = (Block)this.family.getVariants().get(BlockFamily.Variant.WALL_SIGN);
				Identifier identifier = Models.PARTICLE.upload(signBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
				BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(signBlock, identifier));
				BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, identifier));
				BlockStateModelGenerator.this.registerItemModel(signBlock.asItem());
				BlockStateModelGenerator.this.excludeFromSimpleItemModelGeneration(block);
				return this;
			}
		}

		public BlockStateModelGenerator.BlockTexturePool slab(Block block) {
			if (this.baseModelId == null) {
				throw new IllegalStateException("Full block not generated yet");
			} else {
				Identifier identifier = this.ensureModel(Models.SLAB, block);
				Identifier identifier2 = this.ensureModel(Models.SLAB_TOP, block);
				BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(block, identifier, identifier2, this.baseModelId));
				BlockStateModelGenerator.this.registerParentedItemModel(block, identifier);
				return this;
			}
		}

		public BlockStateModelGenerator.BlockTexturePool stairs(Block block) {
			Identifier identifier = this.ensureModel(Models.INNER_STAIRS, block);
			Identifier identifier2 = this.ensureModel(Models.STAIRS, block);
			Identifier identifier3 = this.ensureModel(Models.OUTER_STAIRS, block);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createStairsBlockState(block, identifier, identifier2, identifier3));
			BlockStateModelGenerator.this.registerParentedItemModel(block, identifier2);
			return this;
		}

		private BlockStateModelGenerator.BlockTexturePool block(Block block) {
			TexturedModel texturedModel = (TexturedModel)BlockStateModelGenerator.this.texturedModels.getOrDefault(block, TexturedModel.CUBE_ALL.get(block));
			BlockStateModelGenerator.this.blockStateCollector
				.accept(BlockStateModelGenerator.createSingletonBlockState(block, texturedModel.upload(block, BlockStateModelGenerator.this.modelCollector)));
			return this;
		}

		private BlockStateModelGenerator.BlockTexturePool door(Block block) {
			BlockStateModelGenerator.this.registerDoor(block);
			return this;
		}

		private void registerTrapdoor(Block block) {
			if (BlockStateModelGenerator.this.nonOrientableTrapdoors.contains(block)) {
				BlockStateModelGenerator.this.registerTrapdoor(block);
			} else {
				BlockStateModelGenerator.this.registerOrientableTrapdoor(block);
			}
		}

		private Identifier ensureModel(Model model, Block block) {
			return (Identifier)this.knownModels.computeIfAbsent(model, newModel -> newModel.upload(block, this.textures, BlockStateModelGenerator.this.modelCollector));
		}

		public BlockStateModelGenerator.BlockTexturePool family(BlockFamily family) {
			this.family = family;
			family.getVariants()
				.forEach(
					(variant, block) -> {
						BiConsumer<BlockStateModelGenerator.BlockTexturePool, Block> biConsumer = (BiConsumer<BlockStateModelGenerator.BlockTexturePool, Block>)BlockStateModelGenerator.VARIANT_POOL_FUNCTIONS
							.get(variant);
						if (biConsumer != null) {
							biConsumer.accept(this, block);
						}
					}
				);
			return this;
		}
	}

	class BuiltinModelPool {
		private final Identifier modelId;

		public BuiltinModelPool(Identifier modelId, Block block) {
			this.modelId = Models.PARTICLE.upload(modelId, TextureMap.particle(block), BlockStateModelGenerator.this.modelCollector);
		}

		public BlockStateModelGenerator.BuiltinModelPool includeWithItem(Block... blocks) {
			for (Block block : blocks) {
				BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, this.modelId));
			}

			return this;
		}

		public BlockStateModelGenerator.BuiltinModelPool includeWithoutItem(Block... blocks) {
			for (Block block : blocks) {
				BlockStateModelGenerator.this.excludeFromSimpleItemModelGeneration(block);
			}

			return this.includeWithItem(blocks);
		}

		public BlockStateModelGenerator.BuiltinModelPool includeWithItem(Model model, Block... blocks) {
			for (Block block : blocks) {
				model.upload(ModelIds.getItemModelId(block.asItem()), TextureMap.particle(block), BlockStateModelGenerator.this.modelCollector);
			}

			return this.includeWithItem(blocks);
		}
	}

	static record ChiseledBookshelfModelCacheKey(Model template, String modelSuffix) {
	}

	class LogTexturePool {
		private final TextureMap textures;

		public LogTexturePool(TextureMap textures) {
			this.textures = textures;
		}

		public BlockStateModelGenerator.LogTexturePool wood(Block woodBlock) {
			TextureMap textureMap = this.textures.copyAndAdd(TextureKey.END, this.textures.getTexture(TextureKey.SIDE));
			Identifier identifier = Models.CUBE_COLUMN.upload(woodBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(woodBlock, identifier));
			return this;
		}

		public BlockStateModelGenerator.LogTexturePool stem(Block stemBlock) {
			Identifier identifier = Models.CUBE_COLUMN.upload(stemBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(stemBlock, identifier));
			return this;
		}

		public BlockStateModelGenerator.LogTexturePool log(Block logBlock) {
			Identifier identifier = Models.CUBE_COLUMN.upload(logBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.CUBE_COLUMN_HORIZONTAL.upload(logBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(logBlock, identifier, identifier2));
			return this;
		}

		public BlockStateModelGenerator.LogTexturePool uvLockedLog(Block logBlock) {
			BlockStateModelGenerator.this.blockStateCollector
				.accept(BlockStateModelGenerator.createUvLockedColumnBlockState(logBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
			return this;
		}
	}

	@FunctionalInterface
	interface StateFactory {
		BlockStateSupplier create(Block block, Identifier modelId, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector);
	}

	static enum TintType {
		TINTED,
		NOT_TINTED;

		public Model getCrossModel() {
			return this == TINTED ? Models.TINTED_CROSS : Models.CROSS;
		}

		public Model getFlowerPotCrossModel() {
			return this == TINTED ? Models.TINTED_FLOWER_POT_CROSS : Models.FLOWER_POT_CROSS;
		}
	}
}
