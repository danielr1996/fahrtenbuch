package de.danielr1996.fahrtenbuch.domain;

import java.util.List;

import one.util.streamex.StreamEx;

public class Messungen {
    public static double length(List<Messung> messungen) {
        double strecke = StreamEx.of(messungen.stream())
                .map(Messung::getPunkt)
                .pairMap(Punkt::distance)
                .reduce((d1, d2) -> d1 + d2)
                .orElse(0.0);
        return strecke;
    }
}
