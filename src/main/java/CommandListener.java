import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class CommandListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		if(event.getAuthor().isBot()) // Ignore bots to avoid infinite loops
			return;

		String author = event.getAuthor().getName();
		System.out.println("We received a message from " +
				event.getAuthor().getName() + ": " +
				event.getMessage().getContentDisplay()
		);

		String[] message = event.getMessage().getContentRaw().split(" ");

		if(message[0].equals("!ping")){
			event.getChannel().sendMessage("Pong!").queue();
			return;
		}

		String reply;
		switch(message[0]){
			case "!create": reply = createCommand(message); break;
			case "!subscribe": reply = subscribeCommand(author, message); break;
			case "!addmeeting": reply = addCommand(message); break;
			case "!schedule": reply = scheduleCommand(author); break;
			case "!courses": reply = coursesCommand(); break;
			case "!meetings": reply = meetingsCommand(message); break;
			default: reply = "";
		}

		event.getChannel().sendMessage(reply).queue();
	}

	// !create [course code] [description]
	// !create MTH240 Calculus 2
	// creates a course with the following code and description.
	private String createCommand(String[] message){
		return "create";
	}

	// !subscribe [course code]
	// !subscribe MTH240
	// subscribes the user to the course
	private String subscribeCommand(String author, String[] message){
		return "subscribe";
	}

	// !add [course code] [link url] [time] [description]
	// Adds a weekly meeting to the course code
	private String addCommand(String[] message){
		return "add";
	}

	// !schedule
	// Displays the upcoming zoom meetings for the person who ran the command
	private String scheduleCommand(String username){
		return "schedule";
	}

	// !courses
	// shows all of the course codes in the system
	private String coursesCommand(){
		return "courses";
	}

	// !meetings [course code]
	// shows all of the meetings associated with a course
	private String meetingsCommand(String[] message){
		return "meetings";
	}
}