import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
public class Assembler {
    
    public static void main(String[] args) throws IOException {
        System.out.println(args[0]);
        
        ArrayList<String> contents = new ArrayList<String>();

        List<String> content = Files.readAllLines(Paths.get("Add.asm"));
        for(String line : content) {
            System.out.println(line);
            contents.add(line);    
        }

        Charset utf8 = StandardCharsets.UTF_8;
        Files.write(Paths.get("Add.text"), contents, utf8);
    }    
}

