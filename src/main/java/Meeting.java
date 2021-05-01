import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class Meeting {
	public static HashSet<String> courses;
	public static List<Meeting> allMeetings;
	public String course;
	public String link;
	public Date time;
	public String description;

	public Meeting(String course, String link, Date time, String description){
		courses.add(course);
		this.link = link;
		this.time = time;
		this.description = description;
	}

	public boolean equals(Object other) {
		if(!(other instanceof Meeting))
			return false;

		Meeting meeting = (Meeting)other;
		return meeting.course.equals(this.course) && meeting.time.equals(this.time);
	}
}