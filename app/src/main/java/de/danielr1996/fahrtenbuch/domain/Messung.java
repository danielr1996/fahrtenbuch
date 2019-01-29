package de.danielr1996.fahrtenbuch.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Messung{
    private LocalDateTime timestamp;
    private Punkt punkt;

    public double speed(Messung other){
        LocalDateTime start = this.timestamp;
        LocalDateTime end = other.timestamp;
        long timeInSeconds = ChronoUnit.SECONDS.between(start, end);
        double distanceInKm = punkt.distance(other.getPunkt());
        double speedInKmS = distanceInKm / timeInSeconds;
        double speedInKmH = speedInKmS * 3600;
        return Math.abs(speedInKmH);
    }


}
