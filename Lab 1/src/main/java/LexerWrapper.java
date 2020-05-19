import lexer.Lexer;
import lexer.Token;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class LexerWrapper {

    private List<Token> tokens;

    private String fileName;

    public LexerWrapper(String fileName) {
        this.fileName = fileName;
        Lexer lexer = new Lexer(fileName);
        lexer.tokenize();
        tokens = lexer.getTokens();
    }

    public void printTokens() {
        int i = 0;
        for (Token token : tokens) {
            System.out.println(++i + "   " + token.toString() + "\n");
        }
    }

    public void toHtml() {
        String head = "<html>\n" +
                "    <head>\n" +
                "        <link rel=\"stylesheet\" href=\"style.css\">\n" +
                "        <title>%s</title>\n" +
                "    </head>\n" +
                "<body>";

        String bum = "</body>\n" +
                "</html>";

        StringBuilder document = new StringBuilder();
        document.append(String.format(head, fileName));

        for (var token : tokens) {
            document.append(styleText(token));
        }
        document.append(bum);

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

    private String styleText(Token token) {
        return "<span class=\"" + token.getType().toString().toLowerCase() + "\">"
                + token.getTokenString() + "</span>";
    }
}
