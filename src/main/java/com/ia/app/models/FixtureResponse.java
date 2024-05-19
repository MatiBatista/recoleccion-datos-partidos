package com.ia.app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixtureResponse {

    private String get;
    private List<FixtureItem> response;
}
