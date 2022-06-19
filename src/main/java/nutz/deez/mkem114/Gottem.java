package nutz.deez.mkem114;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;

import static java.lang.Integer.parseInt;
import static java.lang.String.join;
import static java.lang.System.getenv;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Locality.GROUP;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

public class Gottem extends AbilityBot {

    public static final String BOT_USERNAME = getenv("TG_BOT_USERNAME"); // must end in "bot" (not case sensitive)
    public static final int CREATOR_ID = parseInt(getenv("TG_CREATOR_ID"));
    public static final DallEdeGenService dallEmemeZLimiter = new DallEdeGenService();

    public Gottem() {
        super(getenv("TG_BOT_TOKEN"), BOT_USERNAME);
    }

    @Override
    public long creatorId() {
        return CREATOR_ID;
    }

    public Ability salutations() {
        return Ability
                .builder()
                .name("hello")
                .info("politely greets you back")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.send("OI! wut u want dere cunt?!", ctx.chatId()))
                .build();
    }

    public Ability dallDEEz() {
        return Ability
                .builder()
                .name("meartdumb")
                .info("uses the power of AI driven meme learning to generate impactful business images")
                .locality(GROUP)
                .privacy(PUBLIC)
                .action(ctx -> {
                    final String prompt = join(" ", ctx.arguments());
                    if (prompt.isBlank()) {
                        silent.send("oi solei, the prompt can't be empty aye", ctx.chatId());
                        return;
                    }
                    try {
                        dallEmemeZLimiter.queueRequest(prompt, ctx, sender, silent);
                    } catch (MemesLimitedException e) {
                        silent.send(e.getMessage(), ctx.chatId());
                    }
                })
                .build();
    }
}