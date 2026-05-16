package com.reporteciudadano.app.network;

import com.reporteciudadano.app.models.ReporteRequest;
import com.reporteciudadano.app.models.ReporteResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("reporte.php")
    Call<ReporteResponse> enviarReporte(
        @Header("Authorization") String token,
        @Body ReporteRequest reporteRequest
    );
}
