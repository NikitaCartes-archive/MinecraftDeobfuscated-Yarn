package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.server.command.CommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ClientCommandSource implements CommandSource {
	private final ClientPlayNetworkHandler networkHandler;
	private final MinecraftClient client;
	private int completionId = -1;
	private CompletableFuture<Suggestions> pendingCompletion;

	public ClientCommandSource(ClientPlayNetworkHandler networkHandler, MinecraftClient client) {
		this.networkHandler = networkHandler;
		this.client = client;
	}

	@Override
	public Collection<String> getPlayerNames() {
		List<String> list = Lists.<String>newArrayList();

		for (PlayerListEntry playerListEntry : this.networkHandler.getPlayerList()) {
			list.add(playerListEntry.getProfile().getName());
		}

		return list;
	}

	@Override
	public Collection<String> getEntitySuggestions() {
		return (Collection<String>)(this.client.crosshairTarget != null && this.client.crosshairTarget.getType() == HitResult.Type.ENTITY
			? Collections.singleton(((EntityHitResult)this.client.crosshairTarget).getEntity().getUuidAsString())
			: Collections.emptyList());
	}

	@Override
	public Collection<String> getTeamNames() {
		return this.networkHandler.getWorld().getScoreboard().getTeamNames();
	}

	@Override
	public Collection<Identifier> getSoundIds() {
		return this.client.getSoundManager().getKeys();
	}

	@Override
	public Stream<Identifier> getRecipeIds() {
		return this.networkHandler.getRecipeManager().keys();
	}

	@Override
	public boolean hasPermissionLevel(int level) {
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		return clientPlayerEntity != null ? clientPlayerEntity.hasPermissionLevel(level) : level == 0;
	}

	@Override
	public CompletableFuture<Suggestions> getCompletions(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
		if (this.pendingCompletion != null) {
			this.pendingCompletion.cancel(false);
		}

		this.pendingCompletion = new CompletableFuture();
		int i = ++this.completionId;
		this.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(i, context.getInput()));
		return this.pendingCompletion;
	}

	private static String format(double d) {
		return String.format(Locale.ROOT, "%.2f", d);
	}

	private static String format(int i) {
		return Integer.toString(i);
	}

	@Override
	public Collection<CommandSource.RelativePosition> getBlockPositionSuggestions() {
		HitResult hitResult = this.client.crosshairTarget;
		if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
			return Collections.singleton(new CommandSource.RelativePosition(format(blockPos.getX()), format(blockPos.getY()), format(blockPos.getZ())));
		} else {
			return CommandSource.super.getBlockPositionSuggestions();
		}
	}

	@Override
	public Collection<CommandSource.RelativePosition> getPositionSuggestions() {
		HitResult hitResult = this.client.crosshairTarget;
		if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
			Vec3d vec3d = hitResult.getPos();
			return Collections.singleton(new CommandSource.RelativePosition(format(vec3d.x), format(vec3d.y), format(vec3d.z)));
		} else {
			return CommandSource.super.getPositionSuggestions();
		}
	}

	public void onCommandSuggestions(int completionId, Suggestions suggestions) {
		if (completionId == this.completionId) {
			this.pendingCompletion.complete(suggestions);
			this.pendingCompletion = null;
			this.completionId = -1;
		}
	}
}
