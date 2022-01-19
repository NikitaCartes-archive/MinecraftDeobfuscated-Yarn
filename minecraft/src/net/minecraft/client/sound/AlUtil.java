package net.minecraft.client.sound;

import com.mojang.logging.LogUtils;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class AlUtil {
	private static final Logger LOGGER = LogUtils.getLogger();

	private static String getErrorMessage(int errorCode) {
		switch (errorCode) {
			case 40961:
				return "Invalid name parameter.";
			case 40962:
				return "Invalid enumerated parameter value.";
			case 40963:
				return "Invalid parameter parameter value.";
			case 40964:
				return "Invalid operation.";
			case 40965:
				return "Unable to allocate memory.";
			default:
				return "An unrecognized error occurred.";
		}
	}

	static boolean checkErrors(String sectionName) {
		int i = AL10.alGetError();
		if (i != 0) {
			LOGGER.error("{}: {}", sectionName, getErrorMessage(i));
			return true;
		} else {
			return false;
		}
	}

	private static String getAlcErrorMessage(int errorCode) {
		switch (errorCode) {
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

	static boolean checkAlcErrors(long deviceHandle, String sectionName) {
		int i = ALC10.alcGetError(deviceHandle);
		if (i != 0) {
			LOGGER.error("{}{}: {}", sectionName, deviceHandle, getAlcErrorMessage(i));
			return true;
		} else {
			return false;
		}
	}

	static int getFormatId(AudioFormat format) {
		Encoding encoding = format.getEncoding();
		int i = format.getChannels();
		int j = format.getSampleSizeInBits();
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

		throw new IllegalArgumentException("Invalid audio format: " + format);
	}
}
