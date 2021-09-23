/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.class_6625;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record class_6624(List<StructurePiece> pieces) {
    private static final Logger field_34941 = LogManager.getLogger();
    private static final Identifier field_34942 = new Identifier("jigsaw");
    private static final Map<Identifier, Identifier> field_34943 = ImmutableMap.builder().put(new Identifier("nvi"), field_34942).put(new Identifier("pcp"), field_34942).put(new Identifier("bastionremnant"), field_34942).put(new Identifier("runtime"), field_34942).build();

    public class_6624(List<StructurePiece> list) {
        this.pieces = List.copyOf(list);
    }

    public boolean method_38708() {
        return this.pieces.isEmpty();
    }

    public boolean method_38710(BlockPos blockPos) {
        for (StructurePiece structurePiece : this.pieces) {
            if (!structurePiece.getBoundingBox().contains(blockPos)) continue;
            return true;
        }
        return false;
    }

    public NbtElement method_38709(class_6625 arg) {
        NbtList nbtList = new NbtList();
        for (StructurePiece structurePiece : this.pieces) {
            nbtList.add(structurePiece.toNbt(arg));
        }
        return nbtList;
    }

    public static class_6624 method_38711(NbtList nbtList, class_6625 arg) {
        ArrayList<StructurePiece> list = Lists.newArrayList();
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            String string = nbtCompound.getString("id").toLowerCase(Locale.ROOT);
            Identifier identifier = new Identifier(string);
            Identifier identifier2 = field_34943.getOrDefault(identifier, identifier);
            StructurePieceType structurePieceType = Registry.STRUCTURE_PIECE.get(identifier2);
            if (structurePieceType == null) {
                field_34941.error("Unknown structure piece id: {}", (Object)identifier2);
                continue;
            }
            try {
                StructurePiece structurePiece = structurePieceType.load(arg, nbtCompound);
                list.add(structurePiece);
                continue;
            } catch (Exception exception) {
                field_34941.error("Exception loading structure piece with id {}", (Object)identifier2, (Object)exception);
            }
        }
        return new class_6624(list);
    }

    public BlockBox method_38712() {
        return StructurePiece.method_38703(this.pieces.stream());
    }
}

