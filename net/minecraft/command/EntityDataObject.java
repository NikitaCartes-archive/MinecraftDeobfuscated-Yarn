/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class EntityDataObject
implements DataCommandObject {
    private static final SimpleCommandExceptionType INVALID_ENTITY_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.data.entity.invalid"));
    public static final Function<String, DataCommand.ObjectType> TYPE_FACTORY = string -> new DataCommand.ObjectType((String)string){
        final /* synthetic */ String field_13802;
        {
            this.field_13802 = string;
        }

        @Override
        public DataCommandObject getObject(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
            return new EntityDataObject(EntityArgumentType.getEntity(context, this.field_13802));
        }

        @Override
        public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ServerCommandSource, ?> argument, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> argumentAdder) {
            return argument.then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("entity").then(argumentAdder.apply(CommandManager.argument(this.field_13802, EntityArgumentType.entity()))));
        }
    };
    private final Entity entity;

    public EntityDataObject(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void setNbt(NbtCompound nbt) throws CommandSyntaxException {
        if (this.entity instanceof PlayerEntity) {
            throw INVALID_ENTITY_EXCEPTION.create();
        }
        UUID uUID = this.entity.getUuid();
        this.entity.readNbt(nbt);
        this.entity.setUuid(uUID);
    }

    @Override
    public NbtCompound getNbt() {
        return NbtPredicate.entityToNbt(this.entity);
    }

    @Override
    public Text feedbackModify() {
        return new TranslatableText("commands.data.entity.modified", this.entity.getDisplayName());
    }

    @Override
    public Text feedbackQuery(NbtElement element) {
        return new TranslatableText("commands.data.entity.query", this.entity.getDisplayName(), NbtHelper.toPrettyPrintedText(element));
    }

    @Override
    public Text feedbackGet(NbtPathArgumentType.NbtPath path, double scale, int result) {
        return new TranslatableText("commands.data.entity.get", path, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", scale), result);
    }
}

