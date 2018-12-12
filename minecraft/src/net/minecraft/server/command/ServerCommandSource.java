package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class ServerCommandSource implements CommandSource {
	public static final SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("permissions.requires.player")
	);
	public static final SimpleCommandExceptionType REQUIRES_ENTITY_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("permissions.requires.entity")
	);
	private final CommandOutput output;
	private final Vec3d position;
	private final ServerWorld world;
	private final int level;
	private final String name;
	private final TextComponent textComponent;
	private final MinecraftServer minecraftServer;
	private final boolean silent;
	@Nullable
	private final Entity entity;
	private final ResultConsumer<ServerCommandSource> resultConsumer;
	private final EntityAnchorArgumentType.EntityAnchor entityAnchor;
	private final Vec2f rotation;

	public ServerCommandSource(
		CommandOutput commandOutput,
		Vec3d vec3d,
		Vec2f vec2f,
		ServerWorld serverWorld,
		int i,
		String string,
		TextComponent textComponent,
		MinecraftServer minecraftServer,
		@Nullable Entity entity
	) {
		this(commandOutput, vec3d, vec2f, serverWorld, i, string, textComponent, minecraftServer, entity, false, (commandContext, bl, ix) -> {
		}, EntityAnchorArgumentType.EntityAnchor.field_9853);
	}

	protected ServerCommandSource(
		CommandOutput commandOutput,
		Vec3d vec3d,
		Vec2f vec2f,
		ServerWorld serverWorld,
		int i,
		String string,
		TextComponent textComponent,
		MinecraftServer minecraftServer,
		@Nullable Entity entity,
		boolean bl,
		ResultConsumer<ServerCommandSource> resultConsumer,
		EntityAnchorArgumentType.EntityAnchor entityAnchor
	) {
		this.output = commandOutput;
		this.position = vec3d;
		this.world = serverWorld;
		this.silent = bl;
		this.entity = entity;
		this.level = i;
		this.name = string;
		this.textComponent = textComponent;
		this.minecraftServer = minecraftServer;
		this.resultConsumer = resultConsumer;
		this.entityAnchor = entityAnchor;
		this.rotation = vec2f;
	}

	public ServerCommandSource withEntity(Entity entity) {
		return this.entity == entity
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				this.world,
				this.level,
				entity.getName().getString(),
				entity.getDisplayName(),
				this.minecraftServer,
				entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource withPosition(Vec3d vec3d) {
		return this.position.equals(vec3d)
			? this
			: new ServerCommandSource(
				this.output,
				vec3d,
				this.rotation,
				this.world,
				this.level,
				this.name,
				this.textComponent,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource withRotation(Vec2f vec2f) {
		return this.rotation.equals(vec2f)
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				vec2f,
				this.world,
				this.level,
				this.name,
				this.textComponent,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource withConsumer(ResultConsumer<ServerCommandSource> resultConsumer) {
		return this.resultConsumer.equals(resultConsumer)
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				this.world,
				this.level,
				this.name,
				this.textComponent,
				this.minecraftServer,
				this.entity,
				this.silent,
				resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource mergeConsumers(
		ResultConsumer<ServerCommandSource> resultConsumer, BinaryOperator<ResultConsumer<ServerCommandSource>> binaryOperator
	) {
		ResultConsumer<ServerCommandSource> resultConsumer2 = (ResultConsumer<ServerCommandSource>)binaryOperator.apply(this.resultConsumer, resultConsumer);
		return this.withConsumer(resultConsumer2);
	}

	public ServerCommandSource withSilent() {
		return this.silent
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				this.world,
				this.level,
				this.name,
				this.textComponent,
				this.minecraftServer,
				this.entity,
				true,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource withLevel(int i) {
		return i == this.level
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				this.world,
				i,
				this.name,
				this.textComponent,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource withMaxLevel(int i) {
		return i <= this.level
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				this.world,
				i,
				this.name,
				this.textComponent,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource withEntityAnchor(EntityAnchorArgumentType.EntityAnchor entityAnchor) {
		return entityAnchor == this.entityAnchor
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				this.world,
				this.level,
				this.name,
				this.textComponent,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				entityAnchor
			);
	}

	public ServerCommandSource withWorld(ServerWorld serverWorld) {
		return serverWorld == this.world
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				serverWorld,
				this.level,
				this.name,
				this.textComponent,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource withLookingAt(Entity entity, EntityAnchorArgumentType.EntityAnchor entityAnchor) throws CommandSyntaxException {
		return this.withLookingAt(entityAnchor.positionAt(entity));
	}

	public ServerCommandSource withLookingAt(Vec3d vec3d) throws CommandSyntaxException {
		Vec3d vec3d2 = this.entityAnchor.positionAt(this);
		double d = vec3d.x - vec3d2.x;
		double e = vec3d.y - vec3d2.y;
		double f = vec3d.z - vec3d2.z;
		double g = (double)MathHelper.sqrt(d * d + f * f);
		float h = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI)));
		float i = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F);
		return this.withRotation(new Vec2f(h, i));
	}

	public TextComponent getDisplayName() {
		return this.textComponent;
	}

	public String getName() {
		return this.name;
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
		} else {
			return this.entity;
		}
	}

	public ServerPlayerEntity getPlayer() throws CommandSyntaxException {
		if (!(this.entity instanceof ServerPlayerEntity)) {
			throw REQUIRES_PLAYER_EXCEPTION.create();
		} else {
			return (ServerPlayerEntity)this.entity;
		}
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

	public void sendFeedback(TextComponent textComponent, boolean bl) {
		if (this.output.sendCommandFeedback() && !this.silent) {
			this.output.appendCommandFeedback(textComponent);
		}

		if (bl && this.output.shouldBroadcastConsoleToOps() && !this.silent) {
			this.sendToOps(textComponent);
		}
	}

	private void sendToOps(TextComponent textComponent) {
		TextComponent textComponent2 = new TranslatableTextComponent("chat.type.admin", this.getDisplayName(), textComponent)
			.applyFormat(new TextFormat[]{TextFormat.GRAY, TextFormat.ITALIC});
		if (this.minecraftServer.getGameRules().getBoolean("sendCommandFeedback")) {
			for (ServerPlayerEntity serverPlayerEntity : this.minecraftServer.getPlayerManager().getPlayerList()) {
				if (serverPlayerEntity != this.output && this.minecraftServer.getPlayerManager().isOperator(serverPlayerEntity.getGameProfile())) {
					serverPlayerEntity.appendCommandFeedback(textComponent2);
				}
			}
		}

		if (this.output != this.minecraftServer && this.minecraftServer.getGameRules().getBoolean("logAdminCommands")) {
			this.minecraftServer.appendCommandFeedback(textComponent2);
		}
	}

	public void sendError(TextComponent textComponent) {
		if (this.output.shouldTrackOutput() && !this.silent) {
			this.output.appendCommandFeedback(new StringTextComponent("").append(textComponent).applyFormat(TextFormat.RED));
		}
	}

	public void method_9215(CommandContext<ServerCommandSource> commandContext, boolean bl, int i) {
		if (this.resultConsumer != null) {
			this.resultConsumer.onCommandComplete(commandContext, bl, i);
		}
	}

	@Override
	public Collection<String> getPlayerNames() {
		return Lists.<String>newArrayList(this.minecraftServer.getPlayerNames());
	}

	@Override
	public Collection<String> getTeamNames() {
		return this.minecraftServer.getScoreboard().getTeamNames();
	}

	@Override
	public Collection<Identifier> getSoundIds() {
		return Registry.SOUND_EVENT.keys();
	}

	@Override
	public Collection<Identifier> getRecipeIds() {
		return this.minecraftServer.getRecipeManager().keys();
	}

	@Override
	public CompletableFuture<Suggestions> getCompletions(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return null;
	}

	@Override
	public Collection<CommandSource.RelativePosition> method_9274(boolean bl) {
		return Collections.singleton(CommandSource.RelativePosition.ZERO_WORLD);
	}
}
