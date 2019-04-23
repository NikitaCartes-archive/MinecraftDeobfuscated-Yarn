/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import net.minecraft.ChatFormat;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class ServerCommandSource
implements CommandSource {
    public static final SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("permissions.requires.player", new Object[0]));
    public static final SimpleCommandExceptionType REQUIRES_ENTITY_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("permissions.requires.entity", new Object[0]));
    private final CommandOutput output;
    private final Vec3d position;
    private final ServerWorld world;
    private final int level;
    private final String simpleName;
    private final Component name;
    private final MinecraftServer minecraftServer;
    private final boolean silent;
    @Nullable
    private final Entity entity;
    private final ResultConsumer<ServerCommandSource> resultConsumer;
    private final EntityAnchorArgumentType.EntityAnchor entityAnchor;
    private final Vec2f rotation;

    public ServerCommandSource(CommandOutput commandOutput, Vec3d vec3d, Vec2f vec2f, ServerWorld serverWorld, int i2, String string, Component component, MinecraftServer minecraftServer, @Nullable Entity entity) {
        this(commandOutput, vec3d, vec2f, serverWorld, i2, string, component, minecraftServer, entity, false, (commandContext, bl, i) -> {}, EntityAnchorArgumentType.EntityAnchor.FEET);
    }

    protected ServerCommandSource(CommandOutput commandOutput, Vec3d vec3d, Vec2f vec2f, ServerWorld serverWorld, int i, String string, Component component, MinecraftServer minecraftServer, @Nullable Entity entity, boolean bl, ResultConsumer<ServerCommandSource> resultConsumer, EntityAnchorArgumentType.EntityAnchor entityAnchor) {
        this.output = commandOutput;
        this.position = vec3d;
        this.world = serverWorld;
        this.silent = bl;
        this.entity = entity;
        this.level = i;
        this.simpleName = string;
        this.name = component;
        this.minecraftServer = minecraftServer;
        this.resultConsumer = resultConsumer;
        this.entityAnchor = entityAnchor;
        this.rotation = vec2f;
    }

    public ServerCommandSource withEntity(Entity entity) {
        if (this.entity == entity) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, entity.getName().getString(), entity.getDisplayName(), this.minecraftServer, entity, this.silent, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withPosition(Vec3d vec3d) {
        if (this.position.equals(vec3d)) {
            return this;
        }
        return new ServerCommandSource(this.output, vec3d, this.rotation, this.world, this.level, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withRotation(Vec2f vec2f) {
        if (this.rotation.equals(vec2f)) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, vec2f, this.world, this.level, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withConsumer(ResultConsumer<ServerCommandSource> resultConsumer) {
        if (this.resultConsumer.equals(resultConsumer)) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource mergeConsumers(ResultConsumer<ServerCommandSource> resultConsumer, BinaryOperator<ResultConsumer<ServerCommandSource>> binaryOperator) {
        ResultConsumer resultConsumer2 = (ResultConsumer)binaryOperator.apply(this.resultConsumer, resultConsumer);
        return this.withConsumer(resultConsumer2);
    }

    public ServerCommandSource withSilent() {
        if (this.silent) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, this.simpleName, this.name, this.minecraftServer, this.entity, true, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withLevel(int i) {
        if (i == this.level) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, i, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withMaxLevel(int i) {
        if (i <= this.level) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, i, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withEntityAnchor(EntityAnchorArgumentType.EntityAnchor entityAnchor) {
        if (entityAnchor == this.entityAnchor) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, this.resultConsumer, entityAnchor);
    }

    public ServerCommandSource withWorld(ServerWorld serverWorld) {
        if (serverWorld == this.world) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, serverWorld, this.level, this.simpleName, this.name, this.minecraftServer, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withLookingAt(Entity entity, EntityAnchorArgumentType.EntityAnchor entityAnchor) throws CommandSyntaxException {
        return this.withLookingAt(entityAnchor.positionAt(entity));
    }

    public ServerCommandSource withLookingAt(Vec3d vec3d) throws CommandSyntaxException {
        Vec3d vec3d2 = this.entityAnchor.positionAt(this);
        double d = vec3d.x - vec3d2.x;
        double e = vec3d.y - vec3d2.y;
        double f = vec3d.z - vec3d2.z;
        double g = MathHelper.sqrt(d * d + f * f);
        float h = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875)));
        float i = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f);
        return this.withRotation(new Vec2f(h, i));
    }

    public Component getDisplayName() {
        return this.name;
    }

    public String getName() {
        return this.simpleName;
    }

    @Override
    public boolean hasPermissionLevel(int i) {
        return this.level >= i;
    }

    public Vec3d getPosition() {
        return this.position;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    public Entity getEntityOrThrow() throws CommandSyntaxException {
        if (this.entity == null) {
            throw REQUIRES_ENTITY_EXCEPTION.create();
        }
        return this.entity;
    }

    public ServerPlayerEntity getPlayer() throws CommandSyntaxException {
        if (!(this.entity instanceof ServerPlayerEntity)) {
            throw REQUIRES_PLAYER_EXCEPTION.create();
        }
        return (ServerPlayerEntity)this.entity;
    }

    public Vec2f getRotation() {
        return this.rotation;
    }

    public MinecraftServer getMinecraftServer() {
        return this.minecraftServer;
    }

    public EntityAnchorArgumentType.EntityAnchor getEntityAnchor() {
        return this.entityAnchor;
    }

    public void sendFeedback(Component component, boolean bl) {
        if (this.output.sendCommandFeedback() && !this.silent) {
            this.output.sendMessage(component);
        }
        if (bl && this.output.shouldBroadcastConsoleToOps() && !this.silent) {
            this.sendToOps(component);
        }
    }

    private void sendToOps(Component component) {
        Component component2 = new TranslatableComponent("chat.type.admin", this.getDisplayName(), component).applyFormat(ChatFormat.GRAY, ChatFormat.ITALIC);
        if (this.minecraftServer.getGameRules().getBoolean("sendCommandFeedback")) {
            for (ServerPlayerEntity serverPlayerEntity : this.minecraftServer.getPlayerManager().getPlayerList()) {
                if (serverPlayerEntity == this.output || !this.minecraftServer.getPlayerManager().isOperator(serverPlayerEntity.getGameProfile())) continue;
                serverPlayerEntity.sendMessage(component2);
            }
        }
        if (this.output != this.minecraftServer && this.minecraftServer.getGameRules().getBoolean("logAdminCommands")) {
            this.minecraftServer.sendMessage(component2);
        }
    }

    public void sendError(Component component) {
        if (this.output.shouldTrackOutput() && !this.silent) {
            this.output.sendMessage(new TextComponent("").append(component).applyFormat(ChatFormat.RED));
        }
    }

    public void onCommandComplete(CommandContext<ServerCommandSource> commandContext, boolean bl, int i) {
        if (this.resultConsumer != null) {
            this.resultConsumer.onCommandComplete(commandContext, bl, i);
        }
    }

    @Override
    public Collection<String> getPlayerNames() {
        return Lists.newArrayList(this.minecraftServer.getPlayerNames());
    }

    @Override
    public Collection<String> getTeamNames() {
        return this.minecraftServer.getScoreboard().getTeamNames();
    }

    @Override
    public Collection<Identifier> getSoundIds() {
        return Registry.SOUND_EVENT.getIds();
    }

    @Override
    public Stream<Identifier> getRecipeIds() {
        return this.minecraftServer.getRecipeManager().keys();
    }

    @Override
    public CompletableFuture<Suggestions> getCompletions(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return null;
    }
}

