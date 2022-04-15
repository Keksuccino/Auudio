package de.keksuccino.auudio;

import de.keksuccino.auudio.audio.AudioHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("auudio")
public class Auudio {

	public static final String VERSION = "1.0.3";

	protected static final Logger LOGGER = LogManager.getLogger("auudio/Auudio");

	public Auudio() {
		try {

			//Check if mod was loaded client- or server-side
			if (FMLEnvironment.dist == Dist.CLIENT) {

				AudioHandler.init();

			} else {
				LOGGER.warn("WARNING: Auudio is a client mod and has no effect when loaded on a server!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void registerPostReloadTask(Runnable task) {
		AudioHandler.postReloadingTasks.add(task);
	}

}
