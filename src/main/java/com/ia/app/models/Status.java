package com.ia.app.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    @SerializedName("short")
    private String shortt; // FT= Finalizado
    //PEN= Penaltys
    //CANC= Cancelado
}
