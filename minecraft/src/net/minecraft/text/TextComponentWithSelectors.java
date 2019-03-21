package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

public interface TextComponentWithSelectors {
	TextComponent resolveSelectors(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity) throws CommandSyntaxException;
}
