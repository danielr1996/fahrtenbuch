package de.danielr1996.fahrtenbuch.domain;


import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class MessungTest {

    @Test
    public void speed() {
        Messung start = Messung.builder()
                .punkt(Punkt.builder()
                        .longitude(11.259956359863281)
                        .latitude(49.50113403316642)
                        .build())
                .timestamp(LocalDateTime.of(LocalDate.now(), LocalTime.of(12,0)))
                .build();
        Messung end = Messung.builder()
                .punkt(Punkt.builder()
                        .longitude(11.220474243164062)
                        .latitude(49.4875312703135)
                        .build())
                .timestamp(LocalDateTime.of(LocalDate.now(), LocalTime.of(12,30)))
                .build();
        double expected = 6.46;
        double actual = start.speed(end);

        assertEquals(expected, actual, 0.01);
    }
}