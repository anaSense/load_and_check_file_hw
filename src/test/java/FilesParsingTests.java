import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.opencsv.CSVReader;
import com.fasterxml.jackson.databind.ObjectMapper;


public class FilesParsingTests {

    private final ClassLoader cl = FilesParsingTests.class.getClassLoader();
    private ZipInputStream getFileFromZipWithEnds(String ends) throws Exception {
        InputStream is = cl.getResourceAsStream("archiveWithFiles.zip");
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if(entry.getName().endsWith(ends)) {
                return zis;
            }
        }
        return null;
    }

    @Test
    void csvParsingTest() throws Exception {
        try (ZipInputStream stream = getFileFromZipWithEnds("csv");
             CSVReader csvReader = new CSVReader(new InputStreamReader(stream))) {
            List<String[]> content = csvReader.readAll();
            Assertions.assertArrayEquals(
                    new String[] {"Judy Hill",	"5(9130)901-78-93",	"br1e2tt3@jastrebofigor.site"},
                    content.get(1)
            );
        }
    }

    @Test
    void xlsxParsingTest() throws Exception {
        try (ZipInputStream stream = getFileFromZipWithEnds("xlsx")) {
            XLS xls = new XLS(stream);
            Assertions.assertEquals(
                    "Orchid",
                    xls.excel.getSheet("Лист 1")
                            .getRow(7)
                            .getCell(0)
                            .getStringCellValue()
            );
        }
    }

    @Test
    void pdfParsingTest() throws Exception {
        try (ZipInputStream stream = getFileFromZipWithEnds("pdf")) {
            PDF pdf = new PDF(stream);
            Assertions.assertTrue(pdf.text.contains("A lighthouse is a tower"));
        }
    }

    @Test
    void jsonParsingTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("book.json");
             Reader reader = new InputStreamReader(is)) {
            ObjectMapper mapper = new ObjectMapper();
            Book bookObject = mapper.readValue(reader, Book.class);
            Assertions.assertEquals("A Man Called Ove", bookObject.getTitle());
            Assertions.assertEquals(LocalDate.parse("2012-08-27"), bookObject.getPublicationDate());
            Assertions.assertEquals("Fredrik", bookObject.getAuthor().getFirstname());
            Assertions.assertArrayEquals(new String[] {"Fiction", "Realistic Fiction", "Satire", "Humor"}, bookObject.getCategory());
        }
    }
}