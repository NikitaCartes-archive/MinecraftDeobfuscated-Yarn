package net.minecraft;

import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.text.Text;

public record class_9812(Text reason, Optional<Path> report, Optional<String> bugReportLink) {
	public class_9812(Text text) {
		this(text, Optional.empty(), Optional.empty());
	}
}
