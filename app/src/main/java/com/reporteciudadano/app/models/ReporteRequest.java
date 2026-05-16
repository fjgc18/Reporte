package com.reporteciudadano.app.models;

import com.google.gson.annotations.SerializedName;

public class ReporteRequest {
    @SerializedName("nombre_interesado")
    private String nombreInteresado;
    
    @SerializedName("direccion")
    private String direccion;
    
    @SerializedName("colonia")
    private String colonia;
    
    @SerializedName("celular")
    private String celular;
    
    @SerializedName("correo")
    private String correo;
    
    @SerializedName("tipo")
    private String tipo;
    
    @SerializedName("descripcion")
    private String descripcion;
    
    @SerializedName("imagen")
    private String imagen;

    public ReporteRequest(String nombreInteresado, String direccion, String colonia, String celular, String correo, String tipo, String descripcion, String imagen) {
        this.nombreInteresado = nombreInteresado;
        this.direccion = direccion;
        this.colonia = colonia;
        this.celular = celular;
        this.correo = correo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    // Getters and Setters
    public String getNombreInteresado() { return nombreInteresado; }
    public void setNombreInteresado(String nombreInteresado) { this.nombreInteresado = nombreInteresado; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getColonia() { return colonia; }
    public void setColonia(String colonia) { this.colonia = colonia; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}
