package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_7419;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record BlockNbtText(String rawPos, @Nullable PosArgument pos) implements class_7419 {
	public BlockNbtText(String rawPath) {
		this(rawPath, parsePos(rawPath));
	}

	@Nullable
	private static PosArgument parsePos(String string) {
		try {
			return BlockPosArgumentType.blockPos().parse(new StringReader(string));
		} catch (CommandSyntaxException var2) {
			return null;
		}
	}

	@Override
	public Stream<NbtCompound> toNbt(ServerCommandSource serverCommandSource) {
		if (this.pos != null) {
			ServerWorld serverWorld = serverCommandSource.getWorld();
			BlockPos blockPos = this.pos.toAbsoluteBlockPos(serverCommandSource);
			if (serverWorld.canSetBlock(blockPos)) {
				BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
				if (blockEntity != null) {
					return Stream.of(blockEntity.createNbtWithIdentifyingData());
				}
			}
		}

		return Stream.empty();
	}

	public String toString() {
		return "block=" + this.rawPos;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof BlockNbtText blockNbtText && this.rawPos.equals(blockNbtText.rawPos)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return this.rawPos.hashCode();
	}
}
