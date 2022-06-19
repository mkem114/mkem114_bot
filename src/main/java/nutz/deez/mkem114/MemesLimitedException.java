package nutz.deez.mkem114;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class MemesLimitedException extends Exception {
    public MemesLimitedException(@Nonnull final String message) {
        super(message);
        requireNonNull(message);
    }
}
