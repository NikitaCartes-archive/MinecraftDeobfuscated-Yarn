package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.CommandArgumentSigner;
import net.minecraft.network.message.MessageSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

/**
 * Represents a command source used on server side.
 * 
 * @see MinecraftServer#getCommandSource()
 * @see Entity#getCommandSource()
 */
public class ServerCommandSource implements CommandSource {
	public static final SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("permissions.requires.player"));
	public static final SimpleCommandExceptionType REQUIRES_ENTITY_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("permissions.requires.entity"));
	private final CommandOutput output;
	private final Vec3d position;
	private final ServerWorld world;
	private final int level;
	private final String name;
	private final Text displayName;
	private final MinecraftServer server;
	private final boolean silent;
	@Nullable
	private final Entity entity;
	@Nullable
	private final ResultConsumer<ServerCommandSource> resultConsumer;
	private final EntityAnchorArgumentType.EntityAnchor entityAnchor;
	private final Vec2f rotation;
	private final CommandArgumentSigner signer;

	public ServerCommandSource(
		CommandOutput output, Vec3d pos, Vec2f rot, ServerWorld world, int level, String name, Text displayName, MinecraftServer server, @Nullable Entity entity
	) {
		this(output, pos, rot, world, level, name, displayName, server, entity, false, (context, success, result) -> {
		}, EntityAnchorArgumentType.EntityAnchor.FEET, CommandArgumentSigner.NONE);
	}

	protected ServerCommandSource(
		CommandOutput output,
		Vec3d pos,
		Vec2f rot,
		ServerWorld world,
		int level,
		String name,
		Text displayName,
		MinecraftServer server,
		@Nullable Entity entity,
		boolean silent,
		@Nullable ResultConsumer<ServerCommandSource> consumer,
		EntityAnchorArgumentType.EntityAnchor entityAnchor,
		CommandArgumentSigner signer
	) {
		this.output = output;
		this.position = pos;
		this.world = world;
		this.silent = silent;
		this.entity = entity;
		this.level = level;
		this.name = name;
		this.displayName = displayName;
		this.server = server;
		this.resultConsumer = consumer;
		this.entityAnchor = entityAnchor;
		this.rotation = rot;
		this.signer = signer;
	}

	public ServerCommandSource withOutput(CommandOutput output) {
		return this.output == output
			? this
			: new ServerCommandSource(
				output,
				this.position,
				this.rotation,
				this.world,
				this.level,
				this.name,
				this.displayName,
				this.server,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor,
				this.signer
			);
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
				this.server,
				entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor,
				this.signer
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
				this.name,
				this.displayName,
				this.server,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor,
				this.signer
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
				this.name,
				this.displayName,
				this.server,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor,
				this.signer
			);
	}

	public ServerCommandSource withConsumer(ResultConsumer<ServerCommandSource> consumer) {
		return Objects.equals(this.resultConsumer, consumer)
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				this.world,
				this.level,
				this.name,
				this.displayName,
				this.server,
				this.entity,
				this.silent,
				consumer,
				this.entityAnchor,
				this.signer
			);
	}

	public ServerCommandSource mergeConsumers(ResultConsumer<ServerCommandSource> consumer, BinaryOperator<ResultConsumer<ServerCommandSource>> merger) {
		ResultConsumer<ServerCommandSource> resultConsumer = (ResultConsumer<ServerCommandSource>)merger.apply(this.resultConsumer, consumer);
		return this.withConsumer(resultConsumer);
	}

	public ServerCommandSource withSilent() {
		return !this.silent && !this.output.cannotBeSilenced()
			? new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				this.world,
				this.level,
				this.name,
				this.displayName,
				this.server,
				this.entity,
				true,
				this.resultConsumer,
				this.entityAnchor,
				this.signer
			)
			: this;
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
				this.name,
				this.displayName,
				this.server,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor,
				this.signer
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
				this.name,
				this.displayName,
				this.server,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor,
				this.signer
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
				this.name,
				this.displayName,
				this.server,
				this.entity,
				this.silent,
				this.resultConsumer,
				anchor,
				this.signer
			);
	}

	public ServerCommandSource withWorld(ServerWorld world) {
		if (world == this.world) {
			return this;
		} else {
			double d = DimensionType.getCoordinateScaleFactor(this.world.getDimension(), world.getDimension());
			Vec3d vec3d = new Vec3d(this.position.x * d, this.position.y, this.position.z * d);
			return new ServerCommandSource(
				this.output,
				vec3d,
				this.rotation,
				world,
				this.level,
				this.name,
				this.displayName,
				this.server,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor,
				this.signer
			);
		}
	}

	public ServerCommandSource withLookingAt(Entity entity, EntityAnchorArgumentType.EntityAnchor anchor) {
		return this.withLookingAt(anchor.positionAt(entity));
	}

	public ServerCommandSource withLookingAt(Vec3d position) {
		Vec3d vec3d = this.entityAnchor.positionAt(this);
		double d = position.x - vec3d.x;
		double e = position.y - vec3d.y;
		double f = position.z - vec3d.z;
		double g = Math.sqrt(d * d + f * f);
		float h = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI)));
		float i = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F);
		return this.withRotation(new Vec2f(h, i));
	}

	public ServerCommandSource withSigner(CommandArgumentSigner signer) {
		return signer == this.signer
			? this
			: new ServerCommandSource(
				this.output,
				this.position,
				this.rotation,
				this.world,
				this.level,
				this.name,
				this.displayName,
				this.server,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor,
				signer
			);
	}

	public Text getDisplayName() {
		return this.displayName;
	}

	public String getName() {
		return this.name;
	}

	public MessageSender getChatMessageSender() {
		return this.entity != null ? this.entity.asMessageSender() : MessageSender.of(this.getDisplayName());
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

	/**
	 * Gets the entity from this command source or returns null if this command source is not an entity.
	 */
	@Nullable
	public Entity getEntity() {
		return this.entity;
	}

	/**
	 * Gets the entity from this command source or throws a command syntax exception if this command source is not an entity.
	 */
	public Entity getEntityOrThrow() throws CommandSyntaxException {
		if (this.entity == null) {
			throw REQUIRES_ENTITY_EXCEPTION.create();
		} else {
			return this.entity;
		}
	}

	/**
	 * {@return the player from this command source}
	 * 
	 * @throws CommandSyntaxException if this command source is not a player
	 */
	public ServerPlayerEntity getPlayerOrThrow() throws CommandSyntaxException {
		Entity var2 = this.entity;
		if (var2 instanceof ServerPlayerEntity) {
			return (ServerPlayerEntity)var2;
		} else {
			throw REQUIRES_PLAYER_EXCEPTION.create();
		}
	}

	/**
	 * {@return the player from this command source, or {@code null} if the source is not a player}
	 */
	@Nullable
	public ServerPlayerEntity getPlayer() {
		return this.entity instanceof ServerPlayerEntity serverPlayerEntity ? serverPlayerEntity : null;
	}

	public boolean isExecutedByPlayer() {
		return this.entity instanceof ServerPlayerEntity;
	}

	public Vec2f getRotation() {
		return this.rotation;
	}

	public MinecraftServer getServer() {
		return this.server;
	}

	public EntityAnchorArgumentType.EntityAnchor getEntityAnchor() {
		return this.entityAnchor;
	}

	public CommandArgumentSigner getSigner() {
		return this.signer;
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
		Text text = Text.translatable("chat.type.admin", this.getDisplayName(), message).formatted(Formatting.GRAY, Formatting.ITALIC);
		if (this.server.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
			for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
				if (serverPlayerEntity != this.output && this.server.getPlayerManager().isOperator(serverPlayerEntity.getGameProfile())) {
					serverPlayerEntity.sendMessage(text);
				}
			}
		}

		if (this.output != this.server && this.server.getGameRules().getBoolean(GameRules.LOG_ADMIN_COMMANDS)) {
			this.server.sendMessage(text);
		}
	}

	public void sendError(Text message) {
		if (this.output.shouldTrackOutput() && !this.silent) {
			this.output.sendMessage(Text.empty().append(message).formatted(Formatting.RED));
		}
	}

	public void onCommandComplete(CommandContext<ServerCommandSource> context, boolean success, int result) {
		if (this.resultConsumer != null) {
			this.resultConsumer.onCommandComplete(context, success, result);
		}
	}

	@Override
	public Collection<String> getPlayerNames() {
		return Lists.<String>newArrayList(this.server.getPlayerNames());
	}

	@Override
	public Collection<String> getTeamNames() {
		return this.server.getScoreboard().getTeamNames();
	}

	@Override
	public Collection<Identifier> getSoundIds() {
		return Registry.SOUND_EVENT.getIds();
	}

	@Override
	public Stream<Identifier> getRecipeIds() {
		return this.server.getRecipeManager().keys();
	}

	@Override
	public CompletableFuture<Suggestions> getCompletions(CommandContext<?> context) {
		return Suggestions.empty();
	}

	@Override
	public CompletableFuture<Suggestions> listIdSuggestions(
		RegistryKey<? extends Registry<?>> registryRef, CommandSource.SuggestedIdType suggestedIdType, SuggestionsBuilder builder, CommandContext<?> context
	) {
		return (CompletableFuture<Suggestions>)this.getRegistryManager().getOptional(registryRef).map(registry -> {
			this.suggestIdentifiers(registry, suggestedIdType, builder);
			return builder.buildFuture();
		}).orElseGet(Suggestions::empty);
	}

	@Override
	public Set<RegistryKey<World>> getWorldKeys() {
		return this.server.getWorldRegistryKeys();
	}

	@Override
	public DynamicRegistryManager getRegistryManager() {
		return this.server.getRegistryManager();
	}
}
