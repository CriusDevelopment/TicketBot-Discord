package tr.com.efekurban.ticket.listener;

import me.koply.kcommando.internal.annotations.HandleButton;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import tr.com.efekurban.ticket.TicketBot;

import java.util.Collection;
import java.util.Collections;

public class ButtonListener extends ListenerAdapter {

    private static final Collection<Permission> VIEW_PERMISSION = Collections.singleton(Permission.VIEW_CHANNEL);

    private final Category TICKET_CATEGORY = TicketBot.INSTANCE.getJda().getCategoryById("808543051713609759");
    private final Category ARCHIVE_CATEGORY = TicketBot.INSTANCE.getJda().getCategoryById("808735231740870736");

    @HandleButton("create-ticket")
    public void ticketButton(ButtonInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;

        // check ticket ban role
        if (member.getRoles().stream().anyMatch(role -> role.getId().equals("808535968830849044"))) {
            event.getInteraction().deferReply(true).complete().sendMessage(":x: You are banned from ticket system.").queue();
            return;
        }

        // check if the user already has a ticket
        if (TICKET_CATEGORY.getChannels().stream().anyMatch(channel -> channel.getName().equalsIgnoreCase("ticket-" + member.getId()))) {
            event.getInteraction().deferReply(true).complete().sendMessage(":x: You already have a ticket. Please use the existing one. :x:").queue();
            return;
        }

        TICKET_CATEGORY.createTextChannel("ticket-" + member.getId())
                .addRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(), null, VIEW_PERMISSION)
                .addMemberPermissionOverride(event.getMember().getIdLong(), VIEW_PERMISSION, null)
                .submit().whenComplete((textChannel, throwable) -> {
                    MessageCreateData message = new MessageCreateBuilder()
                            .addContent(event.getMember().getAsMention())
                            .addContent("\n\n")
                            .addContent("Click the button below to close this ticket.\nPlease do not ping anyone and be patient.")
                            .addActionRow(Button.primary("delete-ticket", "Close this ticket")).build();

                    textChannel.sendMessage(message).queue();
                });

        event.getInteraction().deferReply(true).complete().sendMessage(":white_check_mark: Your ticket created successfully.").queue();
    }

    @HandleButton("delete-ticket")
    public void deleteButton(ButtonInteractionEvent event) {
        event.getInteraction().deferReply(true).complete().sendMessage("Ticket closed successfully.").queue();

        event.getChannel().asTextChannel().getManager()
                .putRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(), null, VIEW_PERMISSION).setParent(ARCHIVE_CATEGORY).queue();

        event.getMessageChannel().sendMessage("Ticket closed.").queue();
    }

}
