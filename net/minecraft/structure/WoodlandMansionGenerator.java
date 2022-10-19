/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;

/**
 * The generator for the woodland mansion structure.
 * 
 * <p>The cobblestones underneath the mansion are generated {@linkplain
 * net.minecraft.world.gen.structure.WoodlandMansionStructure#postPlace after
 * the mansion placement}.
 */
public class WoodlandMansionGenerator {
    public static void addPieces(StructureTemplateManager manager, BlockPos pos, BlockRotation rotation, List<Piece> pieces, Random random) {
        MansionParameters mansionParameters = new MansionParameters(random);
        LayoutGenerator layoutGenerator = new LayoutGenerator(manager, random);
        layoutGenerator.generate(pos, rotation, pieces, mansionParameters);
    }

    public static void printRandomFloorLayouts(String[] args) {
        Random random = Random.create();
        long l = random.nextLong();
        System.out.println("Seed: " + l);
        random.setSeed(l);
        MansionParameters mansionParameters = new MansionParameters(random);
        mansionParameters.printFloorLayouts();
    }

    static class MansionParameters {
        private static final int SIZE = 11;
        private static final int UNSET = 0;
        private static final int CORRIDOR = 1;
        private static final int ROOM = 2;
        private static final int STAIRCASE = 3;
        private static final int UNUSED = 4;
        private static final int OUTSIDE = 5;
        private static final int SMALL_ROOM_FLAG = 65536;
        private static final int MEDIUM_ROOM_FLAG = 131072;
        private static final int BIG_ROOM_FLAG = 262144;
        private static final int ORIGIN_CELL_FLAG = 0x100000;
        private static final int ENTRANCE_CELL_FLAG = 0x200000;
        private static final int STAIRCASE_CELL_FLAG = 0x400000;
        private static final int CARPET_CELL_FLAG = 0x800000;
        private static final int ROOM_SIZE_MASK = 983040;
        private static final int ROOM_ID_MASK = 65535;
        private final Random random;
        final FlagMatrix baseLayout;
        final FlagMatrix thirdFloorLayout;
        final FlagMatrix[] roomFlagsByFloor;
        final int entranceI;
        final int entranceJ;

        public MansionParameters(Random random) {
            this.random = random;
            int i = 11;
            this.entranceI = 7;
            this.entranceJ = 4;
            this.baseLayout = new FlagMatrix(SIZE, SIZE, OUTSIDE);
            this.baseLayout.fill(this.entranceI, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, STAIRCASE);
            this.baseLayout.fill(this.entranceI - 1, this.entranceJ, this.entranceI - 1, this.entranceJ + 1, ROOM);
            this.baseLayout.fill(this.entranceI + 2, this.entranceJ - 2, this.entranceI + 3, this.entranceJ + 3, OUTSIDE);
            this.baseLayout.fill(this.entranceI + 1, this.entranceJ - 2, this.entranceI + 1, this.entranceJ - 1, CORRIDOR);
            this.baseLayout.fill(this.entranceI + 1, this.entranceJ + 2, this.entranceI + 1, this.entranceJ + 3, CORRIDOR);
            this.baseLayout.set(this.entranceI - 1, this.entranceJ - 1, CORRIDOR);
            this.baseLayout.set(this.entranceI - 1, this.entranceJ + 2, CORRIDOR);
            this.baseLayout.fill(0, 0, 11, 1, OUTSIDE);
            this.baseLayout.fill(0, 9, 11, 11, OUTSIDE);
            this.layoutCorridor(this.baseLayout, this.entranceI, this.entranceJ - 2, Direction.WEST, 6);
            this.layoutCorridor(this.baseLayout, this.entranceI, this.entranceJ + 3, Direction.WEST, 6);
            this.layoutCorridor(this.baseLayout, this.entranceI - 2, this.entranceJ - 1, Direction.WEST, 3);
            this.layoutCorridor(this.baseLayout, this.entranceI - 2, this.entranceJ + 2, Direction.WEST, 3);
            while (this.adjustLayoutWithRooms(this.baseLayout)) {
            }
            this.roomFlagsByFloor = new FlagMatrix[3];
            this.roomFlagsByFloor[0] = new FlagMatrix(SIZE, SIZE, OUTSIDE);
            this.roomFlagsByFloor[1] = new FlagMatrix(SIZE, SIZE, OUTSIDE);
            this.roomFlagsByFloor[2] = new FlagMatrix(SIZE, SIZE, OUTSIDE);
            this.updateRoomFlags(this.baseLayout, this.roomFlagsByFloor[0]);
            this.updateRoomFlags(this.baseLayout, this.roomFlagsByFloor[1]);
            this.roomFlagsByFloor[0].fill(this.entranceI + 1, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, 0x800000);
            this.roomFlagsByFloor[1].fill(this.entranceI + 1, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, 0x800000);
            this.thirdFloorLayout = new FlagMatrix(this.baseLayout.n, this.baseLayout.m, OUTSIDE);
            this.layoutThirdFloor();
            this.updateRoomFlags(this.thirdFloorLayout, this.roomFlagsByFloor[2]);
        }

        public static boolean isInsideMansion(FlagMatrix layout, int i, int j) {
            int k = layout.get(i, j);
            return k == CORRIDOR || k == ROOM || k == STAIRCASE || k == UNUSED;
        }

        public boolean isRoomId(FlagMatrix layout, int i, int j, int floor, int roomId) {
            return (this.roomFlagsByFloor[floor].get(i, j) & 0xFFFF) == roomId;
        }

        @Nullable
        public Direction findConnectedRoomDirection(FlagMatrix layout, int i, int j, int floor, int roomId) {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                if (!this.isRoomId(layout, i + direction.getOffsetX(), j + direction.getOffsetZ(), floor, roomId)) continue;
                return direction;
            }
            return null;
        }

