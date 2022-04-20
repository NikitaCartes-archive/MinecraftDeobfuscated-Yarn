/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.NbtDataSource;
import org.jetbrains.annotations.Nullable;

public record EntityNbtDataSource(String rawSelector, @Nullable EntitySelector selector) implements NbtDataSource
{
    public EntityNbtDataSource(String rawPath) {
        this(rawPath, EntityNbtDataSource.parseSelector(rawPath));
    }

    @Nullable
    private static EntitySelector parseSelector(String rawSelector) {
        try {
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(rawSelector));
            return entitySelectorReader.read();
        } catch (CommandSyntaxException commandSyntaxException) {
            return null;
        }
    }

    @Override
    public Stream<NbtCompound> get(ServerCommandSource source) throws CommandSyntaxException {
        if (this.selector != null) {
            List<? extends Entity> list = this.selector.getEntities(source);
            return list.stream().map(NbtPredicate::entityToNbt);
        }
        return Stream.empty();
    }

    @Override
    public String toString() {
        return "entity=" + this.rawSelector;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof EntityNbtDataSource)) return false;
        EntityNbtDataSource entityNbtDataSource = (EntityNbtDataSource)object;
        if (!this.rawSelector.equals(entityNbtDataSource.rawSelector)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.rawSelector);
    }

    @Nullable
    public EntitySelector selector() {
        return this.selector;
    }
}

