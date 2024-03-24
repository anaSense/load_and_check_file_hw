import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import model.Book;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.opencsv.CSVReader;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.*;


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
        ZipInputStream stream = null;
        try {
            stream = getFileFromZipWithEnds("csv");
            CSVReader csvReader = new CSVReader(new InputStreamReader(stream));
            List<String[]> content = csvReader.readAll();
            assertThat(content.get(1)).isEqualTo(new String[] {"Judy Hill",	"5(9130)901-78-93",	"br1e2tt3@jastrebofigor.site"});
        } catch (NullPointerException npe) {
            System.out.println("Файл csv отсутствует в архиве");
        } finally {
            stream.close();
        }
    }

    @Test
    void xlsxParsingTest() throws Exception {
        ZipInputStream stream = null;
        try {
            stream = getFileFromZipWithEnds("xlsx");
            XLS xls = new XLS(stream);
            assertThat(xls.excel.getSheet("Лист 1")
                    .getRow(7)
                    .getCell(0)
                    .getStringCellValue()).isEqualTo("Orchid");
        } catch (NullPointerException npe) {
            System.out.println("Файл xlsx отсутствует в архиве");
        } finally {
            stream.close();
        }
    }

    @Test
    void pdfParsingTest() throws Exception {
        ZipInputStream stream = null;
        try {
            stream = getFileFromZipWithEnds("pdf");
            PDF pdf = new PDF(stream);
            assertThat(pdf.text.contains("A lighthouse is a tower")).isTrue();
        } catch (NullPointerException npe) {
            System.out.println("Файл pdf отсутствует в архиве");
        } finally {
            stream.close();
        }
    }

    @Test
    void jsonParsingTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("book.json");
             Reader reader = new InputStreamReader(is)) {
            ObjectMapper mapper = new ObjectMapper();
            Book bookObject = mapper.readValue(reader, Book.class);
            assertThat(bookObject.getTitle()).isEqualTo("A Man Called Ove");
            assertThat(bookObject.getPublicationDate()).isEqualTo("2012-08-27");
            assertThat(bookObject.getAuthor().getFirstname()).isEqualTo("Fredrik");
            assertThat(bookObject.getCategory()).isEqualTo(new String[] {"Fiction", "Realistic Fiction", "Satire", "Humor"});
        }
    }
}