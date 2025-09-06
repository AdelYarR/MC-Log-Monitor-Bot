package org.example.springservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.example.springservice.models.User;
import org.example.springservice.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscordBotService extends ListenerAdapter {

    private final UserRepository userRepository;
    private final String token;

    public DiscordBotService(UserRepository userRepository,
                             @Value("${discordbot.token}") String token) {
        this.userRepository = userRepository;
        this.token = token;
    }

    @PostConstruct
    public void init() {
        JDABuilder.createLight(token)
                .addEventListeners(this)
                .enableIntents(
                        List.of(
                                GatewayIntent.GUILD_MESSAGES,
                                GatewayIntent.MESSAGE_CONTENT
                        )
                )
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("Minecraft"))
                .build();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // Случай, когда бот считывает своё же сообщение или сообщения других ботов
        if (event.getAuthor().isBot()) {
            return;
        }

        Message message = event.getMessage();
        String context = message.getContentRaw();
        MessageChannel channel = event.getChannel();

        if (context.equals("!ping")) {
            channel.sendMessage("Pong!").queue();
        }
        else if (context.equals("!online")) {
            int playersOnline = userRepository.findByOnlineStatusTrue().size();
            channel.sendMessage("\uD83D\uDFE2 Текущее количество игроков на сервере: " + playersOnline).queue();
        }
        else if (context.equals("!achievements")) {
            List<User> users = userRepository.findTop5ByOrderByAchievementsCompleted();

            String messageToSend = "\uD83C\uDFC6 ТОП-5 ИГРОКОВ ПО ДОСТИЖЕНИЯМ \uD83C\uDFC6\n\n";
            for (User user : users) {
                messageToSend += user.getUsername() + " - " + user.getAchievementsCompleted() + " достижений\n";
            }
            channel.sendMessage(messageToSend).queue();
        }
    }
}