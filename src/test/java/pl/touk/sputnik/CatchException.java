package pl.touk.sputnik;

import org.junit.Assert;

import java.util.function.Consumer;

public class CatchException {

    public static void catchException(Runnable block, Consumer<Exception> consumer) {
        try {
            block.run();
            Assert.fail();
        } catch (Exception ex) {
            consumer.accept(ex);
        }
    }

}
