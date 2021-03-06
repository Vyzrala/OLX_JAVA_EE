package main.lsea;

import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.nio.channels.*;

/**
 * This is abstract class. Two classes are inheriting from it (Bike and Car).<br>
 * In the future there might be more<br>
 * Implements Cloneable, Serializable<br>
 * All setters and getters are automatically generated by lombok.<br>
 * Both NoArgsConstructor constructor and AllArgsConstructor constructor are also generated automatically.<br>
 *
 * @author Patryk Dunajewski
 *
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
abstract class Item implements Cloneable, Serializable{

    /*** serialVersionUID -> correct serialization*/
    private static final long serialVersionUID = -8396368963241401323L;
    /*** nameOI -> name of an object (protected String)*/
    @NotNull(message = "\tName cannot be null")
    protected String nameOI;
    /*** brand -> name of a brand of the object (protected String)*/
    @NotNull(message = "\tBrand cannot be null")
    protected String brand;
    /*** auction -> do you want to have auction or buy now (protected int)*/
    @Range(min = 0, max = 1, message = "\tIs_auction must be 0 or 1 (you either want to have auction or not)")
    protected int is_auction;
    /*** price -> price in PLN(protected float)*/
    @NotNull(message = "\tPrice cannot be null")
    @Min(value = 0, message = "\tPrice cannot be negative")
    protected float price;
    /*** weight -> weight of the object in Kg(protected float)*/
    @NotNull(message = "\tWeight cannot be null")
    @Min(value = 0, message = "\tWeight cannot be negative")
    protected float weight;
    /*** account -> Reference to User that poses this item(protected LSEA)*/
    @NotNull(message = "\tError")
    protected LSEA account;
    /*** available -> Is it still available. After buying an object it becomes 0. And when it is available it is 1 (protected int)*/
    @Range(min = 0, max = 1, message = "\tIs_available must be 0 or 1 (item is either available or not)")
    protected int is_available;

    /*** All of the information given for object. This method is overridden in both subclasses*/
    public abstract void getInfo();

    /*** Both classes (Car and Bike) have save method that they inheritate from this class but the one they use is overloading */
    public abstract void save();

    /**
     * Method that is saving serialized data into .dat file.
     * @param bikes list with all of the bike offers
     * @param cars list with all of the car offers
     * @param uPath Path of the file(String)
     */
    public void saveS(List<Bike> bikes, List<Car> cars, String uPath) {
        File file = new File(uPath + ".dat");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            FileLock fl = fos.getChannel().tryLock();
            ObjectOutputStream os = new ObjectOutputStream(fos);
            if(fl != null) {
                System.out.println("File is locked");
                os.writeInt(bikes.size());
                for(int i =0; i<bikes.size(); i++) {
                    os.writeObject(bikes.get(i));
                }
                os.writeInt(cars.size());
                for(int i =0; i<cars.size(); i++) {
                    os.writeObject(cars.get(i));
                }
                TimeUnit.SECONDS.sleep(5);
                fl.release();
                System.out.print("File is unlocked");
            }
            os.close();
        } catch (IOException | InterruptedException ex) {
            System.out.print(ex.getMessage());
        }
    }

    /**
     * Method that is loading serialized from .dat file and deserialize them.
     * @param bikes list with all of the bike offers
     * @param cars list with all of the car offers
     * @param uPath Path of the file(String)
     */
    public void loadS(List<Bike> bikes, List<Car> cars, String uPath) {
        File file = new File(uPath + ".dat");
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream is = new ObjectInputStream(fis);
            int wait = is.readInt();
            for(int i=0; i<wait; i++)
            {
                bikes.add((Bike)is.readObject());
            }
            wait = is.readInt();
            for(int i=0; i<wait; i++)
            {
                cars.add((Car)is.readObject());
            }
            is.close();
        } catch (IOException | ClassNotFoundException ex){
            System.out.print(ex.getMessage());
        }
    }

    /*** deep cloning
     * @return item, which is clone of item class object with LSEA addition
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Item item = (Item) super.clone();
        item.account = (LSEA) account.clone();
        return item;
    }

    /*** overriding toString method to print values from the class*/
    @Override
    public String toString() {
        return "Item [nameOI=" + nameOI + ", brand=" + brand + ", is_auction=" + is_auction + ", price=" + price + ", weight="
                + weight + ", account=" + account.getName() + ", is_available=" + is_available + "]";
    }
}
