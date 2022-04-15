package de.keksuccino.auudio;

import de.keksuccino.auudio.audio.AudioHandler;
import de.keksuccino.auudio.util.event.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Auudio implements ModInitializer {

	public static final String VERSION = "1.0.3";

	protected static final Logger LOGGER = LogManager.getLogger("auudio/Auudio");

	public static final EventHandler EVENT_HANDLER = new EventHandler();

	@Override
	public void onInitialize() {

		try {

			//Check if mod was loaded client- or server-side
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {

				AudioHandler.init();

				//TODO remove debug
//				EVENT_HANDLER.registerEventsFrom(new Test());

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
