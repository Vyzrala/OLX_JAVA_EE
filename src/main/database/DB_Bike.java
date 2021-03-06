package main.database;

import lombok.*;

import javax.persistence.*;

/**
 * Class used for creating DB Bike entity (table).<br>
 * All setters and getters are automatically generated by lombok.<br>
 * Both NoArgsConstructor constructor and AllArgsConstructor constructor are also generated automatically.
 *
 * @author Marcin Hebdzynski
 *
 */
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DB_Bike {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id_bike;
    private String title;
    private String brand;
    private int is_auction;
    private float price;
    private float weight;
    @ManyToOne(cascade = {CascadeType.ALL})
    private DB_Profile account;
    private int is_available;
    private int gear;
    @ManyToOne(cascade = {CascadeType.ALL})
    private DB_Frame frame;
    private int are_lights;
    private int is_bell;
    private int are_breaks;

    /**
     * Constructor needed apart from provided by lombok, because of id_bike being auto-generated
     *
     * @param title name of an object (protected String)
     * @param brand name of a brand of the object (protected String)
     * @param is_auction do you want to have auction or buy now (protected int)
     * @param price price in PLN(protected float)
     * @param weight weight of the object in Kg(protected float)
     * @param account reference to User that poses this item(protected LSEA)
     * @param is_available  is it still available. After buying an object it becomes 0. And when it is available it is 1 (protected int)
     * @param gear number of gears (private int)
     * @param frame type of frame(private Frame(enum type:carbon,steel,aluminum,magnesium,titanium))
     * @param are_lights does it have lights(1-has,0-does not) (private int)
     * @param is_bell does it have bell(1-has,0-does not) (private int)
     * @param are_breaks does it have working breaks(1-has,0-does not)(private int)
     */
    public DB_Bike(String title, String brand, int is_auction, float price, float weight, DB_Profile account, int is_available,
                   int gear, DB_Frame frame, int are_lights, int is_bell, int are_breaks){
        this.title = title;
        this.brand = brand;
        this.is_auction = is_auction;
        this.price = price;
        this.weight = weight;
        this.account = account;
        this.is_available = is_available;
        this.gear = gear;
        this.frame = frame;
        this.are_lights = are_lights;
        this.is_bell = is_bell;
        this.are_breaks = are_breaks;
    }
}
