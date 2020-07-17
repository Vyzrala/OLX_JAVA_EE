package main.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Class used for creating DB Car entity (table).<br>
 * All setters and getters are automatically generated by lombok.<br>
 * Both NoArgsConstructor constructor and AllArgsConstructor constructor are also generated automatically.
 *
 * @author Kamil Rutkowski
 *
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DB_Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_car;
    private String title;
    private String brand;
    private int is_auction;
    private float price;
    private float weight;
    @ManyToOne(cascade = {CascadeType.ALL})
    private DB_Profile account;
    private int is_available;
    private int year_of;
    private int power;
    private int passengers;
    private String transmission;
    private String country;

    /**
     * Constructor needed apart from provided by lombok, because of id_car being auto-generated
     *
     * @param title name of an object (protected String)
     * @param brand name of a brand of the object (protected String)
     * @param is_auction do you want to have auction or buy now (protected int)
     * @param price price in PLN(protected float)
     * @param weight weight of the object in Kg(protected float)
     * @param account reference to User that poses this item(protected LSEA)
     * @param available Is it still available. After buying an object it becomes 0. And when it is available it is 1 (protected int)
     * @param year_of year of production of the car(private int)
     * @param power power of the car(private int)
     * @param passengers number of passengers(private int)
     * @param transmission type of transmission(private String)
     * @param country country of production(private String)
     */
    public DB_Car(String title, String brand, int is_auction, float price, float weight, DB_Profile account,
                  int available, int year_of, int power, int passengers, String transmission, String country) {
        this.title = title;
        this.brand = brand;
        this.is_auction = is_auction;
        this.price = price;
        this.weight = weight;
        this.account = account;
        this.is_available = available;
        this.year_of = year_of;
        this.power = power;
        this.passengers = passengers;
        this.transmission = transmission;
        this.country = country;
    }
}
