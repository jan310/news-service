package jan.ondra.newsservice.domain.user.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.ZoneId;
import java.util.Set;

public class TimeZoneValidator implements ConstraintValidator<ValidTimeZone, String> {

    private static final Set<String> validTimeZones = ZoneId.getAvailableZoneIds();

    @Override
    public boolean isValid(String timeZone, ConstraintValidatorContext context) {
        return timeZone != null && validTimeZones.contains(timeZone);
    }

}
