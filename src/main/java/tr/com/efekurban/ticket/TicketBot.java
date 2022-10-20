package tr.com.efekurban.ticket;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import tr.com.efekurban.ticket.listener.ButtonListener;

public class TicketBot {

    private final JDA jda;

    public static TicketBot INSTANCE;

    public static void main(String[] args) throws InterruptedException {
       new TicketBot(args[0]);
    }

    public TicketBot(String token) throws InterruptedException {
        INSTANCE = this;

        JDABuilder builder = JDABuilder.createDefault(token);

        builder.enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setActivity(Activity.watching("tickets"));

        jda = builder.build();
        jda.awaitReady();

        jda.addEventListener(new ButtonListener());

        Runtime.getRuntime().addShutdownHook(new Thread(jda::shutdown));

        /*GuildMessageChannel channel = jda.getChannelById(GuildMessageChannel.class, "1000866431417720952");
        for (Message message : channel.getHistory().retrievePast(50).complete()) {
            if (message.getMember().getUser().isBot()) {
                message.delete().queue();
            }
        }

        MessageCreateData message = new MessageCreateBuilder()
                .addContent("Click the button below to create a ticket." + "\n" +
                        "*People who use this system unnecessarily will be blocked from our ticket system forever.*")
                .addActionRow(Button.primary("create-ticket", "Create a ticket")).build();

        ticketMessageId = channel.sendMessage(message).complete().getId();*/

    }

    public JDA getJda() {
        return jda;
    }

}
