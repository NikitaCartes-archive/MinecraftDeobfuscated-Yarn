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
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;

public class ServerCommandSource implements CommandSource {
	public static final SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("permissions.requires.player"));
	public static final SimpleCommandExceptionType REQUIRES_ENTITY_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("permissions.requires.entity"));
	private final CommandOutput output;
	private final Vec3d position;
	private final ServerWorld world;
	private final int level;
	private final String simpleName;
	private final Text name;
	private final MinecraftServer minecraftServer;
	private final boolean silent;
	@Nullable
	private final Entity entity;
	private final ResultConsumer<ServerCommandSource> resultConsumer;
	private final EntityAnchorArgumentType.EntityAnchor entityAnchor;
	private final Vec2f rotation;

	public ServerCommandSource(
		CommandOutput output, Vec3d pos, Vec2f rot, ServerWorld world, int level, String simpleName, Text name, MinecraftServer server, @Nullable Entity entity
	) {
		this(output, pos, rot, world, level, simpleName, name, server, entity, false, (commandContext, bl, i) -> {
		}, EntityAnchorArgumentType.EntityAnchor.FEET);
	}

	protected ServerCommandSource(
		CommandOutput output,
		Vec3d pos,
		Vec2f rot,
		ServerWorld world,
		int level,
		String simpleName,
		Text name,
		MinecraftServer server,
		@Nullable Entity entity,
		boolean silent,
		ResultConsumer<ServerCommandSource> resultConsumer,
		EntityAnchorArgumentType.EntityAnchor entityAnchor
	) {
		this.output = output;
		this.position = pos;
		this.world = world;
		this.silent = silent;
		this.entity = entity;
		this.level = level;
		this.simpleName = simpleName;
		this.name = name;
		this.minecraftServer = server;
		this.resultConsumer = resultConsumer;
		this.entityAnchor = entityAnchor;
		this.rotation = rot;
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

	public ServerCommandSource withPosition(Vec3d position) {
		return this.position.equals(position)
			? this
			: new ServerCommandSource(
				this.output,
				position,
				this.rotation,
				this.world,
				this.level,
				this.simpleName,
				this.name,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource withRotation(Vec2f rotation) {
		return this.rotation.equals(rotation)
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				rotation,
				this.world,
				this.level,
				this.simpleName,
				this.name,
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
				this.simpleName,
				this.name,
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
				this.simpleName,
				this.name,
				this.minecraftServer,
				this.entity,
				true,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource withLevel(int level) {
		return level == this.level
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				this.world,
				level,
				this.simpleName,
				this.name,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource withMaxLevel(int level) {
		return level <= this.level
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				this.world,
				level,
				this.simpleName,
				this.name,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource withEntityAnchor(EntityAnchorArgumentType.EntityAnchor anchor) {
		return anchor == this.entityAnchor
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				this.world,
				this.level,
				this.simpleName,
				this.name,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				anchor
			);
	}

	public ServerCommandSource withWorld(ServerWorld world) {
		return world == this.world
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				world,
				this.level,
				this.simpleName,
				this.name,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource withLookingAt(Entity entity, EntityAnchorArgumentType.EntityAnchor anchor) throws CommandSyntaxException {
		return this.withLookingAt(anchor.positionAt(entity));
	}

	public ServerCommandSource withLookingAt(Vec3d position) throws CommandSyntaxException {
		Vec3d vec3d = this.entityAnchor.positionAt(this);
		double d = position.x - vec3d.x;
		double e = position.y - vec3d.y;
		double f = position.z - vec3d.z;
		double g = (double)MathHelper.sqrt(d * d + f * f);
		float h = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI)));
		float i = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F);
		return this.withRotation(new Vec2f(h, i));
	}

	public Text getDisplayName() {
		return this.name;
	}

	public String getName() {
		return this.simpleName;
	}

	@Override
	public boolean hasPermissionLevel(int level) {
		return this.level >= level;
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

	public void sendFeedback(Text message, boolean broadcastToOps) {
		if (this.output.shouldReceiveFeedback() && !this.silent) {
			this.output.sendMessage(message);
		}

		if (broadcastToOps && this.output.shouldBroadcastConsoleToOps() && !this.silent) {
			this.sendToOps(message);
		}
	}

	private void sendToOps(Text message) {
		Text text = new TranslatableText("chat.type.admin", this.getDisplayName(), message).formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC});
		if (this.minecraftServer.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
			for (ServerPlayerEntity serverPlayerEntity : this.minecraftServer.getPlayerManager().getPlayerList()) {
				if (serverPlayerEntity != this.output && this.minecraftServer.getPlayerManager().isOperator(serverPlayerEntity.getGameProfile())) {
					serverPlayerEntity.sendMessage(text);
				}
			}
		}

		if (this.output != this.minecraftServer && this.minecraftServer.getGameRules().getBoolean(GameRules.LOG_ADMIN_COMMANDS)) {
			this.minecraftServer.sendMessage(text);
		}
	}

	public void sendError(Text message) {
		if (this.output.shouldTrackOutput() && !this.silent) {
			this.output.sendMessage(new LiteralText("").append(message).formatted(Formatting.RED));
		}
	}

	public void onCommandComplete(CommandContext<ServerCommandSource> context, boolean success, int result) {
		if (this.resultConsumer != null) {
			this.resultConsumer.onCommandComplete(context, success, result);
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
		return Registry.SOUND_EVENT.getIds();
	}

	@Override
	public Stream<Identifier> getRecipeIds() {
		return this.minecraftServer.getRecipeManager().keys();
	}

	@Override
	public CompletableFuture<Suggestions> getCompletions(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
		return null;
	}
}
