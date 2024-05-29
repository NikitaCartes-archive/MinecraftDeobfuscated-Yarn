package net.minecraft.data.report;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.network.NetworkState;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.network.state.HandshakeStates;
import net.minecraft.network.state.LoginStates;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.network.state.QueryStates;

public class PacketReportProvider implements DataProvider {
	private final DataOutput output;

	public PacketReportProvider(DataOutput output) {
		this.output = output;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		Path path = this.output.resolvePath(DataOutput.OutputType.REPORTS).resolve("packets.json");
		return DataProvider.writeToPath(writer, this.toJson(), path);
	}

	private JsonElement toJson() {
		JsonObject jsonObject = new JsonObject();
		((Map)Stream.of(
					HandshakeStates.C2S_FACTORY,
					QueryStates.S2C_FACTORY,
					QueryStates.C2S_FACTORY,
					LoginStates.S2C_FACTORY,
					LoginStates.C2S_FACTORY,
					ConfigurationStates.S2C_FACTORY,
					ConfigurationStates.C2S_FACTORY,
					PlayStateFactories.S2C,
					PlayStateFactories.C2S
				)
				.collect(Collectors.groupingBy(NetworkState.Factory::phase)))
			.forEach((phase, states) -> {
				JsonObject jsonObject2 = new JsonObject();
				jsonObject.add(phase.getId(), jsonObject2);
				states.forEach(state -> {
					JsonObject jsonObject2x = new JsonObject();
					jsonObject2.add(state.side().getName(), jsonObject2x);
					state.forEachPacketType((packetType, protocolId) -> {
						JsonObject jsonObject2xx = new JsonObject();
						jsonObject2xx.addProperty("protocol_id", protocolId);
						jsonObject2x.add(packetType.id().toString(), jsonObject2xx);
					});
				});
			});
		return jsonObject;
	}

	@Override
	public String getName() {
		return "Packet Report";
	}
}
