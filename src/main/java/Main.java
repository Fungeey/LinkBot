import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import javax.security.auth.login.LoginException;
import java.util.List;

public class Main {
	public static void main(String[] args) throws LoginException {
		JDA jda = JDABuilder.createDefault(DiscordToken.TOKEN).build();
		jda.addEventListener(new CommandListener());

		List<Meeting> allMeetings;
	}
}