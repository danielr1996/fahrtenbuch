package de.danielr1996.fahrtenbuch.domain;


import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class PunktTest {

    @Test
    public void distance() {
        Punkt start = Punkt.builder()
                .longitude(11.259956359863281)
                .latitude(49.50113403316642)
                .build();
        Punkt end = Punkt.builder()
                .longitude(11.220474243164062)
                .latitude(49.4875312703135)
                .build();
        double expected = 3.23;
        double actual = start.distance(end);

        assertEquals(expected, actual,0.01);
    }
}