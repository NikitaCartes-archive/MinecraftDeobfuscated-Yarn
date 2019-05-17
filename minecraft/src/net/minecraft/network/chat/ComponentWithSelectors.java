package net.minecraft.network.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

public interface ComponentWithSelectors {
	Component resolve(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity, int i) throws CommandSyntaxException;
}
