package net.minecraft.network;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.text.Text;

public record DisconnectionInfo(Text reason, Optional<Path> report, Optional<URI> bugReportLink) {
	public DisconnectionInfo(Text reason) {
		this(reason, Optional.empty(), Optional.empty());
	}
}
