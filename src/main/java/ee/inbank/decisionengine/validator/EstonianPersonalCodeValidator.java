package ee.inbank.decisionengine.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

import static java.util.Arrays.stream;

public class EstonianPersonalCodeValidator implements ConstraintValidator<EstonianPersonalCode, String> {

    private static final String PERSONAL_CODE_REGEX = "[1-6][0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])[0-9]{4}";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd")
            .withResolverStyle(ResolverStyle.STRICT);

    @Override
    public boolean isValid(String personalCode, ConstraintValidatorContext context) {
        if (!isValidFormat(personalCode)) {
            return false;
        }

        try {
            getDateOfBirth(personalCode);
        } catch (DateTimeParseException e) {
            return false;
        }

        int controlNumber = Character.getNumericValue(personalCode.charAt(personalCode.length() - 1));
        return controlNumber == calculateControlNumber(personalCode);
    }

    private LocalDate getDateOfBirth(String personalCode) {
        String dateWithoutYear = personalCode.substring(1, 7);

        String date = switch (getGenderIdentifier(personalCode)) {
            case 1, 2 -> "18" + dateWithoutYear;
            case 3, 4 -> "19" + dateWithoutYear;
            default -> "20" + dateWithoutYear;
        };

        return LocalDate.parse(date, DATE_FORMATTER);
    }

    private int getGenderIdentifier(String personalCode) {
        return Character.getNumericValue(personalCode.charAt(0));
    }

    private boolean isValidFormat(String personalCode) {
        return personalCode != null
                && !personalCode.isBlank()
                && personalCode.matches(PERSONAL_CODE_REGEX);
    }

    private static int calculateControlNumber(String personalCode) {
        String[] numberArray = personalCode.substring(0, 10).split("");
        List<Integer> numberList = stream(numberArray)
                .map(Integer::valueOf)
                .toList();
        int sum = 0;
        int[] multipliers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 1};

        for (int i = 0; i < numberList.size(); i++) {
            sum += numberList.get(i) * multipliers[i];
        }

        int parsedControlNumber = sum % 11;

        if (parsedControlNumber == 10) {
            sum = 0;
            multipliers = new int[]{3, 4, 5, 6, 7, 8, 9, 1, 2, 3};

            for (int i = 0; i < numberList.size(); i++) {
                sum += numberList.get(i) * multipliers[i];
            }

            parsedControlNumber = sum % 11;

            if (parsedControlNumber == 10) {
                return 0;
            }
        }

        return parsedControlNumber;
    }
}
