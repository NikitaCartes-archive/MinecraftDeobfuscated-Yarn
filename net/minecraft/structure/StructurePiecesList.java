/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;

/**
 * An immutable list of structure pieces, usually belonging to a structure
 * start.
 */
public record StructurePiecesList(List<StructurePiece> pieces) {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier JIGSAW = new Identifier("jigsaw");
    private static final Map<Identifier, Identifier> ID_UPDATES = ImmutableMap.builder().put(new Identifier("nvi"), JIGSAW).put(new Identifier("pcp"), JIGSAW).put(new Identifier("bastionremnant"), JIGSAW).put(new Identifier("runtime"), JIGSAW).build();

    public StructurePiecesList(List<StructurePiece> pieces) {
        this.pieces = List.copyOf(pieces);
    }

    public boolean isEmpty() {
        return this.pieces.isEmpty();
    }

    public boolean contains(BlockPos pos) {
        for (StructurePiece structurePiece : this.pieces) {
            if (!structurePiece.getBoundingBox().contains(pos)) continue;
            return true;
        }
        return false;
    }

    public NbtElement toNbt(StructureContext context) {
        NbtList nbtList = new NbtList();
        for (StructurePiece structurePiece : this.pieces) {
            nbtList.add(structurePiece.toNbt(context));
        }
        return nbtList;
    }

    public static StructurePiecesList fromNbt(NbtList list, StructureContext context) {
        ArrayList<StructurePiece> list2 = Lists.newArrayList();
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound nbtCompound = list.getCompound(i);
            String string = nbtCompound.getString("id").toLowerCase(Locale.ROOT);
            Identifier identifier = new Identifier(string);
            Identifier identifier2 = ID_UPDATES.getOrDefault(identifier, identifier);
            StructurePieceType structurePieceType = Registry.STRUCTURE_PIECE.get(identifier2);
            if (structurePieceType == null) {
                LOGGER.error("Unknown structure piece id: {}", (Object)identifier2);
                continue;
            }
            try {
                StructurePiece structurePiece = structurePieceType.load(context, nbtCompound);
                list2.add(structurePiece);
                continue;
            } catch (Exception exception) {
                LOGGER.error("Exception loading structure piece with id {}", (Object)identifier2, (Object)exception);
            }
        }
        return new StructurePiecesList(list2);
    }

    public BlockBox getBoundingBox() {
        return StructurePiece.boundingBox(this.pieces.stream());
    }
}

