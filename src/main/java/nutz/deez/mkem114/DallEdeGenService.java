package nutz.deez.mkem114;


import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.Math.pow;
import static java.lang.String.format;
import static java.time.Duration.ofDays;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.DAYS;

public class DallEdeGenService {
    private static final List<String> pissOffMessages = List.of(
            "nah ceebs aye",
            "too busy throwing phones at Mohan",
            "busy unionising",
            "what am I? an Amazon minion?",
            "y'know GIMP is free",
            "do I look like creative cloud to you?",
            "imposter syndrome too stronk",
            "go ask EsotericDalle",
            "I'm too underpaid to care",
            "too busy asking Sid's boss for raise",
            "I'm too busy, go make your own shitty mspaint jpegs",
            "no",
            "shiieeet, you just got unluggy uce"
    );
    public static final int PORT = 8000;
    public static final String PROMPT_QUERY_PARAM = "prompt";

    private final Random random = new Random();
    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private final ExecutorService underpaidAmazonTeamMember =
            new ThreadPoolExecutor(1, 1, ofDays(7).toMillis(), DAYS, workQueue);

    public void queueRequest(
            @Nonnull final String prompt,
            @Nonnull final MessageContext context,
            @Nonnull final MessageSender sender,
            @Nonnull final SilentSender silent
    ) throws MemesLimitedException {
        requireNonNull(prompt);
        requireNonNull(context);
        requireNonNull(sender);
        if (workQueue.size() >= 5) {
            throw new MemesLimitedException("429 cunt, fuck off!");
        }
        if (workQueue.size() > 0 && random.nextDouble(20) < pow(2, workQueue.size())) {
            throw new MemesLimitedException(randomMessage());
        }
        silent.send("generating something for: " + prompt, context.chatId());
        System.out.println(workQueue.size());
        underpaidAmazonTeamMember.submit(() -> {
            System.out.println(workQueue.size());
            try {
                final URI requestUri = buildRequestUri(prompt);
                final HttpRequest request = HttpRequest.newBuilder(requestUri).GET().build();
                final HttpClient client = HttpClient.newHttpClient();
                final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
                if (response.statusCode() >= 200 && response.statusCode() < 400) {
                    final SendPhoto photo = new SendPhoto();
                    photo.setChatId(String.valueOf(context.chatId()));
                    photo.setAllowSendingWithoutReply(true);
                    photo.setReplyToMessageId(context.update().getMessage().getMessageId());
                    photo.setPhoto(new InputFile(response.body(), "dallede.jpg"));
                    sender.sendPhoto(photo);
                } else {
                    silent.send("error: " + response.statusCode(), context.chatId());
                }
            } catch (URISyntaxException | IOException | TelegramApiException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Nonnull
    private URI buildRequestUri(@Nonnull final String prompt) throws URISyntaxException {
        requireNonNull(prompt);
        final String uriEncodedPromptQueryParamValue = prompt.replaceAll(" ", "+");
        return new URI(format("http://localhost:%d/?%s=%s", PORT, PROMPT_QUERY_PARAM, uriEncodedPromptQueryParamValue));
    }

    @Nonnull
    private String randomMessage() {
        final int randomMessageNumber = random.nextInt(pissOffMessages.size());
        return pissOffMessages.get(randomMessageNumber);
    }
}
