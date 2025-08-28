package org.example.bot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class Bot extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(Bot.class);
    private UserRepository userRepository;

    public Bot(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // Случай, когда бот считывает своё же сообщение или сообщения других ботов
        if (event.getAuthor().isBot()) { return; }

        Message message = event.getMessage();
        String context = message.getContentRaw();
        MessageChannel channel = event.getChannel();

        if (context.equals("!ping")) {
            channel.sendMessage("Pong!").queue();
        }

        if (context.equals("!online")) {
            int playersOnline = userRepository.getPlayersOnline();
            channel.sendMessage("\uD83D\uDFE2 Текущее количество игроков на сервере: " + playersOnline).queue();
        }

        if (context.equals("!achievements")) {
            LinkedHashMap<String, Integer> map = userRepository.getTopPlayersByAchievements();

            String messageToSend = "\uD83C\uDFC6 ТОП-5 ИГРОКОВ ПО ДОСТИЖЕНИЯМ \uD83C\uDFC6\n\n";
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                messageToSend += entry.getKey() + " - " + entry.getValue() + " достижений\n";
            }
            channel.sendMessage(messageToSend).queue();
        }

        if (context.equals("!stats")) {

        }
    }
}
