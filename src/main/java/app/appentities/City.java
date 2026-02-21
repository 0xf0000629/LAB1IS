package app.appentities;

import jakarta.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(
        usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "CityRegion"
)
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    @OneToOne
    @JoinColumn(name = "coordinates_id")
    private Coordinates coordinates; //Поле не может быть null
    private LocalDateTime creation_date; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    private String created_by;
    private Integer area; //Значение поля должно быть больше 0, Поле не может быть null
    private Long population; //Значение поля должно быть больше 0, Поле не может быть null
    private ZonedDateTime establishment_date;
    private Boolean capital;
    private Integer meters_above_sea_level;
    private Long car_code; //Значение поля должно быть больше 0, Максимальное значение поля: 1000
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Climate climate; //Поле может быть null
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private StandardOfLiving standardOfLiving; //Поле не может быть null
    @OneToOne
    private Human governor; //Поле не может быть null

    public City() {
        // this empty constructor is for JPA to not break
    }

    public City(long id, String name, Coordinates coords, LocalDateTime creation_date,
                String created_by, Integer area, Long population, ZonedDateTime establishment_date,
                Boolean capital, Integer meters_above_sea_level, Long car_code, Climate climate,
                StandardOfLiving standardOfLiving, Human governor){
        this.id = id;
        this.name = name;
        this.coordinates = coords;
        this.creation_date = creation_date;
        this.created_by = created_by;
        this.area = area;
        this.population = population;
        this.establishment_date = establishment_date;
        this.capital = capital;
        this.meters_above_sea_level = meters_above_sea_level;
        this.car_code = car_code;
        this.climate = climate;
        this.standardOfLiving = standardOfLiving;
        this.governor = governor;
    }

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

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
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

    public Boolean isCapital() {
        return capital;
    }

    public void setCapital(boolean capital) {
        this.capital = capital;
    }

    public Integer getMeters_above_sea_level() {
        return meters_above_sea_level;
    }

    public void setMeters_above_sea_level(int meters_above_sea_level) {
        this.meters_above_sea_level = meters_above_sea_level;
    }

    public Long getCar_code() {
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
