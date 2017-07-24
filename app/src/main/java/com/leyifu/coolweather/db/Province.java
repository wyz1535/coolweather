package com.leyifu.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by xingxing on 2017/7/24.
 */
public class Province extends DataSupport{

    private int id;
    private String provinceNema;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceNema() {
        return provinceNema;
    }

    public void setProvinceNema(String provinceNema) {
        this.provinceNema = provinceNema;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
