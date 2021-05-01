import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Main {
	public static void main(String[] args) throws LoginException {
		JDA jda = JDABuilder.createDefault(DiscordToken.TOKEN)
				.setActivity(Activity.playing("organizing your links"))
				.build();
		jda.addEventListener(new CommandListener());
	}
}