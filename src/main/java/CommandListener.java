import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class CommandListener extends ListenerAdapter {
	public final char COMMAND_CHARACTER = '!';
	private EmbedBuilder messageReply;

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		if(event.getAuthor().isBot()) // Ignore bots to avoid infinite loops
			return;

		if(event.getMessage().getContentRaw().charAt(0) != COMMAND_CHARACTER)
			return;

		String author = event.getAuthor().getName();
		System.out.println("We received a message from " +
				event.getAuthor().getName() + ": " +
				event.getMessage().getContentDisplay()
		);

		String[] message = event.getMessage().getContentRaw().split(" ");

		messageReply = new EmbedBuilder().setColor(Color.green);
		switch(message[0]){
			case COMMAND_CHARACTER + "ping": messageReply.setTitle("pong!"); break;
			case COMMAND_CHARACTER + "help": helpCommand(); break;
			case COMMAND_CHARACTER + "create": createCommand(message); break;
			case COMMAND_CHARACTER + "addmeeting": addCommand(message); break;
			case COMMAND_CHARACTER + "schedule": scheduleCommand(author); break;
			case COMMAND_CHARACTER + "courses": coursesCommand(); break;
			case COMMAND_CHARACTER + "meetings": meetingsCommand(message); break;
			default: messageReply.setTitle("I don't know that command");
		}

		event.getChannel().sendMessage(new MessageBuilder().setEmbed(messageReply.build()).build()).queue();
	}

	private void helpCommand(){
		messageReply.setTitle("Command List")
				.addField(COMMAND_CHARACTER + "help", "displays this command list", false)
				.addField(COMMAND_CHARACTER + "create [course code] [description]", "adds a course into the system.", false)
				.addField(COMMAND_CHARACTER + "addmeeting [course code] [link url] [time] [description]", "adds a weekly meeting for this course and save the link data.", false)
				.addField(COMMAND_CHARACTER + "schedule", "displays all of the upcoming meetings in the next day for the user.", false)
				.addField(COMMAND_CHARACTER + "courses", "displays all of the course codes entered in the system.", false)
				.addField(COMMAND_CHARACTER + "meetings [course code]", "displays all of the registered meetings associated with a course.", false);
		//);
	}

	// !create [course code] [description]
	// !create MTH240 Calculus 2
	// creates a course with the following code and description.
	private void createCommand(String[] message){
		if(message.length < 2) {
			messageReply.setTitle("I don't know what you mean!");
			return;
		}

		if(Course.courses.containsKey(message[1])) {
			messageReply.setTitle(message[1] + " is a course that already exists!"); // May want to call meetingsCommand();
			return;
		}

		StringBuilder description = new StringBuilder();
		for(int i = 2; i < message.length; i++)
			description.append(message[i]).append(" ");
		Course.createCourse(message[1], description.toString());
		if(description.toString().isEmpty()) {
			messageReply.setTitle("Created course " + message[1] + "!");
			return;
		}
		messageReply.setTitle("Created course " + message[1] + "! Description: '" + description + "'");
	}

	// !subscribe [course code]
	// !subscribe MTH240
	// subscribes the user to the course
	private String subscribeCommand(String author, String[] message){
		if(message.length < 2)
			return "Enter the course code of the course you want to subscribe to. \n !subscribe MTH240";
		Course course = Course.courses.get(message[1]);
		if(course != null)
			course.subscribe(author);
		else
			return "That course doesn't exist.";
		return author + " was subscribed to " + message[1];
	}

	// !unsubscribe [course code]
	// !unsubscribe MTH240
	// unsubscribes the user from the course
	private String unsubscribeCommand(String author, String[] message){
		if(message.length < 2)
			return "Enter the course code of the course you want to unsubscribe from. \n !unsubscribe MTH240";
		Course course = Course.courses.get(message[1]);
		if(course != null)
			course.unsubscribe(author);
		else
			return "That course doesn't exist.";
		return author + " was unsubscribed from " + message[1];
	}

	// !addmeeting [course code] [link url] [time] [description]
	// Adds a weekly meeting to the course code
	private EmbedBuilder addCommand(String[] message){
		if(message.length < 4)
			return messageReply.setDescription("Enter in this formula: !add [course code] [link url] [time as DD/MM/YYYY] [description]");

		StringBuilder description = new StringBuilder();
		for(int i = 4; i < message.length; i++)
			description.append(message[i]).append(" ");

		Date time;
		try {
			time = new SimpleDateFormat("dd/mm/yyyy").parse(message[3]);
		} catch (ParseException e) {
			return messageReply.setDescription("Enter the time in the format: [dd/mm/yyyy]");
		}

		Meeting meeting = new Meeting(message[1], message[2], time, description.toString());
		return messageReply.setDescription("Added weekly " + message[1] + " meeting at " + meeting.time.toString());
	}

	// !schedule
	// Displays the upcoming zoom meetings for the person who ran the command
	private String scheduleCommand(String username){
		HashSet<Course> subscribedCourses = new HashSet<>();
		Course.courses.forEach((key, value) -> {
			if(value.subscribedUsers.contains(username))
				subscribedCourses.add(value);
		});

		StringBuilder schedule = new StringBuilder();
		for(Course c : subscribedCourses){
			for(Meeting m : c.meetings) {
				if (TimeUnit.DAYS.convert(m.time.getTime() - Calendar.getInstance().getTime().getTime(), TimeUnit.MILLISECONDS) <= 1) {
					schedule.append(m.toString()).append("\n");
				}
			}
		}
		if(subscribedCourses.isEmpty() || schedule.toString().isEmpty())
			return "You are not subscribed to any courses.";
		return schedule.toString();
	}

	// !courses
	// shows all of the course codes in the system
	private void coursesCommand(){
		StringBuilder courses = new StringBuilder();
		for(Course c : Course.courses.values()){
			courses.append(c.code).append(", ");
		}
		if(courses.toString().isEmpty()) {
			messageReply.setTitle("There are no courses initialized yet. Use !create [course code] to add a course.");
			return;
		}
		messageReply.addField("These are all of the courses in the system: \n", courses.toString().substring(0, courses.toString().length() - 2), false);
	}

	// !meetings [course code]
	// shows all of the meetings associated with a course
	private EmbedBuilder meetingsCommand(String[] message){
		if(message.length < 2)
			return messageReply.setTitle("Enter the code of the course that you want to list. !meetings [course code]");

		StringBuilder schedule = new StringBuilder();
		Course course = Course.courses.get(message[1]);
		if(course == null)
			return messageReply.setTitle("That course is not in the system.");

		for(Meeting m : course.meetings) {
			if (TimeUnit.DAYS.convert(m.time.getTime() - Calendar.getInstance().getTime().getTime(), TimeUnit.MILLISECONDS) <= 1) {
				schedule.append(m.toString()).append("\n");
			}
		}
		if(schedule.toString().isEmpty())
			return messageReply.setDescription("There are no meetings for " + course.code);
		return messageReply.setTitle("Links for " + course.code).setDescription(schedule.toString());
	}
}