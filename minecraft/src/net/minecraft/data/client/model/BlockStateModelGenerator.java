package net.minecraft.data.client.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.List;
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
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.enums.WallShape;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class BlockStateModelGenerator {
	private final Consumer<BlockStateSupplier> blockStateCollector;
	private final BiConsumer<Identifier, Supplier<JsonElement>> modelCollector;
	private final Consumer<Item> simpleItemModelExemptionCollector;

	public BlockStateModelGenerator(
		Consumer<BlockStateSupplier> blockStateCollector,
		BiConsumer<Identifier, Supplier<JsonElement>> modelCollector,
		Consumer<Item> simpleItemModelExemptionCollector
	) {
		this.blockStateCollector = blockStateCollector;
		this.modelCollector = modelCollector;
		this.simpleItemModelExemptionCollector = simpleItemModelExemptionCollector;
	}

	private void excludeFromSimpleItemModelGeneration(Block block) {
		this.simpleItemModelExemptionCollector.accept(block.asItem());
	}

	private void registerParentedItemModel(Block block, Identifier parentModelId) {
		this.modelCollector.accept(ModelIds.getItemModelId(block.asItem()), new SimpleModelSupplier(parentModelId));
	}

	private void registerParentedItemModel(Item item, Identifier parentModelId) {
		this.modelCollector.accept(ModelIds.getItemModelId(item), new SimpleModelSupplier(parentModelId));
	}

	private void registerItemModel(Item item) {
		Models.field_22938.upload(ModelIds.getItemModelId(item), Texture.layer0(item), this.modelCollector);
	}

	private void registerItemModel(Block block) {
		Item item = block.asItem();
		if (item != Items.AIR) {
			Models.field_22938.upload(ModelIds.getItemModelId(item), Texture.layer0(block), this.modelCollector);
		}
	}

	private void registerItemModel(Block block, String textureSuffix) {
		Item item = block.asItem();
		Models.field_22938.upload(ModelIds.getItemModelId(item), Texture.layer0(Texture.getSubId(block, textureSuffix)), this.modelCollector);
	}

	private static BlockStateVariantMap createNorthDefaultHorizontalRotationStates() {
		return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
			.register(Direction.field_11034, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22891))
			.register(Direction.field_11035, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22892))
			.register(Direction.field_11039, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22893))
			.register(Direction.field_11043, BlockStateVariant.create());
	}

	private static BlockStateVariantMap createSouthDefaultHorizontalRotationStates() {
		return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
			.register(Direction.field_11035, BlockStateVariant.create())
			.register(Direction.field_11039, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22891))
			.register(Direction.field_11043, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22892))
			.register(Direction.field_11034, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22893));
	}

	private static BlockStateVariantMap createEastDefaultHorizontalRotationStates() {
		return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
			.register(Direction.field_11034, BlockStateVariant.create())
			.register(Direction.field_11035, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22891))
			.register(Direction.field_11039, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22892))
			.register(Direction.field_11043, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22893));
	}

	private static BlockStateVariantMap createNorthDefaultRotationStates() {
		return BlockStateVariantMap.create(Properties.FACING)
			.register(Direction.field_11033, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891))
			.register(Direction.field_11036, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22893))
			.register(Direction.field_11043, BlockStateVariant.create())
			.register(Direction.field_11035, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22892))
			.register(Direction.field_11039, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22893))
			.register(Direction.field_11034, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22891));
	}

	private static VariantsBlockStateSupplier createBlockStateWithRandomHorizontalRotations(Block block, Identifier modelId) {
		return VariantsBlockStateSupplier.create(block, createModelVariantWithRandomHorizontalRotations(modelId));
	}

	private static BlockStateVariant[] createModelVariantWithRandomHorizontalRotations(Identifier modelId) {
		return new BlockStateVariant[]{
			BlockStateVariant.create().put(VariantSettings.MODEL, modelId),
			BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22891),
			BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22892),
			BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
		};
	}

	private static VariantsBlockStateSupplier createBlockStateWithTwoModelAndRandomInversion(Block block, Identifier firstModelId, Identifier secondModelId) {
		return VariantsBlockStateSupplier.create(
			block,
			BlockStateVariant.create().put(VariantSettings.MODEL, firstModelId),
			BlockStateVariant.create().put(VariantSettings.MODEL, secondModelId),
			BlockStateVariant.create().put(VariantSettings.MODEL, firstModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22892),
			BlockStateVariant.create().put(VariantSettings.MODEL, secondModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
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

	private static BlockStateSupplier createButtonBlockState(Block buttonBlock, Identifier regularModelId, Identifier pressedModelId) {
		return VariantsBlockStateSupplier.create(buttonBlock)
			.coordinate(
				BlockStateVariantMap.create(Properties.POWERED)
					.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId))
					.register(true, BlockStateVariant.create().put(VariantSettings.MODEL, pressedModelId))
			)
			.coordinate(
				BlockStateVariantMap.create(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING)
					.register(WallMountLocation.field_12475, Direction.field_11034, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22891))
					.register(WallMountLocation.field_12475, Direction.field_11039, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22893))
					.register(WallMountLocation.field_12475, Direction.field_11035, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22892))
					.register(WallMountLocation.field_12475, Direction.field_11043, BlockStateVariant.create())
					.register(
						WallMountLocation.field_12471,
						Direction.field_11034,
						BlockStateVariant.create()
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						WallMountLocation.field_12471,
						Direction.field_11039,
						BlockStateVariant.create()
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						WallMountLocation.field_12471,
						Direction.field_11035,
						BlockStateVariant.create()
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						WallMountLocation.field_12471,
						Direction.field_11043,
						BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.UVLOCK, true)
					)
					.register(
						WallMountLocation.field_12473,
						Direction.field_11034,
						BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22893).put(VariantSettings.X, VariantSettings.Rotation.field_22892)
					)
					.register(
						WallMountLocation.field_12473,
						Direction.field_11039,
						BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22891).put(VariantSettings.X, VariantSettings.Rotation.field_22892)
					)
					.register(WallMountLocation.field_12473, Direction.field_11035, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22892))
					.register(
						WallMountLocation.field_12473,
						Direction.field_11043,
						BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22892).put(VariantSettings.X, VariantSettings.Rotation.field_22892)
					)
			);
	}

	private static BlockStateVariantMap.QuadrupleProperty<Direction, DoubleBlockHalf, DoorHinge, Boolean> fillDoorVariantMap(
		BlockStateVariantMap.QuadrupleProperty<Direction, DoubleBlockHalf, DoorHinge, Boolean> variantMap,
		DoubleBlockHalf targetHalf,
		Identifier regularModel,
		Identifier hingeModel
	) {
		return variantMap.register(
				Direction.field_11034, targetHalf, DoorHinge.field_12588, false, BlockStateVariant.create().put(VariantSettings.MODEL, regularModel)
			)
			.register(
				Direction.field_11035,
				targetHalf,
				DoorHinge.field_12588,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, regularModel).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
			)
			.register(
				Direction.field_11039,
				targetHalf,
				DoorHinge.field_12588,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, regularModel).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
			)
			.register(
				Direction.field_11043,
				targetHalf,
				DoorHinge.field_12588,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, regularModel).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
			)
			.register(Direction.field_11034, targetHalf, DoorHinge.field_12586, false, BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel))
			.register(
				Direction.field_11035,
				targetHalf,
				DoorHinge.field_12586,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
			)
			.register(
				Direction.field_11039,
				targetHalf,
				DoorHinge.field_12586,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
			)
			.register(
				Direction.field_11043,
				targetHalf,
				DoorHinge.field_12586,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
			)
			.register(
				Direction.field_11034,
				targetHalf,
				DoorHinge.field_12588,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
			)
			.register(
				Direction.field_11035,
				targetHalf,
				DoorHinge.field_12588,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
			)
			.register(
				Direction.field_11039,
				targetHalf,
				DoorHinge.field_12588,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
			)
			.register(Direction.field_11043, targetHalf, DoorHinge.field_12588, true, BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel))
			.register(
				Direction.field_11034,
				targetHalf,
				DoorHinge.field_12586,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, regularModel).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
			)
			.register(Direction.field_11035, targetHalf, DoorHinge.field_12586, true, BlockStateVariant.create().put(VariantSettings.MODEL, regularModel))
			.register(
				Direction.field_11039,
				targetHalf,
				DoorHinge.field_12586,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, regularModel).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
			)
			.register(
				Direction.field_11043,
				targetHalf,
				DoorHinge.field_12586,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, regularModel).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
			);
	}

	private static BlockStateSupplier createDoorBlockState(
		Block doorBlock, Identifier bottomModelId, Identifier bottomHingeModelId, Identifier topModelId, Identifier topHingeModelId
	) {
		return VariantsBlockStateSupplier.create(doorBlock)
			.coordinate(
				fillDoorVariantMap(
					fillDoorVariantMap(
						BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.DOUBLE_BLOCK_HALF, Properties.DOOR_HINGE, Properties.OPEN),
						DoubleBlockHalf.field_12607,
						bottomModelId,
						bottomHingeModelId
					),
					DoubleBlockHalf.field_12609,
					topModelId,
					topHingeModelId
				)
			);
	}

	private static BlockStateSupplier createFenceBlockState(Block fenceBlock, Identifier postModelId, Identifier sideModelId) {
		return MultipartBlockStateSupplier.create(fenceBlock)
			.with(BlockStateVariant.create().put(VariantSettings.MODEL, postModelId))
			.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, sideModelId).put(VariantSettings.UVLOCK, true))
			.with(
				When.create().set(Properties.EAST, true),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, sideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					.put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.SOUTH, true),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, sideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
					.put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.WEST, true),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, sideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
					.put(VariantSettings.UVLOCK, true)
			);
	}

	private static BlockStateSupplier createWallBlockState(Block wallBlock, Identifier postModelId, Identifier lowSideModelId, Identifier tallSideModelId) {
		return MultipartBlockStateSupplier.create(wallBlock)
			.with(When.create().set(Properties.UP, true), BlockStateVariant.create().put(VariantSettings.MODEL, postModelId))
			.with(
				When.create().set(Properties.NORTH_WALL_SHAPE, WallShape.field_22179),
				BlockStateVariant.create().put(VariantSettings.MODEL, lowSideModelId).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.EAST_WALL_SHAPE, WallShape.field_22179),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, lowSideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					.put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.SOUTH_WALL_SHAPE, WallShape.field_22179),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, lowSideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
					.put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.WEST_WALL_SHAPE, WallShape.field_22179),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, lowSideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
					.put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.NORTH_WALL_SHAPE, WallShape.field_22180),
				BlockStateVariant.create().put(VariantSettings.MODEL, tallSideModelId).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.EAST_WALL_SHAPE, WallShape.field_22180),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, tallSideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					.put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.SOUTH_WALL_SHAPE, WallShape.field_22180),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, tallSideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
					.put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.WEST_WALL_SHAPE, WallShape.field_22180),
				BlockStateVariant.create()
					.put(VariantSettings.MODEL, tallSideModelId)
					.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
					.put(VariantSettings.UVLOCK, true)
			);
	}

	private static BlockStateSupplier createFenceGateBlockState(
		Block fenceGateBlock, Identifier openModelId, Identifier closedModelId, Identifier openWallModelId, Identifier closedWallModelId
	) {
		return VariantsBlockStateSupplier.create(fenceGateBlock, BlockStateVariant.create().put(VariantSettings.UVLOCK, true))
			.coordinate(createSouthDefaultHorizontalRotationStates())
			.coordinate(
				BlockStateVariantMap.create(Properties.IN_WALL, Properties.OPEN)
					.register(false, false, BlockStateVariant.create().put(VariantSettings.MODEL, closedModelId))
					.register(true, false, BlockStateVariant.create().put(VariantSettings.MODEL, closedWallModelId))
					.register(false, true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId))
					.register(true, true, BlockStateVariant.create().put(VariantSettings.MODEL, openWallModelId))
			);
	}

	private static BlockStateSupplier createStairsBlockState(Block stairsBlock, Identifier innerModelId, Identifier regularModelId, Identifier outerModelId) {
		return VariantsBlockStateSupplier.create(stairsBlock)
			.coordinate(
				BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.STAIR_SHAPE)
					.register(Direction.field_11034, BlockHalf.field_12617, StairShape.field_12710, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId))
					.register(
						Direction.field_11039,
						BlockHalf.field_12617,
						StairShape.field_12710,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11035,
						BlockHalf.field_12617,
						StairShape.field_12710,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11043,
						BlockHalf.field_12617,
						StairShape.field_12710,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(Direction.field_11034, BlockHalf.field_12617, StairShape.field_12709, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId))
					.register(
						Direction.field_11039,
						BlockHalf.field_12617,
						StairShape.field_12709,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11035,
						BlockHalf.field_12617,
						StairShape.field_12709,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11043,
						BlockHalf.field_12617,
						StairShape.field_12709,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11034,
						BlockHalf.field_12617,
						StairShape.field_12708,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11039,
						BlockHalf.field_12617,
						StairShape.field_12708,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(Direction.field_11035, BlockHalf.field_12617, StairShape.field_12708, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId))
					.register(
						Direction.field_11043,
						BlockHalf.field_12617,
						StairShape.field_12708,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(Direction.field_11034, BlockHalf.field_12617, StairShape.field_12713, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId))
					.register(
						Direction.field_11039,
						BlockHalf.field_12617,
						StairShape.field_12713,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11035,
						BlockHalf.field_12617,
						StairShape.field_12713,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11043,
						BlockHalf.field_12617,
						StairShape.field_12713,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11034,
						BlockHalf.field_12617,
						StairShape.field_12712,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11039,
						BlockHalf.field_12617,
						StairShape.field_12712,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(Direction.field_11035, BlockHalf.field_12617, StairShape.field_12712, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId))
					.register(
						Direction.field_11043,
						BlockHalf.field_12617,
						StairShape.field_12712,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11034,
						BlockHalf.field_12619,
						StairShape.field_12710,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11039,
						BlockHalf.field_12619,
						StairShape.field_12710,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11035,
						BlockHalf.field_12619,
						StairShape.field_12710,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11043,
						BlockHalf.field_12619,
						StairShape.field_12710,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, regularModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11034,
						BlockHalf.field_12619,
						StairShape.field_12709,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11039,
						BlockHalf.field_12619,
						StairShape.field_12709,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11035,
						BlockHalf.field_12619,
						StairShape.field_12709,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11043,
						BlockHalf.field_12619,
						StairShape.field_12709,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11034,
						BlockHalf.field_12619,
						StairShape.field_12708,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11039,
						BlockHalf.field_12619,
						StairShape.field_12708,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11035,
						BlockHalf.field_12619,
						StairShape.field_12708,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11043,
						BlockHalf.field_12619,
						StairShape.field_12708,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, outerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11034,
						BlockHalf.field_12619,
						StairShape.field_12713,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11039,
						BlockHalf.field_12619,
						StairShape.field_12713,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11035,
						BlockHalf.field_12619,
						StairShape.field_12713,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11043,
						BlockHalf.field_12619,
						StairShape.field_12713,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11034,
						BlockHalf.field_12619,
						StairShape.field_12712,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11039,
						BlockHalf.field_12619,
						StairShape.field_12712,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11035,
						BlockHalf.field_12619,
						StairShape.field_12712,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.field_11043,
						BlockHalf.field_12619,
						StairShape.field_12712,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, innerModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
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
					.register(Direction.field_11043, BlockHalf.field_12617, false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
					.register(
						Direction.field_11035,
						BlockHalf.field_12617,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
					)
					.register(
						Direction.field_11034,
						BlockHalf.field_12617,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
					.register(
						Direction.field_11039,
						BlockHalf.field_12617,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
					)
					.register(Direction.field_11043, BlockHalf.field_12619, false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
					.register(
						Direction.field_11035,
						BlockHalf.field_12619,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, topModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
					)
					.register(
						Direction.field_11034,
						BlockHalf.field_12619,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, topModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
					.register(
						Direction.field_11039,
						BlockHalf.field_12619,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, topModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
					)
					.register(Direction.field_11043, BlockHalf.field_12617, true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId))
					.register(
						Direction.field_11035,
						BlockHalf.field_12617,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
					)
					.register(
						Direction.field_11034,
						BlockHalf.field_12617,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
					.register(
						Direction.field_11039,
						BlockHalf.field_12617,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
					)
					.register(
						Direction.field_11043,
						BlockHalf.field_12619,
						true,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, openModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
					)
					.register(
						Direction.field_11035,
						BlockHalf.field_12619,
						true,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, openModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22890)
					)
					.register(
						Direction.field_11034,
						BlockHalf.field_12619,
						true,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, openModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
					)
					.register(
						Direction.field_11039,
						BlockHalf.field_12619,
						true,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, openModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
			);
	}

	private static BlockStateSupplier createTrapdoorBlockState(Block trapdoorBlock, Identifier topModelId, Identifier bottomModelId, Identifier openModelId) {
		return VariantsBlockStateSupplier.create(trapdoorBlock)
			.coordinate(
				BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.OPEN)
					.register(Direction.field_11043, BlockHalf.field_12617, false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
					.register(Direction.field_11035, BlockHalf.field_12617, false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
					.register(Direction.field_11034, BlockHalf.field_12617, false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
					.register(Direction.field_11039, BlockHalf.field_12617, false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
					.register(Direction.field_11043, BlockHalf.field_12619, false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
					.register(Direction.field_11035, BlockHalf.field_12619, false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
					.register(Direction.field_11034, BlockHalf.field_12619, false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
					.register(Direction.field_11039, BlockHalf.field_12619, false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
					.register(Direction.field_11043, BlockHalf.field_12617, true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId))
					.register(
						Direction.field_11035,
						BlockHalf.field_12617,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
					)
					.register(
						Direction.field_11034,
						BlockHalf.field_12617,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
					.register(
						Direction.field_11039,
						BlockHalf.field_12617,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
					)
					.register(Direction.field_11043, BlockHalf.field_12619, true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId))
					.register(
						Direction.field_11035,
						BlockHalf.field_12619,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
					)
					.register(
						Direction.field_11034,
						BlockHalf.field_12619,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
					.register(
						Direction.field_11039,
						BlockHalf.field_12619,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
					)
			);
	}

	private static VariantsBlockStateSupplier createSingletonBlockState(Block block, Identifier modelId) {
		return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, modelId));
	}

	private static BlockStateVariantMap createAxisRotatedVariantMap() {
		return BlockStateVariantMap.create(Properties.AXIS)
			.register(Direction.Axis.field_11052, BlockStateVariant.create())
			.register(Direction.Axis.field_11051, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891))
			.register(
				Direction.Axis.field_11048,
				BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
			);
	}

	private static BlockStateSupplier createAxisRotatedBlockState(Block block, Identifier modelId) {
		return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, modelId)).coordinate(createAxisRotatedVariantMap());
	}

	private void method_31063(Block block, Identifier identifier) {
		this.blockStateCollector.accept(createAxisRotatedBlockState(block, identifier));
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

	private static BlockStateSupplier createAxisRotatedBlockState(Block block, Identifier verticalModelId, Identifier horizontalModelId) {
		return VariantsBlockStateSupplier.create(block)
			.coordinate(
				BlockStateVariantMap.create(Properties.AXIS)
					.register(Direction.Axis.field_11052, BlockStateVariant.create().put(VariantSettings.MODEL, verticalModelId))
					.register(
						Direction.Axis.field_11051,
						BlockStateVariant.create().put(VariantSettings.MODEL, horizontalModelId).put(VariantSettings.X, VariantSettings.Rotation.field_22891)
					)
					.register(
						Direction.Axis.field_11048,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, horizontalModelId)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
			);
	}

	private void registerAxisRotated(Block block, TexturedModel.Factory verticalModelFactory, TexturedModel.Factory horizontalModelFactory) {
		Identifier identifier = verticalModelFactory.upload(block, this.modelCollector);
		Identifier identifier2 = horizontalModelFactory.upload(block, this.modelCollector);
		this.blockStateCollector.accept(createAxisRotatedBlockState(block, identifier, identifier2));
	}

	private Identifier createSubModel(Block block, String suffix, Model model, Function<Identifier, Texture> textureFactory) {
		return model.upload(block, suffix, (Texture)textureFactory.apply(Texture.getSubId(block, suffix)), this.modelCollector);
	}

	private static BlockStateSupplier createPressurePlateBlockState(Block pressurePlateBlock, Identifier upModelId, Identifier downModelId) {
		return VariantsBlockStateSupplier.create(pressurePlateBlock).coordinate(createBooleanModelMap(Properties.POWERED, downModelId, upModelId));
	}

	private static BlockStateSupplier createSlabBlockState(Block slabBlock, Identifier bottomModelId, Identifier topModelId, Identifier fullModelId) {
		return VariantsBlockStateSupplier.create(slabBlock)
			.coordinate(
				BlockStateVariantMap.create(Properties.SLAB_TYPE)
					.register(SlabType.field_12681, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId))
					.register(SlabType.field_12679, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId))
					.register(SlabType.field_12682, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId))
			);
	}

	private void registerSimpleCubeAll(Block block) {
		this.registerSingleton(block, TexturedModel.CUBE_ALL);
	}

	private void registerSingleton(Block block, TexturedModel.Factory modelFactory) {
		this.blockStateCollector.accept(createSingletonBlockState(block, modelFactory.upload(block, this.modelCollector)));
	}

	private void registerSingleton(Block block, Texture texture, Model model) {
		Identifier identifier = model.upload(block, texture, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(block, identifier));
	}

	private BlockStateModelGenerator.BlockTexturePool registerTexturePool(Block block, TexturedModel model) {
		return new BlockStateModelGenerator.BlockTexturePool(model.getTexture()).base(block, model.getModel());
	}

	private BlockStateModelGenerator.BlockTexturePool registerTexturePool(Block block, TexturedModel.Factory modelFactory) {
		TexturedModel texturedModel = modelFactory.get(block);
		return new BlockStateModelGenerator.BlockTexturePool(texturedModel.getTexture()).base(block, texturedModel.getModel());
	}

	private BlockStateModelGenerator.BlockTexturePool registerCubeAllModelTexturePool(Block block) {
		return this.registerTexturePool(block, TexturedModel.CUBE_ALL);
	}

	private BlockStateModelGenerator.BlockTexturePool registerTexturePool(Texture texture) {
		return new BlockStateModelGenerator.BlockTexturePool(texture);
	}

	private void registerDoor(Block doorBlock) {
		Texture texture = Texture.topBottom(doorBlock);
		Identifier identifier = Models.field_22984.upload(doorBlock, texture, this.modelCollector);
		Identifier identifier2 = Models.field_22985.upload(doorBlock, texture, this.modelCollector);
		Identifier identifier3 = Models.field_22986.upload(doorBlock, texture, this.modelCollector);
		Identifier identifier4 = Models.field_22987.upload(doorBlock, texture, this.modelCollector);
		this.registerItemModel(doorBlock.asItem());
		this.blockStateCollector.accept(createDoorBlockState(doorBlock, identifier, identifier2, identifier3, identifier4));
	}

	private void registerOrientableTrapdoor(Block trapdoorBlock) {
		Texture texture = Texture.texture(trapdoorBlock);
		Identifier identifier = Models.field_22918.upload(trapdoorBlock, texture, this.modelCollector);
		Identifier identifier2 = Models.field_22919.upload(trapdoorBlock, texture, this.modelCollector);
		Identifier identifier3 = Models.field_22920.upload(trapdoorBlock, texture, this.modelCollector);
		this.blockStateCollector.accept(createOrientableTrapdoorBlockState(trapdoorBlock, identifier, identifier2, identifier3));
		this.registerParentedItemModel(trapdoorBlock, identifier2);
	}

	private void registerTrapdoor(Block trapdoorBlock) {
		Texture texture = Texture.texture(trapdoorBlock);
		Identifier identifier = Models.field_22915.upload(trapdoorBlock, texture, this.modelCollector);
		Identifier identifier2 = Models.field_22916.upload(trapdoorBlock, texture, this.modelCollector);
		Identifier identifier3 = Models.field_22917.upload(trapdoorBlock, texture, this.modelCollector);
		this.blockStateCollector.accept(createTrapdoorBlockState(trapdoorBlock, identifier, identifier2, identifier3));
		this.registerParentedItemModel(trapdoorBlock, identifier2);
	}

	private BlockStateModelGenerator.LogTexturePool registerLog(Block logBlock) {
		return new BlockStateModelGenerator.LogTexturePool(Texture.sideAndEndForTop(logBlock));
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

	private void registerTintableCross(Block block, BlockStateModelGenerator.TintType tintType, Texture texture) {
		this.registerItemModel(block);
		this.registerTintableCrossBlockState(block, tintType, texture);
	}

	private void registerTintableCrossBlockState(Block block, BlockStateModelGenerator.TintType tintType) {
		Texture texture = Texture.cross(block);
		this.registerTintableCrossBlockState(block, tintType, texture);
	}

	private void registerTintableCrossBlockState(Block block, BlockStateModelGenerator.TintType tintType, Texture crossTexture) {
		Identifier identifier = tintType.getCrossModel().upload(block, crossTexture, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(block, identifier));
	}

	private void registerFlowerPotPlant(Block plantBlock, Block flowerPotBlock, BlockStateModelGenerator.TintType tintType) {
		this.registerTintableCross(plantBlock, tintType);
		Texture texture = Texture.plant(plantBlock);
		Identifier identifier = tintType.getFlowerPotCrossModel().upload(flowerPotBlock, texture, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(flowerPotBlock, identifier));
	}

	private void registerCoralFan(Block coralFanBlock, Block coralWallFanBlock) {
		TexturedModel texturedModel = TexturedModel.CORAL_FAN.get(coralFanBlock);
		Identifier identifier = texturedModel.upload(coralFanBlock, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(coralFanBlock, identifier));
		Identifier identifier2 = Models.field_22947.upload(coralWallFanBlock, texturedModel.getTexture(), this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(coralWallFanBlock, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
		this.registerItemModel(coralFanBlock);
	}

	private void registerGourd(Block stemBlock, Block attachedStemBlock) {
		this.registerItemModel(stemBlock.asItem());
		Texture texture = Texture.stem(stemBlock);
		Texture texture2 = Texture.stemAndUpper(stemBlock, attachedStemBlock);
		Identifier identifier = Models.field_22959.upload(attachedStemBlock, texture2, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(attachedStemBlock, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.coordinate(
						BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
							.register(Direction.field_11039, BlockStateVariant.create())
							.register(Direction.field_11035, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22893))
							.register(Direction.field_11043, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22891))
							.register(Direction.field_11034, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22892))
					)
			);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(stemBlock)
					.coordinate(
						BlockStateVariantMap.create(Properties.AGE_7)
							.register(
								integer -> BlockStateVariant.create().put(VariantSettings.MODEL, Models.STEM_GROWTH_STAGES[integer].upload(stemBlock, texture, this.modelCollector))
							)
					)
			);
	}

	private void registerCoral(
		Block coral, Block deadCoral, Block coralBlock, Block deadCoralBlock, Block coralFan, Block deadCoralFan, Block coralWallFan, Block deadCoralWallFan
	) {
		this.registerTintableCross(coral, BlockStateModelGenerator.TintType.field_22840);
		this.registerTintableCross(deadCoral, BlockStateModelGenerator.TintType.field_22840);
		this.registerSimpleCubeAll(coralBlock);
		this.registerSimpleCubeAll(deadCoralBlock);
		this.registerCoralFan(coralFan, coralWallFan);
		this.registerCoralFan(deadCoralFan, deadCoralWallFan);
	}

	private void registerDoubleBlock(Block doubleBlock, BlockStateModelGenerator.TintType tintType) {
		this.registerItemModel(doubleBlock, "_top");
		Identifier identifier = this.createSubModel(doubleBlock, "_top", tintType.getCrossModel(), Texture::cross);
		Identifier identifier2 = this.createSubModel(doubleBlock, "_bottom", tintType.getCrossModel(), Texture::cross);
		this.registerDoubleBlock(doubleBlock, identifier, identifier2);
	}

	private void registerSunflower() {
		this.registerItemModel(Blocks.field_10583, "_front");
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.field_10583, "_top");
		Identifier identifier2 = this.createSubModel(Blocks.field_10583, "_bottom", BlockStateModelGenerator.TintType.field_22840.getCrossModel(), Texture::cross);
		this.registerDoubleBlock(Blocks.field_10583, identifier, identifier2);
	}

	private void registerTallSeagrass() {
		Identifier identifier = this.createSubModel(Blocks.field_10238, "_top", Models.field_22932, Texture::texture);
		Identifier identifier2 = this.createSubModel(Blocks.field_10238, "_bottom", Models.field_22932, Texture::texture);
		this.registerDoubleBlock(Blocks.field_10238, identifier, identifier2);
	}

	private void registerDoubleBlock(Block block, Identifier upperHalfModelId, Identifier lowerHalfModelId) {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block)
					.coordinate(
						BlockStateVariantMap.create(Properties.DOUBLE_BLOCK_HALF)
							.register(DoubleBlockHalf.field_12607, BlockStateVariant.create().put(VariantSettings.MODEL, lowerHalfModelId))
							.register(DoubleBlockHalf.field_12609, BlockStateVariant.create().put(VariantSettings.MODEL, upperHalfModelId))
					)
			);
	}

	private void registerTurnableRail(Block rail) {
		Texture texture = Texture.rail(rail);
		Texture texture2 = Texture.rail(Texture.getSubId(rail, "_corner"));
		Identifier identifier = Models.field_22925.upload(rail, texture, this.modelCollector);
		Identifier identifier2 = Models.field_22926.upload(rail, texture2, this.modelCollector);
		Identifier identifier3 = Models.field_22927.upload(rail, texture, this.modelCollector);
		Identifier identifier4 = Models.field_22928.upload(rail, texture, this.modelCollector);
		this.registerItemModel(rail);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(rail)
					.coordinate(
						BlockStateVariantMap.create(Properties.RAIL_SHAPE)
							.register(RailShape.field_12665, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(
								RailShape.field_12674, BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								RailShape.field_12667, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								RailShape.field_12666, BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(RailShape.field_12670, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
							.register(RailShape.field_12668, BlockStateVariant.create().put(VariantSettings.MODEL, identifier4))
							.register(RailShape.field_12664, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
							.register(
								RailShape.field_12671, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								RailShape.field_12672, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								RailShape.field_12663, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
					)
			);
	}

	private void registerStraightRail(Block rail) {
		Identifier identifier = this.createSubModel(rail, "", Models.field_22925, Texture::rail);
		Identifier identifier2 = this.createSubModel(rail, "", Models.field_22927, Texture::rail);
		Identifier identifier3 = this.createSubModel(rail, "", Models.field_22928, Texture::rail);
		Identifier identifier4 = this.createSubModel(rail, "_on", Models.field_22925, Texture::rail);
		Identifier identifier5 = this.createSubModel(rail, "_on", Models.field_22927, Texture::rail);
		Identifier identifier6 = this.createSubModel(rail, "_on", Models.field_22928, Texture::rail);
		BlockStateVariantMap blockStateVariantMap = BlockStateVariantMap.create(Properties.POWERED, Properties.STRAIGHT_RAIL_SHAPE)
			.register(
				(boolean_, railShape) -> {
					switch (railShape) {
						case field_12665:
							return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ ? identifier4 : identifier);
						case field_12674:
							return BlockStateVariant.create()
								.put(VariantSettings.MODEL, boolean_ ? identifier4 : identifier)
								.put(VariantSettings.Y, VariantSettings.Rotation.field_22891);
						case field_12667:
							return BlockStateVariant.create()
								.put(VariantSettings.MODEL, boolean_ ? identifier5 : identifier2)
								.put(VariantSettings.Y, VariantSettings.Rotation.field_22891);
						case field_12666:
							return BlockStateVariant.create()
								.put(VariantSettings.MODEL, boolean_ ? identifier6 : identifier3)
								.put(VariantSettings.Y, VariantSettings.Rotation.field_22891);
						case field_12670:
							return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ ? identifier5 : identifier2);
						case field_12668:
							return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ ? identifier6 : identifier3);
						default:
							throw new UnsupportedOperationException("Fix you generator!");
					}
				}
			);
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
		Identifier identifier = Models.PARTICLE.upload(block, Texture.particle(particleSource), this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(block, identifier));
	}

	private void registerBuiltinWithParticle(Block block, Identifier particleSource) {
		Identifier identifier = Models.PARTICLE.upload(block, Texture.particle(particleSource), this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(block, identifier));
	}

	private void registerCarpet(Block wool, Block carpet) {
		this.registerSingleton(wool, TexturedModel.CUBE_ALL);
		Identifier identifier = TexturedModel.CARPET.get(wool).upload(carpet, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(carpet, identifier));
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
		Texture texture = Texture.paneAndTopForEdge(glass, glassPane);
		Identifier identifier = Models.field_22953.upload(glassPane, texture, this.modelCollector);
		Identifier identifier2 = Models.field_22954.upload(glassPane, texture, this.modelCollector);
		Identifier identifier3 = Models.field_22955.upload(glassPane, texture, this.modelCollector);
		Identifier identifier4 = Models.field_22951.upload(glassPane, texture, this.modelCollector);
		Identifier identifier5 = Models.field_22952.upload(glassPane, texture, this.modelCollector);
		Item item = glassPane.asItem();
		Models.field_22938.upload(ModelIds.getItemModelId(item), Texture.layer0(glass), this.modelCollector);
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(glassPane)
					.with(BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.with(
						When.create().set(Properties.EAST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
					.with(When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
					.with(
						When.create().set(Properties.WEST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
					.with(When.create().set(Properties.NORTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier4))
					.with(When.create().set(Properties.EAST, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier5))
					.with(
						When.create().set(Properties.SOUTH, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier5).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
					.with(
						When.create().set(Properties.WEST, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
					)
			);
	}

	private void registerCommandBlock(Block commandBlock) {
		Texture texture = Texture.sideFrontBack(commandBlock);
		Identifier identifier = Models.field_22956.upload(commandBlock, texture, this.modelCollector);
		Identifier identifier2 = this.createSubModel(
			commandBlock, "_conditional", Models.field_22956, identifierx -> texture.copyAndAdd(TextureKey.field_23018, identifierx)
		);
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
			.mapToObj(i -> BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10211, i + string)))
			.collect(Collectors.toList());
	}

	private void registerBamboo() {
		this.excludeFromSimpleItemModelGeneration(Blocks.field_10211);
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.field_10211)
					.with(When.create().set(Properties.AGE_1, 0), this.getBambooBlockStateVariants(0))
					.with(When.create().set(Properties.AGE_1, 1), this.getBambooBlockStateVariants(1))
					.with(
						When.create().set(Properties.BAMBOO_LEAVES, BambooLeaves.field_12466),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10211, "_small_leaves"))
					)
					.with(
						When.create().set(Properties.BAMBOO_LEAVES, BambooLeaves.field_12468),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10211, "_large_leaves"))
					)
			);
	}

	private BlockStateVariantMap createUpDefaultFacingVariantMap() {
		return BlockStateVariantMap.create(Properties.FACING)
			.register(Direction.field_11033, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22892))
			.register(Direction.field_11036, BlockStateVariant.create())
			.register(Direction.field_11043, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891))
			.register(
				Direction.field_11035,
				BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
			)
			.register(
				Direction.field_11039,
				BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
			)
			.register(
				Direction.field_11034,
				BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
			);
	}

	private void registerBarrel() {
		Identifier identifier = Texture.getSubId(Blocks.field_16328, "_top_open");
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_16328)
					.coordinate(this.createUpDefaultFacingVariantMap())
					.coordinate(
						BlockStateVariantMap.create(Properties.OPEN)
							.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, TexturedModel.CUBE_BOTTOM_TOP.upload(Blocks.field_16328, this.modelCollector)))
							.register(
								true,
								BlockStateVariant.create()
									.put(
										VariantSettings.MODEL,
										TexturedModel.CUBE_BOTTOM_TOP
											.get(Blocks.field_16328)
											.texture(texture -> texture.put(TextureKey.field_23015, identifier))
											.upload(Blocks.field_16328, "_open", this.modelCollector)
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

	private void registerBeehive(Block beehive, Function<Block, Texture> textureGetter) {
		Texture texture = ((Texture)textureGetter.apply(beehive)).inherit(TextureKey.field_23018, TextureKey.field_23012);
		Texture texture2 = texture.copyAndAdd(TextureKey.field_23016, Texture.getSubId(beehive, "_front_honey"));
		Identifier identifier = Models.field_22979.upload(beehive, texture, this.modelCollector);
		Identifier identifier2 = Models.field_22979.upload(beehive, "_honey", texture2, this.modelCollector);
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
			BlockStateVariantMap blockStateVariantMap = BlockStateVariantMap.create(ageProperty).register(integer -> {
				int i = ageTextureIndices[integer];
				Identifier identifier = int2ObjectMap.computeIfAbsent(i, j -> this.createSubModel(crop, "_stage" + i, Models.field_22960, Texture::crop));
				return BlockStateVariant.create().put(VariantSettings.MODEL, identifier);
			});
			this.registerItemModel(crop.asItem());
			this.blockStateCollector.accept(VariantsBlockStateSupplier.create(crop).coordinate(blockStateVariantMap));
		}
	}

	private void registerBell() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.field_16332, "_floor");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.field_16332, "_ceiling");
		Identifier identifier3 = ModelIds.getBlockSubModelId(Blocks.field_16332, "_wall");
		Identifier identifier4 = ModelIds.getBlockSubModelId(Blocks.field_16332, "_between_walls");
		this.registerItemModel(Items.BELL);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_16332)
					.coordinate(
						BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.ATTACHMENT)
							.register(Direction.field_11043, Attachment.field_17098, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(
								Direction.field_11035,
								Attachment.field_17098,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								Direction.field_11034,
								Attachment.field_17098,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								Direction.field_11039,
								Attachment.field_17098,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(Direction.field_11043, Attachment.field_17099, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
							.register(
								Direction.field_11035,
								Attachment.field_17099,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								Direction.field_11034,
								Attachment.field_17099,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								Direction.field_11039,
								Attachment.field_17099,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(
								Direction.field_11043,
								Attachment.field_17100,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(
								Direction.field_11035,
								Attachment.field_17100,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(Direction.field_11034, Attachment.field_17100, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
							.register(
								Direction.field_11039,
								Attachment.field_17100,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								Direction.field_11035,
								Attachment.field_17101,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								Direction.field_11043,
								Attachment.field_17101,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(Direction.field_11034, Attachment.field_17101, BlockStateVariant.create().put(VariantSettings.MODEL, identifier4))
							.register(
								Direction.field_11039,
								Attachment.field_17101,
								BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
					)
			);
	}

	private void registerGrindstone() {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_16337, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.field_16337)))
					.coordinate(
						BlockStateVariantMap.create(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING)
							.register(WallMountLocation.field_12475, Direction.field_11043, BlockStateVariant.create())
							.register(WallMountLocation.field_12475, Direction.field_11034, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22891))
							.register(WallMountLocation.field_12475, Direction.field_11035, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22892))
							.register(WallMountLocation.field_12475, Direction.field_11039, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22893))
							.register(WallMountLocation.field_12471, Direction.field_11043, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891))
							.register(
								WallMountLocation.field_12471,
								Direction.field_11034,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								WallMountLocation.field_12471,
								Direction.field_11035,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								WallMountLocation.field_12471,
								Direction.field_11039,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(WallMountLocation.field_12473, Direction.field_11035, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22892))
							.register(
								WallMountLocation.field_12473,
								Direction.field_11039,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22892).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								WallMountLocation.field_12473,
								Direction.field_11043,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22892).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								WallMountLocation.field_12473,
								Direction.field_11034,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22892).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
					)
			);
	}

	private void registerCooker(Block cooker, TexturedModel.Factory modelFactory) {
		Identifier identifier = modelFactory.upload(cooker, this.modelCollector);
		Identifier identifier2 = Texture.getSubId(cooker, "_front_on");
		Identifier identifier3 = modelFactory.get(cooker)
			.texture(texture -> texture.put(TextureKey.field_23016, identifier2))
			.upload(cooker, "_on", this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(cooker)
					.coordinate(createBooleanModelMap(Properties.LIT, identifier3, identifier))
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
	}

	private void method_27166(Block... blocks) {
		Identifier identifier = ModelIds.getMinecraftNamespacedBlock("campfire_off");

		for (Block block : blocks) {
			Identifier identifier2 = Models.field_23957.upload(block, Texture.method_27167(block), this.modelCollector);
			this.registerItemModel(block.asItem());
			this.blockStateCollector
				.accept(
					VariantsBlockStateSupplier.create(block)
						.coordinate(createBooleanModelMap(Properties.LIT, identifier2, identifier))
						.coordinate(createSouthDefaultHorizontalRotationStates())
				);
		}
	}

	private void registerBookshelf() {
		Texture texture = Texture.sideEnd(Texture.getId(Blocks.field_10504), Texture.getId(Blocks.field_10161));
		Identifier identifier = Models.field_22974.upload(Blocks.field_10504, texture, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(Blocks.field_10504, identifier));
	}

	private void registerRedstone() {
		this.registerItemModel(Items.field_8725);
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.field_10091)
					.with(
						When.anyOf(
							When.create()
								.set(Properties.NORTH_WIRE_CONNECTION, WireConnection.field_12687)
								.set(Properties.EAST_WIRE_CONNECTION, WireConnection.field_12687)
								.set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.field_12687)
								.set(Properties.WEST_WIRE_CONNECTION, WireConnection.field_12687),
							When.create()
								.set(Properties.NORTH_WIRE_CONNECTION, WireConnection.field_12689, WireConnection.field_12686)
								.set(Properties.EAST_WIRE_CONNECTION, WireConnection.field_12689, WireConnection.field_12686),
							When.create()
								.set(Properties.EAST_WIRE_CONNECTION, WireConnection.field_12689, WireConnection.field_12686)
								.set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.field_12689, WireConnection.field_12686),
							When.create()
								.set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.field_12689, WireConnection.field_12686)
								.set(Properties.WEST_WIRE_CONNECTION, WireConnection.field_12689, WireConnection.field_12686),
							When.create()
								.set(Properties.WEST_WIRE_CONNECTION, WireConnection.field_12689, WireConnection.field_12686)
								.set(Properties.NORTH_WIRE_CONNECTION, WireConnection.field_12689, WireConnection.field_12686)
						),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_dot"))
					)
					.with(
						When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.field_12689, WireConnection.field_12686),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side0"))
					)
					.with(
						When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.field_12689, WireConnection.field_12686),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt0"))
					)
					.with(
						When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.field_12689, WireConnection.field_12686),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt1"))
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
					)
					.with(
						When.create().set(Properties.WEST_WIRE_CONNECTION, WireConnection.field_12689, WireConnection.field_12686),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side1"))
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
					)
					.with(
						When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.field_12686),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up"))
					)
					.with(
						When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.field_12686),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up"))
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
					.with(
						When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.field_12686),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up"))
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
					)
					.with(
						When.create().set(Properties.WEST_WIRE_CONNECTION, WireConnection.field_12686),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up"))
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
					)
			);
	}

	private void registerComparator() {
		this.registerItemModel(Items.COMPARATOR);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10377)
					.coordinate(createSouthDefaultHorizontalRotationStates())
					.coordinate(
						BlockStateVariantMap.create(Properties.COMPARATOR_MODE, Properties.POWERED)
							.register(ComparatorMode.field_12576, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.field_10377)))
							.register(
								ComparatorMode.field_12576, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10377, "_on"))
							)
							.register(
								ComparatorMode.field_12578, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10377, "_subtract"))
							)
							.register(
								ComparatorMode.field_12578,
								true,
								BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10377, "_on_subtract"))
							)
					)
			);
	}

	private void registerSmoothStone() {
		Texture texture = Texture.all(Blocks.field_10360);
		Texture texture2 = Texture.sideEnd(Texture.getSubId(Blocks.field_10136, "_side"), texture.getTexture(TextureKey.field_23015));
		Identifier identifier = Models.field_22909.upload(Blocks.field_10136, texture2, this.modelCollector);
		Identifier identifier2 = Models.field_22910.upload(Blocks.field_10136, texture2, this.modelCollector);
		Identifier identifier3 = Models.field_22974.uploadWithoutVariant(Blocks.field_10136, "_double", texture2, this.modelCollector);
		this.blockStateCollector.accept(createSlabBlockState(Blocks.field_10136, identifier, identifier2, identifier3));
		this.blockStateCollector.accept(createSingletonBlockState(Blocks.field_10360, Models.field_22972.upload(Blocks.field_10360, texture, this.modelCollector)));
	}

	private void registerBrewingStand() {
		this.registerItemModel(Items.BREWING_STAND);
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.field_10333)
					.with(BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getId(Blocks.field_10333)))
					.with(
						When.create().set(Properties.HAS_BOTTLE_0, true), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_10333, "_bottle0"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_1, true), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_10333, "_bottle1"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_2, true), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_10333, "_bottle2"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_0, false), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_10333, "_empty0"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_1, false), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_10333, "_empty1"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_2, false), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_10333, "_empty2"))
					)
			);
	}

	private void registerMushroomBlock(Block mushroomBlock) {
		Identifier identifier = Models.field_22937.upload(mushroomBlock, Texture.texture(mushroomBlock), this.modelCollector);
		Identifier identifier2 = ModelIds.getMinecraftNamespacedBlock("mushroom_block_inside");
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(mushroomBlock)
					.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.with(
						When.create().set(Properties.EAST, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.SOUTH, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.WEST, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.UP, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.DOWN, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(When.create().set(Properties.NORTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.with(
						When.create().set(Properties.EAST, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, false)
					)
					.with(
						When.create().set(Properties.SOUTH, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, false)
					)
					.with(
						When.create().set(Properties.WEST, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, false)
					)
					.with(
						When.create().set(Properties.UP, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, false)
					)
					.with(
						When.create().set(Properties.DOWN, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, false)
					)
			);
		this.registerParentedItemModel(mushroomBlock, TexturedModel.CUBE_ALL.upload(mushroomBlock, "_inventory", this.modelCollector));
	}

	private void registerCake() {
		this.registerItemModel(Items.CAKE);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10183)
					.coordinate(
						BlockStateVariantMap.create(Properties.BITES)
							.register(0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.field_10183)))
							.register(1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10183, "_slice1")))
							.register(2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10183, "_slice2")))
							.register(3, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10183, "_slice3")))
							.register(4, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10183, "_slice4")))
							.register(5, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10183, "_slice5")))
							.register(6, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10183, "_slice6")))
					)
			);
	}

	private void registerCartographyTable() {
		Texture texture = new Texture()
			.put(TextureKey.field_23012, Texture.getSubId(Blocks.field_16336, "_side3"))
			.put(TextureKey.field_23024, Texture.getId(Blocks.field_10075))
			.put(TextureKey.field_23023, Texture.getSubId(Blocks.field_16336, "_top"))
			.put(TextureKey.field_23019, Texture.getSubId(Blocks.field_16336, "_side3"))
			.put(TextureKey.field_23021, Texture.getSubId(Blocks.field_16336, "_side3"))
			.put(TextureKey.field_23020, Texture.getSubId(Blocks.field_16336, "_side1"))
			.put(TextureKey.field_23022, Texture.getSubId(Blocks.field_16336, "_side2"));
		this.blockStateCollector.accept(createSingletonBlockState(Blocks.field_16336, Models.field_22942.upload(Blocks.field_16336, texture, this.modelCollector)));
	}

	private void registerSmithingTable() {
		Texture texture = new Texture()
			.put(TextureKey.field_23012, Texture.getSubId(Blocks.field_16329, "_front"))
			.put(TextureKey.field_23024, Texture.getSubId(Blocks.field_16329, "_bottom"))
			.put(TextureKey.field_23023, Texture.getSubId(Blocks.field_16329, "_top"))
			.put(TextureKey.field_23019, Texture.getSubId(Blocks.field_16329, "_front"))
			.put(TextureKey.field_23020, Texture.getSubId(Blocks.field_16329, "_front"))
			.put(TextureKey.field_23021, Texture.getSubId(Blocks.field_16329, "_side"))
			.put(TextureKey.field_23022, Texture.getSubId(Blocks.field_16329, "_side"));
		this.blockStateCollector.accept(createSingletonBlockState(Blocks.field_16329, Models.field_22942.upload(Blocks.field_16329, texture, this.modelCollector)));
	}

	private void registerCubeWithCustomTexture(Block block, Block otherTextureSource, BiFunction<Block, Block, Texture> textureFactory) {
		Texture texture = (Texture)textureFactory.apply(block, otherTextureSource);
		this.blockStateCollector.accept(createSingletonBlockState(block, Models.field_22942.upload(block, texture, this.modelCollector)));
	}

	private void registerPumpkins() {
		Texture texture = Texture.sideEnd(Blocks.field_10261);
		this.blockStateCollector.accept(createSingletonBlockState(Blocks.field_10261, ModelIds.getBlockModelId(Blocks.field_10261)));
		this.registerNorthDefaultHorizontalRotatable(Blocks.field_10147, texture);
		this.registerNorthDefaultHorizontalRotatable(Blocks.field_10009, texture);
	}

	private void registerNorthDefaultHorizontalRotatable(Block block, Texture texture) {
		Identifier identifier = Models.field_22978.upload(block, texture.copyAndAdd(TextureKey.field_23016, Texture.getId(block)), this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
	}

	private void registerCauldron() {
		this.registerItemModel(Items.CAULDRON);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10593)
					.coordinate(
						BlockStateVariantMap.create(Properties.LEVEL_3)
							.register(0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.field_10593)))
							.register(1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10593, "_level1")))
							.register(2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10593, "_level2")))
							.register(3, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10593, "_level3")))
					)
			);
	}

	private void registerCubeColumn(Block block, Block endTexture) {
		Texture texture = new Texture().put(TextureKey.field_23013, Texture.getSubId(endTexture, "_top")).put(TextureKey.field_23018, Texture.getId(block));
		this.registerSingleton(block, texture, Models.field_22974);
	}

	private void registerChorusFlower() {
		Texture texture = Texture.texture(Blocks.field_10528);
		Identifier identifier = Models.field_22949.upload(Blocks.field_10528, texture, this.modelCollector);
		Identifier identifier2 = this.createSubModel(
			Blocks.field_10528, "_dead", Models.field_22949, identifierx -> texture.copyAndAdd(TextureKey.field_23011, identifierx)
		);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(Blocks.field_10528).coordinate(createValueFencedModelMap(Properties.AGE_5, 5, identifier2, identifier)));
	}

	private void registerFurnaceLikeOrientable(Block block) {
		Texture texture = new Texture()
			.put(TextureKey.field_23015, Texture.getSubId(Blocks.field_10181, "_top"))
			.put(TextureKey.field_23018, Texture.getSubId(Blocks.field_10181, "_side"))
			.put(TextureKey.field_23016, Texture.getSubId(block, "_front"));
		Texture texture2 = new Texture()
			.put(TextureKey.field_23018, Texture.getSubId(Blocks.field_10181, "_top"))
			.put(TextureKey.field_23016, Texture.getSubId(block, "_front_vertical"));
		Identifier identifier = Models.field_22978.upload(block, texture, this.modelCollector);
		Identifier identifier2 = Models.field_22980.upload(block, texture2, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block)
					.coordinate(
						BlockStateVariantMap.create(Properties.FACING)
							.register(
								Direction.field_11033, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.X, VariantSettings.Rotation.field_22892)
							)
							.register(Direction.field_11036, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
							.register(Direction.field_11043, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(
								Direction.field_11034, BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								Direction.field_11035, BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								Direction.field_11039, BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
					)
			);
	}

	private void registerEndPortalFrame() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.field_10398);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.field_10398, "_filled");
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10398)
					.coordinate(
						BlockStateVariantMap.create(Properties.EYE)
							.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					)
					.coordinate(createSouthDefaultHorizontalRotationStates())
			);
	}

	private void registerChorusPlant() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.field_10021, "_side");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.field_10021, "_noside");
		Identifier identifier3 = ModelIds.getBlockSubModelId(Blocks.field_10021, "_noside1");
		Identifier identifier4 = ModelIds.getBlockSubModelId(Blocks.field_10021, "_noside2");
		Identifier identifier5 = ModelIds.getBlockSubModelId(Blocks.field_10021, "_noside3");
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.field_10021)
					.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.with(
						When.create().set(Properties.EAST, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.SOUTH, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.WEST, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.UP, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.DOWN, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
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
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier4)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier5)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.WEIGHT, 2)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.SOUTH, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier4)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier5)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.WEIGHT, 2)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.WEST, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier5)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.WEIGHT, 2)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier4)
							.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.UP, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.WEIGHT, 2)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier5)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier4)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22893)
							.put(VariantSettings.UVLOCK, true)
					)
					.with(
						When.create().set(Properties.DOWN, false),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier5)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier4)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.WEIGHT, 2)
							.put(VariantSettings.X, VariantSettings.Rotation.field_22891)
							.put(VariantSettings.UVLOCK, true)
					)
			);
	}

	private void registerComposter() {
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.field_17563)
					.with(BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getId(Blocks.field_17563)))
					.with(When.create().set(Properties.LEVEL_8, 1), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_17563, "_contents1")))
					.with(When.create().set(Properties.LEVEL_8, 2), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_17563, "_contents2")))
					.with(When.create().set(Properties.LEVEL_8, 3), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_17563, "_contents3")))
					.with(When.create().set(Properties.LEVEL_8, 4), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_17563, "_contents4")))
					.with(When.create().set(Properties.LEVEL_8, 5), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_17563, "_contents5")))
					.with(When.create().set(Properties.LEVEL_8, 6), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_17563, "_contents6")))
					.with(When.create().set(Properties.LEVEL_8, 7), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_17563, "_contents7")))
					.with(
						When.create().set(Properties.LEVEL_8, 8), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_17563, "_contents_ready"))
					)
			);
	}

	private void registerNetherrackBottomCustomTop(Block block) {
		Texture texture = new Texture()
			.put(TextureKey.field_23014, Texture.getId(Blocks.field_10515))
			.put(TextureKey.field_23015, Texture.getId(block))
			.put(TextureKey.field_23018, Texture.getSubId(block, "_side"));
		this.blockStateCollector.accept(createSingletonBlockState(block, Models.field_22977.upload(block, texture, this.modelCollector)));
	}

	private void registerDaylightDetector() {
		Identifier identifier = Texture.getSubId(Blocks.field_10429, "_side");
		Texture texture = new Texture().put(TextureKey.field_23015, Texture.getSubId(Blocks.field_10429, "_top")).put(TextureKey.field_23018, identifier);
		Texture texture2 = new Texture().put(TextureKey.field_23015, Texture.getSubId(Blocks.field_10429, "_inverted_top")).put(TextureKey.field_23018, identifier);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10429)
					.coordinate(
						BlockStateVariantMap.create(Properties.INVERTED)
							.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, Models.field_22950.upload(Blocks.field_10429, texture, this.modelCollector)))
							.register(
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, Models.field_22950.upload(ModelIds.getBlockSubModelId(Blocks.field_10429, "_inverted"), texture2, this.modelCollector))
							)
					)
			);
	}

	private void method_31064(Block block) {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(block)))
					.coordinate(this.createUpDefaultFacingVariantMap())
			);
	}

	private void registerFarmland() {
		Texture texture = new Texture().put(TextureKey.field_23000, Texture.getId(Blocks.field_10566)).put(TextureKey.field_23015, Texture.getId(Blocks.field_10362));
		Texture texture2 = new Texture()
			.put(TextureKey.field_23000, Texture.getId(Blocks.field_10566))
			.put(TextureKey.field_23015, Texture.getSubId(Blocks.field_10362, "_moist"));
		Identifier identifier = Models.field_22961.upload(Blocks.field_10362, texture, this.modelCollector);
		Identifier identifier2 = Models.field_22961.upload(Texture.getSubId(Blocks.field_10362, "_moist"), texture2, this.modelCollector);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(Blocks.field_10362).coordinate(createValueFencedModelMap(Properties.MOISTURE, 7, identifier2, identifier)));
	}

	private List<Identifier> getFireFloorModels(Block texture) {
		Identifier identifier = Models.field_22962.upload(ModelIds.getBlockSubModelId(texture, "_floor0"), Texture.fire0(texture), this.modelCollector);
		Identifier identifier2 = Models.field_22962.upload(ModelIds.getBlockSubModelId(texture, "_floor1"), Texture.fire1(texture), this.modelCollector);
		return ImmutableList.of(identifier, identifier2);
	}

	private List<Identifier> getFireSideModels(Block texture) {
		Identifier identifier = Models.field_22963.upload(ModelIds.getBlockSubModelId(texture, "_side0"), Texture.fire0(texture), this.modelCollector);
		Identifier identifier2 = Models.field_22963.upload(ModelIds.getBlockSubModelId(texture, "_side1"), Texture.fire1(texture), this.modelCollector);
		Identifier identifier3 = Models.field_22964.upload(ModelIds.getBlockSubModelId(texture, "_side_alt0"), Texture.fire0(texture), this.modelCollector);
		Identifier identifier4 = Models.field_22964.upload(ModelIds.getBlockSubModelId(texture, "_side_alt1"), Texture.fire1(texture), this.modelCollector);
		return ImmutableList.of(identifier, identifier2, identifier3, identifier4);
	}

	private List<Identifier> getFireUpModels(Block texture) {
		Identifier identifier = Models.field_22965.upload(ModelIds.getBlockSubModelId(texture, "_up0"), Texture.fire0(texture), this.modelCollector);
		Identifier identifier2 = Models.field_22965.upload(ModelIds.getBlockSubModelId(texture, "_up1"), Texture.fire1(texture), this.modelCollector);
		Identifier identifier3 = Models.field_22966.upload(ModelIds.getBlockSubModelId(texture, "_up_alt0"), Texture.fire0(texture), this.modelCollector);
		Identifier identifier4 = Models.field_22966.upload(ModelIds.getBlockSubModelId(texture, "_up_alt1"), Texture.fire1(texture), this.modelCollector);
		return ImmutableList.of(identifier, identifier2, identifier3, identifier4);
	}

	private static List<BlockStateVariant> buildBlockStateVariants(List<Identifier> modelIds, UnaryOperator<BlockStateVariant> processor) {
		return (List<BlockStateVariant>)modelIds.stream()
			.map(identifier -> BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
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
		List<Identifier> list = this.getFireFloorModels(Blocks.field_10036);
		List<Identifier> list2 = this.getFireSideModels(Blocks.field_10036);
		List<Identifier> list3 = this.getFireUpModels(Blocks.field_10036);
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.field_10036)
					.with(when, buildBlockStateVariants(list, blockStateVariant -> blockStateVariant))
					.with(When.anyOf(When.create().set(Properties.NORTH, true), when), buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant))
					.with(
						When.anyOf(When.create().set(Properties.EAST, true), when),
						buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.field_22891))
					)
					.with(
						When.anyOf(When.create().set(Properties.SOUTH, true), when),
						buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.field_22892))
					)
					.with(
						When.anyOf(When.create().set(Properties.WEST, true), when),
						buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.field_22893))
					)
					.with(When.create().set(Properties.UP, true), buildBlockStateVariants(list3, blockStateVariant -> blockStateVariant))
			);
	}

	private void registerSoulFire() {
		List<Identifier> list = this.getFireFloorModels(Blocks.field_22089);
		List<Identifier> list2 = this.getFireSideModels(Blocks.field_22089);
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.field_22089)
					.with(buildBlockStateVariants(list, blockStateVariant -> blockStateVariant))
					.with(buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant))
					.with(buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)))
					.with(buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)))
					.with(buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)))
			);
	}

	private void registerLantern(Block lantern) {
		Identifier identifier = TexturedModel.TEMPLATE_LANTERN.upload(lantern, this.modelCollector);
		Identifier identifier2 = TexturedModel.TEMPLATE_HANGING_LANTERN.upload(lantern, this.modelCollector);
		this.registerItemModel(lantern.asItem());
		this.blockStateCollector.accept(VariantsBlockStateSupplier.create(lantern).coordinate(createBooleanModelMap(Properties.HANGING, identifier2, identifier)));
	}

	private void registerFrostedIce() {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10110)
					.coordinate(
						BlockStateVariantMap.create(Properties.AGE_3)
							.register(0, BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.field_10110, "_0", Models.field_22972, Texture::all)))
							.register(1, BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.field_10110, "_1", Models.field_22972, Texture::all)))
							.register(2, BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.field_10110, "_2", Models.field_22972, Texture::all)))
							.register(3, BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.field_10110, "_3", Models.field_22972, Texture::all)))
					)
			);
	}

	private void registerTopSoils() {
		Identifier identifier = Texture.getId(Blocks.field_10566);
		Texture texture = new Texture()
			.put(TextureKey.field_23014, identifier)
			.inherit(TextureKey.field_23014, TextureKey.field_23012)
			.put(TextureKey.field_23015, Texture.getSubId(Blocks.field_10219, "_top"))
			.put(TextureKey.field_23018, Texture.getSubId(Blocks.field_10219, "_snow"));
		BlockStateVariant blockStateVariant = BlockStateVariant.create()
			.put(VariantSettings.MODEL, Models.field_22977.upload(Blocks.field_10219, "_snow", texture, this.modelCollector));
		this.registerTopSoil(Blocks.field_10219, ModelIds.getBlockModelId(Blocks.field_10219), blockStateVariant);
		Identifier identifier2 = TexturedModel.CUBE_BOTTOM_TOP
			.get(Blocks.field_10402)
			.texture(texturex -> texturex.put(TextureKey.field_23014, identifier))
			.upload(Blocks.field_10402, this.modelCollector);
		this.registerTopSoil(Blocks.field_10402, identifier2, blockStateVariant);
		Identifier identifier3 = TexturedModel.CUBE_BOTTOM_TOP
			.get(Blocks.field_10520)
			.texture(texturex -> texturex.put(TextureKey.field_23014, identifier))
			.upload(Blocks.field_10520, this.modelCollector);
		this.registerTopSoil(Blocks.field_10520, identifier3, blockStateVariant);
	}

	private void registerTopSoil(Block topSoil, Identifier modelId, BlockStateVariant snowyVariant) {
		List<BlockStateVariant> list = Arrays.asList(createModelVariantWithRandomHorizontalRotations(modelId));
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(topSoil).coordinate(BlockStateVariantMap.create(Properties.SNOWY).register(true, snowyVariant).register(false, list))
			);
	}

	private void registerCocoa() {
		this.registerItemModel(Items.field_8116);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10302)
					.coordinate(
						BlockStateVariantMap.create(Properties.AGE_2)
							.register(0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10302, "_stage0")))
							.register(1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10302, "_stage1")))
							.register(2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10302, "_stage2")))
					)
					.coordinate(createSouthDefaultHorizontalRotationStates())
			);
	}

	private void registerGrassPath() {
		this.blockStateCollector.accept(createBlockStateWithRandomHorizontalRotations(Blocks.field_10194, ModelIds.getBlockModelId(Blocks.field_10194)));
	}

	private void registerPressurePlate(Block pressurePlate, Block textureSource) {
		Texture texture = Texture.texture(textureSource);
		Identifier identifier = Models.field_22906.upload(pressurePlate, texture, this.modelCollector);
		Identifier identifier2 = Models.field_22907.upload(pressurePlate, texture, this.modelCollector);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(pressurePlate).coordinate(createValueFencedModelMap(Properties.POWER, 1, identifier2, identifier)));
	}

	private void registerHopper() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.field_10312);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.field_10312, "_side");
		this.registerItemModel(Items.HOPPER);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10312)
					.coordinate(
						BlockStateVariantMap.create(Properties.HOPPER_FACING)
							.register(Direction.field_11033, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(Direction.field_11043, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
							.register(
								Direction.field_11034, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								Direction.field_11035, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								Direction.field_11039, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
					)
			);
	}

	private void registerInfested(Block modelSource, Block infested) {
		Identifier identifier = ModelIds.getBlockModelId(modelSource);
		this.blockStateCollector.accept(VariantsBlockStateSupplier.create(infested, BlockStateVariant.create().put(VariantSettings.MODEL, identifier)));
		this.registerParentedItemModel(infested, identifier);
	}

	private void registerIronBars() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.field_10576, "_post_ends");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.field_10576, "_post");
		Identifier identifier3 = ModelIds.getBlockSubModelId(Blocks.field_10576, "_cap");
		Identifier identifier4 = ModelIds.getBlockSubModelId(Blocks.field_10576, "_cap_alt");
		Identifier identifier5 = ModelIds.getBlockSubModelId(Blocks.field_10576, "_side");
		Identifier identifier6 = ModelIds.getBlockSubModelId(Blocks.field_10576, "_side_alt");
		this.blockStateCollector
			.accept(
				MultipartBlockStateSupplier.create(Blocks.field_10576)
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
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
					.with(
						When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, true).set(Properties.WEST, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier4)
					)
					.with(
						When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
					.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier5))
					.with(
						When.create().set(Properties.EAST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier5).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
					.with(When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier6))
					.with(
						When.create().set(Properties.WEST, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier6).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
					)
			);
		this.registerItemModel(Blocks.field_10576);
	}

	private void registerNorthDefaultHorizontalRotation(Block block) {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(block)))
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
	}

	private void registerLever() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.field_10363);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.field_10363, "_on");
		this.registerItemModel(Blocks.field_10363);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10363)
					.coordinate(createBooleanModelMap(Properties.POWERED, identifier, identifier2))
					.coordinate(
						BlockStateVariantMap.create(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING)
							.register(
								WallMountLocation.field_12473,
								Direction.field_11043,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22892).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								WallMountLocation.field_12473,
								Direction.field_11034,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22892).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(WallMountLocation.field_12473, Direction.field_11035, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22892))
							.register(
								WallMountLocation.field_12473,
								Direction.field_11039,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22892).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(WallMountLocation.field_12475, Direction.field_11043, BlockStateVariant.create())
							.register(WallMountLocation.field_12475, Direction.field_11034, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22891))
							.register(WallMountLocation.field_12475, Direction.field_11035, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22892))
							.register(WallMountLocation.field_12475, Direction.field_11039, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.field_22893))
							.register(WallMountLocation.field_12471, Direction.field_11043, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891))
							.register(
								WallMountLocation.field_12471,
								Direction.field_11034,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								WallMountLocation.field_12471,
								Direction.field_11035,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								WallMountLocation.field_12471,
								Direction.field_11039,
								BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
					)
			);
	}

	private void registerLilyPad() {
		this.registerItemModel(Blocks.field_10588);
		this.blockStateCollector.accept(createBlockStateWithRandomHorizontalRotations(Blocks.field_10588, ModelIds.getBlockModelId(Blocks.field_10588)));
	}

	private void registerNetherPortal() {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10316)
					.coordinate(
						BlockStateVariantMap.create(Properties.HORIZONTAL_AXIS)
							.register(Direction.Axis.field_11048, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10316, "_ns")))
							.register(Direction.Axis.field_11051, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10316, "_ew")))
					)
			);
	}

	private void registerNetherrack() {
		Identifier identifier = TexturedModel.CUBE_ALL.upload(Blocks.field_10515, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(
					Blocks.field_10515,
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier),
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.field_22891),
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.field_22892),
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.field_22893),
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.field_22891),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
						.put(VariantSettings.X, VariantSettings.Rotation.field_22891),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
						.put(VariantSettings.X, VariantSettings.Rotation.field_22892),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
						.put(VariantSettings.X, VariantSettings.Rotation.field_22893),
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.field_22892),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
						.put(VariantSettings.X, VariantSettings.Rotation.field_22891),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
						.put(VariantSettings.X, VariantSettings.Rotation.field_22892),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
						.put(VariantSettings.X, VariantSettings.Rotation.field_22893),
					BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.field_22893),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
						.put(VariantSettings.X, VariantSettings.Rotation.field_22891),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
						.put(VariantSettings.X, VariantSettings.Rotation.field_22892),
					BlockStateVariant.create()
						.put(VariantSettings.MODEL, identifier)
						.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
						.put(VariantSettings.X, VariantSettings.Rotation.field_22893)
				)
			);
	}

	private void registerObserver() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.field_10282);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.field_10282, "_on");
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10282)
					.coordinate(createBooleanModelMap(Properties.POWERED, identifier2, identifier))
					.coordinate(createNorthDefaultRotationStates())
			);
	}

	private void registerPistons() {
		Texture texture = new Texture()
			.put(TextureKey.field_23014, Texture.getSubId(Blocks.field_10560, "_bottom"))
			.put(TextureKey.field_23018, Texture.getSubId(Blocks.field_10560, "_side"));
		Identifier identifier = Texture.getSubId(Blocks.field_10560, "_top_sticky");
		Identifier identifier2 = Texture.getSubId(Blocks.field_10560, "_top");
		Texture texture2 = texture.copyAndAdd(TextureKey.field_23003, identifier);
		Texture texture3 = texture.copyAndAdd(TextureKey.field_23003, identifier2);
		Identifier identifier3 = ModelIds.getBlockSubModelId(Blocks.field_10560, "_base");
		this.registerPiston(Blocks.field_10560, identifier3, texture3);
		this.registerPiston(Blocks.field_10615, identifier3, texture2);
		Identifier identifier4 = Models.field_22977
			.upload(Blocks.field_10560, "_inventory", texture.copyAndAdd(TextureKey.field_23015, identifier2), this.modelCollector);
		Identifier identifier5 = Models.field_22977
			.upload(Blocks.field_10615, "_inventory", texture.copyAndAdd(TextureKey.field_23015, identifier), this.modelCollector);
		this.registerParentedItemModel(Blocks.field_10560, identifier4);
		this.registerParentedItemModel(Blocks.field_10615, identifier5);
	}

	private void registerPiston(Block piston, Identifier extendedModelId, Texture texture) {
		Identifier identifier = Models.field_22971.upload(piston, texture, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(piston)
					.coordinate(createBooleanModelMap(Properties.EXTENDED, extendedModelId, identifier))
					.coordinate(createNorthDefaultRotationStates())
			);
	}

	private void registerPistonHead() {
		Texture texture = new Texture()
			.put(TextureKey.field_23004, Texture.getSubId(Blocks.field_10560, "_top"))
			.put(TextureKey.field_23018, Texture.getSubId(Blocks.field_10560, "_side"));
		Texture texture2 = texture.copyAndAdd(TextureKey.field_23003, Texture.getSubId(Blocks.field_10560, "_top_sticky"));
		Texture texture3 = texture.copyAndAdd(TextureKey.field_23003, Texture.getSubId(Blocks.field_10560, "_top"));
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10379)
					.coordinate(
						BlockStateVariantMap.create(Properties.SHORT, Properties.PISTON_TYPE)
							.register(
								false,
								PistonType.field_12637,
								BlockStateVariant.create().put(VariantSettings.MODEL, Models.field_22930.upload(Blocks.field_10560, "_head", texture3, this.modelCollector))
							)
							.register(
								false,
								PistonType.field_12634,
								BlockStateVariant.create().put(VariantSettings.MODEL, Models.field_22930.upload(Blocks.field_10560, "_head_sticky", texture2, this.modelCollector))
							)
							.register(
								true,
								PistonType.field_12637,
								BlockStateVariant.create().put(VariantSettings.MODEL, Models.field_22931.upload(Blocks.field_10560, "_head_short", texture3, this.modelCollector))
							)
							.register(
								true,
								PistonType.field_12634,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, Models.field_22931.upload(Blocks.field_10560, "_head_short_sticky", texture2, this.modelCollector))
							)
					)
					.coordinate(createNorthDefaultRotationStates())
			);
	}

	private void registerScaffolding() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.field_16492, "_stable");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.field_16492, "_unstable");
		this.registerParentedItemModel(Blocks.field_16492, identifier);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(Blocks.field_16492).coordinate(createBooleanModelMap(Properties.BOTTOM, identifier2, identifier)));
	}

	private void registerRedstoneLamp() {
		Identifier identifier = TexturedModel.CUBE_ALL.upload(Blocks.field_10524, this.modelCollector);
		Identifier identifier2 = this.createSubModel(Blocks.field_10524, "_on", Models.field_22972, Texture::all);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(Blocks.field_10524).coordinate(createBooleanModelMap(Properties.LIT, identifier2, identifier)));
	}

	private void registerTorch(Block torch, Block wallTorch) {
		Texture texture = Texture.torch(torch);
		this.blockStateCollector.accept(createSingletonBlockState(torch, Models.field_22969.upload(torch, texture, this.modelCollector)));
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(
						wallTorch, BlockStateVariant.create().put(VariantSettings.MODEL, Models.field_22970.upload(wallTorch, texture, this.modelCollector))
					)
					.coordinate(createEastDefaultHorizontalRotationStates())
			);
		this.registerItemModel(torch);
		this.excludeFromSimpleItemModelGeneration(wallTorch);
	}

	private void registerRedstoneTorch() {
		Texture texture = Texture.torch(Blocks.field_10523);
		Texture texture2 = Texture.torch(Texture.getSubId(Blocks.field_10523, "_off"));
		Identifier identifier = Models.field_22969.upload(Blocks.field_10523, texture, this.modelCollector);
		Identifier identifier2 = Models.field_22969.upload(Blocks.field_10523, "_off", texture2, this.modelCollector);
		this.blockStateCollector
			.accept(VariantsBlockStateSupplier.create(Blocks.field_10523).coordinate(createBooleanModelMap(Properties.LIT, identifier, identifier2)));
		Identifier identifier3 = Models.field_22970.upload(Blocks.field_10301, texture, this.modelCollector);
		Identifier identifier4 = Models.field_22970.upload(Blocks.field_10301, "_off", texture2, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10301)
					.coordinate(createBooleanModelMap(Properties.LIT, identifier3, identifier4))
					.coordinate(createEastDefaultHorizontalRotationStates())
			);
		this.registerItemModel(Blocks.field_10523);
		this.excludeFromSimpleItemModelGeneration(Blocks.field_10301);
	}

	private void registerRepeater() {
		this.registerItemModel(Items.REPEATER);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10450)
					.coordinate(BlockStateVariantMap.create(Properties.DELAY, Properties.LOCKED, Properties.POWERED).register((integer, boolean_, boolean2) -> {
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append('_').append(integer).append("tick");
						if (boolean2) {
							stringBuilder.append("_on");
						}

						if (boolean_) {
							stringBuilder.append("_locked");
						}

						return BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_10450, stringBuilder.toString()));
					}))
					.coordinate(createSouthDefaultHorizontalRotationStates())
			);
	}

	private void registerSeaPickle() {
		this.registerItemModel(Items.SEA_PICKLE);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10476)
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
		Texture texture = Texture.all(Blocks.field_10477);
		Identifier identifier = Models.field_22972.upload(Blocks.field_10491, texture, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10477)
					.coordinate(
						BlockStateVariantMap.create(Properties.LAYERS)
							.register(
								integer -> BlockStateVariant.create()
										.put(VariantSettings.MODEL, integer < 8 ? ModelIds.getBlockSubModelId(Blocks.field_10477, "_height" + integer * 2) : identifier)
							)
					)
			);
		this.registerParentedItemModel(Blocks.field_10477, ModelIds.getBlockSubModelId(Blocks.field_10477, "_height2"));
		this.blockStateCollector.accept(createSingletonBlockState(Blocks.field_10491, identifier));
	}

	private void registerStonecutter() {
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_16335, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.field_16335)))
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
	}

	private void registerStructureBlock() {
		Identifier identifier = TexturedModel.CUBE_ALL.upload(Blocks.field_10465, this.modelCollector);
		this.registerParentedItemModel(Blocks.field_10465, identifier);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10465)
					.coordinate(
						BlockStateVariantMap.create(Properties.STRUCTURE_BLOCK_MODE)
							.register(
								structureBlockMode -> BlockStateVariant.create()
										.put(VariantSettings.MODEL, this.createSubModel(Blocks.field_10465, "_" + structureBlockMode.asString(), Models.field_22972, Texture::all))
							)
					)
			);
	}

	private void registerSweetBerryBush() {
		this.registerItemModel(Items.field_16998);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_16999)
					.coordinate(
						BlockStateVariantMap.create(Properties.AGE_3)
							.register(
								integer -> BlockStateVariant.create()
										.put(VariantSettings.MODEL, this.createSubModel(Blocks.field_16999, "_stage" + integer, Models.field_22921, Texture::cross))
							)
					)
			);
	}

	private void registerTripwire() {
		this.registerItemModel(Items.field_8276);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10589)
					.coordinate(
						BlockStateVariantMap.create(Properties.ATTACHED, Properties.EAST, Properties.NORTH, Properties.SOUTH, Properties.WEST)
							.register(
								false, false, false, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_ns"))
							)
							.register(
								false,
								true,
								false,
								false,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_n"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(false, false, true, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_n")))
							.register(
								false,
								false,
								false,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_n"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								false,
								false,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_n"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(false, true, true, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_ne")))
							.register(
								false,
								true,
								false,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_ne"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								false,
								false,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_ne"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								false,
								false,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_ne"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(false, false, true, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_ns")))
							.register(
								false,
								true,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_ns"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(false, true, true, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_nse")))
							.register(
								false,
								true,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_nse"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								false,
								false,
								true,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_nse"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								false,
								true,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_nse"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(false, true, true, true, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_nsew")))
							.register(
								true,
								false,
								false,
								false,
								false,
								BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_ns"))
							)
							.register(
								true, false, true, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_n"))
							)
							.register(
								true,
								false,
								false,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_n"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								true,
								true,
								false,
								false,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_n"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								true,
								false,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_n"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(
								true, true, true, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_ne"))
							)
							.register(
								true,
								true,
								false,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_ne"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								true,
								false,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_ne"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								true,
								false,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_ne"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(
								true, false, true, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_ns"))
							)
							.register(
								true,
								true,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_ns"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								true, true, true, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_nse"))
							)
							.register(
								true,
								true,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_nse"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								true,
								false,
								true,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_nse"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								true,
								true,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_nse"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(
								true, true, true, true, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10589, "_attached_nsew"))
							)
					)
			);
	}

	private void registerTripwireHook() {
		this.registerItemModel(Blocks.field_10348);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10348)
					.coordinate(
						BlockStateVariantMap.create(Properties.ATTACHED, Properties.POWERED)
							.register(
								(boolean_, boolean2) -> BlockStateVariant.create()
										.put(VariantSettings.MODEL, Texture.getSubId(Blocks.field_10348, (boolean_ ? "_attached" : "") + (boolean2 ? "_on" : "")))
							)
					)
					.coordinate(createNorthDefaultHorizontalRotationStates())
			);
	}

	private Identifier getTurtleEggModel(int eggs, String prefix, Texture texture) {
		switch (eggs) {
			case 1:
				return Models.field_22933.upload(ModelIds.getMinecraftNamespacedBlock(prefix + "turtle_egg"), texture, this.modelCollector);
			case 2:
				return Models.field_22934.upload(ModelIds.getMinecraftNamespacedBlock("two_" + prefix + "turtle_eggs"), texture, this.modelCollector);
			case 3:
				return Models.field_22935.upload(ModelIds.getMinecraftNamespacedBlock("three_" + prefix + "turtle_eggs"), texture, this.modelCollector);
			case 4:
				return Models.field_22936.upload(ModelIds.getMinecraftNamespacedBlock("four_" + prefix + "turtle_eggs"), texture, this.modelCollector);
			default:
				throw new UnsupportedOperationException();
		}
	}

	private Identifier getTurtleEggModel(Integer eggs, Integer hatch) {
		switch (hatch) {
			case 0:
				return this.getTurtleEggModel(eggs, "", Texture.all(Texture.getId(Blocks.field_10195)));
			case 1:
				return this.getTurtleEggModel(eggs, "slightly_cracked_", Texture.all(Texture.getSubId(Blocks.field_10195, "_slightly_cracked")));
			case 2:
				return this.getTurtleEggModel(eggs, "very_cracked_", Texture.all(Texture.getSubId(Blocks.field_10195, "_very_cracked")));
			default:
				throw new UnsupportedOperationException();
		}
	}

	private void registerTurtleEgg() {
		this.registerItemModel(Items.TURTLE_EGG);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10195)
					.coordinate(
						BlockStateVariantMap.create(Properties.EGGS, Properties.HATCH)
							.registerVariants((integer, integer2) -> Arrays.asList(createModelVariantWithRandomHorizontalRotations(this.getTurtleEggModel(integer, integer2))))
					)
			);
	}

	private void registerVine() {
		this.registerItemModel(Blocks.field_10597);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_10597)
					.coordinate(
						BlockStateVariantMap.create(Properties.EAST, Properties.NORTH, Properties.SOUTH, Properties.UP, Properties.WEST)
							.register(
								false, false, false, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_1"))
							)
							.register(false, false, true, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_1")))
							.register(
								false,
								false,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_1"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								false,
								true,
								false,
								false,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_1"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								true,
								false,
								false,
								false,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_1"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(true, true, false, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_2")))
							.register(
								true,
								false,
								true,
								false,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_2"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								false,
								false,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_2"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								false,
								true,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_2"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(
								true, false, false, false, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_2_opposite"))
							)
							.register(
								false,
								true,
								true,
								false,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_2_opposite"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(true, true, true, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_3")))
							.register(
								true,
								false,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_3"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								false,
								true,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_3"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								true,
								true,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_3"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(true, true, true, false, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_4")))
							.register(false, false, false, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_u")))
							.register(false, false, true, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_1u")))
							.register(
								false,
								false,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_1u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								false,
								true,
								false,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_1u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								true,
								false,
								false,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_1u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(true, true, false, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_2u")))
							.register(
								true,
								false,
								true,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_2u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								false,
								false,
								true,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_2u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								false,
								true,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_2u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(
								true, false, false, true, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_2u_opposite"))
							)
							.register(
								false,
								true,
								true,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_2u_opposite"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(true, true, true, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_3u")))
							.register(
								true,
								false,
								true,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_3u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22891)
							)
							.register(
								false,
								true,
								true,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_3u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22892)
							)
							.register(
								true,
								true,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_3u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.field_22893)
							)
							.register(true, true, true, true, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.field_10597, "_4u")))
					)
			);
	}

	private void registerMagmaBlock() {
		this.blockStateCollector
			.accept(
				createSingletonBlockState(
					Blocks.field_10092, Models.field_22972.upload(Blocks.field_10092, Texture.all(ModelIds.getMinecraftNamespacedBlock("magma")), this.modelCollector)
				)
			);
	}

	private void registerShulkerBox(Block shulkerBox) {
		this.registerSingleton(shulkerBox, TexturedModel.PARTICLE);
		Models.field_22941.upload(ModelIds.getItemModelId(shulkerBox.asItem()), Texture.particle(shulkerBox), this.modelCollector);
	}

	private void registerPlantPart(Block plant, Block plantStem, BlockStateModelGenerator.TintType tintType) {
		this.registerTintableCrossBlockState(plant, tintType);
		this.registerTintableCrossBlockState(plantStem, tintType);
	}

	private void registerBed(Block bed, Block particleSource) {
		Models.field_22943.upload(ModelIds.getItemModelId(bed.asItem()), Texture.particle(particleSource), this.modelCollector);
	}

	private void registerInfestedStone() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.field_10340);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.field_10340, "_mirrored");
		this.blockStateCollector.accept(createBlockStateWithTwoModelAndRandomInversion(Blocks.field_10277, identifier, identifier2));
		this.registerParentedItemModel(Blocks.field_10277, identifier);
	}

	private void registerRoots(Block root, Block pottedRoot) {
		this.registerTintableCross(root, BlockStateModelGenerator.TintType.field_22840);
		Texture texture = Texture.plant(Texture.getSubId(root, "_pot"));
		Identifier identifier = BlockStateModelGenerator.TintType.field_22840.getFlowerPotCrossModel().upload(pottedRoot, texture, this.modelCollector);
		this.blockStateCollector.accept(createSingletonBlockState(pottedRoot, identifier));
	}

	private void registerRespawnAnchor() {
		Identifier identifier = Texture.getSubId(Blocks.field_23152, "_bottom");
		Identifier identifier2 = Texture.getSubId(Blocks.field_23152, "_top_off");
		Identifier identifier3 = Texture.getSubId(Blocks.field_23152, "_top");
		Identifier[] identifiers = new Identifier[5];

		for (int i = 0; i < 5; i++) {
			Texture texture = new Texture()
				.put(TextureKey.field_23014, identifier)
				.put(TextureKey.field_23015, i == 0 ? identifier2 : identifier3)
				.put(TextureKey.field_23018, Texture.getSubId(Blocks.field_23152, "_side" + i));
			identifiers[i] = Models.field_22977.upload(Blocks.field_23152, "_" + i, texture, this.modelCollector);
		}

		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_23152)
					.coordinate(
						BlockStateVariantMap.create(Properties.CHARGES).register(integer -> BlockStateVariant.create().put(VariantSettings.MODEL, identifiers[integer]))
					)
			);
		this.registerParentedItemModel(Items.RESPAWN_ANCHOR, identifiers[0]);
	}

	private BlockStateVariant addJigsawOrientationToVariant(JigsawOrientation orientation, BlockStateVariant variant) {
		switch (orientation) {
			case field_23382:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.field_22891);
			case field_23383:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.Y, VariantSettings.Rotation.field_22892);
			case field_23384:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.Y, VariantSettings.Rotation.field_22893);
			case field_23381:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.field_22891).put(VariantSettings.Y, VariantSettings.Rotation.field_22891);
			case field_23386:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.field_22893).put(VariantSettings.Y, VariantSettings.Rotation.field_22892);
			case field_23387:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.field_22893);
			case field_23388:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.field_22893).put(VariantSettings.Y, VariantSettings.Rotation.field_22891);
			case field_23385:
				return variant.put(VariantSettings.X, VariantSettings.Rotation.field_22893).put(VariantSettings.Y, VariantSettings.Rotation.field_22893);
			case field_23391:
				return variant;
			case field_23392:
				return variant.put(VariantSettings.Y, VariantSettings.Rotation.field_22892);
			case field_23389:
				return variant.put(VariantSettings.Y, VariantSettings.Rotation.field_22893);
			case field_23390:
				return variant.put(VariantSettings.Y, VariantSettings.Rotation.field_22891);
			default:
				throw new UnsupportedOperationException("Rotation " + orientation + " can't be expressed with existing x and y values");
		}
	}

	private void registerJigsaw() {
		Identifier identifier = Texture.getSubId(Blocks.field_16540, "_top");
		Identifier identifier2 = Texture.getSubId(Blocks.field_16540, "_bottom");
		Identifier identifier3 = Texture.getSubId(Blocks.field_16540, "_side");
		Identifier identifier4 = Texture.getSubId(Blocks.field_16540, "_lock");
		Texture texture = new Texture()
			.put(TextureKey.field_23024, identifier3)
			.put(TextureKey.field_23022, identifier3)
			.put(TextureKey.field_23021, identifier3)
			.put(TextureKey.field_23012, identifier)
			.put(TextureKey.field_23019, identifier)
			.put(TextureKey.field_23020, identifier2)
			.put(TextureKey.field_23023, identifier4);
		Identifier identifier5 = Models.field_23400.upload(Blocks.field_16540, texture, this.modelCollector);
		this.blockStateCollector
			.accept(
				VariantsBlockStateSupplier.create(Blocks.field_16540, BlockStateVariant.create().put(VariantSettings.MODEL, identifier5))
					.coordinate(
						BlockStateVariantMap.create(Properties.ORIENTATION)
							.register(jigsawOrientation -> this.addJigsawOrientationToVariant(jigsawOrientation, BlockStateVariant.create()))
					)
			);
	}

	public void register() {
		this.registerSimpleState(Blocks.field_10124);
		this.registerStateWithModelReference(Blocks.field_10543, Blocks.field_10124);
		this.registerStateWithModelReference(Blocks.field_10243, Blocks.field_10124);
		this.registerSimpleState(Blocks.field_10327);
		this.registerSimpleState(Blocks.field_10029);
		this.registerStateWithModelReference(Blocks.field_10422, Blocks.field_10382);
		this.registerSimpleState(Blocks.field_10081);
		this.registerSimpleState(Blocks.field_10342);
		this.registerSimpleState(Blocks.field_10485);
		this.registerSimpleState(Blocks.field_10495);
		this.registerItemModel(Items.FLOWER_POT);
		this.registerSimpleState(Blocks.field_21211);
		this.registerSimpleState(Blocks.field_10382);
		this.registerSimpleState(Blocks.field_10164);
		this.registerSimpleState(Blocks.field_10030);
		this.registerItemModel(Items.CHAIN);
		this.registerSimpleState(Blocks.field_10586);
		this.registerSimpleState(Blocks.field_10018);
		this.registerBuiltinWithParticle(Blocks.field_10499, Items.BARRIER);
		this.registerItemModel(Items.BARRIER);
		this.registerBuiltinWithParticle(Blocks.field_10369, Items.STRUCTURE_VOID);
		this.registerItemModel(Items.STRUCTURE_VOID);
		this.registerBuiltinWithParticle(Blocks.field_10008, Texture.getSubId(Blocks.field_10560, "_side"));
		this.registerSingleton(Blocks.field_10418, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10381, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10442, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10201, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10013, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10234, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10571, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_23077, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10205, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10212, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10085, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_22109, TexturedModel.CUBE_COLUMN);
		this.registerSingleton(Blocks.field_22108, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10090, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10441, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10213, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10080, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10002, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_23880, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10384, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_23866, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10460, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10253, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_23867, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10416, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_22423, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10471, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10171, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10255, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_21212, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10295, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10223, TexturedModel.CUBE_TOP);
		this.registerSingleton(Blocks.field_23261, TexturedModel.CUBE_COLUMN);
		this.registerSingleton(Blocks.field_10545, TexturedModel.CUBE_COLUMN);
		this.registerSingleton(Blocks.field_10541, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10179, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10225, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10540, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_23868, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10174, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_22122, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10114, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_22090, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10260, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10258, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10376, TexturedModel.TEMPLATE_SEAGRASS);
		this.registerItemModel(Items.SEAGRASS);
		this.registerSingleton(Blocks.field_10375, TexturedModel.CUBE_BOTTOM_TOP);
		this.registerSingleton(Blocks.field_22422, TexturedModel.CUBE_COLUMN);
		this.registerSingleton(Blocks.field_22115, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_10562, TexturedModel.CUBE_ALL);
		this.registerSingleton(Blocks.field_23875, TexturedModel.CUBE_ALL);
		this.registerSingleton(
			Blocks.field_10044, TexturedModel.CUBE_COLUMN.withTexture(texture -> texture.put(TextureKey.field_23018, Texture.getId(Blocks.field_10044)))
		);
		this.registerSingleton(Blocks.field_10552, TexturedModel.CUBE_ALL);
		this.registerCubeColumn(Blocks.field_10292, Blocks.field_9979);
		this.registerCubeColumn(Blocks.field_10117, Blocks.field_10344);
		this.registerSingleton(Blocks.field_23876, TexturedModel.CUBE_ALL);
		this.registerPressurePlate(Blocks.field_10224, Blocks.field_10205);
		this.registerPressurePlate(Blocks.field_10582, Blocks.field_10085);
		this.registerBookshelf();
		this.registerBrewingStand();
		this.registerCake();
		this.method_27166(Blocks.field_17350, Blocks.field_23860);
		this.registerCartographyTable();
		this.registerCauldron();
		this.registerChorusFlower();
		this.registerChorusPlant();
		this.registerComposter();
		this.registerDaylightDetector();
		this.registerEndPortalFrame();
		this.method_31064(Blocks.field_10455);
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
		this.registerNetherPortal();
		this.registerNetherrack();
		this.registerObserver();
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
		this.registerVine();
		this.registerMagmaBlock();
		this.registerJigsaw();
		this.registerNorthDefaultHorizontalRotation(Blocks.field_9983);
		this.registerItemModel(Blocks.field_9983);
		this.registerNorthDefaultHorizontalRotation(Blocks.field_16330);
		this.registerTorch(Blocks.field_10336, Blocks.field_10099);
		this.registerTorch(Blocks.field_22092, Blocks.field_22093);
		this.registerCubeWithCustomTexture(Blocks.field_9980, Blocks.field_10161, Texture::frontSideWithCustomBottom);
		this.registerCubeWithCustomTexture(Blocks.field_16331, Blocks.field_10148, Texture::frontTopSide);
		this.registerNetherrackBottomCustomTop(Blocks.field_22120);
		this.registerNetherrackBottomCustomTop(Blocks.field_22113);
		this.registerFurnaceLikeOrientable(Blocks.field_10200);
		this.registerFurnaceLikeOrientable(Blocks.field_10228);
		this.registerLantern(Blocks.field_16541);
		this.registerLantern(Blocks.field_22110);
		this.method_31063(Blocks.field_23985, ModelIds.getBlockModelId(Blocks.field_23985));
		this.registerAxisRotated(Blocks.field_22091, TexturedModel.CUBE_COLUMN);
		this.registerAxisRotated(Blocks.field_23151, TexturedModel.CUBE_COLUMN);
		this.registerAxisRotated(Blocks.field_10166, TexturedModel.CUBE_COLUMN);
		this.registerRotatable(Blocks.field_10566);
		this.registerRotatable(Blocks.field_10102);
		this.registerRotatable(Blocks.field_10534);
		this.registerMirrorable(Blocks.field_9987);
		this.registerAxisRotated(Blocks.field_10359, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
		this.registerAxisRotated(Blocks.field_10505, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
		this.registerAxisRotated(Blocks.field_10437, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
		this.registerNorthDefaultHorizontalRotated(Blocks.field_10083, TexturedModel.ORIENTABLE_WITH_BOTTOM);
		this.registerPumpkins();
		this.registerBeehive(Blocks.field_20421, Texture::sideFrontTopBottom);
		this.registerBeehive(Blocks.field_20422, Texture::sideFrontEnd);
		this.registerCrop(Blocks.field_10341, Properties.AGE_3, 0, 1, 2, 3);
		this.registerCrop(Blocks.field_10609, Properties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
		this.registerCrop(Blocks.field_9974, Properties.AGE_3, 0, 1, 1, 2);
		this.registerCrop(Blocks.field_10247, Properties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
		this.registerCrop(Blocks.field_10293, Properties.AGE_7, 0, 1, 2, 3, 4, 5, 6, 7);
		this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("banner"), Blocks.field_10161)
			.includeWithItem(
				Models.field_22944,
				Blocks.field_10154,
				Blocks.field_10045,
				Blocks.field_10438,
				Blocks.field_10452,
				Blocks.field_10547,
				Blocks.field_10229,
				Blocks.field_10612,
				Blocks.field_10185,
				Blocks.field_9985,
				Blocks.field_10165,
				Blocks.field_10368,
				Blocks.field_10281,
				Blocks.field_10602,
				Blocks.field_10198,
				Blocks.field_10406,
				Blocks.field_10062
			)
			.includeWithoutItem(
				Blocks.field_10202,
				Blocks.field_10599,
				Blocks.field_10274,
				Blocks.field_10050,
				Blocks.field_10139,
				Blocks.field_10318,
				Blocks.field_10531,
				Blocks.field_10267,
				Blocks.field_10604,
				Blocks.field_10372,
				Blocks.field_10054,
				Blocks.field_10067,
				Blocks.field_10370,
				Blocks.field_10594,
				Blocks.field_10279,
				Blocks.field_10537
			);
		this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("bed"), Blocks.field_10161)
			.includeWithoutItem(
				Blocks.field_10120,
				Blocks.field_10410,
				Blocks.field_10230,
				Blocks.field_10621,
				Blocks.field_10356,
				Blocks.field_10180,
				Blocks.field_10610,
				Blocks.field_10141,
				Blocks.field_10326,
				Blocks.field_10109,
				Blocks.field_10019,
				Blocks.field_10527,
				Blocks.field_10288,
				Blocks.field_10561,
				Blocks.field_10069,
				Blocks.field_10461
			);
		this.registerBed(Blocks.field_10120, Blocks.field_10446);
		this.registerBed(Blocks.field_10410, Blocks.field_10095);
		this.registerBed(Blocks.field_10230, Blocks.field_10215);
		this.registerBed(Blocks.field_10621, Blocks.field_10294);
		this.registerBed(Blocks.field_10356, Blocks.field_10490);
		this.registerBed(Blocks.field_10180, Blocks.field_10028);
		this.registerBed(Blocks.field_10610, Blocks.field_10459);
		this.registerBed(Blocks.field_10141, Blocks.field_10423);
		this.registerBed(Blocks.field_10326, Blocks.field_10222);
		this.registerBed(Blocks.field_10109, Blocks.field_10619);
		this.registerBed(Blocks.field_10019, Blocks.field_10259);
		this.registerBed(Blocks.field_10527, Blocks.field_10514);
		this.registerBed(Blocks.field_10288, Blocks.field_10113);
		this.registerBed(Blocks.field_10561, Blocks.field_10170);
		this.registerBed(Blocks.field_10069, Blocks.field_10314);
		this.registerBed(Blocks.field_10461, Blocks.field_10146);
		this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("skull"), Blocks.field_10114)
			.includeWithItem(Models.field_22945, Blocks.field_10042, Blocks.field_10432, Blocks.field_10241, Blocks.field_10481, Blocks.field_10177)
			.includeWithItem(Blocks.field_10337)
			.includeWithoutItem(Blocks.field_10509, Blocks.field_10472, Blocks.field_10208, Blocks.field_10581, Blocks.field_10388, Blocks.field_10101);
		this.registerShulkerBox(Blocks.field_10603);
		this.registerShulkerBox(Blocks.field_10199);
		this.registerShulkerBox(Blocks.field_10407);
		this.registerShulkerBox(Blocks.field_10063);
		this.registerShulkerBox(Blocks.field_10203);
		this.registerShulkerBox(Blocks.field_10600);
		this.registerShulkerBox(Blocks.field_10275);
		this.registerShulkerBox(Blocks.field_10051);
		this.registerShulkerBox(Blocks.field_10140);
		this.registerShulkerBox(Blocks.field_10320);
		this.registerShulkerBox(Blocks.field_10532);
		this.registerShulkerBox(Blocks.field_10268);
		this.registerShulkerBox(Blocks.field_10605);
		this.registerShulkerBox(Blocks.field_10373);
		this.registerShulkerBox(Blocks.field_10055);
		this.registerShulkerBox(Blocks.field_10068);
		this.registerShulkerBox(Blocks.field_10371);
		this.registerSingleton(Blocks.field_10502, TexturedModel.PARTICLE);
		this.excludeFromSimpleItemModelGeneration(Blocks.field_10502);
		this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("chest"), Blocks.field_10161).includeWithoutItem(Blocks.field_10034, Blocks.field_10380);
		this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("ender_chest"), Blocks.field_10540).includeWithoutItem(Blocks.field_10443);
		this.registerBuiltin(Blocks.field_10027, Blocks.field_10540).includeWithItem(Blocks.field_10027, Blocks.field_10613);
		this.registerSimpleCubeAll(Blocks.field_10107);
		this.registerSimpleCubeAll(Blocks.field_10210);
		this.registerSimpleCubeAll(Blocks.field_10585);
		this.registerSimpleCubeAll(Blocks.field_10242);
		this.registerSimpleCubeAll(Blocks.field_10542);
		this.registerSimpleCubeAll(Blocks.field_10421);
		this.registerSimpleCubeAll(Blocks.field_10434);
		this.registerSimpleCubeAll(Blocks.field_10038);
		this.registerSimpleCubeAll(Blocks.field_10172);
		this.registerSimpleCubeAll(Blocks.field_10308);
		this.registerSimpleCubeAll(Blocks.field_10206);
		this.registerSimpleCubeAll(Blocks.field_10011);
		this.registerSimpleCubeAll(Blocks.field_10439);
		this.registerSimpleCubeAll(Blocks.field_10367);
		this.registerSimpleCubeAll(Blocks.field_10058);
		this.registerSimpleCubeAll(Blocks.field_10458);
		this.registerRandomHorizontalRotations(
			TexturedModel.CUBE_ALL,
			Blocks.field_10197,
			Blocks.field_10022,
			Blocks.field_10300,
			Blocks.field_10321,
			Blocks.field_10145,
			Blocks.field_10133,
			Blocks.field_10522,
			Blocks.field_10353,
			Blocks.field_10628,
			Blocks.field_10233,
			Blocks.field_10404,
			Blocks.field_10456,
			Blocks.field_10023,
			Blocks.field_10529,
			Blocks.field_10287,
			Blocks.field_10506
		);
		this.registerSimpleCubeAll(Blocks.field_10415);
		this.registerSimpleCubeAll(Blocks.field_10611);
		this.registerSimpleCubeAll(Blocks.field_10184);
		this.registerSimpleCubeAll(Blocks.field_10015);
		this.registerSimpleCubeAll(Blocks.field_10325);
		this.registerSimpleCubeAll(Blocks.field_10143);
		this.registerSimpleCubeAll(Blocks.field_10014);
		this.registerSimpleCubeAll(Blocks.field_10444);
		this.registerSimpleCubeAll(Blocks.field_10349);
		this.registerSimpleCubeAll(Blocks.field_10590);
		this.registerSimpleCubeAll(Blocks.field_10235);
		this.registerSimpleCubeAll(Blocks.field_10570);
		this.registerSimpleCubeAll(Blocks.field_10409);
		this.registerSimpleCubeAll(Blocks.field_10123);
		this.registerSimpleCubeAll(Blocks.field_10526);
		this.registerSimpleCubeAll(Blocks.field_10328);
		this.registerSimpleCubeAll(Blocks.field_10626);
		this.registerGlassPane(Blocks.field_10033, Blocks.field_10285);
		this.registerGlassPane(Blocks.field_10087, Blocks.field_9991);
		this.registerGlassPane(Blocks.field_10227, Blocks.field_10496);
		this.registerGlassPane(Blocks.field_10574, Blocks.field_10469);
		this.registerGlassPane(Blocks.field_10271, Blocks.field_10193);
		this.registerGlassPane(Blocks.field_10049, Blocks.field_10578);
		this.registerGlassPane(Blocks.field_10157, Blocks.field_10305);
		this.registerGlassPane(Blocks.field_10317, Blocks.field_10565);
		this.registerGlassPane(Blocks.field_10555, Blocks.field_10077);
		this.registerGlassPane(Blocks.field_9996, Blocks.field_10129);
		this.registerGlassPane(Blocks.field_10248, Blocks.field_10355);
		this.registerGlassPane(Blocks.field_10399, Blocks.field_10152);
		this.registerGlassPane(Blocks.field_10060, Blocks.field_9982);
		this.registerGlassPane(Blocks.field_10073, Blocks.field_10163);
		this.registerGlassPane(Blocks.field_10357, Blocks.field_10419);
		this.registerGlassPane(Blocks.field_10272, Blocks.field_10118);
		this.registerGlassPane(Blocks.field_9997, Blocks.field_10070);
		this.registerSouthDefaultHorizontalFacing(
			TexturedModel.TEMPLATE_GLAZED_TERRACOTTA,
			Blocks.field_10595,
			Blocks.field_10280,
			Blocks.field_10538,
			Blocks.field_10345,
			Blocks.field_10096,
			Blocks.field_10046,
			Blocks.field_10567,
			Blocks.field_10220,
			Blocks.field_10052,
			Blocks.field_10078,
			Blocks.field_10426,
			Blocks.field_10550,
			Blocks.field_10004,
			Blocks.field_10475,
			Blocks.field_10383,
			Blocks.field_10501
		);
		this.registerCarpet(Blocks.field_10446, Blocks.field_10466);
		this.registerCarpet(Blocks.field_10095, Blocks.field_9977);
		this.registerCarpet(Blocks.field_10215, Blocks.field_10482);
		this.registerCarpet(Blocks.field_10294, Blocks.field_10290);
		this.registerCarpet(Blocks.field_10490, Blocks.field_10512);
		this.registerCarpet(Blocks.field_10028, Blocks.field_10040);
		this.registerCarpet(Blocks.field_10459, Blocks.field_10393);
		this.registerCarpet(Blocks.field_10423, Blocks.field_10591);
		this.registerCarpet(Blocks.field_10222, Blocks.field_10209);
		this.registerCarpet(Blocks.field_10619, Blocks.field_10433);
		this.registerCarpet(Blocks.field_10259, Blocks.field_10510);
		this.registerCarpet(Blocks.field_10514, Blocks.field_10043);
		this.registerCarpet(Blocks.field_10113, Blocks.field_10473);
		this.registerCarpet(Blocks.field_10170, Blocks.field_10338);
		this.registerCarpet(Blocks.field_10314, Blocks.field_10536);
		this.registerCarpet(Blocks.field_10146, Blocks.field_10106);
		this.registerFlowerPotPlant(Blocks.field_10112, Blocks.field_10128, BlockStateModelGenerator.TintType.field_22839);
		this.registerFlowerPotPlant(Blocks.field_10182, Blocks.field_10354, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10449, Blocks.field_10151, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10086, Blocks.field_9981, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10226, Blocks.field_10162, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10573, Blocks.field_10365, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10270, Blocks.field_10598, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10048, Blocks.field_10249, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10156, Blocks.field_10400, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10315, Blocks.field_10061, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10554, Blocks.field_10074, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_9995, Blocks.field_10358, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10548, Blocks.field_10273, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10606, Blocks.field_9998, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10559, Blocks.field_10138, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10251, Blocks.field_10324, BlockStateModelGenerator.TintType.field_22840);
		this.registerFlowerPotPlant(Blocks.field_10428, Blocks.field_10487, BlockStateModelGenerator.TintType.field_22840);
		this.registerMushroomBlock(Blocks.field_10580);
		this.registerMushroomBlock(Blocks.field_10240);
		this.registerMushroomBlock(Blocks.field_10556);
		this.registerTintableCross(Blocks.field_10479, BlockStateModelGenerator.TintType.field_22839);
		this.registerTintableCrossBlockState(Blocks.field_10424, BlockStateModelGenerator.TintType.field_22839);
		this.registerItemModel(Items.SUGAR_CANE);
		this.registerPlantPart(Blocks.field_9993, Blocks.field_10463, BlockStateModelGenerator.TintType.field_22839);
		this.registerItemModel(Items.KELP);
		this.excludeFromSimpleItemModelGeneration(Blocks.field_10463);
		this.registerPlantPart(Blocks.field_22123, Blocks.field_22124, BlockStateModelGenerator.TintType.field_22840);
		this.registerPlantPart(Blocks.field_23078, Blocks.field_23079, BlockStateModelGenerator.TintType.field_22840);
		this.registerItemModel(Blocks.field_22123, "_plant");
		this.excludeFromSimpleItemModelGeneration(Blocks.field_22124);
		this.registerItemModel(Blocks.field_23078, "_plant");
		this.excludeFromSimpleItemModelGeneration(Blocks.field_23079);
		this.registerTintableCross(Blocks.field_10108, BlockStateModelGenerator.TintType.field_22839, Texture.cross(Texture.getSubId(Blocks.field_10211, "_stage0")));
		this.registerBamboo();
		this.registerTintableCross(Blocks.field_10343, BlockStateModelGenerator.TintType.field_22840);
		this.registerDoubleBlock(Blocks.field_10378, BlockStateModelGenerator.TintType.field_22840);
		this.registerDoubleBlock(Blocks.field_10430, BlockStateModelGenerator.TintType.field_22840);
		this.registerDoubleBlock(Blocks.field_10003, BlockStateModelGenerator.TintType.field_22840);
		this.registerDoubleBlock(Blocks.field_10214, BlockStateModelGenerator.TintType.field_22839);
		this.registerDoubleBlock(Blocks.field_10313, BlockStateModelGenerator.TintType.field_22839);
		this.registerSunflower();
		this.registerTallSeagrass();
		this.registerCoral(
			Blocks.field_10125,
			Blocks.field_10082,
			Blocks.field_10309,
			Blocks.field_10614,
			Blocks.field_10053,
			Blocks.field_10448,
			Blocks.field_10584,
			Blocks.field_10347
		);
		this.registerCoral(
			Blocks.field_10339,
			Blocks.field_10572,
			Blocks.field_10629,
			Blocks.field_10264,
			Blocks.field_10079,
			Blocks.field_10097,
			Blocks.field_10186,
			Blocks.field_10116
		);
		this.registerCoral(
			Blocks.field_10134,
			Blocks.field_10296,
			Blocks.field_10000,
			Blocks.field_10396,
			Blocks.field_10427,
			Blocks.field_10047,
			Blocks.field_10447,
			Blocks.field_10094
		);
		this.registerCoral(
			Blocks.field_10618,
			Blocks.field_10579,
			Blocks.field_10516,
			Blocks.field_10111,
			Blocks.field_10551,
			Blocks.field_10568,
			Blocks.field_10498,
			Blocks.field_10557
		);
		this.registerCoral(
			Blocks.field_10169,
			Blocks.field_10032,
			Blocks.field_10464,
			Blocks.field_10488,
			Blocks.field_10005,
			Blocks.field_10221,
			Blocks.field_9976,
			Blocks.field_10239
		);
		this.registerGourd(Blocks.field_10168, Blocks.field_10150);
		this.registerGourd(Blocks.field_9984, Blocks.field_10331);
		this.registerCubeAllModelTexturePool(Blocks.field_10218)
			.button(Blocks.field_10278)
			.fence(Blocks.field_10144)
			.fenceGate(Blocks.field_10457)
			.pressurePlate(Blocks.field_10397)
			.sign(Blocks.field_10284, Blocks.field_10401)
			.slab(Blocks.field_10031)
			.stairs(Blocks.field_10256);
		this.registerDoor(Blocks.field_10232);
		this.registerOrientableTrapdoor(Blocks.field_10608);
		this.registerLog(Blocks.field_10533).log(Blocks.field_10533).wood(Blocks.field_9999);
		this.registerLog(Blocks.field_10622).log(Blocks.field_10622).wood(Blocks.field_10103);
		this.registerFlowerPotPlant(Blocks.field_10385, Blocks.field_10564, BlockStateModelGenerator.TintType.field_22840);
		this.registerSingleton(Blocks.field_10098, TexturedModel.LEAVES);
		this.registerCubeAllModelTexturePool(Blocks.field_10148)
			.button(Blocks.field_10417)
			.fence(Blocks.field_10299)
			.fenceGate(Blocks.field_10513)
			.pressurePlate(Blocks.field_10592)
			.sign(Blocks.field_10231, Blocks.field_10391)
			.slab(Blocks.field_10257)
			.stairs(Blocks.field_10408);
		this.registerDoor(Blocks.field_10352);
		this.registerOrientableTrapdoor(Blocks.field_10486);
		this.registerLog(Blocks.field_10511).log(Blocks.field_10511).wood(Blocks.field_10307);
		this.registerLog(Blocks.field_10366).log(Blocks.field_10366).wood(Blocks.field_10204);
		this.registerFlowerPotPlant(Blocks.field_10575, Blocks.field_10577, BlockStateModelGenerator.TintType.field_22840);
		this.registerSingleton(Blocks.field_10539, TexturedModel.LEAVES);
		this.registerCubeAllModelTexturePool(Blocks.field_10161)
			.button(Blocks.field_10057)
			.fence(Blocks.field_10620)
			.fenceGate(Blocks.field_10188)
			.pressurePlate(Blocks.field_10484)
			.sign(Blocks.field_10121, Blocks.field_10187)
			.slab(Blocks.field_10119)
			.slab(Blocks.field_10298)
			.stairs(Blocks.field_10563);
		this.registerDoor(Blocks.field_10149);
		this.registerTrapdoor(Blocks.field_10137);
		this.registerLog(Blocks.field_10431).log(Blocks.field_10431).wood(Blocks.field_10126);
		this.registerLog(Blocks.field_10519).log(Blocks.field_10519).wood(Blocks.field_10250);
		this.registerFlowerPotPlant(Blocks.field_10394, Blocks.field_10468, BlockStateModelGenerator.TintType.field_22840);
		this.registerSingleton(Blocks.field_10503, TexturedModel.LEAVES);
		this.registerCubeAllModelTexturePool(Blocks.field_9975)
			.button(Blocks.field_10066)
			.fence(Blocks.field_10020)
			.fenceGate(Blocks.field_10291)
			.pressurePlate(Blocks.field_10332)
			.sign(Blocks.field_10411, Blocks.field_10088)
			.slab(Blocks.field_10071)
			.stairs(Blocks.field_10569);
		this.registerDoor(Blocks.field_10521);
		this.registerOrientableTrapdoor(Blocks.field_10323);
		this.registerLog(Blocks.field_10037).log(Blocks.field_10037).wood(Blocks.field_10155);
		this.registerLog(Blocks.field_10436).log(Blocks.field_10436).wood(Blocks.field_10558);
		this.registerFlowerPotPlant(Blocks.field_10217, Blocks.field_10192, BlockStateModelGenerator.TintType.field_22840);
		this.registerSingleton(Blocks.field_9988, TexturedModel.LEAVES);
		this.registerCubeAllModelTexturePool(Blocks.field_10075)
			.button(Blocks.field_10493)
			.fence(Blocks.field_10132)
			.fenceGate(Blocks.field_10196)
			.pressurePlate(Blocks.field_10470)
			.sign(Blocks.field_10330, Blocks.field_10265)
			.slab(Blocks.field_10500)
			.stairs(Blocks.field_10616);
		this.registerDoor(Blocks.field_10403);
		this.registerTrapdoor(Blocks.field_10246);
		this.registerLog(Blocks.field_10010).log(Blocks.field_10010).wood(Blocks.field_10178);
		this.registerLog(Blocks.field_10244).log(Blocks.field_10244).wood(Blocks.field_10374);
		this.registerFlowerPotPlant(Blocks.field_10160, Blocks.field_10076, BlockStateModelGenerator.TintType.field_22840);
		this.registerSingleton(Blocks.field_10035, TexturedModel.LEAVES);
		this.registerCubeAllModelTexturePool(Blocks.field_10334)
			.button(Blocks.field_10553)
			.fence(Blocks.field_10319)
			.fenceGate(Blocks.field_10041)
			.pressurePlate(Blocks.field_10026)
			.sign(Blocks.field_10544, Blocks.field_10587)
			.slab(Blocks.field_10617)
			.stairs(Blocks.field_10122);
		this.registerDoor(Blocks.field_10627);
		this.registerOrientableTrapdoor(Blocks.field_10017);
		this.registerLog(Blocks.field_10306).log(Blocks.field_10306).wood(Blocks.field_10303);
		this.registerLog(Blocks.field_10254).log(Blocks.field_10254).wood(Blocks.field_10084);
		this.registerFlowerPotPlant(Blocks.field_10276, Blocks.field_10304, BlockStateModelGenerator.TintType.field_22840);
		this.registerSingleton(Blocks.field_10335, TexturedModel.LEAVES);
		this.registerCubeAllModelTexturePool(Blocks.field_22126)
			.button(Blocks.field_22100)
			.fence(Blocks.field_22132)
			.fenceGate(Blocks.field_22096)
			.pressurePlate(Blocks.field_22130)
			.sign(Blocks.field_22104, Blocks.field_22106)
			.slab(Blocks.field_22128)
			.stairs(Blocks.field_22098);
		this.registerDoor(Blocks.field_22102);
		this.registerOrientableTrapdoor(Blocks.field_22094);
		this.registerLog(Blocks.field_22118).stem(Blocks.field_22118).wood(Blocks.field_22505);
		this.registerLog(Blocks.field_22119).stem(Blocks.field_22119).wood(Blocks.field_22506);
		this.registerFlowerPotPlant(Blocks.field_22121, Blocks.field_22424, BlockStateModelGenerator.TintType.field_22840);
		this.registerRoots(Blocks.field_22125, Blocks.field_22426);
		this.registerCubeAllModelTexturePool(Blocks.field_22127)
			.button(Blocks.field_22101)
			.fence(Blocks.field_22133)
			.fenceGate(Blocks.field_22097)
			.pressurePlate(Blocks.field_22131)
			.sign(Blocks.field_22105, Blocks.field_22107)
			.slab(Blocks.field_22129)
			.stairs(Blocks.field_22099);
		this.registerDoor(Blocks.field_22103);
		this.registerOrientableTrapdoor(Blocks.field_22095);
		this.registerLog(Blocks.field_22111).stem(Blocks.field_22111).wood(Blocks.field_22503);
		this.registerLog(Blocks.field_22112).stem(Blocks.field_22112).wood(Blocks.field_22504);
		this.registerFlowerPotPlant(Blocks.field_22114, Blocks.field_22425, BlockStateModelGenerator.TintType.field_22840);
		this.registerRoots(Blocks.field_22116, Blocks.field_22427);
		this.registerTintableCrossBlockState(Blocks.field_22117, BlockStateModelGenerator.TintType.field_22840);
		this.registerItemModel(Items.NETHER_SPROUTS);
		this.registerTexturePool(Texture.all(Blocks.field_10340)).base(texture -> {
			Identifier identifier = Models.field_22972.upload(Blocks.field_10340, texture, this.modelCollector);
			Identifier identifier2 = Models.field_22973.upload(Blocks.field_10340, texture, this.modelCollector);
			this.blockStateCollector.accept(createBlockStateWithTwoModelAndRandomInversion(Blocks.field_10340, identifier, identifier2));
			return identifier;
		}).slab(Blocks.field_10454).pressurePlate(Blocks.field_10158).button(Blocks.field_10494).stairs(Blocks.field_10440);
		this.registerDoor(Blocks.field_9973);
		this.registerTrapdoor(Blocks.field_10453);
		this.registerCubeAllModelTexturePool(Blocks.field_10056).wall(Blocks.field_10252).stairs(Blocks.field_10392).slab(Blocks.field_10131);
		this.registerCubeAllModelTexturePool(Blocks.field_10065).wall(Blocks.field_10059).stairs(Blocks.field_10173).slab(Blocks.field_10024);
		this.registerCubeAllModelTexturePool(Blocks.field_10445).wall(Blocks.field_10625).stairs(Blocks.field_10596).slab(Blocks.field_10351);
		this.registerCubeAllModelTexturePool(Blocks.field_9989).wall(Blocks.field_9990).stairs(Blocks.field_10207).slab(Blocks.field_10405);
		this.registerCubeAllModelTexturePool(Blocks.field_10135).wall(Blocks.field_10530).stairs(Blocks.field_10350).slab(Blocks.field_10389);
		this.registerCubeAllModelTexturePool(Blocks.field_10006).stairs(Blocks.field_10190).slab(Blocks.field_10236);
		this.registerCubeAllModelTexturePool(Blocks.field_10297).stairs(Blocks.field_10130).slab(Blocks.field_10623);
		this.registerTexturePool(Blocks.field_9979, TexturedModel.WALL_CUBE_BOTTOM_TOP).wall(Blocks.field_10630).stairs(Blocks.field_10142).slab(Blocks.field_10007);
		this.registerTexturePool(Blocks.field_10467, TexturedModel.getCubeAll(Texture.getSubId(Blocks.field_9979, "_top")))
			.slab(Blocks.field_10262)
			.stairs(Blocks.field_10549);
		this.registerTexturePool(
				Blocks.field_10361,
				TexturedModel.CUBE_COLUMN.get(Blocks.field_9979).texture(texture -> texture.put(TextureKey.field_23018, Texture.getId(Blocks.field_10361)))
			)
			.slab(Blocks.field_18890);
		this.registerTexturePool(Blocks.field_10344, TexturedModel.WALL_CUBE_BOTTOM_TOP).wall(Blocks.field_10413).stairs(Blocks.field_10420).slab(Blocks.field_10624);
		this.registerTexturePool(Blocks.field_10483, TexturedModel.getCubeAll(Texture.getSubId(Blocks.field_10344, "_top")))
			.slab(Blocks.field_10283)
			.stairs(Blocks.field_10039);
		this.registerTexturePool(
				Blocks.field_10518,
				TexturedModel.CUBE_COLUMN.get(Blocks.field_10344).texture(texture -> texture.put(TextureKey.field_23018, Texture.getId(Blocks.field_10518)))
			)
			.slab(Blocks.field_18891);
		this.registerCubeAllModelTexturePool(Blocks.field_10104).wall(Blocks.field_10269).stairs(Blocks.field_10089).slab(Blocks.field_10191);
		this.registerCubeAllModelTexturePool(Blocks.field_10266)
			.fence(Blocks.field_10364)
			.wall(Blocks.field_10127)
			.stairs(Blocks.field_10159)
			.slab(Blocks.field_10390);
		this.registerCubeAllModelTexturePool(Blocks.field_10286).stairs(Blocks.field_9992).slab(Blocks.field_10175);
		this.registerCubeAllModelTexturePool(Blocks.field_10508).wall(Blocks.field_10517).stairs(Blocks.field_10216).slab(Blocks.field_10507);
		this.registerCubeAllModelTexturePool(Blocks.field_10346).stairs(Blocks.field_10310).slab(Blocks.field_10412);
		this.registerCubeAllModelTexturePool(Blocks.field_10474).wall(Blocks.field_10072).stairs(Blocks.field_10607).slab(Blocks.field_10189);
		this.registerCubeAllModelTexturePool(Blocks.field_10289).stairs(Blocks.field_10435).slab(Blocks.field_10329);
		this.registerCubeAllModelTexturePool(Blocks.field_10115).wall(Blocks.field_10489).stairs(Blocks.field_10386).slab(Blocks.field_10016);
		this.registerCubeAllModelTexturePool(Blocks.field_10093).stairs(Blocks.field_9994).slab(Blocks.field_10322);
		this.registerCubeAllModelTexturePool(Blocks.field_10462).wall(Blocks.field_10001).stairs(Blocks.field_10012).slab(Blocks.field_10064);
		this.registerTexturePool(Blocks.field_10153, TexturedModel.CUBE_COLUMN).stairs(Blocks.field_10451).slab(Blocks.field_10237);
		this.registerTexturePool(Blocks.field_9978, TexturedModel.getCubeAll(Texture.getSubId(Blocks.field_10153, "_bottom")))
			.stairs(Blocks.field_10245)
			.slab(Blocks.field_10601);
		this.registerCubeAllModelTexturePool(Blocks.field_9986).slab(Blocks.field_10478).stairs(Blocks.field_10497).wall(Blocks.field_10311);
		this.registerTexturePool(Blocks.field_23869, TexturedModel.field_23959).wall(Blocks.field_23871).stairs(Blocks.field_23870).slab(Blocks.field_23872);
		this.registerCubeAllModelTexturePool(Blocks.field_23874).wall(Blocks.field_23879).stairs(Blocks.field_23878).slab(Blocks.field_23877);
		this.registerCubeAllModelTexturePool(Blocks.field_23873)
			.wall(Blocks.field_23865)
			.pressurePlate(Blocks.field_23863)
			.button(Blocks.field_23864)
			.stairs(Blocks.field_23861)
			.slab(Blocks.field_23862);
		this.registerSmoothStone();
		this.registerTurnableRail(Blocks.field_10167);
		this.registerStraightRail(Blocks.field_10425);
		this.registerStraightRail(Blocks.field_10025);
		this.registerStraightRail(Blocks.field_10546);
		this.registerComparator();
		this.registerCommandBlock(Blocks.field_10525);
		this.registerCommandBlock(Blocks.field_10263);
		this.registerCommandBlock(Blocks.field_10395);
		this.registerAnvil(Blocks.field_10535);
		this.registerAnvil(Blocks.field_10105);
		this.registerAnvil(Blocks.field_10414);
		this.registerBarrel();
		this.registerBell();
		this.registerCooker(Blocks.field_10181, TexturedModel.ORIENTABLE);
		this.registerCooker(Blocks.field_16333, TexturedModel.ORIENTABLE);
		this.registerCooker(Blocks.field_16334, TexturedModel.ORIENTABLE_WITH_BOTTOM);
		this.registerRedstone();
		this.registerRespawnAnchor();
		this.registerInfested(Blocks.field_10552, Blocks.field_10176);
		this.registerInfested(Blocks.field_10445, Blocks.field_10492);
		this.registerInfested(Blocks.field_10416, Blocks.field_10100);
		this.registerInfested(Blocks.field_10065, Blocks.field_10480);
		this.registerInfestedStone();
		this.registerInfested(Blocks.field_10056, Blocks.field_10387);
		SpawnEggItem.getAll().forEach(spawnEggItem -> this.registerParentedItemModel(spawnEggItem, ModelIds.getMinecraftNamespacedItem("template_spawn_egg")));
	}

	class BlockTexturePool {
		private final Texture texture;
		@Nullable
		private Identifier baseModelId;

		public BlockTexturePool(Texture texture) {
			this.texture = texture;
		}

		public BlockStateModelGenerator.BlockTexturePool base(Block block, Model model) {
			this.baseModelId = model.upload(block, this.texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, this.baseModelId));
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool base(Function<Texture, Identifier> modelFactory) {
			this.baseModelId = (Identifier)modelFactory.apply(this.texture);
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool button(Block buttonBlock) {
			Identifier identifier = Models.field_22981.upload(buttonBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.field_22982.upload(buttonBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createButtonBlockState(buttonBlock, identifier, identifier2));
			Identifier identifier3 = Models.field_22983.upload(buttonBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.registerParentedItemModel(buttonBlock, identifier3);
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool wall(Block wallBlock) {
			Identifier identifier = Models.field_22991.upload(wallBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.field_22992.upload(wallBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier3 = Models.field_22993.upload(wallBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createWallBlockState(wallBlock, identifier, identifier2, identifier3));
			Identifier identifier4 = Models.field_22994.upload(wallBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.registerParentedItemModel(wallBlock, identifier4);
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool fence(Block fenceBlock) {
			Identifier identifier = Models.field_22988.upload(fenceBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.field_22989.upload(fenceBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createFenceBlockState(fenceBlock, identifier, identifier2));
			Identifier identifier3 = Models.field_22990.upload(fenceBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.registerParentedItemModel(fenceBlock, identifier3);
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool fenceGate(Block fenceGateBlock) {
			Identifier identifier = Models.field_22996.upload(fenceGateBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.field_22995.upload(fenceGateBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier3 = Models.field_22905.upload(fenceGateBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier4 = Models.field_22904.upload(fenceGateBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector
				.accept(BlockStateModelGenerator.createFenceGateBlockState(fenceGateBlock, identifier, identifier2, identifier3, identifier4));
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool pressurePlate(Block pressurePlateBlock) {
			Identifier identifier = Models.field_22906.upload(pressurePlateBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.field_22907.upload(pressurePlateBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector
				.accept(BlockStateModelGenerator.createPressurePlateBlockState(pressurePlateBlock, identifier, identifier2));
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool sign(Block signBlock, Block wallSignBlock) {
			Identifier identifier = Models.PARTICLE.upload(signBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(signBlock, identifier));
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(wallSignBlock, identifier));
			BlockStateModelGenerator.this.registerItemModel(signBlock.asItem());
			BlockStateModelGenerator.this.excludeFromSimpleItemModelGeneration(wallSignBlock);
			return this;
		}

		public BlockStateModelGenerator.BlockTexturePool slab(Block slabBlock) {
			if (this.baseModelId == null) {
				throw new IllegalStateException("Full block not generated yet");
			} else {
				Identifier identifier = Models.field_22909.upload(slabBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
				Identifier identifier2 = Models.field_22910.upload(slabBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
				BlockStateModelGenerator.this.blockStateCollector
					.accept(BlockStateModelGenerator.createSlabBlockState(slabBlock, identifier, identifier2, this.baseModelId));
				return this;
			}
		}

		public BlockStateModelGenerator.BlockTexturePool stairs(Block stairsBlock) {
			Identifier identifier = Models.field_22913.upload(stairsBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.field_22912.upload(stairsBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier3 = Models.field_22914.upload(stairsBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createStairsBlockState(stairsBlock, identifier, identifier2, identifier3));
			return this;
		}
	}

	class BuiltinModelPool {
		private final Identifier modelId;

		public BuiltinModelPool(Identifier modelId, Block block) {
			this.modelId = Models.PARTICLE.upload(modelId, Texture.particle(block), BlockStateModelGenerator.this.modelCollector);
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
				model.upload(ModelIds.getItemModelId(block.asItem()), Texture.particle(block), BlockStateModelGenerator.this.modelCollector);
			}

			return this.includeWithItem(blocks);
		}
	}

	class LogTexturePool {
		private final Texture texture;

		public LogTexturePool(Texture texture) {
			this.texture = texture;
		}

		public BlockStateModelGenerator.LogTexturePool wood(Block woodBlock) {
			Texture texture = this.texture.copyAndAdd(TextureKey.field_23013, this.texture.getTexture(TextureKey.field_23018));
			Identifier identifier = Models.field_22974.upload(woodBlock, texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(woodBlock, identifier));
			return this;
		}

		public BlockStateModelGenerator.LogTexturePool stem(Block stemBlock) {
			Identifier identifier = Models.field_22974.upload(stemBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(stemBlock, identifier));
			return this;
		}

		public BlockStateModelGenerator.LogTexturePool log(Block logBlock) {
			Identifier identifier = Models.field_22974.upload(logBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			Identifier identifier2 = Models.field_22975.upload(logBlock, this.texture, BlockStateModelGenerator.this.modelCollector);
			BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(logBlock, identifier, identifier2));
			return this;
		}
	}

	static enum TintType {
		field_22839,
		field_22840;

		public Model getCrossModel() {
			return this == field_22839 ? Models.field_22922 : Models.field_22921;
		}

		public Model getFlowerPotCrossModel() {
			return this == field_22839 ? Models.field_22924 : Models.field_22923;
		}
	}
}
