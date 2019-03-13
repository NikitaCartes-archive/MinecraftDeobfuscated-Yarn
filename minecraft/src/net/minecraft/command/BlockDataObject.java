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
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;

public class BlockDataObject implements DataCommandObject {
	private static final SimpleCommandExceptionType field_13785 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.data.block.invalid"));
	public static final Function<String, DataCommand.class_3167> field_13786 = string -> new DataCommand.class_3167() {
			@Override
			public DataCommandObject method_13924(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
				BlockPos blockPos = BlockPosArgumentType.method_9696(commandContext, string + "Pos");
				BlockEntity blockEntity = ((ServerCommandSource)commandContext.getSource()).method_9225().method_8321(blockPos);
				if (blockEntity == null) {
					throw BlockDataObject.field_13785.create();
				} else {
					return new BlockDataObject(blockEntity, blockPos);
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
	private final BlockPos field_13783;

	public BlockDataObject(BlockEntity blockEntity, BlockPos blockPos) {
		this.field_13784 = blockEntity;
		this.field_13783 = blockPos;
	}

	@Override
	public void method_13880(CompoundTag compoundTag) {
		compoundTag.putInt("x", this.field_13783.getX());
		compoundTag.putInt("y", this.field_13783.getY());
		compoundTag.putInt("z", this.field_13783.getZ());
		this.field_13784.method_11014(compoundTag);
		this.field_13784.markDirty();
		BlockState blockState = this.field_13784.getWorld().method_8320(this.field_13783);
		this.field_13784.getWorld().method_8413(this.field_13783, blockState, blockState, 3);
	}

	@Override
	public CompoundTag method_13881() {
		return this.field_13784.method_11007(new CompoundTag());
	}

	@Override
	public TextComponent method_13883() {
		return new TranslatableTextComponent("commands.data.block.modified", this.field_13783.getX(), this.field_13783.getY(), this.field_13783.getZ());
	}

	@Override
	public TextComponent method_13882(Tag tag) {
		return new TranslatableTextComponent(
			"commands.data.block.query", this.field_13783.getX(), this.field_13783.getY(), this.field_13783.getZ(), tag.method_10715()
		);
	}

	@Override
	public TextComponent method_13879(NbtPathArgumentType.class_2209 arg, double d, int i) {
		return new TranslatableTextComponent(
			"commands.data.block.get", arg, this.field_13783.getX(), this.field_13783.getY(), this.field_13783.getZ(), String.format(Locale.ROOT, "%.2f", d), i
		);
	}
}
