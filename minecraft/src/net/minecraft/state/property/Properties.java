package net.minecraft.state.property;

import java.util.function.Predicate;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.util.math.Direction;

public class Properties {
	public static final BooleanProperty field_12493 = BooleanProperty.of("attached");
	public static final BooleanProperty field_16562 = BooleanProperty.of("bottom");
	public static final BooleanProperty field_12486 = BooleanProperty.of("conditional");
	public static final BooleanProperty field_12553 = BooleanProperty.of("disarmed");
	public static final BooleanProperty field_12526 = BooleanProperty.of("drag");
	public static final BooleanProperty field_12515 = BooleanProperty.of("enabled");
	public static final BooleanProperty field_12552 = BooleanProperty.of("extended");
	public static final BooleanProperty field_12488 = BooleanProperty.of("eye");
	public static final BooleanProperty field_12480 = BooleanProperty.of("falling");
	public static final BooleanProperty field_16561 = BooleanProperty.of("hanging");
	public static final BooleanProperty field_12554 = BooleanProperty.of("has_bottle_0");
	public static final BooleanProperty field_12500 = BooleanProperty.of("has_bottle_1");
	public static final BooleanProperty field_12531 = BooleanProperty.of("has_bottle_2");
	public static final BooleanProperty field_12544 = BooleanProperty.of("has_record");
	public static final BooleanProperty field_17393 = BooleanProperty.of("has_book");
	public static final BooleanProperty field_12501 = BooleanProperty.of("inverted");
	public static final BooleanProperty field_12491 = BooleanProperty.of("in_wall");
	public static final BooleanProperty field_12548 = BooleanProperty.of("lit");
	public static final BooleanProperty field_12502 = BooleanProperty.of("locked");
	public static final BooleanProperty field_12528 = BooleanProperty.of("occupied");
	public static final BooleanProperty field_12537 = BooleanProperty.of("open");
	public static final BooleanProperty field_12514 = BooleanProperty.of("persistent");
	public static final BooleanProperty field_12484 = BooleanProperty.of("powered");
	public static final BooleanProperty field_12535 = BooleanProperty.of("short");
	public static final BooleanProperty field_17394 = BooleanProperty.of("signal_fire");
	public static final BooleanProperty field_12512 = BooleanProperty.of("snowy");
	public static final BooleanProperty field_12522 = BooleanProperty.of("triggered");
	public static final BooleanProperty field_12539 = BooleanProperty.of("unstable");
	public static final BooleanProperty field_12508 = BooleanProperty.of("waterlogged");
	public static final EnumProperty<Direction.Axis> field_12529 = EnumProperty.of("axis", Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z);
	public static final EnumProperty<Direction.Axis> field_12496 = EnumProperty.of("axis", Direction.Axis.class);
	public static final BooleanProperty field_12519 = BooleanProperty.of("up");
	public static final BooleanProperty field_12546 = BooleanProperty.of("down");
	public static final BooleanProperty field_12489 = BooleanProperty.of("north");
	public static final BooleanProperty field_12487 = BooleanProperty.of("east");
	public static final BooleanProperty field_12540 = BooleanProperty.of("south");
	public static final BooleanProperty field_12527 = BooleanProperty.of("west");
	public static final DirectionProperty field_12525 = DirectionProperty.of(
		"facing", Direction.field_11043, Direction.field_11034, Direction.field_11035, Direction.field_11039, Direction.field_11036, Direction.field_11033
	);
	public static final DirectionProperty field_12545 = DirectionProperty.of("facing", (Predicate<Direction>)(direction -> direction != Direction.field_11036));
	public static final DirectionProperty field_12481 = DirectionProperty.of("facing", Direction.Type.field_11062);
	public static final EnumProperty<WallMountLocation> field_12555 = EnumProperty.of("face", WallMountLocation.class);
	public static final EnumProperty<Attachment> field_17104 = EnumProperty.of("attachment", Attachment.class);
	public static final EnumProperty<WireConnection> field_12523 = EnumProperty.of("east", WireConnection.class);
	public static final EnumProperty<WireConnection> field_12495 = EnumProperty.of("north", WireConnection.class);
	public static final EnumProperty<WireConnection> field_12551 = EnumProperty.of("south", WireConnection.class);
	public static final EnumProperty<WireConnection> field_12504 = EnumProperty.of("west", WireConnection.class);
	public static final EnumProperty<DoubleBlockHalf> field_12533 = EnumProperty.of("half", DoubleBlockHalf.class);
	public static final EnumProperty<BlockHalf> field_12518 = EnumProperty.of("half", BlockHalf.class);
	public static final EnumProperty<RailShape> field_12507 = EnumProperty.of("shape", RailShape.class);
	public static final EnumProperty<RailShape> field_12542 = EnumProperty.of(
		"shape",
		RailShape.class,
		(Predicate)(railShape -> railShape != RailShape.field_12663
				&& railShape != RailShape.field_12672
				&& railShape != RailShape.field_12664
				&& railShape != RailShape.field_12671)
	);
	public static final IntProperty field_12521 = IntProperty.of("age", 0, 1);
	public static final IntProperty field_12556 = IntProperty.of("age", 0, 2);
	public static final IntProperty field_12497 = IntProperty.of("age", 0, 3);
	public static final IntProperty field_12482 = IntProperty.of("age", 0, 5);
	public static final IntProperty field_12550 = IntProperty.of("age", 0, 7);
	public static final IntProperty field_12498 = IntProperty.of("age", 0, 15);
	public static final IntProperty field_12517 = IntProperty.of("age", 0, 25);
	public static final IntProperty field_12505 = IntProperty.of("bites", 0, 6);
	public static final IntProperty field_12494 = IntProperty.of("delay", 1, 4);
	public static final IntProperty field_12541 = IntProperty.of("distance", 1, 7);
	public static final IntProperty field_12509 = IntProperty.of("eggs", 1, 4);
	public static final IntProperty field_12530 = IntProperty.of("hatch", 0, 2);
	public static final IntProperty field_12536 = IntProperty.of("layers", 1, 8);
	public static final IntProperty field_12513 = IntProperty.of("level", 0, 3);
	public static final IntProperty field_17586 = IntProperty.of("level", 0, 8);
	public static final IntProperty field_12490 = IntProperty.of("level", 1, 8);
	public static final IntProperty field_12538 = IntProperty.of("level", 0, 15);
	public static final IntProperty field_12510 = IntProperty.of("moisture", 0, 7);
	public static final IntProperty field_12524 = IntProperty.of("note", 0, 24);
	public static final IntProperty field_12543 = IntProperty.of("pickles", 1, 4);
	public static final IntProperty field_12511 = IntProperty.of("power", 0, 15);
	public static final IntProperty field_12549 = IntProperty.of("stage", 0, 1);
	public static final IntProperty field_16503 = IntProperty.of("distance", 0, 7);
	public static final IntProperty field_12532 = IntProperty.of("rotation", 0, 15);
	public static final EnumProperty<BedPart> field_12483 = EnumProperty.of("part", BedPart.class);
	public static final EnumProperty<ChestType> field_12506 = EnumProperty.of("type", ChestType.class);
	public static final EnumProperty<ComparatorMode> field_12534 = EnumProperty.of("mode", ComparatorMode.class);
	public static final EnumProperty<DoorHinge> field_12520 = EnumProperty.of("hinge", DoorHinge.class);
	public static final EnumProperty<Instrument> field_12499 = EnumProperty.of("instrument", Instrument.class);
	public static final EnumProperty<PistonType> field_12492 = EnumProperty.of("type", PistonType.class);
	public static final EnumProperty<SlabType> field_12485 = EnumProperty.of("type", SlabType.class);
	public static final EnumProperty<StairShape> field_12503 = EnumProperty.of("shape", StairShape.class);
	public static final EnumProperty<StructureBlockMode> field_12547 = EnumProperty.of("mode", StructureBlockMode.class);
	public static final EnumProperty<BambooLeaves> field_12516 = EnumProperty.of("leaves", BambooLeaves.class);
}
