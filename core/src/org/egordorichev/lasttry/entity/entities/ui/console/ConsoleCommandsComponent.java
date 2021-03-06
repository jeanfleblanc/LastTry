package org.egordorichev.lasttry.entity.entities.ui.console;

import org.egordorichev.lasttry.entity.component.Component;
import org.egordorichev.lasttry.entity.entities.ui.console.commands.*;

import java.util.ArrayList;

/**
 * Stores all console commands
 */
public class ConsoleCommandsComponent extends Component {
	/**
	 * Commands list
	 */
	public ArrayList<ConsoleCommand> commands = new ArrayList<>();

	public ConsoleCommandsComponent() {
		this.commands.add(new HelpCommand());
		this.commands.add(new GiveCommand());
	}
}