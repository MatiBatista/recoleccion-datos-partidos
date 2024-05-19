package com.ia.app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixtureItem {

    private Fixture fixture;
    private League league;
    private Team teams;
    private Goal goals;
    private Score score;
}
