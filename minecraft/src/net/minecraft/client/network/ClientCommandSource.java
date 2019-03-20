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
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.packet.RequestCommandCompletionsC2SPacket;
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

	public ClientCommandSource(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
		this.networkHandler = clientPlayNetworkHandler;
		this.client = minecraftClient;
	}

	@Override
	public Collection<String> getPlayerNames() {
		List<String> list = Lists.<String>newArrayList();

		for (ScoreboardEntry scoreboardEntry : this.networkHandler.getScoreboardEntries()) {
			list.add(scoreboardEntry.getProfile().getName());
		}

		return list;
	}

	@Override
	public Collection<String> method_9269() {
		return (Collection<String>)(this.client.hitResult != null && this.client.hitResult.getType() == HitResult.Type.ENTITY
			? Collections.singleton(((EntityHitResult)this.client.hitResult).getEntity().getUuidAsString())
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
	public boolean hasPermissionLevel(int i) {
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		return clientPlayerEntity != null ? clientPlayerEntity.allowsPermissionLevel(i) : i == 0;
	}

	@Override
	public CompletableFuture<Suggestions> getCompletions(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (this.pendingCompletion != null) {
			this.pendingCompletion.cancel(false);
		}

		this.pendingCompletion = new CompletableFuture();
		int i = ++this.completionId;
		this.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(i, commandContext.getInput()));
		return this.pendingCompletion;
	}

	private static String formatDouble(double d) {
		return String.format(Locale.ROOT, "%.2f", d);
	}

	private static String formatInt(int i) {
		return Integer.toString(i);
	}

	@Override
	public Collection<CommandSource.RelativePosition> method_17771() {
		HitResult hitResult = this.client.hitResult;
		if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
			return Collections.singleton(new CommandSource.RelativePosition(formatInt(blockPos.getX()), formatInt(blockPos.getY()), formatInt(blockPos.getZ())));
		} else {
			return CommandSource.super.method_17771();
		}
	}

	@Override
	public Collection<CommandSource.RelativePosition> method_17772() {
		HitResult hitResult = this.client.hitResult;
		if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
			Vec3d vec3d = hitResult.getPos();
			return Collections.singleton(new CommandSource.RelativePosition(formatDouble(vec3d.x), formatDouble(vec3d.y), formatDouble(vec3d.z)));
		} else {
			return CommandSource.super.method_17772();
		}
	}

	public void method_2931(int i, Suggestions suggestions) {
		if (i == this.completionId) {
			this.pendingCompletion.complete(suggestions);
			this.pendingCompletion = null;
			this.completionId = -1;
		}
	}
}
