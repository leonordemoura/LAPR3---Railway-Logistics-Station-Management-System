/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package template.model;

/**
 * @author nunocastro
 */
public class Address {
    private Integer id;
    private String street;
    private String postalcode;
    private String city;

    public Integer getId() {
        return id;
    }

    //TODO: o ID não devia ser autogerado pela base de dados?
    public void setId(Integer id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