        private void layoutCorridor(FlagMatrix layout, int i, int j, Direction direction, int length) {
            Direction direction2;
            if (length <= 0) {
                return;
            }
            layout.set(i, j, CORRIDOR);
            layout.update(i + direction.getOffsetX(), j + direction.getOffsetZ(), UNSET, CORRIDOR);
            for (int k = 0; k < 8; ++k) {
                direction2 = Direction.fromHorizontal(this.random.nextInt(4));
                if (direction2 == direction.getOpposite() || direction2 == Direction.EAST && this.random.nextBoolean()) continue;
                int l = i + direction.getOffsetX();
                int m = j + direction.getOffsetZ();
                if (layout.get(l + direction2.getOffsetX(), m + direction2.getOffsetZ()) != 0 || layout.get(l + direction2.getOffsetX() * 2, m + direction2.getOffsetZ() * 2) != 0) continue;
                this.layoutCorridor(layout, i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), direction2, length - 1);
                break;
            }
            Direction direction3 = direction.rotateYClockwise();
            direction2 = direction.rotateYCounterclockwise();
            layout.update(i + direction3.getOffsetX(), j + direction3.getOffsetZ(), UNSET, ROOM);
            layout.update(i + direction2.getOffsetX(), j + direction2.getOffsetZ(), UNSET, ROOM);
            layout.update(i + direction.getOffsetX() + direction3.getOffsetX(), j + direction.getOffsetZ() + direction3.getOffsetZ(), UNSET, ROOM);
            layout.update(i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), UNSET, ROOM);
            layout.update(i + direction.getOffsetX() * 2, j + direction.getOffsetZ() * 2, UNSET, ROOM);
            layout.update(i + direction3.getOffsetX() * 2, j + direction3.getOffsetZ() * 2, UNSET, ROOM);
            layout.update(i + direction2.getOffsetX() * 2, j + direction2.getOffsetZ() * 2, UNSET, ROOM);
        }

        private boolean adjustLayoutWithRooms(FlagMatrix layout) {
            boolean bl = false;
            for (int i = 0; i < layout.m; ++i) {
                for (int j = 0; j < layout.n; ++j) {
                    if (layout.get(j, i) != 0) continue;
                    int k = 0;
                    k += MansionParameters.isInsideMansion(layout, j + 1, i) ? 1 : 0;
                    k += MansionParameters.isInsideMansion(layout, j - 1, i) ? 1 : 0;
                    k += MansionParameters.isInsideMansion(layout, j, i + 1) ? 1 : 0;
                    if ((k += MansionParameters.isInsideMansion(layout, j, i - 1) ? 1 : 0) >= 3) {
                        layout.set(j, i, ROOM);
                        bl = true;
                        continue;
                    }
                    if (k != 2) continue;
                    int l = 0;
                    l += MansionParameters.isInsideMansion(layout, j + 1, i + 1) ? 1 : 0;
                    l += MansionParameters.isInsideMansion(layout, j - 1, i + 1) ? 1 : 0;
                    l += MansionParameters.isInsideMansion(layout, j + 1, i - 1) ? 1 : 0;
                    if ((l += MansionParameters.isInsideMansion(layout, j - 1, i - 1) ? 1 : 0) > 1) continue;
                    layout.set(j, i, ROOM);
                    bl = true;
                }
            }
            return bl;
        }

        private void layoutThirdFloor() {
            int l;
            int j;
            ArrayList<Pair<Integer, Integer>> list = Lists.newArrayList();
            FlagMatrix flagMatrix = this.roomFlagsByFloor[1];
            for (int i = 0; i < this.thirdFloorLayout.m; ++i) {
                for (j = 0; j < this.thirdFloorLayout.n; ++j) {
                    int k = flagMatrix.get(j, i);
                    l = k & 0xF0000;
                    if (l != 131072 || (k & 0x200000) != 0x200000) continue;
                    list.add(new Pair<Integer, Integer>(j, i));
                }
            }
            if (list.isEmpty()) {
                this.thirdFloorLayout.fill(0, 0, this.thirdFloorLayout.n, this.thirdFloorLayout.m, OUTSIDE);
                return;
            }
            Pair pair = (Pair)list.get(this.random.nextInt(list.size()));
            j = flagMatrix.get((Integer)pair.getLeft(), (Integer)pair.getRight());
            flagMatrix.set((Integer)pair.getLeft(), (Integer)pair.getRight(), j | 0x400000);
            Direction direction = this.findConnectedRoomDirection(this.baseLayout, (Integer)pair.getLeft(), (Integer)pair.getRight(), 1, j & 0xFFFF);
            l = (Integer)pair.getLeft() + direction.getOffsetX();
            int m = (Integer)pair.getRight() + direction.getOffsetZ();
            for (int n = 0; n < this.thirdFloorLayout.m; ++n) {
                for (int o = 0; o < this.thirdFloorLayout.n; ++o) {
                    if (!MansionParameters.isInsideMansion(this.baseLayout, o, n)) {
                        this.thirdFloorLayout.set(o, n, OUTSIDE);
                        continue;
                    }
                    if (o == (Integer)pair.getLeft() && n == (Integer)pair.getRight()) {
                        this.thirdFloorLayout.set(o, n, STAIRCASE);
                        continue;
                    }
                    if (o != l || n != m) continue;
                    this.thirdFloorLayout.set(o, n, STAIRCASE);
                    this.roomFlagsByFloor[2].set(o, n, 0x800000);
                }
            }
            ArrayList<Direction> list2 = Lists.newArrayList();
            for (Direction direction2 : Direction.Type.HORIZONTAL) {
                if (this.thirdFloorLayout.get(l + direction2.getOffsetX(), m + direction2.getOffsetZ()) != 0) continue;
                list2.add(direction2);
            }
            if (list2.isEmpty()) {
                this.thirdFloorLayout.fill(0, 0, this.thirdFloorLayout.n, this.thirdFloorLayout.m, OUTSIDE);
                flagMatrix.set((Integer)pair.getLeft(), (Integer)pair.getRight(), j);
                return;
            }
            Direction direction3 = (Direction)list2.get(this.random.nextInt(list2.size()));
            this.layoutCorridor(this.thirdFloorLayout, l + direction3.getOffsetX(), m + direction3.getOffsetZ(), direction3, 4);
            while (this.adjustLayoutWithRooms(this.thirdFloorLayout)) {
            }
        }

        private void updateRoomFlags(FlagMatrix layout, FlagMatrix roomFlags) {
            int i;
            ObjectArrayList<Pair<Integer, Integer>> objectArrayList = new ObjectArrayList<Pair<Integer, Integer>>();
            for (i = 0; i < layout.m; ++i) {
                for (int j = 0; j < layout.n; ++j) {
                    if (layout.get(j, i) != ROOM) continue;
                    objectArrayList.add(new Pair<Integer, Integer>(j, i));
                }
            }
            Util.shuffle(objectArrayList, this.random);
            i = 10;
            for (Pair pair : objectArrayList) {
                int l;
                int k = (Integer)pair.getLeft();
                if (roomFlags.get(k, l = ((Integer)pair.getRight()).intValue()) != 0) continue;
                int m = k;
                int n = k;
                int o = l;
                int p = l;
                int q = 65536;
                if (roomFlags.get(k + 1, l) == 0 && roomFlags.get(k, l + 1) == 0 && roomFlags.get(k + 1, l + 1) == 0 && layout.get(k + 1, l) == ROOM && layout.get(k, l + 1) == ROOM && layout.get(k + 1, l + 1) == ROOM) {
                    ++n;
                    ++p;
                    q = 262144;
                } else if (roomFlags.get(k - 1, l) == 0 && roomFlags.get(k, l + 1) == 0 && roomFlags.get(k - 1, l + 1) == 0 && layout.get(k - 1, l) == ROOM && layout.get(k, l + 1) == ROOM && layout.get(k - 1, l + 1) == ROOM) {
                    --m;
                    ++p;
                    q = 262144;
                } else if (roomFlags.get(k - 1, l) == 0 && roomFlags.get(k, l - 1) == 0 && roomFlags.get(k - 1, l - 1) == 0 && layout.get(k - 1, l) == ROOM && layout.get(k, l - 1) == ROOM && layout.get(k - 1, l - 1) == ROOM) {
                    --m;
                    --o;
                    q = 262144;
                } else if (roomFlags.get(k + 1, l) == 0 && layout.get(k + 1, l) == ROOM) {
                    ++n;
                    q = 131072;
                } else if (roomFlags.get(k, l + 1) == 0 && layout.get(k, l + 1) == ROOM) {
                    ++p;
                    q = 131072;
                } else if (roomFlags.get(k - 1, l) == 0 && layout.get(k - 1, l) == ROOM) {
                    --m;
                    q = 131072;
                } else if (roomFlags.get(k, l - 1) == 0 && layout.get(k, l - 1) == ROOM) {
                    --o;
                    q = 131072;
                }
                int r = this.random.nextBoolean() ? m : n;
                int s = this.random.nextBoolean() ? o : p;
                int t = 0x200000;
                if (!layout.anyMatchAround(r, s, CORRIDOR)) {
                    r = r == m ? n : m;
                    int n2 = s = s == o ? p : o;
                    if (!layout.anyMatchAround(r, s, CORRIDOR)) {
                        int n3 = s = s == o ? p : o;
                        if (!layout.anyMatchAround(r, s, CORRIDOR)) {
                            r = r == m ? n : m;
                            int n4 = s = s == o ? p : o;
                            if (!layout.anyMatchAround(r, s, CORRIDOR)) {
                                t = 0;
                                r = m;
                                s = o;
                            }
                        }
                    }
                }
                for (int u = o; u <= p; ++u) {
                    for (int v = m; v <= n; ++v) {
                        if (v == r && u == s) {
                            roomFlags.set(v, u, 0x100000 | t | q | i);
                            continue;
                        }
                        roomFlags.set(v, u, q | i);
                    }
                }
                ++i;
            }
        }

        public void printFloorLayouts() {
            for (int i = 0; i < 2; ++i) {
                FlagMatrix flagMatrix = i == 0 ? this.baseLayout : this.thirdFloorLayout;
                for (int j = 0; j < flagMatrix.m; ++j) {
                    for (int k = 0; k < flagMatrix.n; ++k) {
                        int l = flagMatrix.get(k, j);
                        if (l == CORRIDOR) {
                            System.out.print("+");
                            continue;
                        }
                        if (l == UNUSED) {
                            System.out.print("x");
                            continue;
                        }
                        if (l == ROOM) {
                            System.out.print("X");
                            continue;
                        }
                        if (l == STAIRCASE) {
                            System.out.print("O");
                            continue;
                        }
                        if (l == OUTSIDE) {
                            System.out.print("#");
                            continue;
                        }
                        System.out.print(" ");
                    }
                    System.out.println("");
                }
                System.out.println("");
            }
        }
    }

    static class LayoutGenerator {
        private final StructureTemplateManager manager;
        private final Random random;
        private int entranceI;
        private int entranceJ;

        public LayoutGenerator(StructureTemplateManager manager, Random random) {
            this.manager = manager;
            this.random = random;
        }

        public void generate(BlockPos pos, BlockRotation rotation, List<Piece> pieces, MansionParameters parameters) {
            int l;
            GenerationPiece generationPiece = new GenerationPiece();
            generationPiece.position = pos;
            generationPiece.rotation = rotation;
            generationPiece.template = "wall_flat";
            GenerationPiece generationPiece2 = new GenerationPiece();
            this.addEntrance(pieces, generationPiece);
            generationPiece2.position = generationPiece.position.up(8);
            generationPiece2.rotation = generationPiece.rotation;
            generationPiece2.template = "wall_window";
            if (!pieces.isEmpty()) {
                // empty if block
            }
            FlagMatrix flagMatrix = parameters.baseLayout;
            FlagMatrix flagMatrix2 = parameters.thirdFloorLayout;
            this.entranceI = parameters.entranceI + 1;
            this.entranceJ = parameters.entranceJ + 1;
            int i = parameters.entranceI + 1;
            int j = parameters.entranceJ;
            this.addOuterWall(pieces, generationPiece, flagMatrix, Direction.SOUTH, this.entranceI, this.entranceJ, i, j);
            this.addOuterWall(pieces, generationPiece2, flagMatrix, Direction.SOUTH, this.entranceI, this.entranceJ, i, j);
            GenerationPiece generationPiece3 = new GenerationPiece();
            generationPiece3.position = generationPiece.position.up(19);
            generationPiece3.rotation = generationPiece.rotation;
            generationPiece3.template = "wall_window";
            boolean bl = false;
            for (int k = 0; k < flagMatrix2.m && !bl; ++k) {
                for (l = flagMatrix2.n - 1; l >= 0 && !bl; --l) {
                    if (!MansionParameters.isInsideMansion(flagMatrix2, l, k)) continue;
                    generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.SOUTH), 8 + (k - this.entranceJ) * 8);
                    generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.EAST), (l - this.entranceI) * 8);
                    this.addWallPiece(pieces, generationPiece3);
                    this.addOuterWall(pieces, generationPiece3, flagMatrix2, Direction.SOUTH, l, k, l, k);
                    bl = true;
                }
            }
            this.addRoof(pieces, pos.up(16), rotation, flagMatrix, flagMatrix2);
            this.addRoof(pieces, pos.up(27), rotation, flagMatrix2, null);
            if (!pieces.isEmpty()) {
                // empty if block
            }
            RoomPool[] roomPools = new RoomPool[]{new FirstFloorRoomPool(), new SecondFloorRoomPool(), new ThirdFloorRoomPool()};
            for (l = 0; l < 3; ++l) {
                BlockPos blockPos = pos.up(8 * l + (l == 2 ? 3 : 0));
                FlagMatrix flagMatrix3 = parameters.roomFlagsByFloor[l];
                FlagMatrix flagMatrix4 = l == 2 ? flagMatrix2 : flagMatrix;
                String string = l == 0 ? "carpet_south_1" : "carpet_south_2";
                String string2 = l == 0 ? "carpet_west_1" : "carpet_west_2";
                for (int m = 0; m < flagMatrix4.m; ++m) {
                    for (int n = 0; n < flagMatrix4.n; ++n) {
                        if (flagMatrix4.get(n, m) != MansionParameters.CORRIDOR) continue;
                        BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (m - this.entranceJ) * 8);
                        blockPos2 = blockPos2.offset(rotation.rotate(Direction.EAST), (n - this.entranceI) * 8);
                        pieces.add(new Piece(this.manager, "corridor_floor", blockPos2, rotation));
                        if (flagMatrix4.get(n, m - 1) == MansionParameters.CORRIDOR || (flagMatrix3.get(n, m - 1) & 0x800000) == 0x800000) {
                            pieces.add(new Piece(this.manager, "carpet_north", blockPos2.offset(rotation.rotate(Direction.EAST), 1).up(), rotation));
                        }
                        if (flagMatrix4.get(n + 1, m) == MansionParameters.CORRIDOR || (flagMatrix3.get(n + 1, m) & 0x800000) == 0x800000) {
                            pieces.add(new Piece(this.manager, "carpet_east", blockPos2.offset(rotation.rotate(Direction.SOUTH), 1).offset(rotation.rotate(Direction.EAST), 5).up(), rotation));
                        }
                        if (flagMatrix4.get(n, m + 1) == MansionParameters.CORRIDOR || (flagMatrix3.get(n, m + 1) & 0x800000) == 0x800000) {
                            pieces.add(new Piece(this.manager, string, blockPos2.offset(rotation.rotate(Direction.SOUTH), 5).offset(rotation.rotate(Direction.WEST), 1), rotation));
                        }
                        if (flagMatrix4.get(n - 1, m) != MansionParameters.CORRIDOR && (flagMatrix3.get(n - 1, m) & 0x800000) != 0x800000) continue;
                        pieces.add(new Piece(this.manager, string2, blockPos2.offset(rotation.rotate(Direction.WEST), 1).offset(rotation.rotate(Direction.NORTH), 1), rotation));
                    }
                }
                String string3 = l == 0 ? "indoors_wall_1" : "indoors_wall_2";
                String string4 = l == 0 ? "indoors_door_1" : "indoors_door_2";
                ArrayList<Direction> list = Lists.newArrayList();
                for (int o = 0; o < flagMatrix4.m; ++o) {
                    for (int p = 0; p < flagMatrix4.n; ++p) {
                        Direction direction3;
                        BlockPos blockPos4;
                        boolean bl2;
                        boolean bl3 = bl2 = l == 2 && flagMatrix4.get(p, o) == MansionParameters.STAIRCASE;
                        if (flagMatrix4.get(p, o) != MansionParameters.ROOM && !bl2) continue;
                        int q = flagMatrix3.get(p, o);
                        int r = q & 0xF0000;
                        int s = q & 0xFFFF;
                        bl2 = bl2 && (q & 0x800000) == 0x800000;
                        list.clear();
                        if ((q & 0x200000) == 0x200000) {
                            for (Direction direction : Direction.Type.HORIZONTAL) {
                                if (flagMatrix4.get(p + direction.getOffsetX(), o + direction.getOffsetZ()) != MansionParameters.CORRIDOR) continue;
                                list.add(direction);
                            }
                        }
                        Direction direction2 = null;
                        if (!list.isEmpty()) {
                            direction2 = (Direction)list.get(this.random.nextInt(list.size()));
                        } else if ((q & 0x100000) == 0x100000) {
                            direction2 = Direction.UP;
                        }
                        BlockPos blockPos3 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (o - this.entranceJ) * 8);
                        blockPos3 = blockPos3.offset(rotation.rotate(Direction.EAST), -1 + (p - this.entranceI) * 8);
                        if (MansionParameters.isInsideMansion(flagMatrix4, p - 1, o) && !parameters.isRoomId(flagMatrix4, p - 1, o, l, s)) {
                            pieces.add(new Piece(this.manager, direction2 == Direction.WEST ? string4 : string3, blockPos3, rotation));
                        }
                        if (flagMatrix4.get(p + 1, o) == MansionParameters.CORRIDOR && !bl2) {
                            blockPos4 = blockPos3.offset(rotation.rotate(Direction.EAST), 8);
                            pieces.add(new Piece(this.manager, direction2 == Direction.EAST ? string4 : string3, blockPos4, rotation));
                        }
                        if (MansionParameters.isInsideMansion(flagMatrix4, p, o + 1) && !parameters.isRoomId(flagMatrix4, p, o + 1, l, s)) {
                            blockPos4 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 7);
                            blockPos4 = blockPos4.offset(rotation.rotate(Direction.EAST), 7);
                            pieces.add(new Piece(this.manager, direction2 == Direction.SOUTH ? string4 : string3, blockPos4, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                        if (flagMatrix4.get(p, o - 1) == MansionParameters.CORRIDOR && !bl2) {
                            blockPos4 = blockPos3.offset(rotation.rotate(Direction.NORTH), 1);
                            blockPos4 = blockPos4.offset(rotation.rotate(Direction.EAST), 7);
                            pieces.add(new Piece(this.manager, direction2 == Direction.NORTH ? string4 : string3, blockPos4, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                        if (r == 65536) {
                            this.addSmallRoom(pieces, blockPos3, rotation, direction2, roomPools[l]);
                            continue;
                        }
                        if (r == 131072 && direction2 != null) {
                            direction3 = parameters.findConnectedRoomDirection(flagMatrix4, p, o, l, s);
                            boolean bl32 = (q & 0x400000) == 0x400000;
                            this.addMediumRoom(pieces, blockPos3, rotation, direction3, direction2, roomPools[l], bl32);
                            continue;
                        }
                        if (r == 262144 && direction2 != null && direction2 != Direction.UP) {
                            direction3 = direction2.rotateYClockwise();
                            if (!parameters.isRoomId(flagMatrix4, p + direction3.getOffsetX(), o + direction3.getOffsetZ(), l, s)) {
                                direction3 = direction3.getOpposite();
                            }
                            this.addBigRoom(pieces, blockPos3, rotation, direction3, direction2, roomPools[l]);
                            continue;
                        }
                        if (r != 262144 || direction2 != Direction.UP) continue;
                        this.addBigSecretRoom(pieces, blockPos3, rotation, roomPools[l]);
                    }
                }
            }
        }

        private void addOuterWall(List<Piece> pieces, GenerationPiece wallPiece, FlagMatrix layout, Direction direction, int startI, int startJ, int endI, int endJ) {
            int i = startI;
            int j = startJ;
            Direction direction2 = direction;
            do {
                if (!MansionParameters.isInsideMansion(layout, i + direction.getOffsetX(), j + direction.getOffsetZ())) {
                    this.turnLeft(pieces, wallPiece);
                    direction = direction.rotateYClockwise();
                    if (i == endI && j == endJ && direction2 == direction) continue;
                    this.addWallPiece(pieces, wallPiece);
                    continue;
                }
                if (MansionParameters.isInsideMansion(layout, i + direction.getOffsetX(), j + direction.getOffsetZ()) && MansionParameters.isInsideMansion(layout, i + direction.getOffsetX() + direction.rotateYCounterclockwise().getOffsetX(), j + direction.getOffsetZ() + direction.rotateYCounterclockwise().getOffsetZ())) {
                    this.turnRight(pieces, wallPiece);
                    i += direction.getOffsetX();
                    j += direction.getOffsetZ();
                    direction = direction.rotateYCounterclockwise();
                    continue;
                }
                if ((i += direction.getOffsetX()) == endI && (j += direction.getOffsetZ()) == endJ && direction2 == direction) continue;
                this.addWallPiece(pieces, wallPiece);
            } while (i != endI || j != endJ || direction2 != direction);
        }

        private void addRoof(List<Piece> pieces, BlockPos pos, BlockRotation rotation, FlagMatrix layout, @Nullable FlagMatrix nextFloorLayout) {
            BlockPos blockPos2;
            boolean bl;
            BlockPos blockPos;
            int j;
            int i;
            for (i = 0; i < layout.m; ++i) {
                for (j = 0; j < layout.n; ++j) {
                    blockPos = pos;
                    blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
                    blockPos = blockPos.offset(rotation.rotate(Direction.EAST), (j - this.entranceI) * 8);
                    boolean bl2 = bl = nextFloorLayout != null && MansionParameters.isInsideMansion(nextFloorLayout, j, i);
                    if (!MansionParameters.isInsideMansion(layout, j, i) || bl) continue;
                    pieces.add(new Piece(this.manager, "roof", blockPos.up(3), rotation));
                    if (!MansionParameters.isInsideMansion(layout, j + 1, i)) {
                        blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
                        pieces.add(new Piece(this.manager, "roof_front", blockPos2, rotation));
                    }
                    if (!MansionParameters.isInsideMansion(layout, j - 1, i)) {
                        blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 0);
                        blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 7);
                        pieces.add(new Piece(this.manager, "roof_front", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                    }
                    if (!MansionParameters.isInsideMansion(layout, j, i - 1)) {
                        blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 1);
                        pieces.add(new Piece(this.manager, "roof_front", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                    }
                    if (MansionParameters.isInsideMansion(layout, j, i + 1)) continue;
                    blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
                    blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
                    pieces.add(new Piece(this.manager, "roof_front", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                }
            }
            if (nextFloorLayout != null) {
                for (i = 0; i < layout.m; ++i) {
                    for (j = 0; j < layout.n; ++j) {
                        blockPos = pos;
                        blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
                        blockPos = blockPos.offset(rotation.rotate(Direction.EAST), (j - this.entranceI) * 8);
                        bl = MansionParameters.isInsideMansion(nextFloorLayout, j, i);
                        if (!MansionParameters.isInsideMansion(layout, j, i) || !bl) continue;
                        if (!MansionParameters.isInsideMansion(layout, j + 1, i)) {
                            blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 7);
                            pieces.add(new Piece(this.manager, "small_wall", blockPos2, rotation));
                        }
                        if (!MansionParameters.isInsideMansion(layout, j - 1, i)) {
                            blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 1);
                            blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
                            pieces.add(new Piece(this.manager, "small_wall", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                        }
                        if (!MansionParameters.isInsideMansion(layout, j, i - 1)) {
                            blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 0);
                            blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 1);
                            pieces.add(new Piece(this.manager, "small_wall", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                        }
                        if (!MansionParameters.isInsideMansion(layout, j, i + 1)) {
                            blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
                            blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 7);
                            pieces.add(new Piece(this.manager, "small_wall", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                        if (!MansionParameters.isInsideMansion(layout, j + 1, i)) {
                            if (!MansionParameters.isInsideMansion(layout, j, i - 1)) {
                                blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 7);
                                blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 2);
                                pieces.add(new Piece(this.manager, "small_wall_corner", blockPos2, rotation));
                            }
                            if (!MansionParameters.isInsideMansion(layout, j, i + 1)) {
                                blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 8);
                                blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 7);
                                pieces.add(new Piece(this.manager, "small_wall_corner", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                            }
                        }
                        if (MansionParameters.isInsideMansion(layout, j - 1, i)) continue;
                        if (!MansionParameters.isInsideMansion(layout, j, i - 1)) {
                            blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 2);
                            blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 1);
                            pieces.add(new Piece(this.manager, "small_wall_corner", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                        }
                        if (MansionParameters.isInsideMansion(layout, j, i + 1)) continue;
                        blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 1);
                        blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 8);
                        pieces.add(new Piece(this.manager, "small_wall_corner", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                    }
                }
            }
            for (i = 0; i < layout.m; ++i) {
                for (j = 0; j < layout.n; ++j) {
                    BlockPos blockPos3;
                    blockPos = pos;
                    blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
                    blockPos = blockPos.offset(rotation.rotate(Direction.EAST), (j - this.entranceI) * 8);
                    boolean bl3 = bl = nextFloorLayout != null && MansionParameters.isInsideMansion(nextFloorLayout, j, i);
                    if (!MansionParameters.isInsideMansion(layout, j, i) || bl) continue;
                    if (!MansionParameters.isInsideMansion(layout, j + 1, i)) {
                        blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
                        if (!MansionParameters.isInsideMansion(layout, j, i + 1)) {
                            blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
                            pieces.add(new Piece(this.manager, "roof_corner", blockPos3, rotation));
                        } else if (MansionParameters.isInsideMansion(layout, j + 1, i + 1)) {
                            blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 5);
                            pieces.add(new Piece(this.manager, "roof_inner_corner", blockPos3, rotation));
                        }
                        if (!MansionParameters.isInsideMansion(layout, j, i - 1)) {
                            pieces.add(new Piece(this.manager, "roof_corner", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                        } else if (MansionParameters.isInsideMansion(layout, j + 1, i - 1)) {
                            blockPos3 = blockPos.offset(rotation.rotate(Direction.EAST), 9);
                            blockPos3 = blockPos3.offset(rotation.rotate(Direction.NORTH), 2);
                            pieces.add(new Piece(this.manager, "roof_inner_corner", blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                    }
                    if (MansionParameters.isInsideMansion(layout, j - 1, i)) continue;
                    blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 0);
                    blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 0);
                    if (!MansionParameters.isInsideMansion(layout, j, i + 1)) {
                        blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
                        pieces.add(new Piece(this.manager, "roof_corner", blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                    } else if (MansionParameters.isInsideMansion(layout, j - 1, i + 1)) {
                        blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 8);
                        blockPos3 = blockPos3.offset(rotation.rotate(Direction.WEST), 3);
                        pieces.add(new Piece(this.manager, "roof_inner_corner", blockPos3, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                    }
                    if (!MansionParameters.isInsideMansion(layout, j, i - 1)) {
                        pieces.add(new Piece(this.manager, "roof_corner", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                        continue;
                    }
                    if (!MansionParameters.isInsideMansion(layout, j - 1, i - 1)) continue;
                    blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 1);
                    pieces.add(new Piece(this.manager, "roof_inner_corner", blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                }
            }
        }

        private void addEntrance(List<Piece> pieces, GenerationPiece wallPiece) {
            Direction direction = wallPiece.rotation.rotate(Direction.WEST);
            pieces.add(new Piece(this.manager, "entrance", wallPiece.position.offset(direction, 9), wallPiece.rotation));
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), 16);
        }

        private void addWallPiece(List<Piece> pieces, GenerationPiece wallPiece) {
            pieces.add(new Piece(this.manager, wallPiece.template, wallPiece.position.offset(wallPiece.rotation.rotate(Direction.EAST), 7), wallPiece.rotation));
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), 8);
        }

        private void turnLeft(List<Piece> pieces, GenerationPiece wallPiece) {
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), -1);
            pieces.add(new Piece(this.manager, "wall_corner", wallPiece.position, wallPiece.rotation));
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), -7);
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.WEST), -6);
            wallPiece.rotation = wallPiece.rotation.rotate(BlockRotation.CLOCKWISE_90);
        }

        private void turnRight(List<Piece> pieces, GenerationPiece wallPiece) {
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), 6);
            wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.EAST), 8);
            wallPiece.rotation = wallPiece.rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
        }

        private void addSmallRoom(List<Piece> pieces, BlockPos pos, BlockRotation rotation, Direction direction, RoomPool pool) {
            BlockRotation blockRotation = BlockRotation.NONE;
            String string = pool.getSmallRoom(this.random);
            if (direction != Direction.EAST) {
                if (direction == Direction.NORTH) {
                    blockRotation = blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
                } else if (direction == Direction.WEST) {
                    blockRotation = blockRotation.rotate(BlockRotation.CLOCKWISE_180);
                } else if (direction == Direction.SOUTH) {
                    blockRotation = blockRotation.rotate(BlockRotation.CLOCKWISE_90);
                } else {
                    string = pool.getSmallSecretRoom(this.random);
                }
            }
            BlockPos blockPos = StructureTemplate.applyTransformedOffset(new BlockPos(1, 0, 0), BlockMirror.NONE, blockRotation, 7, 7);
            blockRotation = blockRotation.rotate(rotation);
            blockPos = blockPos.rotate(rotation);
            BlockPos blockPos2 = pos.add(blockPos.getX(), 0, blockPos.getZ());
            pieces.add(new Piece(this.manager, string, blockPos2, blockRotation));
        }

        private void addMediumRoom(List<Piece> pieces, BlockPos pos, BlockRotation rotation, Direction connectedRoomDirection, Direction entranceDirection, RoomPool pool, boolean staircase) {
            if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.SOUTH) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
                pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation));
            } else if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.NORTH) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
                blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
                pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation, BlockMirror.LEFT_RIGHT));
            } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.NORTH) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
                blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
                pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_180)));
            } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.SOUTH) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
                pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation, BlockMirror.FRONT_BACK));
            } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.EAST) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
                pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.LEFT_RIGHT));
            } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.WEST) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
                pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90)));
            } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.WEST) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
                blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
                pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.FRONT_BACK));
            } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.EAST) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
                blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
                pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
            } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.NORTH) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
                blockPos = blockPos.offset(rotation.rotate(Direction.NORTH), 8);
                pieces.add(new Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation));
            } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.SOUTH) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
                blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 14);
                pieces.add(new Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_180)));
            } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.EAST) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 15);
                pieces.add(new Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90)));
            } else if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.WEST) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.WEST), 7);
                blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
                pieces.add(new Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
            } else if (entranceDirection == Direction.UP && connectedRoomDirection == Direction.EAST) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 15);
                pieces.add(new Piece(this.manager, pool.getMediumSecretRoom(this.random), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90)));
            } else if (entranceDirection == Direction.UP && connectedRoomDirection == Direction.SOUTH) {
                BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
                blockPos = blockPos.offset(rotation.rotate(Direction.NORTH), 0);
                pieces.add(new Piece(this.manager, pool.getMediumSecretRoom(this.random), blockPos, rotation));
            }
        }

        private void addBigRoom(List<Piece> pieces, BlockPos pos, BlockRotation rotation, Direction connectedRoomDirection, Direction entranceDirection, RoomPool pool) {
            int i = 0;
            int j = 0;
            BlockRotation blockRotation = rotation;
            BlockMirror blockMirror = BlockMirror.NONE;
            if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.SOUTH) {
                i = -7;
            } else if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.NORTH) {
                i = -7;
                j = 6;
                blockMirror = BlockMirror.LEFT_RIGHT;
            } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.EAST) {
                i = 1;
                j = 14;
                blockRotation = rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
            } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.WEST) {
                i = 7;
                j = 14;
                blockRotation = rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
                blockMirror = BlockMirror.LEFT_RIGHT;
            } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.WEST) {
                i = 7;
                j = -8;
                blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_90);
            } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.EAST) {
                i = 1;
                j = -8;
                blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_90);
                blockMirror = BlockMirror.LEFT_RIGHT;
            } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.NORTH) {
                i = 15;
                j = 6;
                blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_180);
            } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.SOUTH) {
                i = 15;
                blockMirror = BlockMirror.FRONT_BACK;
            }
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), i);
            blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), j);
            pieces.add(new Piece(this.manager, pool.getBigRoom(this.random), blockPos, blockRotation, blockMirror));
        }

        private void addBigSecretRoom(List<Piece> pieces, BlockPos pos, BlockRotation rotation, RoomPool pool) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
            pieces.add(new Piece(this.manager, pool.getBigSecretRoom(this.random), blockPos, rotation, BlockMirror.NONE));
        }
    }

    static class ThirdFloorRoomPool
    extends SecondFloorRoomPool {
        ThirdFloorRoomPool() {
        }
    }

    static class SecondFloorRoomPool
    extends RoomPool {
        SecondFloorRoomPool() {
        }

        @Override
        public String getSmallRoom(Random random) {
            return "1x1_b" + (random.nextInt(4) + 1);
        }

        @Override
        public String getSmallSecretRoom(Random random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }

        @Override
        public String getMediumFunctionalRoom(Random random, boolean staircase) {
            if (staircase) {
                return "1x2_c_stairs";
            }
            return "1x2_c" + (random.nextInt(4) + 1);
        }

        @Override
        public String getMediumGenericRoom(Random random, boolean staircase) {
            if (staircase) {
                return "1x2_d_stairs";
            }
            return "1x2_d" + (random.nextInt(5) + 1);
        }

        @Override
        public String getMediumSecretRoom(Random random) {
            return "1x2_se" + (random.nextInt(1) + 1);
        }

        @Override
        public String getBigRoom(Random random) {
            return "2x2_b" + (random.nextInt(5) + 1);
        }

        @Override
        public String getBigSecretRoom(Random random) {
            return "2x2_s1";
        }
    }

    static class FirstFloorRoomPool
    extends RoomPool {
        FirstFloorRoomPool() {
        }

        @Override
        public String getSmallRoom(Random random) {
            return "1x1_a" + (random.nextInt(5) + 1);
        }

        @Override
        public String getSmallSecretRoom(Random random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }

        @Override
        public String getMediumFunctionalRoom(Random random, boolean staircase) {
            return "1x2_a" + (random.nextInt(9) + 1);
        }

        @Override
        public String getMediumGenericRoom(Random random, boolean staircase) {
            return "1x2_b" + (random.nextInt(5) + 1);
        }

        @Override
        public String getMediumSecretRoom(Random random) {
            return "1x2_s" + (random.nextInt(2) + 1);
        }

        @Override
        public String getBigRoom(Random random) {
            return "2x2_a" + (random.nextInt(4) + 1);
        }

        @Override
        public String getBigSecretRoom(Random random) {
            return "2x2_s1";
        }
    }

    static abstract class RoomPool {
        RoomPool() {
        }

        public abstract String getSmallRoom(Random var1);

        public abstract String getSmallSecretRoom(Random var1);

        public abstract String getMediumFunctionalRoom(Random var1, boolean var2);

        public abstract String getMediumGenericRoom(Random var1, boolean var2);

        public abstract String getMediumSecretRoom(Random var1);

        public abstract String getBigRoom(Random var1);

        public abstract String getBigSecretRoom(Random var1);
    }

    static class FlagMatrix {
        private final int[][] array;
        final int n;
        final int m;
        private final int fallback;

        public FlagMatrix(int n, int m, int fallback) {
            this.n = n;
            this.m = m;
            this.fallback = fallback;
            this.array = new int[n][m];
        }

        public void set(int i, int j, int value) {
            if (i >= 0 && i < this.n && j >= 0 && j < this.m) {
                this.array[i][j] = value;
            }
        }

        public void fill(int i0, int j0, int i1, int j1, int value) {
            for (int i = j0; i <= j1; ++i) {
                for (int j = i0; j <= i1; ++j) {
                    this.set(j, i, value);
                }
            }
        }

        public int get(int i, int j) {
            if (i >= 0 && i < this.n && j >= 0 && j < this.m) {
                return this.array[i][j];
            }
            return this.fallback;
        }

        public void update(int i, int j, int expected, int newValue) {
            if (this.get(i, j) == expected) {
                this.set(i, j, newValue);
            }
        }

        public boolean anyMatchAround(int i, int j, int value) {
            return this.get(i - 1, j) == value || this.get(i + 1, j) == value || this.get(i, j + 1) == value || this.get(i, j - 1) == value;
        }
    }

    static class GenerationPiece {
        public BlockRotation rotation;
        public BlockPos position;
        public String template;

        GenerationPiece() {
        }
    }

    public static class Piece
    extends SimpleStructurePiece {
        public Piece(StructureTemplateManager manager, String template, BlockPos pos, BlockRotation rotation) {
            this(manager, template, pos, rotation, BlockMirror.NONE);
        }

        public Piece(StructureTemplateManager manager, String template, BlockPos pos, BlockRotation rotation, BlockMirror mirror) {
            super(StructurePieceType.WOODLAND_MANSION, 0, manager, Piece.getId(template), template, Piece.createPlacementData(mirror, rotation), pos);
        }

        public Piece(StructureTemplateManager manager, NbtCompound nbt) {
            super(StructurePieceType.WOODLAND_MANSION, nbt, manager, (Identifier id) -> Piece.createPlacementData(BlockMirror.valueOf(nbt.getString("Mi")), BlockRotation.valueOf(nbt.getString("Rot"))));
        }

        @Override
        protected Identifier getId() {
            return Piece.getId(this.templateIdString);
        }

        private static Identifier getId(String identifier) {
            return new Identifier("woodland_mansion/" + identifier);
        }

        private static StructurePlacementData createPlacementData(BlockMirror mirror, BlockRotation rotation) {
            return new StructurePlacementData().setIgnoreEntities(true).setRotation(rotation).setMirror(mirror).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            super.writeNbt(context, nbt);
            nbt.putString("Rot", this.placementData.getRotation().name());
            nbt.putString("Mi", this.placementData.getMirror().name());
        }

        @Override
        protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
            if (metadata.startsWith("Chest")) {
                BlockRotation blockRotation = this.placementData.getRotation();
                BlockState blockState = Blocks.CHEST.getDefaultState();
                if ("ChestWest".equals(metadata)) {
                    blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.WEST));
                } else if ("ChestEast".equals(metadata)) {
                    blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.EAST));
                } else if ("ChestSouth".equals(metadata)) {
                    blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.SOUTH));
                } else if ("ChestNorth".equals(metadata)) {
                    blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.NORTH));
                }
                this.addChest(world, boundingBox, random, pos, LootTables.WOODLAND_MANSION_CHEST, blockState);
            } else {
                ArrayList<MobEntity> list = new ArrayList<MobEntity>();
                switch (metadata) {
                    case "Mage": {
                        list.add(EntityType.EVOKER.create(world.toServerWorld()));
                        break;
                    }
                    case "Warrior": {
                        list.add(EntityType.VINDICATOR.create(world.toServerWorld()));
                        break;
                    }
                    case "Group of Allays": {
                        int i = world.getRandom().nextInt(3) + 1;
                        for (int j = 0; j < i; ++j) {
                            list.add(EntityType.ALLAY.create(world.toServerWorld()));
                        }
                        break;
                    }
                    default: {
                        return;
                    }
                }
                for (MobEntity mobEntity : list) {
                    if (mobEntity == null) continue;
                    mobEntity.setPersistent();
                    mobEntity.refreshPositionAndAngles(pos, 0.0f, 0.0f);
                    mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.STRUCTURE, null, null);
                    world.spawnEntityAndPassengers(mobEntity);
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                }
            }
        }
    }
}

