package kz.insar.checkbinance.htmlreader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HtmlFileReader {
    public static String readFile(String fileName) throws IOException {
        Path path = Path.of("D:\\code\\Программирование\\checkbinance\\src\\main\\java\\kz\\insar\\checkbinance\\htmlresources\\" + fileName);
        return Files.readString(path);
    }
}
