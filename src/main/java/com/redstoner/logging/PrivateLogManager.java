package com.redstoner.logging;

import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;
import com.redstoner.modules.ModuleLogger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PrivateLogManager {

	private static final String ISSUED_COMMAND_TEXT        = "issued server command: /";
	private static final int    ISSUED_COMMAND_TEXT_LENGTH = ISSUED_COMMAND_TEXT.length();
	private static Map<String, Module> registrar = new HashMap<>();
	private static Map<String, String> commands  = new HashMap<>();
	private static ModuleLogger logger;

	public static void initialize() {
		org.apache.logging.log4j.core.Logger logger;
		logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
		logger.addFilter(new Log4JFilter());
		PrivateLogManager.logger = new ModuleLogger("PrivateLogManager");
	}

	public static void register(Module module, String command, String replacement) {
		command = command.toLowerCase();
		registrar.put(command, module);
		commands.put(command, replacement);
		logger.info(module.getClass().getSimpleName() + " registered &e/" + command
		            + (replacement.equals("") ? "&7. Command will not be logged!"
		                                      : "&7, using replacement, &e" + replacement + "&7."));
	}

	public static void unregister(Module module) {
		String                              unregestered = "";
		Iterator<Map.Entry<String, Module>> i            = registrar.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry<String, Module> entry = i.next();
			if (entry.getValue() == module) {
				i.remove();
				commands.remove(entry.getKey());
				unregestered += "&e" + entry.getKey() + "&7, ";
			}
		}
		if (!unregestered.equals(""))
			logger.info("Unregistered " + unregestered.substring(0, unregestered.length() - 2) + "&7 for module, " + module.getClass().getSimpleName() + ".");
	}

	public static void unregister(Module module, String... toRemove) {
		String unregestered = "";
		for (int i = 0; i < toRemove.length; i++) {
			String command = toRemove[i].toLowerCase();
			registrar.remove(command);
			if (commands.remove(command) != null)
				unregestered += "&e" + command + "&7, ";
		}
		if (!unregestered.equals(""))
			logger.info(module.getClass().getSimpleName() + " unregistered " + unregestered.substring(0, unregestered.length() - 2) + "&7.");
	}

	public static boolean isHidden(String message) {
		if (message == null)
			return false;

		int index = message.indexOf(ISSUED_COMMAND_TEXT);
		if (index == -1)
			return false;

		String command = message.substring(index + ISSUED_COMMAND_TEXT_LENGTH);

		int spaceIndex = command.indexOf(" ");
		command = spaceIndex == -1 ? command.toLowerCase() : command.substring(0, spaceIndex).toLowerCase();

		String replacement = commands.get(command);
		if (replacement == null)
			return false;
		if (replacement.equals(""))
			return true;

		String player = message.substring(0, message.indexOf(" "));
		Utils.run(() -> System.out.println(replacement.replace("$s", player)));
		return true;
	}
}
