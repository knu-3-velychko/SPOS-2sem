import lexer.Lexer;
import lexer.Token;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        AtomicInteger lineIndex = new AtomicInteger(0);
        try {
            Files.lines(Paths.get("src/main/resources/kotlin.txt"), StandardCharsets.UTF_8).forEach(s -> {
                try {
                    if(!s.isEmpty()) {
                        lexer.tokenize(s, lineIndex.incrementAndGet());
                    } else {
                        lineIndex.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            int i = 0;
            for (Token token : lexer.getTokens()) {
                if (token.getType().isSugar())
                    System.out.println("sugar : " + token.toString() + "\n");
                else {
                    System.out.println(++i + "   " + token.toString() + "\n");
                }
            }
        }
    }
}
