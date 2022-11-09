package net.minecraft.structure;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

/**
 * An immutable list of structure pieces, usually belonging to a structure
 * start.
 */
public record StructurePiecesList(List<StructurePiece> pieces) {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Identifier JIGSAW = new Identifier("jigsaw");
	private static final Map<Identifier, Identifier> ID_UPDATES = ImmutableMap.<Identifier, Identifier>builder()
		.put(new Identifier("nvi"), JIGSAW)
		.put(new Identifier("pcp"), JIGSAW)
		.put(new Identifier("bastionremnant"), JIGSAW)
		.put(new Identifier("runtime"), JIGSAW)
		.build();

	public StructurePiecesList(List<StructurePiece> pieces) {
		this.pieces = List.copyOf(pieces);
	}

	public boolean isEmpty() {
		return this.pieces.isEmpty();
	}

	public boolean contains(BlockPos pos) {
		for (StructurePiece structurePiece : this.pieces) {
			if (structurePiece.getBoundingBox().contains(pos)) {
				return true;
			}
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
		List<StructurePiece> list2 = Lists.<StructurePiece>newArrayList();

		for (int i = 0; i < list.size(); i++) {
			NbtCompound nbtCompound = list.getCompound(i);
			String string = nbtCompound.getString("id").toLowerCase(Locale.ROOT);
			Identifier identifier = new Identifier(string);
			Identifier identifier2 = (Identifier)ID_UPDATES.getOrDefault(identifier, identifier);
			StructurePieceType structurePieceType = Registries.STRUCTURE_PIECE.get(identifier2);
			if (structurePieceType == null) {
				LOGGER.error("Unknown structure piece id: {}", identifier2);
			} else {
				try {
					StructurePiece structurePiece = structurePieceType.load(context, nbtCompound);
					list2.add(structurePiece);
				} catch (Exception var10) {
					LOGGER.error("Exception loading structure piece with id {}", identifier2, var10);
				}
			}
		}

		return new StructurePiecesList(list2);
	}

	public BlockBox getBoundingBox() {
		return StructurePiece.boundingBox(this.pieces.stream());
	}
}
