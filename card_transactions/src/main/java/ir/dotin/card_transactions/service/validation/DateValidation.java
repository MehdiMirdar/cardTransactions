package ir.dotin.card_transactions.service.validation;

import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * this class is the validator for the date
 *
 * @author Mehdi Mirdar
 * @version 1.0
 * @since 2020-11-02
 */
@Service
public class DateValidation {

    /**
     * <p>this method will check the date is validate or not
     * </p>
     *
     * @param transactionDate is string amount of date
     * @return this method will return boolean value after check the transaction date
     * @since 1.0
     */
    public boolean dateValidator(String transactionDate) {

        String pattern = "yyyy-MM-dd";
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        Date today = Calendar.getInstance().getTime();
        String todayAsString = dateFormat.format(today);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = format.parse(transactionDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        Date date1 = calendar.getTime();
        String todayFromClient = dateFormat.format(date1);

        return todayFromClient.equals(todayAsString);
    }
}
