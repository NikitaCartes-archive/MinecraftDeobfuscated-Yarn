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
	private final Vec3d field_9817;
	private final ServerWorld field_9828;
	private final int level;
	private final String name;
	private final TextComponent field_9825;
	private final MinecraftServer minecraftServer;
	private final boolean silent;
	@Nullable
	private final Entity entity;
	private final ResultConsumer<ServerCommandSource> resultConsumer;
	private final EntityAnchorArgumentType.EntityAnchor entityAnchor;
	private final Vec2f field_9822;

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
		this.field_9817 = vec3d;
		this.field_9828 = serverWorld;
		this.silent = bl;
		this.entity = entity;
		this.level = i;
		this.name = string;
		this.field_9825 = textComponent;
		this.minecraftServer = minecraftServer;
		this.resultConsumer = resultConsumer;
		this.entityAnchor = entityAnchor;
		this.field_9822 = vec2f;
	}

	public ServerCommandSource withEntity(Entity entity) {
		return this.entity == entity
			? this
			: new ServerCommandSource(
				this.output,
				this.field_9817,
				this.field_9822,
				this.field_9828,
				this.level,
				entity.method_5477().getString(),
				entity.method_5476(),
				this.minecraftServer,
				entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource method_9208(Vec3d vec3d) {
		return this.field_9817.equals(vec3d)
			? this
			: new ServerCommandSource(
				this.output,
				vec3d,
				this.field_9822,
				this.field_9828,
				this.level,
				this.name,
				this.field_9825,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource method_9216(Vec2f vec2f) {
		return this.field_9822.equals(vec2f)
			? this
			: new ServerCommandSource(
				this.output,
				this.field_9817,
				vec2f,
				this.field_9828,
				this.level,
				this.name,
				this.field_9825,
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
				this.field_9817,
				this.field_9822,
				this.field_9828,
				this.level,
				this.name,
				this.field_9825,
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
				this.field_9817,
				this.field_9822,
				this.field_9828,
				this.level,
				this.name,
				this.field_9825,
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
				this.field_9817,
				this.field_9822,
				this.field_9828,
				i,
				this.name,
				this.field_9825,
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
				this.field_9817,
				this.field_9822,
				this.field_9828,
				i,
				this.name,
				this.field_9825,
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
				this.field_9817,
				this.field_9822,
				this.field_9828,
				this.level,
				this.name,
				this.field_9825,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				entityAnchor
			);
	}

	public ServerCommandSource method_9227(ServerWorld serverWorld) {
		return serverWorld == this.field_9828
			? this
			: new ServerCommandSource(
				this.output,
				this.field_9817,
				this.field_9822,
				serverWorld,
				this.level,
				this.name,
				this.field_9825,
				this.minecraftServer,
				this.entity,
				this.silent,
				this.resultConsumer,
				this.entityAnchor
			);
	}

	public ServerCommandSource withLookingAt(Entity entity, EntityAnchorArgumentType.EntityAnchor entityAnchor) throws CommandSyntaxException {
		return this.method_9221(entityAnchor.method_9302(entity));
	}

	public ServerCommandSource method_9221(Vec3d vec3d) throws CommandSyntaxException {
		Vec3d vec3d2 = this.entityAnchor.method_9299(this);
		double d = vec3d.x - vec3d2.x;
		double e = vec3d.y - vec3d2.y;
		double f = vec3d.z - vec3d2.z;
		double g = (double)MathHelper.sqrt(d * d + f * f);
		float h = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI)));
		float i = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F);
		return this.method_9216(new Vec2f(h, i));
	}

	public TextComponent method_9223() {
		return this.field_9825;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public boolean hasPermissionLevel(int i) {
		return this.level >= i;
	}

	public Vec3d method_9222() {
		return this.field_9817;
	}

	public ServerWorld method_9225() {
		return this.field_9828;
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

	public ServerPlayerEntity method_9207() throws CommandSyntaxException {
		if (!(this.entity instanceof ServerPlayerEntity)) {
			throw REQUIRES_PLAYER_EXCEPTION.create();
		} else {
			return (ServerPlayerEntity)this.entity;
		}
	}

	public Vec2f method_9210() {
		return this.field_9822;
	}

	public MinecraftServer getMinecraftServer() {
		return this.minecraftServer;
	}

	public EntityAnchorArgumentType.EntityAnchor getEntityAnchor() {
		return this.entityAnchor;
	}

	public void method_9226(TextComponent textComponent, boolean bl) {
		if (this.output.sendCommandFeedback() && !this.silent) {
			this.output.method_9203(textComponent);
		}

		if (bl && this.output.shouldBroadcastConsoleToOps() && !this.silent) {
			this.method_9212(textComponent);
		}
	}

	private void method_9212(TextComponent textComponent) {
		TextComponent textComponent2 = new TranslatableTextComponent("chat.type.admin", this.method_9223(), textComponent)
			.applyFormat(new TextFormat[]{TextFormat.field_1080, TextFormat.field_1056});
		if (this.minecraftServer.getGameRules().getBoolean("sendCommandFeedback")) {
			for (ServerPlayerEntity serverPlayerEntity : this.minecraftServer.method_3760().getPlayerList()) {
				if (serverPlayerEntity != this.output && this.minecraftServer.method_3760().isOperator(serverPlayerEntity.getGameProfile())) {
					serverPlayerEntity.method_9203(textComponent2);
				}
			}
		}

		if (this.output != this.minecraftServer && this.minecraftServer.getGameRules().getBoolean("logAdminCommands")) {
			this.minecraftServer.method_9203(textComponent2);
		}
	}

	public void method_9213(TextComponent textComponent) {
		if (this.output.shouldTrackOutput() && !this.silent) {
			this.output.method_9203(new StringTextComponent("").append(textComponent).applyFormat(TextFormat.field_1061));
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
		return this.minecraftServer.method_3845().getTeamNames();
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
