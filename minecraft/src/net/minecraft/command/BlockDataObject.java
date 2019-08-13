package net.minecraft.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class BlockDataObject implements DataCommandObject {
	private static final SimpleCommandExceptionType INVALID_BLOCK_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.data.block.invalid"));
	public static final Function<String, DataCommand.ObjectType> field_13786 = string -> new DataCommand.ObjectType() {
			@Override
			public DataCommandObject getObject(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
				BlockPos blockPos = BlockPosArgumentType.getLoadedBlockPos(commandContext, string + "Pos");
				BlockEntity blockEntity = ((ServerCommandSource)commandContext.getSource()).getWorld().getBlockEntity(blockPos);
				if (blockEntity == null) {
					throw BlockDataObject.INVALID_BLOCK_EXCEPTION.create();
				} else {
					return new BlockDataObject(blockEntity, blockPos);
				}
			}

			@Override
			public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(
				ArgumentBuilder<ServerCommandSource, ?> argumentBuilder,
				Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> function
			) {
				return argumentBuilder.then(
					CommandManager.literal("block")
						.then((ArgumentBuilder<ServerCommandSource, ?>)function.apply(CommandManager.argument(string + "Pos", BlockPosArgumentType.blockPos())))
				);
			}
		};
	private final BlockEntity blockEntity;
	private final BlockPos pos;

	public BlockDataObject(BlockEntity blockEntity, BlockPos blockPos) {
		this.blockEntity = blockEntity;
		this.pos = blockPos;
	}

	@Override
	public void setTag(CompoundTag compoundTag) {
		compoundTag.putInt("x", this.pos.getX());
		compoundTag.putInt("y", this.pos.getY());
		compoundTag.putInt("z", this.pos.getZ());
		this.blockEntity.fromTag(compoundTag);
		this.blockEntity.markDirty();
		BlockState blockState = this.blockEntity.getWorld().getBlockState(this.pos);
		this.blockEntity.getWorld().updateListeners(this.pos, blockState, blockState, 3);
	}

	@Override
	public CompoundTag getTag() {
		return this.blockEntity.toTag(new CompoundTag());
	}

	@Override
	public Text getModifiedFeedback() {
		return new TranslatableText("commands.data.block.modified", this.pos.getX(), this.pos.getY(), this.pos.getZ());
	}

	@Override
	public Text getQueryFeedback(Tag tag) {
		return new TranslatableText("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), tag.toText());
	}

	@Override
	public Text getGetFeedback(NbtPathArgumentType.NbtPath nbtPath, double d, int i) {
		return new TranslatableText("commands.data.block.get", nbtPath, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", d), i);
	}
}
