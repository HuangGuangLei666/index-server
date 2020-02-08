package com.pl.indexserver.model;

public class PersonDto {
    private String name;
    private String phone;

    private String car_numbers;

    private String extra;

    public PersonDto() {
    }

    public PersonDto(String phone) {
        this.phone = phone;
    }

    public PersonDto(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getCar_numbers() {
        return car_numbers;
    }

    public void setCar_numbers(String car_numbers) {
        this.car_numbers = car_numbers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersonDto person = (PersonDto) o;

        if (!phone.equals(person.phone)){
            return false;
        }
        return phone.equals(person.phone);

    }

    @Override
    public int hashCode() {
//        int result = phone.hashCode();
//        result = 31 * result + name.hashCode();
        return phone.hashCode();
    }
}
