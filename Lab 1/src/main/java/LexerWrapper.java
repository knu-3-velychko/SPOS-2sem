import j2html.tags.ContainerTag;
import lexer.Lexer;
import lexer.Token;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static j2html.TagCreator.*;

public class LexerWrapper {

    private List<Token> tokens;

    private String fileName;

    public LexerWrapper(String fileName) {
        this.fileName = fileName;
        Lexer lexer = new Lexer();
        AtomicInteger lineIndex = new AtomicInteger(0);
        try {
            Files.lines(Paths.get("src/main/resources/" + fileName), StandardCharsets.UTF_8).forEach(s -> {
                s += "\n";
                try {
                    if (!s.isEmpty()) {
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
        var document = html(
                head(
                        title(fileName),
                        link().withRel("stylesheet").withHref("style.css")
                ),
                body(
                        each(tokens, token ->
                                token.getType() == Token.Type.WHITE_SPACE ?
                                        span("-").withClass("whitespace") : token.getType() == Token.Type.NEW_LINE ? br() :
                                        span(token.getTokenString()).withClass(token.getType().name().toLowerCase()))
                )
        ).render();

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("src/main/resources/" + fileName + ".html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(document);
        printWriter.close();
    }
}
