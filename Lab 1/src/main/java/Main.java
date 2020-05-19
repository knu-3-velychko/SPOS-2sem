import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        LexerWrapper lexer = new LexerWrapper("kotlin.txt");
        lexer.printTokens();
        lexer.toHtml();

//        String str = "as\tbreak\tclass\tcontinue\tdo\telse\n" +
//                "false\tfor\tfun\tif\tin\tinterface\n" +
//                "is\tnull\tobject\tpackage\treturn\tsuper\n" +
//                "this\tthrow\ttrue\ttry\ttypealias\ttypeof\n" +
//                "val\tvar\twhen\twhile\tBoolean\tFloat\tChar\tString\nInt\nLong\nDouble\nShort";
//        String[] keywords = str.split("\t|\n");
//        List<String> list = Arrays.asList(keywords);
//        Collections.sort(list);
//        list.forEach(t -> System.out.print('"' + t + "\", "));
    }
}
