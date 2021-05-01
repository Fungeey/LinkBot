import java.util.ArrayList;
import java.util.HashMap;

public class Course {
	public static HashMap<String, Course> courses = new HashMap<>();
	public ArrayList<String> subscribedUsers;
	public ArrayList<Meeting> meetings;
	String code;
	String description;

	public Course(String code, String description){
		this.code = code;
		this.description =  description;
		this.subscribedUsers = new ArrayList<>();
	}

	public Course(String code){
		this(code, "");
	}

	public static void createCourse(String code, String description){
		courses.put(code, new Course(code, description));
	}

	public void subscribe(String user){
		subscribedUsers.add(user);
	}

	public void unsubscribe(String user){
		subscribedUsers.remove(user);
	}

	public void addMeeting(Meeting meeting){
		if(meetings == null)
			meetings = new ArrayList<>();
		meetings.add(meeting);
	}

	public void removeMeeting(Meeting meeting){
		if(meetings != null)
		meetings.remove(meeting);
	}
}
