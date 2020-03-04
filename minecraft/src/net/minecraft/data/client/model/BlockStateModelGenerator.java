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
	private final Consumer<BlockStateSupplier> blockStateWriter;
	private final BiConsumer<Identifier, Supplier<JsonElement>> modelWriter;
	private final Consumer<Item> field_22832;

	public BlockStateModelGenerator(Consumer<BlockStateSupplier> consumer, BiConsumer<Identifier, Supplier<JsonElement>> biConsumer, Consumer<Item> consumer2) {
		this.blockStateWriter = consumer;
		this.modelWriter = biConsumer;
		this.field_22832 = consumer2;
	}

	private void method_25540(Block block) {
		this.field_22832.accept(block.asItem());
	}

	private void method_25623(Block block, Identifier identifier) {
		this.modelWriter.accept(ModelIds.getItemModelId(block.asItem()), new SimpleModelSupplier(identifier));
	}

	private void method_25538(Item item, Identifier identifier) {
		this.modelWriter.accept(ModelIds.getItemModelId(item), new SimpleModelSupplier(identifier));
	}

	private void method_25537(Item item) {
		Models.GENERATED.upload(ModelIds.getItemModelId(item), Texture.layer0(item), this.modelWriter);
	}

	private void method_25600(Block block) {
		Item item = block.asItem();
		if (item != Items.AIR) {
			Models.GENERATED.upload(ModelIds.getItemModelId(item), Texture.layer0(block), this.modelWriter);
		}
	}

	private void method_25556(Block block, String string) {
		Item item = block.asItem();
		Models.GENERATED.upload(ModelIds.getItemModelId(item), Texture.layer0(Texture.getSubModelId(block, string)), this.modelWriter);
	}

	private static BlockStateVariantMap method_25599() {
		return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
			.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
			.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
			.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
			.register(Direction.NORTH, BlockStateVariant.create());
	}

	private static BlockStateVariantMap method_25618() {
		return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
			.register(Direction.SOUTH, BlockStateVariant.create())
			.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
			.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
			.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270));
	}

	private static BlockStateVariantMap method_25630() {
		return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
			.register(Direction.EAST, BlockStateVariant.create())
			.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
			.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
			.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270));
	}

	private static BlockStateVariantMap method_25640() {
		return BlockStateVariantMap.create(Properties.FACING)
			.register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
			.register(Direction.UP, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R270))
			.register(Direction.NORTH, BlockStateVariant.create())
			.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
			.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
			.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90));
	}

	private static VariantsBlockStateSupplier method_25634(Block block, Identifier identifier) {
		return VariantsBlockStateSupplier.create(block, method_25584(identifier));
	}

	private static BlockStateVariant[] method_25584(Identifier identifier) {
		return new BlockStateVariant[]{
			BlockStateVariant.create().put(VariantSettings.MODEL, identifier),
			BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90),
			BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180),
			BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R270)
		};
	}

	private static VariantsBlockStateSupplier method_25645(Block block, Identifier identifier, Identifier identifier2) {
		return VariantsBlockStateSupplier.create(
			block,
			BlockStateVariant.create().put(VariantSettings.MODEL, identifier),
			BlockStateVariant.create().put(VariantSettings.MODEL, identifier2),
			BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180),
			BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R180)
		);
	}

	private static BlockStateVariantMap method_25565(BooleanProperty booleanProperty, Identifier identifier, Identifier identifier2) {
		return BlockStateVariantMap.create(booleanProperty)
			.register(true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
			.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2));
	}

	private void method_25619(Block block) {
		Identifier identifier = TexturedModel.CUBE_ALL.upload(block, this.modelWriter);
		Identifier identifier2 = TexturedModel.CUBE_MIRRORED_ALL.upload(block, this.modelWriter);
		this.blockStateWriter.accept(method_25645(block, identifier, identifier2));
	}

	private void method_25631(Block block) {
		Identifier identifier = TexturedModel.CUBE_ALL.upload(block, this.modelWriter);
		this.blockStateWriter.accept(method_25634(block, identifier));
	}

	private static BlockStateSupplier method_25654(Block block, Identifier identifier, Identifier identifier2) {
		return VariantsBlockStateSupplier.create(block)
			.method_25775(
				BlockStateVariantMap.create(Properties.POWERED)
					.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.register(true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
			)
			.method_25775(
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

	private static BlockStateVariantMap.QuadrupleProperty<Direction, DoubleBlockHalf, DoorHinge, Boolean> method_25572(
		BlockStateVariantMap.QuadrupleProperty<Direction, DoubleBlockHalf, DoorHinge, Boolean> quadrupleProperty,
		DoubleBlockHalf doubleBlockHalf,
		Identifier identifier,
		Identifier identifier2
	) {
		return quadrupleProperty.register(Direction.EAST, doubleBlockHalf, DoorHinge.LEFT, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
			.register(
				Direction.SOUTH,
				doubleBlockHalf,
				DoorHinge.LEFT,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90)
			)
			.register(
				Direction.WEST,
				doubleBlockHalf,
				DoorHinge.LEFT,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180)
			)
			.register(
				Direction.NORTH,
				doubleBlockHalf,
				DoorHinge.LEFT,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R270)
			)
			.register(Direction.EAST, doubleBlockHalf, DoorHinge.RIGHT, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
			.register(
				Direction.SOUTH,
				doubleBlockHalf,
				DoorHinge.RIGHT,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90)
			)
			.register(
				Direction.WEST,
				doubleBlockHalf,
				DoorHinge.RIGHT,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R180)
			)
			.register(
				Direction.NORTH,
				doubleBlockHalf,
				DoorHinge.RIGHT,
				false,
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R270)
			)
			.register(
				Direction.EAST,
				doubleBlockHalf,
				DoorHinge.LEFT,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90)
			)
			.register(
				Direction.SOUTH,
				doubleBlockHalf,
				DoorHinge.LEFT,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R180)
			)
			.register(
				Direction.WEST,
				doubleBlockHalf,
				DoorHinge.LEFT,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R270)
			)
			.register(Direction.NORTH, doubleBlockHalf, DoorHinge.LEFT, true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
			.register(
				Direction.EAST,
				doubleBlockHalf,
				DoorHinge.RIGHT,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R270)
			)
			.register(Direction.SOUTH, doubleBlockHalf, DoorHinge.RIGHT, true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
			.register(
				Direction.WEST,
				doubleBlockHalf,
				DoorHinge.RIGHT,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90)
			)
			.register(
				Direction.NORTH,
				doubleBlockHalf,
				DoorHinge.RIGHT,
				true,
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180)
			);
	}

	private static BlockStateSupplier method_25609(Block block, Identifier identifier, Identifier identifier2, Identifier identifier3, Identifier identifier4) {
		return VariantsBlockStateSupplier.create(block)
			.method_25775(
				method_25572(
					method_25572(
						BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.DOUBLE_BLOCK_HALF, Properties.DOOR_HINGE, Properties.OPEN),
						DoubleBlockHalf.LOWER,
						identifier,
						identifier2
					),
					DoubleBlockHalf.UPPER,
					identifier3,
					identifier4
				)
			);
	}

	private static BlockStateSupplier method_25661(Block block, Identifier identifier, Identifier identifier2) {
		return MultipartBlockStateSupplier.create(block)
			.with(BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
			.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.UVLOCK, true))
			.with(
				When.create().set(Properties.EAST, true),
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.SOUTH, true),
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.WEST, true),
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)
			);
	}

	private static BlockStateSupplier method_25636(Block block, Identifier identifier, Identifier identifier2, Identifier identifier3) {
		return MultipartBlockStateSupplier.create(block)
			.with(When.create().set(Properties.UP, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
			.with(
				When.create().set(Properties.NORTH_WALL_SHAPE, WallShape.LOW),
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.EAST_WALL_SHAPE, WallShape.LOW),
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.SOUTH_WALL_SHAPE, WallShape.LOW),
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.WEST_WALL_SHAPE, WallShape.LOW),
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.NORTH_WALL_SHAPE, WallShape.TALL),
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.EAST_WALL_SHAPE, WallShape.TALL),
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.SOUTH_WALL_SHAPE, WallShape.TALL),
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)
			)
			.with(
				When.create().set(Properties.WEST_WALL_SHAPE, WallShape.TALL),
				BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)
			);
	}

	private static BlockStateSupplier method_25626(Block block, Identifier identifier, Identifier identifier2, Identifier identifier3, Identifier identifier4) {
		return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.UVLOCK, true))
			.method_25775(method_25618())
			.method_25775(
				BlockStateVariantMap.create(Properties.IN_WALL, Properties.OPEN)
					.register(false, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.register(true, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier4))
					.register(false, true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.register(true, true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
			);
	}

	private static BlockStateSupplier method_25646(Block block, Identifier identifier, Identifier identifier2, Identifier identifier3) {
		return VariantsBlockStateSupplier.create(block)
			.method_25775(
				BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.STAIR_SHAPE)
					.register(Direction.EAST, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						StairShape.STRAIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.BOTTOM,
						StairShape.STRAIGHT,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.BOTTOM,
						StairShape.STRAIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.BOTTOM,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.BOTTOM,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.BOTTOM,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
					)
					.register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
					.register(
						Direction.NORTH,
						BlockHalf.BOTTOM,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.BOTTOM,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.BOTTOM,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.BOTTOM,
						StairShape.INNER_LEFT,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						StairShape.INNER_LEFT,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)
					)
					.register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.register(
						Direction.NORTH,
						BlockHalf.BOTTOM,
						StairShape.INNER_LEFT,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						StairShape.STRAIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						StairShape.STRAIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						StairShape.STRAIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.TOP,
						StairShape.STRAIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.TOP,
						StairShape.OUTER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.TOP,
						StairShape.OUTER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.TOP,
						StairShape.INNER_RIGHT,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						StairShape.INNER_LEFT,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						StairShape.INNER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						StairShape.INNER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							.put(VariantSettings.UVLOCK, true)
					)
					.register(
						Direction.NORTH,
						BlockHalf.TOP,
						StairShape.INNER_LEFT,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							.put(VariantSettings.UVLOCK, true)
					)
			);
	}

	private static BlockStateSupplier method_25655(Block block, Identifier identifier, Identifier identifier2, Identifier identifier3) {
		return VariantsBlockStateSupplier.create(block)
			.method_25775(
				BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.OPEN)
					.register(Direction.NORTH, BlockHalf.BOTTOM, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.register(
						Direction.SOUTH,
						BlockHalf.BOTTOM,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.register(
						Direction.EAST,
						BlockHalf.BOTTOM,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.register(Direction.NORTH, BlockHalf.TOP, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						false,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.register(Direction.NORTH, BlockHalf.BOTTOM, true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
					.register(
						Direction.SOUTH,
						BlockHalf.BOTTOM,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.register(
						Direction.EAST,
						BlockHalf.BOTTOM,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.register(
						Direction.NORTH,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R0)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier3)
							.put(VariantSettings.X, VariantSettings.Rotation.R180)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
			);
	}

	private static BlockStateSupplier method_25662(Block block, Identifier identifier, Identifier identifier2, Identifier identifier3) {
		return VariantsBlockStateSupplier.create(block)
			.method_25775(
				BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.OPEN)
					.register(Direction.NORTH, BlockHalf.BOTTOM, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.register(Direction.SOUTH, BlockHalf.BOTTOM, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.register(Direction.EAST, BlockHalf.BOTTOM, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.register(Direction.WEST, BlockHalf.BOTTOM, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.register(Direction.NORTH, BlockHalf.TOP, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.register(Direction.SOUTH, BlockHalf.TOP, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.register(Direction.EAST, BlockHalf.TOP, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.register(Direction.WEST, BlockHalf.TOP, false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.register(Direction.NORTH, BlockHalf.BOTTOM, true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
					.register(
						Direction.SOUTH,
						BlockHalf.BOTTOM,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.register(
						Direction.EAST,
						BlockHalf.BOTTOM,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.register(
						Direction.WEST,
						BlockHalf.BOTTOM,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.register(Direction.NORTH, BlockHalf.TOP, true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
					.register(
						Direction.SOUTH,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R180)
					)
					.register(
						Direction.EAST,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
					.register(
						Direction.WEST,
						BlockHalf.TOP,
						true,
						BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
			);
	}

	private static VariantsBlockStateSupplier method_25644(Block block, Identifier identifier) {
		return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, identifier));
	}

	private static BlockStateVariantMap method_25649() {
		return BlockStateVariantMap.create(Properties.AXIS)
			.register(Direction.Axis.Y, BlockStateVariant.create())
			.register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
			.register(
				Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90)
			);
	}

	private static BlockStateSupplier method_25653(Block block, Identifier identifier) {
		return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, identifier)).method_25775(method_25649());
	}

	private void method_25553(Block block, TexturedModel.Factory factory) {
		Identifier identifier = factory.upload(block, this.modelWriter);
		this.blockStateWriter.accept(method_25653(block, identifier));
	}

	private void method_25605(Block block, TexturedModel.Factory factory) {
		Identifier identifier = factory.upload(block, this.modelWriter);
		this.blockStateWriter
			.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, identifier)).method_25775(method_25599()));
	}

	private static BlockStateSupplier method_25667(Block block, Identifier identifier, Identifier identifier2) {
		return VariantsBlockStateSupplier.create(block)
			.method_25775(
				BlockStateVariantMap.create(Properties.AXIS)
					.register(Direction.Axis.Y, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.X, VariantSettings.Rotation.R90))
					.register(
						Direction.Axis.X,
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, identifier2)
							.put(VariantSettings.X, VariantSettings.Rotation.R90)
							.put(VariantSettings.Y, VariantSettings.Rotation.R90)
					)
			);
	}

	private void method_25554(Block block, TexturedModel.Factory factory, TexturedModel.Factory factory2) {
		Identifier identifier = factory.upload(block, this.modelWriter);
		Identifier identifier2 = factory2.upload(block, this.modelWriter);
		this.blockStateWriter.accept(method_25667(block, identifier, identifier2));
	}

	private Identifier method_25557(Block block, String string, Model model, Function<Identifier, Texture> function) {
		return model.upload(block, string, (Texture)function.apply(Texture.getSubModelId(block, string)), this.modelWriter);
	}

	private static BlockStateSupplier method_25673(Block block, Identifier identifier, Identifier identifier2) {
		return VariantsBlockStateSupplier.create(block).method_25775(method_25565(Properties.POWERED, identifier2, identifier));
	}

	private static BlockStateSupplier method_25668(Block block, Identifier identifier, Identifier identifier2, Identifier identifier3) {
		return VariantsBlockStateSupplier.create(block)
			.method_25775(
				BlockStateVariantMap.create(Properties.SLAB_TYPE)
					.register(SlabType.BOTTOM, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.register(SlabType.TOP, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					.register(SlabType.DOUBLE, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
			);
	}

	private void method_25641(Block block) {
		this.method_25622(block, TexturedModel.CUBE_ALL);
	}

	private void method_25622(Block block, TexturedModel.Factory factory) {
		this.blockStateWriter.accept(method_25644(block, factory.upload(block, this.modelWriter)));
	}

	private void method_25551(Block block, Texture texture, Model model) {
		Identifier identifier = model.upload(block, texture, this.modelWriter);
		this.blockStateWriter.accept(method_25644(block, identifier));
	}

	private BlockStateModelGenerator.class_4912 method_25555(Block block, TexturedModel texturedModel) {
		return new BlockStateModelGenerator.class_4912(texturedModel.getTexture()).method_25718(block, texturedModel.getModel());
	}

	private BlockStateModelGenerator.class_4912 method_25633(Block block, TexturedModel.Factory factory) {
		TexturedModel texturedModel = factory.get(block);
		return new BlockStateModelGenerator.class_4912(texturedModel.getTexture()).method_25718(block, texturedModel.getModel());
	}

	private BlockStateModelGenerator.class_4912 method_25650(Block block) {
		return this.method_25633(block, TexturedModel.CUBE_ALL);
	}

	private BlockStateModelGenerator.class_4912 method_25574(Texture texture) {
		return new BlockStateModelGenerator.class_4912(texture);
	}

	private void method_25658(Block block) {
		Texture texture = Texture.topBottom(block);
		Identifier identifier = Models.DOOR_BOTTOM.upload(block, texture, this.modelWriter);
		Identifier identifier2 = Models.DOOR_BOTTOM_RH.upload(block, texture, this.modelWriter);
		Identifier identifier3 = Models.DOOR_TOP.upload(block, texture, this.modelWriter);
		Identifier identifier4 = Models.DOOR_TOP_RH.upload(block, texture, this.modelWriter);
		this.method_25537(block.asItem());
		this.blockStateWriter.accept(method_25609(block, identifier, identifier2, identifier3, identifier4));
	}

	private void method_25665(Block block) {
		Texture texture = Texture.texture(block);
		Identifier identifier = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_TOP.upload(block, texture, this.modelWriter);
		Identifier identifier2 = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_BOTTOM.upload(block, texture, this.modelWriter);
		Identifier identifier3 = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_OPEN.upload(block, texture, this.modelWriter);
		this.blockStateWriter.accept(method_25655(block, identifier, identifier2, identifier3));
		this.method_25623(block, identifier2);
	}

	private void method_25671(Block block) {
		Texture texture = Texture.texture(block);
		Identifier identifier = Models.TEMPLATE_TRAPDOOR_TOP.upload(block, texture, this.modelWriter);
		Identifier identifier2 = Models.TEMPLATE_TRAPDOOR_BOTTOM.upload(block, texture, this.modelWriter);
		Identifier identifier3 = Models.TEMPLATE_TRAPDOOR_OPEN.upload(block, texture, this.modelWriter);
		this.blockStateWriter.accept(method_25662(block, identifier, identifier2, identifier3));
		this.method_25623(block, identifier2);
	}

	private BlockStateModelGenerator.class_4914 method_25676(Block block) {
		return new BlockStateModelGenerator.class_4914(Texture.sideAndEndForTop(block));
	}

	private void registerSimpleState(Block block) {
		this.registerStateWithModelReference(block, block);
	}

	private void registerStateWithModelReference(Block block, Block modelReference) {
		this.blockStateWriter.accept(method_25644(block, ModelIds.getBlockModelId(modelReference)));
	}

	private void method_25548(Block block, BlockStateModelGenerator.class_4913 arg) {
		this.method_25600(block);
		this.method_25603(block, arg);
	}

	private void method_25549(Block block, BlockStateModelGenerator.class_4913 arg, Texture texture) {
		this.method_25600(block);
		this.method_25604(block, arg, texture);
	}

	private void method_25603(Block block, BlockStateModelGenerator.class_4913 arg) {
		Texture texture = Texture.cross(block);
		this.method_25604(block, arg, texture);
	}

	private void method_25604(Block block, BlockStateModelGenerator.class_4913 arg, Texture texture) {
		Identifier identifier = arg.method_25726().upload(block, texture, this.modelWriter);
		this.blockStateWriter.accept(method_25644(block, identifier));
	}

	private void method_25545(Block block, Block block2, BlockStateModelGenerator.class_4913 arg) {
		this.method_25548(block, arg);
		Texture texture = Texture.plant(block);
		Identifier identifier = arg.method_25727().upload(block2, texture, this.modelWriter);
		this.blockStateWriter.accept(method_25644(block2, identifier));
	}

	private void method_25601(Block block, Block block2) {
		TexturedModel texturedModel = TexturedModel.CORAL_FAN.get(block);
		Identifier identifier = texturedModel.upload(block, this.modelWriter);
		this.blockStateWriter.accept(method_25644(block, identifier));
		Identifier identifier2 = Models.CORAL_WALL_FAN.upload(block2, texturedModel.getTexture(), this.modelWriter);
		this.blockStateWriter
			.accept(VariantsBlockStateSupplier.create(block2, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2)).method_25775(method_25599()));
		this.method_25600(block);
	}

	private void method_25620(Block block, Block block2) {
		this.method_25537(block.asItem());
		Texture texture = Texture.stem(block);
		Texture texture2 = Texture.stemAndUpper(block, block2);
		Identifier identifier = Models.STEM_FRUIT.upload(block2, texture2, this.modelWriter);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(block2, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					.method_25775(
						BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
							.register(Direction.WEST, BlockStateVariant.create())
							.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
							.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
							.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
					)
			);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(block)
					.method_25775(
						BlockStateVariantMap.create(Properties.AGE_7)
							.register(integer -> BlockStateVariant.create().put(VariantSettings.MODEL, Models.STEM_GROWTH_STAGES[integer].upload(block, texture, this.modelWriter)))
					)
			);
	}

	private void method_25544(Block block, Block block2, Block block3, Block block4, Block block5, Block block6, Block block7, Block block8) {
		this.method_25548(block, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25548(block2, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25641(block3);
		this.method_25641(block4);
		this.method_25601(block5, block7);
		this.method_25601(block6, block8);
	}

	private void method_25621(Block block, BlockStateModelGenerator.class_4913 arg) {
		this.method_25556(block, "_top");
		Identifier identifier = this.method_25557(block, "_top", arg.method_25726(), Texture::cross);
		Identifier identifier2 = this.method_25557(block, "_bottom", arg.method_25726(), Texture::cross);
		this.method_25678(block, identifier, identifier2);
	}

	private void method_25657() {
		this.method_25556(Blocks.SUNFLOWER, "_front");
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.SUNFLOWER, "_top");
		Identifier identifier2 = this.method_25557(Blocks.SUNFLOWER, "_bottom", BlockStateModelGenerator.class_4913.field_22840.method_25726(), Texture::cross);
		this.method_25678(Blocks.SUNFLOWER, identifier, identifier2);
	}

	private void method_25664() {
		Identifier identifier = this.method_25557(Blocks.TALL_SEAGRASS, "_top", Models.TEMPLATE_SEAGRASS, Texture::texture);
		Identifier identifier2 = this.method_25557(Blocks.TALL_SEAGRASS, "_bottom", Models.TEMPLATE_SEAGRASS, Texture::texture);
		this.method_25678(Blocks.TALL_SEAGRASS, identifier, identifier2);
	}

	private void method_25678(Block block, Identifier identifier, Identifier identifier2) {
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(block)
					.method_25775(
						BlockStateVariantMap.create(Properties.DOUBLE_BLOCK_HALF)
							.register(DoubleBlockHalf.LOWER, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
							.register(DoubleBlockHalf.UPPER, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
					)
			);
	}

	private void method_25685(Block block) {
		Texture texture = Texture.rail(block);
		Texture texture2 = Texture.rail(Texture.getSubModelId(block, "_corner"));
		Identifier identifier = Models.RAIL_FLAT.upload(block, texture, this.modelWriter);
		Identifier identifier2 = Models.RAIL_CURVED.upload(block, texture2, this.modelWriter);
		Identifier identifier3 = Models.TEMPLATE_RAIL_RAISED_NE.upload(block, texture, this.modelWriter);
		Identifier identifier4 = Models.TEMPLATE_RAIL_RAISED_SW.upload(block, texture, this.modelWriter);
		this.method_25600(block);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(block)
					.method_25775(
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

	private void method_25688(Block block) {
		Identifier identifier = this.method_25557(block, "", Models.RAIL_FLAT, Texture::rail);
		Identifier identifier2 = this.method_25557(block, "", Models.TEMPLATE_RAIL_RAISED_NE, Texture::rail);
		Identifier identifier3 = this.method_25557(block, "", Models.TEMPLATE_RAIL_RAISED_SW, Texture::rail);
		Identifier identifier4 = this.method_25557(block, "_on", Models.RAIL_FLAT, Texture::rail);
		Identifier identifier5 = this.method_25557(block, "_on", Models.TEMPLATE_RAIL_RAISED_NE, Texture::rail);
		Identifier identifier6 = this.method_25557(block, "_on", Models.TEMPLATE_RAIL_RAISED_SW, Texture::rail);
		BlockStateVariantMap blockStateVariantMap = BlockStateVariantMap.create(Properties.POWERED, Properties.STRAIGHT_RAIL_SHAPE)
			.register((boolean_, railShape) -> {
				switch (railShape) {
					case NORTH_SOUTH:
						return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ ? identifier4 : identifier);
					case EAST_WEST:
						return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ ? identifier4 : identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90);
					case ASCENDING_EAST:
						return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ ? identifier5 : identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90);
					case ASCENDING_WEST:
						return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ ? identifier6 : identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90);
					case ASCENDING_NORTH:
						return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ ? identifier5 : identifier2);
					case ASCENDING_SOUTH:
						return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ ? identifier6 : identifier3);
					default:
						throw new UnsupportedOperationException("Fix you generator!");
				}
			});
		this.method_25600(block);
		this.blockStateWriter.accept(VariantsBlockStateSupplier.create(block).method_25775(blockStateVariantMap));
	}

	private BlockStateModelGenerator.class_4911 method_25585(Identifier identifier, Block block) {
		return new BlockStateModelGenerator.class_4911(identifier, block);
	}

	private BlockStateModelGenerator.class_4911 method_25632(Block block, Block block2) {
		return new BlockStateModelGenerator.class_4911(ModelIds.getBlockModelId(block), block2);
	}

	private void method_25542(Block block, Item item) {
		Identifier identifier = Models.PARTICLE.upload(block, Texture.particle(item), this.modelWriter);
		this.blockStateWriter.accept(method_25644(block, identifier));
	}

	private void method_25660(Block block, Identifier identifier) {
		Identifier identifier2 = Models.PARTICLE.upload(block, Texture.particle(identifier), this.modelWriter);
		this.blockStateWriter.accept(method_25644(block, identifier2));
	}

	private void method_25642(Block block, Block block2) {
		this.method_25622(block, TexturedModel.CUBE_ALL);
		Identifier identifier = TexturedModel.CARPET.get(block).upload(block2, this.modelWriter);
		this.blockStateWriter.accept(method_25644(block2, identifier));
	}

	private void method_25576(TexturedModel.Factory factory, Block... blocks) {
		for (Block block : blocks) {
			Identifier identifier = factory.upload(block, this.modelWriter);
			this.blockStateWriter.accept(method_25634(block, identifier));
		}
	}

	private void method_25614(TexturedModel.Factory factory, Block... blocks) {
		for (Block block : blocks) {
			Identifier identifier = factory.upload(block, this.modelWriter);
			this.blockStateWriter
				.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, identifier)).method_25775(method_25618()));
		}
	}

	private void method_25651(Block block, Block block2) {
		this.method_25641(block);
		Texture texture = Texture.paneAndTopForEdge(block, block2);
		Identifier identifier = Models.TEMPLATE_GLASS_PANE_POST.upload(block2, texture, this.modelWriter);
		Identifier identifier2 = Models.TEMPLATE_GLASS_PANE_SIDE.upload(block2, texture, this.modelWriter);
		Identifier identifier3 = Models.TEMPLATE_GLASS_PANE_SIDE_ALT.upload(block2, texture, this.modelWriter);
		Identifier identifier4 = Models.TEMPLATE_GLASS_PANE_NOSIDE.upload(block2, texture, this.modelWriter);
		Identifier identifier5 = Models.TEMPLATE_GLASS_PANE_NOSIDE_ALT.upload(block2, texture, this.modelWriter);
		Item item = block2.asItem();
		Models.GENERATED.upload(ModelIds.getItemModelId(item), Texture.layer0(block), this.modelWriter);
		this.blockStateWriter
			.accept(
				MultipartBlockStateSupplier.create(block2)
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

	private void registerCommandBlock(Block block) {
		Texture texture = Texture.sideFrontBack(block);
		Identifier identifier = Models.TEMPLATE_COMMAND_BLOCK.upload(block, texture, this.modelWriter);
		Identifier identifier2 = this.method_25557(
			block, "_conditional", Models.TEMPLATE_COMMAND_BLOCK, identifierx -> texture.copyAndAnd(TextureKey.SIDE, identifierx)
		);
		this.blockStateWriter
			.accept(VariantsBlockStateSupplier.create(block).method_25775(method_25565(Properties.CONDITIONAL, identifier2, identifier)).method_25775(method_25640()));
	}

	private void registerAnvil(Block block) {
		Identifier identifier = TexturedModel.TEMPLATE_ANVIL.upload(block, this.modelWriter);
		this.blockStateWriter.accept(method_25644(block, identifier).method_25775(method_25618()));
	}

	private List<BlockStateVariant> method_25535(int i) {
		String string = "_age" + i;
		return (List<BlockStateVariant>)IntStream.range(1, 5)
			.mapToObj(ix -> BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.BAMBOO, ix + string)))
			.collect(Collectors.toList());
	}

	private void method_25670() {
		this.method_25540(Blocks.BAMBOO);
		this.blockStateWriter
			.accept(
				MultipartBlockStateSupplier.create(Blocks.BAMBOO)
					.with(When.create().set(Properties.AGE_1, 0), this.method_25535(0))
					.with(When.create().set(Properties.AGE_1, 1), this.method_25535(1))
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

	private BlockStateVariantMap method_25675() {
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

	private void method_25643(Block block, TexturedModel.Factory factory) {
		Identifier identifier = factory.upload(block, this.modelWriter);
		this.blockStateWriter
			.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, identifier)).method_25775(this.method_25675()));
	}

	private void method_25680() {
		Identifier identifier = Texture.getSubModelId(Blocks.BARREL, "_top_open");
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.BARREL)
					.method_25775(this.method_25675())
					.method_25775(
						BlockStateVariantMap.create(Properties.OPEN)
							.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, TexturedModel.CUBE_BOTTOM_TOP.upload(Blocks.BARREL, this.modelWriter)))
							.register(
								true,
								BlockStateVariant.create()
									.put(
										VariantSettings.MODEL,
										TexturedModel.CUBE_BOTTOM_TOP
											.get(Blocks.BARREL)
											.texture(texture -> texture.put(TextureKey.TOP, identifier))
											.upload(Blocks.BARREL, "_open", this.modelWriter)
									)
							)
					)
			);
	}

	private static <T extends Comparable<T>> BlockStateVariantMap method_25566(Property<T> property, T comparable, Identifier identifier, Identifier identifier2) {
		BlockStateVariant blockStateVariant = BlockStateVariant.create().put(VariantSettings.MODEL, identifier);
		BlockStateVariant blockStateVariant2 = BlockStateVariant.create().put(VariantSettings.MODEL, identifier2);
		return BlockStateVariantMap.create(property).register(comparable2 -> {
			boolean bl = comparable2.compareTo(comparable) >= 0;
			return bl ? blockStateVariant : blockStateVariant2;
		});
	}

	private void method_25558(Block block, Function<Block, Texture> function) {
		Texture texture = ((Texture)function.apply(block)).inherit(TextureKey.SIDE, TextureKey.PARTICLE);
		Texture texture2 = texture.copyAndAnd(TextureKey.FRONT, Texture.getSubModelId(block, "_front_honey"));
		Identifier identifier = Models.ORIENTABLE_WITH_BOTTOM.upload(block, texture, this.modelWriter);
		Identifier identifier2 = Models.ORIENTABLE_WITH_BOTTOM.upload(block, "_honey", texture2, this.modelWriter);
		this.blockStateWriter
			.accept(VariantsBlockStateSupplier.create(block).method_25775(method_25599()).method_25775(method_25566(Properties.HONEY_LEVEL, 5, identifier2, identifier)));
	}

	private void method_25547(Block block, Property<Integer> property, int... is) {
		if (property.getValues().size() != is.length) {
			throw new IllegalArgumentException();
		} else {
			Int2ObjectMap<Identifier> int2ObjectMap = new Int2ObjectOpenHashMap<>();
			BlockStateVariantMap blockStateVariantMap = BlockStateVariantMap.create(property).register(integer -> {
				int i = is[integer];
				Identifier identifier = int2ObjectMap.computeIfAbsent(i, j -> this.method_25557(block, "_stage" + i, Models.CROP, Texture::crop));
				return BlockStateVariant.create().put(VariantSettings.MODEL, identifier);
			});
			this.method_25537(block.asItem());
			this.blockStateWriter.accept(VariantsBlockStateSupplier.create(block).method_25775(blockStateVariantMap));
		}
	}

	private void method_25684() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.BELL, "_floor");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.BELL, "_ceiling");
		Identifier identifier3 = ModelIds.getBlockSubModelId(Blocks.BELL, "_wall");
		Identifier identifier4 = ModelIds.getBlockSubModelId(Blocks.BELL, "_between_walls");
		this.method_25537(Items.BELL);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.BELL)
					.method_25775(
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

	private void method_25687() {
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.GRINDSTONE, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.GRINDSTONE)))
					.method_25775(
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

	private void method_25652(Block block, TexturedModel.Factory factory) {
		Identifier identifier = factory.upload(block, this.modelWriter);
		Identifier identifier2 = Texture.getSubModelId(block, "_front_on");
		Identifier identifier3 = factory.get(block).texture(texture -> texture.put(TextureKey.FRONT, identifier2)).upload(block, "_on", this.modelWriter);
		this.blockStateWriter
			.accept(VariantsBlockStateSupplier.create(block).method_25775(method_25565(Properties.LIT, identifier3, identifier)).method_25775(method_25599()));
	}

	private void method_25689() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.CAMPFIRE, "_off");
		Identifier identifier2 = ModelIds.getBlockModelId(Blocks.CAMPFIRE);
		this.method_25537(Items.CAMPFIRE);
		this.blockStateWriter
			.accept(VariantsBlockStateSupplier.create(Blocks.CAMPFIRE).method_25775(method_25565(Properties.LIT, identifier2, identifier)).method_25775(method_25618()));
	}

	private void method_25691() {
		Texture texture = Texture.sideEnd(Texture.getModelId(Blocks.BOOKSHELF), Texture.getModelId(Blocks.OAK_PLANKS));
		Identifier identifier = Models.CUBE_COLUMN.upload(Blocks.BOOKSHELF, texture, this.modelWriter);
		this.blockStateWriter.accept(method_25644(Blocks.BOOKSHELF, identifier));
	}

	private void method_25693() {
		this.method_25537(Items.REDSTONE);
		this.blockStateWriter
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
						When.anyOf(
							When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
							When.create()
								.set(Properties.NORTH_WIRE_CONNECTION, WireConnection.NONE)
								.set(Properties.EAST_WIRE_CONNECTION, WireConnection.NONE)
								.set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP)
								.set(Properties.WEST_WIRE_CONNECTION, WireConnection.NONE)
						),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side0"))
					)
					.with(
						When.anyOf(
							When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
							When.create()
								.set(Properties.NORTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP)
								.set(Properties.EAST_WIRE_CONNECTION, WireConnection.NONE)
								.set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.NONE)
								.set(Properties.WEST_WIRE_CONNECTION, WireConnection.NONE)
						),
						BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt0"))
					)
					.with(
						When.anyOf(
							When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
							When.create()
								.set(Properties.NORTH_WIRE_CONNECTION, WireConnection.NONE)
								.set(Properties.EAST_WIRE_CONNECTION, WireConnection.NONE)
								.set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.NONE)
								.set(Properties.WEST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP)
						),
						BlockStateVariant.create()
							.put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt1"))
							.put(VariantSettings.Y, VariantSettings.Rotation.R270)
					)
					.with(
						When.anyOf(
							When.create().set(Properties.WEST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
							When.create()
								.set(Properties.NORTH_WIRE_CONNECTION, WireConnection.NONE)
								.set(Properties.EAST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP)
								.set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.NONE)
								.set(Properties.WEST_WIRE_CONNECTION, WireConnection.NONE)
						),
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

	private void method_25695() {
		this.method_25537(Items.COMPARATOR);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.COMPARATOR)
					.method_25775(method_25618())
					.method_25775(
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

	private void method_25697() {
		Texture texture = Texture.all(Blocks.SMOOTH_STONE);
		Texture texture2 = Texture.sideEnd(Texture.getSubModelId(Blocks.SMOOTH_STONE_SLAB, "_side"), texture.getTexture(TextureKey.TOP));
		Identifier identifier = Models.SLAB.upload(Blocks.SMOOTH_STONE_SLAB, texture2, this.modelWriter);
		Identifier identifier2 = Models.SLAB_TOP.upload(Blocks.SMOOTH_STONE_SLAB, texture2, this.modelWriter);
		Identifier identifier3 = Models.CUBE_COLUMN.uploadWithoutVariant(Blocks.SMOOTH_STONE_SLAB, "_double", texture2, this.modelWriter);
		this.blockStateWriter.accept(method_25668(Blocks.SMOOTH_STONE_SLAB, identifier, identifier2, identifier3));
		this.blockStateWriter.accept(method_25644(Blocks.SMOOTH_STONE, Models.CUBE_ALL.upload(Blocks.SMOOTH_STONE, texture, this.modelWriter)));
	}

	private void method_25699() {
		this.method_25537(Items.BREWING_STAND);
		this.blockStateWriter
			.accept(
				MultipartBlockStateSupplier.create(Blocks.BREWING_STAND)
					.with(BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getModelId(Blocks.BREWING_STAND)))
					.with(
						When.create().set(Properties.HAS_BOTTLE_0, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.BREWING_STAND, "_bottle0"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_1, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.BREWING_STAND, "_bottle1"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_2, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.BREWING_STAND, "_bottle2"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_0, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.BREWING_STAND, "_empty0"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_1, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.BREWING_STAND, "_empty1"))
					)
					.with(
						When.create().set(Properties.HAS_BOTTLE_2, false),
						BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.BREWING_STAND, "_empty2"))
					)
			);
	}

	private void method_25694(Block block) {
		Identifier identifier = Models.TEMPLATE_SINGLE_FACE.upload(block, Texture.texture(block), this.modelWriter);
		Identifier identifier2 = ModelIds.getMinecraftNamespacedBlock("mushroom_block_inside");
		this.blockStateWriter
			.accept(
				MultipartBlockStateSupplier.create(block)
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
		this.method_25623(block, TexturedModel.CUBE_ALL.upload(block, "_inventory", this.modelWriter));
	}

	private void method_25701() {
		this.method_25537(Items.CAKE);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.CAKE)
					.method_25775(
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

	private void method_25703() {
		Texture texture = new Texture()
			.put(TextureKey.PARTICLE, Texture.getSubModelId(Blocks.CARTOGRAPHY_TABLE, "_side3"))
			.put(TextureKey.DOWN, Texture.getModelId(Blocks.DARK_OAK_PLANKS))
			.put(TextureKey.UP, Texture.getSubModelId(Blocks.CARTOGRAPHY_TABLE, "_top"))
			.put(TextureKey.NORTH, Texture.getSubModelId(Blocks.CARTOGRAPHY_TABLE, "_side3"))
			.put(TextureKey.EAST, Texture.getSubModelId(Blocks.CARTOGRAPHY_TABLE, "_side3"))
			.put(TextureKey.SOUTH, Texture.getSubModelId(Blocks.CARTOGRAPHY_TABLE, "_side1"))
			.put(TextureKey.WEST, Texture.getSubModelId(Blocks.CARTOGRAPHY_TABLE, "_side2"));
		this.blockStateWriter.accept(method_25644(Blocks.CARTOGRAPHY_TABLE, Models.CUBE.upload(Blocks.CARTOGRAPHY_TABLE, texture, this.modelWriter)));
	}

	private void method_25705() {
		Texture texture = new Texture()
			.put(TextureKey.PARTICLE, Texture.getSubModelId(Blocks.SMITHING_TABLE, "_front"))
			.put(TextureKey.DOWN, Texture.getSubModelId(Blocks.SMITHING_TABLE, "_bottom"))
			.put(TextureKey.UP, Texture.getSubModelId(Blocks.SMITHING_TABLE, "_top"))
			.put(TextureKey.NORTH, Texture.getSubModelId(Blocks.SMITHING_TABLE, "_front"))
			.put(TextureKey.SOUTH, Texture.getSubModelId(Blocks.SMITHING_TABLE, "_front"))
			.put(TextureKey.EAST, Texture.getSubModelId(Blocks.SMITHING_TABLE, "_side"))
			.put(TextureKey.WEST, Texture.getSubModelId(Blocks.SMITHING_TABLE, "_side"));
		this.blockStateWriter.accept(method_25644(Blocks.SMITHING_TABLE, Models.CUBE.upload(Blocks.SMITHING_TABLE, texture, this.modelWriter)));
	}

	private void method_25546(Block block, Block block2, BiFunction<Block, Block, Texture> biFunction) {
		Texture texture = (Texture)biFunction.apply(block, block2);
		this.blockStateWriter.accept(method_25644(block, Models.CUBE.upload(block, texture, this.modelWriter)));
	}

	private void method_25707() {
		Texture texture = Texture.sideEnd(Blocks.PUMPKIN);
		this.blockStateWriter.accept(method_25644(Blocks.PUMPKIN, ModelIds.getBlockModelId(Blocks.PUMPKIN)));
		this.method_25550(Blocks.CARVED_PUMPKIN, texture);
		this.method_25550(Blocks.JACK_O_LANTERN, texture);
	}

	private void method_25550(Block block, Texture texture) {
		Identifier identifier = Models.ORIENTABLE.upload(block, texture.copyAndAnd(TextureKey.FRONT, Texture.getModelId(block)), this.modelWriter);
		this.blockStateWriter
			.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, identifier)).method_25775(method_25599()));
	}

	private void method_25709() {
		this.method_25537(Items.CAULDRON);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.CAULDRON)
					.method_25775(
						BlockStateVariantMap.create(Properties.LEVEL_3)
							.register(0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.CAULDRON)))
							.register(1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAULDRON, "_level1")))
							.register(2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAULDRON, "_level2")))
							.register(3, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAULDRON, "_level3")))
					)
			);
	}

	private void method_25659(Block block, Block block2) {
		Texture texture = new Texture().put(TextureKey.END, Texture.getSubModelId(block2, "_top")).put(TextureKey.SIDE, Texture.getModelId(block));
		this.method_25551(block, texture, Models.CUBE_COLUMN);
	}

	private void method_25711() {
		Texture texture = Texture.texture(Blocks.CHORUS_FLOWER);
		Identifier identifier = Models.TEMPLATE_CHORUS_FLOWER.upload(Blocks.CHORUS_FLOWER, texture, this.modelWriter);
		Identifier identifier2 = this.method_25557(
			Blocks.CHORUS_FLOWER, "_dead", Models.TEMPLATE_CHORUS_FLOWER, identifierx -> texture.copyAndAnd(TextureKey.TEXTURE, identifierx)
		);
		this.blockStateWriter
			.accept(VariantsBlockStateSupplier.create(Blocks.CHORUS_FLOWER).method_25775(method_25566(Properties.AGE_5, 5, identifier2, identifier)));
	}

	private void method_25696(Block block) {
		Texture texture = new Texture()
			.put(TextureKey.TOP, Texture.getSubModelId(Blocks.FURNACE, "_top"))
			.put(TextureKey.SIDE, Texture.getSubModelId(Blocks.FURNACE, "_side"))
			.put(TextureKey.FRONT, Texture.getSubModelId(block, "_front"));
		Texture texture2 = new Texture()
			.put(TextureKey.SIDE, Texture.getSubModelId(Blocks.FURNACE, "_top"))
			.put(TextureKey.FRONT, Texture.getSubModelId(block, "_front_vertical"));
		Identifier identifier = Models.ORIENTABLE.upload(block, texture, this.modelWriter);
		Identifier identifier2 = Models.ORIENTABLE_VERTICAL.upload(block, texture2, this.modelWriter);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(block)
					.method_25775(
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

	private void method_25712() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.END_PORTAL_FRAME);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.END_PORTAL_FRAME, "_filled");
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.END_PORTAL_FRAME)
					.method_25775(
						BlockStateVariantMap.create(Properties.EYE)
							.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(true, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
					)
					.method_25775(method_25618())
			);
	}

	private void method_25508() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_side");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside");
		Identifier identifier3 = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside1");
		Identifier identifier4 = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside2");
		Identifier identifier5 = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside3");
		this.blockStateWriter
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

	private void method_25509() {
		this.blockStateWriter
			.accept(
				MultipartBlockStateSupplier.create(Blocks.COMPOSTER)
					.with(BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getModelId(Blocks.COMPOSTER)))
					.with(
						When.create().set(Properties.LEVEL_8, 1), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.COMPOSTER, "_contents1"))
					)
					.with(
						When.create().set(Properties.LEVEL_8, 2), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.COMPOSTER, "_contents2"))
					)
					.with(
						When.create().set(Properties.LEVEL_8, 3), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.COMPOSTER, "_contents3"))
					)
					.with(
						When.create().set(Properties.LEVEL_8, 4), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.COMPOSTER, "_contents4"))
					)
					.with(
						When.create().set(Properties.LEVEL_8, 5), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.COMPOSTER, "_contents5"))
					)
					.with(
						When.create().set(Properties.LEVEL_8, 6), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.COMPOSTER, "_contents6"))
					)
					.with(
						When.create().set(Properties.LEVEL_8, 7), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.COMPOSTER, "_contents7"))
					)
					.with(
						When.create().set(Properties.LEVEL_8, 8),
						BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.COMPOSTER, "_contents_ready"))
					)
			);
	}

	private void method_25698(Block block) {
		Texture texture = new Texture()
			.put(TextureKey.BOTTOM, Texture.getModelId(Blocks.NETHERRACK))
			.put(TextureKey.TOP, Texture.getModelId(block))
			.put(TextureKey.SIDE, Texture.getSubModelId(block, "_side"));
		this.blockStateWriter.accept(method_25644(block, Models.CUBE_BOTTOM_TOP.upload(block, texture, this.modelWriter)));
	}

	private void method_25510() {
		Identifier identifier = Texture.getSubModelId(Blocks.DAYLIGHT_DETECTOR, "_side");
		Texture texture = new Texture().put(TextureKey.TOP, Texture.getSubModelId(Blocks.DAYLIGHT_DETECTOR, "_top")).put(TextureKey.SIDE, identifier);
		Texture texture2 = new Texture().put(TextureKey.TOP, Texture.getSubModelId(Blocks.DAYLIGHT_DETECTOR, "_inverted_top")).put(TextureKey.SIDE, identifier);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.DAYLIGHT_DETECTOR)
					.method_25775(
						BlockStateVariantMap.create(Properties.INVERTED)
							.register(
								false,
								BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_DAYLIGHT_DETECTOR.upload(Blocks.DAYLIGHT_DETECTOR, texture, this.modelWriter))
							)
							.register(
								true,
								BlockStateVariant.create()
									.put(
										VariantSettings.MODEL,
										Models.TEMPLATE_DAYLIGHT_DETECTOR.upload(ModelIds.getBlockSubModelId(Blocks.DAYLIGHT_DETECTOR, "_inverted"), texture2, this.modelWriter)
									)
							)
					)
			);
	}

	private void method_25511() {
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.END_ROD, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.END_ROD)))
					.method_25775(this.method_25675())
			);
	}

	private void method_25512() {
		Texture texture = new Texture().put(TextureKey.DIRT, Texture.getModelId(Blocks.DIRT)).put(TextureKey.TOP, Texture.getModelId(Blocks.FARMLAND));
		Texture texture2 = new Texture().put(TextureKey.DIRT, Texture.getModelId(Blocks.DIRT)).put(TextureKey.TOP, Texture.getSubModelId(Blocks.FARMLAND, "_moist"));
		Identifier identifier = Models.TEMPLATE_FARMLAND.upload(Blocks.FARMLAND, texture, this.modelWriter);
		Identifier identifier2 = Models.TEMPLATE_FARMLAND.upload(Texture.getSubModelId(Blocks.FARMLAND, "_moist"), texture2, this.modelWriter);
		this.blockStateWriter.accept(VariantsBlockStateSupplier.create(Blocks.FARMLAND).method_25775(method_25566(Properties.MOISTURE, 7, identifier2, identifier)));
	}

	private List<Identifier> method_25700(Block block) {
		Identifier identifier = Models.TEMPLATE_FIRE_FLOOR.upload(ModelIds.getBlockSubModelId(block, "_floor0"), Texture.fire0(block), this.modelWriter);
		Identifier identifier2 = Models.TEMPLATE_FIRE_FLOOR.upload(ModelIds.getBlockSubModelId(block, "_floor1"), Texture.fire1(block), this.modelWriter);
		return ImmutableList.of(identifier, identifier2);
	}

	private List<Identifier> method_25702(Block block) {
		Identifier identifier = Models.TEMPLATE_FIRE_SIDE.upload(ModelIds.getBlockSubModelId(block, "_side0"), Texture.fire0(block), this.modelWriter);
		Identifier identifier2 = Models.TEMPLATE_FIRE_SIDE.upload(ModelIds.getBlockSubModelId(block, "_side1"), Texture.fire1(block), this.modelWriter);
		Identifier identifier3 = Models.TEMPLATE_FIRE_SIDE_ALT.upload(ModelIds.getBlockSubModelId(block, "_side_alt0"), Texture.fire0(block), this.modelWriter);
		Identifier identifier4 = Models.TEMPLATE_FIRE_SIDE_ALT.upload(ModelIds.getBlockSubModelId(block, "_side_alt1"), Texture.fire1(block), this.modelWriter);
		return ImmutableList.of(identifier, identifier2, identifier3, identifier4);
	}

	private List<Identifier> method_25704(Block block) {
		Identifier identifier = Models.TEMPLATE_FIRE_UP.upload(ModelIds.getBlockSubModelId(block, "_up0"), Texture.fire0(block), this.modelWriter);
		Identifier identifier2 = Models.TEMPLATE_FIRE_UP.upload(ModelIds.getBlockSubModelId(block, "_up1"), Texture.fire1(block), this.modelWriter);
		Identifier identifier3 = Models.TEMPLATE_FIRE_UP_ALT.upload(ModelIds.getBlockSubModelId(block, "_up_alt0"), Texture.fire0(block), this.modelWriter);
		Identifier identifier4 = Models.TEMPLATE_FIRE_UP_ALT.upload(ModelIds.getBlockSubModelId(block, "_up_alt1"), Texture.fire1(block), this.modelWriter);
		return ImmutableList.of(identifier, identifier2, identifier3, identifier4);
	}

	private static List<BlockStateVariant> method_25583(List<Identifier> list, UnaryOperator<BlockStateVariant> unaryOperator) {
		return (List<BlockStateVariant>)list.stream()
			.map(identifier -> BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
			.map(unaryOperator)
			.collect(Collectors.toList());
	}

	private void method_25513() {
		When when = When.create()
			.set(Properties.NORTH, false)
			.set(Properties.EAST, false)
			.set(Properties.SOUTH, false)
			.set(Properties.WEST, false)
			.set(Properties.UP, false);
		List<Identifier> list = this.method_25700(Blocks.FIRE);
		List<Identifier> list2 = this.method_25702(Blocks.FIRE);
		List<Identifier> list3 = this.method_25704(Blocks.FIRE);
		this.blockStateWriter
			.accept(
				MultipartBlockStateSupplier.create(Blocks.FIRE)
					.with(when, method_25583(list, blockStateVariant -> blockStateVariant))
					.with(When.anyOf(When.create().set(Properties.NORTH, true), when), method_25583(list2, blockStateVariant -> blockStateVariant))
					.with(
						When.anyOf(When.create().set(Properties.EAST, true), when),
						method_25583(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90))
					)
					.with(
						When.anyOf(When.create().set(Properties.SOUTH, true), when),
						method_25583(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180))
					)
					.with(
						When.anyOf(When.create().set(Properties.WEST, true), when),
						method_25583(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270))
					)
					.with(When.create().set(Properties.UP, true), method_25583(list3, blockStateVariant -> blockStateVariant))
			);
	}

	private void method_25514() {
		List<Identifier> list = this.method_25700(Blocks.SOUL_FIRE);
		List<Identifier> list2 = this.method_25702(Blocks.SOUL_FIRE);
		this.blockStateWriter
			.accept(
				MultipartBlockStateSupplier.create(Blocks.SOUL_FIRE)
					.with(method_25583(list, blockStateVariant -> blockStateVariant))
					.with(method_25583(list2, blockStateVariant -> blockStateVariant))
					.with(method_25583(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90)))
					.with(method_25583(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180)))
					.with(method_25583(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270)))
			);
	}

	private void method_25706(Block block) {
		Identifier identifier = TexturedModel.TEMPLATE_LANTERN.upload(block, this.modelWriter);
		Identifier identifier2 = TexturedModel.TEMPLATE_HANGING_LANTERN.upload(block, this.modelWriter);
		this.method_25537(block.asItem());
		this.blockStateWriter.accept(VariantsBlockStateSupplier.create(block).method_25775(method_25565(Properties.HANGING, identifier2, identifier)));
	}

	private void method_25515() {
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.FROSTED_ICE)
					.method_25775(
						BlockStateVariantMap.create(Properties.AGE_3)
							.register(0, BlockStateVariant.create().put(VariantSettings.MODEL, this.method_25557(Blocks.FROSTED_ICE, "_0", Models.CUBE_ALL, Texture::all)))
							.register(1, BlockStateVariant.create().put(VariantSettings.MODEL, this.method_25557(Blocks.FROSTED_ICE, "_1", Models.CUBE_ALL, Texture::all)))
							.register(2, BlockStateVariant.create().put(VariantSettings.MODEL, this.method_25557(Blocks.FROSTED_ICE, "_2", Models.CUBE_ALL, Texture::all)))
							.register(3, BlockStateVariant.create().put(VariantSettings.MODEL, this.method_25557(Blocks.FROSTED_ICE, "_3", Models.CUBE_ALL, Texture::all)))
					)
			);
	}

	private void method_25516() {
		Identifier identifier = Texture.getModelId(Blocks.DIRT);
		Texture texture = new Texture()
			.put(TextureKey.BOTTOM, identifier)
			.inherit(TextureKey.BOTTOM, TextureKey.PARTICLE)
			.put(TextureKey.TOP, Texture.getSubModelId(Blocks.GRASS_BLOCK, "_top"))
			.put(TextureKey.SIDE, Texture.getSubModelId(Blocks.GRASS_BLOCK, "_snow"));
		BlockStateVariant blockStateVariant = BlockStateVariant.create()
			.put(VariantSettings.MODEL, Models.CUBE_BOTTOM_TOP.upload(Blocks.GRASS_BLOCK, "_snow", texture, this.modelWriter));
		this.method_25560(Blocks.GRASS_BLOCK, ModelIds.getBlockModelId(Blocks.GRASS_BLOCK), blockStateVariant);
		Identifier identifier2 = TexturedModel.CUBE_BOTTOM_TOP
			.get(Blocks.MYCELIUM)
			.texture(texturex -> texturex.put(TextureKey.BOTTOM, identifier))
			.upload(Blocks.MYCELIUM, this.modelWriter);
		this.method_25560(Blocks.MYCELIUM, identifier2, blockStateVariant);
		Identifier identifier3 = TexturedModel.CUBE_BOTTOM_TOP
			.get(Blocks.PODZOL)
			.texture(texturex -> texturex.put(TextureKey.BOTTOM, identifier))
			.upload(Blocks.PODZOL, this.modelWriter);
		this.method_25560(Blocks.PODZOL, identifier3, blockStateVariant);
	}

	private void method_25560(Block block, Identifier identifier, BlockStateVariant blockStateVariant) {
		List<BlockStateVariant> list = Arrays.asList(method_25584(identifier));
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(block)
					.method_25775(BlockStateVariantMap.create(Properties.SNOWY).register(true, blockStateVariant).register(false, list))
			);
	}

	private void method_25517() {
		this.method_25537(Items.COCOA_BEANS);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.COCOA)
					.method_25775(
						BlockStateVariantMap.create(Properties.AGE_2)
							.register(0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage0")))
							.register(1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage1")))
							.register(2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage2")))
					)
					.method_25775(method_25618())
			);
	}

	private void method_25518() {
		this.blockStateWriter.accept(method_25634(Blocks.GRASS_PATH, ModelIds.getBlockModelId(Blocks.GRASS_PATH)));
	}

	private void method_25666(Block block, Block block2) {
		Texture texture = Texture.texture(block2);
		Identifier identifier = Models.PRESSURE_PLATE_UP.upload(block, texture, this.modelWriter);
		Identifier identifier2 = Models.PRESSURE_PLATE_DOWN.upload(block, texture, this.modelWriter);
		this.blockStateWriter.accept(VariantsBlockStateSupplier.create(block).method_25775(method_25566(Properties.POWER, 1, identifier2, identifier)));
	}

	private void method_25519() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.HOPPER);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.HOPPER, "_side");
		this.method_25537(Items.HOPPER);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.HOPPER)
					.method_25775(
						BlockStateVariantMap.create(Properties.HOPPER_FACING)
							.register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
							.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
							.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90))
							.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R180))
							.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R270))
					)
			);
	}

	private void method_25672(Block block, Block block2) {
		Identifier identifier = ModelIds.getBlockModelId(block);
		this.blockStateWriter.accept(VariantsBlockStateSupplier.create(block2, BlockStateVariant.create().put(VariantSettings.MODEL, identifier)));
		this.method_25623(block2, identifier);
	}

	private void method_25520() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_post_ends");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_post");
		Identifier identifier3 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_cap");
		Identifier identifier4 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_cap_alt");
		Identifier identifier5 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_side");
		Identifier identifier6 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_side_alt");
		this.blockStateWriter
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
		this.method_25600(Blocks.IRON_BARS);
	}

	private void method_25708(Block block) {
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(block)))
					.method_25775(method_25599())
			);
	}

	private void method_25521() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.LEVER);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.LEVER, "_on");
		this.method_25600(Blocks.LEVER);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.LEVER)
					.method_25775(method_25565(Properties.POWERED, identifier, identifier2))
					.method_25775(
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

	private void method_25522() {
		this.method_25600(Blocks.LILY_PAD);
		this.blockStateWriter.accept(method_25634(Blocks.LILY_PAD, ModelIds.getBlockModelId(Blocks.LILY_PAD)));
	}

	private void method_25523() {
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.NETHER_PORTAL)
					.method_25775(
						BlockStateVariantMap.create(Properties.HORIZONTAL_AXIS)
							.register(Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.NETHER_PORTAL, "_ns")))
							.register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.NETHER_PORTAL, "_ew")))
					)
			);
	}

	private void method_25524() {
		Identifier identifier = TexturedModel.CUBE_ALL.upload(Blocks.NETHERRACK, this.modelWriter);
		this.blockStateWriter
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

	private void method_25525() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.OBSERVER);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.OBSERVER, "_on");
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.OBSERVER).method_25775(method_25565(Properties.POWERED, identifier2, identifier)).method_25775(method_25640())
			);
	}

	private void method_25526() {
		Texture texture = new Texture()
			.put(TextureKey.BOTTOM, Texture.getSubModelId(Blocks.PISTON, "_bottom"))
			.put(TextureKey.SIDE, Texture.getSubModelId(Blocks.PISTON, "_side"));
		Identifier identifier = Texture.getSubModelId(Blocks.PISTON, "_top_sticky");
		Identifier identifier2 = Texture.getSubModelId(Blocks.PISTON, "_top");
		Texture texture2 = texture.copyAndAnd(TextureKey.PLATFORM, identifier);
		Texture texture3 = texture.copyAndAnd(TextureKey.PLATFORM, identifier2);
		Identifier identifier3 = ModelIds.getBlockSubModelId(Blocks.PISTON, "_base");
		this.method_25561(Blocks.PISTON, identifier3, texture3);
		this.method_25561(Blocks.STICKY_PISTON, identifier3, texture2);
		Identifier identifier4 = Models.CUBE_BOTTOM_TOP.upload(Blocks.PISTON, "_inventory", texture.copyAndAnd(TextureKey.TOP, identifier2), this.modelWriter);
		Identifier identifier5 = Models.CUBE_BOTTOM_TOP.upload(Blocks.STICKY_PISTON, "_inventory", texture.copyAndAnd(TextureKey.TOP, identifier), this.modelWriter);
		this.method_25623(Blocks.PISTON, identifier4);
		this.method_25623(Blocks.STICKY_PISTON, identifier5);
	}

	private void method_25561(Block block, Identifier identifier, Texture texture) {
		Identifier identifier2 = Models.TEMPLATE_PISTON.upload(block, texture, this.modelWriter);
		this.blockStateWriter
			.accept(VariantsBlockStateSupplier.create(block).method_25775(method_25565(Properties.EXTENDED, identifier, identifier2)).method_25775(method_25640()));
	}

	private void method_25527() {
		Texture texture = new Texture()
			.put(TextureKey.UNSTICKY, Texture.getSubModelId(Blocks.PISTON, "_top"))
			.put(TextureKey.SIDE, Texture.getSubModelId(Blocks.PISTON, "_side"));
		Texture texture2 = texture.copyAndAnd(TextureKey.PLATFORM, Texture.getSubModelId(Blocks.PISTON, "_top_sticky"));
		Texture texture3 = texture.copyAndAnd(TextureKey.PLATFORM, Texture.getSubModelId(Blocks.PISTON, "_top"));
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.PISTON_HEAD)
					.method_25775(
						BlockStateVariantMap.create(Properties.SHORT, Properties.PISTON_TYPE)
							.register(
								false,
								PistonType.DEFAULT,
								BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD.upload(Blocks.PISTON, "_head", texture3, this.modelWriter))
							)
							.register(
								false,
								PistonType.STICKY,
								BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD.upload(Blocks.PISTON, "_head_sticky", texture2, this.modelWriter))
							)
							.register(
								true,
								PistonType.DEFAULT,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD_SHORT.upload(Blocks.PISTON, "_head_short", texture3, this.modelWriter))
							)
							.register(
								true,
								PistonType.STICKY,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD_SHORT.upload(Blocks.PISTON, "_head_short_sticky", texture2, this.modelWriter))
							)
					)
					.method_25775(method_25640())
			);
	}

	private void method_25528() {
		Identifier identifier = ModelIds.getBlockSubModelId(Blocks.SCAFFOLDING, "_stable");
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.SCAFFOLDING, "_unstable");
		this.method_25623(Blocks.SCAFFOLDING, identifier);
		this.blockStateWriter.accept(VariantsBlockStateSupplier.create(Blocks.SCAFFOLDING).method_25775(method_25565(Properties.BOTTOM, identifier2, identifier)));
	}

	private void method_25529() {
		Identifier identifier = TexturedModel.CUBE_ALL.upload(Blocks.REDSTONE_LAMP, this.modelWriter);
		Identifier identifier2 = this.method_25557(Blocks.REDSTONE_LAMP, "_on", Models.CUBE_ALL, Texture::all);
		this.blockStateWriter.accept(VariantsBlockStateSupplier.create(Blocks.REDSTONE_LAMP).method_25775(method_25565(Properties.LIT, identifier2, identifier)));
	}

	private void method_25677(Block block, Block block2) {
		Texture texture = Texture.torch(block);
		this.blockStateWriter.accept(method_25644(block, Models.TEMPLATE_TORCH.upload(block, texture, this.modelWriter)));
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(
						block2, BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_TORCH_WALL.upload(block2, texture, this.modelWriter))
					)
					.method_25775(method_25630())
			);
		this.method_25600(block);
		this.method_25540(block2);
	}

	private void method_25530() {
		Texture texture = Texture.torch(Blocks.REDSTONE_TORCH);
		Texture texture2 = Texture.torch(Texture.getSubModelId(Blocks.REDSTONE_TORCH, "_off"));
		Identifier identifier = Models.TEMPLATE_TORCH.upload(Blocks.REDSTONE_TORCH, texture, this.modelWriter);
		Identifier identifier2 = Models.TEMPLATE_TORCH.upload(Blocks.REDSTONE_TORCH, "_off", texture2, this.modelWriter);
		this.blockStateWriter.accept(VariantsBlockStateSupplier.create(Blocks.REDSTONE_TORCH).method_25775(method_25565(Properties.LIT, identifier, identifier2)));
		Identifier identifier3 = Models.TEMPLATE_TORCH_WALL.upload(Blocks.REDSTONE_WALL_TORCH, texture, this.modelWriter);
		Identifier identifier4 = Models.TEMPLATE_TORCH_WALL.upload(Blocks.REDSTONE_WALL_TORCH, "_off", texture2, this.modelWriter);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.REDSTONE_WALL_TORCH)
					.method_25775(method_25565(Properties.LIT, identifier3, identifier4))
					.method_25775(method_25630())
			);
		this.method_25600(Blocks.REDSTONE_TORCH);
		this.method_25540(Blocks.REDSTONE_WALL_TORCH);
	}

	private void method_25531() {
		this.method_25537(Items.REPEATER);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.REPEATER)
					.method_25775(BlockStateVariantMap.create(Properties.DELAY, Properties.LOCKED, Properties.POWERED).register((integer, boolean_, boolean2) -> {
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append('_').append(integer).append("tick");
						if (boolean2) {
							stringBuilder.append("_on");
						}

						if (boolean_) {
							stringBuilder.append("_locked");
						}

						return BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.REPEATER, stringBuilder.toString()));
					}))
					.method_25775(method_25618())
			);
	}

	private void method_25532() {
		this.method_25537(Items.SEA_PICKLE);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.SEA_PICKLE)
					.method_25775(
						BlockStateVariantMap.create(Properties.PICKLES, Properties.WATERLOGGED)
							.register(1, false, Arrays.asList(method_25584(ModelIds.getMinecraftNamespacedBlock("dead_sea_pickle"))))
							.register(2, false, Arrays.asList(method_25584(ModelIds.getMinecraftNamespacedBlock("two_dead_sea_pickles"))))
							.register(3, false, Arrays.asList(method_25584(ModelIds.getMinecraftNamespacedBlock("three_dead_sea_pickles"))))
							.register(4, false, Arrays.asList(method_25584(ModelIds.getMinecraftNamespacedBlock("four_dead_sea_pickles"))))
							.register(1, true, Arrays.asList(method_25584(ModelIds.getMinecraftNamespacedBlock("sea_pickle"))))
							.register(2, true, Arrays.asList(method_25584(ModelIds.getMinecraftNamespacedBlock("two_sea_pickles"))))
							.register(3, true, Arrays.asList(method_25584(ModelIds.getMinecraftNamespacedBlock("three_sea_pickles"))))
							.register(4, true, Arrays.asList(method_25584(ModelIds.getMinecraftNamespacedBlock("four_sea_pickles"))))
					)
			);
	}

	private void method_25533() {
		Texture texture = Texture.all(Blocks.SNOW);
		Identifier identifier = Models.CUBE_ALL.upload(Blocks.SNOW_BLOCK, texture, this.modelWriter);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.SNOW)
					.method_25775(
						BlockStateVariantMap.create(Properties.LAYERS)
							.register(
								integer -> BlockStateVariant.create()
										.put(VariantSettings.MODEL, integer < 8 ? ModelIds.getBlockSubModelId(Blocks.SNOW, "_height" + integer * 2) : identifier)
							)
					)
			);
		this.method_25623(Blocks.SNOW, ModelIds.getBlockSubModelId(Blocks.SNOW, "_height2"));
		this.blockStateWriter.accept(method_25644(Blocks.SNOW_BLOCK, identifier));
	}

	private void method_25590() {
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.STONECUTTER, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.STONECUTTER)))
					.method_25775(method_25599())
			);
	}

	private void method_25591() {
		Identifier identifier = TexturedModel.CUBE_ALL.upload(Blocks.STRUCTURE_BLOCK, this.modelWriter);
		this.method_25623(Blocks.STRUCTURE_BLOCK, identifier);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.STRUCTURE_BLOCK)
					.method_25775(
						BlockStateVariantMap.create(Properties.STRUCTURE_BLOCK_MODE)
							.register(
								structureBlockMode -> BlockStateVariant.create()
										.put(VariantSettings.MODEL, this.method_25557(Blocks.STRUCTURE_BLOCK, "_" + structureBlockMode.asString(), Models.CUBE_ALL, Texture::all))
							)
					)
			);
	}

	private void method_25592() {
		this.method_25537(Items.SWEET_BERRIES);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.SWEET_BERRY_BUSH)
					.method_25775(
						BlockStateVariantMap.create(Properties.AGE_3)
							.register(
								integer -> BlockStateVariant.create()
										.put(VariantSettings.MODEL, this.method_25557(Blocks.SWEET_BERRY_BUSH, "_stage" + integer, Models.CROSS, Texture::cross))
							)
					)
			);
	}

	private void method_25593() {
		this.method_25537(Items.STRING);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.TRIPWIRE)
					.method_25775(
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

	private void method_25594() {
		this.method_25600(Blocks.TRIPWIRE_HOOK);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.TRIPWIRE_HOOK)
					.method_25775(
						BlockStateVariantMap.create(Properties.ATTACHED, Properties.POWERED)
							.register(
								(boolean_, boolean2) -> BlockStateVariant.create()
										.put(VariantSettings.MODEL, Texture.getSubModelId(Blocks.TRIPWIRE_HOOK, (boolean_ ? "_attached" : "") + (boolean2 ? "_on" : "")))
							)
					)
					.method_25775(method_25599())
			);
	}

	private Identifier method_25536(int i, String string, Texture texture) {
		switch (i) {
			case 1:
				return Models.TEMPLATE_TURTLE_EGG.upload(ModelIds.getMinecraftNamespacedBlock(string + "turtle_egg"), texture, this.modelWriter);
			case 2:
				return Models.TEMPLATE_TWO_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("two_" + string + "turtle_eggs"), texture, this.modelWriter);
			case 3:
				return Models.TEMPLATE_THREE_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("three_" + string + "turtle_eggs"), texture, this.modelWriter);
			case 4:
				return Models.TEMPLATE_FOUR_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("four_" + string + "turtle_eggs"), texture, this.modelWriter);
			default:
				throw new UnsupportedOperationException();
		}
	}

	private Identifier method_25581(Integer integer, Integer integer2) {
		switch (integer2) {
			case 0:
				return this.method_25536(integer, "", Texture.all(Texture.getModelId(Blocks.TURTLE_EGG)));
			case 1:
				return this.method_25536(integer, "slightly_cracked_", Texture.all(Texture.getSubModelId(Blocks.TURTLE_EGG, "_slightly_cracked")));
			case 2:
				return this.method_25536(integer, "very_cracked_", Texture.all(Texture.getSubModelId(Blocks.TURTLE_EGG, "_very_cracked")));
			default:
				throw new UnsupportedOperationException();
		}
	}

	private void method_25595() {
		this.method_25537(Items.TURTLE_EGG);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.TURTLE_EGG)
					.method_25775(
						BlockStateVariantMap.create(Properties.EGGS, Properties.HATCH)
							.registerVariants((integer, integer2) -> Arrays.asList(method_25584(this.method_25581(integer, integer2))))
					)
			);
	}

	private void method_25596() {
		this.method_25600(Blocks.VINE);
		this.blockStateWriter
			.accept(
				VariantsBlockStateSupplier.create(Blocks.VINE)
					.method_25775(
						BlockStateVariantMap.create(Properties.EAST, Properties.NORTH, Properties.SOUTH, Properties.UP, Properties.WEST)
							.register(false, false, false, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1")))
							.register(false, false, true, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1")))
							.register(
								false,
								false,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								false,
								true,
								false,
								false,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								true,
								false,
								false,
								false,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(true, true, false, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2")))
							.register(
								true,
								false,
								true,
								false,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								false,
								false,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								false,
								true,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(
								true, false, false, false, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2_opposite"))
							)
							.register(
								false,
								true,
								true,
								false,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2_opposite"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(true, true, true, false, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3")))
							.register(
								true,
								false,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								false,
								true,
								true,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								true,
								true,
								false,
								false,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(true, true, true, false, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_4")))
							.register(false, false, false, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_u")))
							.register(false, false, true, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1u")))
							.register(
								false,
								false,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								false,
								true,
								false,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								true,
								false,
								false,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(true, true, false, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2u")))
							.register(
								true,
								false,
								true,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								false,
								false,
								true,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								false,
								true,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(
								true, false, false, true, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2u_opposite"))
							)
							.register(
								false,
								true,
								true,
								true,
								false,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2u_opposite"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(true, true, true, true, false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3u")))
							.register(
								true,
								false,
								true,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R90)
							)
							.register(
								false,
								true,
								true,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R180)
							)
							.register(
								true,
								true,
								false,
								true,
								true,
								BlockStateVariant.create()
									.put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3u"))
									.put(VariantSettings.Y, VariantSettings.Rotation.R270)
							)
							.register(true, true, true, true, true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_4u")))
					)
			);
	}

	private void method_25597() {
		this.blockStateWriter
			.accept(
				method_25644(Blocks.MAGMA_BLOCK, Models.CUBE_ALL.upload(Blocks.MAGMA_BLOCK, Texture.all(ModelIds.getMinecraftNamespacedBlock("magma")), this.modelWriter))
			);
	}

	private void method_25710(Block block) {
		this.method_25622(block, TexturedModel.PARTICLE);
		Models.TEMPLATE_SHULKER_BOX.upload(ModelIds.getItemModelId(block.asItem()), Texture.particle(block), this.modelWriter);
	}

	private void method_25602(Block block, Block block2, BlockStateModelGenerator.class_4913 arg) {
		this.method_25603(block, arg);
		this.method_25603(block2, arg);
	}

	private void method_25682(Block block, Block block2) {
		Models.TEMPLATE_BED.upload(ModelIds.getItemModelId(block.asItem()), Texture.particle(block2), this.modelWriter);
	}

	private void method_25598() {
		Identifier identifier = ModelIds.getBlockModelId(Blocks.STONE);
		Identifier identifier2 = ModelIds.getBlockSubModelId(Blocks.STONE, "_mirrored");
		this.blockStateWriter.accept(method_25645(Blocks.INFESTED_STONE, identifier, identifier2));
		this.method_25623(Blocks.INFESTED_STONE, identifier);
	}

	private void method_25686(Block block, Block block2) {
		this.method_25548(block, BlockStateModelGenerator.class_4913.field_22840);
		Texture texture = Texture.plant(Texture.getSubModelId(block, "_pot"));
		Identifier identifier = BlockStateModelGenerator.class_4913.field_22840.method_25727().upload(block2, texture, this.modelWriter);
		this.blockStateWriter.accept(method_25644(block2, identifier));
	}

	public void method_25534() {
		this.registerSimpleState(Blocks.AIR);
		this.registerStateWithModelReference(Blocks.CAVE_AIR, Blocks.AIR);
		this.registerStateWithModelReference(Blocks.VOID_AIR, Blocks.AIR);
		this.registerSimpleState(Blocks.BEACON);
		this.registerSimpleState(Blocks.CACTUS);
		this.registerStateWithModelReference(Blocks.BUBBLE_COLUMN, Blocks.WATER);
		this.registerSimpleState(Blocks.DRAGON_EGG);
		this.registerSimpleState(Blocks.DRIED_KELP_BLOCK);
		this.registerSimpleState(Blocks.ENCHANTING_TABLE);
		this.registerSimpleState(Blocks.FLOWER_POT);
		this.method_25537(Items.FLOWER_POT);
		this.registerSimpleState(Blocks.HONEY_BLOCK);
		this.registerSimpleState(Blocks.WATER);
		this.registerSimpleState(Blocks.LAVA);
		this.registerSimpleState(Blocks.SLIME_BLOCK);
		this.registerSimpleState(Blocks.POTTED_BAMBOO);
		this.registerSimpleState(Blocks.POTTED_CACTUS);
		this.method_25542(Blocks.BARRIER, Items.BARRIER);
		this.method_25537(Items.BARRIER);
		this.method_25542(Blocks.STRUCTURE_VOID, Items.STRUCTURE_VOID);
		this.method_25537(Items.STRUCTURE_VOID);
		this.method_25660(Blocks.MOVING_PISTON, Texture.getSubModelId(Blocks.PISTON, "_side"));
		this.method_25622(Blocks.COAL_ORE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.COAL_BLOCK, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.DIAMOND_ORE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.DIAMOND_BLOCK, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.EMERALD_ORE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.EMERALD_BLOCK, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.GOLD_ORE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.GOLD_BLOCK, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.IRON_ORE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.IRON_BLOCK, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.ANCIENT_DEBRIS, TexturedModel.CUBE_COLUMN);
		this.method_25622(Blocks.NETHERITE_BLOCK, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.LAPIS_ORE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.LAPIS_BLOCK, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.NETHER_QUARTZ_ORE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.REDSTONE_ORE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.REDSTONE_BLOCK, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.BLUE_ICE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.CLAY, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.COARSE_DIRT, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.CRACKED_STONE_BRICKS, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.CRYING_OBSIDIAN, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.END_STONE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.GLOWSTONE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.GRAVEL, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.HONEYCOMB_BLOCK, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.ICE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.JUKEBOX, TexturedModel.CUBE_TOP);
		this.method_25622(Blocks.MELON, TexturedModel.CUBE_COLUMN);
		this.method_25622(Blocks.NETHER_WART_BLOCK, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.NOTE_BLOCK, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.PACKED_ICE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.OBSIDIAN, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.SEA_LANTERN, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.SHROOMLIGHT, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.SOUL_SAND, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.SOUL_SOIL, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.SPAWNER, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.SPONGE, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.SEAGRASS, TexturedModel.TEMPLATE_SEAGRASS);
		this.method_25537(Items.SEAGRASS);
		this.method_25622(Blocks.TNT, TexturedModel.CUBE_BOTTOM_TOP);
		this.method_25622(Blocks.TARGET, TexturedModel.CUBE_COLUMN);
		this.method_25622(Blocks.WARPED_WART_BLOCK, TexturedModel.CUBE_ALL);
		this.method_25622(Blocks.WET_SPONGE, TexturedModel.CUBE_ALL);
		this.method_25622(
			Blocks.CHISELED_QUARTZ_BLOCK,
			TexturedModel.CUBE_COLUMN.withTexture(texture -> texture.put(TextureKey.SIDE, Texture.getModelId(Blocks.CHISELED_QUARTZ_BLOCK)))
		);
		this.method_25622(Blocks.CHISELED_STONE_BRICKS, TexturedModel.CUBE_ALL);
		this.method_25659(Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE);
		this.method_25659(Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE);
		this.method_25666(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.GOLD_BLOCK);
		this.method_25666(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.IRON_BLOCK);
		this.method_25691();
		this.method_25699();
		this.method_25701();
		this.method_25689();
		this.method_25703();
		this.method_25709();
		this.method_25711();
		this.method_25508();
		this.method_25509();
		this.method_25510();
		this.method_25712();
		this.method_25511();
		this.method_25512();
		this.method_25513();
		this.method_25514();
		this.method_25515();
		this.method_25516();
		this.method_25517();
		this.method_25518();
		this.method_25687();
		this.method_25519();
		this.method_25520();
		this.method_25521();
		this.method_25522();
		this.method_25523();
		this.method_25524();
		this.method_25525();
		this.method_25526();
		this.method_25527();
		this.method_25528();
		this.method_25530();
		this.method_25529();
		this.method_25531();
		this.method_25532();
		this.method_25705();
		this.method_25533();
		this.method_25590();
		this.method_25591();
		this.method_25592();
		this.method_25593();
		this.method_25594();
		this.method_25595();
		this.method_25596();
		this.method_25597();
		this.method_25708(Blocks.LADDER);
		this.method_25600(Blocks.LADDER);
		this.method_25708(Blocks.LECTERN);
		this.method_25677(Blocks.TORCH, Blocks.WALL_TORCH);
		this.method_25677(Blocks.SOUL_FIRE_TORCH, Blocks.SOUL_FIRE_WALL_TORCH);
		this.method_25546(Blocks.CRAFTING_TABLE, Blocks.OAK_PLANKS, Texture::frontSideWithCustomBottom);
		this.method_25546(Blocks.FLETCHING_TABLE, Blocks.BIRCH_PLANKS, Texture::frontTopSide);
		this.method_25698(Blocks.CRIMSON_NYLIUM);
		this.method_25698(Blocks.WARPED_NYLIUM);
		this.method_25696(Blocks.DISPENSER);
		this.method_25696(Blocks.DROPPER);
		this.method_25706(Blocks.LANTERN);
		this.method_25706(Blocks.SOUL_FIRE_LANTERN);
		this.method_25553(Blocks.BASALT, TexturedModel.CUBE_COLUMN);
		this.method_25553(Blocks.BONE_BLOCK, TexturedModel.CUBE_COLUMN);
		this.method_25631(Blocks.DIRT);
		this.method_25631(Blocks.SAND);
		this.method_25631(Blocks.RED_SAND);
		this.method_25619(Blocks.BEDROCK);
		this.method_25554(Blocks.HAY_BLOCK, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
		this.method_25554(Blocks.PURPUR_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
		this.method_25554(Blocks.QUARTZ_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
		this.method_25643(Blocks.JIGSAW, TexturedModel.CUBE_BOTTOM_TOP);
		this.method_25605(Blocks.LOOM, TexturedModel.ORIENTABLE_WITH_BOTTOM);
		this.method_25707();
		this.method_25558(Blocks.BEE_NEST, Texture::sideFrontTopBottom);
		this.method_25558(Blocks.BEEHIVE, Texture::sideFrontEnd);
		this.method_25547(Blocks.BEETROOTS, Properties.AGE_3, 0, 1, 2, 3);
		this.method_25547(Blocks.CARROTS, Properties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
		this.method_25547(Blocks.NETHER_WART, Properties.AGE_3, 0, 1, 1, 2);
		this.method_25547(Blocks.POTATOES, Properties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
		this.method_25547(Blocks.WHEAT, Properties.AGE_7, 0, 1, 2, 3, 4, 5, 6, 7);
		this.method_25585(ModelIds.getMinecraftNamespacedBlock("banner"), Blocks.OAK_PLANKS)
			.method_25713(
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
			.method_25715(
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
		this.method_25585(ModelIds.getMinecraftNamespacedBlock("bed"), Blocks.OAK_PLANKS)
			.method_25715(
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
		this.method_25682(Blocks.WHITE_BED, Blocks.WHITE_WOOL);
		this.method_25682(Blocks.ORANGE_BED, Blocks.ORANGE_WOOL);
		this.method_25682(Blocks.MAGENTA_BED, Blocks.MAGENTA_WOOL);
		this.method_25682(Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
		this.method_25682(Blocks.YELLOW_BED, Blocks.YELLOW_WOOL);
		this.method_25682(Blocks.LIME_BED, Blocks.LIME_WOOL);
		this.method_25682(Blocks.PINK_BED, Blocks.PINK_WOOL);
		this.method_25682(Blocks.GRAY_BED, Blocks.GRAY_WOOL);
		this.method_25682(Blocks.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
		this.method_25682(Blocks.CYAN_BED, Blocks.CYAN_WOOL);
		this.method_25682(Blocks.PURPLE_BED, Blocks.PURPLE_WOOL);
		this.method_25682(Blocks.BLUE_BED, Blocks.BLUE_WOOL);
		this.method_25682(Blocks.BROWN_BED, Blocks.BROWN_WOOL);
		this.method_25682(Blocks.GREEN_BED, Blocks.GREEN_WOOL);
		this.method_25682(Blocks.RED_BED, Blocks.RED_WOOL);
		this.method_25682(Blocks.BLACK_BED, Blocks.BLACK_WOOL);
		this.method_25585(ModelIds.getMinecraftNamespacedBlock("skull"), Blocks.SOUL_SAND)
			.method_25713(Models.TEMPLATE_SKULL, Blocks.CREEPER_HEAD, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL)
			.method_25714(Blocks.DRAGON_HEAD)
			.method_25715(
				Blocks.CREEPER_WALL_HEAD,
				Blocks.DRAGON_WALL_HEAD,
				Blocks.PLAYER_WALL_HEAD,
				Blocks.ZOMBIE_WALL_HEAD,
				Blocks.SKELETON_WALL_SKULL,
				Blocks.WITHER_SKELETON_WALL_SKULL
			);
		this.method_25710(Blocks.SHULKER_BOX);
		this.method_25710(Blocks.WHITE_SHULKER_BOX);
		this.method_25710(Blocks.ORANGE_SHULKER_BOX);
		this.method_25710(Blocks.MAGENTA_SHULKER_BOX);
		this.method_25710(Blocks.LIGHT_BLUE_SHULKER_BOX);
		this.method_25710(Blocks.YELLOW_SHULKER_BOX);
		this.method_25710(Blocks.LIME_SHULKER_BOX);
		this.method_25710(Blocks.PINK_SHULKER_BOX);
		this.method_25710(Blocks.GRAY_SHULKER_BOX);
		this.method_25710(Blocks.LIGHT_GRAY_SHULKER_BOX);
		this.method_25710(Blocks.CYAN_SHULKER_BOX);
		this.method_25710(Blocks.PURPLE_SHULKER_BOX);
		this.method_25710(Blocks.BLUE_SHULKER_BOX);
		this.method_25710(Blocks.BROWN_SHULKER_BOX);
		this.method_25710(Blocks.GREEN_SHULKER_BOX);
		this.method_25710(Blocks.RED_SHULKER_BOX);
		this.method_25710(Blocks.BLACK_SHULKER_BOX);
		this.method_25622(Blocks.CONDUIT, TexturedModel.PARTICLE);
		this.method_25540(Blocks.CONDUIT);
		this.method_25585(ModelIds.getMinecraftNamespacedBlock("chest"), Blocks.OAK_PLANKS).method_25715(Blocks.CHEST, Blocks.TRAPPED_CHEST);
		this.method_25585(ModelIds.getMinecraftNamespacedBlock("ender_chest"), Blocks.OBSIDIAN).method_25715(Blocks.ENDER_CHEST);
		this.method_25632(Blocks.END_PORTAL, Blocks.OBSIDIAN).method_25714(Blocks.END_PORTAL, Blocks.END_GATEWAY);
		this.method_25641(Blocks.WHITE_CONCRETE);
		this.method_25641(Blocks.ORANGE_CONCRETE);
		this.method_25641(Blocks.MAGENTA_CONCRETE);
		this.method_25641(Blocks.LIGHT_BLUE_CONCRETE);
		this.method_25641(Blocks.YELLOW_CONCRETE);
		this.method_25641(Blocks.LIME_CONCRETE);
		this.method_25641(Blocks.PINK_CONCRETE);
		this.method_25641(Blocks.GRAY_CONCRETE);
		this.method_25641(Blocks.LIGHT_GRAY_CONCRETE);
		this.method_25641(Blocks.CYAN_CONCRETE);
		this.method_25641(Blocks.PURPLE_CONCRETE);
		this.method_25641(Blocks.BLUE_CONCRETE);
		this.method_25641(Blocks.BROWN_CONCRETE);
		this.method_25641(Blocks.GREEN_CONCRETE);
		this.method_25641(Blocks.RED_CONCRETE);
		this.method_25641(Blocks.BLACK_CONCRETE);
		this.method_25576(
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
		this.method_25641(Blocks.TERRACOTTA);
		this.method_25641(Blocks.WHITE_TERRACOTTA);
		this.method_25641(Blocks.ORANGE_TERRACOTTA);
		this.method_25641(Blocks.MAGENTA_TERRACOTTA);
		this.method_25641(Blocks.LIGHT_BLUE_TERRACOTTA);
		this.method_25641(Blocks.YELLOW_TERRACOTTA);
		this.method_25641(Blocks.LIME_TERRACOTTA);
		this.method_25641(Blocks.PINK_TERRACOTTA);
		this.method_25641(Blocks.GRAY_TERRACOTTA);
		this.method_25641(Blocks.LIGHT_GRAY_TERRACOTTA);
		this.method_25641(Blocks.CYAN_TERRACOTTA);
		this.method_25641(Blocks.PURPLE_TERRACOTTA);
		this.method_25641(Blocks.BLUE_TERRACOTTA);
		this.method_25641(Blocks.BROWN_TERRACOTTA);
		this.method_25641(Blocks.GREEN_TERRACOTTA);
		this.method_25641(Blocks.RED_TERRACOTTA);
		this.method_25641(Blocks.BLACK_TERRACOTTA);
		this.method_25651(Blocks.GLASS, Blocks.GLASS_PANE);
		this.method_25651(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE);
		this.method_25651(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE);
		this.method_25651(Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS_PANE);
		this.method_25651(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
		this.method_25651(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE);
		this.method_25651(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE);
		this.method_25651(Blocks.PINK_STAINED_GLASS, Blocks.PINK_STAINED_GLASS_PANE);
		this.method_25651(Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS_PANE);
		this.method_25651(Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
		this.method_25651(Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS_PANE);
		this.method_25651(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE);
		this.method_25651(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE);
		this.method_25651(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE);
		this.method_25651(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE);
		this.method_25651(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE);
		this.method_25651(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE);
		this.method_25614(
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
		this.method_25642(Blocks.WHITE_WOOL, Blocks.WHITE_CARPET);
		this.method_25642(Blocks.ORANGE_WOOL, Blocks.ORANGE_CARPET);
		this.method_25642(Blocks.MAGENTA_WOOL, Blocks.MAGENTA_CARPET);
		this.method_25642(Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_CARPET);
		this.method_25642(Blocks.YELLOW_WOOL, Blocks.YELLOW_CARPET);
		this.method_25642(Blocks.LIME_WOOL, Blocks.LIME_CARPET);
		this.method_25642(Blocks.PINK_WOOL, Blocks.PINK_CARPET);
		this.method_25642(Blocks.GRAY_WOOL, Blocks.GRAY_CARPET);
		this.method_25642(Blocks.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_CARPET);
		this.method_25642(Blocks.CYAN_WOOL, Blocks.CYAN_CARPET);
		this.method_25642(Blocks.PURPLE_WOOL, Blocks.PURPLE_CARPET);
		this.method_25642(Blocks.BLUE_WOOL, Blocks.BLUE_CARPET);
		this.method_25642(Blocks.BROWN_WOOL, Blocks.BROWN_CARPET);
		this.method_25642(Blocks.GREEN_WOOL, Blocks.GREEN_CARPET);
		this.method_25642(Blocks.RED_WOOL, Blocks.RED_CARPET);
		this.method_25642(Blocks.BLACK_WOOL, Blocks.BLACK_CARPET);
		this.method_25545(Blocks.FERN, Blocks.POTTED_FERN, BlockStateModelGenerator.class_4913.field_22839);
		this.method_25545(Blocks.DANDELION, Blocks.POTTED_DANDELION, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.POPPY, Blocks.POTTED_POPPY, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.BLUE_ORCHID, Blocks.POTTED_BLUE_ORCHID, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.ALLIUM, Blocks.POTTED_ALLIUM, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.AZURE_BLUET, Blocks.POTTED_AZURE_BLUET, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.RED_TULIP, Blocks.POTTED_RED_TULIP, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.ORANGE_TULIP, Blocks.POTTED_ORANGE_TULIP, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.WHITE_TULIP, Blocks.POTTED_WHITE_TULIP, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.PINK_TULIP, Blocks.POTTED_PINK_TULIP, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.OXEYE_DAISY, Blocks.POTTED_OXEYE_DAISY, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.CORNFLOWER, Blocks.POTTED_CORNFLOWER, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.LILY_OF_THE_VALLEY, Blocks.POTTED_LILY_OF_THE_VALLEY, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.WITHER_ROSE, Blocks.POTTED_WITHER_ROSE, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.BROWN_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25545(Blocks.DEAD_BUSH, Blocks.POTTED_DEAD_BUSH, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25694(Blocks.BROWN_MUSHROOM_BLOCK);
		this.method_25694(Blocks.RED_MUSHROOM_BLOCK);
		this.method_25694(Blocks.MUSHROOM_STEM);
		this.method_25548(Blocks.GRASS, BlockStateModelGenerator.class_4913.field_22839);
		this.method_25603(Blocks.SUGAR_CANE, BlockStateModelGenerator.class_4913.field_22839);
		this.method_25537(Items.SUGAR_CANE);
		this.method_25602(Blocks.KELP, Blocks.KELP_PLANT, BlockStateModelGenerator.class_4913.field_22839);
		this.method_25537(Items.KELP);
		this.method_25540(Blocks.KELP_PLANT);
		this.method_25602(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25556(Blocks.WEEPING_VINES, "_plant");
		this.method_25540(Blocks.WEEPING_VINES_PLANT);
		this.method_25549(Blocks.BAMBOO_SAPLING, BlockStateModelGenerator.class_4913.field_22839, Texture.cross(Texture.getSubModelId(Blocks.BAMBOO, "_stage0")));
		this.method_25670();
		this.method_25548(Blocks.COBWEB, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25621(Blocks.LILAC, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25621(Blocks.ROSE_BUSH, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25621(Blocks.PEONY, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25621(Blocks.TALL_GRASS, BlockStateModelGenerator.class_4913.field_22839);
		this.method_25621(Blocks.LARGE_FERN, BlockStateModelGenerator.class_4913.field_22839);
		this.method_25657();
		this.method_25664();
		this.method_25544(
			Blocks.TUBE_CORAL,
			Blocks.DEAD_TUBE_CORAL,
			Blocks.TUBE_CORAL_BLOCK,
			Blocks.DEAD_TUBE_CORAL_BLOCK,
			Blocks.TUBE_CORAL_FAN,
			Blocks.DEAD_TUBE_CORAL_FAN,
			Blocks.TUBE_CORAL_WALL_FAN,
			Blocks.DEAD_TUBE_CORAL_WALL_FAN
		);
		this.method_25544(
			Blocks.BRAIN_CORAL,
			Blocks.DEAD_BRAIN_CORAL,
			Blocks.BRAIN_CORAL_BLOCK,
			Blocks.DEAD_BRAIN_CORAL_BLOCK,
			Blocks.BRAIN_CORAL_FAN,
			Blocks.DEAD_BRAIN_CORAL_FAN,
			Blocks.BRAIN_CORAL_WALL_FAN,
			Blocks.DEAD_BRAIN_CORAL_WALL_FAN
		);
		this.method_25544(
			Blocks.BUBBLE_CORAL,
			Blocks.DEAD_BUBBLE_CORAL,
			Blocks.BUBBLE_CORAL_BLOCK,
			Blocks.DEAD_BUBBLE_CORAL_BLOCK,
			Blocks.BUBBLE_CORAL_FAN,
			Blocks.DEAD_BUBBLE_CORAL_FAN,
			Blocks.BUBBLE_CORAL_WALL_FAN,
			Blocks.DEAD_BUBBLE_CORAL_WALL_FAN
		);
		this.method_25544(
			Blocks.FIRE_CORAL,
			Blocks.DEAD_FIRE_CORAL,
			Blocks.FIRE_CORAL_BLOCK,
			Blocks.DEAD_FIRE_CORAL_BLOCK,
			Blocks.FIRE_CORAL_FAN,
			Blocks.DEAD_FIRE_CORAL_FAN,
			Blocks.FIRE_CORAL_WALL_FAN,
			Blocks.DEAD_FIRE_CORAL_WALL_FAN
		);
		this.method_25544(
			Blocks.HORN_CORAL,
			Blocks.DEAD_HORN_CORAL,
			Blocks.HORN_CORAL_BLOCK,
			Blocks.DEAD_HORN_CORAL_BLOCK,
			Blocks.HORN_CORAL_FAN,
			Blocks.DEAD_HORN_CORAL_FAN,
			Blocks.HORN_CORAL_WALL_FAN,
			Blocks.DEAD_HORN_CORAL_WALL_FAN
		);
		this.method_25620(Blocks.MELON_STEM, Blocks.ATTACHED_MELON_STEM);
		this.method_25620(Blocks.PUMPKIN_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
		this.method_25650(Blocks.ACACIA_PLANKS)
			.method_25716(Blocks.ACACIA_BUTTON)
			.method_25721(Blocks.ACACIA_FENCE)
			.method_25722(Blocks.ACACIA_FENCE_GATE)
			.method_25723(Blocks.ACACIA_PRESSURE_PLATE)
			.method_25717(Blocks.ACACIA_SIGN, Blocks.ACACIA_WALL_SIGN)
			.method_25724(Blocks.ACACIA_SLAB)
			.method_25725(Blocks.ACACIA_STAIRS);
		this.method_25658(Blocks.ACACIA_DOOR);
		this.method_25665(Blocks.ACACIA_TRAPDOOR);
		this.method_25676(Blocks.ACACIA_LOG).method_25730(Blocks.ACACIA_LOG).method_25728(Blocks.ACACIA_WOOD);
		this.method_25676(Blocks.STRIPPED_ACACIA_LOG).method_25730(Blocks.STRIPPED_ACACIA_LOG).method_25728(Blocks.STRIPPED_ACACIA_WOOD);
		this.method_25545(Blocks.ACACIA_SAPLING, Blocks.POTTED_ACACIA_SAPLING, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25622(Blocks.ACACIA_LEAVES, TexturedModel.LEAVES);
		this.method_25650(Blocks.BIRCH_PLANKS)
			.method_25716(Blocks.BIRCH_BUTTON)
			.method_25721(Blocks.BIRCH_FENCE)
			.method_25722(Blocks.BIRCH_FENCE_GATE)
			.method_25723(Blocks.BIRCH_PRESSURE_PLATE)
			.method_25717(Blocks.BIRCH_SIGN, Blocks.BIRCH_WALL_SIGN)
			.method_25724(Blocks.BIRCH_SLAB)
			.method_25725(Blocks.BIRCH_STAIRS);
		this.method_25658(Blocks.BIRCH_DOOR);
		this.method_25665(Blocks.BIRCH_TRAPDOOR);
		this.method_25676(Blocks.BIRCH_LOG).method_25730(Blocks.BIRCH_LOG).method_25728(Blocks.BIRCH_WOOD);
		this.method_25676(Blocks.STRIPPED_BIRCH_LOG).method_25730(Blocks.STRIPPED_BIRCH_LOG).method_25728(Blocks.STRIPPED_BIRCH_WOOD);
		this.method_25545(Blocks.BIRCH_SAPLING, Blocks.POTTED_BIRCH_SAPLING, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25622(Blocks.BIRCH_LEAVES, TexturedModel.LEAVES);
		this.method_25650(Blocks.OAK_PLANKS)
			.method_25716(Blocks.OAK_BUTTON)
			.method_25721(Blocks.OAK_FENCE)
			.method_25722(Blocks.OAK_FENCE_GATE)
			.method_25723(Blocks.OAK_PRESSURE_PLATE)
			.method_25717(Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN)
			.method_25724(Blocks.OAK_SLAB)
			.method_25724(Blocks.PETRIFIED_OAK_SLAB)
			.method_25725(Blocks.OAK_STAIRS);
		this.method_25658(Blocks.OAK_DOOR);
		this.method_25671(Blocks.OAK_TRAPDOOR);
		this.method_25676(Blocks.OAK_LOG).method_25730(Blocks.OAK_LOG).method_25728(Blocks.OAK_WOOD);
		this.method_25676(Blocks.STRIPPED_OAK_LOG).method_25730(Blocks.STRIPPED_OAK_LOG).method_25728(Blocks.STRIPPED_OAK_WOOD);
		this.method_25545(Blocks.OAK_SAPLING, Blocks.POTTED_OAK_SAPLING, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25622(Blocks.OAK_LEAVES, TexturedModel.LEAVES);
		this.method_25650(Blocks.SPRUCE_PLANKS)
			.method_25716(Blocks.SPRUCE_BUTTON)
			.method_25721(Blocks.SPRUCE_FENCE)
			.method_25722(Blocks.SPRUCE_FENCE_GATE)
			.method_25723(Blocks.SPRUCE_PRESSURE_PLATE)
			.method_25717(Blocks.SPRUCE_SIGN, Blocks.SPRUCE_WALL_SIGN)
			.method_25724(Blocks.SPRUCE_SLAB)
			.method_25725(Blocks.SPRUCE_STAIRS);
		this.method_25658(Blocks.SPRUCE_DOOR);
		this.method_25665(Blocks.SPRUCE_TRAPDOOR);
		this.method_25676(Blocks.SPRUCE_LOG).method_25730(Blocks.SPRUCE_LOG).method_25728(Blocks.SPRUCE_WOOD);
		this.method_25676(Blocks.STRIPPED_SPRUCE_LOG).method_25730(Blocks.STRIPPED_SPRUCE_LOG).method_25728(Blocks.STRIPPED_SPRUCE_WOOD);
		this.method_25545(Blocks.SPRUCE_SAPLING, Blocks.POTTED_SPRUCE_SAPLING, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25622(Blocks.SPRUCE_LEAVES, TexturedModel.LEAVES);
		this.method_25650(Blocks.DARK_OAK_PLANKS)
			.method_25716(Blocks.DARK_OAK_BUTTON)
			.method_25721(Blocks.DARK_OAK_FENCE)
			.method_25722(Blocks.DARK_OAK_FENCE_GATE)
			.method_25723(Blocks.DARK_OAK_PRESSURE_PLATE)
			.method_25717(Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_WALL_SIGN)
			.method_25724(Blocks.DARK_OAK_SLAB)
			.method_25725(Blocks.DARK_OAK_STAIRS);
		this.method_25658(Blocks.DARK_OAK_DOOR);
		this.method_25671(Blocks.DARK_OAK_TRAPDOOR);
		this.method_25676(Blocks.DARK_OAK_LOG).method_25730(Blocks.DARK_OAK_LOG).method_25728(Blocks.DARK_OAK_WOOD);
		this.method_25676(Blocks.STRIPPED_DARK_OAK_LOG).method_25730(Blocks.STRIPPED_DARK_OAK_LOG).method_25728(Blocks.STRIPPED_DARK_OAK_WOOD);
		this.method_25545(Blocks.DARK_OAK_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25622(Blocks.DARK_OAK_LEAVES, TexturedModel.LEAVES);
		this.method_25650(Blocks.JUNGLE_PLANKS)
			.method_25716(Blocks.JUNGLE_BUTTON)
			.method_25721(Blocks.JUNGLE_FENCE)
			.method_25722(Blocks.JUNGLE_FENCE_GATE)
			.method_25723(Blocks.JUNGLE_PRESSURE_PLATE)
			.method_25717(Blocks.JUNGLE_SIGN, Blocks.JUNGLE_WALL_SIGN)
			.method_25724(Blocks.JUNGLE_SLAB)
			.method_25725(Blocks.JUNGLE_STAIRS);
		this.method_25658(Blocks.JUNGLE_DOOR);
		this.method_25665(Blocks.JUNGLE_TRAPDOOR);
		this.method_25676(Blocks.JUNGLE_LOG).method_25730(Blocks.JUNGLE_LOG).method_25728(Blocks.JUNGLE_WOOD);
		this.method_25676(Blocks.STRIPPED_JUNGLE_LOG).method_25730(Blocks.STRIPPED_JUNGLE_LOG).method_25728(Blocks.STRIPPED_JUNGLE_WOOD);
		this.method_25545(Blocks.JUNGLE_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25622(Blocks.JUNGLE_LEAVES, TexturedModel.LEAVES);
		this.method_25650(Blocks.CRIMSON_PLANKS)
			.method_25716(Blocks.CRIMSON_BUTTON)
			.method_25721(Blocks.CRIMSON_FENCE)
			.method_25722(Blocks.CRIMSON_FENCE_GATE)
			.method_25723(Blocks.CRIMSON_PRESSURE_PLATE)
			.method_25717(Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN)
			.method_25724(Blocks.CRIMSON_SLAB)
			.method_25725(Blocks.CRIMSON_STAIRS);
		this.method_25658(Blocks.CRIMSON_DOOR);
		this.method_25665(Blocks.CRIMSON_TRAPDOOR);
		this.method_25676(Blocks.CRIMSON_STEM).method_25729(Blocks.CRIMSON_STEM).method_25728(Blocks.CRIMSON_HYPHAE);
		this.method_25676(Blocks.STRIPPED_CRIMSON_STEM).method_25729(Blocks.STRIPPED_CRIMSON_STEM).method_25728(Blocks.STRIPPED_CRIMSON_HYPHAE);
		this.method_25545(Blocks.CRIMSON_FUNGUS, Blocks.POTTED_CRIMSON_FUNGUS, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25686(Blocks.CRIMSON_ROOTS, Blocks.POTTED_CRIMSON_ROOTS);
		this.method_25650(Blocks.WARPED_PLANKS)
			.method_25716(Blocks.WARPED_BUTTON)
			.method_25721(Blocks.WARPED_FENCE)
			.method_25722(Blocks.WARPED_FENCE_GATE)
			.method_25723(Blocks.WARPED_PRESSURE_PLATE)
			.method_25717(Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN)
			.method_25724(Blocks.WARPED_SLAB)
			.method_25725(Blocks.WARPED_STAIRS);
		this.method_25658(Blocks.WARPED_DOOR);
		this.method_25665(Blocks.WARPED_TRAPDOOR);
		this.method_25676(Blocks.WARPED_STEM).method_25729(Blocks.WARPED_STEM).method_25728(Blocks.WARPED_HYPHAE);
		this.method_25676(Blocks.STRIPPED_WARPED_STEM).method_25729(Blocks.STRIPPED_WARPED_STEM).method_25728(Blocks.STRIPPED_WARPED_HYPHAE);
		this.method_25545(Blocks.WARPED_FUNGUS, Blocks.POTTED_WARPED_FUNGUS, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25686(Blocks.WARPED_ROOTS, Blocks.POTTED_WARPED_ROOTS);
		this.method_25548(Blocks.NETHER_SPROUTS, BlockStateModelGenerator.class_4913.field_22840);
		this.method_25574(Texture.all(Blocks.STONE)).method_25719(texture -> {
			Identifier identifier = Models.CUBE_ALL.upload(Blocks.STONE, texture, this.modelWriter);
			Identifier identifier2 = Models.CUBE_MIRRORED_ALL.upload(Blocks.STONE, texture, this.modelWriter);
			this.blockStateWriter.accept(method_25645(Blocks.STONE, identifier, identifier2));
			return identifier;
		}).method_25724(Blocks.STONE_SLAB).method_25723(Blocks.STONE_PRESSURE_PLATE).method_25716(Blocks.STONE_BUTTON).method_25725(Blocks.STONE_STAIRS);
		this.method_25658(Blocks.IRON_DOOR);
		this.method_25671(Blocks.IRON_TRAPDOOR);
		this.method_25650(Blocks.STONE_BRICKS).method_25720(Blocks.STONE_BRICK_WALL).method_25725(Blocks.STONE_BRICK_STAIRS).method_25724(Blocks.STONE_BRICK_SLAB);
		this.method_25650(Blocks.MOSSY_STONE_BRICKS)
			.method_25720(Blocks.MOSSY_STONE_BRICK_WALL)
			.method_25725(Blocks.MOSSY_STONE_BRICK_STAIRS)
			.method_25724(Blocks.MOSSY_STONE_BRICK_SLAB);
		this.method_25650(Blocks.COBBLESTONE).method_25720(Blocks.COBBLESTONE_WALL).method_25725(Blocks.COBBLESTONE_STAIRS).method_25724(Blocks.COBBLESTONE_SLAB);
		this.method_25650(Blocks.MOSSY_COBBLESTONE)
			.method_25720(Blocks.MOSSY_COBBLESTONE_WALL)
			.method_25725(Blocks.MOSSY_COBBLESTONE_STAIRS)
			.method_25724(Blocks.MOSSY_COBBLESTONE_SLAB);
		this.method_25650(Blocks.PRISMARINE).method_25720(Blocks.PRISMARINE_WALL).method_25725(Blocks.PRISMARINE_STAIRS).method_25724(Blocks.PRISMARINE_SLAB);
		this.method_25650(Blocks.PRISMARINE_BRICKS).method_25725(Blocks.PRISMARINE_BRICK_STAIRS).method_25724(Blocks.PRISMARINE_BRICK_SLAB);
		this.method_25650(Blocks.DARK_PRISMARINE).method_25725(Blocks.DARK_PRISMARINE_STAIRS).method_25724(Blocks.DARK_PRISMARINE_SLAB);
		this.method_25633(Blocks.SANDSTONE, TexturedModel.WALL_CUBE_BUTTOM_TOP)
			.method_25720(Blocks.SANDSTONE_WALL)
			.method_25725(Blocks.SANDSTONE_STAIRS)
			.method_25724(Blocks.SANDSTONE_SLAB);
		this.method_25555(Blocks.SMOOTH_SANDSTONE, TexturedModel.getCubeAll(Texture.getSubModelId(Blocks.SANDSTONE, "_top")))
			.method_25724(Blocks.SMOOTH_SANDSTONE_SLAB)
			.method_25725(Blocks.SMOOTH_SANDSTONE_STAIRS);
		this.method_25555(
				Blocks.CUT_SANDSTONE,
				TexturedModel.CUBE_COLUMN.get(Blocks.SANDSTONE).texture(texture -> texture.put(TextureKey.SIDE, Texture.getModelId(Blocks.CUT_SANDSTONE)))
			)
			.method_25724(Blocks.CUT_SANDSTONE_SLAB);
		this.method_25633(Blocks.RED_SANDSTONE, TexturedModel.WALL_CUBE_BUTTOM_TOP)
			.method_25720(Blocks.RED_SANDSTONE_WALL)
			.method_25725(Blocks.RED_SANDSTONE_STAIRS)
			.method_25724(Blocks.RED_SANDSTONE_SLAB);
		this.method_25555(Blocks.SMOOTH_RED_SANDSTONE, TexturedModel.getCubeAll(Texture.getSubModelId(Blocks.RED_SANDSTONE, "_top")))
			.method_25724(Blocks.SMOOTH_RED_SANDSTONE_SLAB)
			.method_25725(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
		this.method_25555(
				Blocks.CUT_RED_SANDSTONE,
				TexturedModel.CUBE_COLUMN.get(Blocks.RED_SANDSTONE).texture(texture -> texture.put(TextureKey.SIDE, Texture.getModelId(Blocks.CUT_RED_SANDSTONE)))
			)
			.method_25724(Blocks.CUT_RED_SANDSTONE_SLAB);
		this.method_25650(Blocks.BRICKS).method_25720(Blocks.BRICK_WALL).method_25725(Blocks.BRICK_STAIRS).method_25724(Blocks.BRICK_SLAB);
		this.method_25650(Blocks.NETHER_BRICKS)
			.method_25721(Blocks.NETHER_BRICK_FENCE)
			.method_25720(Blocks.NETHER_BRICK_WALL)
			.method_25725(Blocks.NETHER_BRICK_STAIRS)
			.method_25724(Blocks.NETHER_BRICK_SLAB);
		this.method_25650(Blocks.PURPUR_BLOCK).method_25725(Blocks.PURPUR_STAIRS).method_25724(Blocks.PURPUR_SLAB);
		this.method_25650(Blocks.DIORITE).method_25720(Blocks.DIORITE_WALL).method_25725(Blocks.DIORITE_STAIRS).method_25724(Blocks.DIORITE_SLAB);
		this.method_25650(Blocks.POLISHED_DIORITE).method_25725(Blocks.POLISHED_DIORITE_STAIRS).method_25724(Blocks.POLISHED_DIORITE_SLAB);
		this.method_25650(Blocks.GRANITE).method_25720(Blocks.GRANITE_WALL).method_25725(Blocks.GRANITE_STAIRS).method_25724(Blocks.GRANITE_SLAB);
		this.method_25650(Blocks.POLISHED_GRANITE).method_25725(Blocks.POLISHED_GRANITE_STAIRS).method_25724(Blocks.POLISHED_GRANITE_SLAB);
		this.method_25650(Blocks.ANDESITE).method_25720(Blocks.ANDESITE_WALL).method_25725(Blocks.ANDESITE_STAIRS).method_25724(Blocks.ANDESITE_SLAB);
		this.method_25650(Blocks.POLISHED_ANDESITE).method_25725(Blocks.POLISHED_ANDESITE_STAIRS).method_25724(Blocks.POLISHED_ANDESITE_SLAB);
		this.method_25650(Blocks.END_STONE_BRICKS)
			.method_25720(Blocks.END_STONE_BRICK_WALL)
			.method_25725(Blocks.END_STONE_BRICK_STAIRS)
			.method_25724(Blocks.END_STONE_BRICK_SLAB);
		this.method_25633(Blocks.QUARTZ_BLOCK, TexturedModel.CUBE_COLUMN).method_25725(Blocks.QUARTZ_STAIRS).method_25724(Blocks.QUARTZ_SLAB);
		this.method_25555(Blocks.SMOOTH_QUARTZ, TexturedModel.getCubeAll(Texture.getSubModelId(Blocks.QUARTZ_BLOCK, "_bottom")))
			.method_25725(Blocks.SMOOTH_QUARTZ_STAIRS)
			.method_25724(Blocks.SMOOTH_QUARTZ_SLAB);
		this.method_25650(Blocks.RED_NETHER_BRICKS)
			.method_25724(Blocks.RED_NETHER_BRICK_SLAB)
			.method_25725(Blocks.RED_NETHER_BRICK_STAIRS)
			.method_25720(Blocks.RED_NETHER_BRICK_WALL);
		this.method_25697();
		this.method_25685(Blocks.RAIL);
		this.method_25688(Blocks.POWERED_RAIL);
		this.method_25688(Blocks.DETECTOR_RAIL);
		this.method_25688(Blocks.ACTIVATOR_RAIL);
		this.method_25695();
		this.registerCommandBlock(Blocks.COMMAND_BLOCK);
		this.registerCommandBlock(Blocks.REPEATING_COMMAND_BLOCK);
		this.registerCommandBlock(Blocks.CHAIN_COMMAND_BLOCK);
		this.registerAnvil(Blocks.ANVIL);
		this.registerAnvil(Blocks.CHIPPED_ANVIL);
		this.registerAnvil(Blocks.DAMAGED_ANVIL);
		this.method_25680();
		this.method_25684();
		this.method_25652(Blocks.FURNACE, TexturedModel.ORIENTABLE);
		this.method_25652(Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE);
		this.method_25652(Blocks.SMOKER, TexturedModel.ORIENTABLE_WITH_BOTTOM);
		this.method_25693();
		this.method_25672(Blocks.CHISELED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS);
		this.method_25672(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE);
		this.method_25672(Blocks.CRACKED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
		this.method_25672(Blocks.MOSSY_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS);
		this.method_25598();
		this.method_25672(Blocks.STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS);
		SpawnEggItem.getAll().forEach(spawnEggItem -> this.method_25538(spawnEggItem, ModelIds.getMinecraftNamespacedItem("template_spawn_egg")));
	}

	class class_4911 {
		private final Identifier field_22835;

		public class_4911(Identifier identifier, Block block) {
			this.field_22835 = Models.PARTICLE.upload(identifier, Texture.particle(block), BlockStateModelGenerator.this.modelWriter);
		}

		public BlockStateModelGenerator.class_4911 method_25714(Block... blocks) {
			for (Block block : blocks) {
				BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25644(block, this.field_22835));
			}

			return this;
		}

		public BlockStateModelGenerator.class_4911 method_25715(Block... blocks) {
			for (Block block : blocks) {
				BlockStateModelGenerator.this.method_25540(block);
			}

			return this.method_25714(blocks);
		}

		public BlockStateModelGenerator.class_4911 method_25713(Model model, Block... blocks) {
			for (Block block : blocks) {
				model.upload(ModelIds.getItemModelId(block.asItem()), Texture.particle(block), BlockStateModelGenerator.this.modelWriter);
			}

			return this.method_25714(blocks);
		}
	}

	class class_4912 {
		private final Texture field_22837;
		@Nullable
		private Identifier field_22838;

		public class_4912(Texture texture) {
			this.field_22837 = texture;
		}

		public BlockStateModelGenerator.class_4912 method_25718(Block block, Model model) {
			this.field_22838 = model.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25644(block, this.field_22838));
			return this;
		}

		public BlockStateModelGenerator.class_4912 method_25719(Function<Texture, Identifier> function) {
			this.field_22838 = (Identifier)function.apply(this.field_22837);
			return this;
		}

		public BlockStateModelGenerator.class_4912 method_25716(Block block) {
			Identifier identifier = Models.BUTTON.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			Identifier identifier2 = Models.BUTTON_PRESSED.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25654(block, identifier, identifier2));
			Identifier identifier3 = Models.BUTTON_INVENTORY.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.method_25623(block, identifier3);
			return this;
		}

		public BlockStateModelGenerator.class_4912 method_25720(Block block) {
			Identifier identifier = Models.TEMPLATE_WALL_POST.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			Identifier identifier2 = Models.TEMPLATE_WALL_SIDE.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			Identifier identifier3 = Models.TEMPLATE_WALL_SIDE_TALL.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25636(block, identifier, identifier2, identifier3));
			Identifier identifier4 = Models.WALL_INVENTORY.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.method_25623(block, identifier4);
			return this;
		}

		public BlockStateModelGenerator.class_4912 method_25721(Block block) {
			Identifier identifier = Models.FENCE_POST.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			Identifier identifier2 = Models.FENCE_SIDE.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25661(block, identifier, identifier2));
			Identifier identifier3 = Models.FENCE_INVENTORY.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.method_25623(block, identifier3);
			return this;
		}

		public BlockStateModelGenerator.class_4912 method_25722(Block block) {
			Identifier identifier = Models.TEMPLATE_FENCE_GATE_OPEN.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			Identifier identifier2 = Models.TEMPLATE_FENCE_GATE.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			Identifier identifier3 = Models.TEMPLATE_FENCE_GATE_WALL_OPEN.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			Identifier identifier4 = Models.TEMPLATE_FENCE_GATE_WALL.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25626(block, identifier, identifier2, identifier3, identifier4));
			return this;
		}

		public BlockStateModelGenerator.class_4912 method_25723(Block block) {
			Identifier identifier = Models.PRESSURE_PLATE_UP.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			Identifier identifier2 = Models.PRESSURE_PLATE_DOWN.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25673(block, identifier, identifier2));
			return this;
		}

		public BlockStateModelGenerator.class_4912 method_25717(Block block, Block block2) {
			Identifier identifier = Models.PARTICLE.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25644(block, identifier));
			BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25644(block2, identifier));
			BlockStateModelGenerator.this.method_25537(block.asItem());
			BlockStateModelGenerator.this.method_25540(block2);
			return this;
		}

		public BlockStateModelGenerator.class_4912 method_25724(Block block) {
			if (this.field_22838 == null) {
				throw new IllegalStateException("Full block not generated yet");
			} else {
				Identifier identifier = Models.SLAB.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
				Identifier identifier2 = Models.SLAB_TOP.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
				BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25668(block, identifier, identifier2, this.field_22838));
				return this;
			}
		}

		public BlockStateModelGenerator.class_4912 method_25725(Block block) {
			Identifier identifier = Models.INNER_STAIRS.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			Identifier identifier2 = Models.STAIRS.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			Identifier identifier3 = Models.OUTER_STAIRS.upload(block, this.field_22837, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25646(block, identifier, identifier2, identifier3));
			return this;
		}
	}

	static enum class_4913 {
		field_22839,
		field_22840;

		public Model method_25726() {
			return this == field_22839 ? Models.TINTED_CROSS : Models.CROSS;
		}

		public Model method_25727() {
			return this == field_22839 ? Models.TINTED_FLOWER_POT_CROSS : Models.FLOWER_POT_CROSS;
		}
	}

	class class_4914 {
		private final Texture field_22843;

		public class_4914(Texture texture) {
			this.field_22843 = texture;
		}

		public BlockStateModelGenerator.class_4914 method_25728(Block block) {
			Texture texture = this.field_22843.copyAndAnd(TextureKey.END, this.field_22843.getTexture(TextureKey.SIDE));
			Identifier identifier = Models.CUBE_COLUMN.upload(block, texture, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25653(block, identifier));
			return this;
		}

		public BlockStateModelGenerator.class_4914 method_25729(Block block) {
			Identifier identifier = Models.CUBE_COLUMN.upload(block, this.field_22843, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25653(block, identifier));
			return this;
		}

		public BlockStateModelGenerator.class_4914 method_25730(Block block) {
			Identifier identifier = Models.CUBE_COLUMN.upload(block, this.field_22843, BlockStateModelGenerator.this.modelWriter);
			Identifier identifier2 = Models.CUBE_COLUMN_HORIZONTAL.upload(block, this.field_22843, BlockStateModelGenerator.this.modelWriter);
			BlockStateModelGenerator.this.blockStateWriter.accept(BlockStateModelGenerator.method_25667(block, identifier, identifier2));
			return this;
		}
	}
}
