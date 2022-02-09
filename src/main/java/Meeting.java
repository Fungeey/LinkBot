import java.util.ArrayList;
import java.util.Date;

public class Meeting {
	public Course course;
	public String link;
	public Date time;
	public String description;

	public Meeting(String course, String link, Date time, String description){
		if(Course.courses.containsKey(course))
			this.course = Course.courses.get(course);
		else
			this.course = new Course(course);

		this.course.addMeeting(this);
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

	@Override
	public String toString() {
		return "[" + course.code + " " + description + " @ " + time.toString() + "](" + link + ")";
	}
}