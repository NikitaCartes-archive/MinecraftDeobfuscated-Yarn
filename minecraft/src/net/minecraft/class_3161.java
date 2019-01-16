package net.minecraft;

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
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;

public class class_3161 implements class_3162 {
	private static final SimpleCommandExceptionType field_13785 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.data.block.invalid"));
	public static final Function<String, DataCommand.class_3167> field_13786 = string -> new DataCommand.class_3167() {
			@Override
			public class_3162 method_13924(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
				BlockPos blockPos = BlockPosArgumentType.getValidPosArgument(commandContext, string + "Pos");
				BlockEntity blockEntity = ((ServerCommandSource)commandContext.getSource()).getWorld().getBlockEntity(blockPos);
				if (blockEntity == null) {
					throw class_3161.field_13785.create();
				} else {
					return new class_3161(blockEntity, blockPos);
				}
			}

			@Override
			public ArgumentBuilder<ServerCommandSource, ?> method_13925(
				ArgumentBuilder<ServerCommandSource, ?> argumentBuilder,
				Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> function
			) {
				return argumentBuilder.then(
					ServerCommandManager.literal("block")
						.then((ArgumentBuilder<ServerCommandSource, ?>)function.apply(ServerCommandManager.argument(string + "Pos", BlockPosArgumentType.create())))
				);
			}
		};
	private final BlockEntity field_13784;
	private final BlockPos pos;

	public class_3161(BlockEntity blockEntity, BlockPos blockPos) {
		this.field_13784 = blockEntity;
		this.pos = blockPos;
	}

	@Override
	public void method_13880(CompoundTag compoundTag) {
		compoundTag.putInt("x", this.pos.getX());
		compoundTag.putInt("y", this.pos.getY());
		compoundTag.putInt("z", this.pos.getZ());
		this.field_13784.fromTag(compoundTag);
		this.field_13784.markDirty();
		BlockState blockState = this.field_13784.getWorld().getBlockState(this.pos);
		this.field_13784.getWorld().updateListeners(this.pos, blockState, blockState, 3);
	}

	@Override
	public CompoundTag method_13881() {
		return this.field_13784.toTag(new CompoundTag());
	}

	@Override
	public TextComponent method_13883() {
		return new TranslatableTextComponent("commands.data.block.modified", this.pos.getX(), this.pos.getY(), this.pos.getZ());
	}

	@Override
	public TextComponent method_13882(Tag tag) {
		return new TranslatableTextComponent("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), tag.toTextComponent());
	}

	@Override
	public TextComponent method_13879(NbtPathArgumentType.class_2209 arg, double d, int i) {
		return new TranslatableTextComponent(
			"commands.data.block.get", arg, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", d), i
		);
	}
}
