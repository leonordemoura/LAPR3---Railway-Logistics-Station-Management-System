/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package template.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nunocastro
 */
public class Client {
    private final List<Address> addressList;
    private Integer id;
    private String name;
    private String vatnr;

    public Client() {
        this.addressList = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    //TODO: o ID não devia ser autogerado pela base de dados?
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVatnr() {
        return vatnr;
    }

    public void setVatnr(String vatnr) {
        this.vatnr = vatnr;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void addAddress(Address address) {
        if (!this.addressList.contains(address)) {
            this.addressList.add(address);
        }
    }
}
