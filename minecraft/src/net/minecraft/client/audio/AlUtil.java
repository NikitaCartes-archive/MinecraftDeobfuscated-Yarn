package net.minecraft.client.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;

@Environment(EnvType.CLIENT)
public class AlUtil {
	private static final Logger LOGGER = LogManager.getLogger();

	private static String getErrorMessage(int i) {
		switch (i) {
			case 40961:
				return "Invalid name parameter.";
			case 40962:
				return "Invalid parameter.";
			case 40963:
				return "Invalid enumerated parameter value.";
			case 40964:
				return "Illegal call.";
			case 40965:
				return "Unable to allocate memory.";
			default:
				return "An unrecognized error occurred.";
		}
	}

	static boolean checkErrors(String string) {
		int i = AL10.alGetError();
		if (i != 0) {
			LOGGER.error("{}: {}", string, getErrorMessage(i));
			return true;
		} else {
			return false;
		}
	}

	private static String method_20052(int i) {
		switch (i) {
			case 40961:
				return "Invalid device.";
			case 40962:
				return "Invalid context.";
			case 40963:
				return "Illegal enum.";
			case 40964:
				return "Invalid value.";
			case 40965:
				return "Unable to allocate memory.";
			default:
				return "An unrecognized error occurred.";
		}
	}

	static boolean method_20051(long l, String string) {
		int i = ALC10.alcGetError(l);
		if (i != 0) {
			LOGGER.error("{}{}: {}", string, l, method_20052(i));
			return true;
		} else {
			return false;
		}
	}

	static int getFormatId(AudioFormat audioFormat) {
		Encoding encoding = audioFormat.getEncoding();
		int i = audioFormat.getChannels();
		int j = audioFormat.getSampleSizeInBits();
		if (encoding.equals(Encoding.PCM_UNSIGNED) || encoding.equals(Encoding.PCM_SIGNED)) {
			if (i == 1) {
				if (j == 8) {
					return 4352;
				}

				if (j == 16) {
					return 4353;
				}
			} else if (i == 2) {
				if (j == 8) {
					return 4354;
				}

				if (j == 16) {
					return 4355;
				}
			}
		}

		throw new IllegalArgumentException("Invalid audio format: " + audioFormat);
	}
}
