package it.sonotullio.rockymarciano.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.sonotullio.rockymarciano.utils.DateUtils;
import lombok.Data;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.text.ParseException;
import java.util.*;

@Data
@Entity
public class Course {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    String id;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="UTC")
    @Temporal(TemporalType.DATE)
    Date date;

    Date startTime;

    String sport;
    double duration = 1;
    int prenotation = 0;
    int prenotationMax;

    public static Collection<Course> parse(Workbook workbook) throws ParseException {
        Collection<Course> courses = new ArrayList<>();

        Iterator<Sheet> sheetIterator = workbook.sheetIterator();

        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            Date date = DateUtils.parse(sheet.getSheetName(), "yyyy-MM-dd");

            Iterator<Row> rowIterator = sheet.rowIterator();

            // Skip Header
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                String time = Double.toString(row.getCell(0).getNumericCellValue());

                String hours = time.split("\\.")[0];
                String minute = time.split("\\.")[1];

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, 1900 + date.getYear());
                calendar.set(Calendar.MONTH, date.getMonth());
                calendar.set(Calendar.DATE, date.getDate() -1);
                calendar.set(Calendar.HOUR, new Integer(hours));
                calendar.set(Calendar.MINUTE, new Integer(minute));


                String sport = row.getCell(1).getStringCellValue();
                int limit = (int) row.getCell(2).getNumericCellValue();

                Course course = new Course();
                course.setDate(calendar.getTime());
                course.setStartTime(calendar.getTime());
                course.setSport(sport);
                course.setPrenotationMax(limit);

                courses.add(course);

            }
        }

        return courses;
    }

}