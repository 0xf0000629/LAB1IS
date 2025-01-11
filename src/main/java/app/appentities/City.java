package app.appentities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    @OneToOne
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDateTime creation_date; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer area; //Значение поля должно быть больше 0, Поле не может быть null
    private Long population; //Значение поля должно быть больше 0, Поле не может быть null
    private java.time.ZonedDateTime establishment_date;
    private boolean capital;
    private int meters_above_sea_level;
    private long car_code; //Значение поля должно быть больше 0, Максимальное значение поля: 1000
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Climate climate; //Поле может быть null
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private StandardOfLiving standardOfLiving; //Поле не может быть null
    @OneToOne
    private Human governor; //Поле не может быть null

    public long getId() {return id;}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDateTime getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(LocalDateTime creation_date) {
        this.creation_date = creation_date;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }

    public ZonedDateTime getEstablishment_date() {
        return establishment_date;
    }

    public void setEstablishment_date(ZonedDateTime establishment_date) {
        this.establishment_date = establishment_date;
    }

    public boolean isCapital() {
        return capital;
    }

    public void setCapital(boolean capital) {
        this.capital = capital;
    }

    public int getMeters_above_sea_level() {
        return meters_above_sea_level;
    }

    public void setMeters_above_sea_level(int meters_above_sea_level) {
        this.meters_above_sea_level = meters_above_sea_level;
    }

    public long getCar_code() {
        return car_code;
    }

    public void setCar_code(long car_code) {
        this.car_code = car_code;
    }

    public Climate getClimate() {
        return climate;
    }

    public void setClimate(Climate climate) {
        this.climate = climate;
    }

    public StandardOfLiving getStandardOfLiving() {
        return standardOfLiving;
    }

    public void setStandardOfLiving(StandardOfLiving standardOfLiving) {
        this.standardOfLiving = standardOfLiving;
    }

    public Human getGovernor() {
        return governor;
    }

    public void setGovernor(Human governor) {
        this.governor = governor;
    }
}
