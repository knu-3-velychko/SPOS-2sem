import lexer.Lexer;
import lexer.Token;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LexerWrapper {

    private List<Token> tokens;

    public LexerWrapper(String filename) {
        Lexer lexer = new Lexer();
        AtomicInteger lineIndex = new AtomicInteger(0);
        try {
            Files.lines(Paths.get("src/main/resources/" + filename), StandardCharsets.UTF_8).forEach(s -> {
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
            tokens = lexer.getTokens();
        }
    }

    public void printTokens() {
        int i = 0;
        for (Token token : tokens) {
            if (token.getType().isSugar())
                System.out.println("sugar : " + token.toString() + "\n");
            else {
                System.out.println(++i + "   " + token.toString() + "\n");
            }
        }
    }

    public void toHtml() {

    }
}
